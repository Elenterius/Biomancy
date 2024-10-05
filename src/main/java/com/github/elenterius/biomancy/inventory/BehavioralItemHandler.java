package com.github.elenterius.biomancy.inventory;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.function.Predicate;

public interface BehavioralItemHandler extends IItemHandler {

	IItemHandler withoutBehavior();

	abstract class Wrapper<T extends IItemHandler> implements BehavioralItemHandler {

		protected final T itemHandler;

		public Wrapper(T itemHandler) {
			this.itemHandler = itemHandler;
		}

		@Override
		public IItemHandler withoutBehavior() {
			return itemHandler;
		}

		@Override
		public int getSlots() {
			return itemHandler.getSlots();
		}

		@Nonnull
		@Override
		public ItemStack getStackInSlot(int slot) {
			return itemHandler.getStackInSlot(slot);
		}

		@Nonnull
		@Override
		public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
			return itemHandler.insertItem(slot, stack, simulate);
		}

		@Nonnull
		@Override
		public ItemStack extractItem(int slot, int amount, boolean simulate) {
			return itemHandler.extractItem(slot, amount, simulate);
		}

		@Override
		public int getSlotLimit(int slot) {
			return itemHandler.getSlotLimit(slot);
		}

		@Override
		public boolean isItemValid(int slot, ItemStack stack) {
			return itemHandler.isItemValid(slot, stack);
		}

	}

	/**
	 * prevents item insertion, therefore only allowing item extraction (e.g. output slots).
	 */
	class DenyInput extends Wrapper<SerializableItemHandler> implements SerializableItemHandler {

		public DenyInput(SerializableItemHandler itemHandler) {
			super(itemHandler);
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
		public CompoundTag serializeNBT() {
			return itemHandler.serializeNBT();
		}

		@Override
		public void deserializeNBT(CompoundTag tag) {
			itemHandler.deserializeNBT(tag);
		}

		@Override
		public void setStackInSlot(int slot, @NotNull ItemStack stack) {
			itemHandler.setStackInSlot(slot, stack);
		}
	}

	/**
	 * only allows item insertion of matching items
	 */
	class FilterInput<F extends Predicate<ItemStack>> extends Wrapper<SerializableItemHandler> implements SerializableItemHandler {

		private final List<F> filters;

		public FilterInput(SerializableItemHandler itemHandler, List<F> slotFilters) {
			super(itemHandler);
			assert slotFilters.size() == itemHandler.getSlots();
			filters = slotFilters;
		}

		private boolean isInvalidSlot(int slot) {
			return slot < 0 || slot >= filters.size();
		}

		@Override
		public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
			if (isInvalidSlot(slot)) return false;
			return filters.get(slot).test(stack) && itemHandler.isItemValid(slot, stack);
		}

		@Override
		@Nonnull
		public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
			if (isInvalidSlot(slot)) return stack;
			if (!filters.get(slot).test(stack)) return stack;
			return itemHandler.insertItem(slot, stack, simulate);
		}

		@Override
		public CompoundTag serializeNBT() {
			return itemHandler.serializeNBT();
		}

		@Override
		public void deserializeNBT(CompoundTag tag) {
			itemHandler.deserializeNBT(tag);
		}

		@Override
		public void setStackInSlot(int slot, @NotNull ItemStack stack) {
			itemHandler.setStackInSlot(slot, stack);
		}
	}

}
