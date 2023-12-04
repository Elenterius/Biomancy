package com.github.elenterius.biomancy.world.spatial;

import net.minecraft.world.phys.AABB;
import org.h2.mvstore.rtree.Spatial;

import java.util.Arrays;

public class SpatialKey implements Spatial {

	private final long id;
	private final float[] minMax;

	public SpatialKey(long id, AABB aabb) {
		this.id = id;
		this.minMax = new float[]{
				(float) aabb.minX, (float) aabb.maxX,
				(float) aabb.minY, (float) aabb.maxY,
				(float) aabb.minZ, (float) aabb.maxZ
		};
	}

	public SpatialKey(long id, float minX, float minY, float minZ, float maxX, float maxY, float maxZ) {
		this.id = id;
		this.minMax = new float[]{
				minX, maxX,
				minY, maxY,
				minZ, maxZ
		};
	}

	public SpatialKey(long id, SpatialKey other) {
		this.id = id;
		this.minMax = other.minMax.clone();
	}

	@Override
	public float min(int dim) {
		return minMax[dim + dim];
	}

	@Override
	public void setMin(int dim, float x) {
		minMax[dim + dim] = x;
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
		return new SpatialKey(id, this);
	}

	@Override
	public long getId() {
		return id;
	}

	@Override
	public boolean isNull() {
		return minMax.length == 0;
	}

	@Override
	public int hashCode() {
		return Long.hashCode(id);
	}

	@Override
	public boolean equals(Object other) {
		if (other == this) {
			return true;
		}
		else if (!(other instanceof SpatialKey)) {
			return false;
		}

		SpatialKey otherKey = (SpatialKey) other;
		if (id != otherKey.id) {
			return false;
		}

		return equalsIgnoringId(otherKey);
	}

	@Override
	public boolean equalsIgnoringId(Spatial other) {
		return Arrays.equals(minMax, ((SpatialKey) other).minMax);
	}

}
