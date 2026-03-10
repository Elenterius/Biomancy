package com.github.elenterius.biomancy.util.shooting;

import com.github.elenterius.biomancy.entity.projectile.BaseProjectile;

public interface ProjectileShootContext<T extends BaseProjectile> extends ShootContext {
	ProjectileEntityType<T> entityType();
}
