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

	private final ISH itemHandler;
	private final ISH behavioralItemHandler;
	private final LazyOptional<IItemHandler> optionalItemHandler;

	private Predicate<PlayerEntity> canPlayerAccessInventory = x -> true;

	private Notify markDirtyNotifier = () -> {};

	private Consumer<PlayerEntity> onOpenInventory = player -> {};
	private Consumer<PlayerEntity> onCloseInventory = player -> {};

	SimpleInventory(int slotAmount) {
		//noinspection unchecked
		itemHandler = (ISH) new ItemStackHandler(slotAmount) {
			@Override
			protected void onContentsChanged(int slot) {
				super.onContentsChanged(slot);
				setChanged();
			}
		};
		behavioralItemHandler = itemHandler;
		optionalItemHandler = LazyOptional.of(() -> behavioralItemHandler);
	}

	SimpleInventory(ISH itemStackHandlerIn) {
		itemHandler = itemStackHandlerIn;
		behavioralItemHandler = itemHandler;
		optionalItemHandler = LazyOptional.of(() -> behavioralItemHandler);
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
		itemHandler = ish;
		behavioralItemHandler = operator.apply(ish);
		optionalItemHandler = LazyOptional.of(() -> behavioralItemHandler);
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
		return itemHandler.serializeNBT();
	}

	public void deserializeNBT(CompoundNBT nbt) {
		itemHandler.deserializeNBT(nbt);
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
		return itemHandler.isItemValid(index, stack);
	}

	public ItemStack insertItemStack(int index, ItemStack insertStack) {
		return itemHandler.insertItem(index, insertStack, false);
	}

	public boolean doesItemStackFit(int index, ItemStack insertStack) {
		ItemStack remainder = itemHandler.insertItem(index, insertStack, true);
		return remainder.isEmpty();
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
		for (int i = 0; i < itemHandler.getSlots(); ++i) {
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

	public ISH getItemHandler() {
		return itemHandler;
	}

	public ISH getItemHandlerWithBehavior() {
		return behavioralItemHandler;
	}

	public LazyOptional<IItemHandler> getOptionalItemHandlerWithBehavior() {
		return optionalItemHandler;
	}

}
