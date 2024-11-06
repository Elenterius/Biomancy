package com.github.elenterius.biomancy.api.tribute.fluid;

import com.github.elenterius.biomancy.api.tribute.SacrificeHandler;
import com.github.elenterius.biomancy.inventory.Notify;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;

public class FluidTributeConsumerHandler implements IFluidHandler, INBTSerializable<CompoundTag> {

	public static final String MILLI_TRIBUTE_BUFFER_KEY = "MilliTributeBuffer";

	private final SacrificeHandler sacrificeHandler;
	private final Notify onChange;
	private final long[] milliTributeBuffer = new long[6];

	public FluidTributeConsumerHandler(SacrificeHandler sacrificeHandler, Notify onChange) {
		this.sacrificeHandler = sacrificeHandler;
		this.onChange = onChange;
	}

	@Override
	public boolean isFluidValid(int tank, FluidStack resource) {
		return !sacrificeHandler.isFull() && FluidTributes.isValid(resource);
	}

	@Override
	public int fill(FluidStack resource, FluidAction action) {
		if (resource.isEmpty()) return 0;
		if (sacrificeHandler.isFull()) return 0;

		FluidToTributeConversion conversion = FluidTributes.getConversion(resource);
		if (conversion == null) return 0;

		FluidTribute fluidTribute = conversion.getTributePerUnit(resource);
		if (fluidTribute.isEmpty()) return 0;
		if (fluidTribute.biomass() > 0 && sacrificeHandler.isBiomassFull()) return 0;

		long amountToConsume = resource.getAmount();

		int biomassYield = (int) ((milliTributeBuffer[0] + fluidTribute.biomass() * amountToConsume) / FluidTribute.UNIT_SCALE);
		int biomassToFill = Math.min(sacrificeHandler.getMaxBiomass() - sacrificeHandler.getBiomassAmount(), biomassYield);

		if (biomassToFill > 0) {
			long biomassMissing = biomassToFill * FluidTribute.UNIT_SCALE - milliTributeBuffer[0];
			amountToConsume = Mth.ceil((float) biomassMissing / fluidTribute.biomass());

			if (action.simulate()) return (int) amountToConsume;

			sacrificeHandler.addBiomass(biomassToFill);

			long remainder = (fluidTribute.biomass() * amountToConsume - biomassMissing);
			milliTributeBuffer[0] = remainder;
		}

		if (action.simulate()) return (int) amountToConsume;

		int yield;

		milliTributeBuffer[1] += fluidTribute.lifeEnergy() * amountToConsume;
		yield = (int) (milliTributeBuffer[1] / FluidTribute.UNIT_SCALE);
		sacrificeHandler.addLifeEnergy(yield);
		milliTributeBuffer[1] -= yield * FluidTribute.UNIT_SCALE;

		milliTributeBuffer[2] += fluidTribute.successModifier() * amountToConsume;
		yield = (int) (milliTributeBuffer[2] / FluidTribute.UNIT_SCALE);
		sacrificeHandler.addSuccess(yield);
		milliTributeBuffer[2] -= yield * FluidTribute.UNIT_SCALE;

		milliTributeBuffer[3] += fluidTribute.hostileModifier() * amountToConsume;
		yield = (int) (milliTributeBuffer[3] / FluidTribute.UNIT_SCALE);
		sacrificeHandler.addHostile(yield);
		milliTributeBuffer[3] -= yield * FluidTribute.UNIT_SCALE;

		milliTributeBuffer[4] += fluidTribute.diseaseModifier() * amountToConsume;
		yield = (int) (milliTributeBuffer[4] / FluidTribute.UNIT_SCALE);
		sacrificeHandler.addDisease(yield);
		milliTributeBuffer[4] -= yield * FluidTribute.UNIT_SCALE;

		milliTributeBuffer[5] += fluidTribute.anomalyModifier() * amountToConsume;
		yield = (int) (milliTributeBuffer[5] / FluidTribute.UNIT_SCALE);
		sacrificeHandler.addAnomaly(yield);
		milliTributeBuffer[5] -= yield * FluidTribute.UNIT_SCALE;

		if (sacrificeHandler.isDirty()) {
			onChange.invoke();
			sacrificeHandler.setDirty(false);
		}

		return (int) amountToConsume;
	}

	@Override
	public int getTanks() {
		return 1;
	}

	@Override
	public FluidStack getFluidInTank(int tank) {
		return FluidStack.EMPTY;
	}

	@Override
	public int getTankCapacity(int tank) {
		return 250; //bottle amount
	}

	@Override
	public FluidStack drain(FluidStack resource, FluidAction action) {
		return FluidStack.EMPTY;
	}

	@Override
	public FluidStack drain(int maxDrain, FluidAction action) {
		return FluidStack.EMPTY;
	}

	@Override
	public CompoundTag serializeNBT() {
		CompoundTag tag = new CompoundTag();
		tag.putLongArray(MILLI_TRIBUTE_BUFFER_KEY, milliTributeBuffer);
		return tag;
	}

	@Override
	public void deserializeNBT(CompoundTag tag) {
		long[] buffer = tag.getLongArray(MILLI_TRIBUTE_BUFFER_KEY);
		System.arraycopy(buffer, 0, milliTributeBuffer, 0, milliTributeBuffer.length);
	}

}
