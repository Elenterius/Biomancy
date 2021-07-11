package com.github.elenterius.biomancy.inventory.itemhandler.behavior;

import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;

import javax.annotation.Nonnull;

/**
 * Delegator that prevents item insertion. <br>
 * Used to expose inventory capabilities that only allow item extraction (output slots).
 */
public class DenyInputItemHandler<ISH extends IItemHandler & IItemHandlerModifiable> extends ItemHandlerDelegator<ISH> {

	public DenyInputItemHandler(ISH itemStackHandlerIn) {
		super(itemStackHandlerIn);
	}

	@Override
	public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
		return false;
	}

	@Override
	@Nonnull
	public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
		return stack;
	}

}
