package com.github.elenterius.biomancy.item;

import com.github.elenterius.biomancy.block.FleshChainBlock;
import com.github.elenterius.biomancy.util.ComponentUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class FleshChainBlockItem extends SimpleBlockItem {

	public FleshChainBlockItem(Block block, Properties properties) {
		super(block, properties);
	}

	public static Direction getDirection(UseOnContext context, BlockPos pos, BlockState state) {
		Direction.Axis axis = state.getValue(FleshChainBlock.AXIS);
		if (axis.isVertical()) return getYDirection(pos, context.getClickLocation());
		if (axis == Direction.Axis.X) return getXDirection(pos, context.getClickLocation());
		if (axis == Direction.Axis.Z) return getZDirection(pos, context.getClickLocation());
		return Direction.UP;
	}

	private static Direction getZDirection(BlockPos pos, Vec3 click) {
		return click.z - pos.getZ() > 0.5f ? Direction.SOUTH : Direction.NORTH;
	}

	private static Direction getXDirection(BlockPos pos, Vec3 click) {
		return click.x - pos.getX() > 0.5f ? Direction.EAST : Direction.WEST;
	}

	private static Direction getYDirection(BlockPos pos, Vec3 click) {
		return click.y - pos.getY() > 0.5f ? Direction.UP : Direction.DOWN;
	}

	protected static BlockPos getClickedPos(UseOnContext context) {
		if (context instanceof BlockPlaceContextWrapper contextWrapper) {
			context = contextWrapper.getOriginalUseContext();
		}
		return context.getClickedPos();
	}

	@Override
	public InteractionResult useOn(UseOnContext context) {
		InteractionResult placeResult = place(new BlockPlaceContextWrapper(context));

		if (!placeResult.consumesAction() && isEdible() && context.getPlayer() != null) {
			InteractionResult result = use(context.getLevel(), context.getPlayer(), context.getHand()).getResult();
			return result == InteractionResult.CONSUME ? InteractionResult.CONSUME_PARTIAL : result;
		}

		return placeResult;
	}

	@Nullable
	@Override
	public BlockPlaceContext updatePlacementContext(BlockPlaceContext context) {
		Level level = context.getLevel();
		BlockPos pos = getClickedPos(context);
		BlockState state = level.getBlockState(pos);
		Block block = getBlock();

		if (!state.is(block)) return context;
		if (context.isSecondaryUseActive()) return context;

		Direction direction = getDirection(context, pos, state);
		BlockPos.MutableBlockPos mutablePos = pos.mutable().move(direction);

		for (int blocks = 0; blocks < 7; blocks++) {
			if (!level.isClientSide && !level.isInWorldBounds(mutablePos)) {
				if (context.getPlayer() instanceof ServerPlayer serverPlayer) {
					int maxBuildHeight = level.getMaxBuildHeight();
					if (mutablePos.getY() >= maxBuildHeight) {
						serverPlayer.sendSystemMessage(ComponentUtil.translatable("build.tooHigh", maxBuildHeight - 1).withStyle(ChatFormatting.RED), true);
					}
				}
				return null;
			}

			state = level.getBlockState(mutablePos);
			if (!state.is(block)) {
				return state.canBeReplaced(context) ? BlockPlaceContext.at(context, mutablePos, direction) : null;
			}

			mutablePos.move(direction);
		}

		return null;
	}

	@Override
	protected boolean mustSurvive() {
		return false;
	}

}
