package com.github.elenterius.biomancy.menu.slot;

import com.github.elenterius.biomancy.api.nutrients.Nutrients;
import net.minecraft.world.Container;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class FuelSlot extends Slot {

	public FuelSlot(Container pContainer, int index, int x, int y) {
		super(pContainer, index, x, y);
	}

	@Override
	public boolean mayPlace(ItemStack stack) {
		return Nutrients.isValidFuel(stack);
	}

}
