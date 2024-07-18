package com.github.elenterius.biomancy.init;

import com.github.elenterius.biomancy.entity.projectile.BaseProjectile;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import static com.github.elenterius.biomancy.init.ModDamageTypes.getHolder;

public final class ModDamageSources {

	private ModDamageSources() {}

	public static DamageSource fallOnSpike(Level level, BlockPos pos) {
		return source(ModDamageTypes.FALL_ON_SPIKE, level, Vec3.atCenterOf(pos));
	}

	public static DamageSource impaleBySpike(Level level, BlockPos pos) {
		return source(ModDamageTypes.IMPALED_BY_SPIKE, level, Vec3.atCenterOf(pos));
	}

	public static DamageSource chestBite(Level level, BlockPos pos) {
		return source(ModDamageTypes.CHEST_BITE, level, Vec3.atCenterOf(pos));
	}

	public static DamageSource primalSpikes(Level level, Vec3 pos) {
		return source(ModDamageTypes.PRIMORDIAL_SPIKES, level, pos);
	}

	public static DamageSource acid(Level level, @Nullable Entity attacker) {
		return source(ModDamageTypes.CORROSIVE_ACID, level, attacker);
	}

	public static DamageSource bleed(Level level, @Nullable Entity attacker) {
		return source(ModDamageTypes.BLEED, level, attacker);
	}

	public static DamageSource slash(Level level, @Nullable Entity attacker) {
		return source(ModDamageTypes.SLASH, level, attacker);
	}

	public static DamageSource toothProjectile(Level level, BaseProjectile projectile, @Nullable Entity shooter) {
		return source(ModDamageTypes.TOOTH_PROJECTILE, level, projectile, shooter);
	}

	public static DamageSource acidProjectile(Level level, BaseProjectile projectile, @Nullable Entity shooter) {
		return source(ModDamageTypes.CORROSIVE_ACID, level, projectile, shooter);
	}

	public static DamageSource projectile(Holder<DamageType> type, BaseProjectile projectile, @Nullable Entity shooter) {
		return new DamageSource(type, projectile, shooter);
	}

	private static DamageSource source(ResourceKey<DamageType> key, LevelReader level) {
		return new DamageSource(getHolder(key, level));
	}

	private static DamageSource source(ResourceKey<DamageType> key, LevelReader level, BlockPos pos) {
		return source(key, level, Vec3.atCenterOf(pos));
	}

	private static DamageSource source(ResourceKey<DamageType> key, LevelReader level, Vec3 pos) {
		return new DamageSource(getHolder(key, level), pos);
	}

	private static DamageSource source(ResourceKey<DamageType> key, LevelReader level, @Nullable Entity entity) {
		return new DamageSource(getHolder(key, level), entity);
	}

	private static DamageSource source(ResourceKey<DamageType> key, LevelReader level, @Nullable Entity directEntity, @Nullable Entity causingEntity) {
		return new DamageSource(getHolder(key, level), directEntity, causingEntity);
	}

}
