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
	public static InventoryHandler<FixedSizeItemStackHandler> standard(int slotAmount) {
		return new InventoryHandler<>(new FixedSizeItemStackHandler(slotAmount));
	}

	public static InventoryHandler<FixedSizeItemStackHandler> standard(int slotAmount, Notify onInventoryChanged) {
		FixedSizeItemStackHandler handler = new FixedSizeItemStackHandler(slotAmount) {
			@Override
			protected void onContentsChanged(int slot) {
				onInventoryChanged.invoke();
			}
		};
		return new InventoryHandler<>(handler);
	}

	/**
	 * prevents item insertion, only item extraction is possible (e.g. output inventories)
	 */
	public static <T extends SerializableItemHandler> InventoryHandler<BehavioralItemHandler.DenyInput> denyInput(T itemStackHandler) {
		return new InventoryHandler<>(new BehavioralItemHandler.DenyInput(itemStackHandler));
	}

	/**
	 * prevents item insertion, only item extraction is possible (e.g. output inventories)
	 */
	public static InventoryHandler<BehavioralItemHandler.DenyInput> denyInput(int slotAmount, Notify onInventoryChanged) {
		FixedSizeItemStackHandler handler = new FixedSizeItemStackHandler(slotAmount) {
			@Override
			protected void onContentsChanged(int slot) {
				onInventoryChanged.invoke();
			}
		};
		return new InventoryHandler<>(new BehavioralItemHandler.DenyInput(handler));
	}

	/**
	 * only allows item insertion of valid items
	 */
	public static InventoryHandler<BehavioralItemHandler.PredicateFilterInput> filterInput(int slotAmount, List<Predicate<ItemStack>> slotFilters) {
		return filterInput(new FixedSizeItemStackHandler(slotAmount), slotFilters);
	}

	/**
	 * only allows item insertion of valid items
	 */
	public static InventoryHandler<BehavioralItemHandler.PredicateFilterInput> filterInput(int slotAmount, List<Predicate<ItemStack>> slotFilters, Notify onInventoryChanged) {
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
	public static <T extends SerializableItemHandler> InventoryHandler<BehavioralItemHandler.PredicateFilterInput> filterInput(T itemStackHandler, List<Predicate<ItemStack>> slotFilters) {
		return new InventoryHandler<>(new BehavioralItemHandler.PredicateFilterInput(itemStackHandler, slotFilters));
	}

	/**
	 * only allows item insertion of valid items
	 */
	public static InventoryHandler<BehavioralItemHandler.ItemStackFilterInput> filterInput(int slotAmount) {
		return filterInput(new FixedSizeItemStackHandler(slotAmount));
	}

	/**
	 * only allows item insertion of valid items
	 */
	public static InventoryHandler<BehavioralItemHandler.ItemStackFilterInput> filterInput(int slotAmount, Notify onInventoryChanged) {
		FixedSizeItemStackHandler handler = new FixedSizeItemStackHandler(slotAmount) {
			@Override
			protected void onContentsChanged(int slot) {
				onInventoryChanged.invoke();
			}
		};
		return filterInput(handler);
	}

	/**
	 * only allows item insertion of valid items
	 */
	public static <T extends SerializableItemHandler> InventoryHandler<BehavioralItemHandler.ItemStackFilterInput> filterInput(T itemStackHandler) {
		return new InventoryHandler<>(new BehavioralItemHandler.ItemStackFilterInput(itemStackHandler));
	}

	public static InventoryHandler<BehavioralItemHandler.LockableItemStackFilterInput> lockableFilterInput(int slotAmount, Notify onInventoryChanged) {
		FixedSizeItemStackHandler handler = new FixedSizeItemStackHandler(slotAmount) {
			@Override
			protected void onContentsChanged(int slot) {
				onInventoryChanged.invoke();
			}
		};
		return lockableFilterInput(handler);
	}

	public static <T extends SerializableItemHandler> InventoryHandler<BehavioralItemHandler.LockableItemStackFilterInput> lockableFilterInput(T itemStackHandler) {
		return new InventoryHandler<>(new BehavioralItemHandler.LockableItemStackFilterInput(itemStackHandler));
	}

	/**
	 * prevents nesting of items with inventories,<br>
	 * i.e. insertion of filled shulker boxes and items with filled inventories (item handler capability)
	 */
	public static <T extends SerializableItemHandler> InventoryHandler<BehavioralItemHandler.PredicateFilterInput> denyItemWithFilledInventory(T itemHandler) {
		return new InventoryHandler<>(new BehavioralItemHandler.PredicateFilterInput(itemHandler, IntStream.range(0, itemHandler.getSlots()).mapToObj(x -> EMPTY_ITEM_INVENTORY_PREDICATE).toList()));
	}

	/**
	 * prevents nesting of items with inventories,<br>
	 * i.e. insertion of filled shulker boxes and items with filled inventories (item handler capability)
	 */
	public static <T extends SerializableItemHandler> InventoryHandler<BehavioralItemHandler.PredicateFilterInput> denyItemWithFilledInventory(int slotAmount, Notify onInventoryChanged) {
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
	public static InventoryHandler<BehavioralItemHandler.PredicateFilterInput> filterFuel(int slotAmount) {
		return filterFuel(new FixedSizeItemStackHandler(slotAmount));
	}

	/**
	 * only allows the insertion of items that are biofuel (solid & fluid container)
	 */
	public static InventoryHandler<BehavioralItemHandler.PredicateFilterInput> filterFuel(int slotAmount, Notify onInventoryChanged) {
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
	public static <T extends SerializableItemHandler> InventoryHandler<BehavioralItemHandler.PredicateFilterInput> filterFuel(T itemHandler) {
		return filterInput(itemHandler, IntStream.range(0, itemHandler.getSlots()).mapToObj(x -> Nutrients.FUEL_PREDICATE).toList());
	}

}
