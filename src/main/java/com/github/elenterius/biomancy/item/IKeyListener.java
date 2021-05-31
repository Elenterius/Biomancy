package com.github.elenterius.biomancy.item;

import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;

public interface IKeyListener {

	/**
	 * @param slotIndex of EquipmentSlotType (Armor + Main & Off Hand). This is not the inventory index of a player
	 */
	static void onReceiveKeybindingPacket(ServerWorld world, ServerPlayerEntity player, int slotIndex, byte flag) {
		EquipmentSlotType slotType = getEquipmentSlotTypeFrom(slotIndex);
		if (slotType != null) {
			ItemStack heldStack = player.getItemStackFromSlot(slotType);
			if ((heldStack.getItem() instanceof IKeyListener) && !(player.getCooldownTracker().hasCooldown(heldStack.getItem()))) {
				((IKeyListener) heldStack.getItem()).onServerReceiveKeyPress(heldStack, world, player, flag);
			}
		}
		else {
			ItemStack stackInSlot = player.inventory.getStackInSlot(slotIndex);
			if (!stackInSlot.isEmpty() && stackInSlot.getItem() instanceof IKeyListener && !(player.getCooldownTracker().hasCooldown(stackInSlot.getItem()))) {
				((IKeyListener) stackInSlot.getItem()).onServerReceiveKeyPress(stackInSlot, world, player, flag);
			}
		}
	}

	@Nullable
	static EquipmentSlotType getEquipmentSlotTypeFrom(int slotIndex) {
		if (slotIndex < 0 || slotIndex > 5) return null;
		for (EquipmentSlotType equipmentSlotType : EquipmentSlotType.values()) {
			if (equipmentSlotType.getSlotIndex() == slotIndex) {
				return equipmentSlotType;
			}
		}
		return null;
	}

	/**
	 * If this method returns ActionResult Success, the result byte flag will be sent to the server
	 */
	@OnlyIn(Dist.CLIENT)
	ActionResult<Byte> onClientKeyPress(ItemStack stack, ClientWorld world, PlayerEntity player, byte flags);

	void onServerReceiveKeyPress(ItemStack stack, ServerWorld world, ServerPlayerEntity player, byte flags);
}
