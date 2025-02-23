package com.github.elenterius.biomancy.item;

import com.github.elenterius.biomancy.mixin.accessor.CreeperAccessor;
import com.github.elenterius.biomancy.util.ExplosionUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.MagmaBlock;
import net.minecraft.world.level.block.TntBlock;
import net.minecraft.world.level.block.state.BlockState;

public class UnstableCompoundItem extends SimpleItem {

	public UnstableCompoundItem(Properties properties) {
		super(properties);
	}

	public static void explode(ItemEntity itemEntity, boolean isBurning) {
		if (itemEntity.level().isClientSide) return;
		if (itemEntity.fallDistance <= 1f) return;

		float explosionRadius = 0.5f + (itemEntity.getItem().getCount() / 64f) * 1.5f;
		float multiplier = isBurning ? 2f : 1f;

		ExplosionUtil.explodeIncendiary(itemEntity.level(), itemEntity, explosionRadius * multiplier, Level.ExplosionInteraction.TNT);

		itemEntity.discard();
	}

	@Override
	public void onDestroyed(ItemEntity itemEntity, DamageSource damageSource) {
		super.onDestroyed(itemEntity, damageSource);

		if (damageSource.is(DamageTypeTags.IS_FIRE)) {
			explode(itemEntity, true);
		}
	}

	@Override
	public InteractionResult useOn(UseOnContext context) {
		Level level = context.getLevel();
		BlockPos pos = context.getClickedPos();
		BlockState state = level.getBlockState(pos);
		Block block = state.getBlock();

		if (block instanceof TntBlock tnt) {
			if (!level.isClientSide) {
				context.getItemInHand().shrink(1);
				tnt.onCaughtFire(state, level, pos, context.getClickedFace(), context.getPlayer());
				level.setBlock(pos, Blocks.AIR.defaultBlockState(), Block.UPDATE_ALL_IMMEDIATE);
			}
			return InteractionResult.sidedSuccess(level.isClientSide);
		}

		if (block instanceof MagmaBlock) {
			if (!level.isClientSide) {
				context.getItemInHand().shrink(1);
				level.setBlock(pos, Blocks.LAVA.defaultBlockState(), Block.UPDATE_ALL_IMMEDIATE);
			}
			level.playSound(context.getPlayer(), pos, SoundEvents.GENERIC_EXPLODE, SoundSource.BLOCKS, 2f, (1f + (level.random.nextFloat() - level.random.nextFloat()) * 0.2f) * 0.7f);
			return InteractionResult.sidedSuccess(level.isClientSide);
		}

		return InteractionResult.PASS;
	}

	@Override
	public InteractionResult interactLivingEntity(ItemStack stack, Player player, LivingEntity target, InteractionHand usedHand) {
		if (target instanceof Creeper creeper) {
			if (!player.level().isClientSide) {
				stack.shrink(1);
			}
			((CreeperAccessor) creeper).biomancy$explodeCreeper();
			return InteractionResult.sidedSuccess(player.level().isClientSide);
		}

		return InteractionResult.PASS;
	}

}
