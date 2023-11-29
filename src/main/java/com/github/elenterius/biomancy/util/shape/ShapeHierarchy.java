package com.github.elenterius.biomancy.util.shape;

import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import net.minecraft.core.SectionPos;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class ShapeHierarchy<T extends Shape> {

	/**
	 * spatial hash map based on a 16x16x16 cell grid
	 */
	protected final Long2ObjectMap<Set<T>> sections = new Long2ObjectOpenHashMap<>();

	protected final AABB aabb;

	public ShapeHierarchy(Iterable<T> shapes) {
		double aabbMinX = Double.MAX_VALUE;
		double aabbMinY = Double.MAX_VALUE;
		double aabbMinZ = Double.MAX_VALUE;
		double aabbMaxX = Double.MIN_VALUE;
		double aabbMaxY = Double.MIN_VALUE;
		double aabbMaxZ = Double.MIN_VALUE;

		for (T shape : shapes) {
			AABB boundingBox = shape.getAABB();

			if (boundingBox.minX < aabbMinX) aabbMinX = boundingBox.minX;
			if (boundingBox.minY < aabbMinY) aabbMinY = boundingBox.minY;
			if (boundingBox.minZ < aabbMinZ) aabbMinZ = boundingBox.minZ;
			if (boundingBox.maxX > aabbMaxX) aabbMaxX = boundingBox.maxX;
			if (boundingBox.maxY > aabbMaxY) aabbMaxY = boundingBox.maxY;
			if (boundingBox.maxZ > aabbMaxZ) aabbMaxZ = boundingBox.maxZ;

			addShapeToSections(shape);
		}

		this.aabb = new AABB(aabbMinX, aabbMinY, aabbMinZ, aabbMaxX, aabbMaxY, aabbMaxZ);
	}

	protected void addShapesToSection(long sectionKey, Collection<T> shapes) {
		sections.computeIfAbsent(sectionKey, k -> new HashSet<>()).addAll(shapes);
	}

	protected void addShapeToSections(T shape) {
		AABB boundingBox = shape.getAABB();
		int minSectionX = SectionPos.blockToSectionCoord(boundingBox.minX);
		int minSectionY = SectionPos.blockToSectionCoord(boundingBox.minY);
		int minSectionZ = SectionPos.blockToSectionCoord(boundingBox.minZ);
		int maxSectionX = SectionPos.blockToSectionCoord(boundingBox.maxX);
		int maxSectionY = SectionPos.blockToSectionCoord(boundingBox.maxY);
		int maxSectionZ = SectionPos.blockToSectionCoord(boundingBox.maxZ);

		for (int y = minSectionY; y <= maxSectionY; y++) {
			for (int x = minSectionX; x <= maxSectionX; x++) {
				for (int z = minSectionZ; z <= maxSectionZ; z++) {
					sections.computeIfAbsent(SectionPos.asLong(x, y, z), k -> new HashSet<>()).add(shape);
				}
			}
		}
	}

	@Nullable
	protected Set<T> getShapesInSection(double x, double y, double z) {
		int sectionX = SectionPos.blockToSectionCoord(x);
		int sectionY = SectionPos.blockToSectionCoord(y);
		int sectionZ = SectionPos.blockToSectionCoord(z);
		return sections.get(SectionPos.asLong(sectionX, sectionY, sectionZ));
	}

	public Vec3 getCenter() {
		return aabb.getCenter();
	}

	public double distanceToSqr(double x, double y, double z) {
		double smallestDistSqr = Double.MAX_VALUE;

		Set<T> shapesInSection = getShapesInSection(x, y, z);
		if (shapesInSection != null) {
			for (Shape shape : shapesInSection) {
				if (shape.contains(x, y, z)) {
					double distSqr = shape.distanceToSqr(x, y, z);
					if (distSqr < smallestDistSqr) {
						smallestDistSqr = distSqr;
					}
				}
			}
		}

		return smallestDistSqr;
	}

	public AABB getAABB() {
		return aabb;
	}

	@Nullable
	public T getClosestShapeContaining(double x, double y, double z) {
		if (!aabb.contains(x, y, z)) return null;

		T closestShape = null;

		Set<T> shapesInSection = getShapesInSection(x, y, z);
		if (shapesInSection != null) {
			double minDistSqr = Double.MAX_VALUE;
			for (T shape : shapesInSection) {
				if (shape.contains(x, y, z)) {
					double distSqr = shape.distanceToSqr(x, y, z);
					if (distSqr < minDistSqr) {
						closestShape = shape;
						minDistSqr = distSqr;
					}
				}
			}
		}

		return closestShape;
	}

	public boolean contains(double x, double y, double z) {
		if (!aabb.contains(x, y, z)) return false;

		Set<T> shapesInSection = getShapesInSection(x, y, z);
		if (shapesInSection != null) {
			for (Shape shape : shapesInSection) {
				if (shape.contains(x, y, z)) {
					return true;
				}
			}
		}

		return false;
	}

}
