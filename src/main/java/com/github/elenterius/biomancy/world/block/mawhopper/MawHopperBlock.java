package com.github.elenterius.biomancy.world.block.mawhopper;

import com.github.elenterius.biomancy.init.ModBlockEntities;
import com.github.elenterius.biomancy.util.VoxelShapeUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Stream;

public class MawHopperBlock extends BaseEntityBlock {

	public static final DirectionProperty FACING = BlockStateProperties.FACING;

	public static final EnumProperty<Shape> SHAPE = EnumProperty.create("shape", Shape.class);

	protected static final Map<BlockState, VoxelShape> CACHED_SHAPES = new HashMap<>();

	public MawHopperBlock(Properties properties) {
		super(properties);
		registerDefaultState(defaultBlockState().setValue(FACING, Direction.UP).setValue(SHAPE, Shape.NORMAL));
	}

	private static VoxelShape createVoxelShape(BlockState state) {
		Direction direction = getDirection(state);
		Shape shape = getShape(state);

		if (shape == Shape.NORMAL) {
			return Stream.of(
					VoxelShapeUtil.createXZRotatedTowards(direction, 6, 0, 6, 10, 2, 10),
					VoxelShapeUtil.createXZRotatedTowards(direction, 6, 2, 6, 10, 6, 10),
					VoxelShapeUtil.createXZRotatedTowards(direction, 5, 6, 5, 11, 10, 11),
					VoxelShapeUtil.createXZRotatedTowards(direction, 3, 10, 3, 13, 13, 13),
					VoxelShapeUtil.createXZRotatedTowards(direction, 0, 13, 3, 16, 16, 13),
					VoxelShapeUtil.createXZRotatedTowards(direction, 3, 13, 0, 13, 16, 16)
			).reduce((a, b) -> Shapes.join(a, b, BooleanOp.OR)).orElse(Shapes.block());
		}

		//connected shape
		return Stream.of(
				VoxelShapeUtil.createXZRotatedTowards(direction, 5, 12, 5, 11, 16, 11),
				VoxelShapeUtil.createXZRotatedTowards(direction, 6, 0, 6, 10, 12, 10)
		).reduce((a, b) -> Shapes.join(a, b, BooleanOp.OR)).orElse(Shapes.block());
	}

	public static Direction getDirection(BlockState state) {
		return state.getValue(FACING);
	}

	public static Shape getShape(BlockState state) {
		return state.getValue(SHAPE);
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(FACING, SHAPE);
	}

	@Nullable
	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		Direction direction = context.getClickedFace();
		BlockState state = defaultBlockState().setValue(FACING, direction);

		BlockPos pullPos = context.getClickedPos().relative(direction);
		if (context.getLevel().getBlockState(pullPos).getBlock() instanceof MawHopperBlock) {
			return state.setValue(SHAPE, Shape.CONNECTED);
		}

		return state.setValue(SHAPE, Shape.NORMAL);
	}

	@Override
	public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos currentPos, BlockPos neighborPos) {
		Direction ownDirection = getDirection(state);
		if (ownDirection == direction) {
			if (neighborState.getBlock() instanceof MawHopperBlock && getDirection(neighborState) == ownDirection) {
				return state.setValue(SHAPE, Shape.CONNECTED);
			}
			else {
				return state.setValue(SHAPE, Shape.NORMAL);
			}
		}

		return state;
	}

	@Override
	public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
		if (player.getItemInHand(hand).isEmpty() && level.getBlockEntity(pos) instanceof MawHopperBlockEntity blockEntity) {
			if (level.isClientSide) return InteractionResult.SUCCESS;
			blockEntity.giveInventoryContentsTo(level, pos, player);
			return InteractionResult.CONSUME;
		}
		return super.use(state, level, pos, player, hand, hit);
	}

	@Override
	public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
		if (!state.is(newState.getBlock())) {
			if (!level.isClientSide && level.getBlockEntity(pos) instanceof MawHopperBlockEntity blockEntity) {
				blockEntity.dropInventoryContents(level, pos);
			}
			super.onRemove(state, level, pos, newState, isMoving);
		}
	}

	@Nullable
	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return ModBlockEntities.MAW_HOPPER.get().create(pos, state);
	}

	@Nullable
	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
		return level.isClientSide ? null : createTickerHelper(blockEntityType, ModBlockEntities.MAW_HOPPER.get(), MawHopperBlockEntity::serverTick);
	}

	@Override
	public void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
		BlockEntity blockentity = level.getBlockEntity(pos);
		if (blockentity instanceof MawHopperBlockEntity blockEntity) {
			MawHopperBlockEntity.entityInside(level, pos, state, blockEntity, entity);
		}
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		return CACHED_SHAPES.computeIfAbsent(state, MawHopperBlock::createVoxelShape);
	}

	@Override
	public BlockState rotate(BlockState state, Rotation rot) {
		return state.setValue(FACING, rot.rotate(state.getValue(FACING)));
	}

	@Override
	public BlockState mirror(BlockState state, Mirror mirror) {
		return state.rotate(mirror.getRotation(state.getValue(FACING)));
	}

	public enum Shape implements StringRepresentable {
		NORMAL, CONNECTED;

		@Override
		public String getSerializedName() {
			return name().toLowerCase(Locale.ENGLISH);
		}
	}

}
