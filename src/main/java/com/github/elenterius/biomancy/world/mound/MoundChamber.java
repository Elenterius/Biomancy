package com.github.elenterius.biomancy.world.mound;

import com.github.elenterius.biomancy.util.serialization.NBTSerializer;
import com.github.elenterius.biomancy.world.mound.decorator.ChamberDecorator;
import com.github.elenterius.biomancy.world.mound.decorator.ChamberDecorators;
import com.github.elenterius.biomancy.world.spatial.geometry.Shape;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class MoundChamber implements Chamber {
	private final Shape shape;
	private final Vec3 origin;
	private int seed = 1337;
	private ChamberDecorator chamberDecorator = ChamberDecorators.EMPTY;

	public MoundChamber(Vec3 origin, Shape shape) {
		this.shape = shape;
		this.origin = origin;
	}

	public MoundChamber(Shape shape) {
		this.shape = shape;
		origin = shape.center();
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
	public Vec3 center() {
		return shape.center();
	}

	@Override
	public int seed() {
		return seed;
	}

	public void setDecorator(ChamberDecorator chamberDecorator, int seed) {
		this.chamberDecorator = chamberDecorator;
		this.seed = seed;
	}

	@Override
	public ChamberDecorator getDecorator() {
		return chamberDecorator;
	}

	@Override
	public Vec3 origin() {
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
