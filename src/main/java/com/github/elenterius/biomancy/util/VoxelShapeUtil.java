package com.github.elenterius.biomancy.util;

import net.minecraft.block.Block;
import net.minecraft.util.Direction;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;

public final class VoxelShapeUtil {
	private VoxelShapeUtil() {}

	public static VoxelShape createRotatedCuboidShape(Direction facing, double x1, double y1, double z1, double x2, double y2, double z2) {
		switch (facing) {
			case NORTH:
				return Block.makeCuboidShape(x1, y1, z1, x2, y2, z2);
			case SOUTH:
				return Block.makeCuboidShape(16 - x2, y1, 16 - z2, 16 - x1, y2, 16 - z1);
			case EAST:
				return Block.makeCuboidShape(16 - z2, y1, x1, 16 - z1, y2, x2);
			case WEST:
				return Block.makeCuboidShape(z1, y1, 16 - x2, z2, y2, 16 - x1);
		}
		return Block.makeCuboidShape(x1, y1, z1, x2, y2, z2);
	}

	public static AxisAlignedBB createUnitAABB(double x1, double y1, double z1, double x2, double y2, double z2) {
		return new AxisAlignedBB(x1 / 16d, y1 / 16d, z1 / 16d, x2 / 16d, y2 / 16d, z2 / 16d);
	}

	public static VoxelShape createWithFacing(Direction facing, AxisAlignedBB unitAABB) {
		switch (facing) {
			case NORTH:
				return VoxelShapes.create(unitAABB);
			case SOUTH:
				return VoxelShapes.create(1d - unitAABB.maxX, unitAABB.minY, 1d - unitAABB.maxZ, 1d - unitAABB.minX, unitAABB.maxY, 1d - unitAABB.minZ);
			case EAST:
				return VoxelShapes.create(1d - unitAABB.maxZ, unitAABB.minY, unitAABB.minX, 1d - unitAABB.minZ, unitAABB.maxY, unitAABB.maxX);
			case WEST:
				return VoxelShapes.create(unitAABB.minZ, unitAABB.minY, 1d - unitAABB.maxX, unitAABB.maxZ, unitAABB.maxY, 1d - unitAABB.minX);
		}
		return VoxelShapes.create(unitAABB);
	}
}
