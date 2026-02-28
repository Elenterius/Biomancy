package com.github.elenterius.biomancy.util.explosion;

import com.github.elenterius.biomancy.init.ModParticleTypes;
import com.github.elenterius.biomancy.init.tags.ModBlockTags;
import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.ExplosionDamageCalculator;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.Optional;

public class DecayExplosion extends GenericExplosion {

	public static ExplosionDamageCalculator BLOCK_DAMAGE_CALCULATOR = new ExplosionDamageCalculator() {
		@Override
		public Optional<Float> getBlockExplosionResistance(Explosion explosion, BlockGetter level, BlockPos pos, BlockState state, FluidState fluidState) {
			if (state.isAir() && fluidState.isEmpty()) {
				return Optional.empty();
			}

			if (state.is(ModBlockTags.DECAY_DESTRUCTIBLE)) {
				float explosionResistance = Math.max(state.getExplosionResistance(level, pos, explosion), fluidState.getExplosionResistance(level, pos, explosion));
				return Optional.of(explosionResistance * 0.25f);
			}

			if (state.canBeReplaced() || state.getExplosionResistance(level, pos, explosion) == 0f) {
				return Optional.empty();
			}

			return Optional.of(3_600_000f);
		}

		@Override
		public boolean shouldBlockExplode(Explosion explosion, BlockGetter level, BlockPos pos, BlockState state, float power) {
			return state.is(ModBlockTags.DECAY_DESTRUCTIBLE) || state.canBeReplaced() || state.getExplosionResistance(level, pos, explosion) == 0f;
		}
	};

	public DecayExplosion(Level level, @Nullable Entity source, double x, double y, double z, float radius, List<BlockPos> positions) {
		this(level, source, null, null, x, y, z, radius, false, BlockInteraction.DESTROY_WITH_DECAY);
		toBlow.addAll(positions);
	}

	public DecayExplosion(Level level, @Nullable Entity source, @Nullable DamageSource damageSource, @Nullable ExplosionDamageCalculator damageCalculator, double x, double y, double z, float radius, boolean ignoredFire, BlockInteraction interaction) {
		super(level, source, damageSource, damageCalculator, x, y, z, radius, false, interaction); //fire is always false
	}

	@Override
	protected void hurtEntity(Entity entity, float amount) {
		entity.hurt(getDamageSource(), amount * 0.125f);
	}

	@Override
	protected double getKnockbackStrength(Entity entity, double nearExplosionPercent) {
		return super.getKnockbackStrength(entity, nearExplosionPercent) * 0.125d;
	}

	@Override
	public void finalizeExplosion(boolean spawnParticles) {
		if (level.isClientSide) {
			level.playLocalSound(x, y, z, SoundEvents.GENERIC_EXPLODE, SoundSource.BLOCKS, 4f, (1f + (level.random.nextFloat() - level.random.nextFloat()) * 0.2f) * 0.7f, false);
		}

		boolean interactsWithBlocks = interactsWithBlocks();

		if (spawnParticles) {
			if (!(radius < 2f) && interactsWithBlocks) {
				level.addParticle(ModParticleTypes.DECAY_EXPLOSION_EMITTER.get(), x, y, z, 1d, 0d, 0d);
			}
			else {
				level.addParticle(ModParticleTypes.DECAY_EXPLOSION.get(), x, y, z, 1d, 0d, 0d);
			}
		}

		if (interactsWithBlocks) {
			destroyBlocks();
		}
	}

	protected void destroyBlocks() {
		ObjectArrayList<Pair<ItemStack, BlockPos>> drops = new ObjectArrayList<>();
		boolean isPlayerSource = getIndirectSourceEntity() instanceof Player;

		Util.shuffle(toBlow, level.random);

		for (BlockPos pos : toBlow) {
			BlockState state = level.getBlockState(pos);

			if (!state.isAir()) {
				level.getProfiler().push("explosion_blocks");

				if (level instanceof ServerLevel serverlevel && state.canDropFromExplosion(level, pos, this)) {
					LootParams.Builder lootParams = new LootParams.Builder(serverlevel)
							.withParameter(LootContextParams.ORIGIN, Vec3.atCenterOf(pos))
							.withParameter(LootContextParams.TOOL, ItemStack.EMPTY)
							.withOptionalParameter(LootContextParams.BLOCK_ENTITY, state.hasBlockEntity() ? level.getBlockEntity(pos) : null)
							.withOptionalParameter(LootContextParams.THIS_ENTITY, source);

					if (blockInteraction == BlockInteraction.DESTROY_WITH_DECAY) {
						lootParams.withParameter(LootContextParams.EXPLOSION_RADIUS, radius);
					}

					state.spawnAfterBreak(serverlevel, pos, ItemStack.EMPTY, isPlayerSource);
					state.getDrops(lootParams).forEach(stack -> addBlockDrops(drops, stack, pos.immutable()));
				}

				state.onBlockExploded(level, pos, this);
				level.getProfiler().pop();
			}
		}

		for (Pair<ItemStack, BlockPos> pair : drops) {
			Block.popResource(level, pair.getSecond(), pair.getFirst());
		}
	}

}
