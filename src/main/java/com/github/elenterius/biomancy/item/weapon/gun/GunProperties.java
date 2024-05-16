package com.github.elenterius.biomancy.item.weapon.gun;

public record GunProperties(float projectileDamageModifier, float accuracy, int shootDelayTicks, int maxAmmo, int reloadDurationTicks, boolean isAutoReload) {

	public static Builder builder() {
		return new Builder();
	}

	public static class Builder {

		private int shootDelayTicks = 20;
		private float projectileDamageModifier = 0;
		private int maxAmmo = 6;
		private float accuracy = 1f;
		private int reloadDurationTicks = 20;
		private boolean isAutoReload = false;

		public Builder shootDelay(int ticks) {
			shootDelayTicks = ticks;
			return this;
		}

		public Builder fireRate(float fireRate) {
			shootDelayTicks = Math.max(1, Math.round(Gun.ONE_SECOND_IN_TICKS / fireRate));
			return this;
		}

		public Builder damageModifier(float modifier) {
			this.projectileDamageModifier = modifier;
			return this;
		}

		public Builder maxAmmo(int maxAmmo) {
			this.maxAmmo = maxAmmo;
			return this;
		}

		public Builder accuracy(float accuracy) {
			if (accuracy < 0f || accuracy > 1f) throw new IllegalArgumentException("Invalid accuracy: " + accuracy);
			this.accuracy = accuracy;
			return this;
		}

		public Builder reloadDuration(int ticks) {
			reloadDurationTicks = ticks;
			return this;
		}

		public Builder autoReload(boolean bool) {
			isAutoReload = bool;
			return this;
		}

		public GunProperties build() {
			return new GunProperties(projectileDamageModifier, accuracy, shootDelayTicks, maxAmmo, reloadDurationTicks, isAutoReload);
		}
	}

}
