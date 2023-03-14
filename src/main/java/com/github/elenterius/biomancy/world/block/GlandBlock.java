package com.github.elenterius.biomancy.world.block;

import com.github.elenterius.biomancy.client.util.ClientTextUtil;
import com.github.elenterius.biomancy.util.VoxelShapeUtil;
import com.github.elenterius.biomancy.world.block.decomposer.DecomposerBlockEntity;
import com.github.elenterius.biomancy.world.block.entity.GlandBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@Deprecated(forRemoval = true)
public class GlandBlock extends BaseEntityBlock {

	public static final DirectionProperty FACING = BlockStateProperties.FACING;

	public static final VoxelShape SHAPE_UP = createVoxelShape(Direction.UP);
	public static final VoxelShape SHAPE_DOWN = createVoxelShape(Direction.DOWN);
	public static final VoxelShape SHAPE_NORTH = createVoxelShape(Direction.NORTH);
	public static final VoxelShape SHAPE_SOUTH = createVoxelShape(Direction.SOUTH);
	public static final VoxelShape SHAPE_WEST = createVoxelShape(Direction.WEST);
	public static final VoxelShape SHAPE_EAST = createVoxelShape(Direction.EAST);

	public GlandBlock(Properties properties) {
		super(properties);
	}

	private static VoxelShape createVoxelShape(Direction direction) {
		Optional<VoxelShape> voxelShape = Stream.of(
				VoxelShapeUtil.createXZRotatedTowards(direction, 4, 5, 4, 12, 6, 12),
				VoxelShapeUtil.createXZRotatedTowards(direction, 3, 1, 3, 13, 5, 13),
				VoxelShapeUtil.createXZRotatedTowards(direction, 5, 0, 5, 11, 1, 11)
		).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR));
		return voxelShape.get();
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(FACING);
	}

	@Nullable
	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		return defaultBlockState().setValue(FACING, context.getClickedFace());
	}

	@Override
	public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
		if (level.isClientSide) return InteractionResult.SUCCESS;

		if (level.getBlockEntity(pos) instanceof GlandBlockEntity gland) {
			BlockPos relativePos = pos.relative(state.getValue(FACING).getOpposite());
			if (level.getBlockEntity(relativePos) instanceof DecomposerBlockEntity) {
				player.openMenu(gland);
			}
		}
		return InteractionResult.CONSUME;
	}

	@Override
	public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
		if (level.getBlockEntity(pos) instanceof GlandBlockEntity gland) {
			if (stack.hasCustomHoverName()) {
				gland.setCustomName(stack.getHoverName());
			}
		}
	}

	@Override
	public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
		if (!state.is(newState.getBlock())) {
			if (level.getBlockEntity(pos) instanceof GlandBlockEntity gland) {
				gland.dropContainerContents(level, pos);
//				level.updateNeighbourForOutputSignal(pos, this);
			}
			super.onRemove(state, level, pos, newState, isMoving);
		}
	}

	@Nullable
	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
//		return new GlandBlockEntity(pos, state);
		return null;
	}

	@Override
	public RenderShape getRenderShape(BlockState state) {
		return RenderShape.MODEL;
	}

	@Override
	public void appendHoverText(ItemStack stack, @Nullable BlockGetter level, List<Component> tooltip, TooltipFlag flag) {
		super.appendHoverText(stack, level, tooltip, flag);
		tooltip.add(ClientTextUtil.getItemInfoTooltip(stack.getItem()));
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
	public BlockState rotate(BlockState state, Rotation rot) {
		return state.setValue(FACING, rot.rotate(state.getValue(FACING)));
	}

	@Override
	public BlockState mirror(BlockState state, Mirror mirror) {
		return state.rotate(mirror.getRotation(state.getValue(FACING)));
	}

}
