package com.github.elenterius.biomancy.util.shooting;

import com.github.elenterius.biomancy.entity.projectile.BaseProjectile;
import net.minecraft.sounds.SoundEvent;
import org.joml.Vector3f;
import org.joml.Vector3fc;
import org.jspecify.annotations.Nullable;

public record GunProperties<T extends BaseProjectile>(float damage, int knockback, float velocity, float accuracy, float spreadBias, int shotCount,
                                                      Vector3fc localOffset, int delayBetweenShots, ShootBehavior shootBehavior,
                                                      int maxAmmo, int reloadDurationTicks, boolean isAutoReload,
                                                      ProjectileFactory<T> factory, @Nullable SoundEvent shootSound) {

	public static <T extends BaseProjectile> Builder<T> builder() {
		return new Builder<>();
	}

	public enum ShootBehavior {
		INSTANT,
		ON_FULL_CHARGE,
		ON_RELEASE_INSTANT,
		ON_RELEASE_WITH_FULL_CHARGE;

		public boolean isOnRelease() {
			return this == ON_RELEASE_WITH_FULL_CHARGE || this == ON_RELEASE_INSTANT;
		}
	}

	public static class Builder<T extends BaseProjectile> {

		private float damage = 0;
		private int knockback = 0;
		private float velocity = 1f;
		private float accuracy = 1f;
		private float spreadBias = SpreadBias.UNIFORM;
		private int shotCount = 1;
		private Vector3fc localOffset = new Vector3f(0f, -0.1f, 0f);

		private int timeBetweenShots = 20;
		private ShootBehavior shootBehavior = ShootBehavior.INSTANT;

		private int maxAmmo = 6;
		private int reloadDurationTicks = 20;
		private boolean isAutoReload = false;

		@SuppressWarnings("DataFlowIssue")
		private ProjectileFactory<T> factory = null;

		private @Nullable SoundEvent shootSound = null;

		public Builder<T> damage(float damage) {
			this.damage = damage;
			return this;
		}

		public Builder<T> knockback(int knockback) {
			this.knockback = knockback;
			return this;
		}

		public Builder<T> velocity(float velocity) {
			this.velocity = velocity;
			return this;
		}

		public Builder<T> accuracy(float accuracy) {
			if (accuracy < -1f || accuracy > 1f) throw new IllegalArgumentException("Invalid accuracy: " + accuracy);
			this.accuracy = accuracy;
			return this;
		}

		public Builder<T> spreadBias(float spreadBias) {
			this.spreadBias = spreadBias;
			return this;
		}

		public Builder<T> shotCount(int shotCount) {
			this.shotCount = shotCount;
			return this;
		}

		/// @param localOffset (x=right, y=up, z=forward)
		public Builder<T> localOffset(Vector3fc localOffset) {
			this.localOffset = localOffset;
			return this;
		}

		public Builder<T> localOffset(float x, float y, float z) {
			localOffset = new Vector3f(x, y, z);
			return this;
		}

		public Builder<T> timeBetweenShots(int ticks) {
			timeBetweenShots = ticks;
			return this;
		}

		public Builder<T> shootBehavior(ShootBehavior behavior) {
			shootBehavior = behavior;
			return this;
		}

		public Builder<T> fireRate(float fireRate) {
			if (fireRate > 20f) throw new IllegalArgumentException("Fire rate above 20.0f is not supported. Fire rate is: " + fireRate);
			timeBetweenShots = Math.max(1, Math.round(Gun.ONE_SECOND_IN_TICKS / fireRate));
			return this;
		}

		public Builder<T> maxAmmo(int maxAmmo) {
			this.maxAmmo = maxAmmo;
			return this;
		}

		public Builder<T> reloadDuration(int ticks) {
			reloadDurationTicks = ticks;
			return this;
		}

		public Builder<T> autoReload() {
			isAutoReload = true;
			return this;
		}

		public Builder<T> autoReload(boolean bool) {
			isAutoReload = bool;
			return this;
		}

		public Builder<T> shootSound(@Nullable SoundEvent shootSound) {
			this.shootSound = shootSound;
			return this;
		}

		public Builder<T> projectile(ProjectileFactory<T> factory) {
			this.factory = factory;
			return this;
		}

		public GunProperties<T> build() {
			if (factory == null) throw new IllegalArgumentException("Projectile factory is null");

			return new GunProperties<>(
					damage, knockback, velocity, accuracy, spreadBias, shotCount, localOffset,
					timeBetweenShots, shootBehavior,
					maxAmmo, reloadDurationTicks, isAutoReload,
					factory, shootSound
			);
		}

	}

}
