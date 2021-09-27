package com.github.elenterius.biomancy.inventory.itemhandler;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemHandlerHelper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class SpecialSingleItemStackHandler implements IItemHandler, IItemHandlerModifiable, INBTSerializable<CompoundNBT> {

	public static final String NBT_KEY_INVENTORY = "SingleItemInv";
	public static final String NBT_KEY_ITEM = "Item";
	public static final String NBT_KEY_AMOUNT = "Amount";
	public static final String NBT_KEY_MAX_AMOUNT = "MaxAmount";
	public static final String NBT_KEY_IS_DIRTY = "IsDirty";
	private static final CompoundNBT EMPTY_COMPOUND_NBT = new CompoundNBT();

	private final ItemStack hostStack;
	private final int maxItemAmount;
	private ItemStack cachedStack;
	private int itemAmount;

	private boolean isDirty = true;

	public SpecialSingleItemStackHandler(ItemStack hostStack, short maxAmount) {
		this.maxItemAmount = maxAmount;
		this.hostStack = hostStack;
		deserializeNBT(null);
	}

	protected void validateSlotIndex(int slot) {
		if (slot != 0) throw new RuntimeException("Slot " + slot + " not valid - must be 0");
	}

	@Override
	public int getSlots() {
		return 1;
	}

	@Override
	public int getSlotLimit(int slot) {
		return maxItemAmount;
	}

	@Override
	public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
		return slot == 0;
	}

	public boolean isEmpty() {
		return cachedStack.isEmpty();
	}

	public int getCount() {
		return itemAmount;
	}

	public Item getItem() {
		return cachedStack.getItem();
	}

	@Nonnull
	@Override
	public ItemStack getStackInSlot(int slot) {
		validateSlotIndex(slot);
		updateCachedStack();
		return cachedStack;
	}

	@Override
	public void setStackInSlot(int slot, @Nonnull ItemStack stack) {
		validateSlotIndex(slot);
		cachedStack = stack;
		itemAmount = stack.getCount();
		serializeNBT(); // save new stack
	}

	@Nonnull
	@Override
	public ItemStack insertItem(int slot, @Nonnull ItemStack stackIn, boolean simulate) {
		validateSlotIndex(slot);
		if (stackIn.isEmpty()) return ItemStack.EMPTY;
		if (!isItemValid(slot, stackIn)) return stackIn;

		updateCachedStack();
		ItemStack storedStack = cachedStack;
		if (!storedStack.isEmpty() && !ItemHandlerHelper.canItemStacksStack(stackIn, storedStack)) return stackIn;

		if (itemAmount >= maxItemAmount) return stackIn;
		int insertGoal = stackIn.getCount();
		int newAmount = itemAmount + insertGoal;
		int overflow = newAmount > maxItemAmount ? newAmount - maxItemAmount : 0;

		if (!simulate) {
			int insertAmount = overflow > 0 ? insertGoal - overflow : insertGoal;
			if (storedStack.isEmpty()) cachedStack = ItemHandlerHelper.copyStackWithSize(stackIn, insertAmount);
			else storedStack.grow(insertAmount);
			itemAmount += insertAmount;
			serializeNBT();
		}

		return overflow > 0 ? ItemHandlerHelper.copyStackWithSize(stackIn, overflow) : ItemStack.EMPTY;
	}

	@Nonnull
	@Override
	public ItemStack extractItem(int slot, int amount, boolean simulate) {
		validateSlotIndex(slot);
		if (amount == 0) return ItemStack.EMPTY;

		updateCachedStack();
		ItemStack storedStack = cachedStack;
		if (storedStack.isEmpty()) return ItemStack.EMPTY;
		int extractGoal = Math.min(amount, maxItemAmount);

		if (itemAmount <= extractGoal) {
			if (!simulate) {
				cachedStack = ItemStack.EMPTY;
				itemAmount = 0;
				serializeNBT();
				return storedStack;
			}
			else return storedStack.copy();
		}
		else {
			if (!simulate) {
				itemAmount -= extractGoal;
				cachedStack = ItemHandlerHelper.copyStackWithSize(storedStack, itemAmount);
				serializeNBT();
			}
			return ItemHandlerHelper.copyStackWithSize(storedStack, extractGoal);
		}
	}

	@Override
	public CompoundNBT serializeNBT() {
		CompoundNBT invNbt = new CompoundNBT();
		invNbt.putShort(NBT_KEY_MAX_AMOUNT, (short) maxItemAmount); // for client display
		invNbt.putShort(NBT_KEY_AMOUNT, (short) itemAmount);
		if (cachedStack.getCount() > 64) cachedStack.setCount(64); // prevent byte overflow
		invNbt.put(NBT_KEY_ITEM, cachedStack.save(new CompoundNBT()));
		if (cachedStack.getCount() != itemAmount) cachedStack.setCount(itemAmount); //restore correct item amount
		hostStack.getOrCreateTag().put(NBT_KEY_INVENTORY, invNbt); //cheese cap sync

		onContentsChanged();
		return EMPTY_COMPOUND_NBT;
	}

	@Override
	public void deserializeNBT(@Nullable CompoundNBT ignored) {
		CompoundNBT nbt = hostStack.getOrCreateTag();
		if (nbt.contains(NBT_KEY_INVENTORY)) readNBT(nbt.getCompound(NBT_KEY_INVENTORY));
		else reset();
	}

	private void readNBT(CompoundNBT invNbt) {
		cachedStack = ItemStack.of(invNbt.getCompound(NBT_KEY_ITEM)); //cheese cap sync
		itemAmount = invNbt.getShort(NBT_KEY_AMOUNT);
		if (cachedStack.getCount() != itemAmount) cachedStack.setCount(itemAmount); //restore correct item amount
		if (!invNbt.contains(NBT_KEY_MAX_AMOUNT)) {
			invNbt.putShort(NBT_KEY_MAX_AMOUNT, (short) maxItemAmount); //for client display
		}
	}

	protected void updateCachedStack() {
		CompoundNBT nbt = hostStack.getOrCreateTag();
		if (nbt.contains(NBT_KEY_INVENTORY)) {
			CompoundNBT inventory = nbt.getCompound(NBT_KEY_INVENTORY);
			if (inventory.getBoolean(NBT_KEY_IS_DIRTY)) {
				inventory.putBoolean(NBT_KEY_IS_DIRTY, false);
				readNBT(inventory); //reload
			}
		}
		else {
			reset();
		}
	}

	private void reset() {
		cachedStack = ItemStack.EMPTY;
		itemAmount = 0;
	}

	protected void onContentsChanged() {
		isDirty = true;
	}

	public boolean isDirty() {
		boolean flag = isDirty;
		isDirty = false;
		return flag;
	}

}
