package com.github.elenterius.biomancy.world.block.entity;

import com.github.elenterius.biomancy.init.ModBlockEntities;
import com.github.elenterius.biomancy.init.ModRecipes;
import com.github.elenterius.biomancy.recipe.DecomposerRecipe;
import com.github.elenterius.biomancy.recipe.RecipeTypeImpl;
import com.github.elenterius.biomancy.recipe.VariableProductionOutput;
import com.github.elenterius.biomancy.util.TextComponentUtil;
import com.github.elenterius.biomancy.world.block.MachineBlock;
import com.github.elenterius.biomancy.world.block.entity.state.DecomposerStateData;
import com.github.elenterius.biomancy.world.inventory.BehavioralInventory;
import com.github.elenterius.biomancy.world.inventory.itemhandler.HandlerBehaviors;
import com.github.elenterius.biomancy.world.inventory.menu.DecomposerMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
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
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class DecomposerBlockEntity extends MachineBlockEntity<DecomposerRecipe, DecomposerStateData> implements MenuProvider, IAnimatable {

	public static final int FUEL_SLOTS = 1;
	public static final int INPUT_SLOTS = DecomposerRecipe.MAX_INGREDIENTS;
	public static final int OUTPUT_SLOTS = DecomposerRecipe.MAX_OUTPUTS;

	public static final int MAX_FUEL = 32_000;
	public static final short FUEL_COST = 5;
	public static final RecipeTypeImpl.ItemStackRecipeType<DecomposerRecipe> RECIPE_TYPE = ModRecipes.DECOMPOSING_RECIPE_TYPE;

	private final DecomposerStateData stateData = new DecomposerStateData();
	private final BehavioralInventory<?> fuelInventory;
	private final BehavioralInventory<?> inputInventory;
	private final BehavioralInventory<?> outputInventory;

	private final AnimationFactory animationFactory = new AnimationFactory(this);
	@Nullable
	private DecomposerRecipeResult computedRecipeResult;

	public DecomposerBlockEntity(BlockPos pos, BlockState state) {
		super(ModBlockEntities.DECOMPOSER.get(), pos, state);
		fuelInventory = BehavioralInventory.createServerContents(FUEL_SLOTS, HandlerBehaviors::filterFuel, this::canPlayerOpenInv, this::setChanged);
		inputInventory = BehavioralInventory.createServerContents(INPUT_SLOTS, this::canPlayerOpenInv, this::setChanged);
		outputInventory = BehavioralInventory.createServerContents(OUTPUT_SLOTS, HandlerBehaviors::denyInput, this::canPlayerOpenInv, this::setChanged);
	}

	public static void serverTick(Level level, BlockPos pos, BlockState state, DecomposerBlockEntity decomposer) {
		decomposer.serverTick((ServerLevel) level);
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

	public boolean canPlayerOpenInv(Player player) {
		if (level == null || level.getBlockEntity(worldPosition) != this) return false;
		return player.distanceToSqr(Vec3.atCenterOf(worldPosition)) < 8d * 8d;
	}

	@Override
	protected DecomposerStateData getStateData() {
		return stateData;
	}

	@Override
	public int getFuelAmount() {
		return stateData.getFuelAmount();
	}

	@Override
	public void setFuelAmount(int newAmount) {
		stateData.setFuelAmount(newAmount);
	}

	@Override
	public void addFuelAmount(int addAmount) {
		stateData.setFuelAmount(stateData.getFuelAmount() + addAmount);
	}

	@Override
	public int getMaxFuelAmount() {
		return MAX_FUEL;
	}

	@Override
	public int getFuelCost() {
		return FUEL_COST;
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

	@Override
	protected boolean craftRecipe(DecomposerRecipe recipeToCraft, Level level) {
		DecomposerRecipeResult precomputedResult = getComputedRecipeResult(recipeToCraft);

		if (!outputInventory.doAllItemsFit(precomputedResult.items)) return false;

		inputInventory.removeItem(0, recipeToCraft.getIngredientQuantity().count()); //consume input

		for (ItemStack stack : precomputedResult.items) {
			outputInventory.insertItemStack(stack);
		}

		setChanged();
		return true;
	}

	DecomposerRecipeResult getComputedRecipeResult(DecomposerRecipe craftingGoal) {
		if (computedRecipeResult == null || !computedRecipeResult.recipeId.equals(craftingGoal.getId())) {
			return DecomposerRecipeResult.computeRecipeResult(craftingGoal, level.random.nextInt());
		}

		return computedRecipeResult;
	}

	@Override
	protected @Nullable DecomposerRecipe resolveRecipeFromInput(Level level) {
		return RECIPE_TYPE.getRecipeFromContainer(level, inputInventory).orElse(null);
	}

	@Override
	protected void saveAdditional(CompoundTag tag) {
		super.saveAdditional(tag);
		stateData.serialize(tag);
		if (computedRecipeResult != null) {
			tag.put("ComputedRecipeResult", computedRecipeResult.serialize());
		}
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
		if (!remove && cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
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

	private <E extends BlockEntity & IAnimatable> PlayState handleIdleAnim(AnimationEvent<E> event) {
		Boolean isCrafting = event.getAnimatable().getBlockState().getValue(MachineBlock.CRAFTING);
		if (Boolean.TRUE.equals(isCrafting)) {
			event.getController().setAnimation(new AnimationBuilder().addAnimation("decomposer.anim.working", true));
		} else {
			event.getController().setAnimation(new AnimationBuilder().addAnimation("decomposer.anim.idle.normal", true));
		}
		return PlayState.CONTINUE;
	}

	@Override
	public void registerControllers(AnimationData data) {
		data.addAnimationController(new AnimationController<>(this, "idle_controller", 10, this::handleIdleAnim));
	}

	@Override
	public AnimationFactory getFactory() {
		return animationFactory;
	}

	record DecomposerRecipeResult(ResourceLocation recipeId, int seed, List<ItemStack> items) {
		@Nullable
		public static DecomposerRecipeResult deserialize(CompoundTag tag, RecipeManager recipeManager) {
			String id = tag.getString("recipeId");
			ResourceLocation recipeId = ResourceLocation.tryParse(id);
			if (recipeId == null) return null;

			Recipe<Container> recipe = recipeManager.byType(RECIPE_TYPE).get(recipeId);
			if (recipe instanceof DecomposerRecipe decomposerRecipe) {
				return computeRecipeResult(decomposerRecipe, tag.getInt("seed"));
			}

			return null;
		}

		public static DecomposerRecipeResult computeRecipeResult(DecomposerRecipe recipe, int seed) {
			Random random = new Random(seed);

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
