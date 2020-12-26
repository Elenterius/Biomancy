package com.github.elenterius.blightlings.network;

import com.github.elenterius.blightlings.BlightlingsMod;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;

public final class ModNetworkHandler {
	private static final String PROTOCOL_VERSION = "1";
	public static final SimpleChannel SIMPLE_NETWORK_CHANNEL = NetworkRegistry
			.newSimpleChannel(new ResourceLocation(BlightlingsMod.MOD_ID, "key_bind"), () -> PROTOCOL_VERSION, PROTOCOL_VERSION::equals, PROTOCOL_VERSION::equals);

	private ModNetworkHandler() {}

	public static void sendKeyBindPressToServer(EquipmentSlotType slotType, byte flag) {
		SIMPLE_NETWORK_CHANNEL.sendToServer(new KeyBindPacket(slotType.getSlotIndex(), flag));
	}

	public static void sendKeyBindPressToServer(int slotIndex, byte flag) {
		SIMPLE_NETWORK_CHANNEL.sendToServer(new KeyBindPacket(slotIndex, flag));
	}

	public static void register() {
		SIMPLE_NETWORK_CHANNEL.registerMessage(0, KeyBindPacket.class, KeyBindPacket::encode, KeyBindPacket::decode, KeyBindPacket::handle);
	}
}
