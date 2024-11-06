package com.github.elenterius.biomancy.api.nutrients.fluid;

import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Experimental
public interface FluidToFuelConversion {

	int MILLI_FUEL_SCALE = 1000;

	FluidToFuelConversion IDENTITY = resource -> MILLI_FUEL_SCALE;

	/**
	 * @return Fuel (in milli units) per 1 milli-bucket of fluid
	 */
	int getMilliFuelPerUnit(FluidStack resource);

}
