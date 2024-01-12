package com.github.elenterius.biomancy.world.spatial;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.h2.mvstore.rtree.Spatial;

import java.util.Arrays;

public final class SpatialQuery implements Spatial {

	private final float[] minMax;

	private SpatialQuery(float minX, float minY, float minZ, float maxX, float maxY, float maxZ) {
		this.minMax = new float[]{
				minX, maxX,
				minY, maxY,
				minZ, maxZ
		};
	}

	public SpatialQuery(SpatialQuery other) {
		this.minMax = other.minMax.clone();
	}

	public float minX() {
		return minMax[0];
	}

	public float maxX() {
		return minMax[1];
	}

	public float minY() {
		return minMax[2];
	}

	public float maxY() {
		return minMax[3];
	}

	public float minZ() {
		return minMax[4];
	}

	public float maxZ() {
		return minMax[5];
	}

	@Override
	public float min(int dim) {
		return minMax[dim + dim];
	}

	@Override
	public void setMin(int dim, float v) {
		minMax[dim + dim] = v;
	}

	@Override
	public float max(int dim) {
		return minMax[dim + dim + 1];
	}

	@Override
	public void setMax(int dim, float x) {
		minMax[dim + dim + 1] = x;
	}

	@Override
	public Spatial clone(long id) {
		return new SpatialQuery(this);
	}

	@Override
	public long getId() {
		return 0;
	}

	@Override
	public boolean isNull() {
		return minMax.length == 0;
	}

	@Override
	public int hashCode() {
		return Arrays.hashCode(minMax);
	}

	@Override
	public boolean equals(Object other) {
		if (other == this) {
			return true;
		}

		if (!(other instanceof SpatialQuery otherKey)) {
			return false;
		}

		return equalsIgnoringId(otherKey);
	}

	@Override
	public boolean equalsIgnoringId(Spatial other) {
		return Arrays.equals(minMax, ((SpatialQuery) other).minMax);
	}

	public static SpatialQuery of(BlockPos pos) {
		return new SpatialQuery(
				pos.getX(), pos.getY(), pos.getZ(),
				pos.getX() + 1f, pos.getY() + 1f, pos.getZ() + 1f
		);
	}

	public static SpatialQuery of(BlockPos posA, BlockPos posB) {
		int minX = Math.min(posA.getX(), posB.getX());
		int minY = Math.min(posA.getY(), posB.getY());
		int minZ = Math.min(posA.getZ(), posB.getZ());
		int maxX = Math.max(posA.getX(), posB.getX());
		int maxY = Math.max(posA.getY(), posB.getY());
		int maxZ = Math.max(posA.getZ(), posB.getZ());

		return new SpatialQuery(minX, minY, minZ, maxX, maxY, maxZ);
	}

	public static SpatialQuery of(AABB aabb) {
		return new SpatialQuery(
				(float) aabb.minX, (float) aabb.minY, (float) aabb.minZ,
				(float) aabb.maxX, (float) aabb.maxY, (float) aabb.maxZ
		);
	}

	public static SpatialQuery of(Entity entity) {
		return of(entity.getBoundingBox());
	}

	public static SpatialQuery of(BoundingBox bb) {
		return new SpatialQuery(
				bb.minX(), bb.minY(), bb.minZ(),
				bb.maxX() + 1f, bb.maxY() + 1f, bb.maxZ() + 1f
		);
	}

	public static SpatialQuery of(Vec3 vecA, Vec3 vecB) {
		return new SpatialQuery(
				(float) vecA.x, (float) vecA.y, (float) vecA.z,
				(float) vecB.x, (float) vecB.y, (float) vecB.z
		);
	}

	public static SpatialQuery unitCubeFromLowerCorner(Vec3 vec) {
		return new SpatialQuery(
				(float) vec.x, (float) vec.y, (float) vec.z,
				(float) vec.x + 1f, (float) vec.y + 1f, (float) vec.z + 1f
		);
	}

	public static SpatialQuery unitCubeFromMiddle(Vec3 vec) {
		return new SpatialQuery(
				(float) vec.x - 0.5f, (float) vec.y - 0.5f, (float) vec.z - 0.5f,
				(float) vec.x + 0.5f, (float) vec.y + 0.5f, (float) vec.z + 0.5f
		);
	}
}
