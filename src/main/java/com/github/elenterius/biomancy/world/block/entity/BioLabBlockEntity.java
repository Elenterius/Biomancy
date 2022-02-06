package com.github.elenterius.biomancy.world.block.entity;

import com.github.elenterius.biomancy.init.ModBlockEntities;
import com.github.elenterius.biomancy.init.ModItems;
import com.github.elenterius.biomancy.init.ModRecipes;
import com.github.elenterius.biomancy.recipe.BioLabRecipe;
import com.github.elenterius.biomancy.recipe.RecipeTypeImpl;
import com.github.elenterius.biomancy.util.TextComponentUtil;
import com.github.elenterius.biomancy.world.block.entity.state.BioLabStateData;
import com.github.elenterius.biomancy.world.inventory.BehavioralInventory;
import com.github.elenterius.biomancy.world.inventory.SimpleInventory;
import com.github.elenterius.biomancy.world.inventory.itemhandler.HandlerBehaviors;
import com.github.elenterius.biomancy.world.inventory.menu.BioLabMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;

import javax.annotation.Nonnull;
import java.util.function.Predicate;

public class BioLabBlockEntity extends MachineBlockEntity<BioLabRecipe, BioLabStateData> implements MenuProvider, IAnimatable {

	public static final int FUEL_SLOTS = 1;
	public static final int INPUT_SLOTS = BioLabRecipe.MAX_INGREDIENTS;
	public static final int OUTPUT_SLOTS = 3;

	public static final int MAX_FUEL = 32_000;
	public static final short FUEL_COST = 2;
	public static final float ITEM_FUEL_VALUE = 200; // FUEL_COST * 400 / 4f
	public static final Predicate<ItemStack> VALID_FUEL_ITEM = stack -> stack.getItem() == ModItems.BILE.get();
	public static final RecipeTypeImpl.ItemStackRecipeType<BioLabRecipe> RECIPE_TYPE = ModRecipes.BIO_BREWING_RECIPE_TYPE;

	private final BioLabStateData stateData = new BioLabStateData();
	private final BehavioralInventory<?> fuelInventory;
	private final SimpleInventory inputInventory;
	private final BehavioralInventory<?> outputInventory;

	private final AnimationFactory animationFactory = new AnimationFactory(this);

	public BioLabBlockEntity(BlockPos worldPosition, BlockState blockState) {
		super(ModBlockEntities.BIO_LAB.get(), worldPosition, blockState);
		fuelInventory = BehavioralInventory.createServerContents(FUEL_SLOTS, ish -> HandlerBehaviors.filterInput(ish, VALID_FUEL_ITEM), this::canPlayerOpenInv, this::setChanged);
		inputInventory = SimpleInventory.createServerContents(INPUT_SLOTS, this::canPlayerOpenInv, this::setChanged);
		outputInventory = BehavioralInventory.createServerContents(OUTPUT_SLOTS, HandlerBehaviors::denyInput, this::canPlayerOpenInv, this::setChanged);
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
		return BioLabMenu.createServerMenu(containerId, playerInventory, fuelInventory, inputInventory, outputInventory, stateData);
	}

	public static void serverTick(Level level, BlockPos pos, BlockState state, BioLabBlockEntity bioLab) {
		bioLab.serverTick((ServerLevel) level);
	}

	public boolean canPlayerOpenInv(Player player) {
		if (level == null || level.getBlockEntity(worldPosition) != this) return false;
		return player.distanceToSqr(Vec3.atCenterOf(worldPosition)) < 8d * 8d;
	}

	@Override
	protected BioLabStateData getStateData() {
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
	public boolean isItemValidFuel(ItemStack stack) {
		return VALID_FUEL_ITEM.test(stack);
	}

	@Override
	public float getItemFuelValue(ItemStack stackIn) {
		return ITEM_FUEL_VALUE;
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
		int emptyCount = 0;
		int containerSize = outputInventory.getContainerSize();
		for (int i = 0; i < containerSize; i++) {
			ItemStack stackInSlot = outputInventory.getItem(i);
			boolean isEmpty = stackInSlot.isEmpty();
			if (isEmpty) emptyCount++;
			else if (!stackInSlot.is(ModItems.GLASS_VIAL.get()) && !outputInventory.doesItemStackFit(i, stackToCraft)) return false;
		}
		return emptyCount < containerSize;
	}

	@Override
	protected boolean craftRecipe(BioLabRecipe recipeToCraft, Level level) {
		ItemStack result = recipeToCraft.assemble(inputInventory);
		if (!result.isEmpty() && doesItemFitIntoOutputInventory(result)) {
			for (int i = 0; i < outputInventory.getContainerSize(); i++) {
				ItemStack stackInSlot = outputInventory.getItem(i);
				if (stackInSlot.isEmpty()) continue;

				if (stackInSlot.is(ModItems.GLASS_VIAL.get())) {
					outputInventory.setItem(i, result.copy());
				}
				else if (outputInventory.doesItemStackFit(i, result)) {
					outputInventory.insertItemStack(i, result.copy());
				}
			}
			for (int idx = 0; idx < inputInventory.getContainerSize(); idx++) {
				inputInventory.removeItem(idx, 1);
			}
			setChanged();
			return true;
		}
		return false;
	}

	@Nullable
	@Override
	protected BioLabRecipe resolveRecipeFromInput(Level level) {
		return RECIPE_TYPE.getRecipeFromContainer(level, inputInventory).orElse(null);
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

	private <E extends BlockEntity & IAnimatable> PlayState handlePlaceholderAnim(AnimationEvent<E> event) {
		event.getController().setAnimation(new AnimationBuilder().addAnimation("placeholder.anim.idle", true));
		return PlayState.CONTINUE;
	}

	@Override
	public void registerControllers(AnimationData data) {
		data.addAnimationController(new AnimationController<>(this, "placeholder_controller", 0, this::handlePlaceholderAnim));
	}

	@Override
	public AnimationFactory getFactory() {
		return animationFactory;
	}

}
