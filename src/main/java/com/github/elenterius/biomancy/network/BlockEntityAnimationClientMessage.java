package com.github.elenterius.biomancy.network;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

//client bound message
public class BlockEntityAnimationClientMessage {

	public final BlockPos pos;
	public final int id;
	public final int data;

	public BlockEntityAnimationClientMessage(BlockPos pos, int id, int data) {
		this.pos = pos;
		this.id = id;
		this.data = data;
	}

	public static BlockEntityAnimationClientMessage decode(final FriendlyByteBuf buffer) {
		BlockPos blockPos = buffer.readBlockPos();
		int id = buffer.readVarInt();
		int data = buffer.readVarInt();

		return new BlockEntityAnimationClientMessage(blockPos, id, data);
	}

	public void encode(final FriendlyByteBuf buffer) {
		buffer.writeBlockPos(pos);
		buffer.writeVarInt(id);
		buffer.writeVarInt(data);
	}

	public static void handle(BlockEntityAnimationClientMessage msg, Supplier<NetworkEvent.Context> ctx) {
		ctx.get().enqueueWork(() -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> ClientPacketHandler.handlePacket(msg, ctx)));
		ctx.get().setPacketHandled(true);
	}

}
