package com.github.elenterius.biomancy.api.nutrients;

import net.minecraftforge.fluids.FluidStack;

@FunctionalInterface
public interface FluidToFuelConversion {
	FluidToFuelConversion IDENTITY = fluidStack -> 1;

	/**
	 * @return the fuel value for 1 millibucket (mb) of fluid
	 */
	int getFuelValuePerUnit(FluidStack fluidStack);
}
