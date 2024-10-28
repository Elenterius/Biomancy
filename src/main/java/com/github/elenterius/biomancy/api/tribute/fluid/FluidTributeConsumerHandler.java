package com.github.elenterius.biomancy.api.tribute.fluid;

import com.github.elenterius.biomancy.api.tribute.MilliTribute;
import com.github.elenterius.biomancy.api.tribute.SacrificeHandler;
import com.github.elenterius.biomancy.inventory.Notify;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;

public class FluidTributeConsumerHandler implements IFluidHandler, INBTSerializable<CompoundTag> {

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

		MilliTribute milliTribute = conversion.getTributePerUnit(resource);
		if (milliTribute.isEmpty()) return 0;
		if (milliTribute.biomass() > 0 && sacrificeHandler.isBiomassFull()) return 0;

		long amountToConsume = resource.getAmount();

		int biomassYield = (int) ((milliTributeBuffer[0] + milliTribute.biomass() * amountToConsume) / MilliTribute.UNIT_SCALE);
		int biomassToFill = Math.min(sacrificeHandler.getMaxBiomass() - sacrificeHandler.getBiomassAmount(), biomassYield);

		if (biomassToFill > 0) {
			long biomassMissing = biomassToFill * MilliTribute.UNIT_SCALE - milliTributeBuffer[0];
			amountToConsume = Mth.ceil((float) biomassMissing / milliTribute.biomass());

			if (action.simulate()) return (int) amountToConsume;

			sacrificeHandler.addBiomass(biomassToFill);

			long remainder = (milliTribute.biomass() * amountToConsume - biomassMissing);
			milliTributeBuffer[0] = remainder;
		}

		if (action.simulate()) return (int) amountToConsume;

		int yield;

		milliTributeBuffer[1] += milliTribute.lifeEnergy() * amountToConsume;
		yield = (int) (milliTributeBuffer[1] / MilliTribute.UNIT_SCALE);
		sacrificeHandler.addLifeEnergy(yield);
		milliTributeBuffer[1] -= yield * MilliTribute.UNIT_SCALE;

		milliTributeBuffer[2] += milliTribute.successModifier() * amountToConsume;
		yield = (int) (milliTributeBuffer[2] / MilliTribute.UNIT_SCALE);
		sacrificeHandler.addSuccess(yield);
		milliTributeBuffer[2] -= yield * MilliTribute.UNIT_SCALE;

		milliTributeBuffer[3] += milliTribute.hostileModifier() * amountToConsume;
		yield = (int) (milliTributeBuffer[3] / MilliTribute.UNIT_SCALE);
		sacrificeHandler.addHostile(yield);
		milliTributeBuffer[3] -= yield * MilliTribute.UNIT_SCALE;

		milliTributeBuffer[4] += milliTribute.diseaseModifier() * amountToConsume;
		yield = (int) (milliTributeBuffer[4] / MilliTribute.UNIT_SCALE);
		sacrificeHandler.addDisease(yield);
		milliTributeBuffer[4] -= yield * MilliTribute.UNIT_SCALE;

		milliTributeBuffer[5] += milliTribute.anomalyModifier() * amountToConsume;
		yield = (int) (milliTributeBuffer[5] / MilliTribute.UNIT_SCALE);
		sacrificeHandler.addAnomaly(yield);
		milliTributeBuffer[5] -= yield * MilliTribute.UNIT_SCALE;

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
		tag.putLongArray("MilliTributeBuffer", milliTributeBuffer);
		return tag;
	}

	@Override
	public void deserializeNBT(CompoundTag tag) {
		long[] buffer = tag.getLongArray("MilliTributeBuffer");
		System.arraycopy(buffer, 0, milliTributeBuffer, 0, milliTributeBuffer.length);
	}

}
