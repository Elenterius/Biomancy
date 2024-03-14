package com.github.elenterius.biomancy.block;

import com.github.elenterius.biomancy.block.property.DirectionalSlabType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.Fluids;
import org.jetbrains.annotations.Nullable;

public class DirectionalPillarSlabBlock extends DirectionalSlabBlock {
	public static final EnumProperty<Direction.Axis> AXIS = BlockStateProperties.AXIS;

	public DirectionalPillarSlabBlock(Properties properties) {
		super(properties);
		registerDefaultState(defaultBlockState().setValue(AXIS, Direction.Axis.Y));
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		super.createBlockStateDefinition(builder);
		builder.add(AXIS);
	}

	@Override
	public @Nullable BlockState getStateForPlacement(BlockPlaceContext context) {
		Level level = context.getLevel();
		BlockPos clickedPos = context.getClickedPos();
		BlockState state = context.getLevel().getBlockState(clickedPos);
		boolean isWaterlogged = level.getFluidState(clickedPos).getType() == Fluids.WATER;

		if (state.is(this) && state.getValue(TYPE) != DirectionalSlabType.FULL) {
			return defaultBlockState()
					.setValue(TYPE, DirectionalSlabType.FULL)
					.setValue(AXIS, state.getValue(TYPE).getFacing().getAxis())
					.setValue(WATERLOGGED, false);
		}

		DirectionalSlabType type = DirectionalSlabType.getHalfFrom(clickedPos, context.getClickLocation(), context.getClickedFace());

		return defaultBlockState()
				.setValue(TYPE, type)
				.setValue(AXIS, type.getFacing().getAxis())
				.setValue(WATERLOGGED, isWaterlogged);
	}

	@Override
	public BlockState getStateForPlacement(BlockGetter level, BlockPos pos, Direction direction) {
		BlockState state = level.getBlockState(pos);
		boolean isWaterlogged = level.getFluidState(pos).getType() == Fluids.WATER;

		if (state.is(this) && state.getValue(TYPE) != DirectionalSlabType.FULL) {
			return defaultBlockState()
					.setValue(TYPE, DirectionalSlabType.FULL)
					.setValue(AXIS, state.getValue(TYPE).getFacing().getAxis())
					.setValue(WATERLOGGED, false);
		}

		DirectionalSlabType type = DirectionalSlabType.getHalfFrom(direction);

		return defaultBlockState()
				.setValue(TYPE, type)
				.setValue(AXIS, type.getFacing().getAxis())
				.setValue(WATERLOGGED, isWaterlogged);
	}

	@Override
	public BlockState rotate(BlockState state, Rotation rotationDirection) {
		DirectionalSlabType type = state.getValue(TYPE).rotate(rotationDirection);

		Direction.Axis axis = state.getValue(AXIS);
		if (type != DirectionalSlabType.FULL) {
			axis = type.getFacing().getAxis();
		}
		else {
			axis = switch (rotationDirection) {
				case COUNTERCLOCKWISE_90, CLOCKWISE_90 -> switch (axis) {
					case X -> Direction.Axis.Z;
					case Z -> Direction.Axis.X;
					default -> axis;
				};
				default -> axis;
			};
		}

		return state.setValue(TYPE, type).setValue(AXIS, axis);
	}

	@Override
	public BlockState mirror(BlockState state, Mirror mirror) {
		DirectionalSlabType type = state.getValue(TYPE);
		return state
				.setValue(TYPE, type.mirror(mirror))
				.setValue(AXIS, type.getFacing().getAxis());
	}

}
