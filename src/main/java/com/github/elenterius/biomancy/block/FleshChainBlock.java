package com.github.elenterius.biomancy.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class FleshChainBlock extends RotatedPillarBlock implements SimpleWaterloggedBlock {

	public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
	protected static final VoxelShape Y_AXIS_SHAPE = Block.box(6.5d, 0, 6.5d, 9.5d, 16, 9.5d);
	protected static final VoxelShape Z_AXIS_SHAPE = Block.box(6.5d, 6.5d, 0, 9.5d, 9.5d, 16);
	protected static final VoxelShape X_AXIS_SHAPE = Block.box(0, 6.5d, 6.5d, 16, 9.5d, 9.5d);

	public FleshChainBlock(Properties properties) {
		super(properties);
		registerDefaultState(stateDefinition.any().setValue(WATERLOGGED, Boolean.FALSE).setValue(AXIS, Direction.Axis.Y));
	}

	public static Boolean isWaterlogged(BlockState state) {
		return state.getValue(WATERLOGGED);
	}

	@Override
	public boolean isLadder(BlockState state, LevelReader level, BlockPos pos, LivingEntity entity) {
		return true;
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(WATERLOGGED).add(AXIS);
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		FluidState fluidstate = context.getLevel().getFluidState(context.getClickedPos());
		boolean isWaterlogged = fluidstate.getType() == Fluids.WATER;
		return super.getStateForPlacement(context).setValue(WATERLOGGED, isWaterlogged);
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		return switch (state.getValue(AXIS)) {
			case X -> X_AXIS_SHAPE;
			case Z -> Z_AXIS_SHAPE;
			case Y -> Y_AXIS_SHAPE;
		};
	}

	@Override
	public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos currentPos, BlockPos neighborPos) {
		if (isWaterlogged(state)) {
			level.scheduleTick(currentPos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
		}

		return super.updateShape(state, direction, neighborState, level, currentPos, neighborPos);
	}

	@Override
	public FluidState getFluidState(BlockState state) {
		return isWaterlogged(state) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
	}

	@Override
	public boolean isPathfindable(BlockState state, BlockGetter level, BlockPos pos, PathComputationType type) {
		return false;
	}

}
