package com.github.elenterius.biomancy.block;

import com.github.elenterius.biomancy.block.property.Orientation;
import com.github.elenterius.biomancy.init.ModBlockProperties;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class PaneBlock extends Block implements SimpleWaterloggedBlock {

	public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
	public static final EnumProperty<Orientation> ORIENTATION = ModBlockProperties.ORIENTATION;
	protected static final int THICKNESS = 4;
	protected static final VoxelShape Y_POS_AABB = createShape(0, 0, 0, 16, THICKNESS, 16);
	protected static final VoxelShape Y_NONE_AABB = createShape(0, 8d - THICKNESS / 2d, 0, 16, 8d + THICKNESS / 2d, 16);
	protected static final VoxelShape Y_NEG_AABB = createShape(0, 16d - THICKNESS, 0, 16, 16, 16);
	protected static final VoxelShape x_NEG_AABB = createShape(0, 0, 0, THICKNESS, 16, 16);
	protected static final VoxelShape x_NONE_AABB = createShape(8d - THICKNESS / 2d, 0, 0, 8d + THICKNESS / 2d, 16, 16);
	protected static final VoxelShape X_POS_AABB = createShape(16d - THICKNESS, 0, 0, 16, 16, 16);
	protected static final VoxelShape Z_NEG_AABB = createShape(0, 0, 0, 16, 16, THICKNESS);
	protected static final VoxelShape Z_NONE_AABB = createShape(0, 0, 8d - THICKNESS / 2d, 16, 16, 8d + THICKNESS / 2d);
	protected static final VoxelShape Z_POS_AABB = createShape(0, 0, 16d - THICKNESS, 16, 16, 16);

	public PaneBlock(Properties properties) {
		super(properties);
		registerDefaultState(defaultBlockState()
				.setValue(ORIENTATION, Orientation.Y_MIDDLE)
				.setValue(WATERLOGGED, false)
		);
	}

	protected static VoxelShape createShape(double x0, double y0, double z0, double x1, double y1, double z1) {
		return Block.box(x0, y0, z0, x1, y1, z1);
	}

	public static Orientation getOrientation(BlockState state) {
		return state.getValue(ORIENTATION);
	}

	public static boolean canWallConnectToPane(BlockState paneState, Direction wallDirection) {
		Orientation orientation = PaneBlock.getOrientation(paneState);
		return orientation.isMiddle() && orientation.axis == wallDirection.getClockWise().getAxis();
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(ORIENTATION, WATERLOGGED);
	}

	@Nullable
	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		BlockState state = defaultBlockState();
		FluidState fluidState = context.getLevel().getFluidState(context.getClickedPos());

		if (!context.replacingClickedOnBlock()) {
			Orientation orientation = Orientation.getOrientationFrom(context);
			state = state.setValue(ORIENTATION, orientation);
		}

		return state.setValue(WATERLOGGED, fluidState.getType() == Fluids.WATER);
	}

	@Override
	public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
		if (isWaterlogged(state)) level.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
		return super.use(state, level, pos, player, hand, hit);
	}

	@Override
	public void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, BlockPos fromPos, boolean isMoving) {
		if (level.isClientSide()) return;
		if (isWaterlogged(state)) level.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
	}

	@Override
	public FluidState getFluidState(BlockState state) {
		return isWaterlogged(state) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
	}

	public boolean isWaterlogged(BlockState state) {
		return state.getValue(WATERLOGGED);
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		return switch (state.getValue(ORIENTATION)) {
			case X_POSITIVE -> X_POS_AABB;
			case X_MIDDLE -> x_NONE_AABB;
			case X_NEGATIVE -> x_NEG_AABB;
			case Z_POSITIVE -> Z_POS_AABB;
			case Z_MIDDLE -> Z_NONE_AABB;
			case Z_NEGATIVE -> Z_NEG_AABB;
			case Y_POSITIVE -> Y_POS_AABB;
			case Y_NEGATIVE -> Y_NEG_AABB;
			case Y_MIDDLE -> Y_NONE_AABB;
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
	public BlockState rotate(BlockState state, Rotation rotationDirection) {
		Orientation orientation = getOrientation(state);
		return state.setValue(ORIENTATION, orientation.rotate(rotationDirection));
	}

	@Override
	public BlockState mirror(BlockState state, Mirror mirror) {
		Orientation orientation = getOrientation(state);
		return state.setValue(ORIENTATION, orientation.mirror(mirror));
	}

	@Override
	public boolean isPathfindable(BlockState state, BlockGetter level, BlockPos pos, PathComputationType type) {
		return false;
	}

}
