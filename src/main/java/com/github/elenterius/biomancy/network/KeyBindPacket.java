package com.github.elenterius.biomancy.network;

import com.github.elenterius.biomancy.item.IKeyListener;
import com.google.common.primitives.UnsignedBytes;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.INetHandler;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.ServerPlayNetHandler;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class KeyBindPacket {

	public final byte slotIndex; //unsigned byte (0 - 255)
	public final byte flag;

	public KeyBindPacket(int slotIndex, byte flag) {
		this.slotIndex = UnsignedBytes.checkedCast(slotIndex);
		this.flag = flag;
	}

	public KeyBindPacket(byte slotIndex, byte flag) {
		this.slotIndex = slotIndex;
		this.flag = flag;
	}

	public static KeyBindPacket decode(final PacketBuffer packetBuffer) {
		return new KeyBindPacket(packetBuffer.readByte(), packetBuffer.readByte());
	}

	public static void handle(KeyBindPacket packet, Supplier<NetworkEvent.Context> ctx) {
		ctx.get().enqueueWork(() -> {
			NetworkEvent.Context context = ctx.get();
			INetHandler handler = context.getNetworkManager().getNetHandler();
			if (handler instanceof ServerPlayNetHandler) {
				ServerWorld world = (ServerWorld) ((ServerPlayNetHandler) handler).player.world;
				ServerPlayerEntity playerEntity = ((ServerPlayNetHandler) handler).player;
				IKeyListener.onReceiveKeybindingPacket(world, playerEntity, UnsignedBytes.toInt(packet.slotIndex), packet.flag); //TODO: add version which is not tied to EquipmentSlotType
			}
		});
		ctx.get().setPacketHandled(true);
	}

	public void encode(final PacketBuffer packetBuffer) {
		packetBuffer.writeByte(slotIndex);
		packetBuffer.writeByte(flag);
	}

}
