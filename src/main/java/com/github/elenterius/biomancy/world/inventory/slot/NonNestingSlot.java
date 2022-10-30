package com.github.elenterius.biomancy.world.inventory.slot;

import com.github.elenterius.biomancy.world.inventory.itemhandler.HandlerBehaviors;
import net.minecraft.world.Container;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class NonNestingSlot extends Slot {

	public NonNestingSlot(Container container, int index, int x, int y) {
		super(container, index, x, y);
	}

	@Override
	public boolean mayPlace(ItemStack stack) {
		//only allow empty item inventories
		return HandlerBehaviors.EMPTY_ITEM_INVENTORY_PREDICATE.test(stack);
	}

}