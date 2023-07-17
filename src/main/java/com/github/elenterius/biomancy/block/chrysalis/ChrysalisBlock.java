package com.github.elenterius.biomancy.block.chrysalis;

import com.github.elenterius.biomancy.block.base.WaterloggedFacingEntityBlock;
import com.github.elenterius.biomancy.util.VoxelShapeUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.stream.Stream;

public class ChrysalisBlock extends WaterloggedFacingEntityBlock {

	public static final VoxelShape SHAPE_UP = createShape(Direction.UP);
	public static final VoxelShape SHAPE_DOWN = createShape(Direction.DOWN);
	public static final VoxelShape SHAPE_NORTH = createShape(Direction.NORTH);
	public static final VoxelShape SHAPE_SOUTH = createShape(Direction.SOUTH);
	public static final VoxelShape SHAPE_WEST = createShape(Direction.WEST);
	public static final VoxelShape SHAPE_EAST = createShape(Direction.EAST);

	public ChrysalisBlock(Properties properties) {
		super(properties);
	}

	private static VoxelShape createShape(Direction direction) {
		return Stream.of(
				VoxelShapeUtil.createXZRotatedTowards(direction, 4, 9, 4, 12, 13, 12),
				VoxelShapeUtil.createXZRotatedTowards(direction, 3.5, 0, 3.5, 12.5, 1, 12.5),
				VoxelShapeUtil.createXZRotatedTowards(direction, 3, 1, 3, 13, 9, 13)
		).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).orElse(Shapes.empty());
	}

	@Nullable
	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new ChrysalisBlockEntity(pos, state);
	}

	@Override
	public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
		if (level.getBlockEntity(pos) instanceof ChrysalisBlockEntity blockEntity && stack.hasCustomHoverName()) {
			blockEntity.setCustomName(stack.getHoverName());
		}
	}

	@Override
	public void playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
		if (level.isClientSide) {
			super.playerWillDestroy(level, pos, state, player);
			return;
		}

		if (player.isCreative() && level.getBlockEntity(pos) instanceof ChrysalisBlockEntity chrysalis && !chrysalis.isEmpty()) {
			ItemStack stack = new ItemStack(this);
			chrysalis.saveToItem(stack);
			if (chrysalis.hasCustomName()) stack.setHoverName(chrysalis.getCustomName());
			ItemEntity itemEntity = new ItemEntity(level, pos.getX() + 0.5d, pos.getY() + 0.5D, pos.getZ() + 0.5d, stack);
			itemEntity.setDefaultPickUpDelay();
			level.addFreshEntity(itemEntity);
		}

		super.playerWillDestroy(level, pos, state, player);
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		return switch (getFacing(state)) {
			case DOWN -> SHAPE_DOWN;
			case NORTH -> SHAPE_NORTH;
			case SOUTH -> SHAPE_SOUTH;
			case WEST -> SHAPE_WEST;
			case EAST -> SHAPE_EAST;
			default -> SHAPE_UP;
		};
	}

	@Override
	public RenderShape getRenderShape(BlockState state) {
		return RenderShape.MODEL;
	}

}
