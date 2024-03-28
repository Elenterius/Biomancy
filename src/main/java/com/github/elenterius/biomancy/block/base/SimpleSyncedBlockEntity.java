package com.github.elenterius.biomancy.block.base;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;

public abstract class SimpleSyncedBlockEntity extends BlockEntity {

	protected boolean reRenderBlockOnSync = false;

	protected SimpleSyncedBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
	}

	protected abstract void saveForSyncToClient(CompoundTag tag);

	protected void syncToClient() {
		if (level != null && !level.isClientSide) {
			BlockState state = getBlockState();
			level.sendBlockUpdated(getBlockPos(), state, state, Block.UPDATE_CLIENTS);
		}
	}

	@Override
	public CompoundTag getUpdateTag() {
		CompoundTag tag = new CompoundTag();
		saveForSyncToClient(tag);
		return tag;
	}

	@Override
	@Nullable
	public ClientboundBlockEntityDataPacket getUpdatePacket() {
		return ClientboundBlockEntityDataPacket.create(this);
	}

	@Override
	public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket packet) {
		super.onDataPacket(net, packet);
		if (reRenderBlockOnSync && level != null) {
			level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 0); //cause re-render
		}
	}

}
