package com.github.elenterius.biomancy.network;

import com.github.elenterius.biomancy.item.KeyPressListener;
import com.google.common.primitives.UnsignedBytes;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class KeyPressMessage {

	public final byte slotIndex; //unsigned byte (0 - 255)
	public final byte flag;

	public KeyPressMessage(int slotIndex, byte flag) {
		this.slotIndex = UnsignedBytes.checkedCast(slotIndex);
		this.flag = flag;
	}

	public KeyPressMessage(byte slotIndex, byte flag) {
		this.slotIndex = slotIndex;
		this.flag = flag;
	}

	public static void handle(KeyPressMessage packet, Supplier<NetworkEvent.Context> ctx) {
		ctx.get().enqueueWork(() -> {
			ServerPlayer player = ctx.get().getSender();
			if (player != null) {
				ServerLevel world = player.getLevel();
				KeyPressListener.onReceiveKeybindingPacket(world, player, UnsignedBytes.toInt(packet.slotIndex), packet.flag); //TODO: add version which is not tied to EquipmentSlotType
			}
		});
		ctx.get().setPacketHandled(true);
	}

	public static KeyPressMessage decode(final FriendlyByteBuf byteBuf) {
		byte slotIndex = byteBuf.readByte();
		byte flag = byteBuf.readByte();

		return new KeyPressMessage(slotIndex, flag);
	}

	public void encode(final FriendlyByteBuf byteBuf) {
		byteBuf.writeByte(slotIndex);
		byteBuf.writeByte(flag);
	}

}
