package com.github.elenterius.biomancy.inventory.itemhandler.behavior;

import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;

import javax.annotation.Nonnull;

public abstract class ItemHandlerDelegator<ISH extends IItemHandler & IItemHandlerModifiable> implements IItemHandler, IItemHandlerModifiable {

	protected final ISH itemStackHandler;

	protected ItemHandlerDelegator(ISH itemStackHandlerIn) {
		itemStackHandler = itemStackHandlerIn;
	}

	@Override
	public void setStackInSlot(int slot, @Nonnull ItemStack stack) {
		itemStackHandler.setStackInSlot(slot, stack);
	}

	@Override
	public int getSlots() {
		return itemStackHandler.getSlots();
	}

	@Nonnull
	@Override
	public ItemStack getStackInSlot(int slot) {
		return itemStackHandler.getStackInSlot(slot);
	}

	@Nonnull
	@Override
	public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
		return itemStackHandler.insertItem(slot, stack, simulate);
	}

	@Nonnull
	@Override
	public ItemStack extractItem(int slot, int amount, boolean simulate) {
		return itemStackHandler.extractItem(slot, amount, simulate);
	}

	@Override
	public int getSlotLimit(int slot) {
		return itemStackHandler.getSlotLimit(slot);
	}

	@Override
	public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
		return itemStackHandler.isItemValid(slot, stack);
	}

}
