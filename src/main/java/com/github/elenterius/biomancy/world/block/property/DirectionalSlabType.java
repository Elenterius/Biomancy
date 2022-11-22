package com.github.elenterius.biomancy.world.block.property;

import com.mojang.math.OctahedralGroup;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;

public enum DirectionalSlabType implements StringRepresentable {
	HALF_NORTH("half_north", Direction.NORTH),
	HALF_SOUTH("half_south", Direction.SOUTH),
	HALF_WEST("half_west", Direction.WEST),
	HALF_EAST("half_east", Direction.EAST),
	HALF_UP("half_up", Direction.UP),
	HALF_DOWN("half_down", Direction.DOWN),
	FULL("full", Direction.UP);


	private final String name;
	private final Direction facing;

	DirectionalSlabType(String name, Direction facing) {
		this.name = name;
		this.facing = facing;
	}

	public static DirectionalSlabType getHalfFrom(Direction facing) {
		return switch (facing) {
			case DOWN -> HALF_DOWN;
			case UP -> HALF_UP;
			case NORTH -> HALF_NORTH;
			case SOUTH -> HALF_SOUTH;
			case WEST -> HALF_WEST;
			case EAST -> HALF_EAST;
		};
	}

	public static DirectionalSlabType getHalfFrom(BlockPos clickedPos, Vec3 clickLocation, Direction clickedFace) {
		Vec3 relativeClickPos = clickLocation.subtract(Vec3.atLowerCornerOf(clickedPos));

		if (clickedFace.getAxis() == Direction.Axis.Y) {
			float x = (float) relativeClickPos.x;
			float z = (float) relativeClickPos.z;

			Sector clickedSector = getClickedSector(x, z);
			return switch (clickedSector) {
				case MIDDLE -> clickedFace == Direction.UP ? HALF_UP : HALF_DOWN;
				case NORTH -> HALF_SOUTH;
				case EAST -> HALF_WEST;
				case SOUTH -> HALF_NORTH;
				case WEST -> HALF_EAST;
			};
		}

		if (clickedFace.getAxis() == Direction.Axis.X) {
			float x = (float) relativeClickPos.z;
			float y = (float) relativeClickPos.y;

			Sector clickedSector = getClickedSector(x, y);
			return switch (clickedSector) {
				case MIDDLE -> clickedFace == Direction.WEST ? HALF_WEST : HALF_EAST;
				case NORTH -> HALF_UP;
				case SOUTH -> HALF_DOWN;
				case EAST -> HALF_NORTH;
				case WEST -> HALF_SOUTH;
			};
		}

		if (clickedFace.getAxis() == Direction.Axis.Z) {
			float z = (float) relativeClickPos.x;
			float y = (float) relativeClickPos.y;

			Sector clickedSector = getClickedSector(z, y);
			return switch (clickedSector) {
				case MIDDLE -> clickedFace == Direction.SOUTH ? HALF_SOUTH : HALF_NORTH;
				case NORTH -> HALF_UP;
				case SOUTH -> HALF_DOWN;
				case EAST -> HALF_WEST;
				case WEST -> HALF_EAST;
			};
		}

		return getHalfFrom(clickedFace);
	}

	private static boolean isMiddleSector(float x, float y) {
		double max = 2d / 3d;
		double min = 1d / 3d;
		return x <= max && x > min && y <= max && y > min;
	}

	private static Sector getClickedSector(float x, float y) {
		if (isMiddleSector(x, y)) return Sector.MIDDLE;

		Vec2 point = new Vec2(x, y);

		Vec2 middle = new Vec2(0.5f, 0.5f);
		Vec2 a = new Vec2(0, 0); //west-north
		Vec2 b = new Vec2(1, 0); //north-east
		Vec2 c = new Vec2(1, 1); //east-south
		Vec2 d = new Vec2(0, 1); //west-south

		if (isInsideTriangle(middle, a, b, point)) return Sector.NORTH;
		if (isInsideTriangle(middle, b, c, point)) return Sector.EAST;
		if (isInsideTriangle(middle, c, d, point)) return Sector.SOUTH;
		if (isInsideTriangle(middle, d, a, point)) return Sector.WEST;

		return Sector.MIDDLE;
	}

	private static float triangleArea(Vec2 a, Vec2 b, Vec2 c) {
		return 0.5f * Math.abs(a.x * (b.y - c.y) + b.x * (c.y - a.y) + c.x * (a.y - b.y));
	}

	private static boolean isInsideTriangle(Vec2 a, Vec2 b, Vec2 c, Vec2 p) {
		float areaABC = triangleArea(a, b, c);
		float areaPBC = triangleArea(p, b, c);
		float areaAPC = triangleArea(a, p, c);
		float areaABP = triangleArea(a, b, p);
		return areaABC == areaPBC + areaAPC + areaABP;
	}

	public DirectionalSlabType rotate(Rotation rotation) {
		if (facing.getAxis() == Direction.Axis.Y) {
			return this;
		}

		//oriented towards x or z, rotation happens around the y-axis
		return switch (rotation) {
			case CLOCKWISE_90 -> getClockWise();
			case CLOCKWISE_180 -> getHalfFrom(facing.getOpposite());
			case COUNTERCLOCKWISE_90 -> getCounterClockWise();
			default -> this;
		};
	}

	public DirectionalSlabType getClockWise() {
		return switch (this.facing) {
			case NORTH -> getHalfFrom(Direction.EAST);
			case SOUTH -> getHalfFrom(Direction.WEST);
			case WEST -> getHalfFrom(Direction.NORTH);
			case EAST -> getHalfFrom(Direction.SOUTH);
			default -> this;
		};
	}

	public DirectionalSlabType getCounterClockWise() {
		return switch (this.facing) {
			case NORTH -> getHalfFrom(Direction.WEST);
			case SOUTH -> getHalfFrom(Direction.EAST);
			case WEST -> getHalfFrom(Direction.SOUTH);
			case EAST -> getHalfFrom(Direction.NORTH);
			default -> this;
		};
	}

	public DirectionalSlabType mirror(Mirror mirror) {
		OctahedralGroup rotation = mirror.rotation();
		if (rotation == OctahedralGroup.INVERT_X && facing.getAxis() == Direction.Axis.X) return getHalfFrom(facing.getOpposite());
		if (rotation == OctahedralGroup.INVERT_Z && facing.getAxis() == Direction.Axis.Z) return getHalfFrom(facing.getOpposite());
		if (rotation == OctahedralGroup.INVERT_Y && facing.getAxis() == Direction.Axis.Y) return getHalfFrom(facing.getOpposite());
		return this;
	}

	@Override
	public String toString() {
		return name;
	}

	public String getSerializedName() {
		return name;
	}

	public Direction getFacing() {
		return facing;
	}

	private enum Sector {
		MIDDLE, NORTH, EAST, SOUTH, WEST
	}

}
