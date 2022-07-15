package com.github.elenterius.biomancy.world.item;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;

public interface IKeyListener {

	/**
	 * @param slotIndex of EquipmentSlotType (Armor + Main & Off Hand). This is not the inventory index of a player
	 */
	static void onReceiveKeybindingPacket(ServerLevel world, ServerPlayer player, int slotIndex, byte flag) {
		EquipmentSlot slotType = getEquipmentSlotTypeFrom(slotIndex);
		if (slotType != null) {
			ItemStack heldStack = player.getItemBySlot(slotType);
			if ((heldStack.getItem() instanceof IKeyListener keyListener) && !(player.getCooldowns().isOnCooldown(heldStack.getItem()))) {
				keyListener.onServerReceiveKeyPress(heldStack, world, player, flag);
			}
		}
		else {
			ItemStack stackInSlot = player.getInventory().getItem(slotIndex);
			if (!stackInSlot.isEmpty() && (stackInSlot.getItem() instanceof IKeyListener keyListener) && !(player.getCooldowns().isOnCooldown(stackInSlot.getItem()))) {
				keyListener.onServerReceiveKeyPress(stackInSlot, world, player, flag);
			}
		}
	}

	@Nullable
	static EquipmentSlot getEquipmentSlotTypeFrom(int slotIndex) {
		if (slotIndex < 0 || slotIndex > 5) return null;
		for (EquipmentSlot equipmentSlotType : EquipmentSlot.values()) {
			if (equipmentSlotType.getFilterFlag() == slotIndex) {
				return equipmentSlotType;
			}
		}
		return null;
	}

	/**
	 * If this method returns ActionResult Success, the result byte customFlags will be sent to the server
	 */
	@OnlyIn(Dist.CLIENT)
	InteractionResultHolder<Byte> onClientKeyPress(ItemStack stack, ClientLevel level, Player player, EquipmentSlot slot, byte flags);

	void onServerReceiveKeyPress(ItemStack stack, ServerLevel level, Player player, byte flags);

}
