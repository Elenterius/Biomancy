package com.github.elenterius.biomancy.item;

import com.github.elenterius.biomancy.mixin.accessor.CreeperAccessor;
import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.game.ClientboundExplodePacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.ExplosionDamageCalculator;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.MagmaBlock;
import net.minecraft.world.level.block.TntBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.ForgeEventFactory;
import org.jetbrains.annotations.Nullable;

public class UnstableCompoundItem extends SimpleItem {

	public UnstableCompoundItem(Properties properties) {
		super(properties);
	}

	public static void explode(ItemEntity itemEntity, boolean isBurning) {
		if (itemEntity.level().isClientSide) return;
		if (itemEntity.fallDistance <= 1f) return;

		float explosionRadius = 0.5f + (itemEntity.getItem().getCount() / 64f) * 1.5f;
		float multiplier = isBurning ? 2f : 1f;

		UnstableExplosion.explode(itemEntity.level(), itemEntity, itemEntity.getX(), itemEntity.getY(), itemEntity.getZ(), explosionRadius * multiplier, Level.ExplosionInteraction.TNT);

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

	public static class UnstableExplosion extends Explosion {

		public UnstableExplosion(Level level, @Nullable Entity source, @Nullable DamageSource damageSource, @Nullable ExplosionDamageCalculator damageCalculator, double x, double y, double z, float radius, boolean fire, BlockInteraction blockInteraction) {
			super(level, source, damageSource, damageCalculator, x, y, z, radius, fire, blockInteraction);
		}

		public static void explode(Level level, @Nullable Entity source, double x, double y, double z, float radius, Level.ExplosionInteraction explosionInteraction) {
			explode(level, source, null, null, x, y, z, radius, true, explosionInteraction, true);
		}

		public static void explode(Level level, @Nullable Entity source, @Nullable DamageSource damageSource, @Nullable ExplosionDamageCalculator damageCalculator, double x, double y, double z, float radius, boolean fire, Level.ExplosionInteraction explosionInteraction, boolean spawnParticles) {
			Explosion.BlockInteraction blockInteraction = switch (explosionInteraction) {
				case NONE -> BlockInteraction.KEEP;
				case BLOCK -> getDestroyType(level, GameRules.RULE_BLOCK_EXPLOSION_DROP_DECAY);
				case MOB -> ForgeEventFactory.getMobGriefingEvent(level, source) ? getDestroyType(level, GameRules.RULE_MOB_EXPLOSION_DROP_DECAY) : BlockInteraction.KEEP;
				case TNT -> getDestroyType(level, GameRules.RULE_TNT_EXPLOSION_DROP_DECAY);
			};

			UnstableExplosion explosion = new UnstableExplosion(level, source, damageSource, damageCalculator, x, y, z, radius, fire, blockInteraction);

			if (ForgeEventFactory.onExplosionStart(level, explosion)) return;

			explosion.explode();
			explosion.finalizeExplosion(spawnParticles);

			if (level instanceof ServerLevel serverLevel) {
				sendToClients(serverLevel, x, y, z, radius, explosion);
			}
		}

		private static void sendToClients(ServerLevel level, double x, double y, double z, float radius, UnstableExplosion explosion) {
			if (!explosion.interactsWithBlocks()) {
				explosion.clearToBlow();
			}

			for (ServerPlayer player : level.players()) {
				if (player.distanceToSqr(x, y, z) < 4096d) {
					player.connection.send(new ClientboundExplodePacket(x, y, z, radius, explosion.getToBlow(), explosion.getHitPlayers().get(player)));
				}
			}
		}

		private static Explosion.BlockInteraction getDestroyType(Level level, GameRules.Key<GameRules.BooleanValue> gameRule) {
			return level.getGameRules().getBoolean(gameRule) ? Explosion.BlockInteraction.DESTROY_WITH_DECAY : Explosion.BlockInteraction.DESTROY;
		}
	}

}
