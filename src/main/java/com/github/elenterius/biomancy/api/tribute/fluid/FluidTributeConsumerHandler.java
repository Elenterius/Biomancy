package com.github.elenterius.biomancy.api.tribute.fluid;

import com.github.elenterius.biomancy.api.tribute.MilliTribute;
import com.github.elenterius.biomancy.api.tribute.SacrificeHandler;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;

public class FluidTributeConsumerHandler implements IFluidHandler, INBTSerializable<CompoundTag> {

	private final SacrificeHandler sacrificeHandler;
	private final long[] milliTributeBuffer = new long[6];

	public FluidTributeConsumerHandler(SacrificeHandler sacrificeHandler) {
		this.sacrificeHandler = sacrificeHandler;
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

		MilliTribute tributePerUnit = conversion.getTributePerUnit(resource);
		if (tributePerUnit.isEmpty()) return 0;
		if (tributePerUnit.biomass() > 0 && sacrificeHandler.isBiomassFull()) return 0;

		long amountToConsume = resource.getAmount();

		int biomassYield = (int) ((milliTributeBuffer[0] + tributePerUnit.biomass() * amountToConsume) / MilliTribute.UNIT_SCALE);
		int biomassToFill = Math.min(sacrificeHandler.getMaxBiomass() - sacrificeHandler.getBiomassAmount(), biomassYield);

		if (biomassToFill > 0) {
			long biomassMissing = biomassToFill * MilliTribute.UNIT_SCALE - milliTributeBuffer[0];
			amountToConsume = Mth.ceil((float) biomassMissing / tributePerUnit.biomass());

			if (action.simulate()) return (int) amountToConsume;

			sacrificeHandler.addBiomass(biomassToFill);

			long remainder = (tributePerUnit.biomass() * amountToConsume - biomassMissing);
			milliTributeBuffer[0] = remainder;
		}

		if (action.simulate()) return (int) amountToConsume;

		int amount;

		milliTributeBuffer[1] += tributePerUnit.lifeEnergy() * amountToConsume;
		amount = (int) (milliTributeBuffer[1] / MilliTribute.UNIT_SCALE);
		sacrificeHandler.addLifeEnergy(amount);
		milliTributeBuffer[1] -= amount;

		milliTributeBuffer[2] += tributePerUnit.successModifier() * amountToConsume;
		amount = (int) (milliTributeBuffer[2] / MilliTribute.UNIT_SCALE);
		sacrificeHandler.addSuccess(amount);
		milliTributeBuffer[2] -= amount;

		milliTributeBuffer[3] += tributePerUnit.hostileModifier() * amountToConsume;
		amount = (int) (milliTributeBuffer[3] / MilliTribute.UNIT_SCALE);
		sacrificeHandler.addHostile(amount);
		milliTributeBuffer[3] -= amount;

		milliTributeBuffer[4] += tributePerUnit.diseaseModifier() * amountToConsume;
		amount = (int) (milliTributeBuffer[4] / MilliTribute.UNIT_SCALE);
		sacrificeHandler.addDisease(amount);
		milliTributeBuffer[4] -= amount;

		milliTributeBuffer[5] += tributePerUnit.anomalyModifier() * amountToConsume;
		amount = (int) (milliTributeBuffer[5] / MilliTribute.UNIT_SCALE);
		sacrificeHandler.addAnomaly(amount);
		milliTributeBuffer[5] -= amount;

		return (int) amountToConsume;
	}

	@Override
	public int getTanks() {
		return 6;
	}

	@Override
	public FluidStack getFluidInTank(int tank) {
		return FluidStack.EMPTY;
	}

	@Override
	public int getTankCapacity(int tank) {
		return switch (tank) {
			case 0 -> sacrificeHandler.getMaxBiomass();
			case 1, 2, 3, 4, 5 -> 250; //250 mb which is roughly equal to a bottle of fluid
			default -> 0;
		};
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
