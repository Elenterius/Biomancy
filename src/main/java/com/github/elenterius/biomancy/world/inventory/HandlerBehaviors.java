package com.github.elenterius.biomancy.world.inventory;

import com.github.elenterius.biomancy.util.FuelUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ShulkerBoxBlock;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
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
	public static <T extends IItemHandler & IItemHandlerModifiable & INBTSerializable<CompoundTag>> T standard(T itemStackHandler) {
		return itemStackHandler;
	}

	/**
	 * prevents item insertion, only item extraction is possible (e.g. output inventories)
	 */
	public static <T extends IItemHandler & IItemHandlerModifiable & INBTSerializable<CompoundTag>> T denyInput(T itemStackHandler) {
		//noinspection unchecked
		return (T) new ItemHandlerDelegator.DenyInput<>(itemStackHandler);
	}

	/**
	 * only allows item insertion of valid items
	 */
	public static <T extends IItemHandler & IItemHandlerModifiable & INBTSerializable<CompoundTag>> T filterInput(T itemStackHandler, Predicate<ItemStack> validItems) {
		//noinspection unchecked
		return (T) new ItemHandlerDelegator.FilterInput<>(itemStackHandler, validItems);
	}

	public static final Predicate<ItemStack> EMPTY_ITEM_INVENTORY_PREDICATE = stack -> {
		if (stack.getItem() instanceof BlockItem blockItem) {
			Block block = blockItem.getBlock();
			if (block instanceof ShulkerBoxBlock) {
				return stack.getTagElement("BlockEntityTag") == null;
			}
//			else if (block instanceof FleshChestBlock || block instanceof GulgeBlock) {
//				CompoundTag nbt = stack.getTagElement("BlockEntityTag");
//				if (nbt == null) return true;
//				return nbt.getCompound("Inventory").isEmpty();
//			}
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
	public static <T extends IItemHandler & IItemHandlerModifiable & INBTSerializable<CompoundTag>> T denyItemWithFilledInventory(T itemStackHandler) {
		//noinspection unchecked
		return (T) new ItemHandlerDelegator.FilterInput<>(itemStackHandler, EMPTY_ITEM_INVENTORY_PREDICATE);
	}

	/**
	 * only allows the insertion of items that are biofuel (solid & fluid container)
	 */
	public static <T extends IItemHandler & IItemHandlerModifiable & INBTSerializable<CompoundTag>> T filterFuel(T itemStackHandler) {
		//noinspection unchecked
		return (T) new ItemHandlerDelegator.FilterInput<>(itemStackHandler, FuelUtil::isItemValidFuel);
	}

}
