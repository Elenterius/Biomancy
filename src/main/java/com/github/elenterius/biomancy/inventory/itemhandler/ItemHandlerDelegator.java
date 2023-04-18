package com.github.elenterius.biomancy.inventory.itemhandler;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;

import javax.annotation.Nonnull;
import java.util.function.Predicate;

/**
 * ItemHandler delegator that is used to expose inventory capabilities with additional behavior
 *
 * @param <T> the ItemStackHandler to delegate
 */
public abstract class ItemHandlerDelegator<T extends IItemHandler & IItemHandlerModifiable & INBTSerializable<CompoundTag>> implements IItemHandler, IItemHandlerModifiable, INBTSerializable<CompoundTag> {

	protected final T itemStackHandler;

	protected ItemHandlerDelegator(T itemStackHandlerIn) {
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

	@Override
	public CompoundTag serializeNBT() {
		return itemStackHandler.serializeNBT();
	}

	@Override
	public void deserializeNBT(CompoundTag nbt) {
		itemStackHandler.deserializeNBT(nbt);
	}

	/**
	 * prevents item insertion, therefore only allowing item extraction (e.g. output slots).
	 */
	public static class DenyInput<T extends IItemHandler & IItemHandlerModifiable & INBTSerializable<CompoundTag>> extends ItemHandlerDelegator<T> {

		public DenyInput(T itemStackHandlerIn) {
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

	/**
	 * only allows item insertion of matching items
	 */
	public static class FilterInput<T extends IItemHandler & IItemHandlerModifiable & INBTSerializable<CompoundTag>> extends ItemHandlerDelegator<T> {

		private final Predicate<ItemStack> validItems;

		public FilterInput(T itemStackHandlerIn, Predicate<ItemStack> validItems) {
			super(itemStackHandlerIn);
			this.validItems = validItems;
		}

		@Override
		public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
			return validItems.test(stack) && itemStackHandler.isItemValid(slot, stack);
		}

		@Override
		@Nonnull
		public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
			if (!validItems.test(stack)) return stack;
			return itemStackHandler.insertItem(slot, stack, simulate);
		}

	}
}
