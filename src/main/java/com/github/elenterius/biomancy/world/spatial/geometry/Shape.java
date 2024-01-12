package com.github.elenterius.biomancy.world.spatial.geometry;

import com.github.elenterius.biomancy.util.serialization.NBTSerializable;
import com.github.elenterius.biomancy.util.serialization.NBTSerializer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public interface Shape extends NBTSerializable<Shape> {

	boolean contains(double x, double y, double z);

	//boolean containsCuboid(double minX, double minY, double minZ, double maxX, double maxY, double maxZ);

	default boolean intersectsCuboid(AABB aabb) {
		return intersectsCuboid(aabb.minX, aabb.minY, aabb.minZ, aabb.maxX, aabb.maxY, aabb.maxZ);
	}

	boolean intersectsCuboid(double minX, double minY, double minZ, double maxX, double maxY, double maxZ);

	Vec3 center();

	double distanceToSqr(double x, double y, double z);

	AABB getAABB();

	default boolean isEmpty() {
		return this == EMPTY;
	}

	Shape EMPTY = new Shape() {
		static final Serializer SERIALIZER = new Serializer("empty");

		@Override
		public boolean contains(double x, double y, double z) {
			return false;
		}

		@Override
		public boolean intersectsCuboid(double minX, double minY, double minZ, double maxX, double maxY, double maxZ) {
			return false;
		}

		@Override
		public Vec3 center() {
			return Vec3.ZERO;
		}

		@Override
		public double distanceToSqr(double x, double y, double z) {
			return Double.MAX_VALUE;
		}

		@Override
		public AABB getAABB() {
			return AABB.unitCubeFromLowerCorner(Vec3.ZERO);
		}

		@Override
		public int hashCode() {
			return 0;
		}

		@Override
		public boolean equals(Object obj) {
			return super.equals(obj); //compare by identity
		}

		@Override
		public NBTSerializer<Shape> getNBTSerializer() {
			return SERIALIZER;
		}

		public record Serializer(String id) implements NBTSerializer<Shape> {
			@Override
			public CompoundTag write(Shape shape) {
				return new CompoundTag();
			}

			@Override
			public Shape read(CompoundTag tag) {
				return Shape.EMPTY;
			}
		}
	};
}
