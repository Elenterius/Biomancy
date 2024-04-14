package com.github.elenterius.biomancy.block.base;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.crafting.recipe.ProcessingRecipe;
import com.github.elenterius.biomancy.crafting.state.CraftingState;
import com.github.elenterius.biomancy.crafting.state.RecipeCraftingStateData;
import com.github.elenterius.biomancy.init.ModBlockProperties;
import com.github.elenterius.biomancy.util.fuel.IFuelHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Container;
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

public abstract class MachineBlockEntity<R extends ProcessingRecipe<C>, C extends Container, S extends RecipeCraftingStateData<R, C>> extends BlockEntity implements Nameable {

	protected final int tickOffset = BiomancyMod.GLOBAL_RANDOM.nextInt(20);
	protected int ticks = tickOffset;

	protected MachineBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
	}

	public static <R extends ProcessingRecipe<C>, C extends Container, S extends RecipeCraftingStateData<R, C>> void serverTick(Level level, BlockPos pos, BlockState state, MachineBlockEntity<R, C, S> entity) {
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

	protected abstract C getInputInventory();

	protected abstract IFuelHandler getFuelHandler();

	public int getFuelCost(R recipeToCraft) {
		return getFuelHandler().getFuelCost(recipeToCraft.getCraftingCostNutrients(getInputInventory()));
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

		S state = getStateData();
		R craftingGoal = state
				.getCraftingGoalRecipe(level) //try to use the current/previous crafting goal first
				.filter(r -> doesRecipeMatchInput(r, level)) //check if it's still matches with the ingredients in the input, if yes use it
				.orElseGet(() -> resolveRecipeFromInput(level)); //else try to find new crafting goal

		boolean emitRedstoneSignal = false;
		if (craftingGoal == null) {
			state.cancelCrafting();
		} else {
			ItemStack itemToCraft = getItemToCraft(level, craftingGoal);
			if (itemToCraft.isEmpty()) {
				state.cancelCrafting();
			} else {
				if (doesRecipeResultFitIntoOutputInv(craftingGoal, itemToCraft)) {
					if (state.getCraftingState() == CraftingState.NONE) { // nothing is being crafted, try to start crafting
						if (hasEnoughFuel(craftingGoal)) { //make sure there is enough fuel to craft the recipe
							state.setCraftingState(CraftingState.IN_PROGRESS);
							state.clear(); //safeguard, shouldn't be needed
							state.setCraftingGoalRecipe(craftingGoal, getInputInventory()); // this also sets the time required for crafting
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

	private ItemStack getItemToCraft(ServerLevel level, R craftingGoal) {
		if (craftingGoal.isSpecial()) {
			return craftingGoal.assemble(getInputInventory(), level.registryAccess());
		}
		return craftingGoal.getResultItem(level.registryAccess());
	}

	protected BooleanProperty getIsCraftingBlockStateProperty() {
		return ModBlockProperties.IS_CRAFTING;
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
