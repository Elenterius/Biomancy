package com.github.elenterius.biomancy.world.block;

import com.github.elenterius.biomancy.init.ModBlocks;
import com.github.elenterius.biomancy.init.ModSoundEvents;
import com.github.elenterius.biomancy.world.block.property.Orientation;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LadderBlock;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class IrisDoorBlock extends Block implements SimpleWaterloggedBlock {

	public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
	public static final BooleanProperty OPEN = BlockStateProperties.OPEN;
	public static final BooleanProperty POWERED = BlockStateProperties.POWERED;

	public static final EnumProperty<Orientation> ORIENTATION = ModBlocks.ORIENTATION;
	protected static final int THICKNESS = 2;
	protected static final int CLOSED_SHAPE_INDEX = 0;
	protected static final int OPEN_SHAPE_INDEX = 1;
	protected static final VoxelShape[] Y_POS_AABB = createClosedAndOpenShape(0, 0, 0, 16, THICKNESS, 16);
	protected static final VoxelShape[] Y_NONE_AABB = createClosedAndOpenShape(0, 8d - THICKNESS / 2d, 0, 16, 8d + THICKNESS / 2d, 16);
	protected static final VoxelShape[] Y_NEG_AABB = createClosedAndOpenShape(0, 16d - THICKNESS, 0, 16, 16, 16);
	protected static final VoxelShape[] x_NEG_AABB = createClosedAndOpenShape(0, 0, 0, THICKNESS, 16, 16);
	protected static final VoxelShape[] x_NONE_AABB = createClosedAndOpenShape(8d - THICKNESS / 2d, 0, 0, 8d + THICKNESS / 2d, 16, 16);
	protected static final VoxelShape[] X_POS_AABB = createClosedAndOpenShape(16d - THICKNESS, 0, 0, 16, 16, 16);
	protected static final VoxelShape[] Z_NEG_AABB = createClosedAndOpenShape(0, 0, 0, 16, 16, THICKNESS);
	protected static final VoxelShape[] Z_NONE_AABB = createClosedAndOpenShape(0, 0, 8d - THICKNESS / 2d, 16, 16, 8d + THICKNESS / 2d);
	protected static final VoxelShape[] Z_POS_AABB = createClosedAndOpenShape(0, 0, 16d - THICKNESS, 16, 16, 16);

	private static VoxelShape[] createClosedAndOpenShape(double x0, double y0, double z0, double x1, double y1, double z1) {
		VoxelShape closedShape = Block.box(x0, y0, z0, x1, y1, z1);

		boolean caseX = x0 == 0 && x1 == 16;
		boolean caseY = y0 == 0 && y1 == 16;
		boolean caseZ = z0 == 0 && z1 == 16;
		double offset = 2;
		double xA = caseX ? x0 + offset : x0;
		double yA = caseY ? y0 + offset : y0;
		double zA = caseZ ? z0 + offset : z0;
		double xB = caseX ? x1 - offset : x1;
		double yB = caseY ? y1 - offset : y1;
		double zB = caseZ ? z1 - offset : z1;
		VoxelShape openShape = Shapes.join(closedShape, Block.box(xA, yA, zA, xB, yB, zB), BooleanOp.NOT_SAME);

		return new VoxelShape[]{closedShape, openShape};
	}

	public IrisDoorBlock(Properties properties) {
		super(properties);
		registerDefaultState(defaultBlockState()
				.setValue(ORIENTATION, Orientation.Y_MIDDLE)
				.setValue(OPEN, false).setValue(POWERED, false)
				.setValue(WATERLOGGED, false)
		);
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(ORIENTATION, OPEN, POWERED, WATERLOGGED);
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

		if (context.getLevel().hasNeighborSignal(context.getClickedPos())) {
			state = state.setValue(OPEN, true).setValue(POWERED, true);
		}

		return state.setValue(WATERLOGGED, fluidState.getType() == Fluids.WATER);
	}

	@Override
	public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
		state = state.cycle(OPEN);
		level.setBlock(pos, state, Block.UPDATE_CLIENTS);
		if (isWaterlogged(state)) level.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
		triggerEvents(player, level, pos, isOpen(state));

		return InteractionResult.sidedSuccess(level.isClientSide);
	}

	@Override
	public void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, BlockPos fromPos, boolean isMoving) {
		if (level.isClientSide()) return;

		boolean hasSignal = level.hasNeighborSignal(pos);

		if (hasSignal != isPowered(state)) {
			if (isOpen(state) != hasSignal) {
				state = state.setValue(OPEN, hasSignal);
				triggerEvents(null, level, pos, hasSignal);
			}

			level.setBlock(pos, state.setValue(POWERED, hasSignal), Block.UPDATE_CLIENTS);
			if (isWaterlogged(state)) level.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
		}
	}

	protected void triggerEvents(@Nullable Player player, Level level, BlockPos pos, boolean open) {
		playSound(player, level, pos, open ? ModSoundEvents.FLESHY_DOOR_OPEN.get() : ModSoundEvents.FLESHY_DOOR_CLOSE.get());
		level.gameEvent(player, open ? GameEvent.BLOCK_OPEN : GameEvent.BLOCK_CLOSE, pos);
	}

	private void playSound(@Nullable Player player, Level level, BlockPos pos, SoundEvent sound) {
		level.playSound(player, pos, sound, SoundSource.BLOCKS, 1f, level.random.nextFloat() * 0.1f + 0.9f);
	}

	@Override
	public FluidState getFluidState(BlockState state) {
		return isWaterlogged(state) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
	}

	public boolean isOpen(BlockState state) {
		return state.getValue(OPEN);
	}

	public boolean isPowered(BlockState state) {
		return state.getValue(POWERED);
	}

	public boolean isWaterlogged(BlockState state) {
		return state.getValue(WATERLOGGED);
	}

	@Override
	public boolean isLadder(BlockState state, LevelReader level, BlockPos pos, LivingEntity entity) {
		if (isOpen(state) && state.getValue(ORIENTATION).axis.isVertical()) {
			BlockState stateBelow = level.getBlockState(pos.below());
			if (stateBelow.getBlock() instanceof IrisDoorBlock) {
				return stateBelow.getValue(ORIENTATION).axis == state.getValue(ORIENTATION).axis;
			}
			return stateBelow.getBlock() instanceof LadderBlock || stateBelow.is(BlockTags.CLIMBABLE);
		}
		return false;
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		int idx = isOpen(state) ? OPEN_SHAPE_INDEX : CLOSED_SHAPE_INDEX;
		return switch (state.getValue(ORIENTATION)) {
			case X_POSITIVE -> X_POS_AABB[idx];
			case X_MIDDLE -> x_NONE_AABB[idx];
			case X_NEGATIVE -> x_NEG_AABB[idx];
			case Z_POSITIVE -> Z_POS_AABB[idx];
			case Z_MIDDLE -> Z_NONE_AABB[idx];
			case Z_NEGATIVE -> Z_NEG_AABB[idx];
			case Y_POSITIVE -> Y_POS_AABB[idx];
			case Y_NEGATIVE -> Y_NEG_AABB[idx];
			case Y_MIDDLE -> Y_NONE_AABB[idx];
		};
	}

	//TODO: is custom occlusion shape needed?

	@Override
	public VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		if (isOpen(state)) return Shapes.empty();

		int idx = CLOSED_SHAPE_INDEX;
		return switch (state.getValue(ORIENTATION)) {
			case X_POSITIVE -> X_POS_AABB[idx];
			case X_MIDDLE -> x_NONE_AABB[idx];
			case X_NEGATIVE -> x_NEG_AABB[idx];
			case Z_POSITIVE -> Z_POS_AABB[idx];
			case Z_MIDDLE -> Z_NONE_AABB[idx];
			case Z_NEGATIVE -> Z_NEG_AABB[idx];
			case Y_POSITIVE -> Y_POS_AABB[idx];
			case Y_NEGATIVE -> Y_NEG_AABB[idx];
			case Y_MIDDLE -> Y_NONE_AABB[idx];
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
	public boolean isPathfindable(BlockState state, BlockGetter level, BlockPos pos, PathComputationType type) {
		return switch (type) {
			case LAND, AIR -> isOpen(state);
			case WATER -> isWaterlogged(state);
		};
	}

}
