package com.github.elenterius.biomancy.inventory;

import com.github.elenterius.biomancy.util.ItemStackFilter;
import com.github.elenterius.biomancy.util.ItemStackFilterList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
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
	class PredicateFilterInput extends Wrapper<SerializableItemHandler> implements SerializableItemHandler {

		protected final List<Predicate<ItemStack>> filters;

		public PredicateFilterInput(SerializableItemHandler itemHandler, List<Predicate<ItemStack>> slotFilters) {
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

	class ItemStackFilterInput extends Wrapper<SerializableItemHandler> implements SerializableItemHandler {

		protected final ItemStackFilterList filters;

		public ItemStackFilterInput(SerializableItemHandler itemHandler) {
			super(itemHandler);
			filters = ItemStackFilterList.of(ItemStackFilter.ALLOW_ANY, itemHandler.getSlots());
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
			CompoundTag tag = new CompoundTag();
			tag.put("Handler", itemHandler.serializeNBT());
			tag.put("Filters", filters.serializeNBT());
			return tag;
		}

		@Override
		public void deserializeNBT(CompoundTag tag) {
			itemHandler.deserializeNBT(tag.getCompound("Handler"));
			filters.deserializeNBT(tag.getList("Filters", Tag.TAG_COMPOUND));
		}

		@Override
		public void setStackInSlot(int slot, @NotNull ItemStack stack) {
			itemHandler.setStackInSlot(slot, stack);
		}
	}

	class LockableItemStackFilterInput extends ItemStackFilterInput {

		private boolean locked = false;

		public LockableItemStackFilterInput(SerializableItemHandler itemHandler) {
			super(itemHandler);
		}

		public void toggleLock() {
			setLocked(!locked);
		}

		public boolean isLocked() {
			return locked;
		}

		public void setLocked(boolean locked) {
			this.locked = locked;

			if (locked) {
				for (int i = 0; i < itemHandler.getSlots(); i++) {
					ItemStack stack = itemHandler.getStackInSlot(i);
					filters.set(i, ItemStackFilter.of(stack));
				}
			}
			else {
				filters.setAllFilters(ItemStackFilter.ALLOW_ANY);
			}
		}

		@Override
		public CompoundTag serializeNBT() {
			CompoundTag tag = super.serializeNBT();
			tag.putBoolean("Locked", locked);
			return tag;
		}

		@Override
		public void deserializeNBT(CompoundTag tag) {
			super.deserializeNBT(tag);
			locked = tag.getBoolean("Locked");
		}
	}
}
