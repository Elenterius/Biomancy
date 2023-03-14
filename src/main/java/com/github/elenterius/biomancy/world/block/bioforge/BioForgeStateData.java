package com.github.elenterius.biomancy.world.block.bioforge;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.inventory.ContainerData;

public class BioForgeStateData implements ContainerData {

	public static final int FUEL_INDEX = 0;
	public static final String NBT_KEY_FUEL = "Fuel";
	private short fuelAmount;

	public int getFuelAmount() {
		return fuelAmount;
	}

	public void setFuelAmount(short amount) {
		fuelAmount = amount;
	}

	public void addFuelAmount(short value) {
		fuelAmount = (short) Math.max(fuelAmount + value, 0);
	}

	public void serialize(CompoundTag nbt) {
		nbt.putShort(NBT_KEY_FUEL, fuelAmount);
	}

	public void deserialize(CompoundTag nbt) {
		fuelAmount = nbt.getShort(NBT_KEY_FUEL);
	}

	@Override
	public int get(int index) {
		validateIndex(index);
		if (index == FUEL_INDEX) return fuelAmount;
		return 0;
	}

	@Override
	public void set(int index, int value) {
		validateIndex(index);
		if (index == FUEL_INDEX) fuelAmount = (short) value;
	}

	@Override
	public int getCount() {
		return 1;
	}

	protected void validateIndex(int index) {
		if (index < 0 || index >= getCount()) throw new IndexOutOfBoundsException(index);
	}

}
