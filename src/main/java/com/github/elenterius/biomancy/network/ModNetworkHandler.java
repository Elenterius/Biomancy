package com.github.elenterius.biomancy.network;

import com.github.elenterius.biomancy.BiomancyMod;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.PacketDistributor;
import net.minecraftforge.fml.network.simple.SimpleChannel;

public final class ModNetworkHandler {
	private static final String PROTOCOL_VERSION = "1";
	public static final SimpleChannel SIMPLE_NETWORK_CHANNEL = NetworkRegistry
			.newSimpleChannel(new ResourceLocation(BiomancyMod.MOD_ID, "main"), () -> PROTOCOL_VERSION, PROTOCOL_VERSION::equals, PROTOCOL_VERSION::equals);

	private ModNetworkHandler() {}

	public static void sendKeyBindPressToServer(EquipmentSlotType slotType, byte flag) {
		SIMPLE_NETWORK_CHANNEL.sendToServer(new KeyBindPacket(slotType.getFilterFlag(), flag));
	}

	public static void sendKeyBindPressToServer(int slotIndex, byte flag) {
		SIMPLE_NETWORK_CHANNEL.sendToServer(new KeyBindPacket(slotIndex, flag));
	}

	public static void sendCustomEntityEventToClients(Entity entity, int eventId) {
		SIMPLE_NETWORK_CHANNEL.send(PacketDistributor.TRACKING_ENTITY.with(() -> entity), new EntityEventPacket(entity, eventId));
	}

	public static void sendCustomEntityEventToClient(Entity entity, int eventId, ServerPlayerEntity player) {
		SIMPLE_NETWORK_CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), new EntityEventPacket(entity, eventId));
	}

	public static void register() {
		SIMPLE_NETWORK_CHANNEL.registerMessage(0, KeyBindPacket.class, KeyBindPacket::encode, KeyBindPacket::decode, KeyBindPacket::handle);
		SIMPLE_NETWORK_CHANNEL.registerMessage(1, EntityEventPacket.class, EntityEventPacket::encode, EntityEventPacket::decode, EntityEventPacket::handle);
	}
}
