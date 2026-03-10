package com.github.elenterius.biomancy.util.shooting;

public enum ShootBehavior {
	INSTANT,
	ON_FULL_CHARGE,
	ON_RELEASE_INSTANT,
	ON_RELEASE_WITH_FULL_CHARGE;

	public boolean isOnRelease() {
		return this == ON_RELEASE_WITH_FULL_CHARGE || this == ON_RELEASE_INSTANT;
	}
}
