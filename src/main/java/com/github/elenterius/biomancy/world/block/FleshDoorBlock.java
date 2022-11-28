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
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
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

public class FleshDoorBlock extends DoorBlock {

	public static final EnumProperty<Orientation> ORIENTATION = ModBlocks.ORIENTATION;
	public static final int USE_UPDATE_FLAG = Block.UPDATE_CLIENTS | Block.UPDATE_IMMEDIATE; // 10

	protected static final int THICKNESS = 2;
	protected static final int CLOSED_SHAPE_INDEX = 0;
	protected static final int OPEN_SHAPE_INDEX = 1;
	protected static final VoxelShape[] x_NEG_AABB = createClosedAndOpenShape(0, 0, 0, THICKNESS, 16, 16);
	protected static final VoxelShape[] x_NONE_AABB = createClosedAndOpenShape(8d - THICKNESS / 2d, 0, 0, 8d + THICKNESS / 2d, 16, 16);
	protected static final VoxelShape[] X_POS_AABB = createClosedAndOpenShape(16d - THICKNESS, 0, 0, 16, 16, 16);
	protected static final VoxelShape[] Z_NEG_AABB = createClosedAndOpenShape(0, 0, 0, 16, 16, THICKNESS);
	protected static final VoxelShape[] Z_NONE_AABB = createClosedAndOpenShape(0, 0, 8d - THICKNESS / 2d, 16, 16, 8d + THICKNESS / 2d);
	protected static final VoxelShape[] Z_POS_AABB = createClosedAndOpenShape(0, 0, 16d - THICKNESS, 16, 16, 16);

	public FleshDoorBlock(Properties properties) {
		super(properties);
		registerDefaultState(defaultBlockState().setValue(ORIENTATION, Orientation.X_MIDDLE));
	}

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

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		//note: FACING and HINGE are not used! This is only here to prevent crashes.
		super.createBlockStateDefinition(builder);
		builder.add(ORIENTATION);
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
		else {
			boolean isXAxis = context.getHorizontalDirection().getAxis() == Direction.Axis.X;
			state = state.setValue(ORIENTATION, isXAxis ? Orientation.X_MIDDLE : Orientation.Z_MIDDLE);
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
		BlockState newState = state.cycle(OPEN);
		level.setBlock(pos, newState, USE_UPDATE_FLAG);
		triggerOpenCloseEvent(player, level, pos, isOpen(newState));

		return InteractionResult.sidedSuccess(level.isClientSide);
	}

	@Override
	public void setOpen(@Nullable Entity user, Level level, BlockState state, BlockPos pos, boolean open) {
		if (state.is(this) && state.getValue(OPEN) != open) {
			BlockState newState = state.setValue(OPEN, open);
			level.setBlock(pos, newState, USE_UPDATE_FLAG);
			triggerOpenCloseEvent(user, level, pos, open);
		}
	}

	@Override
	public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
		return isLowerHalf(state) || level.getBlockState(pos.below()).is(this);
	}

	@Override
	public void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, BlockPos fromPos, boolean isMoving) {
		boolean hasSignal = level.hasNeighborSignal(pos) || level.hasNeighborSignal(pos.relative(isLowerHalf(state) ? Direction.UP : Direction.DOWN));

		if (!defaultBlockState().is(block) && hasSignal != isPowered(state)) {
			if (isOpen(state) != hasSignal) {
				state = state.setValue(OPEN, hasSignal);
				triggerOpenCloseEvent(null, level, pos, hasSignal);
			}
			level.setBlock(pos, state.setValue(POWERED, hasSignal), Block.UPDATE_CLIENTS);
		}
	}

	@Override
	public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos currentPos, BlockPos neighborPos) {
		DoubleBlockHalf half = state.getValue(HALF);

		if ((direction.getAxis() == Direction.Axis.Y) && (half == DoubleBlockHalf.LOWER == (direction == Direction.UP))) {
			if (neighborState.is(this) && neighborState.getValue(HALF) != half) {
				return state
						.setValue(ORIENTATION, neighborState.getValue(ORIENTATION))
						.setValue(OPEN, neighborState.getValue(OPEN))
						.setValue(POWERED, neighborState.getValue(POWERED));
			}
			return Blocks.AIR.defaultBlockState();
		}

		if (half == DoubleBlockHalf.LOWER && direction == Direction.DOWN && !state.canSurvive(level, currentPos)) {
			return Blocks.AIR.defaultBlockState();
		}

		return super.updateShape(state, direction, neighborState, level, currentPos, neighborPos);
	}

	protected void triggerOpenCloseEvent(@Nullable Entity entity, Level level, BlockPos pos, boolean isDoorOpening) {
		playSound(entity instanceof Player player ? player : null, level, pos, isDoorOpening ? ModSoundEvents.FLESH_DOOR_OPEN.get() : ModSoundEvents.FLESH_DOOR_CLOSE.get());
		level.gameEvent(entity, isDoorOpening ? GameEvent.BLOCK_OPEN : GameEvent.BLOCK_CLOSE, pos);
	}

	private void playSound(@Nullable Player player, Level level, BlockPos pos, SoundEvent sound) {
		level.playSound(player, pos, sound, SoundSource.BLOCKS, 1f, level.random.nextFloat() * 0.1f + 0.9f);
	}

	public boolean isLowerHalf(BlockState state) {
		return state.getValue(HALF) == DoubleBlockHalf.LOWER;
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

	@Override
	public BlockState rotate(BlockState state, Rotation rotationDirection) {
		//Note: the Create Mod does not call IForgeBlock#rotate and calls this method directly (Create Train/Contraption disassembly)
		Orientation orientation = state.getValue(ORIENTATION);
		return state.setValue(ORIENTATION, orientation.rotate(rotationDirection));
	}

	@Override
	public BlockState mirror(BlockState state, Mirror mirror) {
		Orientation orientation = state.getValue(ORIENTATION);
		return state.setValue(ORIENTATION, orientation.mirror(mirror));
	}

}
