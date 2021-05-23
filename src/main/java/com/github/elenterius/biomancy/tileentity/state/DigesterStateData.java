package com.github.elenterius.biomancy.tileentity.state;

import com.github.elenterius.biomancy.recipe.DigesterRecipe;
import com.github.elenterius.biomancy.tileentity.DigesterTileEntity;
import net.minecraft.fluid.Fluids;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.IIntArray;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;

public class DigesterStateData extends RecipeCraftingStateData<DigesterRecipe> implements IIntArray {

	public static final String NBT_KEY_TIME_ELAPSED = "TimeElapsed";
	public static final String NBT_KEY_TIME_FOR_COMPLETION = "TimeForCompletion";
	public static final String NBT_KEY_FUEL = "Fuel";

	public static final int TIME_INDEX = 0;
	public static final int TIME_FOR_COMPLETION_INDEX = 1;
	public static final int FUEL_INDEX = 2;

	public int timeElapsed;
	public int timeForCompletion;

	public FluidTank fuel = new FluidTank(DigesterTileEntity.MAX_FUEL, fluidStack -> fluidStack.getFluid() == Fluids.WATER);
	private final LazyOptional<IFluidHandler> optionalFluidHandler = LazyOptional.of(() -> fuel);

	public LazyOptional<IFluidHandler> getOptionalFluidHandler() {
		return optionalFluidHandler;
	}

	@Override
	Class<DigesterRecipe> getRecipeType() {
		return DigesterRecipe.class;
	}

	@Override
	public void setCraftingGoalRecipe(DigesterRecipe recipe) {
		super.setCraftingGoalRecipe(recipe);
		timeForCompletion = recipe.getCraftingTime();
	}

	@Override
	public void clear() {
		timeElapsed = 0;
		timeForCompletion = 0;
		super.clear();
	}

	@Override
	public void serializeNBT(CompoundNBT nbt) {
		super.serializeNBT(nbt);
		nbt.putInt(NBT_KEY_TIME_ELAPSED, timeElapsed);
		nbt.putInt(NBT_KEY_TIME_FOR_COMPLETION, timeForCompletion);
		nbt.put(NBT_KEY_FUEL, fuel.writeToNBT(new CompoundNBT()));
	}

	@Override
	public void deserializeNBT(CompoundNBT nbt) {
		super.deserializeNBT(nbt);
		timeElapsed = nbt.getInt(NBT_KEY_TIME_ELAPSED);
		timeForCompletion = nbt.getInt(NBT_KEY_TIME_FOR_COMPLETION);
		fuel.readFromNBT(nbt.getCompound(NBT_KEY_FUEL));
	}

	private void validateIndex(int index) {
		if (index < 0 || index >= size()) throw new IndexOutOfBoundsException("Index out of bounds:" + index);
	}

	@Override
	public int get(int index) {
		validateIndex(index);
		if (index == TIME_INDEX) return timeElapsed;
		else if (index == TIME_FOR_COMPLETION_INDEX) return timeForCompletion;
		else if (index == FUEL_INDEX) return fuel.getFluidAmount();
		return 0;
	}

	@Override
	public void set(int index, int value) {
		validateIndex(index);
		if (index == TIME_INDEX) timeElapsed = value;
		else if (index == TIME_FOR_COMPLETION_INDEX) timeForCompletion = value;
		else if (index == FUEL_INDEX) {
			if (fuel.isEmpty()) {
				fuel.setFluid(new FluidStack(Fluids.WATER, value));
			}
			else {
				fuel.getFluid().setAmount(value);
			}
		}
	}

	@Override
	public int size() {
		return 3;
	}

}
