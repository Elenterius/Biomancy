package com.github.elenterius.biomancy.world.inventory.itemhandler;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.function.Predicate;

interface Forwarding<T> {
	T inner();
}

interface QueryOperations extends Forwarding<IItemHandler> {
	default boolean isEmpty() {
		for (int i = 0; i < inner().getSlots(); i++) {
			if (!inner().getStackInSlot(i).isEmpty()) return false;
		}
		return true;
	}

	default boolean isFull() {
		for (int i = 0; i < inner().getSlots(); i++) {
			ItemStack stack = inner().getStackInSlot(i);
			if (stack.isEmpty() || stack.getCount() < inner().getSlotLimit(i)) return false;
		}
		return true;
	}

	default int countItem(Item item) {
		int totalCount = 0;
		for (int i = 0; i < inner().getSlots(); i++) {
			ItemStack stack = inner().getStackInSlot(i);
			if (stack.getItem().equals(item)) totalCount += stack.getCount();
		}
		return totalCount;
	}

	default boolean contains(Item item) {
		for (int i = 0; i < inner().getSlots(); i++) {
			if (inner().getStackInSlot(i).getItem().equals(item)) return true;
		}
		return false;
	}

	default boolean hasAny(Collection<Item> items) {
		for (int i = 0; i < inner().getSlots(); i++) {
			ItemStack stack = inner().getStackInSlot(i);
			if (stack.getCount() > 0 && items.contains(stack.getItem())) return true;
		}
		return false;
	}

	default boolean doesItemStackFit(int index, ItemStack stack) {
		if (!inner().isItemValid(index, stack)) return false;
		ItemStack remainder = inner().insertItem(index, stack, true);
		return remainder.isEmpty();
	}

	default boolean doesItemStackFit(ItemStack stack) {
		for (int i = 0; i < inner().getSlots(); i++) {
			if (!inner().isItemValid(i, stack)) return false;
			stack = inner().insertItem(i, stack, true);
			if (stack.isEmpty()) return true;
		}
		return false;
	}

}

interface TransferOperations extends Forwarding<IItemHandler> {
	default ItemStack insertItem(ItemStack stack) {
		return insertItem(stack, false);
	}

	default ItemStack insertItem(ItemStack stack, boolean simulate) {
		if (stack.isEmpty()) return stack;

		for (int i = 0; i < inner().getSlots(); i++) {
			stack = inner().insertItem(i, stack, simulate);
			if (stack.isEmpty()) return ItemStack.EMPTY;
		}
		return stack;
	}

	default ItemStack insertItemOnExistingFirst(ItemStack stack) {
		return insertItemOnExistingFirst(stack, false);
	}

	default ItemStack insertItemOnExistingFirst(ItemStack stack, boolean simulate) {
		if (stack.isEmpty()) return stack;

		for (int i = 0; i < inner().getSlots(); i++) {
			if (inner().getStackInSlot(i).isEmpty()) continue; //skip empty slots

			stack = inner().insertItem(i, stack, simulate);
			if (stack.isEmpty()) return ItemStack.EMPTY;
		}

		return insertItem(stack, simulate);
	}

	default ItemStack extractItemFirstFound() {
		return extractItemFirstFound(false);
	}

	default ItemStack extractItemFirstFound(boolean simulate) {
		return extractItemFirstFound(Integer.MAX_VALUE, simulate);
	}

	default ItemStack extractItemFirstFound(int maxAmount, boolean simulate) {
		for (int i = 0; i < inner().getSlots(); i++) {
			ItemStack stackInSlot = inner().getStackInSlot(i);
			if (stackInSlot.isEmpty()) continue;

			int amount = Math.min(stackInSlot.getMaxStackSize(), maxAmount);
			ItemStack stack = inner().extractItem(i, amount, simulate);
			if (!stack.isEmpty()) return stack;
		}
		return ItemStack.EMPTY;
	}

	default ItemStack extractItemFirstMatch(Predicate<ItemStack> predicate) {
		return extractItemFirstMatch(predicate, false);
	}

	default ItemStack extractItemFirstMatch(Predicate<ItemStack> predicate, boolean simulate) {
		return extractItemFirstMatch(predicate, Integer.MAX_VALUE, simulate);
	}

	default ItemStack extractItemFirstMatch(Predicate<ItemStack> predicate, int maxAmount, boolean simulate) {
		for (int i = 0; i < inner().getSlots(); i++) {
			int amount = Math.min(maxAmount, inner().getStackInSlot(i).getMaxStackSize());
			ItemStack stack = inner().extractItem(i, amount, true);

			if (!stack.isEmpty() && predicate.test(stack)) {
				if (!simulate) {
					return inner().extractItem(i, amount, false);
				}
				return stack;
			}
		}

		return ItemStack.EMPTY;
	}

	default ItemStack extractItemAny(int targetAmount, boolean simulate) {
		ItemStack extracted = ItemStack.EMPTY;

		for (int i = 0; i < inner().getSlots(); i++) {
			ItemStack stackInSlot = inner().getStackInSlot(i);
			if (stackInSlot.isEmpty()) continue;

			if (extracted.isEmpty()) {
				int amount = Math.min(stackInSlot.getMaxStackSize(), targetAmount);
				extracted = inner().extractItem(i, amount, simulate);

				if (!extracted.isEmpty()) targetAmount = Math.min(extracted.getMaxStackSize(), targetAmount);

				if (extracted.getCount() >= targetAmount) return extracted;
			}
			else if (ItemHandlerHelper.canItemStacksStack(stackInSlot, extracted)) {
				int amount = Math.min(stackInSlot.getMaxStackSize(), targetAmount - extracted.getCount());
				ItemStack result = inner().extractItem(i, amount, simulate);
				if (!result.isEmpty()) extracted.grow(result.getCount());
				if (extracted.getCount() >= targetAmount) return extracted;
			}
		}

		return extracted;
	}
}

interface ForwardingItemHandler extends IItemHandler, Forwarding<IItemHandler> {
	@Override
	default int getSlots() {
		return inner().getSlots();
	}

	@Nonnull
	@Override
	default ItemStack getStackInSlot(int slot) {
		return inner().getStackInSlot(slot);
	}

	@Nonnull
	@Override
	default ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
		return inner().insertItem(slot, stack, simulate);
	}

	@Nonnull
	@Override
	default ItemStack extractItem(int slot, int amount, boolean simulate) {
		return inner().extractItem(slot, amount, simulate);
	}

	@Override
	default int getSlotLimit(int slot) {
		return inner().getSlotLimit(slot);
	}

	@Override
	default boolean isItemValid(int slot, ItemStack stack) {
		return inner().isItemValid(slot, stack);
	}
}

public record EnhancedItemHandler(IItemHandler inner) implements ForwardingItemHandler, TransferOperations, QueryOperations {}
