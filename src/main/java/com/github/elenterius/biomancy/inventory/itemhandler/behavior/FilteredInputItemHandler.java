package com.github.elenterius.biomancy.inventory.itemhandler.behavior;

import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;

import javax.annotation.Nonnull;
import java.util.function.Predicate;

/**
 * Delegator that only allows item insertion of valid items. <br>
 * Used to expose inventory capabilities that only allow item insertion of matching items.
 */
public class FilteredInputItemHandler<ISH extends IItemHandler & IItemHandlerModifiable> extends ItemHandlerDelegator<ISH> {

	private final Predicate<ItemStack> validItems;

	public FilteredInputItemHandler(ISH itemStackHandlerIn, Predicate<ItemStack> validItems) {
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
