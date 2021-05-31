package com.github.elenterius.biomancy.handler;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.init.ClientSetupHandler;
import com.github.elenterius.biomancy.item.IKeyListener;
import com.github.elenterius.biomancy.network.ModNetworkHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.lwjgl.glfw.GLFW;

@OnlyIn(Dist.CLIENT)
@Mod.EventBusSubscriber(modid = BiomancyMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public final class ClientInputHandler {
	private ClientInputHandler() {}

	public static final EquipmentSlotType[] armorSlotTypes = new EquipmentSlotType[]{EquipmentSlotType.HEAD, EquipmentSlotType.CHEST, EquipmentSlotType.LEGS, EquipmentSlotType.FEET};
	public static final EquipmentSlotType[] handSlotTypes = new EquipmentSlotType[]{EquipmentSlotType.MAINHAND, EquipmentSlotType.OFFHAND};

	@SubscribeEvent
	public static void onKeyInput(final InputEvent.KeyInputEvent event) {
		ClientPlayerEntity player = Minecraft.getInstance().player;
		if (player != null && event.getKey() == ClientSetupHandler.ITEM_DEFAULT_KEY_BINDING.getKey().getKeyCode() && event.getAction() == GLFW.GLFW_RELEASE) {
			if (event.getModifiers() == GLFW.GLFW_MOD_CONTROL) {
				for (EquipmentSlotType slotType : armorSlotTypes) { //worst case this will send 4 packets to the server
					ItemStack armorStack = player.getItemStackFromSlot(slotType);
					if (!armorStack.isEmpty() && armorStack.getItem() instanceof IKeyListener) {
						ActionResult<Byte> result = ((IKeyListener) armorStack.getItem()).onClientKeyPress(armorStack, player.worldClient, player, (byte) 0);
						if (result.getType().isSuccess()) {
							ModNetworkHandler.sendKeyBindPressToServer(slotType, result.getResult());
						}
					}
				}
			}
			else {
				for (EquipmentSlotType slotType : handSlotTypes) { //worst case this will send 2 packets to the server
					ItemStack heldStack = player.getItemStackFromSlot(slotType);
					if (!heldStack.isEmpty() && heldStack.getItem() instanceof IKeyListener) {
						ActionResult<Byte> result = ((IKeyListener) heldStack.getItem()).onClientKeyPress(heldStack, player.worldClient, player, (byte) 0);
						if (result.getType().isSuccess()) {
							ModNetworkHandler.sendKeyBindPressToServer(slotType, result.getResult());
						}
					}
				}
			}
		}
	}

//	@SubscribeEvent
//	public static void onMouseClick(final InputEvent.ClickInputEvent event) {
//		if (event.isAttack()) {
//			ClientPlayerEntity player = Minecraft.getInstance().player;
//			if (player != null) {
//				ItemStack heldStack = player.getHeldItem(event.getHand());
//				if (!heldStack.isEmpty() && heldStack.getItem() == ModItems.INFESTED_RIFLE.get()) {
//					event.setSwingHand(false);
//					event.setCanceled(true);
//				}
//			}
//		}
//	}
}
