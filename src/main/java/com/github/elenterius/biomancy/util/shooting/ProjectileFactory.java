package com.github.elenterius.biomancy.util.shooting;

import com.github.elenterius.biomancy.entity.projectile.BaseProjectile;
import net.minecraft.world.level.Level;

public interface ProjectileFactory<T extends BaseProjectile> {
	T create(Level level, double x, double v, double z);
}
