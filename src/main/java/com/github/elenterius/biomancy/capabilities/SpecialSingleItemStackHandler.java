package com.github.elenterius.biomancy.capabilities;

import com.github.elenterius.biomancy.BiomancyMod;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemHandlerHelper;

import javax.annotation.Nonnull;

public class SpecialSingleItemStackHandler implements IItemHandler, IItemHandlerModifiable, INBTSerializable<CompoundNBT> {

	private static final CompoundNBT EMPTY_COMPOUND_NBT = new CompoundNBT();

	private final ItemStack hostStack;
	private final int maxItemAmount;
	private ItemStack cachedStack;
	private int itemAmount;

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
		CompoundNBT inventory = new CompoundNBT();
		inventory.putShort("MaxAmount", (short) maxItemAmount); // for client display
		inventory.putShort("Amount", (short) itemAmount);
		if (cachedStack.getCount() > 64) cachedStack.setCount(64); // prevent byte overflow
		inventory.put("Item", cachedStack.write(new CompoundNBT()));
		if (cachedStack.getCount() != itemAmount) cachedStack.setCount(itemAmount); //restore correct item amount
		hostStack.getOrCreateChildTag(BiomancyMod.MOD_ID).put("Inventory", inventory); //cheese cap sync
		return EMPTY_COMPOUND_NBT;
	}

	@Override
	public void deserializeNBT(CompoundNBT ignored) {
		CompoundNBT nbt = hostStack.getOrCreateChildTag(BiomancyMod.MOD_ID);
		if (nbt.contains("Inventory")) readNBT(nbt.getCompound("Inventory"));
		else reset();
	}

	private void readNBT(CompoundNBT inventory) {
		cachedStack = ItemStack.read(inventory.getCompound("Item")); //cheese cap sync
		itemAmount = inventory.getShort("Amount");
		if (cachedStack.getCount() != itemAmount) cachedStack.setCount(itemAmount); //restore correct item amount
		if (!inventory.contains("MaxAmount")) {
			inventory.putShort("MaxAmount", (short) maxItemAmount); //for client display
		}
	}

	protected void updateCachedStack() {
		CompoundNBT nbt = hostStack.getOrCreateChildTag(BiomancyMod.MOD_ID);
		if (nbt.contains("Inventory")) {
			CompoundNBT inventory = nbt.getCompound("Inventory");
			if (inventory.getBoolean("IsDirty")) {
				inventory.putBoolean("IsDirty", false);
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
}
