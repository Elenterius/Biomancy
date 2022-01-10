package com.github.elenterius.biomancy.world.inventory;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemStackHandler;

import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;

public class SimpleInventory<T extends IItemHandler & IItemHandlerModifiable & INBTSerializable<CompoundTag>> implements Container {

	private final T itemHandler;
	private final T behavioralItemHandler;
	private LazyOptional<IItemHandler> optionalItemHandler;

	private Predicate<Player> canPlayerAccessInventory = x -> true;

	private Notify markDirtyNotifier = () -> {};

	private Consumer<Player> onOpenInventory = player -> {};
	private Consumer<Player> onCloseInventory = player -> {};

	SimpleInventory(int slotAmount) {
		//noinspection unchecked
		itemHandler = (T) new ItemStackHandler(slotAmount) {
			@Override
			protected void onContentsChanged(int slot) {
				super.onContentsChanged(slot);
				setChanged();
			}
		};
		behavioralItemHandler = itemHandler;
		optionalItemHandler = LazyOptional.of(() -> behavioralItemHandler);
	}

	SimpleInventory(T itemStackHandlerIn) {
		itemHandler = itemStackHandlerIn;
		behavioralItemHandler = itemHandler;
		optionalItemHandler = LazyOptional.of(() -> behavioralItemHandler);
	}

	SimpleInventory(int slotAmount, UnaryOperator<T> operator) {
		//noinspection unchecked
		T handler = (T) new ItemStackHandler(slotAmount) {
			@Override
			protected void onContentsChanged(int slot) {
				super.onContentsChanged(slot);
				setChanged();
			}
		};
		itemHandler = handler;
		behavioralItemHandler = operator.apply(handler);
		optionalItemHandler = LazyOptional.of(() -> behavioralItemHandler);
	}

	SimpleInventory(T itemStackHandlerIn, Predicate<Player> canPlayerAccessInventory, Notify markDirtyNotifier) {
		this(itemStackHandlerIn);
		this.canPlayerAccessInventory = canPlayerAccessInventory;
		this.markDirtyNotifier = markDirtyNotifier;
	}

	SimpleInventory(int slotAmount, UnaryOperator<T> operator, Predicate<Player> canPlayerAccessInventory, Notify markDirtyNotifier) {
		this(slotAmount, operator);
		this.canPlayerAccessInventory = canPlayerAccessInventory;
		this.markDirtyNotifier = markDirtyNotifier;
	}

	SimpleInventory(int slotAmount, Predicate<Player> canPlayerAccessInventory, Notify markDirtyNotifier) {
		this(slotAmount);
		this.canPlayerAccessInventory = canPlayerAccessInventory;
		this.markDirtyNotifier = markDirtyNotifier;
	}

	public static <T extends IItemHandler & IItemHandlerModifiable & INBTSerializable<CompoundTag>> SimpleInventory<T> createServerContents(T itemStackHandlerIn, Predicate<Player> canPlayerAccessInventory, Notify markDirtyNotifier) {
		return new SimpleInventory<>(itemStackHandlerIn, canPlayerAccessInventory, markDirtyNotifier);
	}

	public static <T extends IItemHandler & IItemHandlerModifiable & INBTSerializable<CompoundTag>> SimpleInventory<T> createServerContents(int slotAmount, UnaryOperator<T> operator, Predicate<Player> canPlayerAccessInventory, Notify markDirtyNotifier) {
		return new SimpleInventory<>(slotAmount, operator, canPlayerAccessInventory, markDirtyNotifier);
	}

	public static <T extends IItemHandler & IItemHandlerModifiable & INBTSerializable<CompoundTag>> SimpleInventory<T> createServerContents(int slotAmount, Predicate<Player> canPlayerAccessInventory, Notify markDirtyNotifier) {
		return new SimpleInventory<>(slotAmount, canPlayerAccessInventory, markDirtyNotifier);
	}

	public static <T extends IItemHandler & IItemHandlerModifiable & INBTSerializable<CompoundTag>> SimpleInventory<T> createClientContents(int slotAmount) {
		return new SimpleInventory<>(slotAmount);
	}

	public static <T extends IItemHandler & IItemHandlerModifiable & INBTSerializable<CompoundTag>> SimpleInventory<T> createClientContents(T itemStackHandlerIn) {
		return new SimpleInventory<>(itemStackHandlerIn);
	}

	public CompoundTag serializeNBT() {
		return itemHandler.serializeNBT();
	}

	public void deserializeNBT(CompoundTag nbt) {
		itemHandler.deserializeNBT(nbt);
	}

	@Override
	public void setChanged() {
		markDirtyNotifier.invoke();
	}

	@Override
	public void startOpen(Player player) {
		onOpenInventory.accept(player);
	}

	@Override
	public void stopOpen(Player player) {
		onCloseInventory.accept(player);
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
		onCloseInventory = consumer;
	}

	@Override
	public boolean stillValid(Player player) {
		return canPlayerAccessInventory.test(player);
	}

	@Override
	public boolean canPlaceItem(int index, ItemStack stack) {
		return itemHandler.isItemValid(index, stack);
	}

	public ItemStack insertItemStack(int index, ItemStack insertStack) {
		return itemHandler.insertItem(index, insertStack, false);
	}

	public boolean doesItemStackFit(int index, ItemStack insertStack) {
		ItemStack remainder = itemHandler.insertItem(index, insertStack, true);
		return remainder.isEmpty();
	}

	public boolean doesItemStackFit(ItemStack insertStack) {
		for (int i = 0; i < itemHandler.getSlots(); i++) {
			insertStack = itemHandler.insertItem(i, insertStack, true);
			if (insertStack.isEmpty()) return true;
		}
		return false;
	}

	@Override
	public int getContainerSize() {
		return itemHandler.getSlots();
	}

	@Override
	public int getMaxStackSize() {
		return itemHandler.getSlotLimit(0);
	}

	@Override
	public boolean isEmpty() {
		for (int i = 0; i < itemHandler.getSlots(); i++) {
			if (!itemHandler.getStackInSlot(i).isEmpty()) return false;
		}
		return true;
	}

	@Override
	public ItemStack getItem(int index) {
		return itemHandler.getStackInSlot(index);
	}

	@Override
	public ItemStack removeItem(int index, int count) {
		if (count < 0) throw new IllegalArgumentException("count should be >= 0:" + count);
		return itemHandler.extractItem(index, count, false);
	}

	@Override
	public ItemStack removeItemNoUpdate(int index) {
		int maxPossibleItemStackSize = itemHandler.getSlotLimit(index);
		return itemHandler.extractItem(index, maxPossibleItemStackSize, false);
	}

	@Override
	public void setItem(int index, ItemStack stack) {
		itemHandler.setStackInSlot(index, stack);
	}

	@Override
	public void clearContent() {
		for (int i = 0; i < itemHandler.getSlots(); ++i) {
			itemHandler.setStackInSlot(i, ItemStack.EMPTY);
		}
	}

	public T getItemHandler() {
		return itemHandler;
	}

	public T getItemHandlerWithBehavior() {
		return behavioralItemHandler;
	}

	public LazyOptional<IItemHandler> getOptionalItemHandlerWithBehavior() {
		return optionalItemHandler;
	}

	public void invalidate() {
		optionalItemHandler.invalidate();
	}

	public void revive() {
		optionalItemHandler = LazyOptional.of(() -> behavioralItemHandler);
	}

}
