package com.github.elenterius.biomancy.world.inventory.itemhandler;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemHandlerHelper;

import javax.annotation.Nonnull;

public class SingleItemStackHandler implements IItemHandler, IItemHandlerModifiable, INBTSerializable<CompoundTag> {

	public static final String NBT_KEY_ITEM = "Item";

	protected ItemStack cachedStack = ItemStack.EMPTY;

	public SingleItemStackHandler() {}

	protected void validateSlotIndex(int slot) {
		if (slot != 0) throw new IllegalArgumentException("Slot " + slot + " not valid - must be 0");
	}

	@Override
	public int getSlots() {
		return 1;
	}

	@Override
	public int getSlotLimit(int slot) {
		return cachedStack.getMaxStackSize();
	}

	@Override
	public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
		return slot == 0;
	}

	public boolean isEmpty() {
		return cachedStack.isEmpty();
	}

	public int getAmount() {
		return cachedStack.getCount();
	}

	public void setAmount(short amount) {
		if (!cachedStack.isEmpty()) {
			cachedStack.setCount(Mth.clamp(amount, 0, getMaxAmount()));
			onContentsChanged();
		}
	}

	public int getMaxAmount() {
		return getSlotLimit(0);
	}

	public Item getItem() {
		return cachedStack.getItem();
	}

	public ItemStack getStack() {
		return cachedStack;
	}

	@Nonnull
	@Override
	public ItemStack getStackInSlot(int slot) {
		validateSlotIndex(slot);
		return cachedStack;
	}

	public void setStack(ItemStack stack) {
		cachedStack = stack;
		onContentsChanged();
	}

	@Override
	public void setStackInSlot(int slot, @Nonnull ItemStack stack) {
		validateSlotIndex(slot);
		setStack(stack);
	}

	@Nonnull
	@Override
	public ItemStack insertItem(int slot, @Nonnull ItemStack stackIn, boolean simulate) {
		validateSlotIndex(slot);
		if (stackIn.isEmpty()) return ItemStack.EMPTY;
		if (!isItemValid(slot, stackIn)) return stackIn;

		if (!cachedStack.isEmpty() && !ItemHandlerHelper.canItemStacksStack(stackIn, cachedStack)) return stackIn;
		if (getAmount() >= getMaxAmount()) return stackIn;

		int insertGoal = stackIn.getCount();
		int newAmount = getAmount() + insertGoal;
		int overflow = newAmount > getMaxAmount() ? newAmount - getMaxAmount() : 0;

		if (!simulate) {
			int insertAmount = overflow > 0 ? insertGoal - overflow : insertGoal;
			if (cachedStack.isEmpty()) cachedStack = ItemHandlerHelper.copyStackWithSize(stackIn, insertAmount);
			else cachedStack.grow(insertAmount);
			onContentsChanged();
		}

		return overflow > 0 ? ItemHandlerHelper.copyStackWithSize(stackIn, overflow) : ItemStack.EMPTY;
	}

	@Nonnull
	@Override
	public ItemStack extractItem(int slot, int amount, boolean simulate) {
		validateSlotIndex(slot);
		if (amount == 0) return ItemStack.EMPTY;

		if (cachedStack.isEmpty()) return ItemStack.EMPTY;
		int extractGoal = Math.min(amount, getMaxAmount());

		if (getAmount() <= extractGoal) {
			if (!simulate) {
				ItemStack stack = cachedStack;
				cachedStack = ItemStack.EMPTY;
				onContentsChanged();
				return stack;
			}
			else return cachedStack.copy();
		}
		else {
			if (!simulate) {
				cachedStack.grow(-extractGoal);
				onContentsChanged();
			}
			return ItemHandlerHelper.copyStackWithSize(cachedStack, extractGoal);
		}
	}

	public void serializeItemAmount(CompoundTag tag) {}

	public int deserializeItemAmount(CompoundTag tag) {
		return -1;
	}

	@Override
	public CompoundTag serializeNBT() {
		CompoundTag nbt = new CompoundTag();
		serializeItemAmount(nbt);
		if (!cachedStack.isEmpty()) {
			int count = cachedStack.getCount();
			if (count > 64) cachedStack.setCount(64); //prevent byte overflow
			nbt.put(NBT_KEY_ITEM, cachedStack.save(new CompoundTag()));
			if (count != cachedStack.getCount()) cachedStack.setCount(count); //restore item count
		}
		return nbt;
	}

	@Override
	public void deserializeNBT(CompoundTag nbt) {
		if (nbt.contains(NBT_KEY_ITEM)) cachedStack = ItemStack.of(nbt.getCompound(NBT_KEY_ITEM));
		else cachedStack = ItemStack.EMPTY;

		int itemAmount = deserializeItemAmount(nbt);
		if (itemAmount > cachedStack.getCount()) {
			cachedStack.setCount(itemAmount); //restore item amount
		}
	}

	protected void onContentsChanged() {
		// to be overridden
	}

}
