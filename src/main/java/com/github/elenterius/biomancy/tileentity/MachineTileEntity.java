package com.github.elenterius.biomancy.tileentity;

import com.github.elenterius.biomancy.block.MachineBlock;
import com.github.elenterius.biomancy.recipe.AbstractProductionRecipe;
import com.github.elenterius.biomancy.recipe.IFluidResultRecipe;
import com.github.elenterius.biomancy.tileentity.state.CraftingState;
import com.github.elenterius.biomancy.tileentity.state.RecipeCraftingStateData;
import net.minecraft.block.BlockState;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.state.BooleanProperty;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.items.ItemHandlerHelper;

import javax.annotation.Nullable;

public abstract class MachineTileEntity<R extends AbstractProductionRecipe, S extends RecipeCraftingStateData<R>> extends OwnableTileEntity implements INamedContainerProvider, ITickableTileEntity {

	public MachineTileEntity(TileEntityType<?> entityType) {
		super(entityType);
	}

	protected abstract S getStateData();

	public abstract int getFuelAmount();

	public abstract void setFuelAmount(int newAmount);

	public abstract void addFuelAmount(int addAmount);

	public abstract int getMaxFuelAmount();

	public abstract int getFuelCost();

	public abstract boolean isItemValidFuel(ItemStack stack);

	public abstract float getItemFuelValue(ItemStack stackIn);

	public abstract ItemStack getStackInFuelSlot();

	public abstract void setStackInFuelSlot(ItemStack stack);

	protected abstract boolean doesItemFitIntoOutputInventory(ItemStack stackToCraft);

	protected boolean doesFluidFitIntoOutputTank(FluidStack stackToCraft) { return false; }

	protected abstract boolean craftRecipe(R recipeToCraft, World world);

	@Nullable
	protected abstract R resolveRecipeFromInput(World world);

	public abstract void dropAllInvContents(World world, BlockPos pos);

	public boolean consumeFuel() {
		int fuelCost = getFuelCost();
		if (getFuelAmount() >= fuelCost) {
			addFuelAmount(-fuelCost);
			return true;
		}
		return false;
	}

	public void refuel() {
		if (getFuelAmount() < getMaxFuelAmount()) {
			ItemStack stack = getStackInFuelSlot();
			if (isItemValidFuel(stack)) {
				ItemStack remainder = addFuel(stack);
				if (remainder.getCount() != stack.getCount()) {
					setStackInFuelSlot(remainder);
					markDirty();
				}
			}
		}
	}

	public ItemStack addFuel(ItemStack stackIn) {
		if (world == null || world.isRemote()) return stackIn;

		if (!stackIn.isEmpty() && getFuelAmount() < getMaxFuelAmount()) {
			float itemFuelValue = getItemFuelValue(stackIn);
			if (itemFuelValue <= 0f) return stackIn;

			int itemsNeeded = MathHelper.floor(Math.max(0, getMaxFuelAmount() - getFuelAmount()) / itemFuelValue);
			int consumeAmount = Math.min(stackIn.getCount(), itemsNeeded);
			if (consumeAmount > 0) {
				short newFuel = (short) MathHelper.clamp(getFuelAmount() + itemFuelValue * consumeAmount, 0, getMaxFuelAmount());
				setFuelAmount(newFuel);
				return ItemHandlerHelper.copyStackWithSize(stackIn, stackIn.getCount() - consumeAmount);
			}
		}
		return stackIn;
	}

