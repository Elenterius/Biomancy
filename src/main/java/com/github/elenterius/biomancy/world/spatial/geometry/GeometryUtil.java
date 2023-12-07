package com.github.elenterius.biomancy.world.spatial.geometry;

import net.minecraft.util.Mth;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public final class GeometryUtil {
	private GeometryUtil() {}

	public static Vec3 closestPointOnAABB(double minX, double minY, double minZ, double maxX, double maxY, double maxZ, Vec3 pos) {
		double x = Mth.clamp(pos.x, minX, maxX);
		double y = Mth.clamp(pos.y, minY, maxY);
		double z = Mth.clamp(pos.z, minZ, maxZ);
		return new Vec3(x, y, z);
	}

	public static Vec3 closestPointOnAABB(AABB aabb, Vec3 pos) {
		double x = Mth.clamp(pos.x, aabb.minX, aabb.maxX);
		double y = Mth.clamp(pos.y, aabb.minY, aabb.maxY);
		double z = Mth.clamp(pos.z, aabb.minZ, aabb.maxZ);
		return new Vec3(x, y, z);
	}

}
