package com.github.elenterius.biomancy.inventory.itemhandler;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;

import javax.annotation.Nonnull;

public class LargeSingleItemStackHandler extends SingleItemStackHandler {

	private final short maxItemAmount;
	private short itemAmount;

	public LargeSingleItemStackHandler() {
		this(Short.MAX_VALUE);
	}

	public LargeSingleItemStackHandler(short maxItemAmount) {
		this.maxItemAmount = maxItemAmount;
	}

	@Override
	public int getSlotLimit(int slot) {
		return maxItemAmount;
	}

	@Override
	public int getAmount() {
		return itemAmount;
	}

	public void setAmount(short amount) {
		itemAmount = amount;
		if (!cachedStack.isEmpty()) cachedStack.setCount(amount);
	}

	@Override
	public void setStackInSlot(int slot, @Nonnull ItemStack stack) {
		super.setStackInSlot(slot, stack);
		itemAmount = (short) cachedStack.getCount();
	}

	@Nonnull
	@Override
	public ItemStack insertItem(int slot, @Nonnull ItemStack stackIn, boolean simulate) {
		ItemStack remainder = super.insertItem(slot, stackIn, simulate);
		if (!simulate) {
			itemAmount = (short) cachedStack.getCount();
		}
		return remainder;
	}

	@Nonnull
	@Override
	public ItemStack extractItem(int slot, int amount, boolean simulate) {
		ItemStack remainder = super.extractItem(slot, amount, simulate);
		if (!simulate) {
			itemAmount = (short) cachedStack.getCount();
		}
		return remainder;
	}

	@Override
	public void serializeItemAmount(CompoundNBT nbt) {
		nbt.putShort("ItemAmount", itemAmount);
	}

	@Override
	public int deserializeItemAmount(CompoundNBT nbt) {
		itemAmount = nbt.getShort("ItemAmount");
		return itemAmount;
	}

	@Override
	public CompoundNBT serializeNBT() {
		CompoundNBT nbt = new CompoundNBT();
		if (!cachedStack.isEmpty()) {
			serializeItemAmount(nbt);
			if (itemAmount > Byte.MAX_VALUE) {
				cachedStack.setCount(Byte.MAX_VALUE); //prevent byte overflow
				nbt.put("Item", cachedStack.write(new CompoundNBT()));
				cachedStack.setCount(itemAmount); //restore item count
			}
			else {
				nbt.put("Item", cachedStack.write(new CompoundNBT()));
			}
		}
		return nbt;
	}

	@Override
	public void deserializeNBT(CompoundNBT nbt) {
		cachedStack = nbt.contains("Item") ? ItemStack.read(nbt.getCompound("Item")) : ItemStack.EMPTY;
		if (!cachedStack.isEmpty()) {
			int itemAmount = deserializeItemAmount(nbt);
			cachedStack.setCount(itemAmount); //restore item amount
		}
	}
}
