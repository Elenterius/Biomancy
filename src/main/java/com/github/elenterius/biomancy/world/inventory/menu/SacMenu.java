package com.github.elenterius.biomancy.world.inventory.menu;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.init.ModMenuTypes;
import com.github.elenterius.biomancy.world.block.entity.SacBlockEntity;
import com.github.elenterius.biomancy.world.inventory.SimpleInventory;
import com.github.elenterius.biomancy.world.inventory.slot.ISlotZone;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.apache.logging.log4j.MarkerManager;

public class SacMenu extends PlayerContainerMenu {

	private final SimpleInventory inventory;
	protected final Level level;

	protected SacMenu(int id, Inventory playerInventory, SimpleInventory inventory) {
		super(ModMenuTypes.SAC.get(), id, playerInventory, 51, 109);
		level = playerInventory.player.level;

		this.inventory = inventory;

		int posY = 20;
		int posX = 44;
		for (int i = 0; i < 5; i++) {
			addSlot(new Slot(inventory, i, posX + i * 18, posY));
		}
	}

	public static SacMenu createServerMenu(int screenId, Inventory playerInventory, SimpleInventory inventory) {
		return new SacMenu(screenId, playerInventory, inventory);
	}

	public static SacMenu createClientMenu(int screenId, Inventory playerInventory, FriendlyByteBuf extraData) {
		SimpleInventory inventory = SimpleInventory.createClientContents(SacBlockEntity.SLOTS);
		return new SacMenu(screenId, playerInventory, inventory);
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

		boolean successfulTransfer;
		SlotZone slotZone = SlotZone.getZoneFromIndex(index);

		switch (slotZone) {
			case INVENTORY -> successfulTransfer = mergeIntoEither(SlotZone.PLAYER_HOTBAR, SlotZone.PLAYER_MAIN_INVENTORY, stackInSlot, true);
			case PLAYER_HOTBAR -> successfulTransfer = mergeIntoEither(SlotZone.INVENTORY, SlotZone.PLAYER_MAIN_INVENTORY, stackInSlot, false);
			case PLAYER_MAIN_INVENTORY -> successfulTransfer = mergeIntoEither(SlotZone.INVENTORY, SlotZone.PLAYER_HOTBAR, stackInSlot, false);
			default -> throw new IllegalArgumentException("unexpected SlotZone:" + slotZone);
		}

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

	public enum SlotZone implements ISlotZone {
		PLAYER_HOTBAR(0, 9),
		PLAYER_MAIN_INVENTORY(PLAYER_HOTBAR.lastIndexPlus1, 3 * 9),
		INVENTORY(PLAYER_MAIN_INVENTORY.lastIndexPlus1, SacBlockEntity.SLOTS);

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
