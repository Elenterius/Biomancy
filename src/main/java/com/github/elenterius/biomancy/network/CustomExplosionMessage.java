package com.github.elenterius.biomancy.network;

import com.github.elenterius.biomancy.mixin.accessor.ExplosionAccessor;
import com.github.elenterius.biomancy.util.ClientUtil;
import com.github.elenterius.biomancy.util.explosion.ExplosionType;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkEvent;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.function.Supplier;

/// client bound message
public class CustomExplosionMessage {

	private final ExplosionType type;
	private final @Nullable Integer sourceId;

	private final double x;
	private final double y;
	private final double z;

	private final float radius;
	private final List<BlockPos> toBlow;

	private final float knockbackX;
	private final float knockbackY;
	private final float knockbackZ;

	public CustomExplosionMessage(ExplosionType type, Explosion explosion, ServerPlayer serverPlayer) {
		this.type = type;

		Entity source = explosion.getDirectSourceEntity();
		sourceId = source != null ? source.getId() : null;

		Vec3 position = explosion.getPosition();
		x = position.x;
		y = position.y;
		z = position.z;

		radius = ((ExplosionAccessor) explosion).getRadius();
		toBlow = explosion.getToBlow();

		Vec3 knockback = explosion.getHitPlayers().get(serverPlayer);

		if (knockback != null) {
			knockbackX = (float) knockback.x;
			knockbackY = (float) knockback.y;
			knockbackZ = (float) knockback.z;
		}
		else {
			knockbackX = 0f;
			knockbackY = 0f;
			knockbackZ = 0f;
		}
	}

	public CustomExplosionMessage(ExplosionType type, @Nullable Integer sourceId, double x, double y, double z, float radius, List<BlockPos> toBlow, float knockbackX, float knockbackY, float knockbackZ) {
		this.type = type;
		this.sourceId = sourceId;
		this.x = x;
		this.y = y;
		this.z = z;
		this.radius = radius;
		this.toBlow = toBlow;
		this.knockbackX = knockbackX;
		this.knockbackY = knockbackY;
		this.knockbackZ = knockbackZ;
	}

	public static void handle(CustomExplosionMessage packet, Supplier<NetworkEvent.Context> ctx) {
		NetworkEvent.Context context = ctx.get();

		if (context.getDirection().getReceptionSide().isClient()) {
			context.enqueueWork(() -> ClientHandler.handle(packet));
		}

		context.setPacketHandled(true);
	}

	public void encode(final FriendlyByteBuf buffer) {
		buffer.writeEnum(type);

		if (sourceId != null) {
			buffer.writeBoolean(true);
			buffer.writeVarInt(sourceId);
		}
		else {
			buffer.writeBoolean(false);
		}

		buffer.writeDouble(x);
		buffer.writeDouble(y);
		buffer.writeDouble(z);
		buffer.writeFloat(radius);

		int xi = Mth.floor(x);
		int yi = Mth.floor(y);
		int zi = Mth.floor(z);
		buffer.writeCollection(toBlow, (buf, pos) -> {
			buf.writeByte(pos.getX() - xi);
			buf.writeByte(pos.getY() - yi);
			buf.writeByte(pos.getZ() - zi);
		});

		buffer.writeFloat(knockbackX);
		buffer.writeFloat(knockbackY);
		buffer.writeFloat(knockbackZ);
	}

	public static CustomExplosionMessage decode(final FriendlyByteBuf buffer) {
		ExplosionType type = buffer.readEnum(ExplosionType.class);

		Integer sourceId = null;
		if (buffer.readBoolean()) {
			sourceId = buffer.readVarInt();
		}

		double x = buffer.readDouble();
		double y = buffer.readDouble();
		double z = buffer.readDouble();
		float power = buffer.readFloat();

		int xi = Mth.floor(x);
		int yi = Mth.floor(y);
		int zi = Mth.floor(z);
		List<BlockPos> toBlow = buffer.readList(buf -> new BlockPos(
				buf.readByte() + xi,
				buf.readByte() + yi,
				buf.readByte() + zi
		));

		float knockbackX = buffer.readFloat();
		float knockbackY = buffer.readFloat();
		float knockbackZ = buffer.readFloat();

		return new CustomExplosionMessage(type, sourceId, x, y, z, power, toBlow, knockbackX, knockbackY, knockbackZ);
	}

	private static class ClientHandler {

		private static void handle(CustomExplosionMessage packet) {
			Level level = ClientUtil.getLevel();
			Player player = ClientUtil.getPlayer();
			if (level == null || player == null) return;

			Entity source = ClientUtil.getEntity(packet.sourceId);
			Explosion explosion = packet.type.clientFactory.create(level, source, packet.x, packet.y, packet.z, packet.radius, packet.toBlow);
			explosion.finalizeExplosion(true);
			player.setDeltaMovement(player.getDeltaMovement().add(packet.knockbackX, packet.knockbackY, packet.knockbackZ));
		}

	}

}
