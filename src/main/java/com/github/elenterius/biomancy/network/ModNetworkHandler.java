package com.github.elenterius.biomancy.network;

import com.github.elenterius.biomancy.BiomancyMod;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

public final class ModNetworkHandler {

	private static final String PROTOCOL_VERSION = "1";
	public static final SimpleChannel SIMPLE_NETWORK_CHANNEL = NetworkRegistry.newSimpleChannel(BiomancyMod.createRL("main"), () -> PROTOCOL_VERSION, PROTOCOL_VERSION::equals, PROTOCOL_VERSION::equals);

	private ModNetworkHandler() {}

	public static void sendKeyBindPressToServer(EquipmentSlot slot, byte flag) {
		SIMPLE_NETWORK_CHANNEL.sendToServer(new KeyPressMessage(slot.getFilterFlag(), flag));
	}

	public static void sendKeyBindPressToServer(int slotIndex, byte flag) {
		SIMPLE_NETWORK_CHANNEL.sendToServer(new KeyPressMessage(slotIndex, flag));
	}

	public static void register() {
		SIMPLE_NETWORK_CHANNEL.registerMessage(0, KeyPressMessage.class, KeyPressMessage::encode, KeyPressMessage::decode, KeyPressMessage::handle);
	}

}
