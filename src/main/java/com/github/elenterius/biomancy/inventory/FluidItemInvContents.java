package com.github.elenterius.biomancy.inventory;

import com.github.elenterius.biomancy.handler.item.InputFilterItemStackHandler;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;

import java.util.function.Predicate;

public class FluidItemInvContents extends SimpleInvContents {

	public static final Predicate<ItemStack> VALID_FLUID_ITEM = stack -> FluidUtil.getFluidContained(stack).isPresent();

	FluidItemInvContents(int slotAmount) {
		super(slotAmount);
	}

	FluidItemInvContents(ItemStackHandler itemStackHandler, LazyOptional<IItemHandler> optionalItemStackHandler, Predicate<PlayerEntity> canPlayerAccessInventory, Notify markDirtyNotifier) {
		super(itemStackHandler, optionalItemStackHandler, canPlayerAccessInventory, markDirtyNotifier);
	}

	public static FluidItemInvContents createServerContents(int slotAmount, Predicate<ItemStack> validFluidItem, Predicate<PlayerEntity> canPlayerAccessInventory, Notify markDirtyNotifier) {
		ItemStackHandler itemStackHandler = new ItemStackHandler(slotAmount);
		return new FluidItemInvContents(itemStackHandler, LazyOptional.of(() -> new InputFilterItemStackHandler(itemStackHandler, validFluidItem)), canPlayerAccessInventory, markDirtyNotifier);
	}

	public static FluidItemInvContents createServerContents(int slotAmount, Predicate<PlayerEntity> canPlayerAccessInventory, Notify markDirtyNotifier) {
		ItemStackHandler itemStackHandler = new ItemStackHandler(slotAmount);
		return new FluidItemInvContents(itemStackHandler, LazyOptional.of(() -> new InputFilterItemStackHandler(itemStackHandler, VALID_FLUID_ITEM)), canPlayerAccessInventory, markDirtyNotifier);
	}

	public static FluidItemInvContents createClientContents(int slotAmount) {
		return new FluidItemInvContents(slotAmount);
	}

}
