package com.github.elenterius.biomancy.world.block;

import com.github.elenterius.biomancy.util.VoxelShapeUtil;
import com.github.elenterius.biomancy.world.block.property.DirectionalSlabType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.item.ItemStack;
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
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class DirectionalSlabBlock extends Block implements SimpleWaterloggedBlock {

	public static final EnumProperty<DirectionalSlabType> TYPE = EnumProperty.create("type", DirectionalSlabType.class);
	public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

	protected static final VoxelShape SHAPE_UP = VoxelShapeUtil.createXZRotatedTowards(Direction.UP, 0, 0, 0, 16, 8, 16);
	protected static final VoxelShape SHAPE_DOWN = VoxelShapeUtil.createXZRotatedTowards(Direction.DOWN, 0, 0, 0, 16, 8, 16);
	protected static final VoxelShape SHAPE_NORTH = VoxelShapeUtil.createXZRotatedTowards(Direction.NORTH, 0, 0, 0, 16, 8, 16);
	protected static final VoxelShape SHAPE_SOUTH = VoxelShapeUtil.createXZRotatedTowards(Direction.SOUTH, 0, 0, 0, 16, 8, 16);
	protected static final VoxelShape SHAPE_EAST = VoxelShapeUtil.createXZRotatedTowards(Direction.EAST, 0, 0, 0, 16, 8, 16);
	protected static final VoxelShape SHAPE_WEST = VoxelShapeUtil.createXZRotatedTowards(Direction.WEST, 0, 0, 0, 16, 8, 16);

	public DirectionalSlabBlock(Properties properties) {
		super(properties);
		registerDefaultState(defaultBlockState()
				.setValue(TYPE, DirectionalSlabType.HALF_UP)
				.setValue(WATERLOGGED, Boolean.FALSE)
		);
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(TYPE, WATERLOGGED);
	}

	@Override
	public FluidState getFluidState(BlockState state) {
		return Boolean.TRUE.equals(state.getValue(WATERLOGGED)) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
	}

	@Override
	public boolean placeLiquid(LevelAccessor pLevel, BlockPos pPos, BlockState state, FluidState fluidState) {
		return state.getValue(TYPE) != DirectionalSlabType.FULL && SimpleWaterloggedBlock.super.placeLiquid(pLevel, pPos, state, fluidState);
	}

	@Override
	public boolean canPlaceLiquid(BlockGetter level, BlockPos pos, BlockState state, Fluid fluid) {
		return state.getValue(TYPE) != DirectionalSlabType.FULL && SimpleWaterloggedBlock.super.canPlaceLiquid(level, pos, state, fluid);
	}

	@Nullable
	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		Level level = context.getLevel();
		BlockPos clickedPos = context.getClickedPos();
		BlockState state = context.getLevel().getBlockState(clickedPos);
		boolean isWaterlogged = level.getFluidState(clickedPos).getType() == Fluids.WATER;

		if (state.is(this) && state.getValue(TYPE) != DirectionalSlabType.FULL) {
			return defaultBlockState().setValue(TYPE, DirectionalSlabType.FULL).setValue(WATERLOGGED, false);
		}

		DirectionalSlabType type = DirectionalSlabType.getHalfFrom(clickedPos, context.getClickLocation(), context.getClickedFace());

		return defaultBlockState().setValue(TYPE, type).setValue(WATERLOGGED, isWaterlogged);
	}

	@Override
	public boolean canBeReplaced(BlockState state, BlockPlaceContext useContext) {
		ItemStack stack = useContext.getItemInHand();
		DirectionalSlabType type = state.getValue(TYPE);

		if (type != DirectionalSlabType.FULL && stack.is(asItem())) {
			if (useContext.replacingClickedOnBlock()) {
				Direction clickedFace = useContext.getClickedFace();
				return type.getFacing() == clickedFace;
			}
			return true;
		}

		return false;
	}

	@Override
	public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos currentPos, BlockPos neighborPos) {
		if (Boolean.TRUE.equals(state.getValue(WATERLOGGED))) {
			level.scheduleTick(currentPos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
		}
		return super.updateShape(state, direction, neighborState, level, currentPos, neighborPos);
	}

	@Override
	public boolean useShapeForLightOcclusion(BlockState state) {
		return state.getValue(TYPE) != DirectionalSlabType.FULL;
	}

	@Override
	public BlockState rotate(BlockState state, Rotation rotationDirection) {
		//Note: the Create Mod does not call IForgeBlock#rotate and calls this method directly (Create Train/Contraption disassembly)
		DirectionalSlabType type = state.getValue(TYPE);
		return state.setValue(TYPE, type.rotate(rotationDirection));
	}

	@Override
	public BlockState mirror(BlockState state, Mirror mirror) {
		DirectionalSlabType type = state.getValue(TYPE);
		return state.setValue(TYPE, type.mirror(mirror));
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		return switch (state.getValue(TYPE)) {
			case HALF_DOWN -> SHAPE_DOWN;
			case HALF_UP -> SHAPE_UP;
			case HALF_NORTH -> SHAPE_NORTH;
			case HALF_SOUTH -> SHAPE_SOUTH;
			case HALF_WEST -> SHAPE_WEST;
			case HALF_EAST -> SHAPE_EAST;
			default -> Shapes.block(); //full slab type
		};
	}

	@Override
	public boolean isPathfindable(BlockState pState, BlockGetter plevel, BlockPos pos, PathComputationType type) {
		return switch (type) {
			case LAND, AIR -> false;
			case WATER -> plevel.getFluidState(pos).is(FluidTags.WATER);
		};
	}

}
