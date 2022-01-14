package com.github.elenterius.biomancy.world.inventory.itemhandler;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nonnull;

public class LargeSingleItemStackHandler extends SingleItemStackHandler {

	public static final String NBT_KEY_ITEM_AMOUNT = "ItemAmount";

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
		super.setStack(stack);
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
	public void serializeItemAmount(CompoundTag tag) {
		tag.putShort(NBT_KEY_ITEM_AMOUNT, itemAmount);
	}

	@Override
	public int deserializeItemAmount(CompoundTag tag) {
		itemAmount = tag.getShort(NBT_KEY_ITEM_AMOUNT);
		return itemAmount;
	}

	@Override
	public CompoundTag serializeNBT() {
		CompoundTag tag = new CompoundTag();
		if (!cachedStack.isEmpty()) {
			serializeItemAmount(tag);
			if (itemAmount > Byte.MAX_VALUE) {
				cachedStack.setCount(Byte.MAX_VALUE); //prevent byte overflow (ItemStack serializes its item count as byte)
				tag.put(NBT_KEY_ITEM, cachedStack.save(new CompoundTag()));
				cachedStack.setCount(itemAmount); //restore item count
			}
			else {
				tag.put(NBT_KEY_ITEM, cachedStack.save(new CompoundTag()));
			}
		}
		return tag;
	}

	@Override
	public void deserializeNBT(CompoundTag tag) {
		cachedStack = tag.contains(NBT_KEY_ITEM) ? ItemStack.of(tag.getCompound(NBT_KEY_ITEM)) : ItemStack.EMPTY;
		if (!cachedStack.isEmpty()) {
			cachedStack.setCount(deserializeItemAmount(tag)); //restore item amount
		}
	}

}
