package com.github.elenterius.biomancy.network;

import com.github.elenterius.biomancy.handler.CustomClientEventHandler;
import com.google.common.primitives.UnsignedBytes;
import net.minecraft.client.network.play.ClientPlayNetHandler;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.network.INetHandler;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class EntityEventPacket {

	private final byte eventId; //unsigned byte (0 - 255)
	private final int entityId;

	public EntityEventPacket(Entity entity, int eventId) {
		this(entity, UnsignedBytes.checkedCast(eventId));
	}

	public EntityEventPacket(Entity entity, byte eventId) {
		this(entity.getId(), eventId);
	}

	public EntityEventPacket(int entityId, byte eventId) {
		this.entityId = entityId;
		this.eventId = eventId;
	}

	public static EntityEventPacket decode(final PacketBuffer packetBuffer) {
		return new EntityEventPacket(packetBuffer.readInt(), packetBuffer.readByte());
	}

	public static void handle(EntityEventPacket packet, Supplier<NetworkEvent.Context> ctx) {
		if (ctx.get().getDirection().getReceptionSide().isClient()) {
			ctx.get().enqueueWork(() -> {
				NetworkEvent.Context context = ctx.get();
				INetHandler handler = context.getNetworkManager().getPacketListener();
				if (handler instanceof ClientPlayNetHandler) {
					ClientWorld world = ((ClientPlayNetHandler) handler).getLevel();
					Entity entity = world.getEntity(packet.entityId);
					if (entity != null) {
						CustomClientEventHandler.onEntityEvent(entity, UnsignedBytes.toInt(packet.eventId));
					}
				}
			});
			ctx.get().setPacketHandled(true);
		}
	}

	public void encode(final PacketBuffer packetBuffer) {
		packetBuffer.writeInt(entityId);
		packetBuffer.writeByte(eventId);
	}

}
