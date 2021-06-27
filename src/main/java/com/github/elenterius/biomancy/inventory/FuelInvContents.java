package com.github.elenterius.biomancy.inventory;

import com.github.elenterius.biomancy.capabilities.InputFilterItemStackHandler;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;

import java.util.function.Predicate;

public class FuelInvContents extends SimpleInvContents {

	FuelInvContents(int slotAmount) {
		super(slotAmount);
	}

	FuelInvContents(ItemStackHandler itemStackHandler, LazyOptional<IItemHandler> optionalItemStackHandler, Predicate<PlayerEntity> canPlayerAccessInventory, Notify markDirtyNotifier) {
		super(itemStackHandler, optionalItemStackHandler, canPlayerAccessInventory, markDirtyNotifier);
	}

	public static FuelInvContents createServerContents(int slotAmount, Predicate<ItemStack> isItemValid, Predicate<PlayerEntity> canPlayerAccessInventory, Notify markDirtyNotifier) {
		ItemStackHandler itemStackHandler = new ItemStackHandler(slotAmount);
		return new FuelInvContents(itemStackHandler, LazyOptional.of(() -> new InputFilterItemStackHandler(itemStackHandler, isItemValid)), canPlayerAccessInventory, markDirtyNotifier);
	}

	public static FuelInvContents createClientContents(int slotAmount) {
		return new FuelInvContents(slotAmount);
	}

}
