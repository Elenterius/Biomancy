package com.github.elenterius.biomancy.util.shooting;

import org.joml.Vector3f;
import org.joml.Vector3fc;

public interface ShootContext {
	/// default spawn/origin offset of shot in local space (x=RIGHT, y=UP and z=FORWARD) relative to the view vector
	Vector3fc DEFAULT_LOCAL_OFFSET = new Vector3f(0f, -0.1f, 0f);

	float velocity();

	float accuracy();

	float spreadBias();

	/// Number of projectiles to shoot per shot:
	/// - default: 1
	/// - pistol burst: ~3
	/// - shotgun: ~5-10
	int projectileCount();

	//		float multishot();

	float damage();

	int knockback();

	default Vector3fc localOffset() {
		return DEFAULT_LOCAL_OFFSET;
	}
}
