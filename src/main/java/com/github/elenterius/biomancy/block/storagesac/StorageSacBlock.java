package com.github.elenterius.biomancy.block.storagesac;

import com.github.elenterius.biomancy.block.base.WaterloggedFacingEntityBlock;
import com.github.elenterius.biomancy.init.ModSoundEvents;
import com.github.elenterius.biomancy.util.SoundUtil;
import com.github.elenterius.biomancy.util.VoxelShapeUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.Nullable;

public class StorageSacBlock extends WaterloggedFacingEntityBlock {

	public static final VoxelShape SHAPE_UP = createShape(Direction.UP);
	public static final VoxelShape SHAPE_DOWN = createShape(Direction.DOWN);
	public static final VoxelShape SHAPE_NORTH = createShape(Direction.NORTH);
	public static final VoxelShape SHAPE_SOUTH = createShape(Direction.SOUTH);
	public static final VoxelShape SHAPE_WEST = createShape(Direction.WEST);
	public static final VoxelShape SHAPE_EAST = createShape(Direction.EAST);

	public StorageSacBlock(Properties properties) {
		super(properties);
	}

	private static VoxelShape createShape(Direction direction) {
		return Shapes.join(
				VoxelShapeUtil.createXZRotatedTowards(direction, 4, 0, 4, 12, 2, 12),
				VoxelShapeUtil.createXZRotatedTowards(direction, 2, 2, 2, 14, 16, 14), BooleanOp.OR);
	}

	@Nullable
	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new StorageSacBlockEntity(pos, state);
	}

	@Override
	public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
		if (level.getBlockEntity(pos) instanceof StorageSacBlockEntity sac && stack.hasCustomHoverName()) {
			sac.setCustomName(stack.getHoverName());
		}
	}

	@Override
	public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
		if (level.getBlockEntity(pos) instanceof StorageSacBlockEntity sac && sac.canPlayerOpenInv(player)) {
			if (!level.isClientSide) {
				NetworkHooks.openGui((ServerPlayer) player, sac, buffer -> buffer.writeBlockPos(pos));
				SoundUtil.broadcastBlockSound((ServerLevel) level, pos, ModSoundEvents.UI_STORAGE_SAC_OPEN);
			}
			return InteractionResult.SUCCESS;
		}

		return InteractionResult.CONSUME;
	}

	@Override
	public void playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
		if (!level.isClientSide && player.isCreative() && level.getBlockEntity(pos) instanceof StorageSacBlockEntity storage && !storage.isEmpty()) {
			ItemStack stack = new ItemStack(this);
			storage.saveToItem(stack);
			if (storage.hasCustomName()) stack.setHoverName(storage.getCustomName());
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
