package com.github.elenterius.biomancy.inventory.itemhandler.behavior;

import com.github.elenterius.biomancy.block.FleshChestBlock;
import com.github.elenterius.biomancy.block.GulgeBlock;
import com.github.elenterius.biomancy.util.BiofuelUtil;
import net.minecraft.block.Block;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;

import java.util.function.Predicate;

public final class ItemHandlerBehavior {

	public static LazyOptional<IItemHandler> standard(ItemStackHandler itemStackHandler) {
		return LazyOptional.of(() -> itemStackHandler);
	}

	/**
	 * prevents item insertion, only item extraction is possible (e.g. output inventories)
	 */
	public static LazyOptional<IItemHandler> denyInput(ItemStackHandler itemStackHandler) {
		return LazyOptional.of(() -> new DenyInputItemHandler<>(itemStackHandler));
	}

	/**
	 * only allows item insertion of valid items
	 */
	public static LazyOptional<IItemHandler> filterInput(ItemStackHandler itemStackHandler, Predicate<ItemStack> validItems) {
		return LazyOptional.of(() -> new FilteredInputItemHandler<>(itemStackHandler, validItems));
	}

	public static final Predicate<ItemStack> EMPTY_ITEM_INVENTORY_PREDICATE = stack -> {
		if (stack.getItem() instanceof BlockItem) {
			Block block = ((BlockItem) stack.getItem()).getBlock();
			if (block instanceof ShulkerBoxBlock) {
				return stack.getChildTag("BlockEntityTag") == null;
			}
			else if (block instanceof FleshChestBlock || block instanceof GulgeBlock) {
				CompoundNBT nbt = stack.getChildTag("BlockEntityTag");
				if (nbt == null) return true;
				return nbt.getCompound("Inventory").isEmpty();
			}
		}

		LazyOptional<IItemHandler> capability = stack.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY);
		final boolean[] isEmpty = {true};
		capability.ifPresent(itemHandler -> {
			int slots = itemHandler.getSlots();
			for (int i = 0; i < slots; i++) {
				if (!itemHandler.getStackInSlot(i).isEmpty()) {
					isEmpty[0] = false;
					break;
				}
			}
		});
		return isEmpty[0];
	};

	/**
	 * prevents nesting of items with inventories,<br>
	 * i.e. insertion of filled shulker boxes and items with filled inventories (item handler capability)
	 */
	public static LazyOptional<IItemHandler> denyItemWithFilledInventory(ItemStackHandler itemStackHandler) {
		return LazyOptional.of(() -> new FilteredInputItemHandler<>(itemStackHandler, EMPTY_ITEM_INVENTORY_PREDICATE));
	}

	public static final Predicate<ItemStack> FILLED_FLUID_ITEM_PREDICATE = stack -> FluidUtil.getFluidContained(stack).isPresent();

	/**
	 * only allows the insertion of items that contain any fluid (fluid handler capability), e.g. water buckets
	 */
	public static LazyOptional<IItemHandler> filterFilledFluidContainer(ItemStackHandler itemStackHandler) {
		return LazyOptional.of(() -> new FilteredInputItemHandler<>(itemStackHandler, FILLED_FLUID_ITEM_PREDICATE));
	}

	/**
	 * only allows the insertion of items that are biofuel (solid & fluid)
	 */
	public static LazyOptional<IItemHandler> filterBiofuel(ItemStackHandler itemStackHandler) {
		return LazyOptional.of(() -> new FilteredInputItemHandler<>(itemStackHandler, BiofuelUtil::isItemValidFuel));
	}

}
