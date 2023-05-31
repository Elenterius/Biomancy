package com.github.elenterius.biomancy.init;

import com.github.elenterius.biomancy.entity.projectile.BaseProjectile;
import com.github.elenterius.biomancy.entity.projectile.CorrosiveAcidProjectile;
import com.github.elenterius.biomancy.entity.projectile.ToothProjectile;
import com.github.elenterius.biomancy.entity.projectile.WitherProjectile;
import com.github.elenterius.biomancy.item.weapon.IGun;
import it.unimi.dsi.fastutil.floats.FloatUnaryOperator;
import it.unimi.dsi.fastutil.ints.IntUnaryOperator;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;

public final class ModProjectiles {

	public static final List<ConfiguredProjectile<? extends BaseProjectile>> PRECONFIGURED_PROJECTILES = new ArrayList<>();
	public static final ConfiguredProjectile<ToothProjectile> TOOTH = build("Sharp Tooth", 1.75f, 5f, 0, convertToInaccuracy(0.92f), ToothProjectile::new);
	public static final ConfiguredProjectile<WitherProjectile> WITHER = build("Withershot", 0.8f, 8f, 0, convertToInaccuracy(0.9f), WitherProjectile::new);
	public static final ConfiguredProjectile<CorrosiveAcidProjectile> CORROSIVE = build("Corrosive", 1.5f, 4, 0, convertToInaccuracy(0.9f), CorrosiveAcidProjectile::new);

	private static float convertToInaccuracy(float accuracy) {
		return -IGun.MAX_INACCURACY * accuracy + IGun.MAX_INACCURACY;
	}

	private static <T extends BaseProjectile> ConfiguredProjectile<T> build(String name, float velocity, float damage, int knockback, float accuracy, ProjectileFactory<T> factory) {
		ConfiguredProjectile<T> configuredProjectile = new ConfiguredProjectile<>(name, velocity, damage, knockback, convertToInaccuracy(accuracy), factory);
		PRECONFIGURED_PROJECTILES.add(configuredProjectile);
		return configuredProjectile;
	}

	public static <T extends BaseProjectile> boolean shootProjectile(Level level, LivingEntity shooter, float velocity, float damage, int knockback, float inaccuracy, ProjectileFactory<T> factory) {
		BaseProjectile projectile = factory.create(level, shooter);
		projectile.setDamage(damage);
		if (knockback > 0) {
			projectile.setKnockback((byte) knockback);
		}

		Vec3 direction = shooter.getLookAngle();
		projectile.shoot(direction.x(), direction.y(), direction.z(), velocity, inaccuracy);

		if (level.addFreshEntity(projectile)) {
			level.playSound(null, shooter.getX(), shooter.getY(), shooter.getZ(), SoundEvents.CROSSBOW_SHOOT, SoundSource.PLAYERS, 0.8f, 0.4f);
			return true;
		}

		return false;
	}

	public interface ProjectileFactory<T extends BaseProjectile> {
		T create(Level level, LivingEntity shooter);
	}

	public record ConfiguredProjectile<T extends BaseProjectile>(String name, float velocity, float damage, int knockback, float inaccuracy, ProjectileFactory<T> factory) {

		public boolean shoot(Level level, LivingEntity shooter) {
			return shootProjectile(level, shooter, velocity, damage, knockback, inaccuracy, factory);
		}

		public boolean shoot(Level level, LivingEntity shooter, FloatUnaryOperator velocityFunc, FloatUnaryOperator damageFunc, IntUnaryOperator knockbackFunc, FloatUnaryOperator inaccuracyFunc) {
			return shootProjectile(level, shooter, velocityFunc.apply(velocity), damageFunc.apply(damage), knockbackFunc.apply(knockback), inaccuracyFunc.apply(inaccuracy), factory);
		}

	}

}
