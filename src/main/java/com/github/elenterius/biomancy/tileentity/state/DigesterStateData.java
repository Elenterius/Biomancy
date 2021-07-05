package com.github.elenterius.biomancy.tileentity.state;

import com.github.elenterius.biomancy.recipe.DigesterRecipe;
import com.github.elenterius.biomancy.tileentity.DigesterTileEntity;
import net.minecraft.fluid.Fluids;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;

public class DigesterStateData extends RecipeCraftingStateData<DigesterRecipe> {

	public static final String NBT_KEY_FUEL = "Fuel";
	public static final int FUEL_INDEX = 2;
	public static final String NBT_KEY_OUTPUT = "FluidOutput";
	public static final int FLUID_OUTPUT_INDEX = 3;

	public FluidTank waterTank = new FluidTank(DigesterTileEntity.MAX_FUEL, fluidStack -> fluidStack.getFluid() == Fluids.WATER);
	private final LazyOptional<IFluidHandler> optionalWaterFluidHandler = LazyOptional.of(() -> waterTank);

	public FluidTank outputTank = new FluidTank(DigesterTileEntity.MAX_FUEL);
	private final LazyOptional<IFluidHandler> optionalOutputFluidHandler = LazyOptional.of(() -> outputTank);

	public LazyOptional<IFluidHandler> getOptionalInputFluidHandler() {
		return optionalWaterFluidHandler;
	}

	public LazyOptional<IFluidHandler> getOptionalOutputFluidHandler() {
		return optionalOutputFluidHandler;
	}

	@Override
	Class<DigesterRecipe> getRecipeType() {
		return DigesterRecipe.class;
	}

	@Override
	public void serializeNBT(CompoundNBT nbt) {
		super.serializeNBT(nbt);
		nbt.put(NBT_KEY_FUEL, waterTank.writeToNBT(new CompoundNBT()));
		nbt.put(NBT_KEY_OUTPUT, outputTank.writeToNBT(new CompoundNBT()));
	}

	@Override
	public void deserializeNBT(CompoundNBT nbt) {
		super.deserializeNBT(nbt);
		waterTank.readFromNBT(nbt.getCompound(NBT_KEY_FUEL));
		outputTank.readFromNBT(nbt.getCompound(NBT_KEY_OUTPUT));
	}

	@Override
	public int get(int index) {
		validateIndex(index);
		if (index == TIME_INDEX) return timeElapsed;
		else if (index == TIME_FOR_COMPLETION_INDEX) return timeForCompletion;
		else if (index == FUEL_INDEX) return waterTank.getFluidAmount();
		else if (index == FLUID_OUTPUT_INDEX) return outputTank.getFluidAmount();
		return 0;
	}

	@Override
	public void set(int index, int value) {
		validateIndex(index);
		if (index == TIME_INDEX) timeElapsed = value;
		else if (index == TIME_FOR_COMPLETION_INDEX) timeForCompletion = value;
		else if (index == FUEL_INDEX) {
			if (waterTank.isEmpty()) {
				waterTank.setFluid(new FluidStack(Fluids.WATER, value));
			}
			else {
				waterTank.getFluid().setAmount(value);
			}
		}
		else if (index == FLUID_OUTPUT_INDEX) {
			if (!outputTank.isEmpty()) {
				outputTank.getFluid().setAmount(value);
			}
		}
	}

	@Override
	public int size() {
		return 4;
	}

}
