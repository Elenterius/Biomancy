package com.github.elenterius.biomancy.world.inventory.menu;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.init.ModMenuTypes;
import com.github.elenterius.biomancy.world.block.fleshkinchest.FleshkinChestBlockEntity;
import com.github.elenterius.biomancy.world.inventory.BehavioralInventory;
import com.github.elenterius.biomancy.world.inventory.slot.ISlotZone;
import com.github.elenterius.biomancy.world.inventory.slot.NonNestingSlot;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.apache.logging.log4j.MarkerManager;

public class FleshkinChestMenu extends PlayerContainerMenu {

	private final BehavioralInventory<?> inventory;
	protected final Level level;

	protected FleshkinChestMenu(int id, Inventory playerInventory, BehavioralInventory<?> inventory) {
		super(ModMenuTypes.FLESHKIN_CHEST.get(), id, playerInventory, 150, 208);
		level = playerInventory.player.level;

		this.inventory = inventory;
		this.inventory.startOpen(playerInventory.player);

		final int posX = 26;
		final int posY = 24;
		final int rows = 6;
		final int columns = 7;
		for (int y = 0; y < rows; y++) {
			for (int x = 0; x < columns; x++) {
				addSlot(new NonNestingSlot(this.inventory, y * columns + x, posX + x * 18, posY + y * 18));
			}
		}
	}

	public static FleshkinChestMenu createServerMenu(int screenId, Inventory playerInventory, BehavioralInventory<?> inventory) {
		return new FleshkinChestMenu(screenId, playerInventory, inventory);
	}

	public static FleshkinChestMenu createClientMenu(int screenId, Inventory playerInventory, FriendlyByteBuf extraData) {
		BehavioralInventory<?> inventory = BehavioralInventory.createClientContents(FleshkinChestBlockEntity.SLOTS);
		return new FleshkinChestMenu(screenId, playerInventory, inventory);
	}

	@Override
	public boolean stillValid(Player player) {
		return inventory.stillValid(player);
	}

	@Override
	public ItemStack quickMoveStack(Player player, int index) {
		Slot slot = slots.get(index);
		if (!slot.hasItem()) return ItemStack.EMPTY;
		ItemStack stackInSlot = slot.getItem();
		ItemStack copyOfStack = stackInSlot.copy();

		boolean successfulTransfer = switch (SlotZone.getZoneFromIndex(index)) {
			case INVENTORY -> mergeIntoEither(SlotZone.PLAYER_HOTBAR, SlotZone.PLAYER_MAIN_INVENTORY, stackInSlot, true);
			case PLAYER_HOTBAR -> mergeIntoEither(SlotZone.INVENTORY, SlotZone.PLAYER_MAIN_INVENTORY, stackInSlot, false);
			case PLAYER_MAIN_INVENTORY -> mergeIntoEither(SlotZone.INVENTORY, SlotZone.PLAYER_HOTBAR, stackInSlot, false);
		};

		if (!successfulTransfer) return ItemStack.EMPTY;

		if (stackInSlot.isEmpty()) slot.set(ItemStack.EMPTY);
		else slot.setChanged();

		if (stackInSlot.getCount() == copyOfStack.getCount()) {
			BiomancyMod.LOGGER.warn(MarkerManager.getMarker("GlandMenu"), "Stack transfer failed in an unexpected way!");
			return ItemStack.EMPTY; //transfer error
		}

		slot.onTake(player, stackInSlot);
		return copyOfStack;
	}

	public Container getContainer() {
		return inventory;
	}

	@Override
	public void removed(Player player) {
		super.removed(player);
		inventory.stopOpen(player);
	}

	public enum SlotZone implements ISlotZone {
		PLAYER_HOTBAR(0, 9),
		PLAYER_MAIN_INVENTORY(PLAYER_HOTBAR.lastIndexPlus1, 3 * 9),
		INVENTORY(PLAYER_MAIN_INVENTORY.lastIndexPlus1, FleshkinChestBlockEntity.SLOTS);

		public final int firstIndex;
		public final int slotCount;
		public final int lastIndexPlus1;

		SlotZone(int firstIndex, int numberOfSlots) {
			this.firstIndex = firstIndex;
			slotCount = numberOfSlots;
			lastIndexPlus1 = firstIndex + numberOfSlots;
		}

		public static SlotZone getZoneFromIndex(int slotIndex) {
			for (SlotZone slotZone : SlotZone.values()) {
				if (slotIndex >= slotZone.firstIndex && slotIndex < slotZone.lastIndexPlus1) return slotZone;
			}
			throw new IndexOutOfBoundsException("Unexpected slotIndex");
		}

		@Override
		public int getFirstIndex() {
			return firstIndex;
		}

		@Override
		public int getLastIndexPlusOne() {
			return lastIndexPlus1;
		}

		@Override
		public int getSlotCount() {
			return slotCount;
		}

	}

}
