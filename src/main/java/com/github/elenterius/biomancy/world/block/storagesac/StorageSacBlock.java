package com.github.elenterius.biomancy.world.block.storagesac;

import com.github.elenterius.biomancy.init.ModSoundEvents;
import com.github.elenterius.biomancy.util.SoundUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
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
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.Nullable;

public class StorageSacBlock extends BaseEntityBlock {

	public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;

	protected static final VoxelShape SHAPE = createShape();

	private static VoxelShape createShape() {
		VoxelShape base = Block.box(2d, 0d, 2d, 14d, 14d, 14d);
		VoxelShape lid = Block.box(4d, 14d, 4d, 12d, 16d, 12d);
		return Shapes.join(base, lid, BooleanOp.OR);
	}

	public StorageSacBlock(Properties properties) {
		super(properties);
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(FACING);
	}

	@Nullable
	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		return defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
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
				NetworkHooks.openScreen((ServerPlayer) player, sac, buffer -> buffer.writeBlockPos(pos));
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
		return SHAPE;
	}

	@Override
	public RenderShape getRenderShape(BlockState state) {
		return RenderShape.MODEL;
	}

}
