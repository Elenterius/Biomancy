package com.github.elenterius.biomancy.api.tribute.fluid;

import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Experimental
public interface FluidToTributeConversion {

	/**
	 * @return Tribute (in milli units) per 1 milli-bucket of fluid
	 */
	FluidTribute getTributePerUnit(FluidStack resource);

}
