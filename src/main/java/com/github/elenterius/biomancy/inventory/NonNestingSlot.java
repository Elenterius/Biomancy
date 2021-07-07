package com.github.elenterius.biomancy.inventory;

import com.github.elenterius.biomancy.handler.item.NonNestingItemStackHandler;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;

public class NonNestingSlot extends Slot {

	public NonNestingSlot(IInventory inventoryIn, int index, int xPosition, int yPosition) {
		super(inventoryIn, index, xPosition, yPosition);
	}

	@Override
	public boolean isItemValid(ItemStack stack) {
		//only allow empty item inventories
		return NonNestingItemStackHandler.isItemStackInventoryEmpty(stack);
	}

}
