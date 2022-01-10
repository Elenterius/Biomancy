package com.github.elenterius.biomancy.world.inventory;

import com.github.elenterius.biomancy.world.inventory.slot.ISlotZone;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.SlotItemHandler;
import net.minecraftforge.items.wrapper.PlayerInvWrapper;
import org.jetbrains.annotations.Nullable;

public abstract class PlayerContainerMenu extends AbstractContainerMenu {

	protected PlayerContainerMenu(@Nullable MenuType<?> type, int id, Inventory playerInventory) {
		super(type, id);
		initPlayerInvSlots(playerInventory);
	}

	protected void initPlayerInvSlots(Inventory playerInventory) {
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

	protected boolean mergeInto(ISlotZone zone, ItemStack stack, boolean fillFromEnd) {
		return moveItemStackTo(stack, zone.getFirstIndex(), zone.getLastIndexPlusOne(), fillFromEnd);
	}

	protected boolean mergeIntoEither(ISlotZone zoneA, ISlotZone zoneB, ItemStack stack, boolean fillFromEnd) {
		return mergeInto(zoneA, stack, fillFromEnd) || mergeInto(zoneB, stack, fillFromEnd);
	}

}
