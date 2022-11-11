package com.github.elenterius.biomancy.world.block;

import com.github.elenterius.biomancy.init.ModDamageSources;
import com.github.elenterius.biomancy.util.VoxelShapeUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class BoneSpikeBlock extends Block implements SimpleWaterloggedBlock {

	public static final DirectionProperty FACING = BlockStateProperties.FACING;
	public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
	public static final VoxelShape SHAPE_UP = VoxelShapeUtil.createXZRotatedTowards(Direction.UP, 7, 0, 7, 9, 5, 9);
	public static final VoxelShape SHAPE_DOWN = VoxelShapeUtil.createXZRotatedTowards(Direction.DOWN, 7, 0, 7, 9, 5, 9);
	public static final VoxelShape SHAPE_NORTH = VoxelShapeUtil.createXZRotatedTowards(Direction.NORTH, 7, 0, 7, 9, 5, 9);
	public static final VoxelShape SHAPE_SOUTH = VoxelShapeUtil.createXZRotatedTowards(Direction.SOUTH, 7, 0, 7, 9, 5, 9);
	public static final VoxelShape SHAPE_EAST = VoxelShapeUtil.createXZRotatedTowards(Direction.EAST, 7, 0, 7, 9, 5, 9);
	public static final VoxelShape SHAPE_WEST = VoxelShapeUtil.createXZRotatedTowards(Direction.WEST, 7, 0, 7, 9, 5, 9);

	public BoneSpikeBlock(Properties properties) {
		super(properties);
		registerDefaultState(stateDefinition.any().setValue(FACING, Direction.UP).setValue(WATERLOGGED, Boolean.FALSE));
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(FACING, WATERLOGGED);
	}

	@Override
	public FluidState getFluidState(BlockState state) {
		return Boolean.TRUE.equals(state.getValue(WATERLOGGED)) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
	}

	@Nullable
	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		Level level = context.getLevel();
		BlockPos pos = context.getClickedPos();
		boolean isWaterlogged = level.getFluidState(pos).getType() == Fluids.WATER;
		return defaultBlockState().setValue(FACING, context.getClickedFace()).setValue(WATERLOGGED, isWaterlogged);
	}

	@Override
	public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
		Direction direction = state.getValue(FACING);
		BlockPos basePos = pos.relative(direction.getOpposite());
		return Block.canSupportCenter(level, basePos, direction);
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		return switch (state.getValue(FACING)) {
			case DOWN -> SHAPE_DOWN;
			case NORTH -> SHAPE_NORTH;
			case SOUTH -> SHAPE_SOUTH;
			case WEST -> SHAPE_WEST;
			case EAST -> SHAPE_EAST;
			default -> SHAPE_UP;
		};
	}

	@Override
	public boolean isCollisionShapeFullBlock(BlockState state, BlockGetter level, BlockPos pos) {
		return false;
	}

	@Override
	public BlockState rotate(BlockState state, Rotation rot) {
		return state.setValue(FACING, rot.rotate(state.getValue(FACING)));
	}

	@Override
	public BlockState mirror(BlockState state, Mirror mirror) {
		return state.rotate(mirror.getRotation(state.getValue(FACING)));
	}

	@Override
	public PushReaction getPistonPushReaction(BlockState state) {
		return PushReaction.NORMAL;
	}

	@Override
	public void fallOn(Level level, BlockState state, BlockPos pos, Entity entity, float fallDistance) {
		if (state.getValue(FACING) == Direction.UP) {
			entity.causeFallDamage(fallDistance + 2f, 2f, ModDamageSources.FALL_ON_BONE_SPIKE);
		}
		else super.fallOn(level, state, pos, entity, fallDistance);
	}

	@Override
	public void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
		if (!isEntityColliding(level, pos, state, entity)) return;

		entity.makeStuckInBlock(state, new Vec3(0.8d, 0.75d, 0.8d));

		if (!level.isClientSide && (entity.xOld != entity.getX() || entity.zOld != entity.getZ())) {
			double dX = Math.abs(entity.getX() - entity.xOld);
			double dZ = Math.abs(entity.getZ() - entity.zOld);
			if (dX >= 0.003F || dZ >= 0.003F) {
				boolean isMovingAwayCorrectly = entity.getMotionDirection() == state.getValue(FACING);
				entity.hurt(ModDamageSources.IMPALED_BY_BONE_SPIKE, 1f + (!isMovingAwayCorrectly ? 1f : 0f));
			}
		}
	}

	private boolean isEntityColliding(Level level, BlockPos pos, BlockState state, Entity entity) {
		VoxelShape blockShape = state.getCollisionShape(level, pos, CollisionContext.of(entity)).move(pos.getX(), pos.getY(), pos.getZ());
		VoxelShape entityShape = Shapes.create(entity.getBoundingBox().inflate(0.05f));
		return Shapes.joinIsNotEmpty(blockShape, entityShape, BooleanOp.AND);
	}

}
