package com.github.elenterius.biomancy.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.Direction;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReader;

import javax.annotation.Nullable;

public class BlightPustuleBlock extends FleshPlantBlock {
	public static final DirectionProperty FACING = BlockStateProperties.FACING;

	protected static final VoxelShape SHAPE_UP = Block.makeCuboidShape(2.0D, 0.0D, 2.0D, 14.0D, 4.0D, 14.0D);
	protected static final VoxelShape SHAPE_DOWN = Block.makeCuboidShape(2.0D, 12.0D, 2.0D, 14.0D, 16.0D, 14.0D);
	protected static final VoxelShape SHAPE_EAST = Block.makeCuboidShape(0.0D, 2.0D, 2.0D, 4.0D, 14.0D, 14.0D);
	protected static final VoxelShape SHAPE_WEST = Block.makeCuboidShape(12.0D, 2.0D, 2.0D, 16.0D, 14.0D, 14.0D);
	protected static final VoxelShape SHAPE_SOUTH = Block.makeCuboidShape(2.0D, 2.0D, 0.0D, 14.0D, 14.0D, 4.0D);
	protected static final VoxelShape SHAPE_NORTH = Block.makeCuboidShape(2.0D, 2.0D, 12.0D, 14.0D, 14.0D, 16.0D);

	public BlightPustuleBlock(Properties properties) {
		super(properties);
		setDefaultState(getDefaultState().with(FACING, Direction.UP));
	}

	@Override
	protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
		builder.add(FACING);
	}

	@Override
	public boolean isValidPosition(BlockState state, IWorldReader worldIn, BlockPos pos) {
		Direction direction = state.get(FACING);
		BlockPos blockpos = pos.offset(direction.getOpposite());
		BlockState blockstate = worldIn.getBlockState(blockpos);
		return blockstate.isSolidSide(worldIn, blockpos, direction);
	}

	@Nullable
	@Override
	public BlockState getStateForPlacement(BlockItemUseContext context) {
		return getDefaultState().with(FACING, context.getFace());
	}

	@Override
	public BlockState rotate(BlockState state, Rotation rot) {
		return state.with(FACING, rot.rotate(state.get(FACING)));
	}

	@Override
	public BlockState mirror(BlockState state, Mirror mirrorIn) {
		return state.rotate(mirrorIn.toRotation(state.get(FACING)));
	}

	@Override
	public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
		Vector3d vec = state.getOffset(worldIn, pos);
		switch (state.get(FACING)) {
			case UP:
			default:
				return SHAPE_UP.withOffset(vec.x, vec.y, vec.z);
			case DOWN:
				return SHAPE_DOWN.withOffset(vec.x, vec.y, vec.z);
			case NORTH:
				return SHAPE_NORTH.withOffset(vec.x, vec.y, vec.z);
			case SOUTH:
				return SHAPE_SOUTH.withOffset(vec.x, vec.y, vec.z);
			case WEST:
				return SHAPE_WEST.withOffset(vec.x, vec.y, vec.z);
			case EAST:
				return SHAPE_EAST.withOffset(vec.x, vec.y, vec.z);
		}
	}

	@Override
	public OffsetType getOffsetType() {
		return OffsetType.NONE;
	}
}
