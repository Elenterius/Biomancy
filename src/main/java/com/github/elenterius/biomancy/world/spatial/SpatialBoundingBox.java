package com.github.elenterius.biomancy.world.spatial;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.h2.mvstore.rtree.Spatial;

public final class SpatialBoundingBox {
	private SpatialBoundingBox() {}

	public static Spatial of(BlockPos pos) {
		return new SpatialKey(0,
				pos.getX(), pos.getY(), pos.getZ(),
				pos.getX() + 1, pos.getY() + 1, pos.getZ() + 1
		);
	}

	public static Spatial of(BlockPos posA, BlockPos posB) {
		int minX = Math.min(posA.getX(), posB.getX());
		int minY = Math.min(posA.getY(), posB.getY());
		int minZ = Math.min(posA.getZ(), posB.getZ());
		int maxX = Math.max(posA.getX(), posB.getX());
		int maxY = Math.max(posA.getY(), posB.getY());
		int maxZ = Math.max(posA.getZ(), posB.getZ());

		return new SpatialKey(0, minX, minY, minZ, maxX, maxY, maxZ);
	}

	public static Spatial of(AABB aabb) {
		return new SpatialKey(0, aabb);
	}

	public static Spatial of(Entity entity) {
		return new SpatialKey(0, entity.getBoundingBox());
	}

	public static Spatial of(BoundingBox bb) {
		return new SpatialKey(0,
				bb.minX(), bb.minY(), bb.minZ(),
				bb.maxX() + 1, bb.maxY() + 1, bb.maxZ() + 1
		);
	}

	public static Spatial of(Vec3 vecA, Vec3 vecB) {
		return new SpatialKey(0,
				(float) vecA.x, (float) vecA.y, (float) vecA.z,
				(float) vecB.x, (float) vecB.y, (float) vecB.z
		);
	}

	public static Spatial unitCubeFromLowerCorner(Vec3 vec) {
		return new SpatialKey(0,
				(float) vec.x, (float) vec.y, (float) vec.z,
				(float) vec.x + 1f, (float) vec.y + 1f, (float) vec.z + 1f
		);
	}

	public static Spatial unitCubeFromMiddle(Vec3 vec) {
		return new SpatialKey(0,
				(float) vec.x - 0.5f, (float) vec.y - 0.5f, (float) vec.z - 0.5f,
				(float) vec.x + 0.5f, (float) vec.y + 0.5f, (float) vec.z + 0.5f
		);
	}
}
