package com.github.elenterius.biomancy.world.inventory;

import com.github.elenterius.biomancy.world.inventory.itemhandler.LargeSingleItemStackHandler;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;

import java.util.function.Consumer;
import java.util.function.Predicate;

public class GulgeInventory implements Container {

	private final LargeSingleItemStackHandler itemHandler;
	private LazyOptional<IItemHandler> optionalItemHandler;

	private Predicate<Player> canPlayerAccessInventory = x -> true;
	private Notify markDirtyNotifier = () -> {};
	private Consumer<Player> onOpenInventory = player -> {};
	private Consumer<Player> closeInventoryNotifier = player -> {};

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

	public CompoundTag serializeNBT() {
		return itemHandler.serializeNBT();
	}

	public void deserializeNBT(CompoundTag nbt) {
		itemHandler.deserializeNBT(nbt);
		bigItemData.count = itemHandler.getAmount();
	}

	@Override
	public void setChanged() {
		bigItemData.count = itemHandler.getAmount();
		markDirtyNotifier.invoke();
	}

	public LazyOptional<IItemHandler> getOptionalItemHandler() {
		return optionalItemHandler;
	}

	public BigItemData getBigItemData() {
		return bigItemData;
	}

	public void invalidate() {
		optionalItemHandler.invalidate();
	}

	public void revive() {
		optionalItemHandler = LazyOptional.of(() -> itemHandler);
	}

	@Override
	public void startOpen(Player player) {
		onOpenInventory.accept(player);
	}

	@Override
	public void stopOpen(Player player) {
		closeInventoryNotifier.accept(player);
	}

	public void setCanPlayerAccessInventory(Predicate<Player> predicate) {
		canPlayerAccessInventory = predicate;
	}

	public void setMarkDirtyNotifier(Notify callback) {
		markDirtyNotifier = callback;
	}

	public void setOpenInventoryConsumer(Consumer<Player> consumer) {
		onOpenInventory = consumer;
	}

	public void setCloseInventoryConsumer(Consumer<Player> consumer) {
		closeInventoryNotifier = consumer;
	}

	@Override
	public boolean stillValid(Player player) {
		return canPlayerAccessInventory.test(player);
	}

	@Override
	public boolean canPlaceItem(int index, ItemStack stack) {
		return itemHandler.isItemValid(index, stack);
	}

	@Override
	public int getContainerSize() {
		return itemHandler.getSlots();
	}

	@Override
	public boolean isEmpty() {
		return itemHandler.isEmpty();
	}

	@Override
	public int getMaxStackSize() {
		return itemHandler.getMaxAmount();
	}

	@Override
	public ItemStack getItem(int index) {
		return itemHandler.getStackInSlot(index);
	}

	@Override
	public ItemStack removeItem(int index, int count) {
		return itemHandler.extractItem(index, count, false);
	}

	@Override
	public ItemStack removeItemNoUpdate(int index) {
		return itemHandler.extractItem(index, itemHandler.getMaxAmount(), false);
	}

	@Override
	public void setItem(int index, ItemStack stack) {
		itemHandler.setStackInSlot(index, stack);
	}

	@Override
	public void clearContent() {
		itemHandler.setStackInSlot(0, ItemStack.EMPTY);
	}

	static class BigItemData implements ContainerData {
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
