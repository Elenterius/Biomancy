package com.github.elenterius.biomancy.util.explosion;

import com.github.elenterius.biomancy.init.ModDamageSources;
import com.github.elenterius.biomancy.network.ModNetworkHandler;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TraceableEntity;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.ExplosionDamageCalculator;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.ForgeEventFactory;
import org.jspecify.annotations.Nullable;

public class ExplosionUtil {

	public static void explodeDecay(Level level, @Nullable Entity source, double x, double y, double z, float radius, Level.ExplosionInteraction explosionInteraction) {
		explode(ExplosionType.DECAY, level, source, ModDamageSources.decay(level, source, getIndirectSourceEntity(source)), DecayExplosion.BLOCK_DAMAGE_CALCULATOR, x, y, z, radius, false, explosionInteraction, true);
	}

	public static void explodeVolatile(Level level, Entity source, float radius, Level.ExplosionInteraction explosionInteraction) {
		explode(ExplosionType.VOLATILE, level, source, ModDamageSources.incendiary(level, source, getIndirectSourceEntity(source)), VolatileExplosion.BLOCK_DAMAGE_CALCULATOR, source.getX(), source.getY(), source.getZ(), radius, true, explosionInteraction, true);
	}

	public static void explodeVolatile(Level level, @Nullable Entity source, double x, double y, double z, float radius, Level.ExplosionInteraction explosionInteraction) {
		explode(ExplosionType.VOLATILE, level, source, ModDamageSources.incendiary(level, source, getIndirectSourceEntity(source)), VolatileExplosion.BLOCK_DAMAGE_CALCULATOR, x, y, z, radius, true, explosionInteraction, true);
	}

	public static void explode(ExplosionType explosionType, Level level, @Nullable Entity source, @Nullable DamageSource damageSource, @Nullable ExplosionDamageCalculator damageCalculator, double x, double y, double z, float radius, boolean fire, Level.ExplosionInteraction explosionInteraction, boolean spawnParticles) {
		Explosion.BlockInteraction blockInteraction = switch (explosionInteraction) {
			case NONE -> Explosion.BlockInteraction.KEEP;
			case BLOCK -> getDestroyType(level, GameRules.RULE_BLOCK_EXPLOSION_DROP_DECAY);
			case MOB -> ForgeEventFactory.getMobGriefingEvent(level, source) ? getDestroyType(level, GameRules.RULE_MOB_EXPLOSION_DROP_DECAY) : Explosion.BlockInteraction.KEEP;
			case TNT -> getDestroyType(level, GameRules.RULE_TNT_EXPLOSION_DROP_DECAY);
		};

		Explosion explosion = explosionType.serverFactory.create(level, source, damageSource, damageCalculator, x, y, z, radius, fire, blockInteraction);

		if (ForgeEventFactory.onExplosionStart(level, explosion)) return;

		explosion.explode();
		explosion.finalizeExplosion(spawnParticles);

		if (level instanceof ServerLevel serverLevel) {
			ModNetworkHandler.sendCustomExplosionToClients(serverLevel, explosionType, explosion);
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

}
