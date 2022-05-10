package com.github.elenterius.biomancy.world.block.property;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.context.BlockPlaceContext;
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

	Orientation(String name, Direction.Axis x) {
		this.name = name;
		axis = x;
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
