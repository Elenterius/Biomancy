package com.github.elenterius.biomancy.world.block.ownable;

import com.github.elenterius.biomancy.world.ownable.IOwnable;
import com.github.elenterius.biomancy.world.ownable.IOwnableEntityBlock;
import com.github.elenterius.biomancy.world.permission.Actions;
import com.github.elenterius.biomancy.world.permission.IRestrictedInteraction;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.PushReaction;

import javax.annotation.Nullable;
import java.util.List;

public abstract class OwnableBlock extends BaseEntityBlock implements IOwnableEntityBlock {

	protected OwnableBlock(Properties properties) {
		super(properties);
	}

	@Override
	public void appendHoverText(ItemStack stack, @Nullable BlockGetter level, List<Component> tooltip, TooltipFlag flag) {
		super.appendHoverText(stack, level, tooltip, flag);
		IOwnableEntityBlock.appendUserListToTooltip(stack, tooltip);
	}

	@Override
	public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
		super.setPlacedBy(level, pos, state, placer, stack);
		if (level.getBlockEntity(pos) instanceof IOwnable ownable) {
			IOwnableEntityBlock.setupBlockEntityOwner(level, ownable, placer, stack);
		}
	}

//	@Override
//	public void playerWillDestroy(Level worldIn, BlockPos pos, BlockState state, PlayerEntity player) {
//		dropForCreativePlayer(worldIn, this, pos, player);
//		super.playerWillDestroy(worldIn, pos, state, player);
//	}

	@Override
	public float getDestroyProgress(BlockState state, Player player, BlockGetter level, BlockPos pos) {
		if (level.getBlockEntity(pos) instanceof IRestrictedInteraction interaction && interaction.isActionAllowed(player, Actions.DESTROY_BLOCK)) {
			return super.getDestroyProgress(state, player, level, pos);
		}
		return 0f;
	}

	@Override
	public PushReaction getPistonPushReaction(BlockState state) {
		return PushReaction.BLOCK;
	}

}
