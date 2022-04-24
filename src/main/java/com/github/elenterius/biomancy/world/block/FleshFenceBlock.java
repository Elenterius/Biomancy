package com.github.elenterius.biomancy.world.block;

import com.github.elenterius.biomancy.init.ModTags;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.FenceBlock;
import net.minecraft.world.level.block.FenceGateBlock;
import net.minecraft.world.level.block.PipeBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;

public class FleshFenceBlock extends FenceBlock {

	public static final BooleanProperty UP = PipeBlock.UP;

	public FleshFenceBlock(Properties properties) {
		super(properties);
		registerDefaultState(defaultBlockState().setValue(UP, true));
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(UP);
		super.createBlockStateDefinition(builder);
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		BlockState state = super.getStateForPlacement(context);
		return state != null ? state.setValue(UP, isSpikeHidden(context.getLevel(), context.getClickedPos())) : null;
	}

	@Override
	public BlockState updateShape(BlockState state, Direction facing, BlockState facingState, LevelAccessor level, BlockPos currentPos, BlockPos facingPos) {
		BlockState newState = super.updateShape(state, facing, facingState, level, currentPos, facingPos);
		return facing == Direction.UP ? newState.setValue(UP, isSpikeHidden(level, currentPos)) : newState;
	}

	private boolean isSpikeHidden(LevelAccessor level, BlockPos pos) {
		return !level.getBlockState(pos.above()).isAir(); //stateAbove.isFaceSturdy(level, posAbove, Direction.DOWN, SupportType.CENTER);
	}

	@Override
	public boolean connectsTo(BlockState state, boolean isSideSolid, Direction direction) {
		Block block = state.getBlock();
		boolean validFenceGate = block instanceof FenceGateBlock && FenceGateBlock.connectsToDirection(state, direction);
		return !isExceptionForConnection(state) && isSideSolid || isSameFence(state) || validFenceGate;
	}

	protected boolean isSameFence(BlockState state) {
		return state.is(BlockTags.FENCES) && state.is(ModTags.Blocks.FLESHY_FENCES);
	}

}
