package com.github.elenterius.biomancy.world.block;

import com.github.elenterius.biomancy.init.ModTags;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.FenceBlock;
import net.minecraft.world.level.block.FenceGateBlock;
import net.minecraft.world.level.block.state.BlockState;

public class FleshFenceBlock extends FenceBlock {

	public FleshFenceBlock(Properties properties) {
		super(properties);
	}

	@Override
	public boolean connectsTo(BlockState state, boolean isSideSolid, Direction direction) {
		return !isExceptionForConnection(state) && isSideSolid || canConnectToFence(state) || canConnectToFenceGate(state, direction);
	}

	protected boolean canConnectToFenceGate(BlockState state, Direction direction) {
		return state.getBlock() instanceof FenceGateBlock && FenceGateBlock.connectsToDirection(state, direction);
	}

	protected boolean canConnectToFence(BlockState state) {
		return state.is(BlockTags.FENCES) && state.is(ModTags.Blocks.FLESHY_FENCES);
	}

}
