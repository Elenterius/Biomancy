package com.github.elenterius.biomancy.inventory;

import com.github.elenterius.biomancy.block.FleshChestBlock;
import com.github.elenterius.biomancy.block.GulgeBlock;
import com.github.elenterius.biomancy.inventory.fluidhandler.FluidHandlerDelegator;
import com.github.elenterius.biomancy.inventory.itemhandler.ItemHandlerDelegator;
import com.github.elenterius.biomancy.util.BiofuelUtil;
import net.minecraft.block.Block;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;

import java.util.function.Predicate;

/**
 * allows us to add specific behavior to any IItemHandler or IFluidHandler without the need of subclassing
 */
public final class HandlerBehaviors {

	private HandlerBehaviors() {}

	/**
	 * default item handler behavior
	 */
	public static <ISH extends IItemHandler & IItemHandlerModifiable & INBTSerializable<CompoundNBT>> LazyOptional<IItemHandler> standard(ISH itemStackHandler) {
		return LazyOptional.of(() -> itemStackHandler);
	}

	/**
	 * default fluid handler behavior
	 */
	public static LazyOptional<IFluidHandler> standard(IFluidHandler fluidHandler) {
		return LazyOptional.of(() -> fluidHandler);
	}

	/**
	 * prevents item insertion, only item extraction is possible (e.g. output inventories)
	 */
	public static <ISH extends IItemHandler & IItemHandlerModifiable & INBTSerializable<CompoundNBT>> LazyOptional<IItemHandler> denyInput(ISH itemStackHandler) {
		return LazyOptional.of(() -> new ItemHandlerDelegator.DenyInput<>(itemStackHandler));
	}

	/**
	 * prevents fluid insertion, only fluid extraction is possible (e.g. output tank)
	 */
	public static LazyOptional<IFluidHandler> denyInput(IFluidHandler fluidHandler) {
		return LazyOptional.of(() -> new FluidHandlerDelegator.DenyInput<>(fluidHandler));
	}

	/**
	 * only allows item insertion of valid items
	 */
	public static <ISH extends IItemHandler & IItemHandlerModifiable & INBTSerializable<CompoundNBT>> LazyOptional<IItemHandler> filterInput(ISH itemStackHandler, Predicate<ItemStack> validItems) {
		return LazyOptional.of(() -> new ItemHandlerDelegator.FilterInput<>(itemStackHandler, validItems));
	}

	/**
	 * only allows fluid insertion of valid fluids
	 */
	public static LazyOptional<IFluidHandler> filterInput(IFluidHandler fluidHandler, Predicate<Fluid> validFluids) {
		return LazyOptional.of(() -> new FluidHandlerDelegator.FilterInput<>(fluidHandler, validFluids));
	}

	public static final Predicate<ItemStack> EMPTY_ITEM_INVENTORY_PREDICATE = stack -> {
		if (stack.getItem() instanceof BlockItem) {
			Block block = ((BlockItem) stack.getItem()).getBlock();
			if (block instanceof ShulkerBoxBlock) {
				return stack.getTagElement("BlockEntityTag") == null;
			}
			else if (block instanceof FleshChestBlock || block instanceof GulgeBlock) {
				CompoundNBT nbt = stack.getTagElement("BlockEntityTag");
				if (nbt == null) return true;
				return nbt.getCompound("Inventory").isEmpty();
			}
		}

		LazyOptional<IItemHandler> capability = stack.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY);
		final boolean[] isEmpty = {true};
		capability.ifPresent(itemHandler -> {
			int slots = itemHandler.getSlots();
			if (slots > 1000) { //if we have more than 1000 slots we don't bother checking them and just return false
				isEmpty[0] = false;
				return;
			}
			//ItemHandler cap doesn't have a isEmpty() method which forces us to sequentially check every slot if it is empty
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
	public static <ISH extends IItemHandler & IItemHandlerModifiable & INBTSerializable<CompoundNBT>> LazyOptional<IItemHandler> denyItemWithFilledInventory(ISH itemStackHandler) {
		return LazyOptional.of(() -> new ItemHandlerDelegator.FilterInput<>(itemStackHandler, EMPTY_ITEM_INVENTORY_PREDICATE));
	}

	public static final Predicate<ItemStack> FILLED_FLUID_ITEM_PREDICATE = stack -> FluidUtil.getFluidContained(stack).isPresent();

	/**
	 * only allows the insertion of items that contain any fluid (fluid handler capability), e.g. water buckets
	 */
	public static <ISH extends IItemHandler & IItemHandlerModifiable & INBTSerializable<CompoundNBT>> LazyOptional<IItemHandler> filterFilledFluidContainer(ISH itemStackHandler) {
		return LazyOptional.of(() -> new ItemHandlerDelegator.FilterInput<>(itemStackHandler, FILLED_FLUID_ITEM_PREDICATE));
	}

	public static final Predicate<ItemStack> FLUID_CONTAINER_ITEM_PREDICATE = stack -> FluidUtil.getFluidHandler(stack).isPresent();

	/**
	 * only allows the insertion of items are not full fluid containers (fluid handler capability), e.g. buckets
	 */
	public static <ISH extends IItemHandler & IItemHandlerModifiable & INBTSerializable<CompoundNBT>> LazyOptional<IItemHandler> filterFluidContainer(ISH itemStackHandler) {
		return LazyOptional.of(() -> new ItemHandlerDelegator.FilterInput<>(itemStackHandler, FLUID_CONTAINER_ITEM_PREDICATE));
	}

	/**
	 * only allows the insertion of items that are biofuel (solid & fluid container)
	 */
	public static <ISH extends IItemHandler & IItemHandlerModifiable & INBTSerializable<CompoundNBT>> LazyOptional<IItemHandler> filterBiofuel(ISH itemStackHandler) {
		return LazyOptional.of(() -> new ItemHandlerDelegator.FilterInput<>(itemStackHandler, BiofuelUtil::isItemValidFuel));
	}

	/**
	 * only allows the insertion of fluid that is biofuel
	 */
	public static LazyOptional<IFluidHandler> filterBiofuel(IFluidHandler fluidHandler) {
		return LazyOptional.of(() -> new FluidHandlerDelegator.FilterInput<>(fluidHandler, BiofuelUtil.VALID_FLUID));
	}

}
