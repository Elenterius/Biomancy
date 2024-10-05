package com.github.elenterius.biomancy.inventory;

import com.github.elenterius.biomancy.api.serum.SerumContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;

public class InjectorItemInventory {

	private final ItemStack cachedInventoryHost;
	private final LargeSingleItemStackHandler itemHandler;
	private final LazyOptional<IItemHandler> optionalItemHandler;

	private InjectorItemInventory(short maxSlotSize, ItemStack inventoryHost) {
		itemHandler = new LargeSingleItemStackHandler(maxSlotSize) {

			@Override
			public boolean isItemValid(ItemStack stack) {
				return stack.getItem() instanceof SerumContainer;
			}

			@Override
			protected void onContentsChanged() {
				serializeToHost();
			}
		};
		optionalItemHandler = LazyOptional.of(() -> itemHandler);
		cachedInventoryHost = inventoryHost;
	}

	public static InjectorItemInventory create(short maxSlotSize, ItemStack inventoryHost) {
		InjectorItemInventory inventory = new InjectorItemInventory(maxSlotSize, inventoryHost);
		inventory.deserializeFromHost();
		return inventory;
	}

	private void serializeToHost() {
		cachedInventoryHost.getOrCreateTag().put("inventory", itemHandler.serializeNBT());
	}

	private void deserializeFromHost() {
		itemHandler.deserializeNBT(cachedInventoryHost.getOrCreateTag().getCompound("inventory"));
	}

	public boolean stillValid() {
		return !cachedInventoryHost.isEmpty();
	}

	public LargeSingleItemStackHandler getItemHandler() {
		deserializeFromHost(); //prime cheese
		return itemHandler;
	}

	public LazyOptional<IItemHandler> getLazyOptional() {
		deserializeFromHost(); //prime cheese
		//we now get the inventory from the ItemStack NBT, this makes it available on the client as well if someone gets the cap
		return optionalItemHandler;
	}

}
