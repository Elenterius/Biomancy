package com.github.elenterius.biomancy.util.shape;

import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public interface Shape {

	boolean contains(double x, double y, double z);

	Vec3 getCenter();

	double distanceToSqr(double x, double y, double z);

	AABB getAABB();

	interface Sphere extends Shape {
		double getRadius();
	}
}
