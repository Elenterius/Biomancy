package com.github.elenterius.biomancy.network;

import com.github.elenterius.biomancy.BiomancyMod;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.screen.inventory.CreativeScreen;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
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

	public static void sendCustomEntityEventToClient(ServerPlayerEntity receiver, Entity entity, int eventId) {
		SIMPLE_NETWORK_CHANNEL.send(PacketDistributor.PLAYER.with(() -> receiver), new EntityEventPacket(entity, eventId));
	}

	public static void sendCarriedItemToServer(ContainerScreen<?> screen, ClientPlayerEntity player, ItemStack carriedStack, int slotIndex, int data) {
		//only send the carriedStack to server if the player is in creative screen (it doesn't sync carriedStack)
		SIMPLE_NETWORK_CHANNEL.sendToServer(new CarriedItemPacket(player.isCreative() && screen instanceof CreativeScreen ? carriedStack : ItemStack.EMPTY, slotIndex, data));
	}

	public static void sendCarriedItemToClient(ServerPlayerEntity receiver, ItemStack carriedStack, int data) {
		//here we can send the carriedStack to the client
		SIMPLE_NETWORK_CHANNEL.send(PacketDistributor.PLAYER.with(() -> receiver), new CarriedItemPacket(carriedStack, 0, data));
	}

	public static void register() {
		SIMPLE_NETWORK_CHANNEL.registerMessage(0, KeyBindPacket.class, KeyBindPacket::encode, KeyBindPacket::decode, KeyBindPacket::handle);
		SIMPLE_NETWORK_CHANNEL.registerMessage(1, EntityEventPacket.class, EntityEventPacket::encode, EntityEventPacket::decode, EntityEventPacket::handle);
		SIMPLE_NETWORK_CHANNEL.registerMessage(2, CarriedItemPacket.class, CarriedItemPacket::encode, CarriedItemPacket::decode, CarriedItemPacket::handle);
	}
}
