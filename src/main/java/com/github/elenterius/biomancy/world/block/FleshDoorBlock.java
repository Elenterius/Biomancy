package com.github.elenterius.biomancy.world.block;

import com.github.elenterius.biomancy.init.ModBlocks;
import com.github.elenterius.biomancy.init.ModSoundEvents;
import com.github.elenterius.biomancy.world.block.property.Orientation;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class FleshDoorBlock extends Block {

	public static final BooleanProperty OPEN = BlockStateProperties.OPEN;
	public static final BooleanProperty POWERED = BlockStateProperties.POWERED;

	public static final EnumProperty<Orientation> ORIENTATION = ModBlocks.ORIENTATION;
	public static final EnumProperty<DoubleBlockHalf> HALF = BlockStateProperties.DOUBLE_BLOCK_HALF;

	public static final int UPDATE_FLAGS = Block.UPDATE_CLIENTS | Block.UPDATE_IMMEDIATE; //10

	protected static final int THICKNESS = 2;
	protected static final int CLOSED_SHAPE_INDEX = 0;
	protected static final int OPEN_SHAPE_INDEX = 1;
	protected static final VoxelShape[] x_NEG_AABB = createClosedAndOpenShape(0, 0, 0, THICKNESS, 16, 16);
	protected static final VoxelShape[] x_NONE_AABB = createClosedAndOpenShape(8d - THICKNESS / 2d, 0, 0, 8d + THICKNESS / 2d, 16, 16);
	protected static final VoxelShape[] X_POS_AABB = createClosedAndOpenShape(16d - THICKNESS, 0, 0, 16, 16, 16);
	protected static final VoxelShape[] Z_NEG_AABB = createClosedAndOpenShape(0, 0, 0, 16, 16, THICKNESS);
	protected static final VoxelShape[] Z_NONE_AABB = createClosedAndOpenShape(0, 0, 8d - THICKNESS / 2d, 16, 16, 8d + THICKNESS / 2d);
	protected static final VoxelShape[] Z_POS_AABB = createClosedAndOpenShape(0, 0, 16d - THICKNESS, 16, 16, 16);

	private static VoxelShape[] createClosedAndOpenShape(double x0, double y0, double z0, double x1, double y1, double z1) {
		VoxelShape closedShape = Block.box(x0, y0, z0, x1, y1, z1);

		boolean caseX = x0 == 0 && x1 == 16;
		boolean caseZ = z0 == 0 && z1 == 16;
		double offset = 2;
		double xA = caseX ? x0 + offset : x0;
		double zA = caseZ ? z0 + offset : z0;
		double xB = caseX ? x1 - offset : x1;
		double zB = caseZ ? z1 - offset : z1;
		VoxelShape openShapeBottom = Shapes.join(closedShape, Block.box(xA, y0 + offset, zA, xB, y1, zB), BooleanOp.NOT_SAME);
		VoxelShape openShapeTop = Shapes.join(closedShape, Block.box(xA, y0, zA, xB, y1 - offset, zB), BooleanOp.NOT_SAME);
		VoxelShape openShapeForCollision = Shapes.join(closedShape, Block.box(xA, y0, zA, xB, y1, zB), BooleanOp.NOT_SAME);

		return new VoxelShape[]{closedShape, openShapeBottom, openShapeTop, openShapeForCollision};
	}

	public FleshDoorBlock(Properties properties) {
		super(properties);
		registerDefaultState(defaultBlockState()
				.setValue(ORIENTATION, Orientation.Y_MIDDLE)
				.setValue(OPEN, false).setValue(POWERED, false)
				.setValue(HALF, DoubleBlockHalf.LOWER)
		);
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(ORIENTATION, OPEN, POWERED, HALF);
	}

	@Nullable
	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		BlockPos pos = context.getClickedPos();
		Level level = context.getLevel();
		if (pos.getY() >= level.getMaxBuildHeight() - 1 || !level.getBlockState(pos.above()).canBeReplaced(context)) {
			return null;
		}

		BlockState state = defaultBlockState();
		if (!context.replacingClickedOnBlock()) {
			Orientation orientation = Orientation.getXZOrientationFrom(context);
			state = state.setValue(ORIENTATION, orientation);
		}

		if (level.hasNeighborSignal(pos) || level.hasNeighborSignal(pos.above())) {
			state = state.setValue(OPEN, true).setValue(POWERED, true);
		}

		return state.setValue(HALF, DoubleBlockHalf.LOWER);
	}

	@Override
	public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
		level.setBlock(pos.above(), state.setValue(HALF, DoubleBlockHalf.UPPER), Block.UPDATE_ALL);
	}

	@Override
	public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
		state = state.cycle(OPEN);
		level.setBlock(pos, state, UPDATE_FLAGS);
		triggerEvents(player, level, pos, isOpen(state));
		return InteractionResult.sidedSuccess(level.isClientSide);
	}

	@Override
	public void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, BlockPos fromPos, boolean isMoving) {
		if (level.isClientSide()) return;

		boolean hasSignal = level.hasNeighborSignal(pos) || level.hasNeighborSignal(pos.relative(isLowerHalf(state) ? Direction.UP : Direction.DOWN));

		if (hasSignal != isPowered(state)) {
			if (isOpen(state) != hasSignal) {
				state = state.setValue(OPEN, hasSignal);
				triggerEvents(null, level, pos, hasSignal);
			}
			level.setBlock(pos, state.setValue(POWERED, hasSignal), Block.UPDATE_CLIENTS);
		}
	}

	@Override
	public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
		BlockPos posBelow = pos.below();
		BlockState stateBelow = level.getBlockState(posBelow);
		return isLowerHalf(state) ? stateBelow.isFaceSturdy(level, posBelow, Direction.UP) : stateBelow.is(this);
	}

	@Override
	public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos currentPos, BlockPos neighborPos) {
		DoubleBlockHalf half = state.getValue(HALF);
		if (direction.getAxis() == Direction.Axis.Y) {
			boolean isUp = direction == Direction.UP;
			boolean isLowerHalf = half == DoubleBlockHalf.LOWER;
			if (isLowerHalf == isUp) {
				return neighborState.is(this) && neighborState.getValue(HALF) != half ?
						state
//								.setValue(FACING, neighborState.getValue(FACING))
								.setValue(OPEN, neighborState.getValue(OPEN))
								.setValue(POWERED, neighborState.getValue(POWERED))
						:
						Blocks.AIR.defaultBlockState();
			}
		}

		return half == DoubleBlockHalf.LOWER && direction == Direction.DOWN && !state.canSurvive(level, currentPos) ? Blocks.AIR.defaultBlockState() : super.updateShape(state, direction, neighborState, level, currentPos, neighborPos);
	}

	@Override
	public void playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
		//prevent Creative Drop From Bottom Part
		if (!level.isClientSide && player.isCreative() && !isLowerHalf(state)) {
			BlockPos posBelow = pos.below();
			BlockState stateBelow = level.getBlockState(posBelow);
			if (stateBelow.is(state.getBlock()) && isLowerHalf(stateBelow)) {
				level.setBlock(posBelow, Blocks.AIR.defaultBlockState(), 35);
				level.levelEvent(player, 2001, posBelow, Block.getId(stateBelow));
			}
		}
		super.playerWillDestroy(level, pos, state, player);
	}

	protected void triggerEvents(@Nullable Player player, Level level, BlockPos pos, boolean open) {
		playSound(player, level, pos, open ? ModSoundEvents.FLESH_DOOR_OPEN.get() : ModSoundEvents.FLESH_DOOR_CLOSE.get());
		level.gameEvent(player, open ? GameEvent.BLOCK_OPEN : GameEvent.BLOCK_CLOSE, pos);
	}

	private void playSound(@Nullable Player player, Level level, BlockPos pos, SoundEvent sound) {
		level.playSound(player, pos, sound, SoundSource.BLOCKS, 1f, level.random.nextFloat() * 0.1f + 0.9f);
	}

	public boolean isLowerHalf(BlockState state) {
		return state.getValue(HALF) == DoubleBlockHalf.LOWER;
	}

	public boolean isOpen(BlockState state) {
		return state.getValue(OPEN);
	}

	public boolean isPowered(BlockState state) {
		return state.getValue(POWERED);
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		int offset = isLowerHalf(state) ? 0 : 1;
		int idx = isOpen(state) ? OPEN_SHAPE_INDEX + offset : CLOSED_SHAPE_INDEX;

		return switch (state.getValue(ORIENTATION)) {
			case X_POSITIVE -> X_POS_AABB[idx];
			case X_MIDDLE -> x_NONE_AABB[idx];
			case X_NEGATIVE -> x_NEG_AABB[idx];
			case Z_POSITIVE -> Z_POS_AABB[idx];
			case Z_MIDDLE -> Z_NONE_AABB[idx];
			case Z_NEGATIVE -> Z_NEG_AABB[idx];
			default -> Shapes.block();
		};
	}

	@Override
	public VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		int idx = isOpen(state) ? OPEN_SHAPE_INDEX + 2 : CLOSED_SHAPE_INDEX;

		return switch (state.getValue(ORIENTATION)) {
			case X_POSITIVE -> X_POS_AABB[idx];
			case X_MIDDLE -> x_NONE_AABB[idx];
			case X_NEGATIVE -> x_NEG_AABB[idx];
			case Z_POSITIVE -> Z_POS_AABB[idx];
			case Z_MIDDLE -> Z_NONE_AABB[idx];
			case Z_NEGATIVE -> Z_NEG_AABB[idx];
			default -> Shapes.block();
		};
	}

	@Override
	public boolean isPathfindable(BlockState state, BlockGetter level, BlockPos pos, PathComputationType type) {
		return switch (type) {
			case LAND, AIR -> isOpen(state);
			case WATER -> false;
		};
	}

}
