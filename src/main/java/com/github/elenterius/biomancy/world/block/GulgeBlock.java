package com.github.elenterius.biomancy.world.block;

import com.github.elenterius.biomancy.chat.ComponentUtil;
import com.github.elenterius.biomancy.client.util.ClientTextUtil;
import com.github.elenterius.biomancy.styles.TextComponentUtil;
import com.github.elenterius.biomancy.world.block.entity.GulgeBlockEntity;
import com.github.elenterius.biomancy.world.inventory.itemhandler.LargeSingleItemStackHandler;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
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
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.text.DecimalFormat;
import java.util.List;

@Deprecated
public class GulgeBlock extends BaseEntityBlock {

	public static final DirectionProperty FACING = BlockStateProperties.FACING;

	protected static final VoxelShape SHAPE_UD = Block.box(3d, 0d, 3d, 13d, 16d, 13d);
	protected static final VoxelShape SHAPE_NS = Block.box(3d, 3d, 0d, 13d, 13d, 16d);
	protected static final VoxelShape SHAPE_WE = Block.box(0d, 3d, 3d, 16d, 13d, 13d);

	public GulgeBlock(Properties properties) {
		super(properties);
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

	@Nullable
	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return null;
//		return new GulgeBlockEntity(pos, state);
	}

	@Override
	public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
		if (level.getBlockEntity(pos) instanceof GulgeBlockEntity gulge && stack.hasCustomHoverName()) {
			gulge.setCustomName(stack.getHoverName());
		}
	}

	@Override
	public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
		if (level.isClientSide) return InteractionResult.SUCCESS;

		if (level.getBlockEntity(pos) instanceof GulgeBlockEntity gulge) {
			player.openMenu(gulge);
		}
		return InteractionResult.CONSUME;
	}

	@Override
	public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
		if (!state.is(newState.getBlock())) {
			if (level.getBlockEntity(pos) instanceof GulgeBlockEntity gulge) {
				gulge.dropContainerContents(level, pos);
			}
			super.onRemove(state, level, pos, newState, isMoving);
		}
	}

	@Override
	public void playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
		if (!level.isClientSide && player.isCreative() && level.getBlockEntity(pos) instanceof GulgeBlockEntity gulge && !gulge.isEmpty()) {
			ItemStack stack = new ItemStack(this);
			gulge.saveToItem(stack);
			if (gulge.hasCustomName()) stack.setHoverName(gulge.getCustomName());
			ItemEntity itementity = new ItemEntity(level, pos.getX() + 0.5d, pos.getY() + 0.5D, pos.getZ() + 0.5d, stack);
			itementity.setDefaultPickUpDelay();
			level.addFreshEntity(itementity);
		}

		super.playerWillDestroy(level, pos, state, player);
	}

	@Override
	public ItemStack getCloneItemStack(BlockState state, HitResult target, BlockGetter level, BlockPos pos, Player player) {
		ItemStack stack = super.getCloneItemStack(state, target, level, pos, player);
		if (level.getBlockEntity(pos) instanceof GulgeBlockEntity gulge) {
			gulge.saveToItem(stack);
		}
		return stack;
	}

	@Override
	public void appendHoverText(ItemStack stack, @Nullable BlockGetter level, List<Component> tooltip, TooltipFlag flag) {
		super.appendHoverText(stack, level, tooltip, flag);
		tooltip.add(ClientTextUtil.getItemInfoTooltip(stack.getItem()));

		CompoundTag tag = stack.getTagElement("BlockEntityTag");
		if (tag != null) {
			CompoundTag invNbt = tag.getCompound("Inventory");
			if (!invNbt.isEmpty()) {
				ItemStack storedStack = invNbt.contains("Item") ? ItemStack.of(invNbt.getCompound("Item")) : ItemStack.EMPTY;
				if (!storedStack.isEmpty()) {
					short itemAmount = invNbt.getShort(LargeSingleItemStackHandler.ITEM_AMOUNT_TAG);
					tooltip.add(TextComponentUtil.getTooltipText("contains", storedStack.getHoverName().copy()).withStyle(ChatFormatting.GRAY));
					DecimalFormat df = ClientTextUtil.getDecimalFormatter("#,###,###");
					tooltip.add(ComponentUtil.literal(df.format(itemAmount) + "/" + df.format(GulgeBlockEntity.MAX_ITEM_AMOUNT)).withStyle(ChatFormatting.GRAY));
					return;
				}
			}
		}

		tooltip.add(TextComponentUtil.getTooltipText("empty").withStyle(ChatFormatting.GRAY));
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		return switch (state.getValue(FACING)) {
			case NORTH, SOUTH -> SHAPE_NS;
			case WEST, EAST -> SHAPE_WE;
			default -> SHAPE_UD;
		};
	}

	@Override
	public RenderShape getRenderShape(BlockState state) {
		return RenderShape.MODEL;
	}

}
