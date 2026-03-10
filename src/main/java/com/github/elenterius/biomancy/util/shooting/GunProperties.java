package com.github.elenterius.biomancy.util.shooting;

import com.github.elenterius.biomancy.entity.projectile.BaseProjectile;
import net.minecraft.SharedConstants;
import net.minecraft.Util;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import org.joml.Vector3f;
import org.joml.Vector3fc;
import org.jspecify.annotations.Nullable;

import java.util.function.Function;
import java.util.function.Supplier;

public record GunProperties<T extends BaseProjectile>(float damage, int knockback, float velocity, float accuracy, float spreadBias, int projectileCount,
                                                      Vector3fc localOffset, int delayBetweenShots, ShootBehavior shootBehavior,
                                                      int maxAmmo, int reloadDurationTicks, boolean isAutoReload,
                                                      Supplier<ProjectileEntityType<T>> projectileType, GunSounds sounds) {

	private static final Function<GunProperties<? extends BaseProjectile>, ProjectileRange> DEFAULT_RANGE_CALCULATION = Util.memoize(gunProperties -> computeRange(gunProperties, gunProperties.velocity, gunProperties.accuracy));

	public static <T extends BaseProjectile> ProjectileRange computeRange(GunProperties<T> properties, float velocity, float accuracy) {
		ProjectileEntityType<? extends BaseProjectile> type = properties.projectileType().get();

		float upOffset = properties.localOffset.y();
		float forwardOffset = properties.localOffset.z();

		float height = Player.DEFAULT_EYE_HEIGHT + upOffset;
		float aimAngle = 2f * Mth.DEG_TO_RAD; //slightly up to simulate "natural player aim"

		float spreadAngle = (float) Math.pow(1f - Mth.abs(accuracy), properties.spreadBias) * Mth.PI;

		float minRange = (float) ProjectileUtil.computeRange(height, aimAngle - spreadAngle * 0.5f, velocity, type.getAirDrag(), type.getGravity());
		float maxRange = (float) ProjectileUtil.computeRange(height, aimAngle + spreadAngle * 0.5f, velocity, type.getAirDrag(), type.getGravity());

		return new ProjectileRange(minRange + forwardOffset, maxRange + forwardOffset);
	}

	public static <T extends BaseProjectile> Builder<T> builder() {
		return new Builder<>();
	}

	public float horizontalDefaultRange() {
		return DEFAULT_RANGE_CALCULATION.apply(this).mean();
	}

	public ProjectileRange defaultRange() {
		return DEFAULT_RANGE_CALCULATION.apply(this);
	}

	public static class Builder<T extends BaseProjectile> {

		private float damage = 0;
		private int knockback = 0;
		private float velocity = 1f;
		private float accuracy = 1f;
		private float spreadBias = SpreadBias.UNIFORM;
		private int projectileCount = 1;
		private Vector3fc localOffset = new Vector3f(0f, -0.1f, 0f);

		private int timeBetweenShots = 20;
		private ShootBehavior shootBehavior = ShootBehavior.INSTANT;

		private int maxAmmo = 6;
		private int reloadDurationTicks = 20;
		private boolean isAutoReload = false;

		private @Nullable Supplier<ProjectileEntityType<T>> projectileType = null;

		private GunSounds sounds = GunSounds.DEFAULT;

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

		public Builder<T> projectileCount(int projectileCount) {
			this.projectileCount = projectileCount;
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
			timeBetweenShots = Math.max(1, Math.round(SharedConstants.TICKS_PER_SECOND / fireRate));
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

		public Builder<T> sounds(GunSounds sounds) {
			this.sounds = sounds;
			return this;
		}

		public Builder<T> projectile(Supplier<ProjectileEntityType<T>> projectileType) {
			this.projectileType = projectileType;
			return this;
		}

		public GunProperties<T> build() {
			if (projectileType == null) throw new IllegalArgumentException("Projectile EntityType Supplier is null");

			return new GunProperties<>(
					damage, knockback, velocity, accuracy, spreadBias, projectileCount, localOffset,
					timeBetweenShots, shootBehavior,
					maxAmmo, reloadDurationTicks, isAutoReload,
					projectileType,
					sounds
			);
		}
	}

}
