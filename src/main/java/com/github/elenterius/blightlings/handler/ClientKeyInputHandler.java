package com.github.elenterius.blightlings.handler;

import com.github.elenterius.blightlings.BlightlingsMod;
import com.github.elenterius.blightlings.init.ClientSetupHandler;
import com.github.elenterius.blightlings.item.IEntityUnveilerHeadSlotItem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@OnlyIn(Dist.CLIENT)
@Mod.EventBusSubscriber(modid = BlightlingsMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public final class ClientKeyInputHandler {
	private ClientKeyInputHandler() {}

	@SubscribeEvent
	public static void onKeyInput(final InputEvent.KeyInputEvent event) {
		ClientPlayerEntity player = Minecraft.getInstance().player;
		if (player != null && event.getKey() == ClientSetupHandler.DEFAULT_ITEM_KEY_BINDING.getKey().getKeyCode() && ClientSetupHandler.DEFAULT_ITEM_KEY_BINDING.isPressed()) {
			ItemStack stack = player.getItemStackFromSlot(EquipmentSlotType.HEAD);
			if (!stack.isEmpty() && stack.getItem() instanceof IEntityUnveilerHeadSlotItem) {
				CompoundNBT nbt = stack.getOrCreateTag();
				nbt.putBoolean("BlightlingsItemAbilityEnabled", !nbt.getBoolean("BlightlingsItemAbilityEnabled")); //TODO: replace with capability. sync to server?

				SoundEvent soundEvent = SoundEvents.ITEM_ARMOR_EQUIP_GENERIC;
				Item item = stack.getItem();
				if (item instanceof ArmorItem) {
					soundEvent = ((ArmorItem) item).getArmorMaterial().getSoundEvent();
				}
				player.playSound(soundEvent, 1.0F, 1.0F);
			}
		}
	}
}
