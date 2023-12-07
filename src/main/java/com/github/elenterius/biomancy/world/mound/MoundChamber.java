package com.github.elenterius.biomancy.world.mound;

import com.github.elenterius.biomancy.util.serialization.NBTSerializer;
import com.github.elenterius.biomancy.world.spatial.geometry.Shape;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class MoundChamber implements Chamber, Shape {
	private final Shape shape;
	private final Vec3 origin;

	public MoundChamber(Vec3 origin, Shape shape) {
		this.shape = shape;
		this.origin = origin;
	}

	public MoundChamber(Shape shape) {
		this.shape = shape;
		origin = shape.getCenter();
	}

	@Override
	public boolean contains(double x, double y, double z) {
		return shape.contains(x, y, z);
	}

	@Override
	public boolean intersectsCuboid(double minX, double minY, double minZ, double maxX, double maxY, double maxZ) {
		return shape.intersectsCuboid(minX, minY, minZ, maxX, maxY, maxZ);
	}

	@Override
	public Vec3 getCenter() {
		return shape.getCenter();
	}

	@Override
	public Vec3 getOrigin() {
		return origin;
	}

	@Override
	public double distanceToSqr(double x, double y, double z) {
		return shape.distanceToSqr(x, y, z);
	}

	@Override
	public AABB getAABB() {
		return shape.getAABB();
	}

	@Override
	public NBTSerializer<Shape> getNBTSerializer() {
		return null;
	}
}
