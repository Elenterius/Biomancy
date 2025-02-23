package com.github.elenterius.biomancy.util;

import com.github.elenterius.biomancy.init.ModDamageSources;
import com.github.elenterius.biomancy.init.tags.ModBlockTags;
import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.protocol.game.ClientboundExplodePacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TraceableEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.BaseFireBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.ForgeEventFactory;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class ExplosionUtil {

	public static ExplosionDamageCalculator DECAY_DAMAGE_CALCULATOR = new ExplosionDamageCalculator() {
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

	public static ExplosionDamageCalculator INCENDIARY_DAMAGE_CALCULATOR = new ExplosionDamageCalculator() {

		@Override
		public Optional<Float> getBlockExplosionResistance(Explosion explosion, BlockGetter level, BlockPos pos, BlockState state, FluidState fluid) {
			if (state.isAir() && fluid.isEmpty()) return Optional.empty();

			boolean isFlammable = Direction.stream().anyMatch(direction -> state.isFlammable(level, pos, direction));
			float multiplier = isFlammable ? 0.5f : 3f;

			return Optional.of(multiplier * Math.max(state.getExplosionResistance(level, pos, explosion), fluid.getExplosionResistance(level, pos, explosion)));
		}

		@Override
		public boolean shouldBlockExplode(Explosion explosion, BlockGetter level, BlockPos pos, BlockState state, float power) {
			return super.shouldBlockExplode(explosion, level, pos, state, power);
		}

	};

	public static void explodeDecay(Level level, @Nullable Entity source, double x, double y, double z, float radius, Level.ExplosionInteraction explosionInteraction) {
		explode(level, source, ModDamageSources.decay(level, source, getIndirectSourceEntity(source)), DECAY_DAMAGE_CALCULATOR, x, y, z, radius, false, explosionInteraction, true, Explosion::new);
	}

	public static void explodeIncendiary(Level level, Entity source, float radius, Level.ExplosionInteraction explosionInteraction) {
		explode(level, source, ModDamageSources.incendiary(level, source, getIndirectSourceEntity(source)), INCENDIARY_DAMAGE_CALCULATOR, source.getX(), source.getY(), source.getZ(), radius, true, explosionInteraction, true, IncendiaryExplosion::new);
	}

	public static void explodeIncendiary(Level level, @Nullable Entity source, double x, double y, double z, float radius, Level.ExplosionInteraction explosionInteraction) {
		explode(level, source, ModDamageSources.incendiary(level, source, getIndirectSourceEntity(source)), INCENDIARY_DAMAGE_CALCULATOR, x, y, z, radius, true, explosionInteraction, true, IncendiaryExplosion::new);
	}

	public static <T extends Explosion> void explode(Level level, @Nullable Entity source, @Nullable DamageSource damageSource, @Nullable ExplosionDamageCalculator damageCalculator, double x, double y, double z, float radius, boolean fire, Level.ExplosionInteraction explosionInteraction, boolean spawnParticles, ExplosionFactory<T> factory) {
		Explosion.BlockInteraction blockInteraction = switch (explosionInteraction) {
			case NONE -> Explosion.BlockInteraction.KEEP;
			case BLOCK -> getDestroyType(level, GameRules.RULE_BLOCK_EXPLOSION_DROP_DECAY);
			case MOB -> ForgeEventFactory.getMobGriefingEvent(level, source) ? getDestroyType(level, GameRules.RULE_MOB_EXPLOSION_DROP_DECAY) : Explosion.BlockInteraction.KEEP;
			case TNT -> getDestroyType(level, GameRules.RULE_TNT_EXPLOSION_DROP_DECAY);
		};

		T explosion = factory.create(level, source, damageSource, damageCalculator, x, y, z, radius, fire, blockInteraction);

		if (ForgeEventFactory.onExplosionStart(level, explosion)) return;

		explosion.explode();
		explosion.finalizeExplosion(spawnParticles);

		if (level instanceof ServerLevel serverLevel) {
			sendToClients(serverLevel, x, y, z, radius, explosion);
		}
	}

	private static void sendToClients(ServerLevel level, double x, double y, double z, float radius, Explosion explosion) {
		if (!explosion.interactsWithBlocks()) {
			explosion.clearToBlow();
		}

		for (ServerPlayer player : level.players()) {
			if (player.distanceToSqr(x, y, z) < 64d * 64d) {
				player.connection.send(new ClientboundExplodePacket(x, y, z, radius, explosion.getToBlow(), explosion.getHitPlayers().get(player)));
			}
		}
	}

	private static Explosion.BlockInteraction getDestroyType(Level level, GameRules.Key<GameRules.BooleanValue> gameRule) {
		return level.getGameRules().getBoolean(gameRule) ? Explosion.BlockInteraction.DESTROY_WITH_DECAY : Explosion.BlockInteraction.DESTROY;
	}

	public static @Nullable LivingEntity getIndirectSourceEntity(@Nullable Entity source) {
		if (source == null) {
			return null;
		}

		if (source instanceof LivingEntity livingEntity) {
			return livingEntity;
		}

		if (source instanceof TraceableEntity traceableEntity && traceableEntity.getOwner() instanceof LivingEntity livingEntity) {
			return livingEntity;
		}

		return null;
	}

	public interface ExplosionFactory<T extends Explosion> {
		T create(Level level, @Nullable Entity source, @Nullable DamageSource damageSource, @Nullable ExplosionDamageCalculator damageCalculator, double x, double y, double z, float radius, boolean fire, Explosion.BlockInteraction interaction);
	}

	public static class IncendiaryExplosion extends Explosion {

		public IncendiaryExplosion(Level level, @Nullable Entity source, @Nullable DamageSource damageSource, @Nullable ExplosionDamageCalculator damageCalculator, double x, double y, double z, float radius, boolean ignoredFire, BlockInteraction interaction) {
			super(level, source, damageSource, damageCalculator, x, y, z, radius, true, interaction); //fire is always true
		}

		@Override
		public void finalizeExplosion(boolean spawnParticles) {
			if (level.isClientSide) {
				level.playLocalSound(x, y, z, SoundEvents.GENERIC_EXPLODE, SoundSource.BLOCKS, 4f, (1f + (level.random.nextFloat() - level.random.nextFloat()) * 0.2f) * 0.7f, false);
			}

			boolean interactsWithBlocks = interactsWithBlocks();

			if (spawnParticles) {
				if (!(radius < 2f) && interactsWithBlocks) {
					level.addParticle(ParticleTypes.EXPLOSION_EMITTER, x, y, z, 1d, 0d, 0d);
				}
				else {
					level.addParticle(ParticleTypes.EXPLOSION, x, y, z, 1d, 0d, 0d);
				}
			}

			if (interactsWithBlocks) {
				destroyBlocks();
			}

			placeFire();
		}

		protected void destroyBlocks() {
			ObjectArrayList<Pair<ItemStack, BlockPos>> drops = new ObjectArrayList<>();
			boolean isPlayerSource = getIndirectSourceEntity() instanceof Player;

			Util.shuffle(toBlow, level.random);

			for (BlockPos pos : toBlow) {
				BlockState blockstate = level.getBlockState(pos);

				if (blockstate.is(Blocks.MAGMA_BLOCK)) {
					level.setBlock(pos, Blocks.LAVA.defaultBlockState(), Block.UPDATE_ALL);
				}
				else if (!blockstate.isAir()) {
					level.getProfiler().push("explosion_blocks");

					if (blockstate.canDropFromExplosion(level, pos, this)) {
						if (level instanceof ServerLevel serverlevel) {
							LootParams.Builder lootParams = new LootParams.Builder(serverlevel)
									.withParameter(LootContextParams.ORIGIN, Vec3.atCenterOf(pos))
									.withParameter(LootContextParams.TOOL, ItemStack.EMPTY)
									.withOptionalParameter(LootContextParams.BLOCK_ENTITY, blockstate.hasBlockEntity() ? level.getBlockEntity(pos) : null)
									.withOptionalParameter(LootContextParams.THIS_ENTITY, source);

							if (blockInteraction == BlockInteraction.DESTROY_WITH_DECAY) {
								lootParams.withParameter(LootContextParams.EXPLOSION_RADIUS, radius);
							}

							blockstate.spawnAfterBreak(serverlevel, pos, ItemStack.EMPTY, isPlayerSource);
							blockstate.getDrops(lootParams).forEach(stack -> addBlockDrops(drops, stack, pos.immutable()));
						}
					}

					blockstate.onBlockExploded(level, pos, this);
					level.getProfiler().pop();
				}
			}

			for (Pair<ItemStack, BlockPos> pair : drops) {
				Block.popResource(level, pair.getSecond(), pair.getFirst());
			}
		}

		protected void placeFire() {
			for (BlockPos pos : toBlow) {
				if (random.nextInt(3) == 0 && level.getBlockState(pos).isAir()) {
					for (Direction direction : Direction.values()) {
						BlockPos neighborPos = pos.relative(direction);
						BlockState neighborState = level.getBlockState(neighborPos);
						if (neighborState.isFaceSturdy(level, neighborPos, direction.getOpposite())) {
							//isFlammable(level, neighborPos, direction.getOpposite())
							level.setBlockAndUpdate(pos, BaseFireBlock.getState(level, pos));
							break;
						}
					}
				}
			}
		}

	}

}
