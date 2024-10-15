package com.github.elenterius.biomancy.api.nutrients.fluid;

import com.github.elenterius.biomancy.api.nutrients.FuelHandler;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Experimental
public class FluidFuelConsumerHandler implements IFluidHandler, INBTSerializable<CompoundTag> {

	public static final String FRACTIONAL_FUEL_BUFFER_KEY = "FractionalFuelBuffer";
	private static final long SCALE_FACTOR = 1_000_000;  //

	private final FuelHandler fuelHandler;
	private long fractionalFuelBuffer;

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
		if (fuelHandler.getFuelAmount() >= fuelHandler.getMaxFuelAmount()) return 0;

		FluidToFuelConversion fuelConversion = FluidNutrients.getFuelConversion(resource);
		if (fuelConversion == null) return 0;

		int fuelMultiplier = fuelConversion.getFuelMultiplier(resource);
		int fluidToFuelRatio = fuelConversion.getFluidToFuelRatio(resource);
		if (fuelMultiplier <= 0 || fluidToFuelRatio <= 0) return 0;

		int fuelYield = (resource.getAmount() / fluidToFuelRatio) * fuelMultiplier;
		if (fuelYield <= 0) {
			if (action.simulate()) return resource.getAmount();

			fractionalFuelBuffer += (resource.getAmount() * SCALE_FACTOR / fluidToFuelRatio) * fuelMultiplier;
			fuelYield = (int) (fractionalFuelBuffer / SCALE_FACTOR);
			if (fuelYield <= 0) return resource.getAmount();

			int fuelFilled = Math.min(fuelHandler.getMaxFuelAmount() - fuelHandler.getFuelAmount(), fuelYield);
			fuelHandler.addFuelAmount(fuelFilled);
			fractionalFuelBuffer -= fuelFilled * SCALE_FACTOR;

			return resource.getAmount();
		}
		else {
			int fuelToFill = Math.min(fuelHandler.getMaxFuelAmount() - fuelHandler.getFuelAmount(), fuelYield);

			// calculate how much fluid this fuel corresponds to, based on the ratio
			int fluidFilled = (fuelToFill * fluidToFuelRatio) / fuelMultiplier; //make sure we only take as much fluid actually "fits inside"

			if (fluidFilled <= 0) return 0;
			if (action.simulate()) return fluidFilled;

			int fuelFilled = (fluidFilled / fluidToFuelRatio) * fuelMultiplier;
			fuelHandler.addFuelAmount(fuelFilled);

			return fluidFilled;
		}
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

	@Override
	public CompoundTag serializeNBT() {
		CompoundTag tag = new CompoundTag();
		tag.putLong(FRACTIONAL_FUEL_BUFFER_KEY, fractionalFuelBuffer);
		return tag;
	}

	@Override
	public void deserializeNBT(CompoundTag tag) {
		fractionalFuelBuffer = tag.getLong(FRACTIONAL_FUEL_BUFFER_KEY);
	}

}
