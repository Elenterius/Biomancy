package com.github.elenterius.biomancy.tileentity;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;

import javax.annotation.Nullable;

public abstract class SimpleSyncedTileEntity extends TileEntity {

	public SimpleSyncedTileEntity(TileEntityType<?> tileEntityTypeIn) {
		super(tileEntityTypeIn);
	}

	@Override
	@Nullable
	public SUpdateTileEntityPacket getUpdatePacket() {
		// on (client side call of) World.notifyBlockUpdate(...) resynchronize client with this packet
		return new SUpdateTileEntityPacket(this.worldPosition, -1, getUpdateTag());
	}

	@Override
	public CompoundNBT getUpdateTag() {
		// synchronizing on initial chunk load, or when many blocks change at once
		return save(new CompoundNBT());
	}

	@Override
	public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt) {
		BlockState state = level != null ? level.getBlockState(worldPosition) : Blocks.AIR.defaultBlockState();
		handleUpdateTag(state, pkt.getTag());
	}

	@Override
	public void handleUpdateTag(BlockState blockState, CompoundNBT tag) {
		load(blockState, tag);
	}

}
