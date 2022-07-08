package com.github.elenterius.biomancy.world.block.entity;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.init.ModBlocks;
import com.github.elenterius.biomancy.recipe.AbstractProductionRecipe;
import com.github.elenterius.biomancy.util.fuel.IFuelHandler;
import com.github.elenterius.biomancy.world.block.MachineBlock;
import com.github.elenterius.biomancy.world.block.entity.state.CraftingState;
import com.github.elenterius.biomancy.world.block.entity.state.RecipeCraftingStateData;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Nameable;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public abstract class MachineBlockEntity<R extends AbstractProductionRecipe, S extends RecipeCraftingStateData<R>> extends BlockEntity implements Nameable {

	protected final int tickOffset = BiomancyMod.GLOBAL_RANDOM.nextInt(20);
	protected int ticks = tickOffset;

	protected MachineBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
	}

	public static <R extends AbstractProductionRecipe, S extends RecipeCraftingStateData<R>> void serverTick(Level level, BlockPos pos, BlockState state, MachineBlockEntity<R, S> entity) {
		entity.serverTick((ServerLevel) level);
	}

	public boolean canPlayerOpenInv(Player player) {
		if (level == null || level.getBlockEntity(worldPosition) != this) return false;
		return player.distanceToSqr(Vec3.atCenterOf(worldPosition)) < 8d * 8d;
	}

	public int getTicks() {
		return ticks - tickOffset;
	}

	protected abstract S getStateData();

	protected abstract IFuelHandler getFuelHandler();

	public int getFuelCost(R recipeToCraft) {
		return getFuelHandler().getFuelCost(recipeToCraft.getCraftingTime());
	}

	public abstract ItemStack getStackInFuelSlot();

	public abstract void setStackInFuelSlot(ItemStack stack);

	protected abstract boolean doesRecipeResultFitIntoOutputInv(R craftingGoal, ItemStack stackToCraft);

	protected abstract boolean craftRecipe(R recipeToCraft, Level level);

	@Nullable
	protected abstract R resolveRecipeFromInput(Level level);

	protected abstract boolean doesRecipeMatchInput(R recipeToTest, Level level);

	public abstract void dropAllInvContents(Level level, BlockPos pos);

	public boolean hasEnoughFuel(R recipeToCraft) {
		return getFuelHandler().getFuelAmount() >= getFuelCost(recipeToCraft);
	}

	public void refuel() {
		if (getFuelHandler().getFuelAmount() < getFuelHandler().getMaxFuelAmount()) {
			ItemStack stack = getStackInFuelSlot();
			if (getFuelHandler().isValidFuel(stack)) {
				ItemStack remainder = getFuelHandler().addFuel(stack);
				if (remainder.getCount() != stack.getCount()) {
					setStackInFuelSlot(remainder);
					setChanged();
				}
			}
		}
	}

	protected void serverTick(final ServerLevel level) {
		ticks++;

		if (ticks % 8 == 0) {
			refuel();
		}

		//TODO: use previous craftingGoalRecipe before trying to find a recipe to craft
		R craftingGoal = resolveRecipeFromInput(level); //get the currently possible crafting goal
		S state = getStateData();
		boolean emitRedstoneSignal = false;
		if (craftingGoal == null) {
			state.cancelCrafting();
		} else {
			ItemStack itemToCraft = craftingGoal.getResultItem();
			if (itemToCraft.isEmpty()) {
				state.cancelCrafting();
			}
			else {
				if (doesRecipeResultFitIntoOutputInv(craftingGoal, itemToCraft)) {
					if (state.getCraftingState() == CraftingState.NONE) { // nothing is being crafted, try to start crafting
						if (hasEnoughFuel(craftingGoal)) { //make sure there is enough fuel to craft the recipe
							state.setCraftingState(CraftingState.IN_PROGRESS);
							state.clear(); //safe guard, shouldn't be needed
							state.setCraftingGoalRecipe(craftingGoal); // this also sets the time required for crafting
						}
					} else if (!state.isCraftingCanceled()) { // something is being crafted, check that the crafting goals match
						R prevCraftingGoal = state.getCraftingGoalRecipe(level).orElse(null);
						if (prevCraftingGoal == null || !craftingGoal.isRecipeEqual(prevCraftingGoal)) {
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

			//change crafting progress
			if (state.getCraftingState() == CraftingState.IN_PROGRESS) {
				if (hasEnoughFuel(craftingGoal)) state.timeElapsed += 1;
				else state.timeElapsed -= 2;

				if (state.timeElapsed < 0) state.timeElapsed = 0;
			}

			//craft the recipe output
			if (state.getCraftingState() == CraftingState.IN_PROGRESS || state.getCraftingState() == CraftingState.COMPLETED) {
				if (state.timeElapsed >= state.timeForCompletion) {
					state.setCraftingState(CraftingState.COMPLETED);
					if (craftRecipe(craftingGoal, level)) {
						getFuelHandler().addFuelAmount(-getFuelCost(craftingGoal));
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
		updateBlockState(level, state, emitRedstoneSignal);
	}

	protected BooleanProperty getIsCraftingBlockStateProperty() {
		return ModBlocks.CRAFTING_PROPERTY;
	}

	protected void updateBlockState(Level world, S tileState, boolean redstoneSignal) {
		BlockState oldBlockState = world.getBlockState(worldPosition);
		BlockState newBlockState = oldBlockState.setValue(getIsCraftingBlockStateProperty(), tileState.getCraftingState() == CraftingState.IN_PROGRESS);
		if (!newBlockState.equals(oldBlockState)) {
			if (redstoneSignal) {
				if (newBlockState.getBlock() instanceof MachineBlock machine) {
					machine.powerBlock(world, worldPosition, newBlockState);
				}
			}
			else {
				world.setBlock(worldPosition, newBlockState, Block.UPDATE_CLIENTS);
			}
			setChanged();
		}
		else if (redstoneSignal && newBlockState.getBlock() instanceof MachineBlock machine) {
			machine.powerBlock(world, worldPosition, oldBlockState);
		}
	}

}
