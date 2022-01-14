package com.github.elenterius.biomancy.world.inventory;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.init.ModMenuTypes;
import com.github.elenterius.biomancy.world.block.entity.GlandBlockEntity;
import com.github.elenterius.biomancy.world.inventory.slot.ISlotZone;
import com.github.elenterius.biomancy.world.inventory.slot.OutputSlot;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.apache.logging.log4j.MarkerManager;

public class GlandMenu extends PlayerContainerMenu {

	private final SimpleInventory<?> outputInventory;
	protected final Level level;

	protected GlandMenu(int id, Inventory playerInventory, SimpleInventory<?> outputInventory) {
		super(ModMenuTypes.GLAND.get(), id, playerInventory);
		level = playerInventory.player.level;

		this.outputInventory = outputInventory;

		int posY = 17;
		int posX = 98;
		addSlot(new OutputSlot(outputInventory, 0, posX, posY));
		addSlot(new OutputSlot(outputInventory, 1, posX + 18, posY));
		addSlot(new OutputSlot(outputInventory, 2, posX, posY + 18));
		addSlot(new OutputSlot(outputInventory, 3, posX + 18, posY + 18));
	}

	public static GlandMenu createServerMenu(int screenId, Inventory playerInventory, SimpleInventory<?> outputInventory) {
		return new GlandMenu(screenId, playerInventory, outputInventory);
	}

	public static GlandMenu createClientMenu(int screenId, Inventory playerInventory, FriendlyByteBuf extraData) {
		SimpleInventory<?> outputInventory = SimpleInventory.createClientContents(GlandBlockEntity.OUTPUT_SLOTS);
		return new GlandMenu(screenId, playerInventory, outputInventory);
	}

	@Override
	public boolean stillValid(Player player) {
		return outputInventory.stillValid(player);
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
			case OUTPUT_ZONE -> successfulTransfer = mergeIntoEither(SlotZone.PLAYER_HOTBAR, SlotZone.PLAYER_MAIN_INVENTORY, stackInSlot, true);
			case PLAYER_HOTBAR -> successfulTransfer = mergeInto(SlotZone.PLAYER_MAIN_INVENTORY, stackInSlot, false);
			case PLAYER_MAIN_INVENTORY -> successfulTransfer = mergeInto(SlotZone.PLAYER_HOTBAR, stackInSlot, false);
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
		OUTPUT_ZONE(PLAYER_MAIN_INVENTORY.lastIndexPlus1, GlandBlockEntity.OUTPUT_SLOTS);

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
