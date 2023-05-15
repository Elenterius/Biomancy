package com.github.elenterius.biomancy.util;

import net.minecraft.core.Direction;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public final class VoxelShapeUtil {

	private VoxelShapeUtil() {}

	/**
	 * this assumes that direction up is the default voxel shape
	 */
	public static VoxelShape createXZRotatedTowards(Direction dir, double x1, double y1, double z1, double x2, double y2, double z2) {
		return switch (dir) {
			case DOWN -> rotateXAxis(Rotation.ROT180, x1, y1, z1, x2, y2, z2);
			case NORTH -> rotateXAxis(Rotation.ROT270, x1, y1, z1, x2, y2, z2);
			case SOUTH -> rotateXAxis(Rotation.ROT90, x1, y1, z1, x2, y2, z2);
			case WEST -> rotateZAxis(Rotation.ROT90, x1, y1, z1, x2, y2, z2);
			case EAST -> rotateZAxis(Rotation.ROT270, x1, y1, z1, x2, y2, z2);
			default -> box(x1, y1, z1, x2, y2, z2);
		};
	}

	public static VoxelShape createYRotatedTowards(Direction dir, double x1, double y1, double z1, double x2, double y2, double z2) {
		return switch (dir) {
			case SOUTH -> rotateYAxis(Rotation.ROT180, x1, y1, z1, x2, y2, z2);
			case WEST -> rotateYAxis(Rotation.ROT270, x1, y1, z1, x2, y2, z2);
			case EAST -> rotateYAxis(Rotation.ROT90, x1, y1, z1, x2, y2, z2);
			default -> box(x1, y1, z1, x2, y2, z2);
		};
	}

	public static VoxelShape createRotated(Rotation rot, Direction.Axis axis, double x1, double y1, double z1, double x2, double y2, double z2) {
		return switch (axis) {
			case X -> rotateXAxis(rot, x1, y1, z1, x2, y2, z2);
			case Y -> rotateYAxis(rot, x1, y1, z1, x2, y2, z2);
			case Z -> rotateZAxis(rot, x1, y1, z1, x2, y2, z2);
		};
	}

	public static VoxelShape rotateXAxis(Rotation rot, double x1, double y1, double z1, double x2, double y2, double z2) {
		return switch (rot) {
			case ROT90 -> box(x1, 16 - z1, y1, x2, 16 - z2, y2);
			case ROT180 -> box(x1, 16 - y1, z1, x2, 16 - y2, z2);
			case ROT270 -> box(x1, z1, 16 - y1, x2, z2, 16 - y2);
			default -> box(x1, y1, z1, x2, y2, z2);
		};
	}

	public static VoxelShape rotateYAxis(Rotation rot, double x1, double y1, double z1, double x2, double y2, double z2) {
		return switch (rot) {
			case ROT90 -> box(16 - z2, y1, x1, 16 - z1, y2, x2);
			case ROT180 -> box(16 - x2, y1, 16 - z2, 16 - x1, y2, 16 - z1);
			case ROT270 -> box(z1, y1, 16 - x2, z2, y2, 16 - x1);
			default -> box(x1, y1, z1, x2, y2, z2);
		};
		//return Block.makeCuboidShape(x1 * rot.cos + z1 * rot.sin, y1, z1 * rot.cos - x1 * rot.sin, x2 * rot.cos + z2 * rot.sin, y2, z2 * rot.cos - x2 * rot.sin);
	}

	public static VoxelShape rotateZAxis(Rotation rot, double x1, double y1, double z1, double x2, double y2, double z2) {
		return switch (rot) {
			case ROT90 -> box(16 - y1, x1, z1, 16 - y2, x2, z2);
			case ROT180 -> box(x1 * -1, 16 - y1, z1, x2 * -1, 16 - y2, z2);
			case ROT270 -> box(y1, 16 - x1, z1, y2, 16 - x2, z2);
			default -> box(x1, y1, z1, x2, y2, z2);
		};
	}

	public static VoxelShape box(double x1, double y1, double z1, double x2, double y2, double z2) {
		return Shapes.create(unitAABB(x1, y1, z1, x2, y2, z2));
	}

	public static AABB unitAABB(double x1, double y1, double z1, double x2, double y2, double z2) {
		return new AABB(x1 / 16d, y1 / 16d, z1 / 16d, x2 / 16d, y2 / 16d, z2 / 16d);
	}

	public static VoxelShape rotateYTo(Direction facing, AABB unitAABB) {
		return switch (facing) {
			case SOUTH -> Shapes.box(1d - unitAABB.maxX, unitAABB.minY, 1d - unitAABB.maxZ, 1d - unitAABB.minX, unitAABB.maxY, 1d - unitAABB.minZ);
			case EAST -> Shapes.box(1d - unitAABB.maxZ, unitAABB.minY, unitAABB.minX, 1d - unitAABB.minZ, unitAABB.maxY, unitAABB.maxX);
			case WEST -> Shapes.box(unitAABB.minZ, unitAABB.minY, 1d - unitAABB.maxX, unitAABB.maxZ, unitAABB.maxY, 1d - unitAABB.minX);
			default -> Shapes.create(unitAABB);
		};
	}

	public enum Rotation {
		ROT0, ROT90, ROT180, ROT270;
	}

}
