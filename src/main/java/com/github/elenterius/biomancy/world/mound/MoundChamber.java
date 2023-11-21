package com.github.elenterius.biomancy.world.mound;

import com.github.elenterius.biomancy.util.shape.Shape;
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
}
