package com.github.elenterius.biomancy.util.fuel;

import com.github.elenterius.biomancy.api.nutrients.FluidNutrients;
import com.github.elenterius.biomancy.api.nutrients.FluidToFuelConversion;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;

public class FluidFuelConsumerHandler implements IFluidHandler {

	private final FuelHandler fuelHandler;

	public FluidFuelConsumerHandler(FuelHandler fuelHandler) {
		this.fuelHandler = fuelHandler;
	}

	@Override
	public boolean isFluidValid(int tank, FluidStack resource) {
		return FluidNutrients.isValidFuel(resource);
	}

	@Override
	public int fill(FluidStack resource, FluidAction action) {
		if (resource.isEmpty()) return 0;

		FluidToFuelConversion fuelConversion = FluidNutrients.getFuelConversion(resource);
		if (fuelConversion == null) return 0;

		int valuePerUnit = fuelConversion.getFuelValuePerUnit(resource);
		if (valuePerUnit <= 0) return 0;

		int fuelAmount = resource.getAmount() * valuePerUnit;
		int fuelToFill = Math.min(fuelHandler.getMaxFuelAmount() - fuelHandler.getFuelAmount(), fuelAmount);
		int fluidFilled = fuelToFill / valuePerUnit; //make sure we only take as much fluid actually "fits inside"

		if (fluidFilled <= 0) return 0;
		if (action.simulate()) return fluidFilled;

		int fuelFilled = fluidFilled * valuePerUnit;
		fuelHandler.addFuelAmount(fuelFilled);

		return fluidFilled;
	}

	@Override
	public FluidStack drain(FluidStack resource, FluidAction action) {
		return FluidStack.EMPTY; //we only consume fuel
	}

	@Override
	public FluidStack drain(int maxDrain, FluidAction action) {
		return FluidStack.EMPTY; //we only consume fuel
	}

	@Override
	public int getTanks() {
		return 1;
	}

	@Override
	public int getTankCapacity(int tank) {
		return fuelHandler.getMaxFuelAmount(); //misleading value as it does not represent the fluid volume but the amount of fuel stored in the machine
		//could be Integer.MAX_VALUE instead
	}

	@Override
	public FluidStack getFluidInTank(int tank) {
		return FluidStack.EMPTY;
	}

}
