package com.github.elenterius.biomancy.api.nutrients.fluid;

import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Experimental
public interface FluidToFuelConversion {
	FluidToFuelConversion IDENTITY = new FluidToFuelConversion() {
		@Override
		public int getFuelMultiplier(FluidStack resource) {
			return 1;
		}

		@Override
		public int getFluidToFuelRatio(FluidStack resource) {
			return 1;
		}
	};

	/**
	 * Multiplier for increasing the yield of fuel <br>
	 * fuel_yield = raw_fuel * multiplier
	 * @return value > 0
	 */
	int getFuelMultiplier(FluidStack resource);

	/**
	 * Fluid-To-Fuel Ration <br>
	 * If fluidToFuelRatio = 10, then for every 10 units of fluid, 1 raw_fuel will be generated
	 *
	 * @return value > 0
	 */
	int getFluidToFuelRatio(FluidStack resource);

}
