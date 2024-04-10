package com.github.elenterius.biomancy.block.bioforge;

import com.github.elenterius.biomancy.util.fuel.IFuelHandler;
import net.minecraft.world.inventory.ContainerData;

public class BioForgeStateData implements ContainerData {

	public static final int FUEL_INDEX = 0;
	public final IFuelHandler fuelHandler;

	public BioForgeStateData(IFuelHandler fuelHandler) {
		this.fuelHandler = fuelHandler;
	}

	@Override
	public int get(int index) {
		validateIndex(index);
		if (index == FUEL_INDEX) return fuelHandler.getFuelAmount();
		return 0;
	}

	@Override
	public void set(int index, int value) {
		validateIndex(index);
		if (index == FUEL_INDEX) fuelHandler.setFuelAmount(value);
	}

	@Override
	public int getCount() {
		return 1;
	}

	protected void validateIndex(int index) {
		if (index < 0 || index >= getCount()) throw new IndexOutOfBoundsException(index);
	}

}
