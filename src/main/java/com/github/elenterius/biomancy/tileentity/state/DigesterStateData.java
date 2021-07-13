package com.github.elenterius.biomancy.tileentity.state;

import com.github.elenterius.biomancy.fluid.simibubi.CombinedTankWrapper;
import com.github.elenterius.biomancy.inventory.fluidhandler.FluidHandlerDelegator;
import com.github.elenterius.biomancy.recipe.DigesterRecipe;
import com.github.elenterius.biomancy.tileentity.DigesterTileEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.ForgeRegistry;

public class DigesterStateData extends RecipeCraftingStateData<DigesterRecipe> {

	public static final String NBT_KEY_FUEL = "Fuel";
	public static final int FUEL_INDEX = 2;

	public static final String NBT_KEY_FLUID_OUT = "FluidOutput";
	//for the IIntArray sync to client to properly work the index for fluid id has to be smaller than fluid amount index
	// --> the fluid has to be set before the amount
	//TODO: reevaluate this and consider if we instead send a dedicated network package to client containing the fluid registry key
	public static final int FLUID_AMOUNT_INDEX = 4;
	public static final int FLUID_ID_INDEX = 3;

	public FluidTank fuelTank = new FluidTank(DigesterTileEntity.MAX_FLUID, fluidStack -> fluidStack.getFluid() == Fluids.WATER);
	public FluidTank outputTank = new FluidTank(DigesterTileEntity.MAX_FLUID);
	private final LazyOptional<IFluidHandler> optionalCombinedFluidHandlers = LazyOptional.of(() -> new CombinedTankWrapper(fuelTank, new FluidHandlerDelegator.DenyInput<>(outputTank)));

	public LazyOptional<IFluidHandler> getCombinedFluidHandlers() {
		return optionalCombinedFluidHandlers;
	}

	@Override
	Class<DigesterRecipe> getRecipeType() {
		return DigesterRecipe.class;
	}

	@Override
	public void serializeNBT(CompoundNBT nbt) {
		super.serializeNBT(nbt);
		if (!fuelTank.isEmpty()) nbt.put(NBT_KEY_FUEL, fuelTank.writeToNBT(new CompoundNBT()));
		if (!outputTank.isEmpty()) nbt.put(NBT_KEY_FLUID_OUT, outputTank.writeToNBT(new CompoundNBT()));
	}

	@Override
	public void deserializeNBT(CompoundNBT nbt) {
		super.deserializeNBT(nbt);
		fuelTank.readFromNBT(nbt.getCompound(NBT_KEY_FUEL));
		outputTank.readFromNBT(nbt.getCompound(NBT_KEY_FLUID_OUT));
	}

	@Override
	public int get(int index) {
		validateIndex(index);
		if (index == TIME_INDEX) return timeElapsed;
		else if (index == TIME_FOR_COMPLETION_INDEX) return timeForCompletion;
		else if (index == FUEL_INDEX) return fuelTank.getFluidAmount();
		else if (index == FLUID_AMOUNT_INDEX) return outputTank.getFluidAmount();
		else if (index == FLUID_ID_INDEX) {
			ForgeRegistry<Fluid> reg = (ForgeRegistry<Fluid>) ForgeRegistries.FLUIDS;
			return reg.getID(outputTank.getFluid().getFluid());
		}
		return 0;
	}

	@Override
	public void set(int index, int value) {
		validateIndex(index);
		if (index == TIME_INDEX) timeElapsed = value;
		else if (index == TIME_FOR_COMPLETION_INDEX) timeForCompletion = value;
		else if (index == FUEL_INDEX) {
			if (fuelTank.isEmpty()) {
				fuelTank.setFluid(new FluidStack(Fluids.WATER, value));
			}
			else {
				fuelTank.getFluid().setAmount(value);
			}
		}
		else if (index == FLUID_ID_INDEX) { //index order matters, fluid id has to be set first, before the fluid amount
			ForgeRegistry<Fluid> reg = (ForgeRegistry<Fluid>) ForgeRegistries.FLUIDS;
			Fluid fluid = reg.getValue(value);
			outputTank.setFluid(new FluidStack(fluid, outputTank.getFluidAmount()));
		}
		else if (index == FLUID_AMOUNT_INDEX) {
			if (outputTank.getFluid().getRawFluid() != Fluids.EMPTY) {
				outputTank.getFluid().setAmount(value);
			}
		}
	}

	@Override
	public int size() {
		return 5;
	}

}
