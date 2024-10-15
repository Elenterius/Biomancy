package com.github.elenterius.biomancy.block.digester;

import com.github.elenterius.biomancy.api.nutrients.FuelHandler;
import com.github.elenterius.biomancy.api.nutrients.FuelHandlerImpl;
import com.github.elenterius.biomancy.block.base.MachineBlock;
import com.github.elenterius.biomancy.block.base.MachineBlockEntity;
import com.github.elenterius.biomancy.client.util.ClientLoopingSoundHelper;
import com.github.elenterius.biomancy.crafting.recipe.DigestingRecipe;
import com.github.elenterius.biomancy.crafting.recipe.SimpleRecipeType;
import com.github.elenterius.biomancy.init.ModBlockEntities;
import com.github.elenterius.biomancy.init.ModCapabilities;
import com.github.elenterius.biomancy.init.ModRecipes;
import com.github.elenterius.biomancy.init.ModSoundEvents;
import com.github.elenterius.biomancy.inventory.InventoryHandler;
import com.github.elenterius.biomancy.inventory.InventoryHandlers;
import com.github.elenterius.biomancy.inventory.ItemHandlerUtil;
import com.github.elenterius.biomancy.menu.DigesterMenu;
import com.github.elenterius.biomancy.styles.TextComponentUtil;
import com.github.elenterius.biomancy.util.ILoopingSoundHelper;
import com.github.elenterius.biomancy.util.SoundUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.BowlFoodItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.registries.ForgeRegistries;
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

import java.util.Objects;

public class DigesterBlockEntity extends MachineBlockEntity<DigestingRecipe, DigesterStateData> implements MenuProvider, GeoBlockEntity {

	public static final int FUEL_SLOTS = 1;
	public static final int INPUT_SLOTS = 1;
	public static final int OUTPUT_SLOTS = 2;

	public static final int MAX_FUEL = 1_000;

	public static final RegistryObject<SimpleRecipeType.ItemStackRecipeType<DigestingRecipe>> RECIPE_TYPE = ModRecipes.DIGESTING_RECIPE_TYPE;
	protected static final RawAnimation WORKING_ANIM = RawAnimation.begin().thenLoop("digester.working");
	protected static final RawAnimation IDLE_ANIM = RawAnimation.begin().thenLoop("digester.idle");

	private final DigesterStateData stateData;
	private final FuelHandlerImpl fuelHandler;
	private final InventoryHandler<?> fuelInventory;
	private final InventoryHandler<?> inputInventory;
	private final InventoryHandler<?> outputInventory;

	private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
	private ILoopingSoundHelper loopingSoundHelper = ILoopingSoundHelper.NULL;

	private LazyOptional<IFluidHandler> optionalFluidConsumer;

	public DigesterBlockEntity(BlockPos pos, BlockState state) {
		super(ModBlockEntities.DIGESTER.get(), pos, state);

		inputInventory = InventoryHandlers.standard(INPUT_SLOTS, this::onInventoryChanged);
		outputInventory = InventoryHandlers.denyInput(OUTPUT_SLOTS, this::onInventoryChanged);

		fuelInventory = InventoryHandlers.filterFuel(FUEL_SLOTS, this::onInventoryChanged);
		fuelHandler = FuelHandlerImpl.createNutrientFuelHandler(MAX_FUEL, this::onInventoryChanged);

		stateData = new DigesterStateData(fuelHandler);
		optionalFluidConsumer = LazyOptional.of(fuelHandler::getFluidConsumer);
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
		return TextComponentUtil.getTranslationText("container", Objects.requireNonNull(ForgeRegistries.BLOCK_ENTITY_TYPES.getKey(getType())).getPath());
	}

