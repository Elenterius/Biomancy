package com.github.elenterius.biomancy.inventory;

import net.minecraft.core.BlockPos;
import net.minecraft.world.Containers;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;

import java.util.List;

public final class ItemHandlerUtil {

	private ItemHandlerUtil() {}

	public static void dropContents(Level level, BlockPos pos, IItemHandler itemHandler) {
		dropContents(level, pos.getX(), pos.getY(), pos.getZ(), itemHandler);
	}

	public static void dropContents(Level level, Entity entity, IItemHandler itemHandler) {
		dropContents(level, entity.getX(), entity.getY(), entity.getZ(), itemHandler);
	}

	public static void dropContents(Level level, double x, double y, double z, IItemHandler itemHandler) {
		if (level.isClientSide()) return;

		for (int slot = 0; slot < itemHandler.getSlots(); slot++) {
			Containers.dropItemStack(level, x, y, z, itemHandler.extractItem(slot, Integer.MAX_VALUE, false));
		}
	}

	public static boolean doesItemFit(IItemHandler itemHandler, int index, ItemStack stack) {
		if (!itemHandler.isItemValid(index, stack)) return false;
		ItemStack remainder = itemHandler.insertItem(index, stack, true);
		return remainder.isEmpty();
	}

	public static boolean doesItemFit(IItemHandler itemHandler, ItemStack stack) {
		for (int i = 0; i < itemHandler.getSlots(); i++) {
			if (!itemHandler.isItemValid(i, stack)) continue;
			stack = itemHandler.insertItem(i, stack, true);
			if (stack.isEmpty()) return true;
		}
		return false;
	}

	public static ItemStack insertItem(IItemHandler itemHandler, int slot, ItemStack stack) {
		return itemHandler.insertItem(slot, stack, false);
	}

	public static ItemStack insertItem(IItemHandler itemHandler, ItemStack stack) {
		return insertItem(itemHandler, stack, false);
	}

	public static ItemStack insertItem(IItemHandler itemHandler, ItemStack stack, boolean simulate) {
		if (stack.isEmpty()) return stack;

		for (int i = 0; i < itemHandler.getSlots(); i++) {
			stack = itemHandler.insertItem(i, stack, simulate);
			if (stack.isEmpty()) return ItemStack.EMPTY;
		}
		return stack;
	}

	public static boolean doAllItemsFit(IItemHandler itemHandler, List<ItemStack> items) {
		InsertSimulation simulation = new InsertSimulation(itemHandler);
		return simulation.insertAllItems(items);
	}

	private static class InsertSimulation {

		private final IItemHandler itemHandler;
		int slots;
		int[] availableSlotSpace;
		ItemStack[] itemInSlot;

		private InsertSimulation(IItemHandler itemHandler) {
			this.itemHandler = itemHandler;

			slots = itemHandler.getSlots();
			availableSlotSpace = new int[slots];
			itemInSlot = new ItemStack[slots];

			for (int i = 0; i < slots; i++) {
				ItemStack stack = itemHandler.getStackInSlot(i);
				itemInSlot[i] = stack;
				availableSlotSpace[i] = itemHandler.getSlotLimit(i) - stack.getCount();
			}
		}

		private boolean canInsertItem(int index, ItemStack stack) {
			if (stack.isEmpty() || availableSlotSpace[index] <= 0) return false;
			if (!itemHandler.isItemValid(index, stack)) return false;
			if (itemInSlot[index].isEmpty()) return true;
			return ItemHandlerHelper.canItemStacksStack(stack, itemInSlot[index]);
		}

		private ItemStack insertItem(int index, ItemStack stack) {
			if (!canInsertItem(index, stack)) return stack;

			int insertAmount = Math.min(availableSlotSpace[index], stack.getCount());
			availableSlotSpace[index] -= insertAmount;

			if (itemInSlot[index].isEmpty()) itemInSlot[index] = stack;

			return stack.copyWithCount(stack.getCount() - insertAmount); //remainder
		}

		private boolean insertAllItems(List<ItemStack> items) {
			for (ItemStack stack : items) {
				if (stack.isEmpty()) continue;

				for (int i = 0; i < slots; i++) {
					stack = insertItem(i, stack); //override stack with remainder
				}

				if (!stack.isEmpty()) return false;
			}

			return true;
		}

	}

}

