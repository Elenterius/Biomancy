package com.github.elenterius.blightlings.inventory;

import com.github.elenterius.blightlings.capabilities.LargeSingleItemStackHandler;
import com.github.elenterius.blightlings.capabilities.SingleItemStackHandler;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.common.util.LazyOptional;

import java.util.function.Consumer;
import java.util.function.Predicate;

public class GulgeContents implements IInventory {

	public final LazyOptional<LargeSingleItemStackHandler> itemHandler;
	private Predicate<PlayerEntity> canPlayerAccessInventory = x -> true;
	private Notify markDirtyNotifier = () -> {};
	private Consumer<PlayerEntity> onOpenInventory = (player) -> {};
	private Consumer<PlayerEntity> closeInventoryNotifier = (player) -> {};

	private GulgeContents(short maxItemAmount) {
		itemHandler = LazyOptional.of(() -> new LargeSingleItemStackHandler(maxItemAmount));
	}

	private GulgeContents(short maxItemAmount, Predicate<PlayerEntity> canPlayerAccessInventory, Notify markDirtyNotifier) {
		this(maxItemAmount);
		this.canPlayerAccessInventory = canPlayerAccessInventory;
		this.markDirtyNotifier = markDirtyNotifier;
	}

	public static GulgeContents createServerContents(short maxItemAmount, Predicate<PlayerEntity> canPlayerAccessInventory, Notify markDirtyNotificationLambda) {
		return new GulgeContents(maxItemAmount, canPlayerAccessInventory, markDirtyNotificationLambda);
	}

	public static GulgeContents createClientContents(short maxItemAmount) {
		return new GulgeContents(maxItemAmount);
	}

	public CompoundNBT serializeNBT() {
		return itemHandler.map(LargeSingleItemStackHandler::serializeNBT).orElse(new CompoundNBT());
	}

	public void deserializeNBT(CompoundNBT nbt) {
		itemHandler.ifPresent(handler -> handler.deserializeNBT(nbt));
	}

	@Override
	public void markDirty() {
		markDirtyNotifier.invoke();
	}

	@Override
	public void openInventory(PlayerEntity player) {
		onOpenInventory.accept(player);
	}

	@Override
	public void closeInventory(PlayerEntity player) {
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
	public boolean isUsableByPlayer(PlayerEntity player) {
		return canPlayerAccessInventory.test(player);
	}

	@Override
	public boolean isItemValidForSlot(int index, ItemStack stack) {
		return itemHandler.map(handler -> handler.isItemValid(index, stack)).orElse(false);
	}

	@Override
	public int getSizeInventory() {
		return itemHandler.map(SingleItemStackHandler::getSlots).orElse(0);
	}

	@Override
	public boolean isEmpty() {
		return itemHandler.map(SingleItemStackHandler::isEmpty).orElse(true); //suboptimal
	}

	@Override
	public ItemStack getStackInSlot(int index) {
		return itemHandler.map(handler -> handler.getStackInSlot(index)).orElse(ItemStack.EMPTY); //suboptimal
	}

	@Override
	public ItemStack decrStackSize(int index, int count) {
		return itemHandler.map(handler -> handler.extractItem(index, count, false)).orElse(ItemStack.EMPTY);
	}

	@Override
	public ItemStack removeStackFromSlot(int index) {
		return itemHandler.map(handler -> handler.extractItem(index, handler.getMaxAmount(), false)).orElse(ItemStack.EMPTY);
	}

	@Override
	public void setInventorySlotContents(int index, ItemStack stack) {
		itemHandler.ifPresent(handler -> handler.setStackInSlot(index, stack));
	}

	@Override
	public void clear() {
		itemHandler.ifPresent(handler -> handler.setStackInSlot(0, ItemStack.EMPTY));
	}

	@FunctionalInterface
	public interface Notify {
		void invoke();
	}

}
