package com.github.elenterius.biomancy.world.inventory;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemStackHandler;

import java.util.function.Predicate;
import java.util.function.UnaryOperator;

public class BehavioralInventory<T extends IItemHandler & IItemHandlerModifiable & INBTSerializable<CompoundTag>> extends BaseInventory<T> {

	private final T behavioralItemHandler;

	BehavioralInventory(int slotAmount) {
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

	BehavioralInventory(int slotAmount, UnaryOperator<T> operator) {
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

	BehavioralInventory(int slotAmount, UnaryOperator<T> operator, Predicate<Player> canPlayerAccessInventory, Notify markDirtyNotifier) {
		this(slotAmount, operator);
		this.canPlayerAccessInventory = canPlayerAccessInventory;
		this.markDirtyNotifier = markDirtyNotifier;
	}

	BehavioralInventory(int slotAmount, Predicate<Player> canPlayerAccessInventory, Notify markDirtyNotifier) {
		this(slotAmount);
		this.canPlayerAccessInventory = canPlayerAccessInventory;
		this.markDirtyNotifier = markDirtyNotifier;
	}

	public static <T extends IItemHandler & IItemHandlerModifiable & INBTSerializable<CompoundTag>> BehavioralInventory<T> createServerContents(int slotAmount, UnaryOperator<T> operator, Predicate<Player> canPlayerAccessInventory, Notify markDirtyNotifier) {
		return new BehavioralInventory<>(slotAmount, operator, canPlayerAccessInventory, markDirtyNotifier);
	}

	public static <T extends IItemHandler & IItemHandlerModifiable & INBTSerializable<CompoundTag>> BehavioralInventory<T> createServerContents(int slotAmount, Predicate<Player> canPlayerAccessInventory, Notify markDirtyNotifier) {
		return new BehavioralInventory<>(slotAmount, canPlayerAccessInventory, markDirtyNotifier);
	}

	public static <T extends IItemHandler & IItemHandlerModifiable & INBTSerializable<CompoundTag>> BehavioralInventory<T> createClientContents(int slotAmount) {
		return new BehavioralInventory<>(slotAmount);
	}

	public T getItemHandlerWithBehavior() {
		return behavioralItemHandler;
	}

	@Override
	public void revive() {
		optionalItemHandler = LazyOptional.of(() -> behavioralItemHandler);
	}

}
