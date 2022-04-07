package com.github.elenterius.biomancy.world.block.entity;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.init.ModBlockEntities;
import com.github.elenterius.biomancy.init.ModRecipes;
import com.github.elenterius.biomancy.network.ModNetworkHandler;
import com.github.elenterius.biomancy.recipe.BioForgeRecipe;
import com.github.elenterius.biomancy.recipe.IngredientQuantity;
import com.github.elenterius.biomancy.recipe.RecipeTypeImpl;
import com.github.elenterius.biomancy.util.TextComponentUtil;
import com.github.elenterius.biomancy.world.block.entity.state.BioForgeStateData;
import com.github.elenterius.biomancy.world.inventory.BehavioralInventory;
import com.github.elenterius.biomancy.world.inventory.SimpleInventory;
import com.github.elenterius.biomancy.world.inventory.itemhandler.HandlerBehaviors;
import com.github.elenterius.biomancy.world.inventory.menu.BioForgeMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import org.apache.logging.log4j.MarkerManager;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Optional;

public class BioForgeBlockEntity extends MachineBlockEntity<BioForgeRecipe, BioForgeStateData> implements MenuProvider, IAnimatable {

	public static final int FUEL_SLOTS = 1;
	public static final int INPUT_SLOTS = BioForgeRecipe.MAX_INGREDIENTS + BioForgeRecipe.MAX_REACTANT;
	public static final int OUTPUT_SLOTS = 1;

	public static final int FUEL_COST = 2;
	public static final int MAX_FUEL = 32_000;
	public static final RecipeTypeImpl.ItemStackRecipeType<BioForgeRecipe> RECIPE_TYPE = ModRecipes.BIO_FORGING_RECIPE_TYPE;

	private final AnimationFactory animationFactory = new AnimationFactory(this);

	private final BioForgeStateData stateData = new BioForgeStateData();
	private final BehavioralInventory<?> fuelInventory;
	private final SimpleInventory inputInventory;
	private final BehavioralInventory<?> outputInventory;

	public BioForgeBlockEntity(BlockPos worldPosition, BlockState blockState) {
		super(ModBlockEntities.BIO_FORGE.get(), worldPosition, blockState);
		fuelInventory = BehavioralInventory.createServerContents(FUEL_SLOTS, HandlerBehaviors::filterFuel, this::canPlayerOpenInv, this::setChanged);
		inputInventory = SimpleInventory.createServerContents(INPUT_SLOTS, this::canPlayerOpenInv, this::setChanged);
		outputInventory = BehavioralInventory.createServerContents(OUTPUT_SLOTS, HandlerBehaviors::denyInput, this::canPlayerOpenInv, this::setChanged);
	}

	@Nullable
	@Override
	public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
		return BioForgeMenu.createServerMenu(containerId, playerInventory, fuelInventory, inputInventory, outputInventory, stateData, this::setSelectedRecipe);
	}

	public void setSelectedRecipe(@Nullable BioForgeRecipe recipe) {
		stateData.selectedRecipeId = recipe == null ? null : recipe.getId();

		if (level != null && !level.isClientSide) {
			ModNetworkHandler.sendToClientsTrackingBioForge(this, recipe);
		}

		setChanged();
	}

	@Override
	public BioForgeStateData getStateData() {
		return stateData;
	}

	@Override
	public int getFuelAmount() {
		return stateData.getFuelAmount();
	}

	@Override
	public void setFuelAmount(int newAmount) {
		stateData.setFuelAmount((short) newAmount);
	}

	@Override
	public void addFuelAmount(int addAmount) {
		stateData.setFuelAmount((short) (stateData.getFuelAmount() + addAmount));
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
	protected boolean doesItemFitIntoOutputInventory(ItemStack stackToCraft) {
		return outputInventory.getItem(0).isEmpty() || outputInventory.doesItemStackFit(0, stackToCraft);
	}

	@Override
	protected boolean craftRecipe(BioForgeRecipe recipe, Level level) {
		ItemStack result = recipe.assemble(inputInventory);
		if (result.isEmpty() || !doesItemFitIntoOutputInventory(result)) {
			return false;
		}

		List<IngredientQuantity> ingredientQuantities = recipe.getIngredientQuantities();
		for (IngredientQuantity ingredientQuantity : ingredientQuantities) {
			Ingredient ingredient = ingredientQuantity.ingredient();
			int amount = ingredientQuantity.count();
			for (int idx = 0; idx < inputInventory.getContainerSize() - 1; idx++) {
				ItemStack stack = inputInventory.getItem(idx);
				if (!stack.isEmpty() && ingredient.test(stack)) {
					ItemStack extractedStack = inputInventory.removeItem(idx, amount);
					amount -= extractedStack.getCount();
				}
				if (amount <= 0) break;
			}
			if (amount > 0)
				BiomancyMod.LOGGER.warn(MarkerManager.getMarker("BioForge"), "Correct Ingredient consumption failed for {}, missing {} items!", ingredientQuantity, amount);
		}

		inputInventory.removeItem(inputInventory.getContainerSize() - 1, 1);

		outputInventory.insertItemStack(0, result.copy());

		//if we can't craft any further items clear the selected recipe
		if (!recipe.matches(inputInventory, level)) {
			setSelectedRecipe(null);
		}

		setChanged();
		return true;
	}

	@Nullable
	@Override
	protected BioForgeRecipe resolveRecipeFromInput(Level level) {
		if (stateData.selectedRecipeId == null) return null;
		Optional<BioForgeRecipe> recipe = RECIPE_TYPE.getRecipeById(level, stateData.selectedRecipeId);
		return recipe.isPresent() && recipe.get().matches(inputInventory, level) ? recipe.get() : null;
	}

	@Override
	protected void saveAdditional(CompoundTag tag) {
		super.saveAdditional(tag);
		stateData.serialize(tag);
		tag.put("FuelSlots", fuelInventory.serializeNBT());
		tag.put("InputSlots", inputInventory.serializeNBT());
		tag.put("OutputSlots", outputInventory.serializeNBT());
	}

	@Override
	public void load(CompoundTag tag) {
		super.load(tag);
		stateData.deserialize(tag);
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

	@Override
	public Component getDisplayName() {
		return getName();
	}

	@Override
	public Component getName() {
		return TextComponentUtil.getTranslationText("container", "bio_forge");
	}

	private <E extends BlockEntity & IAnimatable> PlayState handleAnim(AnimationEvent<E> event) {
		event.getController().setAnimation(new AnimationBuilder().addAnimation("placeholder.anim.idle", true));
		return PlayState.CONTINUE;
	}

	@Override
	public void registerControllers(AnimationData data) {
		data.addAnimationController(new AnimationController<>(this, "controller", 0, this::handleAnim));
	}

	@Override
	public AnimationFactory getFactory() {
		return animationFactory;
	}

}
