package com.github.elenterius.biomancy.network;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

//client bound message
public class CreatorAttackClientMessage {

	public final BlockPos pos;

	public CreatorAttackClientMessage(BlockPos pos) {
		this.pos = pos;
	}

	public static CreatorAttackClientMessage decode(final FriendlyByteBuf buffer) {
		return new CreatorAttackClientMessage(buffer.readBlockPos());
	}

	public static void handle(CreatorAttackClientMessage msg, Supplier<NetworkEvent.Context> ctx) {
		ctx.get().enqueueWork(() -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> ClientPacketHandler.handlePacket(msg, ctx)));
		ctx.get().setPacketHandled(true);
	}

	public void encode(final FriendlyByteBuf buffer) {
		buffer.writeBlockPos(pos);
	}

}
