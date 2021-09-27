package com.github.elenterius.biomancy.util;

import net.minecraft.block.Block;
import net.minecraft.util.Direction;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;

public final class VoxelShapeUtil {
	private VoxelShapeUtil() {}

	/**
	 * this assumes that direction up is the default voxel shape
	 */
	public static VoxelShape createXZRotatedTowards(Direction dir, double x1, double y1, double z1, double x2, double y2, double z2) {
		switch (dir) {
			case UP:
			default:
				return Block.box(x1, y1, z1, x2, y2, z2);
			case DOWN:
				return rotateXAxis(Rotation.ROT180, x1, y1, z1, x2, y2, z2);
			case NORTH:
				return rotateXAxis(Rotation.ROT270, x1, y1, z1, x2, y2, z2);
			case SOUTH:
				return rotateXAxis(Rotation.ROT90, x1, y1, z1, x2, y2, z2);
			case WEST:
				return rotateZAxis(Rotation.ROT90, x1, y1, z1, x2, y2, z2);
			case EAST:
				return rotateZAxis(Rotation.ROT270, x1, y1, z1, x2, y2, z2);
		}
	}

	public static VoxelShape createRotated(Rotation rot, Direction.Axis axis, double x1, double y1, double z1, double x2, double y2, double z2) {
		switch (axis) {
			case X:
				return rotateXAxis(rot, x1, y1, z1, x2, y2, z2);
			case Y:
				return rotateYAxis(rot, x1, y1, z1, x2, y2, z2);
			case Z:
				return rotateZAxis(rot, x1, y1, z1, x2, y2, z2);
		}
		return Block.box(x1, y1, z1, x2, y2, z2);
	}

	public static VoxelShape rotateXAxis(Rotation rot, double x1, double y1, double z1, double x2, double y2, double z2) {
		switch (rot) {
			case ROT0:
			default:
				return Block.box(x1, y1, z1, x2, y2, z2);
			case ROT90:
				return Block.box(x1, 16 - z1, y1, x2, 16 - z2, y2);
			case ROT180:
				return Block.box(x1, 16 - y1, z1, x2, 16 - y2, z2);
			case ROT270:
				return Block.box(x1, z1, 16 - y1, x2, z2, 16 - y2);
		}
	}

	public static VoxelShape rotateYAxis(Rotation rot, double x1, double y1, double z1, double x2, double y2, double z2) {
		switch (rot) {
			case ROT0:
			default:
				return Block.box(x1, y1, z1, x2, y2, z2);
			case ROT90:
				return Block.box(16 - z2, y1, x1, 16 - z1, y2, x2);
			case ROT180:
				return Block.box(16 - x2, y1, 16 - z2, 16 - x1, y2, 16 - z1);
			case ROT270:
				return Block.box(z1, y1, 16 - x2, z2, y2, 16 - x1);
		}
		//return Block.makeCuboidShape(x1 * rot.cos + z1 * rot.sin, y1, z1 * rot.cos - x1 * rot.sin, x2 * rot.cos + z2 * rot.sin, y2, z2 * rot.cos - x2 * rot.sin);
	}

	public static VoxelShape rotateZAxis(Rotation rot, double x1, double y1, double z1, double x2, double y2, double z2) {
		switch (rot) {
			case ROT0:
			default:
				return Block.box(x1, y1, z1, x2, y2, z2);
			case ROT90:
				return Block.box(16 - y1, x1, z1, 16 - y2, x2, z2);
			case ROT180:
				return Block.box(x1 * -1, 16 - y1, z1, x2 * -1, 16 - y2, z2);
			case ROT270:
				return Block.box(y1, 16 - x1, z1, y2, 16 - x2, z2);
		}
	}

	public static AxisAlignedBB createUnitAABB(double x1, double y1, double z1, double x2, double y2, double z2) {
		return new AxisAlignedBB(x1 / 16d, y1 / 16d, z1 / 16d, x2 / 16d, y2 / 16d, z2 / 16d);
	}

	public static VoxelShape rotateYTo(Direction facing, AxisAlignedBB unitAABB) {
		switch (facing) {
			case NORTH:
				return VoxelShapes.create(unitAABB);
			case SOUTH:
				return VoxelShapes.box(1d - unitAABB.maxX, unitAABB.minY, 1d - unitAABB.maxZ, 1d - unitAABB.minX, unitAABB.maxY, 1d - unitAABB.minZ);
			case EAST:
				return VoxelShapes.box(1d - unitAABB.maxZ, unitAABB.minY, unitAABB.minX, 1d - unitAABB.minZ, unitAABB.maxY, unitAABB.maxX);
			case WEST:
				return VoxelShapes.box(unitAABB.minZ, unitAABB.minY, 1d - unitAABB.maxX, unitAABB.maxZ, unitAABB.maxY, 1d - unitAABB.minX);
		}
		return VoxelShapes.create(unitAABB);
	}

	public enum Rotation {
		ROT0, ROT90, ROT180, ROT270;
	}
}
