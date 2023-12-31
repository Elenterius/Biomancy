package com.github.elenterius.biomancy.init;

import com.github.elenterius.biomancy.entity.projectile.*;
import com.github.elenterius.biomancy.item.weapon.Gun;
import com.github.elenterius.biomancy.util.function.FloatOperator;
import com.github.elenterius.biomancy.util.function.IntOperator;
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
	public static final ConfiguredProjectile<AcidBlobProjectile> ACID_BLOB = build("Acid Blob", 1.2f, 2, 0, convertToInaccuracy(0.9f), AcidBlobProjectile::new);
	public static final ConfiguredProjectile<AcidBlobProjectile> FALLING_ACID_BLOB = build("Falling Acid Blob", 0.1f, 2, 0, convertToInaccuracy(0.9f), AcidBlobProjectile::new);
	public static final ConfiguredProjectile<SapberryProjectile> SAPBERRY = build("Sapberry", 1.25f, 2, 0, convertToInaccuracy(0.9f), SapberryProjectile::new);

	private static float convertToInaccuracy(float accuracy) {
		return -Gun.MAX_INACCURACY * accuracy + Gun.MAX_INACCURACY;
	}

	private static <T extends BaseProjectile> ConfiguredProjectile<T> build(String name, float velocity, float damage, int knockback, float accuracy, ProjectileFactory<T> factory) {
		ConfiguredProjectile<T> configuredProjectile = new ConfiguredProjectile<>(name, velocity, damage, knockback, convertToInaccuracy(accuracy), factory);
		PRECONFIGURED_PROJECTILES.add(configuredProjectile);
		return configuredProjectile;
	}

	public static <T extends BaseProjectile> boolean shootProjectile(Level level, LivingEntity shooter, float velocity, float damage, int knockback, float inaccuracy, ProjectileFactory<T> factory) {
		BaseProjectile projectile = factory.create(level, shooter.getX(), shooter.getEyeY() - 0.1f, shooter.getZ());
		projectile.setOwner(shooter);

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

	public static <T extends BaseProjectile> boolean shootProjectile(Level level, Vec3 origin, Vec3 target, float velocity, float damage, int knockback, float inaccuracy, ProjectileFactory<T> factory) {
		BaseProjectile projectile = factory.create(level, origin.x, origin.y, origin.z);

		projectile.setDamage(damage);
		if (knockback > 0) {
			projectile.setKnockback((byte) knockback);
		}

		Vec3 direction = target.subtract(origin).normalize();
		projectile.shoot(direction.x(), direction.y(), direction.z(), velocity, inaccuracy);

		if (level.addFreshEntity(projectile)) {
			level.playSound(null, origin.x, origin.y, origin.z, SoundEvents.CROSSBOW_SHOOT, SoundSource.PLAYERS, 0.8f, 0.4f);
			return true;
		}

		return false;
	}

	public interface ProjectileFactory<T extends BaseProjectile> {
		T create(Level level, double x, double v, double z);
	}

	public record ConfiguredProjectile<T extends BaseProjectile>(String name, float velocity, float damage, int knockback, float inaccuracy, ProjectileFactory<T> factory) {

		public boolean shoot(Level level, Vec3 origin, Vec3 target) {
			return shootProjectile(level, origin, target, velocity, damage, knockback, inaccuracy, factory);
		}

		public boolean shoot(Level level, Vec3 origin, Vec3 target, FloatOperator velocityModifier, FloatOperator damageModifier, IntOperator knockbackModifier, FloatOperator inaccuracyModifier) {
			return shootProjectile(level, origin, target, velocityModifier.apply(velocity), damageModifier.apply(damage), knockbackModifier.apply(knockback), inaccuracyModifier.apply(inaccuracy), factory);
		}

		public boolean shoot(Level level, LivingEntity shooter) {
			return shootProjectile(level, shooter, velocity, damage, knockback, inaccuracy, factory);
		}

		public boolean shoot(Level level, LivingEntity shooter, FloatOperator velocityModifier, FloatOperator damageModifier, IntOperator knockbackModifier, FloatOperator inaccuracyModifier) {
			return shootProjectile(level, shooter, velocityModifier.apply(velocity), damageModifier.apply(damage), knockbackModifier.apply(knockback), inaccuracyModifier.apply(inaccuracy), factory);
		}

	}

}
