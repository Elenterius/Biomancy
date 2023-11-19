package com.github.elenterius.biomancy.util.shape;

import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class SphereShape implements Shape.Sphere {

	private final Vec3 origin;
	private final float radius;
	private final AABB aabb;

	public SphereShape(Vec3 origin, float radius) {
		this.origin = origin;
		this.radius = radius;
		aabb = new AABB(origin.x - radius, origin.y - radius, origin.z - radius, origin.x + radius, origin.y + radius, origin.z + radius);
	}

	public SphereShape(double x, double y, double z, float radius) {
		this(new Vec3(x, y, z), radius);
	}

	@Override
	public boolean contains(double x, double y, double z) {
		return origin.distanceToSqr(x, y, z) < radius * radius;
	}

	@Override
	public Vec3 getCenter() {
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

	public double getRadius() {
		return radius;
	}

}
