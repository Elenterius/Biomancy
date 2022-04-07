package com.github.elenterius.biomancy.world.inventory.menu;

import com.github.elenterius.biomancy.world.inventory.slot.ISlotZone;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.SlotItemHandler;
import net.minecraftforge.items.wrapper.PlayerInvWrapper;
import org.jetbrains.annotations.Nullable;

public abstract class PlayerContainerMenu extends AbstractContainerMenu {

	protected PlayerContainerMenu(@Nullable MenuType<?> type, int containerId, Inventory playerInventory) {
		this(type, containerId, playerInventory, 84, 142);
	}

	protected PlayerContainerMenu(@Nullable MenuType<?> type, int containerId, Inventory playerInventory, final int invPosY, final int hotbarPosY) {
		super(type, containerId);
		initPlayerInvSlots(playerInventory, invPosY, hotbarPosY);
	}

	protected PlayerContainerMenu(@Nullable MenuType<?> type, int containerId, Inventory playerInventory, final int pX, final int invPosY, final int hotbarPosY) {
		super(type, containerId);
		initPlayerInvSlots(playerInventory, pX, invPosY, hotbarPosY);
	}

	protected void initPlayerInvSlots(Inventory playerInventory, final int invPosY, final int hotbarPosY) {
		initPlayerInvSlots(playerInventory, 8, invPosY, hotbarPosY);
	}

	protected void initPlayerInvSlots(Inventory playerInventory, final int pX, final int invPosY, final int hotbarPosY) {
		PlayerInvWrapper playerInventoryForge = new PlayerInvWrapper(playerInventory);

		final int SLOTS_PER_ROW = 9;
		final int SLOT_X_SPACING = 18;
		final int SLOT_Y_SPACING = 18;

		// Add the players hotbar
		for (int idx = 0; idx < SLOTS_PER_ROW; idx++) {
			addSlot(new SlotItemHandler(playerInventoryForge, idx, pX + SLOT_X_SPACING * idx, hotbarPosY));
		}

		// Add the players main inventory
		for (int y = 0; y < 3; y++) {
			for (int x = 0; x < SLOTS_PER_ROW; x++) {
				int slotNumber = SLOTS_PER_ROW + y * SLOTS_PER_ROW + x;
				int posX = pX + x * SLOT_X_SPACING;
				int posY = invPosY + y * SLOT_Y_SPACING;
				addSlot(new SlotItemHandler(playerInventoryForge, slotNumber, posX, posY));
			}
		}
	}

	protected boolean mergeInto(ISlotZone zone, ItemStack stack, boolean fillFromEnd) {
		return moveItemStackTo(stack, zone.getFirstIndex(), zone.getLastIndexPlusOne(), fillFromEnd);
	}

	protected boolean mergeIntoEither(ISlotZone zoneA, ISlotZone zoneB, ItemStack stack, boolean fillFromEnd) {
		return mergeInto(zoneA, stack, fillFromEnd) || mergeInto(zoneB, stack, fillFromEnd);
	}

}
