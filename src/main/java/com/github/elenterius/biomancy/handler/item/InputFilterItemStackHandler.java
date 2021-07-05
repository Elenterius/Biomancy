package com.github.elenterius.biomancy.handler.item;

import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nonnull;
import java.util.function.Predicate;

/**
 * Delegator that only allows item insertion of valid items. <br>
 * Used to expose inventory capabilities that only allow item insertion of specific items.
 */
public class InputFilterItemStackHandler implements IItemHandler {

	private final ItemStackHandler itemStackHandler;
	private final Predicate<ItemStack> validItems;

	public InputFilterItemStackHandler(ItemStackHandler itemStackHandler, Predicate<ItemStack> validItems) {
		this.itemStackHandler = itemStackHandler;
		this.validItems = validItems;
	}

	@Override
	public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
		return validItems.test(stack);
	}

	@Override
	@Nonnull
	public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
		if (!validItems.test(stack)) {
			return stack;
		}

		return itemStackHandler.insertItem(slot, stack, simulate);
	}

	@Override
	public int getSlots() {
		return itemStackHandler.getSlots();
	}

	@Override
	@Nonnull
	public ItemStack getStackInSlot(int slot) {
		return itemStackHandler.getStackInSlot(slot);
	}

	@Override
	@Nonnull
	public ItemStack extractItem(int slot, int amount, boolean simulate) {
		return itemStackHandler.extractItem(slot, amount, simulate);
	}

	@Override
	public int getSlotLimit(int slot) {
		return itemStackHandler.getSlotLimit(slot);
	}

}
