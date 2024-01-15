package com.github.elenterius.biomancy.util;

import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class VectorUtil {

	public static final Vec3i XZ_PLANE = new Vec3i(1, 0, 1);
	public static final Vec3i XY_PLANE = new Vec3i(1, 1, 0);
	public static final Vec3i YZ_PLANE = new Vec3i(0, 1, 1);

	private static final Vec3i[] OFFSETS_CUBE_3I;

	static {
		List<Vec3i> offsets = new ArrayList<>();
		for (int y = -1; y <= 1; y++) {
			for (int x = -1; x <= 1; x++) {
				for (int z = -1; z <= 1; z++) {
					if (x == 0 && y == 0 && z == 0) continue;
					offsets.add(new Vec3i(x, y, z));
				}
			}
		}
		OFFSETS_CUBE_3I = offsets.toArray(new Vec3i[]{});
	}

	private VectorUtil() {}

	public static Vec3i randomOffsetInCube3i(Random random) {
		return OFFSETS_CUBE_3I[random.nextInt(OFFSETS_CUBE_3I.length)];
	}

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

	public static double max(double a, double b, double c) {
		return Math.max(Math.max(a, b), c);
	}

	public static double boxDistanceSqr(AABB aabb, Vec3 pos) {
		double dx = max(aabb.minX - pos.x, 0, pos.x - aabb.maxX);
		double dy = max(aabb.minY - pos.y, 0, pos.y - aabb.maxY);
		double dz = max(aabb.minZ - pos.z, 0, pos.z - aabb.maxZ);
		return dx * dx + dy * dy + dz * dz;
	}
}
