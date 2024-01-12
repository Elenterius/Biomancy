package com.github.elenterius.biomancy.menu.slot;

import net.minecraft.world.Container;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class OutputSlot extends Slot {

	public OutputSlot(Container container, int index, int xPos, int yPos) {
		super(container, index, xPos, yPos);
	}

	@Override
	public boolean mayPlace(ItemStack stack) {
		return false;
	}

}
