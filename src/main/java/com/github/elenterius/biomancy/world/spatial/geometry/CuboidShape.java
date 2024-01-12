package com.github.elenterius.biomancy.world.spatial.geometry;

import com.github.elenterius.biomancy.util.serialization.NBTSerializer;
import com.github.elenterius.biomancy.world.spatial.type.ShapeSerializers;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

/**
 * Axis Aligned Box
 */
public class CuboidShape implements Shape {

	private final Vec3 origin;
	private final AABB aabb;

	public CuboidShape(Vec3 min, Vec3 max) {
		aabb = new AABB(min, max);
		origin = aabb.getCenter();
	}

	public CuboidShape(BlockPos min, BlockPos max) {
		aabb = new AABB(min, max);
		origin = aabb.getCenter();
	}

	public CuboidShape(double minX, double minY, double minZ, double maxX, double maxY, double maxZ) {
		aabb = new AABB(minX, minY, minZ, maxX, maxY, maxZ);
		origin = aabb.getCenter();
	}

	@Override
	public boolean contains(double x, double y, double z) {
		return aabb.contains(x, y, z);
	}

	@Override
	public Vec3 center() {
		return origin;
	}

	@Override
	public double distanceToSqr(double x, double y, double z) {
		return origin.distanceToSqr(x, y, z);
	}

	@Override
	public AABB getAABB() {
		return aabb;
	}

	@Override
	public boolean intersectsCuboid(double minX, double minY, double minZ, double maxX, double maxY, double maxZ) {
		return aabb.intersects(minX, minY, minZ, maxX, maxY, maxZ);
	}

	@Override
	public NBTSerializer<Shape> getNBTSerializer() {
		return ShapeSerializers.CUBOID_SERIALIZER;
	}

	public record Serializer(String id) implements NBTSerializer<CuboidShape> {
		@Override
		public CompoundTag write(CuboidShape shape) {
			CompoundTag tag = new CompoundTag();
			tag.putLongArray("MinMax", new long[]{
					Double.doubleToLongBits(shape.aabb.minX),
					Double.doubleToLongBits(shape.aabb.minY),
					Double.doubleToLongBits(shape.aabb.minZ),
					Double.doubleToLongBits(shape.aabb.maxX),
					Double.doubleToLongBits(shape.aabb.maxY),
					Double.doubleToLongBits(shape.aabb.maxZ)
			});

			return tag;
		}

		@Override
		public CuboidShape read(CompoundTag tag) {
			long[] minMax = tag.getLongArray("MinMax");
			double minX = Double.longBitsToDouble(minMax[0]);
			double minY = Double.longBitsToDouble(minMax[1]);
			double minZ = Double.longBitsToDouble(minMax[2]);
			double maxX = Double.longBitsToDouble(minMax[3]);
			double maxY = Double.longBitsToDouble(minMax[4]);
			double maxZ = Double.longBitsToDouble(minMax[5]);
			return new CuboidShape(minX, minY, minZ, maxX, maxY, maxZ);
		}
	}

}
