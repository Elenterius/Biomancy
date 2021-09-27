package com.github.elenterius.biomancy.inventory;

import com.github.elenterius.biomancy.inventory.itemhandler.LargeSingleItemStackHandler;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.IIntArray;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;

import java.util.function.Consumer;
import java.util.function.Predicate;

public class GulgeInventory implements IInventory, IIntArray {

	private final LargeSingleItemStackHandler itemStackHandler;
	private final LazyOptional<IItemHandler> optionalItemStackHandler;

	private Predicate<PlayerEntity> canPlayerAccessInventory = x -> true;
	private Notify markDirtyNotifier = () -> {};
	private Consumer<PlayerEntity> onOpenInventory = (player) -> {};
	private Consumer<PlayerEntity> closeInventoryNotifier = (player) -> {};

	private GulgeInventory(short maxItemAmount) {
		itemStackHandler = new LargeSingleItemStackHandler(maxItemAmount);
		optionalItemStackHandler = LazyOptional.of(() -> itemStackHandler);
	}

	private GulgeInventory(short maxItemAmount, Predicate<PlayerEntity> canPlayerAccessInventory, Notify markDirtyNotifier) {
		this(maxItemAmount);
		this.canPlayerAccessInventory = canPlayerAccessInventory;
		this.markDirtyNotifier = markDirtyNotifier;
	}

	public static GulgeInventory createServerContents(short maxItemAmount, Predicate<PlayerEntity> canPlayerAccessInventory, Notify markDirtyNotifier) {
		return new GulgeInventory(maxItemAmount, canPlayerAccessInventory, markDirtyNotifier);
	}

	public static GulgeInventory createClientContents(short maxItemAmount) {
		return new GulgeInventory(maxItemAmount);
	}

	public CompoundNBT serializeNBT() {
		return itemStackHandler.serializeNBT();
	}

	public void deserializeNBT(CompoundNBT nbt) {
		itemStackHandler.deserializeNBT(nbt);
	}

	@Override
	public void setChanged() {
		markDirtyNotifier.invoke();
	}

	public LazyOptional<IItemHandler> getOptionalItemStackHandler() {
		return optionalItemStackHandler;
	}

	@Override
	public void startOpen(PlayerEntity player) {
		onOpenInventory.accept(player);
	}

	@Override
	public void stopOpen(PlayerEntity player) {
		closeInventoryNotifier.accept(player);
	}

	public void setCanPlayerAccessInventory(Predicate<PlayerEntity> predicate) {
		canPlayerAccessInventory = predicate;
	}

	public void setMarkDirtyNotifier(Notify callback) {
		markDirtyNotifier = callback;
	}

	public void setOpenInventoryConsumer(Consumer<PlayerEntity> consumer) {
		onOpenInventory = consumer;
	}

	public void setCloseInventoryConsumer(Consumer<PlayerEntity> consumer) {
		closeInventoryNotifier = consumer;
	}

	// vanilla container stuff for manipulating the inventory

	@Override
	public boolean stillValid(PlayerEntity player) {
		return canPlayerAccessInventory.test(player);
	}

	@Override
	public boolean canPlaceItem(int index, ItemStack stack) {
		return itemStackHandler.isItemValid(index, stack);
	}

	@Override
	public int getContainerSize() {
		return itemStackHandler.getSlots();
	}

	@Override
	public boolean isEmpty() {
		return itemStackHandler.isEmpty();
	}

	@Override
	public int getMaxStackSize() {
		return itemStackHandler.getMaxAmount();
	}

	@Override
	public ItemStack getItem(int index) {
		return itemStackHandler.getStackInSlot(index);
	}

	@Override
	public ItemStack removeItem(int index, int count) {
		return itemStackHandler.extractItem(index, count, false);
	}

	@Override
	public ItemStack removeItemNoUpdate(int index) {
		return itemStackHandler.extractItem(index, itemStackHandler.getMaxAmount(), false);
	}

	@Override
	public void setItem(int index, ItemStack stack) {
		itemStackHandler.setStackInSlot(index, stack);
	}

	@Override
	public void clearContent() {
		itemStackHandler.setStackInSlot(0, ItemStack.EMPTY);
	}

	private void validateTrackingIndex(int index) {
		if (index < 0 || index >= getCount()) throw new IndexOutOfBoundsException("Index out of bounds:" + index);
	}

	/**
	 * get tracked value
	 *
	 * @param index tracking-index
	 * @return tracked value by index
	 */
	@Override
	public int get(int index) {
		validateTrackingIndex(index);
		return itemStackHandler.getAmount();
	}

	/**
	 * update value of tracked value
	 *
	 * @param index tracking-index
	 */
	@Override
	public void set(int index, int value) {
		validateTrackingIndex(index);
		itemStackHandler.setAmount((short) value);
	}

	/**
	 * @return tracking IntArray Size
	 */
	@Override
	public int getCount() {
		return 1;
	}
}
