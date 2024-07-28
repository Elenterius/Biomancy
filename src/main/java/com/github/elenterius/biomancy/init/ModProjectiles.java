package com.github.elenterius.biomancy.init;

import com.github.elenterius.biomancy.entity.projectile.*;
import com.github.elenterius.biomancy.item.weapon.gun.Gun;
import com.github.elenterius.biomancy.util.function.FloatOperator;
import com.github.elenterius.biomancy.util.function.IntOperator;
import net.minecraft.sounds.SoundEvent;
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
	public static final ConfiguredProjectile<AcidBlobProjectile> ACID_BLOB = build("Acid Blob", 1.2f, 2, 0, convertToInaccuracy(0.9f), SoundEvents.SLIME_JUMP_SMALL, (level, x, y, z) -> new AcidBlobProjectile(level, x, y, z, false));
	public static final ConfiguredProjectile<AcidBlobProjectile> FALLING_ACID_BLOB = build("Falling Acid Blob", 0.1f, 2, 0, convertToInaccuracy(0.9f), SoundEvents.SLIME_SQUISH_SMALL, AcidBlobProjectile::new);
	public static final ConfiguredProjectile<BloomberryProjectile> BLOOMBERRY = build("Bloomberry", 1.25f, 2, 0, convertToInaccuracy(0.9f), BloomberryProjectile::new);
	public static final ConfiguredProjectile<AcidSpitProjectile> GASTRIC_SPIT = build("Gastric Spit", 1.5f, 1, 0, 0.25f, SoundEvents.LLAMA_SPIT, AcidSpitProjectile::new);

	private static float convertToInaccuracy(float accuracy) {
		return -Gun.MAX_INACCURACY * accuracy + Gun.MAX_INACCURACY;
	}

	private static <T extends BaseProjectile> ConfiguredProjectile<T> build(String name, float velocity, float damage, int knockback, float accuracy, ProjectileFactory<T> factory) {
		ConfiguredProjectile<T> configuredProjectile = new ConfiguredProjectile<>(name, velocity, damage, knockback, convertToInaccuracy(accuracy), SoundEvents.CROSSBOW_SHOOT, factory);
		PRECONFIGURED_PROJECTILES.add(configuredProjectile);
		return configuredProjectile;
	}

	private static <T extends BaseProjectile> ConfiguredProjectile<T> build(String name, float velocity, float damage, int knockback, float accuracy, SoundEvent shootSound, ProjectileFactory<T> factory) {
		ConfiguredProjectile<T> configuredProjectile = new ConfiguredProjectile<>(name, velocity, damage, knockback, convertToInaccuracy(accuracy), shootSound, factory);
		PRECONFIGURED_PROJECTILES.add(configuredProjectile);
		return configuredProjectile;
	}

	public static <T extends BaseProjectile> boolean shootProjectile(Level level, LivingEntity shooter, float velocity, float damage, int knockback, float inaccuracy, SoundEvent shootSound, ProjectileFactory<T> factory) {
		BaseProjectile projectile = factory.create(level, shooter.getX(), shooter.getEyeY() - 0.1f, shooter.getZ());
		projectile.setOwner(shooter);

		projectile.setDamage(damage);
		if (knockback > 0) {
			projectile.setKnockback((byte) knockback);
		}

		Vec3 direction = shooter.getLookAngle();
		projectile.shoot(direction.x(), direction.y(), direction.z(), velocity, inaccuracy);

		if (level.addFreshEntity(projectile)) {
			level.playSound(null, shooter.getX(), shooter.getY(), shooter.getZ(), shootSound, SoundSource.PLAYERS, 0.8f, 0.4f);
			return true;
		}

		return false;
	}

	public static <T extends BaseProjectile> boolean shootProjectile(Level level, Vec3 origin, Vec3 target, float velocity, float damage, int knockback, float inaccuracy, SoundEvent shootSound, ProjectileFactory<T> factory) {
		BaseProjectile projectile = factory.create(level, origin.x, origin.y, origin.z);

		projectile.setDamage(damage);
		if (knockback > 0) {
			projectile.setKnockback((byte) knockback);
		}

		Vec3 direction = target.subtract(origin).normalize();
		projectile.shoot(direction.x(), direction.y(), direction.z(), velocity, inaccuracy);

		if (level.addFreshEntity(projectile)) {
			level.playSound(null, origin.x, origin.y, origin.z, shootSound, SoundSource.PLAYERS, 0.8f, 0.4f);
			return true;
		}

		return false;
	}

	public interface ProjectileFactory<T extends BaseProjectile> {
		T create(Level level, double x, double v, double z);
	}

	public record ConfiguredProjectile<T extends BaseProjectile>(String name, float velocity, float damage, int knockback, float inaccuracy, SoundEvent shootSound, ProjectileFactory<T> factory) {

		public boolean shoot(Level level, Vec3 origin, Vec3 target) {
			return shootProjectile(level, origin, target, velocity, damage, knockback, inaccuracy, shootSound, factory);
		}

		public boolean shoot(Level level, Vec3 origin, Vec3 target, FloatOperator velocityModifier, FloatOperator damageModifier, IntOperator knockbackModifier, FloatOperator inaccuracyModifier) {
			return shootProjectile(level, origin, target, velocityModifier.apply(velocity), damageModifier.apply(damage), knockbackModifier.apply(knockback), inaccuracyModifier.apply(inaccuracy), shootSound, factory);
		}

		public boolean shoot(Level level, LivingEntity shooter) {
			return shootProjectile(level, shooter, velocity, damage, knockback, inaccuracy, shootSound, factory);
		}

		public boolean shoot(Level level, LivingEntity shooter, FloatOperator velocityModifier, FloatOperator damageModifier, IntOperator knockbackModifier, FloatOperator inaccuracyModifier) {
			return shootProjectile(level, shooter, velocityModifier.apply(velocity), damageModifier.apply(damage), knockbackModifier.apply(knockback), inaccuracyModifier.apply(inaccuracy), shootSound, factory);
		}

	}

}
