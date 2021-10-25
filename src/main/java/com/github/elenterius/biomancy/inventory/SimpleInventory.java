package com.github.elenterius.biomancy.inventory;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemStackHandler;

import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;

public class SimpleInventory<ISH extends IItemHandler & IItemHandlerModifiable & INBTSerializable<CompoundNBT>> implements IInventory {

	private final ISH itemStackHandler;
	private final LazyOptional<IItemHandler> optionalItemStackHandler;

	private Predicate<PlayerEntity> canPlayerAccessInventory = x -> true;

	private Notify markDirtyNotifier = () -> {};

	private Consumer<PlayerEntity> onOpenInventory = player -> {};
	private Consumer<PlayerEntity> onCloseInventory = player -> {};

	SimpleInventory(int slotAmount) {
		//noinspection unchecked
		itemStackHandler = (ISH) new ItemStackHandler(slotAmount) {
			@Override
			protected void onContentsChanged(int slot) {
				super.onContentsChanged(slot);
				setChanged();
			}
		};
		optionalItemStackHandler = LazyOptional.of(() -> itemStackHandler);
	}

	SimpleInventory(ISH itemStackHandlerIn) {
		itemStackHandler = itemStackHandlerIn;
		optionalItemStackHandler = LazyOptional.of(() -> itemStackHandler);
	}

	SimpleInventory(int slotAmount, UnaryOperator<ISH> operator) {
		//noinspection unchecked
		ISH ish = (ISH) new ItemStackHandler(slotAmount) {
			@Override
			protected void onContentsChanged(int slot) {
				super.onContentsChanged(slot);
				setChanged();
			}
		};
		itemStackHandler = operator.apply(ish);
		optionalItemStackHandler = LazyOptional.of(() -> itemStackHandler);
	}

	SimpleInventory(ISH itemStackHandlerIn, Predicate<PlayerEntity> canPlayerAccessInventory, Notify markDirtyNotifier) {
		this(itemStackHandlerIn);
		this.canPlayerAccessInventory = canPlayerAccessInventory;
		this.markDirtyNotifier = markDirtyNotifier;
	}

	SimpleInventory(int slotAmount, UnaryOperator<ISH> operator, Predicate<PlayerEntity> canPlayerAccessInventory, Notify markDirtyNotifier) {
		this(slotAmount, operator);
		this.canPlayerAccessInventory = canPlayerAccessInventory;
		this.markDirtyNotifier = markDirtyNotifier;
	}

	SimpleInventory(int slotAmount, Predicate<PlayerEntity> canPlayerAccessInventory, Notify markDirtyNotifier) {
		this(slotAmount);
		this.canPlayerAccessInventory = canPlayerAccessInventory;
		this.markDirtyNotifier = markDirtyNotifier;
	}

	public static <ISH extends IItemHandler & IItemHandlerModifiable & INBTSerializable<CompoundNBT>> SimpleInventory<ISH> createServerContents(ISH itemStackHandlerIn, Predicate<PlayerEntity> canPlayerAccessInventory, Notify markDirtyNotifier) {
		return new SimpleInventory<>(itemStackHandlerIn, canPlayerAccessInventory, markDirtyNotifier);
	}

	public static <ISH extends IItemHandler & IItemHandlerModifiable & INBTSerializable<CompoundNBT>> SimpleInventory<ISH> createServerContents(int slotAmount, UnaryOperator<ISH> operator, Predicate<PlayerEntity> canPlayerAccessInventory, Notify markDirtyNotifier) {
		return new SimpleInventory<>(slotAmount, operator, canPlayerAccessInventory, markDirtyNotifier);
	}

	public static <ISH extends IItemHandler & IItemHandlerModifiable & INBTSerializable<CompoundNBT>> SimpleInventory<ISH> createServerContents(int slotAmount, Predicate<PlayerEntity> canPlayerAccessInventory, Notify markDirtyNotifier) {
		return new SimpleInventory<>(slotAmount, canPlayerAccessInventory, markDirtyNotifier);
	}

	public static <ISH extends IItemHandler & IItemHandlerModifiable & INBTSerializable<CompoundNBT>> SimpleInventory<ISH> createClientContents(int slotAmount) {
		return new SimpleInventory<>(slotAmount);
	}

	public static <ISH extends IItemHandler & IItemHandlerModifiable & INBTSerializable<CompoundNBT>> SimpleInventory<ISH> createClientContents(ISH itemStackHandlerIn) {
		return new SimpleInventory<>(itemStackHandlerIn);
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

	@Override
	public void startOpen(PlayerEntity player) {
		onOpenInventory.accept(player);
	}

	@Override
	public void stopOpen(PlayerEntity player) {
		onCloseInventory.accept(player);
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
		onCloseInventory = consumer;
	}

	@Override
	public boolean stillValid(PlayerEntity player) {
		return canPlayerAccessInventory.test(player);
	}

	@Override
	public boolean canPlaceItem(int index, ItemStack stack) {
		return itemStackHandler.isItemValid(index, stack);
	}

	public ItemStack insertItemStack(int index, ItemStack insertStack) {
		return itemStackHandler.insertItem(index, insertStack, false);
	}

	public boolean doesItemStackFit(int index, ItemStack insertStack) {
		ItemStack remainder = itemStackHandler.insertItem(index, insertStack, true);
		return remainder.isEmpty();
	}

	@Override
	public int getContainerSize() {
		return itemStackHandler.getSlots();
	}

	@Override
	public int getMaxStackSize() {
		return itemStackHandler.getSlotLimit(0);
	}

	@Override
	public boolean isEmpty() {
		for (int i = 0; i < itemStackHandler.getSlots(); ++i) {
			if (!itemStackHandler.getStackInSlot(i).isEmpty()) return false;
		}
		return true;
	}

	@Override
	public ItemStack getItem(int index) {
		return itemStackHandler.getStackInSlot(index);
	}

	@Override
	public ItemStack removeItem(int index, int count) {
		if (count < 0) throw new IllegalArgumentException("count should be >= 0:" + count);
		return itemStackHandler.extractItem(index, count, false);
	}

	@Override
	public ItemStack removeItemNoUpdate(int index) {
		int maxPossibleItemStackSize = itemStackHandler.getSlotLimit(index);
		return itemStackHandler.extractItem(index, maxPossibleItemStackSize, false);
	}

	@Override
	public void setItem(int index, ItemStack stack) {
		itemStackHandler.setStackInSlot(index, stack);
	}

	@Override
	public void clearContent() {
		for (int i = 0; i < itemStackHandler.getSlots(); ++i) {
			itemStackHandler.setStackInSlot(i, ItemStack.EMPTY);
		}
	}

	public ISH getItemHandler() {
		return itemStackHandler;
	}

	public LazyOptional<IItemHandler> getOptionalItemStackHandler() {
		return optionalItemStackHandler;
	}

}
