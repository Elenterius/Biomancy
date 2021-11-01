package com.github.elenterius.biomancy.inventory.itemhandler;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.MathHelper;

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
			int value = MathHelper.clamp(amount, 0, getMaxAmount());
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
	public void serializeItemAmount(CompoundNBT nbt) {
		nbt.putShort(NBT_KEY_ITEM_AMOUNT, itemAmount);
	}

	@Override
	public int deserializeItemAmount(CompoundNBT nbt) {
		itemAmount = nbt.getShort(NBT_KEY_ITEM_AMOUNT);
		return itemAmount;
	}

	@Override
	public CompoundNBT serializeNBT() {
		CompoundNBT nbt = new CompoundNBT();
		if (!cachedStack.isEmpty()) {
			serializeItemAmount(nbt);
			if (itemAmount > Byte.MAX_VALUE) {
				cachedStack.setCount(Byte.MAX_VALUE); //prevent byte overflow (ItemStack serializes its item count as byte)
				nbt.put(NBT_KEY_ITEM, cachedStack.save(new CompoundNBT()));
				cachedStack.setCount(itemAmount); //restore item count
			}
			else {
				nbt.put(NBT_KEY_ITEM, cachedStack.save(new CompoundNBT()));
			}
		}
		return nbt;
	}

	@Override
	public void deserializeNBT(CompoundNBT nbt) {
		cachedStack = nbt.contains(NBT_KEY_ITEM) ? ItemStack.of(nbt.getCompound(NBT_KEY_ITEM)) : ItemStack.EMPTY;
		if (!cachedStack.isEmpty()) {
			int itemAmount = deserializeItemAmount(nbt);
			cachedStack.setCount(itemAmount); //restore item amount
		}
	}

}