	@Nullable
	@Override
	public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
		return DigesterMenu.createServerMenu(containerId, playerInventory, this);
	}

	@Override
	public DigesterStateData getStateData() {
		return stateData;
	}

	@Override
	public InventoryHandler<?> getInputInventory() {
		return inputInventory;
	}

	public InventoryHandler<?> getFuelInventory() {
		return fuelInventory;
	}

	public InventoryHandler<?> getOutputInventory() {
		return outputInventory;
	}

	@Override
	protected FuelHandler getFuelHandler() {
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
	protected boolean doesRecipeResultFitIntoOutputInv(DigestingRecipe craftingGoal, ItemStack stackToCraft) {
		return ItemHandlerUtil.doesItemFit(outputInventory.getRaw(), stackToCraft);
	}

	@Override
	protected @Nullable DigestingRecipe resolveRecipeFromInput(Level level) {
		return RECIPE_TYPE.get().getRecipeFromContainer(level, inputInventory.getRecipeWrapper()).orElse(null);
	}

	@Override
	protected boolean doesRecipeMatchInput(DigestingRecipe recipeToTest, Level level) {
		return recipeToTest.matches(inputInventory.getRecipeWrapper(), level);
	}

	@Override
	protected boolean craftRecipe(DigestingRecipe recipeToCraft, Level level) {
		ItemStack result = recipeToCraft.assemble(inputInventory.getRecipeWrapper(), level.registryAccess());

		if (!result.isEmpty() && doesRecipeResultFitIntoOutputInv(recipeToCraft, result)) {
			ItemStack craftingRemainder = getCraftingRemainder();

			inputInventory.extractItem(0, 1, false); //consume input
			ItemHandlerUtil.insertItem(outputInventory.getRaw(), result); //output result

			if (!craftingRemainder.isEmpty()) {
				ItemHandlerUtil.insertItem(outputInventory.getRaw(), craftingRemainder);
			}

			SoundUtil.broadcastBlockSound((ServerLevel) level, getBlockPos(), ModSoundEvents.DIGESTER_CRAFTING_COMPLETED);

			setChanged();
			return true;
		}

		return false;
	}

	private ItemStack getCraftingRemainder() {
		ItemStack stack = inputInventory.getStackInSlot(0);

		if (stack.hasCraftingRemainingItem()) {
			return stack.getCraftingRemainingItem();
		}

		if (stack.getItem() instanceof BowlFoodItem) {
			return new ItemStack(Items.BOWL);
		}

		return ItemStack.EMPTY;
	}

	public ItemStack getInputSlotStack() {
		return inputInventory.getStackInSlot(0);
	}

	@Override
	protected void saveAdditional(CompoundTag tag) {
		super.saveAdditional(tag);
		stateData.serialize(tag);
		tag.put("Fuel", fuelHandler.serializeNBT());
		tag.put("FuelSlots", fuelInventory.serializeNBT());
		tag.put("InputSlots", inputInventory.serializeNBT());
		tag.put("OutputSlots", outputInventory.serializeNBT());
	}

	@Override
	public void load(CompoundTag tag) {
		super.load(tag);
		stateData.deserialize(tag);
		fuelHandler.deserializeNBT(tag.getCompound("Fuel"));
		fuelInventory.deserializeNBT(tag.getCompound("FuelSlots"));
		inputInventory.deserializeNBT(tag.getCompound("InputSlots"));
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
			return fuelInventory.getLazyOptional().cast();
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
		optionalFluidConsumer.invalidate();
	}

	@Override
	public void reviveCaps() {
		super.reviveCaps();
		fuelInventory.revive();
		inputInventory.revive();
		outputInventory.revive();
		optionalFluidConsumer = LazyOptional.of(fuelHandler::getFluidConsumer);
	}

	private <E extends DigesterBlockEntity> PlayState handleAnimationState(AnimationState<E> event) {
		Boolean isCrafting = getBlockState().getValue(MachineBlock.CRAFTING);

		if (Boolean.TRUE.equals(isCrafting)) {
			event.getController().setAnimation(WORKING_ANIM);
			loopingSoundHelper.startLoop(this, ModSoundEvents.DIGESTER_CRAFTING.get(), 0.65f);
		}
		else {
			event.getController().setAnimation(IDLE_ANIM);
			loopingSoundHelper.stopLoop();
		}

		return PlayState.CONTINUE;
	}

	@Override
	public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
		controllers.add(new AnimationController<>(this, "controller", 0, this::handleAnimationState));
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
