package com.github.elenterius.biomancy.world.inventory;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;

public class ItemInventory extends BaseInventory<ItemStackHandler> {

	private final ItemStack cachedInventoryHost;

	ItemInventory(int slots, int maxSlotSize, ItemStack inventoryHost) {
		itemHandler = new ItemStackHandler(slots) {
			@Override
			public int getSlotLimit(int slot) {
				return maxSlotSize;
			}

			@Override
			protected void onContentsChanged(int slot) {
				super.onContentsChanged(slot);
				setChanged();
			}
		};
		optionalItemHandler = LazyOptional.of(() -> itemHandler);
		cachedInventoryHost = inventoryHost;
	}

	private void serializeToHost() {
		cachedInventoryHost.getOrCreateTag().put("inventory", itemHandler.serializeNBT());
	}

	private void deserializeFromHost() {
		deserializeNBT(cachedInventoryHost.getOrCreateTag().getCompound("inventory"));
	}

	@Override
	public void setChanged() {
		serializeToHost();
		super.setChanged();
	}

	@Override
	public boolean stillValid(Player player) {
		if (cachedInventoryHost.isEmpty()) return false;
		return super.stillValid(player);
	}

	@Override
	public ItemStackHandler getItemHandler() {
		deserializeFromHost(); //prime cheese
		return super.getItemHandler();
	}

	@Override
	public LazyOptional<IItemHandler> getOptionalItemHandler() {
		deserializeFromHost(); //prime cheese
		//we now get the inventory from the ItemStack NBT, this makes it available on the client as well if someone gets the cap
		return super.getOptionalItemHandler();
	}

	public static ItemInventory createServerContents(int slots, int maxSlotSize, ItemStack inventoryHost) {
		ItemInventory inventory = new ItemInventory(slots, maxSlotSize, inventoryHost);
		inventory.deserializeFromHost();
		return inventory;
	}

	public static ItemInventory createClientContents(int slots, int maxSlotSize, ItemStack inventoryHost) {
		return new ItemInventory(slots, maxSlotSize, inventoryHost);
	}

}
