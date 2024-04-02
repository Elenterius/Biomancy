package com.github.elenterius.biomancy.inventory.itemhandler;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;

public class LargeSingleItemStackHandler extends SingleItemStackHandler {

	public static final String ITEM_AMOUNT_TAG = "ItemAmount";

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
	public int getMaxAmount() {
		return maxItemAmount;
	}

	@Override
	public int getAmount() {
		return itemAmount;
	}

	@Override
	public void setAmount(short amount) {
		if (!cachedStack.isEmpty()) {
			int value = Mth.clamp(amount, 0, getMaxAmount());
			itemAmount = (short) value;
			cachedStack.setCount(value);
			onContentsChanged();
		}
	}

	@Override
	public void setStack(ItemStack stack) {
		cachedStack = stack;
		itemAmount = (short) cachedStack.getCount();
		onContentsChanged();
	}

	@Override
	public ItemStack insertItem(int slot, ItemStack stackIn, boolean simulate) {
		ItemStack remainder = internalInsertItem(slot, stackIn, simulate);
		if (!simulate) {
			itemAmount = (short) cachedStack.getCount();
			onContentsChanged();
		}
		return remainder;
	}

	@Override
	public ItemStack extractItem(int slot, int amount, boolean simulate) {
		ItemStack remainder = internalExtractItem(slot, amount, simulate);
		if (!simulate) {
			itemAmount = (short) cachedStack.getCount();
			onContentsChanged();
		}
		return remainder;
	}

	@Override
	public void serializeItemAmount(CompoundTag tag) {
		tag.putShort(ITEM_AMOUNT_TAG, itemAmount);
	}

	@Override
	public int deserializeItemAmount(CompoundTag tag) {
		itemAmount = tag.getShort(ITEM_AMOUNT_TAG);
		return itemAmount;
	}

	@Override
	public CompoundTag serializeNBT() {
		CompoundTag tag = new CompoundTag();
		if (!cachedStack.isEmpty()) {
			serializeItemAmount(tag);
			if (itemAmount > Byte.MAX_VALUE) {
				cachedStack.setCount(Byte.MAX_VALUE); //prevent byte overflow (ItemStack serializes its item count as byte)
				tag.put(ITEM_TAG, cachedStack.save(new CompoundTag()));
				cachedStack.setCount(itemAmount); //restore item count
			}
			else {
				tag.put(ITEM_TAG, cachedStack.save(new CompoundTag()));
			}
		}
		return tag;
	}

	@Override
	public void deserializeNBT(CompoundTag tag) {
		cachedStack = tag.contains(ITEM_TAG) ? ItemStack.of(tag.getCompound(ITEM_TAG)) : ItemStack.EMPTY;
		if (!cachedStack.isEmpty()) {
			cachedStack.setCount(deserializeItemAmount(tag)); //restore item amount
		}
	}

}
