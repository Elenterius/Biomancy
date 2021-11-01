package com.github.elenterius.biomancy.network;

import com.github.elenterius.biomancy.handler.event.InventoryContainerHandler;
import com.google.common.primitives.UnsignedBytes;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.PlayerContainer;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class CarriedItemPacket {

	private final int slotIndex;    //index of the slot the carried stack is interacting with
	private final byte data;        //unsigned byte (0 - 255)
	private final boolean hasStack;
	private final ItemStack carriedStack;

	protected CarriedItemPacket(ItemStack carriedStack, int slotIndex, int data) {
		this.carriedStack = carriedStack;
		this.data = UnsignedBytes.checkedCast(data);
		hasStack = !carriedStack.isEmpty();
		this.slotIndex = slotIndex;
	}

	public static CarriedItemPacket decode(final PacketBuffer buffer) {
		int slotIndex = buffer.readVarInt();
		byte flags = buffer.readByte();
		boolean isStackPresent = buffer.readBoolean();
		ItemStack stack = isStackPresent ? buffer.readItem() : ItemStack.EMPTY;
		return new CarriedItemPacket(stack, slotIndex, flags);
	}

	public static void handle(final CarriedItemPacket packet, Supplier<NetworkEvent.Context> ctx) {
		ctx.get().enqueueWork(() -> {
			LogicalSide side = ctx.get().getDirection().getReceptionSide();
			if (side.isServer()) {
				ServerPlayerEntity player = ctx.get().getSender();
				if (player != null) {
					Container container = player.containerMenu;
					//we only "trust" and read the carriedStack from Creative Players inside the PlayerContainer since the creative screen doesn't sync the carriedStack to the server
					ItemStack carriedStack = player.isCreative() && container instanceof PlayerContainer ? packet.carriedStack : player.inventory.getCarried();
					InventoryContainerHandler.onServerReceiveSlotInteraction(container, player, carriedStack, packet.slotIndex, UnsignedBytes.toInt(packet.data));
				}
			}
			else if (side.isClient()) {
				handleClientSide(packet.carriedStack, UnsignedBytes.toInt(packet.data));
			}
		});
		ctx.get().setPacketHandled(true);
	}

	private static void handleClientSide(ItemStack stack, int flags) {
		ClientPlayerEntity player = Minecraft.getInstance().player;
		if (player != null) {
			InventoryContainerHandler.onClientReceiveSlotInteraction(player, stack, flags);
		}
	}

	public void encode(final PacketBuffer buffer) {
		buffer.writeVarInt(slotIndex);
		buffer.writeByte(data);
		buffer.writeBoolean(hasStack);
		if (hasStack) buffer.writeItem(carriedStack);
	}

}