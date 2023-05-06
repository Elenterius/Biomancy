package com.github.elenterius.biomancy.block.fleshspike;

import com.github.elenterius.biomancy.init.ModDamageSources;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
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
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class FleshSpikeBlock extends Block implements SimpleWaterloggedBlock {

	public static final DirectionProperty FACING = BlockStateProperties.FACING;
	public static final int MIN_SPIKES = 1;
	public static final int MAX_SPIKES = 3;
	public static final IntegerProperty SPIKES = IntegerProperty.create("spikes", MIN_SPIKES, MAX_SPIKES);
	public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

	public FleshSpikeBlock(Properties properties) {
		super(properties);
		registerDefaultState(stateDefinition.any().setValue(FACING, Direction.UP).setValue(SPIKES, MIN_SPIKES).setValue(WATERLOGGED, Boolean.FALSE));
		FleshSpikeShapes.computePossibleShapes(stateDefinition.getPossibleStates());
	}

	public static int getSpikes(BlockState blockState) {
		return blockState.getValue(SPIKES);
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(FACING, SPIKES, WATERLOGGED);
	}

	@Override
	public FluidState getFluidState(BlockState state) {
		return Boolean.TRUE.equals(state.getValue(WATERLOGGED)) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
	}

	@Nullable
	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		BlockPos pos = context.getClickedPos();

		BlockState blockState = context.getLevel().getBlockState(pos);
		if (blockState.getBlock() instanceof FleshSpikeBlock) {
			return blockState.setValue(SPIKES, Math.min(getSpikes(blockState) + 1, MAX_SPIKES));
		}

		Level level = context.getLevel();
		boolean isWaterlogged = level.getFluidState(pos).getType() == Fluids.WATER;
		return defaultBlockState().setValue(FACING, context.getClickedFace()).setValue(WATERLOGGED, isWaterlogged);
	}

	@Override
	public boolean canBeReplaced(BlockState state, BlockPlaceContext useContext) {
		if (!useContext.isSecondaryUseActive() && useContext.getItemInHand().is(asItem()) && getSpikes(state) < MAX_SPIKES) return true;
		return super.canBeReplaced(state, useContext);
	}

	@Override
	public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
		Direction direction = state.getValue(FACING);
		BlockPos basePos = pos.relative(direction.getOpposite());
		return Block.canSupportCenter(level, basePos, direction);
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		return FleshSpikeShapes.getBoundingShape(state);
	}

	@Override
	public VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		return hasCollision ? FleshSpikeShapes.getCollisionShape(state) : Shapes.empty();
	}

	@Override
	public boolean isCollisionShapeFullBlock(BlockState state, BlockGetter level, BlockPos pos) {
		return false;
	}

	@Override
	public void fallOn(Level level, BlockState state, BlockPos pos, Entity entity, float fallDistance) {
		if (state.getValue(FACING) == Direction.UP) {
			entity.causeFallDamage(fallDistance + 2f, 2f, ModDamageSources.FALL_ON_SPIKE);
		}
		else super.fallOn(level, state, pos, entity, fallDistance);
	}

	@Override
	public void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
		if (!isEntityInsideDamageArea(level, pos, state, entity)) return;

		Direction direction = state.getValue(FACING);
		Vec3i normal = direction.getNormal();
		float x = (1 - Math.abs(normal.getX())) * 0.1f;
		float y = (1 - Math.abs(normal.getY())) * 0.1f;
		float z = (1 - Math.abs(normal.getZ())) * 0.1f;
		entity.makeStuckInBlock(state, new Vec3(0.9d - x, 0.9d - y, 0.9d - z));

		if (!level.isClientSide) {
			boolean isMovingAwayCorrectly = entity.getMotionDirection() == direction;
			int spikes = getSpikes(state);
			entity.hurt(ModDamageSources.IMPALED_BY_SPIKE, spikes + (!isMovingAwayCorrectly ? 0.5f : 0f));
		}
	}

	private boolean isEntityInsideDamageArea(Level level, BlockPos pos, BlockState state, Entity entity) {
		VoxelShape blockShape = FleshSpikeShapes.getDamageShape(state).move(pos.getX(), pos.getY(), pos.getZ());
		VoxelShape entityShape = Shapes.create(entity.getBoundingBox());
		return Shapes.joinIsNotEmpty(blockShape, entityShape, BooleanOp.AND);
	}

	@Override
	public BlockState rotate(BlockState state, Rotation rot) {
		return state.setValue(FACING, rot.rotate(state.getValue(FACING)));
	}

	@Override
	public BlockState mirror(BlockState state, Mirror mirror) {
		return state.rotate(mirror.getRotation(state.getValue(FACING)));
	}

}
