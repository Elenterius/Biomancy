package com.github.elenterius.biomancy.block.biolab;

import com.github.elenterius.biomancy.block.base.MachineBlock;
import com.github.elenterius.biomancy.block.base.MachineBlockEntity;
import com.github.elenterius.biomancy.client.util.ClientLoopingSoundHelper;
import com.github.elenterius.biomancy.crafting.recipe.BioLabRecipe;
import com.github.elenterius.biomancy.crafting.recipe.IngredientStack;
import com.github.elenterius.biomancy.crafting.recipe.SimpleRecipeType;
import com.github.elenterius.biomancy.init.ModBlockEntities;
import com.github.elenterius.biomancy.init.ModCapabilities;
import com.github.elenterius.biomancy.init.ModRecipes;
import com.github.elenterius.biomancy.init.ModSoundEvents;
import com.github.elenterius.biomancy.inventory.InventoryHandler;
import com.github.elenterius.biomancy.inventory.InventoryHandlers;
import com.github.elenterius.biomancy.inventory.ItemHandlerUtil;
import com.github.elenterius.biomancy.menu.BioLabMenu;
import com.github.elenterius.biomancy.styles.TextComponentUtil;
import com.github.elenterius.biomancy.util.ILoopingSoundHelper;
import com.github.elenterius.biomancy.util.ItemStackFilter;
import com.github.elenterius.biomancy.util.ItemStackFilterList;
import com.github.elenterius.biomancy.util.SoundUtil;
import com.github.elenterius.biomancy.util.fuel.FluidFuelConsumerHandler;
import com.github.elenterius.biomancy.util.fuel.FuelHandler;
import com.github.elenterius.biomancy.util.fuel.IFuelHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.CombinedInvWrapper;
import net.minecraftforge.items.wrapper.RangedWrapper;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.List;

public class BioLabBlockEntity extends MachineBlockEntity<BioLabRecipe, BioLabStateData> implements MenuProvider, GeoBlockEntity {

	public static final int FUEL_SLOTS = 1;
	public static final int INPUT_SLOTS = BioLabRecipe.MAX_INGREDIENTS + BioLabRecipe.MAX_REACTANT;
	public static final int OUTPUT_SLOTS = 1;

	public static final int MAX_FUEL = 1_000;

	public static final RegistryObject<SimpleRecipeType.ItemStackRecipeType<BioLabRecipe>> RECIPE_TYPE = ModRecipes.BIO_BREWING_RECIPE_TYPE;

	protected static final RawAnimation WORKING_ANIM = RawAnimation.begin().thenLoop("bio_lab.working");
	protected static final RawAnimation IDLE_ANIM = RawAnimation.begin().thenLoop("bio_lab.idle");

	private final BioLabStateData stateData;
	private final FuelHandler fuelHandler;
	private final InventoryHandler fuelInventory;

	private final InventoryHandler inputInventory;
	private final ItemStackFilterList inputSlotsFilter;

	private final InventoryHandler outputInventory;

	private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
	private ILoopingSoundHelper loopingSoundHelper = ILoopingSoundHelper.NULL;

	private LazyOptional<IItemHandler> optionalCombinedInventory;
	private LazyOptional<IFluidHandler> optionalFluidConsumer;

	public BioLabBlockEntity(BlockPos worldPosition, BlockState blockState) {
		super(ModBlockEntities.BIO_LAB.get(), worldPosition, blockState);

		inputSlotsFilter = ItemStackFilterList.of(ItemStackFilter.ALLOW_ALL, INPUT_SLOTS);
		inputInventory = InventoryHandlers.filterInput(INPUT_SLOTS, inputSlotsFilter, this::onInventoryChanged);

		outputInventory = InventoryHandlers.denyInput(OUTPUT_SLOTS, this::onInventoryChanged);

		fuelInventory = InventoryHandlers.filterFuel(FUEL_SLOTS, this::onInventoryChanged);

		optionalCombinedInventory = createCombinedInventory();

		fuelHandler = FuelHandler.createNutrientFuelHandler(MAX_FUEL, this::setChanged);
		stateData = new BioLabStateData(fuelHandler);
		optionalFluidConsumer = LazyOptional.of(() -> new FluidFuelConsumerHandler(fuelHandler));
	}

