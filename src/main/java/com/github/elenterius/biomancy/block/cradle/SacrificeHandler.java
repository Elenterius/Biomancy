package com.github.elenterius.biomancy.block.cradle;

import com.github.elenterius.biomancy.api.tribute.Tribute;
import com.github.elenterius.biomancy.api.tribute.Tributes;
import com.google.common.math.IntMath;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.util.INBTSerializable;

import java.util.function.Consumer;

public class SacrificeHandler implements INBTSerializable<CompoundTag> {

	private static final int MAX_BIOMASS_VALUE = 100;

	private byte biomass;
	private int lifeEnergy;
	private int successValue;
	private int diseaseValue;
	private int hostileValue;
	private int anomalyValue;
	private boolean hasModifiers;

	public SacrificeHandler() {
		reset();
	}

	public void reset() {
		biomass = 0;
		lifeEnergy = 0;

		successValue = 0;

		diseaseValue = 0;
		hostileValue = 85;
		anomalyValue = 7;

		hasModifiers = false;
	}

	public boolean isFull() {
		return lifeEnergy >= MAX_BIOMASS_VALUE && biomass >= MAX_BIOMASS_VALUE;
	}

	public void setBiomass(int amount) {
		biomass = (byte) Mth.clamp(amount, 0, MAX_BIOMASS_VALUE);
	}

	public boolean addBiomass(int amount) {
		if (amount == 0) return false;

		if (amount < 0 && biomass > 0) {
			setBiomass(biomass + amount);
			return true;
		}

		if (amount > 0 && biomass < MAX_BIOMASS_VALUE) {
			setBiomass(biomass + amount);
			return true;
		}

		return false;
	}

	public int getBiomassAmount() {
		return biomass;
	}

	public float getBiomassPct() {
		return biomass / 100f;
	}

	public void setLifeEnergy(int amount) {
		lifeEnergy = Mth.clamp(amount, 0, Integer.MAX_VALUE);
	}

	public boolean addLifeEnergy(int amount) {
		if (amount == 0) return false;

		if (amount < 0 && lifeEnergy > 0) {
			setLifeEnergy(lifeEnergy + amount);
			return true;
		}

		if (amount > 0) {
			setLifeEnergy(IntMath.saturatedAdd(lifeEnergy, amount));
			return true;
		}

		return false;
	}

	public int getLifeEnergyAmount() {
		return lifeEnergy;
	}

	public float getLifeEnergyPct() {
		return lifeEnergy / 100f;
	}

	public float getSuccessChance() {
		return successValue / 100f;
	}

	public float getHostileChance() {
		return hostileValue / 100f;
	}

	public float getAnomalyChance() {
		return anomalyValue / 100f;
	}

	public float getTumorFactor() {
		return diseaseValue / 100f;
	}

	public boolean hasModifiers() {
		return hasModifiers;
	}

	public boolean addItem(ItemStack stack, Consumer<Tribute> onSuccess) {
		if (isFull()) return false;
		if (stack.isEmpty()) return false;

		Tribute tribute = Tributes.getTribute(stack);
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

		int count = addTribute(Tributes.getTribute(stack), stack.getCount());
		if (count > 0) {
			stack.shrink(count);
			return true;
		}

		return false;
	}

	private int addTribute(Tribute tribute, int maxCount) {
		int n = maxCount;
		while (n > 0 && addTribute(tribute)) n--;
		return maxCount - n;
	}

	public boolean addTribute(Tribute tribute) {
		if (isFull()) return false;
		if (tribute.isEmpty()) return false;

		boolean addedBiomass = addBiomass(tribute.biomass());
		boolean addedLifeEnergy = addLifeEnergy(tribute.lifeEnergy());

		boolean consumeTribute = addedBiomass || addedLifeEnergy;
		boolean isModifier = tribute.biomass() == 0 && tribute.lifeEnergy() == 0;

		if (consumeTribute || isModifier) {
			successValue += tribute.successModifier();

			int diseaseModifier = tribute.diseaseModifier();
			int hostileModifier = tribute.hostileModifier();
			int anomalyModifier = tribute.anomalyModifier();

			if (diseaseModifier != 0 || hostileModifier != 0 || anomalyModifier != 0) hasModifiers = true;

			diseaseValue += diseaseModifier;
			hostileValue += hostileModifier;
			anomalyValue += anomalyModifier;
			return true;
		}

		return false;
	}

	@Override
	public CompoundTag serializeNBT() {
		CompoundTag tag = new CompoundTag();
		tag.putByte("Biomass", biomass);
		tag.putInt("LifeEnergy", lifeEnergy);

		tag.putInt("Success", successValue);

		tag.putInt("Disease", diseaseValue);
		tag.putInt("Hostile", hostileValue);
		tag.putInt("Anomaly", anomalyValue);

		tag.putBoolean("HasModifiers", hasModifiers);
		return tag;
	}

	@Override
	public void deserializeNBT(CompoundTag tag) {
		biomass = tag.getByte("Biomass");
		lifeEnergy = tag.getInt("LifeEnergy");

		successValue = tag.getInt("Success");

		diseaseValue = tag.getInt("Disease");
		hostileValue = tag.getInt("Hostile");
		anomalyValue = tag.getInt("Anomaly");

		hasModifiers = tag.getBoolean("HasModifiers");
	}

}