	@Override
	public void tick() {
		if (world == null || world.isRemote) return;

		if (world.getGameTime() % 10L == 0L) {
			refuel();
		}

		R craftingGoal = resolveRecipeFromInput(world); //get the currently possible crafting goal
		S state = getStateData();
		boolean emitRedstoneSignal = false;
		if (craftingGoal == null) {
			state.cancelCrafting();
		}
		else {
			if (craftingGoal instanceof IFluidResultRecipe) {
				FluidStack fluidToCraft = ((IFluidResultRecipe) craftingGoal).getFluidOutput();
				if (fluidToCraft.isEmpty()) {
					state.cancelCrafting();
				}
				else {
					if (doesFluidFitIntoOutputTank(fluidToCraft)) {
						if (state.getCraftingState() == CraftingState.NONE) { // nothing is being crafted, try to start crafting
							int totalFuelCost = craftingGoal.getCraftingTime() * getFuelCost();
							if (getFuelAmount() >= totalFuelCost) { //make sure there is enough fuel to craft the recipe
								state.setCraftingState(CraftingState.IN_PROGRESS);
								state.clear(); //safe guard, shouldn't be needed
								state.setCraftingGoalRecipe(craftingGoal); // this also sets the time required for crafting
							}
						}
						else if (!state.isCraftingCanceled()) { // something is being crafted, check that the crafting goals match
							R prevCraftingGoal = state.getCraftingGoalRecipe(world).orElse(null);
							if (prevCraftingGoal == null || !craftingGoal.areRecipesEqual(prevCraftingGoal, true)) {
								state.cancelCrafting();
							}
						}
					}
					else {
						if (state.getCraftingState() != CraftingState.COMPLETED) {
							state.cancelCrafting();
						}
					}
				}
			}
			else {
				ItemStack itemToCraft = craftingGoal.getRecipeOutput();
				if (itemToCraft.isEmpty()) {
					state.cancelCrafting();
				}
				else {
					if (doesItemFitIntoOutputInventory(itemToCraft)) {
						if (state.getCraftingState() == CraftingState.NONE) { // nothing is being crafted, try to start crafting
							int totalFuelCost = craftingGoal.getCraftingTime() * getFuelCost();
							if (getFuelAmount() >= totalFuelCost) { //make sure there is enough fuel to craft the recipe
								state.setCraftingState(CraftingState.IN_PROGRESS);
								state.clear(); //safe guard, shouldn't be needed
								state.setCraftingGoalRecipe(craftingGoal); // this also sets the time required for crafting
							}
						}
						else if (!state.isCraftingCanceled()) { // something is being crafted, check that the crafting goals match
							R prevCraftingGoal = state.getCraftingGoalRecipe(world).orElse(null);
							if (prevCraftingGoal == null || !craftingGoal.areRecipesEqual(prevCraftingGoal, true)) {
								state.cancelCrafting();
							}
						}
					}
					else {
						if (state.getCraftingState() != CraftingState.COMPLETED) {
							state.cancelCrafting();
						}
					}
				}
			}

			//change crafting progress
			if (state.getCraftingState() == CraftingState.IN_PROGRESS) {
				if (consumeFuel()) state.timeElapsed += 1;
				else state.timeElapsed -= 2;

				if (state.timeElapsed < 0) state.timeElapsed = 0;
			}

			//craft the recipe output
			if (state.getCraftingState() == CraftingState.IN_PROGRESS || state.getCraftingState() == CraftingState.COMPLETED) {
				if (state.timeElapsed >= state.timeForCompletion) {
					state.setCraftingState(CraftingState.COMPLETED);
					if (craftRecipe(craftingGoal, world)) {
						emitRedstoneSignal = true;
						state.setCraftingState(CraftingState.NONE);
					}
				}
			}
		}

		//clean-up states
		if (state.isCraftingCanceled()) {
			state.setCraftingState(CraftingState.NONE);
			state.clear();
		}
		else if (state.getCraftingState() == CraftingState.NONE) {
			state.clear();
		}

		//update BlockState to reflect tile state
		updateBlockState(world, state, emitRedstoneSignal);
	}

	protected BooleanProperty getIsCraftingBlockStateProperty() {
		return MachineBlock.CRAFTING;
	}

	protected void updateBlockState(World world, S tileState, boolean redstoneSignal) {
		BlockState oldBlockState = world.getBlockState(pos);
		BlockState newBlockState = oldBlockState.with(getIsCraftingBlockStateProperty(), tileState.getCraftingState() == CraftingState.IN_PROGRESS);
		if (!newBlockState.equals(oldBlockState)) {
			if (redstoneSignal) {
				if (newBlockState.getBlock() instanceof MachineBlock) {
					((MachineBlock<?>) newBlockState.getBlock()).powerBlock(world, pos, newBlockState);
				}
			}
			else {
				world.setBlockState(pos, newBlockState, Constants.BlockFlags.BLOCK_UPDATE);
			}
			markDirty();
		}
		else if (redstoneSignal) {
			if (newBlockState.getBlock() instanceof MachineBlock) {
				((MachineBlock<?>) newBlockState.getBlock()).powerBlock(world, pos, oldBlockState);
			}
		}
	}

}
