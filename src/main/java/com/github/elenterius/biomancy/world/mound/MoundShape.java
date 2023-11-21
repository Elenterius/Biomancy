package com.github.elenterius.biomancy.world.mound;

import com.github.elenterius.biomancy.util.shape.Shape;
import com.github.elenterius.biomancy.util.shape.ShapeMap;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class MoundShape implements Shape {
	BlockPos origin;
	ShapeMap<Shape> boundingShapes;
	ShapeMap<MoundChamber> chamberShapes;

	MoundShape(BlockPos origin, List<Shape> boundingShapes, List<MoundChamber> chamberShapes) {
		this.boundingShapes = new ShapeMap<>(boundingShapes);
		this.chamberShapes = new ShapeMap<>(chamberShapes);
		this.origin = origin;
	}

	public BlockPos getOrigin() {
		return origin;
	}

	@Override
	public boolean contains(double x, double y, double z) {
		return boundingShapes.contains(x, y, z);
	}

	@Override
	public Vec3 getCenter() {
		return boundingShapes.getCenter();
	}

	@Override
	public double distanceToSqr(double x, double y, double z) {
		return boundingShapes.distanceToSqr(x, y, z);
	}

	@Override
	public AABB getAABB() {
		return boundingShapes.getAABB();
	}

	@Nullable
	public MoundChamber getChamberAt(int x, int y, int z) {
		return chamberShapes.getClosestShapeContaining(x, y, z);
	}

	@Nullable
	public Shape getBoundingShapeAt(int x, int y, int z) {
		return boundingShapes.getClosestShapeContaining(x, y, z);
	}

}


