package com.github.elenterius.biomancy.world.block.decomposer;

import com.github.elenterius.biomancy.client.util.ClientLoopingSoundHelper;
import com.github.elenterius.biomancy.init.ModBlockEntities;
import com.github.elenterius.biomancy.init.ModCapabilities;
import com.github.elenterius.biomancy.init.ModRecipes;
import com.github.elenterius.biomancy.init.ModSoundEvents;
import com.github.elenterius.biomancy.recipe.DecomposerRecipe;
import com.github.elenterius.biomancy.recipe.SimpleRecipeType;
import com.github.elenterius.biomancy.recipe.VariableProductionOutput;
import com.github.elenterius.biomancy.styles.TextComponentUtil;
import com.github.elenterius.biomancy.util.ILoopingSoundHelper;
import com.github.elenterius.biomancy.util.SoundUtil;
import com.github.elenterius.biomancy.util.fuel.FuelHandler;
import com.github.elenterius.biomancy.util.fuel.IFuelHandler;
import com.github.elenterius.biomancy.world.block.MachineBlock;
import com.github.elenterius.biomancy.world.block.entity.MachineBlockEntity;
import com.github.elenterius.biomancy.world.inventory.BehavioralInventory;
import com.github.elenterius.biomancy.world.inventory.itemhandler.HandlerBehaviors;
import com.github.elenterius.biomancy.world.inventory.menu.DecomposerMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Container;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.registries.RegistryObject;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.SoundKeyframeEvent;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;
import software.bernie.geckolib3.util.GeckoLibUtil;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class DecomposerBlockEntity extends MachineBlockEntity<DecomposerRecipe, DecomposerStateData> implements MenuProvider, IAnimatable {

	public static final int FUEL_SLOTS = 1;
	public static final int INPUT_SLOTS = DecomposerRecipe.MAX_INGREDIENTS;
	public static final int OUTPUT_SLOTS = DecomposerRecipe.MAX_OUTPUTS;

	public static final int MAX_FUEL = 1_000;
	public static final int BASE_COST = 1;

	public static final RegistryObject<SimpleRecipeType.ItemStackRecipeType<DecomposerRecipe>> RECIPE_TYPE = ModRecipes.DECOMPOSING_RECIPE_TYPE;
	protected static final AnimationBuilder WORKING_ANIM = new AnimationBuilder().loop("decomposer.working");
	protected static final AnimationBuilder IDLE_ANIM = new AnimationBuilder().loop("decomposer.idle");

	private final DecomposerStateData stateData;
	private final FuelHandler fuelHandler;
	private final BehavioralInventory<?> fuelInventory;
	private final BehavioralInventory<?> inputInventory;
	private final BehavioralInventory<?> outputInventory;

	private final AnimationFactory animationFactory = GeckoLibUtil.createFactory(this);
	@Nullable
	private DecomposerRecipeResult computedRecipeResult;

	private ILoopingSoundHelper loopingSoundHelper = ILoopingSoundHelper.NULL;

	public DecomposerBlockEntity(BlockPos pos, BlockState state) {
		super(ModBlockEntities.DECOMPOSER.get(), pos, state);

		inputInventory = BehavioralInventory.createServerContents(INPUT_SLOTS, this::canPlayerOpenInv, this::setChanged);
		outputInventory = BehavioralInventory.createServerContents(OUTPUT_SLOTS, HandlerBehaviors::denyInput, this::canPlayerOpenInv, this::setChanged);

		fuelInventory = BehavioralInventory.createServerContents(FUEL_SLOTS, HandlerBehaviors::filterFuel, this::canPlayerOpenInv, this::setChanged);
		fuelHandler = FuelHandler.createNutrientFuelHandler(MAX_FUEL, BASE_COST, this::setChanged);

		stateData = new DecomposerStateData(fuelHandler);
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
		return TextComponentUtil.getTranslationText("container", "decomposer");
	}

	@Nullable
	@Override
	public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
		return DecomposerMenu.createServerMenu(containerId, playerInventory, fuelInventory, inputInventory, outputInventory, stateData);
	}

	@Override
	public boolean canPlayerOpenInv(Player player) {
		if (level == null || level.getBlockEntity(worldPosition) != this) return false;
		return player.distanceToSqr(Vec3.atCenterOf(worldPosition)) < 8d * 8d;
	}

	@Override
	protected DecomposerStateData getStateData() {
		return stateData;
	}

	@Override
	protected IFuelHandler getFuelHandler() {
		return fuelHandler;
	}

	@Override
	public ItemStack getStackInFuelSlot() {
		return fuelInventory.getItem(0);
	}

	@Override
	public void setStackInFuelSlot(ItemStack stack) {
		fuelInventory.setItem(0, stack);
	}

	@Override
	protected boolean doesRecipeResultFitIntoOutputInv(DecomposerRecipe craftingGoal, ItemStack ignored) {
		DecomposerRecipeResult precomputedResult = getComputedRecipeResult(craftingGoal);
		return outputInventory.doAllItemsFit(precomputedResult.items);
	}

	DecomposerRecipeResult getComputedRecipeResult(DecomposerRecipe craftingGoal) {
		if (computedRecipeResult == null || !computedRecipeResult.recipeId.equals(craftingGoal.getId())) {
			return DecomposerRecipeResult.computeRecipeResult(craftingGoal, level.random.nextInt());
		}

		return computedRecipeResult;
	}

	@Override
	protected @Nullable DecomposerRecipe resolveRecipeFromInput(Level level) {
		return RECIPE_TYPE.get().getRecipeFromContainer(level, inputInventory).orElse(null);
	}

	@Override
	protected boolean doesRecipeMatchInput(DecomposerRecipe recipeToTest, Level level) {
		return recipeToTest.matches(inputInventory, level);
	}

	@Override
	protected void saveAdditional(CompoundTag tag) {
		super.saveAdditional(tag);
		stateData.serialize(tag);
		if (computedRecipeResult != null) {
			tag.put("ComputedRecipeResult", computedRecipeResult.serialize());
		}
		tag.put("Fuel", fuelHandler.serializeNBT());
		tag.put("FuelSlots", fuelInventory.serializeNBT());
		tag.put("InputSlots", inputInventory.serializeNBT());
		tag.put("OutputSlots", outputInventory.serializeNBT());
	}

	@Override
	public void load(CompoundTag tag) {
		super.load(tag);
		stateData.deserialize(tag);
		if (level != null && tag.contains("ComputedRecipeResult")) {
			computedRecipeResult = DecomposerRecipeResult.deserialize(tag.getCompound("ComputedRecipeResult"), level.getRecipeManager());
		}
		fuelHandler.deserializeNBT(tag.getCompound("Fuel"));
		fuelInventory.deserializeNBT(tag.getCompound("FuelSlots"));
		inputInventory.deserializeNBT(tag.getCompound("InputSlots"));
		outputInventory.deserializeNBT(tag.getCompound("OutputSlots"));
	}

	@Override
	public void dropAllInvContents(Level level, BlockPos pos) {
		Containers.dropContents(level, pos, fuelInventory);
		Containers.dropContents(level, pos, inputInventory);
		Containers.dropContents(level, pos, outputInventory);
	}

	@Nonnull
	@Override
	public <T> LazyOptional<T> getCapability(Capability<T> cap, @Nullable Direction side) {
		if (!remove && cap == ModCapabilities.ITEM_HANDLER) {
			if (side == null || side == Direction.DOWN) return outputInventory.getOptionalItemHandler().cast();
			if (side == Direction.UP) return inputInventory.getOptionalItemHandler().cast();
			return fuelInventory.getOptionalItemHandler().cast();
		}
		return super.getCapability(cap, side);
	}

	@Override
	public void invalidateCaps() {
		super.invalidateCaps();
		fuelInventory.invalidate();
		inputInventory.invalidate();
		outputInventory.invalidate();
	}

	@Override
	public void reviveCaps() {
		super.reviveCaps();
		fuelInventory.revive();
		inputInventory.revive();
		outputInventory.revive();
	}

	@Override
	protected boolean craftRecipe(DecomposerRecipe recipeToCraft, Level level) {
		DecomposerRecipeResult precomputedResult = getComputedRecipeResult(recipeToCraft);

		if (!outputInventory.doAllItemsFit(precomputedResult.items)) return false;

		inputInventory.removeItem(0, recipeToCraft.getIngredientQuantity().count()); //consume input

		for (ItemStack stack : precomputedResult.items) {  //output result
			outputInventory.insertItemStack(stack);
		}

		SoundUtil.broadcastBlockSound((ServerLevel) level, getBlockPos(), ModSoundEvents.DECOMPOSER_CRAFTING_COMPLETED);

		setChanged();
		return true;
	}

	private <T extends DecomposerBlockEntity> void onSoundKeyframe(final SoundKeyframeEvent<T> event) {
		if (event.sound.equals("eat") && level != null && !isRemoved()) {
			SoundUtil.clientPlayBlockSound(level, getBlockPos(), ModSoundEvents.DECOMPOSER_EAT);
		}
	}

	private <T extends DecomposerBlockEntity> PlayState onAnimation(final AnimationEvent<T> event) {
		Boolean isCrafting = event.getAnimatable().getBlockState().getValue(MachineBlock.CRAFTING);

		if (Boolean.TRUE.equals(isCrafting)) {
			event.getController().setAnimation(WORKING_ANIM);
			loopingSoundHelper.startLoop(this, ModSoundEvents.DECOMPOSER_CRAFTING.get(), 0.65f);
		}
		else {
			event.getController().setAnimation(IDLE_ANIM);
			loopingSoundHelper.stopLoop();
		}

		return PlayState.CONTINUE;
	}

	@Override
	public void registerControllers(AnimationData data) {
		AnimationController<DecomposerBlockEntity> controller = new AnimationController<>(this, "controller", 10, this::onAnimation);
		controller.registerSoundListener(this::onSoundKeyframe);
		data.addAnimationController(controller);
	}

	@Override
	public AnimationFactory getFactory() {
		return animationFactory;
	}


	@Override
	public void setRemoved() {
		if (level != null && level.isClientSide) {
			loopingSoundHelper.clear();
		}
		super.setRemoved();
	}

	record DecomposerRecipeResult(ResourceLocation recipeId, int seed, List<ItemStack> items) {
		@Nullable
		public static DecomposerRecipeResult deserialize(CompoundTag tag, RecipeManager recipeManager) {
			String id = tag.getString("recipeId");
			ResourceLocation recipeId = ResourceLocation.tryParse(id);
			if (recipeId == null) return null;

			Recipe<Container> recipe = recipeManager.byType(RECIPE_TYPE.get()).get(recipeId);
			if (recipe instanceof DecomposerRecipe decomposerRecipe) {
				return computeRecipeResult(decomposerRecipe, tag.getInt("seed"));
			}

			return null;
		}

		public static DecomposerRecipeResult computeRecipeResult(DecomposerRecipe recipe, int seed) {
			RandomSource random = RandomSource.create(seed);

			List<ItemStack> items = new ArrayList<>();
			for (VariableProductionOutput output : recipe.getOutputs()) {
				ItemStack stack = output.getItemStack(random);
				if (!stack.isEmpty()) items.add(stack);
			}

			return new DecomposerRecipeResult(recipe.getId(), seed, items);
		}

		public CompoundTag serialize() {
			CompoundTag tag = new CompoundTag();
			tag.putString("recipeId", recipeId().toString());
			tag.putInt("seed", seed);
			return tag;
		}

	}

}
