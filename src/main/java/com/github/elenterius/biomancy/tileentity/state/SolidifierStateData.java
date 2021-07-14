package com.github.elenterius.biomancy.tileentity.state;

import com.github.elenterius.biomancy.recipe.SolidifierRecipe;
import com.github.elenterius.biomancy.tileentity.SolidifierTileEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.ForgeRegistry;

public class SolidifierStateData extends RecipeCraftingStateData<SolidifierRecipe> {

	public static final String NBT_KEY_FLUID = "FluidInput";
	public static final int FLUID_ID_INDEX = 2; //index order matters, fluid id has to be set first, before the fluid amount
	public static final int FLUID_AMOUNT_INDEX = 3;

	public FluidTank inputTank = new FluidTank(SolidifierTileEntity.MAX_FLUID);
	private final LazyOptional<IFluidHandler> optionalInputFluidHandler = LazyOptional.of(() -> inputTank);

	public LazyOptional<IFluidHandler> getOptionalInputFluidHandler() {
		return optionalInputFluidHandler;
	}

	@Override
	Class<SolidifierRecipe> getRecipeType() {
		return SolidifierRecipe.class;
	}

	@Override
	public void serializeNBT(CompoundNBT nbt) {
		super.serializeNBT(nbt);
		if (!inputTank.isEmpty()) nbt.put(NBT_KEY_FLUID, inputTank.writeToNBT(new CompoundNBT()));
	}

	@Override
	public void deserializeNBT(CompoundNBT nbt) {
		super.deserializeNBT(nbt);
		inputTank.readFromNBT(nbt.getCompound(NBT_KEY_FLUID));
	}

	@Override
	public int get(int index) {
		validateIndex(index);
		if (index == TIME_INDEX) return timeElapsed;
		else if (index == TIME_FOR_COMPLETION_INDEX) return timeForCompletion;
		else if (index == FLUID_ID_INDEX) {
			ForgeRegistry<Fluid> reg = (ForgeRegistry<Fluid>) ForgeRegistries.FLUIDS;
			return reg.getID(inputTank.getFluid().getFluid());
		}
		else if (index == FLUID_AMOUNT_INDEX) return inputTank.getFluidAmount();
		return 0;
	}

	@Override
	public void set(int index, int value) {
		validateIndex(index);
		if (index == TIME_INDEX) timeElapsed = value;
		else if (index == TIME_FOR_COMPLETION_INDEX) timeForCompletion = value;
		else if (index == FLUID_ID_INDEX) {
			ForgeRegistry<Fluid> reg = (ForgeRegistry<Fluid>) ForgeRegistries.FLUIDS;
			Fluid fluid = reg.getValue(value);
			inputTank.setFluid(new FluidStack(fluid, inputTank.getFluidAmount()));
		}
		else if (index == FLUID_AMOUNT_INDEX) {
			if (inputTank.getFluid().getRawFluid() != Fluids.EMPTY) {
				inputTank.getFluid().setAmount(value);
			}
		}
	}

	@Override
	public int size() {
		return 4;
	}

}
