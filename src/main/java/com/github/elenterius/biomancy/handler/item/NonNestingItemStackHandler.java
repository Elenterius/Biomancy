package com.github.elenterius.biomancy.handler.item;

import com.github.elenterius.biomancy.block.FleshChestBlock;
import com.github.elenterius.biomancy.block.GulgeBlock;
import net.minecraft.block.Block;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nonnull;

/**
 * Delegator that prevents nesting of items with an inventory (ITEM_HANDLER_CAPABILITY). <br>
 * Used to expose inventory capabilities that only allow item extraction (output slots).
 */
public class NonNestingItemStackHandler implements IItemHandler, IItemHandlerModifiable {

	private final ItemStackHandler itemStackHandler;

	public NonNestingItemStackHandler(ItemStackHandler itemStackHandler) {
		this.itemStackHandler = itemStackHandler;
	}

	public static boolean isItemStackInventoryEmpty(@Nonnull ItemStack stack) {
		if (stack.getItem() instanceof BlockItem) {
			Block block = ((BlockItem) stack.getItem()).getBlock();
			if (block instanceof ShulkerBoxBlock) {
				return stack.getChildTag("BlockEntityTag") == null;
			}
			else if (block instanceof FleshChestBlock || block instanceof GulgeBlock) {
				CompoundNBT nbt = stack.getChildTag("BlockEntityTag");
				if (nbt == null) return true;
				return nbt.getCompound("Inventory").isEmpty();
			}
		}

		LazyOptional<IItemHandler> capability = stack.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY);
		final boolean[] isEmpty = {true};
		capability.ifPresent(itemHandler -> {
			int slots = itemHandler.getSlots();
			for (int i = 0; i < slots; i++) {
				if (!itemHandler.getStackInSlot(i).isEmpty()) {
					isEmpty[0] = false;
					break;
				}
			}
		});
		return isEmpty[0];
	}

	@Override
	public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
		//only allow empty item inventories
		return isItemStackInventoryEmpty(stack);
	}

	@Override
	@Nonnull
	public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
		if (stack.getItem() instanceof BlockItem && ((BlockItem) stack.getItem()).getBlock() instanceof ShulkerBoxBlock) {
			if (stack.getChildTag("BlockEntityTag") != null) return stack;
		}

		if (stack.getItem() instanceof BlockItem) {
			Block block = ((BlockItem) stack.getItem()).getBlock();
			if (block instanceof ShulkerBoxBlock) {
				if (stack.getChildTag("BlockEntityTag") != null) return stack;
			}
			else if (block instanceof FleshChestBlock || block instanceof GulgeBlock) {
				CompoundNBT nbt = stack.getChildTag("BlockEntityTag");
				if (nbt != null && !nbt.getCompound("Inventory").isEmpty()) return stack;
			}
		}

		LazyOptional<IItemHandler> capability = stack.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY);
		final boolean[] isEmpty = {true};
		capability.ifPresent(itemHandler -> {
			int slots = itemHandler.getSlots();
			for (int i = 0; i < slots; i++) {
				if (!itemHandler.getStackInSlot(i).isEmpty()) {
					isEmpty[0] = false;
					break;
				}
			}
		});
		if (!isEmpty[0]) return stack;

		//only allow insertion of items with empty inventories
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

	@Override
	public void setStackInSlot(int slot, @Nonnull ItemStack stack) {
		itemStackHandler.setStackInSlot(slot, stack);
	}
}
