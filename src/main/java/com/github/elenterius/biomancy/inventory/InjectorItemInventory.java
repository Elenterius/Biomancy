package com.github.elenterius.biomancy.inventory;

import com.github.elenterius.biomancy.api.serum.SerumContainer;
import com.github.elenterius.biomancy.inventory.itemhandler.LargeSingleItemStackHandler;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;

public class InjectorItemInventory extends BaseInventory<LargeSingleItemStackHandler> {

	private final ItemStack cachedInventoryHost;

	private InjectorItemInventory(short maxSlotSize, ItemStack inventoryHost) {
		itemHandler = new LargeSingleItemStackHandler(maxSlotSize) {

			@Override
			public boolean isItemValid(ItemStack stack) {
				return stack.getItem() instanceof SerumContainer;
			}

			@Override
			protected void onContentsChanged() {
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
	public int getMaxStackSize() {
		return itemHandler.getMaxAmount();
	}

	@Override
	public boolean isEmpty() {
		return itemHandler.isEmpty();
	}

	@Override
	public boolean isFull() {
		return itemHandler.isFull();
	}

	@Override
	public boolean doesItemStackFit(ItemStack stack) {
		ItemStack remainder = itemHandler.insertItem(0, stack, true);
		return remainder.isEmpty();
	}

	@Override
	public ItemStack insertItemStack(ItemStack stack) {
		return itemHandler.insertItem(0, stack, false);
	}


	@Override
	public ItemStack removeItemNoUpdate(int index) {
		return itemHandler.extractItem(index, itemHandler.getMaxAmount(), false);
	}

	@Override
	public void clearContent() {
		itemHandler.setStackInSlot(0, ItemStack.EMPTY);
	}

	@Override
	public LargeSingleItemStackHandler getItemHandler() {
		deserializeFromHost(); //prime cheese
		return super.getItemHandler();
	}

	@Override
	public LazyOptional<IItemHandler> getOptionalItemHandler() {
		deserializeFromHost(); //prime cheese
		//we now get the inventory from the ItemStack NBT, this makes it available on the client as well if someone gets the cap
		return super.getOptionalItemHandler();
	}

	public static InjectorItemInventory createServerContents(short maxSlotSize, ItemStack inventoryHost) {
		InjectorItemInventory inventory = new InjectorItemInventory(maxSlotSize, inventoryHost);
		inventory.deserializeFromHost();
		return inventory;
	}

	public static InjectorItemInventory createClientContents(short maxSlotSize, ItemStack inventoryHost) {
		return new InjectorItemInventory(maxSlotSize, inventoryHost);
	}

}
