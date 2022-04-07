package com.github.elenterius.biomancy.world.block.entity;

import com.github.elenterius.biomancy.init.ModBlockEntities;
import com.github.elenterius.biomancy.init.ModRecipes;
import com.github.elenterius.biomancy.recipe.DigesterRecipe;
import com.github.elenterius.biomancy.recipe.RecipeTypeImpl;
import com.github.elenterius.biomancy.util.TextComponentUtil;
import com.github.elenterius.biomancy.world.block.entity.state.DigesterStateData;
import com.github.elenterius.biomancy.world.inventory.BehavioralInventory;
import com.github.elenterius.biomancy.world.inventory.itemhandler.HandlerBehaviors;
import com.github.elenterius.biomancy.world.inventory.menu.DigesterMenu;
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
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;
import java.util.Objects;

public class DigesterBlockEntity extends MachineBlockEntity<DigesterRecipe, DigesterStateData> implements MenuProvider {

	public static final int FUEL_SLOTS = 1;
	public static final int INPUT_SLOTS = 1;
	public static final int OUTPUT_SLOTS = 2;

	public static final int MAX_FUEL = 32_000;
	public static final short FUEL_COST = 2;
	public static final RecipeTypeImpl.ItemStackRecipeType<DigesterRecipe> RECIPE_TYPE = ModRecipes.DIGESTING_RECIPE_TYPE;

	private final DigesterStateData stateData = new DigesterStateData();
	private final BehavioralInventory<?> fuelInventory;
	private final BehavioralInventory<?> inputInventory;
	private final BehavioralInventory<?> outputInventory;

	public DigesterBlockEntity(BlockPos pos, BlockState state) {
		super(ModBlockEntities.DIGESTER.get(), pos, state);
		fuelInventory = BehavioralInventory.createServerContents(FUEL_SLOTS, HandlerBehaviors::filterFuel, this::canPlayerOpenInv, this::setChanged);
		inputInventory = BehavioralInventory.createServerContents(INPUT_SLOTS, this::canPlayerOpenInv, this::setChanged);
		outputInventory = BehavioralInventory.createServerContents(OUTPUT_SLOTS, HandlerBehaviors::denyInput, this::canPlayerOpenInv, this::setChanged);
	}

	@Override
	public Component getDisplayName() {
		return getName();
	}

	@Override
	public Component getName() {
		return TextComponentUtil.getTranslationText("container", Objects.requireNonNull(getType().getRegistryName()).getPath());
	}

	@Nullable
	@Override
	public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
		return DigesterMenu.createServerMenu(containerId, playerInventory, fuelInventory, inputInventory, outputInventory, stateData);
	}

	public boolean canPlayerOpenInv(Player player) {
		if (level == null || level.getBlockEntity(worldPosition) != this) return false;
		return player.distanceToSqr(Vec3.atCenterOf(worldPosition)) < 8d * 8d;
	}

	@Override
	protected DigesterStateData getStateData() {
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
	protected boolean doesItemFitIntoOutputInventory(ItemStack stackToCraft) {
		return outputInventory.doesItemStackFit(stackToCraft);
	}

	@Override
	protected boolean craftRecipe(DigesterRecipe recipeToCraft, Level level) {
		ItemStack result = recipeToCraft.assemble(inputInventory);
		if (!result.isEmpty() && doesItemFitIntoOutputInventory(result)) {
			inputInventory.removeItem(0, 1);
			outputInventory.insertItemStack(result);
			setChanged();
			return true;
		}
		return false;
	}

	@Override
	protected @Nullable DigesterRecipe resolveRecipeFromInput(Level level) {
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

}
