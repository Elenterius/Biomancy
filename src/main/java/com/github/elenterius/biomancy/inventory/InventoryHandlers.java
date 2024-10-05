package com.github.elenterius.biomancy.inventory;

import com.github.elenterius.biomancy.api.nutrients.Nutrients;
import com.github.elenterius.biomancy.init.ModCapabilities;
import net.minecraft.world.item.BundleItem;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.IntStream;

/**
 * allows us to add specific behavior to any IItemHandler or IFluidHandler without the need of subclassing
 */
public final class InventoryHandlers {

	public static final Predicate<ItemStack> EMPTY_ITEM_INVENTORY_PREDICATE = stack -> {
		//		if (stack.getItem() instanceof BlockItem blockItem) {
		//			Block block = blockItem.getBlock();
		//			if (block instanceof ShulkerBoxBlock) {
		//				return stack.getTagElement("BlockEntityTag") == null;
		//			}
		//		}

		if (stack.getItem() instanceof BundleItem) return false;
		if (!stack.getItem().canFitInsideContainerItems()) return false;

		LazyOptional<IItemHandler> capability = stack.getCapability(ModCapabilities.ITEM_HANDLER);
		final boolean[] isEmpty = {true};
		capability.ifPresent(itemHandler -> {
			int slots = itemHandler.getSlots();
			if (slots > 200) { //if we have more than 200 slots we don't bother checking them and just return false
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

	private InventoryHandlers() {}

	/**
	 * default item handler behavior
	 */
	public static InventoryHandler standard(int slotAmount) {
		return new InventoryHandler(new FixedSizeItemStackHandler(slotAmount));
	}

	public static InventoryHandler standard(int slotAmount, Notify onInventoryChanged) {
		FixedSizeItemStackHandler handler = new FixedSizeItemStackHandler(slotAmount) {
			@Override
			protected void onContentsChanged(int slot) {
				onInventoryChanged.invoke();
			}
		};
		return new InventoryHandler(handler);
	}

	/**
	 * prevents item insertion, only item extraction is possible (e.g. output inventories)
	 */
	public static <T extends SerializableItemHandler> InventoryHandler denyInput(T itemStackHandler) {
		return new InventoryHandler(new BehavioralItemHandler.DenyInput(itemStackHandler));
	}

	/**
	 * prevents item insertion, only item extraction is possible (e.g. output inventories)
	 */
	public static InventoryHandler denyInput(int slotAmount, Notify onInventoryChanged) {
		FixedSizeItemStackHandler handler = new FixedSizeItemStackHandler(slotAmount) {
			@Override
			protected void onContentsChanged(int slot) {
				onInventoryChanged.invoke();
			}
		};
		return new InventoryHandler(new BehavioralItemHandler.DenyInput(handler));
	}

	/**
	 * only allows item insertion of valid items
	 */
	public static <F extends Predicate<ItemStack>> InventoryHandler filterInput(int slotAmount, List<F> slotFilters) {
		return filterInput(new FixedSizeItemStackHandler(slotAmount), slotFilters);
	}

	/**
	 * only allows item insertion of valid items
	 */
	public static <F extends Predicate<ItemStack>> InventoryHandler filterInput(int slotAmount, List<F> slotFilters, Notify onInventoryChanged) {
		FixedSizeItemStackHandler handler = new FixedSizeItemStackHandler(slotAmount) {
			@Override
			protected void onContentsChanged(int slot) {
				onInventoryChanged.invoke();
			}
		};
		return filterInput(handler, slotFilters);
	}

	/**
	 * only allows item insertion of valid items
	 */
	public static <T extends SerializableItemHandler, F extends Predicate<ItemStack>> InventoryHandler filterInput(T itemStackHandler, List<F> slotFilters) {
		return new InventoryHandler(new BehavioralItemHandler.FilterInput<>(itemStackHandler, slotFilters));
	}

	/**
	 * prevents nesting of items with inventories,<br>
	 * i.e. insertion of filled shulker boxes and items with filled inventories (item handler capability)
	 */
	public static <T extends SerializableItemHandler> InventoryHandler denyItemWithFilledInventory(T itemHandler) {
		return new InventoryHandler(new BehavioralItemHandler.FilterInput<>(itemHandler, IntStream.range(0, itemHandler.getSlots()).mapToObj(x -> EMPTY_ITEM_INVENTORY_PREDICATE).toList()));
	}

	/**
	 * prevents nesting of items with inventories,<br>
	 * i.e. insertion of filled shulker boxes and items with filled inventories (item handler capability)
	 */
	public static <T extends SerializableItemHandler> InventoryHandler denyItemWithFilledInventory(int slotAmount, Notify onInventoryChanged) {
		FixedSizeItemStackHandler handler = new FixedSizeItemStackHandler(slotAmount) {
			@Override
			protected void onContentsChanged(int slot) {
				onInventoryChanged.invoke();
			}
		};
		return denyItemWithFilledInventory(handler);
	}

	/**
	 * only allows the insertion of items that are biofuel (solid & fluid container)
	 */
	public static InventoryHandler filterFuel(int slotAmount) {
		return filterFuel(new FixedSizeItemStackHandler(slotAmount));
	}

	/**
	 * only allows the insertion of items that are biofuel (solid & fluid container)
	 */
	public static InventoryHandler filterFuel(int slotAmount, Notify onInventoryChanged) {
		FixedSizeItemStackHandler handler = new FixedSizeItemStackHandler(slotAmount) {
			@Override
			protected void onContentsChanged(int slot) {
				onInventoryChanged.invoke();
			}
		};
		return filterFuel(handler);
	}

	/**
	 * only allows the insertion of items that are biofuel (solid & fluid container)
	 */
	public static <T extends SerializableItemHandler> InventoryHandler filterFuel(T itemHandler) {
		return filterInput(itemHandler, IntStream.range(0, itemHandler.getSlots()).mapToObj(x -> Nutrients.FUEL_PREDICATE).toList());
	}

}
