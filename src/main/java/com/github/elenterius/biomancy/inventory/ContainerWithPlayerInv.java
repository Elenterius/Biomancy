package com.github.elenterius.biomancy.inventory;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import net.minecraftforge.items.SlotItemHandler;
import net.minecraftforge.items.wrapper.PlayerInvWrapper;

import javax.annotation.Nullable;

public abstract class ContainerWithPlayerInv extends Container {

	protected ContainerWithPlayerInv(@Nullable ContainerType<?> containerType, int screenId, PlayerInventory playerInventory) {
		super(containerType, screenId);
		initPlayerInvSlots(playerInventory);
	}

	protected void initPlayerInvSlots(PlayerInventory playerInventory) {
		PlayerInvWrapper playerInventoryForge = new PlayerInvWrapper(playerInventory);

		final int POS_X = 8;
		final int INV_POS_Y = 84;
		final int HOTBAR_POS_Y = 142;
		final int SLOTS_PER_ROW = 9;
		final int SLOT_X_SPACING = 18;
		final int SLOT_Y_SPACING = 18;

		// Add the players hotbar
		for (int idx = 0; idx < SLOTS_PER_ROW; idx++) {
			addSlot(new SlotItemHandler(playerInventoryForge, idx, POS_X + SLOT_X_SPACING * idx, HOTBAR_POS_Y));
		}

		// Add the players main inventory
		for (int y = 0; y < 3; y++) {
			for (int x = 0; x < SLOTS_PER_ROW; x++) {
				int slotNumber = SLOTS_PER_ROW + y * SLOTS_PER_ROW + x;
				int posX = POS_X + x * SLOT_X_SPACING;
				int posY = INV_POS_Y + y * SLOT_Y_SPACING;
				addSlot(new SlotItemHandler(playerInventoryForge, slotNumber, posX, posY));
			}
		}
	}

}
