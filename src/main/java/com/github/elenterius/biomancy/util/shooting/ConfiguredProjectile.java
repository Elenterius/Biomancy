package com.github.elenterius.biomancy.util.shooting;

import com.github.elenterius.biomancy.entity.projectile.BaseProjectile;
import net.minecraft.sounds.SoundEvent;

public record ConfiguredProjectile<T extends BaseProjectile>(float velocity, float damage, int knockback, float accuracy, SoundEvent shootSound,
                                                             ProjectileFactory<T> factory) implements ProjectileShootContext<T> {

	public ConfiguredProjectile {
		if (accuracy < -1f || accuracy > 1f) throw new IllegalArgumentException("accuracy of " + accuracy + "is not within valid bounds of -1..1");
	}

	@Override
	public float spreadBias() {
		return SpreadBias.SLIGHTLY_CENTER_HEAVY;
	}

	@Override
	public int shotCount() {
		return 1;
	}

}
