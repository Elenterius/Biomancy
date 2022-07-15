package com.github.elenterius.biomancy.world.inventory;

import com.github.elenterius.biomancy.world.inventory.itemhandler.LargeSingleItemStackHandler;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.util.LazyOptional;

import java.util.function.Predicate;

public class GulgeInventory extends BaseInventory<LargeSingleItemStackHandler> {

	private final BigItemData bigItemData = new BigItemData();

	private GulgeInventory(short maxItemAmount) {
		itemHandler = new LargeSingleItemStackHandler(maxItemAmount) {
			@Override
			protected void onContentsChanged() {
				super.onContentsChanged();
				setChanged();
			}
		};
		optionalItemHandler = LazyOptional.of(() -> itemHandler);
	}

	private GulgeInventory(short maxItemAmount, Predicate<Player> canPlayerAccessInventory, Notify markDirtyNotifier) {
		this(maxItemAmount);
		this.canPlayerAccessInventory = canPlayerAccessInventory;
		this.markDirtyNotifier = markDirtyNotifier;
	}

	public static GulgeInventory createServerContents(short maxItemAmount, Predicate<Player> canPlayerAccessInventory, Notify markDirtyNotifier) {
		return new GulgeInventory(maxItemAmount, canPlayerAccessInventory, markDirtyNotifier);
	}

	public static GulgeInventory createClientContents(short maxItemAmount) {
		return new GulgeInventory(maxItemAmount);
	}

	@Override
	public void deserializeNBT(CompoundTag tag) {
		super.deserializeNBT(tag);
		bigItemData.count = itemHandler.getAmount();
	}

	@Override
	public void setChanged() {
		bigItemData.count = itemHandler.getAmount();
		super.setChanged();
	}

	@Override
	public int getMaxStackSize() {
		return itemHandler.getMaxAmount();
	}

	@Override
	public boolean isEmpty() {
		return itemHandler.isEmpty();
	}

	@Override
	public boolean isFull() {
		return itemHandler.isFull();
	}

	@Override
	public boolean doesItemStackFit(ItemStack stack) {
		ItemStack remainder = itemHandler.insertItem(0, stack, true);
		return remainder.isEmpty();
	}

	@Override
	public ItemStack insertItemStack(ItemStack stack) {
		return itemHandler.insertItem(0, stack, false);
	}

	@Override
	public ItemStack removeItemNoUpdate(int index) {
		return itemHandler.extractItem(index, itemHandler.getMaxAmount(), false);
	}

	@Override
	public void clearContent() {
		itemHandler.setStackInSlot(0, ItemStack.EMPTY);
	}

	public BigItemData getBigItemData() {
		return bigItemData;
	}

	public static class BigItemData implements ContainerData {
		int count = 0;

		private void validateTrackingIndex(int index) {
			if (index < 0 || index >= getCount()) throw new IndexOutOfBoundsException("Index out of bounds:" + index);
		}

		public int getItemCount() {
			return count;
		}

		@Override
		public int get(int index) {
			validateTrackingIndex(index);
			return count;
		}

		@Override
		public void set(int index, int value) {
			validateTrackingIndex(index);
			count = value;
		}

		@Override
		public int getCount() {
			return 1;
		}
	}

}
