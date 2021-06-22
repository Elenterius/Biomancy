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
	public void serializeNBT(CompoundNBT nbt) {
		super.serializeNBT(nbt);
		nbt.put(NBT_KEY_FUEL, fuel.writeToNBT(new CompoundNBT()));
	}

	@Override
	public void deserializeNBT(CompoundNBT nbt) {
		super.deserializeNBT(nbt);
		fuel.readFromNBT(nbt.getCompound(NBT_KEY_FUEL));
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
