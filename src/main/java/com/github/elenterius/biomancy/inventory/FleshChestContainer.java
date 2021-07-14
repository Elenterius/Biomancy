package com.github.elenterius.biomancy.inventory;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.init.ModContainerTypes;
import com.github.elenterius.biomancy.tileentity.FleshbornChestTileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.items.SlotItemHandler;
import net.minecraftforge.items.wrapper.PlayerInvWrapper;
import org.apache.logging.log4j.MarkerManager;

public class FleshChestContainer extends Container {

	protected final SimpleInventory invContents;

	private FleshChestContainer(int screenId, PlayerInventory playerInventory, SimpleInventory invContents) {
		super(ModContainerTypes.FLESHBORN_CHEST.get(), screenId);
		this.invContents = invContents;
		PlayerInvWrapper playerInventoryForge = new PlayerInvWrapper(playerInventory);

		invContents.openInventory(playerInventory.player);

		final int HOT_BAR_SIZE = 9;
		final int SLOT_X_SPACING = 18;
		final int SLOT_Y_SPACING = 18;

		// Add the players hotbar
		for (int idx = 0; idx < HOT_BAR_SIZE; idx++) { //hotbar
			addSlot(new SlotItemHandler(playerInventoryForge, idx, 8 + SLOT_X_SPACING * idx, 208));
		}

		// Add the players main inventory
		final int PLAYER_INVENTORY_POS_X = 8;
		final int PLAYER_INVENTORY_POS_Y = 150;
		for (int y = 0; y < 3; y++) {
			for (int x = 0; x < 9; x++) {
				int slotNumber = HOT_BAR_SIZE + y * 9 + x;
				int posX = PLAYER_INVENTORY_POS_X + x * SLOT_X_SPACING;
				int posY = PLAYER_INVENTORY_POS_Y + y * SLOT_Y_SPACING;
				addSlot(new SlotItemHandler(playerInventoryForge, slotNumber, posX, posY));
			}
		}

		final int CHEST_INVENTORY_POS_X = 8;
		final int CHEST_INVENTORY_POS_Y = 17;
		for (int y = 0; y < 6; y++) {
			for (int x = 0; x < 9; x++) {
				int slotNumber = y * 9 + x;
				int posX = CHEST_INVENTORY_POS_X + x * SLOT_X_SPACING;
				int posY = CHEST_INVENTORY_POS_Y + y * SLOT_Y_SPACING;
				addSlot(new NonNestingSlot(invContents, slotNumber, posX, posY));
			}
		}
	}

	public static FleshChestContainer createServerContainer(int screenId, PlayerInventory playerInventory, SimpleInventory invContents) {
		return new FleshChestContainer(screenId, playerInventory, invContents);
	}

	public static FleshChestContainer createClientContainer(int screenId, PlayerInventory playerInventory, PacketBuffer extraData) {
		SimpleInventory invContents = SimpleInventory.createClientContents(FleshbornChestTileEntity.INV_SLOTS_COUNT);
		return new FleshChestContainer(screenId, playerInventory, invContents);
	}

	@Override
	public boolean canInteractWith(PlayerEntity playerIn) {
		return invContents.isUsableByPlayer(playerIn);
	}

	@Override
	public void onContainerClosed(PlayerEntity playerIn) {
		super.onContainerClosed(playerIn);
		invContents.closeInventory(playerIn);
	}

	/**
	 * copied from: https://github.com/TheGreyGhost/MinecraftByExample/blob/1-16-3-final/src/main/java/minecraftbyexample/mbe31_inventory_furnace/ContainerFurnace.java
	 */
	@Override
	public ItemStack transferStackInSlot(PlayerEntity playerIn, int sourceSlotIndex) {

		Slot sourceSlot = inventorySlots.get(sourceSlotIndex); // side-effect: throws error if the sourceSlotIndex is out of range (index < 0 || index >= size())
		if (sourceSlot == null || !sourceSlot.getHasStack()) return ItemStack.EMPTY;
		ItemStack sourceStack = sourceSlot.getStack();
		ItemStack copyOfSourceStack = sourceStack.copy();

		boolean successfulTransfer = false;
		SlotZone sourceZone = SlotZone.getZoneFromIndex(sourceSlotIndex);

		switch (sourceZone) {
			case CHEST_INVENTORY:
				successfulTransfer = mergeInto(SlotZone.PLAYER_MAIN_INVENTORY, sourceStack, false);
				if (!successfulTransfer) successfulTransfer = mergeInto(SlotZone.PLAYER_HOTBAR, sourceStack, false);
				break;

			case PLAYER_HOTBAR:
			case PLAYER_MAIN_INVENTORY:
				if (HandlerBehaviors.EMPTY_ITEM_INVENTORY_PREDICATE.test(sourceStack)) {
					successfulTransfer = mergeInto(SlotZone.CHEST_INVENTORY, sourceStack, false);
				}
				if (!successfulTransfer) {
					if (sourceZone == SlotZone.PLAYER_HOTBAR) successfulTransfer = mergeInto(SlotZone.PLAYER_MAIN_INVENTORY, sourceStack, false);
					else successfulTransfer = mergeInto(SlotZone.PLAYER_HOTBAR, sourceStack, false);
				}
				break;

			default:
				throw new IllegalArgumentException("unexpected sourceZone:" + sourceZone);
		}

		if (!successfulTransfer) return ItemStack.EMPTY;

		if (sourceStack.isEmpty()) sourceSlot.putStack(ItemStack.EMPTY);
		else sourceSlot.onSlotChanged();

		if (sourceStack.getCount() == copyOfSourceStack.getCount()) {
			BiomancyMod.LOGGER.warn(MarkerManager.getMarker("FLESH_CHEST_CONTAINER"), "Stack transfer failed in an unexpected way!");
			return ItemStack.EMPTY; //transfer error
		}

		sourceSlot.onTake(playerIn, sourceStack);
		return copyOfSourceStack;
	}

	private boolean mergeInto(SlotZone destinationZone, ItemStack sourceStack, boolean fillFromEnd) {
		return mergeItemStack(sourceStack, destinationZone.firstIndex, destinationZone.lastIndexPlus1, fillFromEnd);
	}

	private enum SlotZone {
		PLAYER_HOTBAR(0, 9),
		PLAYER_MAIN_INVENTORY(9, 3 * 9),
		CHEST_INVENTORY(9 + 3 * 9, FleshbornChestTileEntity.INV_SLOTS_COUNT);

		public final int firstIndex;
		public final int slotCount;
		public final int lastIndexPlus1;

		SlotZone(int firstIndex, int numberOfSlots) {
			this.firstIndex = firstIndex;
			this.slotCount = numberOfSlots;
			this.lastIndexPlus1 = firstIndex + numberOfSlots;
		}

		public static SlotZone getZoneFromIndex(int slotIndex) {
			for (SlotZone slotZone : SlotZone.values()) {
				if (slotIndex >= slotZone.firstIndex && slotIndex < slotZone.lastIndexPlus1) return slotZone;
			}
			throw new IndexOutOfBoundsException("Unexpected slotIndex");
		}
	}
}
