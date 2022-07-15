package com.github.elenterius.biomancy.world.inventory.slot;

import com.github.elenterius.biomancy.util.fuel.NutrientFuelUtil;
import net.minecraft.world.Container;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class FuelSlot extends Slot {

	public FuelSlot(Container pContainer, int index, int x, int y) {
		super(pContainer, index, x, y);
	}

	@Override
	public boolean mayPlace(ItemStack stack) {
		return NutrientFuelUtil.isValidFuel(stack);
	}

}
