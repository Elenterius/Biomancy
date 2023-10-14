package com.github.elenterius.biomancy.util;

import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.world.phys.Vec3;

public class VectorUtil {

	private VectorUtil() {}

	public static final Vec3i XZ_PLANE = new Vec3i(1, 0, 1);
	public static final Vec3i XY_PLANE = new Vec3i(1, 1, 0);
	public static final Vec3i YZ_PLANE = new Vec3i(0, 1, 1);

	public static Vec3i abs(Vec3i vec) {
		return new Vec3i(Math.abs(vec.getX()), Math.abs(vec.getY()), Math.abs(vec.getZ()));
	}

	public static Vec3i axisAlignedPlane3i(Direction direction) {
		Vec3i normal = direction.getNormal();
		return new Vec3i(1 - Math.abs(normal.getX()), 1 - Math.abs(normal.getY()), 1 - Math.abs(normal.getZ()));
	}

	public static Vec3 axisAlignedPlane3d(Direction direction) {
		Vec3i normal = direction.getNormal();
		return new Vec3(1d - Math.abs(normal.getX()), 1d - Math.abs(normal.getY()), 1d - Math.abs(normal.getZ()));
	}

}
