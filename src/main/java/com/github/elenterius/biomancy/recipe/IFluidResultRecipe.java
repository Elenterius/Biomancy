package com.github.elenterius.biomancy.recipe;

import net.minecraftforge.fluids.FluidStack;

public interface IFluidResultRecipe {

	/**
	 * @return copy of internal FluidStack, safe for modification
	 */
	FluidStack getFluidResult();

	/**
	 * @return internal FluidStack, do not modify
	 */
	FluidStack getFluidOutput();

}
