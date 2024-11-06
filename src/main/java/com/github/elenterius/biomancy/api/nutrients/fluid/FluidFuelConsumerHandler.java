package com.github.elenterius.biomancy.api.nutrients.fluid;

import com.github.elenterius.biomancy.api.nutrients.FuelHandler;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Experimental
public class FluidFuelConsumerHandler implements IFluidHandler, INBTSerializable<CompoundTag> {

	public static final String MILLI_FUEL_BUFFER_KEY = "MilliFuelBuffer";

	private final FuelHandler fuelHandler;
	private long milliFuelBuffer;

	public FluidFuelConsumerHandler(FuelHandler fuelHandler) {
		this.fuelHandler = fuelHandler;
	}

	@Override
	public boolean isFluidValid(int tank, FluidStack resource) {
		return FluidNutrients.isValid(resource);
	}

	@Override
	public int fill(FluidStack resource, FluidAction action) {
		if (resource.isEmpty()) return 0;
		if (fuelHandler.getFuelAmount() >= fuelHandler.getMaxFuelAmount()) return 0;

		FluidToFuelConversion conversion = FluidNutrients.getConversion(resource);
		if (conversion == null) return 0;

		int milliFuel = conversion.getMilliFuelPerUnit(resource);
		if (milliFuel <= 0) return 0;

		long amountToConsume = resource.getAmount(); //8 of 120 --> 120/8 = 15
		int fuelYield = (int) ((milliFuelBuffer + milliFuel * amountToConsume) / FluidToFuelConversion.MILLI_FUEL_SCALE); //1600 -> 1,6

		if (fuelYield > 0) {
			int fuelToFill = Math.min(fuelHandler.getMaxFuelAmount() - fuelHandler.getFuelAmount(), fuelYield); //1
			long fuelMissing = (long) fuelToFill * FluidToFuelConversion.MILLI_FUEL_SCALE - milliFuelBuffer; //1000 - 800 = 200

			amountToConsume = (int) (fuelMissing / milliFuel); // 200/100 = 2

			if (action.simulate()) return (int) amountToConsume;

			fuelHandler.addFuelAmount(fuelToFill);

			long remainder = (milliFuel * amountToConsume - fuelMissing); // 100 * 2 - 200
			milliFuelBuffer = remainder;
		}
		else {
			if (action.simulate()) return (int) amountToConsume;
			milliFuelBuffer += milliFuel * amountToConsume;
		}

		return (int) amountToConsume;
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
	}

	@Override
	public FluidStack getFluidInTank(int tank) {
		return FluidStack.EMPTY;
	}

	@Override
	public CompoundTag serializeNBT() {
		CompoundTag tag = new CompoundTag();
		tag.putLong(MILLI_FUEL_BUFFER_KEY, milliFuelBuffer);
		return tag;
	}

	@Override
	public void deserializeNBT(CompoundTag tag) {
		milliFuelBuffer = tag.getLong(MILLI_FUEL_BUFFER_KEY);
	}

}
