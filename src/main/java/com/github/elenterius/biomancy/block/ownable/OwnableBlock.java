package com.github.elenterius.biomancy.block.ownable;

import com.github.elenterius.biomancy.init.ModSoundEvents;
import com.github.elenterius.biomancy.item.EssenceItem;
import com.github.elenterius.biomancy.ownable.Ownable;
import com.github.elenterius.biomancy.ownable.OwnableEntityBlock;
import com.github.elenterius.biomancy.permission.Actions;
import com.github.elenterius.biomancy.permission.IRestrictedInteraction;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.BlockHitResult;

import javax.annotation.Nullable;
import java.util.List;

public abstract class OwnableBlock extends BaseEntityBlock implements OwnableEntityBlock {

	protected OwnableBlock(Properties properties) {
		super(properties);
	}

	@Override
	public void appendHoverText(ItemStack stack, @Nullable BlockGetter level, List<Component> tooltip, TooltipFlag flag) {
		super.appendHoverText(stack, level, tooltip, flag);
		OwnableEntityBlock.appendUserListToTooltip(stack, tooltip);
	}

	@Override
	public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
		super.setPlacedBy(level, pos, state, placer, stack);
		if (level.getBlockEntity(pos) instanceof Ownable ownable) {
			OwnableEntityBlock.setBlockEntityOwner(level, ownable, placer, stack);
		}
	}

	@Override
	public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {

		if (level.getBlockEntity(pos) instanceof IRestrictedInteraction restrictedBlock && restrictedBlock.isActionAllowed(player, Actions.USE_BLOCK)) {
			ItemStack stack = player.getItemInHand(hand);
			if (stack.getItem() instanceof EssenceItem essenceItem) {
				if (level.isClientSide) return InteractionResult.SUCCESS;

				if (restrictedBlock.isActionAllowed(player, Actions.CONFIGURE)) {
					boolean success = essenceItem.getEntityUUID(stack).map(restrictedBlock::addUser).orElse(false);
					if (success) {
						stack.shrink(1);
						level.playSound(null, pos, ModSoundEvents.FLESHKIN_EAT.get(), SoundSource.BLOCKS, 1f, level.random.nextFloat() * 0.1f + 0.9f);
						return InteractionResult.SUCCESS;
					}
				}

				level.playSound(null, pos, ModSoundEvents.FLESHKIN_NO.get(), SoundSource.BLOCKS, 1f, level.random.nextFloat() * 0.1f + 0.9f);
				return InteractionResult.CONSUME;
			}
		}

		return InteractionResult.PASS;
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
