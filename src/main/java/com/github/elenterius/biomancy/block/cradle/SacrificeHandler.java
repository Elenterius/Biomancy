package com.github.elenterius.biomancy.block.cradle;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.util.INBTSerializable;

import java.util.function.Consumer;

public class SacrificeHandler implements INBTSerializable<CompoundTag> {

	private static final int MAX_VALUE = 100;
	private byte biomass;
	private byte lifeEnergy;
	private int successValue;
	private int diseaseValue;
	private int hostileValue;

	public boolean isFull() {
		return lifeEnergy >= MAX_VALUE && biomass >= MAX_VALUE;
	}

	public void setBiomass(int amount) {
		biomass = (byte) Mth.clamp(amount, 0, MAX_VALUE);
	}

	public boolean addBiomass(int amount) {
		if (amount == 0) return false;

		if (amount < 0 && biomass > 0) {
			setBiomass(biomass + amount);
			return true;
		}

		if (amount > 0 && biomass < MAX_VALUE) {
			setBiomass(biomass + amount);
			return true;
		}

		return false;
	}

	public int getBiomassAmount() {
		return biomass;
	}

	public float getBiomassPct() {
		return biomass / (float) MAX_VALUE;
	}

	public void setLifeEnergy(int amount) {
		lifeEnergy = (byte) Mth.clamp(amount, 0, MAX_VALUE);
	}

	public boolean addLifeEnergy(int amount) {
		if (amount == 0) return false;

		if (amount < 0 && lifeEnergy > 0) {
			setLifeEnergy(lifeEnergy + amount);
			return true;
		}

		if (amount > 0 && lifeEnergy < MAX_VALUE) {
			setLifeEnergy(lifeEnergy + amount);
			return true;
		}

		return false;
	}

	public int getLifeEnergyAmount() {
		return lifeEnergy;
	}

	public float getLifeEnergyPct() {
		return lifeEnergy / (float) MAX_VALUE;
	}

	public float getSuccessChance() {
		return successValue / 100f;
	}

	public float getHostileChance() {
		return hostileValue / 100f;
	}

	public float getTumorFactor() {
		return diseaseValue / 100f;
	}

	public boolean hasModifiers() {
		return diseaseValue != 0 || hostileValue != 0;
	}

	public boolean addItem(ItemStack stack, Consumer<ITribute> onSuccess) {
		if (isFull()) return false;
		if (stack.isEmpty()) return false;

		ITribute tribute = Tributes.from(stack);
		int count = addTribute(tribute, stack.getCount());
		if (count > 0) {
			stack.shrink(count);
			onSuccess.accept(tribute);
			return true;
		}

		return false;
	}

	public boolean addItem(ItemStack stack) {
		if (isFull()) return false;
		if (stack.isEmpty()) return false;

		int count = addTribute(Tributes.from(stack), stack.getCount());
		if (count > 0) {
			stack.shrink(count);
			return true;
		}

		return false;
	}

	private int addTribute(ITribute tribute, int maxCount) {
		int n = maxCount;
		while (n > 0 && addTribute(tribute)) n--;
		return maxCount - n;
	}

	public boolean addTribute(ITribute tribute) {
		if (isFull()) return false;
		if (tribute.isEmpty()) return false;

		boolean consumeTribute = addBiomass(tribute.biomass()) || addLifeEnergy(tribute.lifeEnergy());
		boolean isModifier = tribute.biomass() == 0 && tribute.lifeEnergy() == 0;

		if (consumeTribute || isModifier) {
			diseaseValue += tribute.diseaseModifier();
			hostileValue += tribute.hostileModifier();
			successValue += tribute.successModifier();
			return true;
		}

		return false;
	}

	public void reset() {
		biomass = 0;
		lifeEnergy = 0;

		diseaseValue = 0;
		hostileValue = 0;
		successValue = 0;
	}

	@Override
	public CompoundTag serializeNBT() {
		CompoundTag tag = new CompoundTag();
		tag.putByte("Biomass", biomass);
		tag.putByte("LifeEnergy", lifeEnergy);

		tag.putInt("Disease", diseaseValue);
		tag.putInt("Hostile", hostileValue);
		tag.putInt("Success", successValue);
		return tag;
	}

	@Override
	public void deserializeNBT(CompoundTag tag) {
		biomass = tag.getByte("Biomass");
		lifeEnergy = tag.getByte("LifeEnergy");

		diseaseValue = tag.getInt("Disease");
		hostileValue = tag.getInt("Hostile");
		successValue = tag.getInt("Success");
	}

}
