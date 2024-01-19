package com.github.elenterius.biomancy.block.fleshspike;

import com.github.elenterius.biomancy.block.base.WaterloggedFacingBlock;
import com.github.elenterius.biomancy.init.ModBlockProperties;
import com.github.elenterius.biomancy.init.ModDamageSources;
import com.github.elenterius.biomancy.util.EnhancedIntegerProperty;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class FleshSpikeBlock extends WaterloggedFacingBlock {

	public static final EnhancedIntegerProperty SPIKES = ModBlockProperties.SPIKES;

	public FleshSpikeBlock(Properties properties) {
		super(properties);
		registerDefaultState(defaultBlockState().setValue(SPIKES.get(), SPIKES.getMin()));
		FleshSpikeShapes.computePossibleShapes(stateDefinition.getPossibleStates());
	}

	public static int getSpikes(BlockState blockState) {
		return SPIKES.getValue(blockState);
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		super.createBlockStateDefinition(builder);
		builder.add(SPIKES.get());
	}

	@Nullable
	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		BlockState blockState = context.getLevel().getBlockState(context.getClickedPos());
		if (blockState.getBlock() instanceof FleshSpikeBlock) {
			return SPIKES.addValue(blockState, 1);
		}

		return super.getStateForPlacement(context);
	}

	@Override
	public boolean canBeReplaced(BlockState state, BlockPlaceContext useContext) {
		if (!useContext.isSecondaryUseActive() && useContext.getItemInHand().is(asItem()) && getSpikes(state) < SPIKES.getMax()) return true;
		return super.canBeReplaced(state, useContext);
	}

	@Override
	public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
		Direction direction = getFacing(state);
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
		if (getFacing(state) == Direction.UP) {
			int spikes = getSpikes(state);
			entity.causeFallDamage(fallDistance + 2f, 1f + spikes * 0.5f, ModDamageSources.fallOnSpike(level, pos));
		}
		else super.fallOn(level, state, pos, entity, fallDistance);
	}

	@Override
	public void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
		if (entity instanceof ItemEntity) return;
		if (!isEntityInsideDamageArea(level, pos, state, entity)) return;

		Direction direction = getFacing(state);
		Vec3i normal = direction.getNormal();
		float x = (1 - Math.abs(normal.getX())) * 0.1f;
		float y = (1 - Math.abs(normal.getY())) * 0.1f;
		float z = (1 - Math.abs(normal.getZ())) * 0.1f;
		entity.makeStuckInBlock(state, new Vec3(0.9d - x, 0.9d - y, 0.9d - z));

		if (!level.isClientSide) {
			boolean isMovingAwayCorrectly = entity.getMotionDirection() == direction;
			int spikes = getSpikes(state);
			entity.hurt(ModDamageSources.impaleBySpike(level, pos), spikes + (!isMovingAwayCorrectly ? 0.5f : 0f));
		}
	}

	private boolean isEntityInsideDamageArea(Level level, BlockPos pos, BlockState state, Entity entity) {
		VoxelShape blockShape = FleshSpikeShapes.getDamageShape(state).move(pos.getX(), pos.getY(), pos.getZ());
		VoxelShape entityShape = Shapes.create(entity.getBoundingBox());
		return Shapes.joinIsNotEmpty(blockShape, entityShape, BooleanOp.AND);
	}

}
