package com.github.elenterius.biomancy.network;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkEvent;

import javax.annotation.Nullable;
import java.util.function.Supplier;

//client bound message
public class BioForgeRecipeClientMessage {

	@Nullable
	public final ResourceLocation id;
	public final BlockPos pos;

	public BioForgeRecipeClientMessage(BlockPos pos, @Nullable ResourceLocation recipeId) {
		this.id = recipeId;
		this.pos = pos;
	}

	public static BioForgeRecipeClientMessage decode(final FriendlyByteBuf buffer) {
		BlockPos blockPos = buffer.readBlockPos();
		boolean isRecipePresent = buffer.readBoolean();
		ResourceLocation recipeId = isRecipePresent ? buffer.readResourceLocation() : null;
		return new BioForgeRecipeClientMessage(blockPos, recipeId);
	}

	public static void handle(BioForgeRecipeClientMessage msg, Supplier<NetworkEvent.Context> ctx) {
		//ctx.get().enqueueWork(() -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> ClientPacketHandler.handlePacket(msg, ctx)));
		ctx.get().setPacketHandled(true);
	}

	public void encode(final FriendlyByteBuf buffer) {
		buffer.writeBlockPos(pos);
		buffer.writeBoolean(id != null); // is recipe present
		if (id != null) buffer.writeResourceLocation(id);
	}

}
