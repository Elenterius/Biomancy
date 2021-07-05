package com.github.elenterius.biomancy.handler.item;

import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nonnull;

/**
 * Delegator that prevents item insertion. <br>
 * Used to expose inventory capabilities that only allow item extraction (output slots).
 */
public class NoInsertItemStackHandler implements IItemHandler {

	private final ItemStackHandler itemStackHandler;

	public NoInsertItemStackHandler(ItemStackHandler itemStackHandler) {
		this.itemStackHandler = itemStackHandler;
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

	@Override
	public int getSlots() {
		return itemStackHandler.getSlots();
	}

	@Override
	@Nonnull
	public ItemStack getStackInSlot(int slot) {
		return itemStackHandler.getStackInSlot(slot);
	}

	@Override
	@Nonnull
	public ItemStack extractItem(int slot, int amount, boolean simulate) {
		return itemStackHandler.extractItem(slot, amount, simulate);
	}

	@Override
	public int getSlotLimit(int slot) {
		return itemStackHandler.getSlotLimit(slot);
	}

}