	private LazyOptional<IItemHandler> createCombinedInventory() {
		return LazyOptional.of(() -> new CombinedInvWrapper(
				fuelInventory,
				new RangedWrapper(inputInventory, inputInventory.getSlots() - 1, inputInventory.getSlots())
		));
	}

	@Override
	public void onLoad() {
		if (level != null && level.isClientSide) {
			loopingSoundHelper = new ClientLoopingSoundHelper();
		}
	}

	@Override
	public Component getDisplayName() {
		return getName();
	}

	@Override
	public Component getName() {
		return TextComponentUtil.getTranslationText("container", "bio_lab");
	}

	@Nullable
	@Override
	public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
		return BioLabMenu.createServerMenu(containerId, playerInventory, this);
	}

	@Override
	public BioLabStateData getStateData() {
		return stateData;
	}

	@Override
	public InventoryHandler getInputInventory() {
		return inputInventory;
	}

	public InventoryHandler getFuelInventory() {
		return fuelInventory;
	}

	public InventoryHandler getOutputInventory() {
		return outputInventory;
	}

	@Override
	protected IFuelHandler getFuelHandler() {
		return fuelHandler;
	}

	@Override
	public ItemStack getStackInFuelSlot() {
		return fuelInventory.getStackInSlot(0);
	}

	@Override
	public void setStackInFuelSlot(ItemStack stack) {
		fuelInventory.setStackInSlot(0, stack);
	}

	@Override
	protected boolean doesRecipeResultFitIntoOutputInv(BioLabRecipe craftingGoal, ItemStack stackToCraft) {
		return ItemHandlerUtil.doesItemFit(outputInventory.getRaw(), 0, stackToCraft);
	}

	@Nullable
	@Override
	protected BioLabRecipe resolveRecipeFromInput(Level level) {
		return RECIPE_TYPE.get().getRecipeFromContainer(level, inputInventory.getRecipeWrapper()).orElse(null);
	}

	@Override
	protected boolean doesRecipeMatchInput(BioLabRecipe recipeToTest, Level level) {
		return recipeToTest.matches(inputInventory.getRecipeWrapper(), level);
	}

	@Override
	protected void saveAdditional(CompoundTag tag) {
		super.saveAdditional(tag);
		stateData.serialize(tag);
		tag.put("Fuel", fuelHandler.serializeNBT());
		tag.put("FuelSlots", fuelInventory.serializeNBT());
		tag.put("InputSlots", inputInventory.serializeNBT());
		tag.put("InputSlotsFilter", inputSlotsFilter.serializeNBT());
		tag.put("OutputSlots", outputInventory.serializeNBT());
	}

	@Override
	public void load(CompoundTag tag) {
		super.load(tag);
		stateData.deserialize(tag);
		fuelHandler.deserializeNBT(tag.getCompound("Fuel"));
		fuelInventory.deserializeNBT(tag.getCompound("FuelSlots"));
		inputInventory.deserializeNBT(tag.getCompound("InputSlots"));
		inputSlotsFilter.deserializeNBT(tag.getCompound("InputSlotsFilter"));
		outputInventory.deserializeNBT(tag.getCompound("OutputSlots"));
	}

	@Override
	public void dropAllInvContents(Level level, BlockPos pos) {
		ItemHandlerUtil.dropContents(level, pos, fuelInventory);
		ItemHandlerUtil.dropContents(level, pos, inputInventory);
		ItemHandlerUtil.dropContents(level, pos, outputInventory);
	}

	@Override
	public <T> @NotNull LazyOptional<T> getCapability(Capability<T> cap, @Nullable Direction side) {
		if (remove) return super.getCapability(cap, side);

		if (cap == ModCapabilities.ITEM_HANDLER) {
			if (side == null || side == Direction.DOWN) return outputInventory.getLazyOptional().cast();
			if (side == Direction.UP) return inputInventory.getLazyOptional().cast();
			return optionalCombinedInventory.cast();
		}

		if (cap == ModCapabilities.FLUID_HANDLER) {
			return optionalFluidConsumer.cast();
		}

		return super.getCapability(cap, side);
	}

	@Override
	public void invalidateCaps() {
		super.invalidateCaps();
		fuelInventory.invalidate();
		inputInventory.invalidate();
		outputInventory.invalidate();
		optionalCombinedInventory.invalidate();
		optionalFluidConsumer.invalidate();
	}

	@Override
	public void reviveCaps() {
		super.reviveCaps();
		fuelInventory.revive();
		inputInventory.revive();
		outputInventory.revive();
		optionalCombinedInventory = createCombinedInventory();
		optionalFluidConsumer = LazyOptional.of(() -> new FluidFuelConsumerHandler(fuelHandler));
	}

	@Override
	protected boolean craftRecipe(BioLabRecipe recipeToCraft, Level level) {
		ItemStack result = recipeToCraft.getResultItem(level.registryAccess()).copy();
		if (result.isEmpty() || !doesRecipeResultFitIntoOutputInv(recipeToCraft, result)) {
			return false;
		}

		//get ingredients cost
		List<IngredientStack> ingredients = recipeToCraft.getIngredientQuantities();
		int[] ingredientCost = new int[ingredients.size()];
		for (int i = 0; i < ingredients.size(); i++) {
			ingredientCost[i] = ingredients.get(i).count();
		}

		//consume reactant
		final int lastIndex = inputInventory.getSlots() - 1;
		inputInventory.extractItem(lastIndex, 1, false);

		//consume ingredients
		for (int idx = 0; idx < lastIndex; idx++) {
			final ItemStack foundStack = inputInventory.getStackInSlot(idx); //do not modify this stack
			if (!foundStack.isEmpty()) {
				for (int i = 0; i < ingredients.size(); i++) {
					int remainingCost = ingredientCost[i];
					if (remainingCost > 0 && ingredients.get(i).testItem(foundStack)) {
						int amount = Math.min(remainingCost, foundStack.getCount());
						inputInventory.extractItem(idx, amount, false);
						ingredientCost[i] -= amount;
						break;
					}
				}
			}
		}

		//output result
		outputInventory.getRaw().insertItem(0, result, false);

		SoundUtil.broadcastBlockSound((ServerLevel) level, getBlockPos(), ModSoundEvents.BIO_LAB_CRAFTING_COMPLETED);

		setChanged();
		return true;
	}

	private <T extends BioLabBlockEntity> PlayState handleAnimationState(AnimationState<T> event) {
		boolean isCrafting = Boolean.TRUE.equals(getBlockState().getValue(MachineBlock.CRAFTING));

		if (isCrafting) {
			event.getController().setAnimation(WORKING_ANIM);
			loopingSoundHelper.startLoop(this, ModSoundEvents.BIO_LAB_CRAFTING.get(), 0.65f);
		}
		else {
			event.getController().setAnimation(IDLE_ANIM);
			loopingSoundHelper.stopLoop();
		}

		return PlayState.CONTINUE;
	}

	@Override
	public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
		controllers.add(new AnimationController<>(this, "main", 0, this::handleAnimationState));
	}

	@Override
	public AnimatableInstanceCache getAnimatableInstanceCache() {
		return cache;
	}

	@Override
	public void setRemoved() {
		if (level != null && level.isClientSide) {
			loopingSoundHelper.clear();
		}
		super.setRemoved();
	}

}
