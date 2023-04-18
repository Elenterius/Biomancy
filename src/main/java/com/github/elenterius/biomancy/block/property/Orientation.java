package com.github.elenterius.biomancy.block.property;

import com.mojang.math.OctahedralGroup;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.phys.Vec3;

public enum Orientation implements StringRepresentable {
	X_POSITIVE("x_positive", Direction.Axis.X),
	X_MIDDLE("x_middle", Direction.Axis.X),
	X_NEGATIVE("x_negative", Direction.Axis.X),
	Z_POSITIVE("z_positive", Direction.Axis.Z),
	Z_MIDDLE("z_middle", Direction.Axis.Z),
	Z_NEGATIVE("z_negative", Direction.Axis.Z),
	Y_POSITIVE("y_positive", Direction.Axis.Y),
	Y_MIDDLE("y_middle", Direction.Axis.Y),
	Y_NEGATIVE("y_negative", Direction.Axis.Y);

	private final String name;
	public final Direction.Axis axis;

	Orientation(String name, Direction.Axis axis) {
		this.name = name;
		this.axis = axis;
	}

	public boolean isMiddle() {
		return this == X_MIDDLE || this == Z_MIDDLE || this == Y_MIDDLE;
	}

	public boolean isPositive() {
		return this == X_POSITIVE || this == Z_POSITIVE || this == Y_POSITIVE;
	}

	public boolean isNegative() {
		return this == X_NEGATIVE || this == Z_NEGATIVE || this == Y_NEGATIVE;
	}

	public Orientation rotate(Rotation rotation) {
		if (axis == Direction.Axis.Y) {
			return this;
		}

		//oriented towards x or z, rotation happens around the y-axis
		return switch (rotation) {
			case CLOCKWISE_90 -> getClockWise();
			case CLOCKWISE_180 -> getOpposite();
			case COUNTERCLOCKWISE_90 -> getCounterClockWise();
			default -> this;
		};
	}

	public Orientation mirror(Mirror mirror) {
		OctahedralGroup rotation = mirror.rotation();
		if (rotation == OctahedralGroup.INVERT_X && axis == Direction.Axis.X) return getOpposite();
		if (rotation == OctahedralGroup.INVERT_Z && axis == Direction.Axis.Z) return getOpposite();
		if (rotation == OctahedralGroup.INVERT_Y && axis == Direction.Axis.Y) return getOpposite();
		return this;
	}

	public Orientation getClockWise() {
		return switch (this) {
			case Z_NEGATIVE -> X_POSITIVE;
			case Z_POSITIVE -> X_NEGATIVE;
			case X_NEGATIVE -> Z_NEGATIVE;
			case X_POSITIVE -> Z_POSITIVE;
			case X_MIDDLE -> Z_MIDDLE;
			case Z_MIDDLE -> X_MIDDLE;
			default -> this;
		};
	}

	public Orientation getCounterClockWise() {
		return switch (this) {
			case Z_NEGATIVE -> X_NEGATIVE;
			case Z_POSITIVE -> X_POSITIVE;
			case X_NEGATIVE -> Z_POSITIVE;
			case X_POSITIVE -> Z_NEGATIVE;
			case X_MIDDLE -> Z_MIDDLE;
			case Z_MIDDLE -> X_MIDDLE;
			default -> this;
		};
	}

	public Orientation getOpposite() {
		return switch (this) {
			case Z_NEGATIVE -> Z_POSITIVE;
			case Z_POSITIVE -> Z_NEGATIVE;
			case X_NEGATIVE -> X_POSITIVE;
			case X_POSITIVE -> X_NEGATIVE;
			case Y_POSITIVE -> Y_NEGATIVE;
			case Y_NEGATIVE -> Y_POSITIVE;
			default -> this;
		};
	}

	public static Orientation getOrientationFrom(BlockPlaceContext context) {
		Direction.Axis axis = context.getClickedFace().getAxis();
		if (axis.isHorizontal()) return getYOrientationFrom(context.getClickedPos(), context.getClickLocation());
		if (context.getHorizontalDirection().getAxis() == Direction.Axis.X) return getXOrientationFrom(context.getClickedPos(), context.getClickLocation());
		if (context.getHorizontalDirection().getAxis() == Direction.Axis.Z) return getZOrientationFrom(context.getClickedPos(), context.getClickLocation());
		return Orientation.Y_MIDDLE;
	}

	public static Orientation getXZOrientationFrom(BlockPlaceContext context) {
		if (context.getHorizontalDirection().getAxis() == Direction.Axis.X) return getXOrientationFrom(context.getClickedPos(), context.getClickLocation());
		if (context.getHorizontalDirection().getAxis() == Direction.Axis.Z) return getZOrientationFrom(context.getClickedPos(), context.getClickLocation());
		return Orientation.Z_MIDDLE;
	}

	private static Orientation getZOrientationFrom(BlockPos clickPosition, Vec3 clickLocation) {
		double p = clickLocation.z - clickPosition.getZ();
		if (p > 2d / 3d) return Orientation.Z_POSITIVE;
		else if (p > 1d / 3d) return Orientation.Z_MIDDLE;
		return Orientation.Z_NEGATIVE;
	}

	private static Orientation getXOrientationFrom(BlockPos clickPosition, Vec3 clickLocation) {
		double p = clickLocation.x - clickPosition.getX();
		if (p > 2d / 3d) return Orientation.X_POSITIVE;
		else if (p > 1d / 3d) return Orientation.X_MIDDLE;
		return Orientation.X_NEGATIVE;
	}

	private static Orientation getYOrientationFrom(BlockPos clickPosition, Vec3 clickLocation) {
		double p = clickLocation.y - clickPosition.getY();
		if (p > 2d / 3d) return Orientation.Y_NEGATIVE;
		else if (p > 1d / 3d) return Orientation.Y_MIDDLE;
		return Orientation.Y_POSITIVE;
	}

	@Override
	public String getSerializedName() {
		return name;
	}

}
