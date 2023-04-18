package com.github.elenterius.biomancy.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class FleshLanternBlock extends Block implements SimpleWaterloggedBlock {

	public static final BooleanProperty HANGING = BlockStateProperties.HANGING;
	public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

	protected static final VoxelShape SHAPE = Shapes.or(
			Block.box(6, 9, 6, 10, 10, 10),
			Block.box(5, 2, 5, 11, 9, 11),
			Block.box(6, 0, 6, 10, 2, 10)
	);
	protected static final VoxelShape HANGING_SHAPE = Shapes.or(
			Block.box(6, 8, 6, 10, 10, 10),
			Block.box(5, 1, 5, 11, 8, 11),
			Block.box(6, 0, 6, 10, 1, 10)
	);

	public FleshLanternBlock(Properties properties) {
		super(properties);
		registerDefaultState(stateDefinition.any().setValue(HANGING, Boolean.FALSE).setValue(WATERLOGGED, Boolean.FALSE));
	}

	protected static Direction getConnectedDirection(BlockState state) {
		return isHanging(state) ? Direction.DOWN : Direction.UP;
	}

	public static boolean isHanging(BlockState state) {
		return state.getValue(HANGING);
	}

	public static boolean isWaterlogged(BlockState state) {
		return state.getValue(WATERLOGGED);
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(HANGING, WATERLOGGED);
	}

	@Nullable
	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		FluidState fluidState = context.getLevel().getFluidState(context.getClickedPos());

		for (Direction direction : context.getNearestLookingDirections()) {
			if (direction.getAxis() == Direction.Axis.Y) {
				BlockState state = defaultBlockState().setValue(HANGING, direction == Direction.UP);
				if (state.canSurvive(context.getLevel(), context.getClickedPos())) {
					return state.setValue(WATERLOGGED, fluidState.getType() == Fluids.WATER);
				}
			}
		}

		return null;
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		return isHanging(state) ? HANGING_SHAPE : SHAPE;
	}

	@Override
	public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos currentPos, BlockPos neighborPos) {
		if (isWaterlogged(state)) {
			level.scheduleTick(currentPos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
		}

		if (getConnectedDirection(state).getOpposite() == direction && !state.canSurvive(level, currentPos)) {
			return Blocks.AIR.defaultBlockState();
		}

		return super.updateShape(state, direction, neighborState, level, currentPos, neighborPos);
	}

	@Override
	public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
		Direction direction = getConnectedDirection(state).getOpposite();
		return Block.canSupportCenter(level, pos.relative(direction), direction.getOpposite());
	}

	@Override
	public FluidState getFluidState(BlockState state) {
		return isWaterlogged(state) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
	}

	@Override
	public boolean isPathfindable(BlockState state, BlockGetter level, BlockPos pos, PathComputationType type) {
		return false;
	}

	@Override
	public PushReaction getPistonPushReaction(BlockState state) {
		return PushReaction.DESTROY;
	}

}
