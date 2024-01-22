package com.github.elenterius.biomancy.block.property;

import com.mojang.math.Axis;
import net.minecraft.core.Direction;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.block.Rotation;
import org.joml.Quaternionf;

import java.util.Locale;

public enum DirectedConnection implements StringRepresentable {
	NORTH_SOUTH(Direction.NORTH, Direction.SOUTH), NORTH_EAST(Direction.NORTH, Direction.EAST), NORTH_WEST(Direction.NORTH, Direction.WEST), NORTH_UP(Direction.NORTH, Direction.UP), NORTH_DOWN(Direction.NORTH, Direction.DOWN),
	SOUTH_NORTH(Direction.SOUTH, Direction.NORTH), SOUTH_EAST(Direction.SOUTH, Direction.EAST), SOUTH_WEST(Direction.SOUTH, Direction.WEST), SOUTH_UP(Direction.SOUTH, Direction.UP), SOUTH_DOWN(Direction.SOUTH, Direction.DOWN),
	EAST_NORTH(Direction.EAST, Direction.NORTH), EAST_SOUTH(Direction.EAST, Direction.SOUTH), EAST_WEST(Direction.EAST, Direction.WEST), EAST_UP(Direction.EAST, Direction.UP), EAST_DOWN(Direction.EAST, Direction.DOWN),
	WEST_NORTH(Direction.WEST, Direction.NORTH), WEST_SOUTH(Direction.WEST, Direction.SOUTH), WEST_EAST(Direction.WEST, Direction.EAST), WEST_UP(Direction.WEST, Direction.UP), WEST_DOWN(Direction.WEST, Direction.DOWN),
	UP_NORTH(Direction.UP, Direction.NORTH), UP_SOUTH(Direction.UP, Direction.SOUTH), UP_EAST(Direction.UP, Direction.EAST), UP_WEST(Direction.UP, Direction.WEST), UP_DOWN(Direction.UP, Direction.DOWN),
	DOWN_NORTH(Direction.DOWN, Direction.NORTH), DOWN_SOUTH(Direction.DOWN, Direction.SOUTH), DOWN_EAST(Direction.DOWN, Direction.EAST), DOWN_WEST(Direction.DOWN, Direction.WEST), DOWN_UP(Direction.DOWN, Direction.UP);

	static {
		for (DirectedConnection directedConnection : values()) {
			directedConnection.quaternion = computeRotation(directedConnection);
		}
	}

	public final Direction ingoing;
	public final Direction outgoing;
	private final boolean isStraight;
	private Quaternionf quaternion;

	DirectedConnection(Direction in, Direction out) {
		ingoing = in;
		outgoing = out;
		isStraight = in.getAxis() == out.getAxis();
	}

	private static Quaternionf computeRotation(DirectedConnection connection) {
		return switch (connection) {
			case NORTH_DOWN, NORTH_SOUTH -> Axis.XP.rotationDegrees(-90);
			case NORTH_EAST -> {
				Quaternionf quaternion = Axis.XP.rotationDegrees(-90);
				quaternion.mul(Axis.YP.rotationDegrees(-90));
				yield quaternion;
			}
			case NORTH_WEST -> {
				Quaternionf quaternion = Axis.XP.rotationDegrees(-90);
				quaternion.mul(Axis.YP.rotationDegrees(90));
				yield quaternion;
			}
			case NORTH_UP -> {
				Quaternionf quaternion = Axis.XP.rotationDegrees(-90);
				quaternion.mul(Axis.YP.rotationDegrees(-180));
				yield quaternion;
			}
			case SOUTH_UP, SOUTH_NORTH -> Axis.XP.rotationDegrees(90);
			case SOUTH_EAST -> {
				Quaternionf quaternion = Axis.XP.rotationDegrees(90);
				quaternion.mul(Axis.YP.rotationDegrees(-90));
				yield quaternion;
			}
			case SOUTH_WEST -> {
				Quaternionf quaternion = Axis.XP.rotationDegrees(90);
				quaternion.mul(Axis.YP.rotationDegrees(90));
				yield quaternion;
			}
			case SOUTH_DOWN -> {
				Quaternionf quaternion = Axis.XP.rotationDegrees(90);
				quaternion.mul(Axis.YP.rotationDegrees(-180));
				yield quaternion;
			}
			case EAST_WEST, EAST_NORTH -> Axis.ZP.rotationDegrees(-90);
			case EAST_SOUTH -> {
				Quaternionf quaternion = Axis.ZP.rotationDegrees(90);
				quaternion.mul(Axis.XP.rotationDegrees(-180));
				yield quaternion;
			}
			case EAST_UP -> {
				Quaternionf quaternion = Axis.ZP.rotationDegrees(-90);
				quaternion.mul(Axis.YP.rotationDegrees(90));
				yield quaternion;
			}
			case EAST_DOWN -> {
				Quaternionf quaternion = Axis.ZP.rotationDegrees(-90);
				quaternion.mul(Axis.YP.rotationDegrees(-90));
				yield quaternion;
			}
			case WEST_NORTH, WEST_EAST -> Axis.ZP.rotationDegrees(90);
			case WEST_SOUTH -> {
				Quaternionf quaternion = Axis.ZP.rotationDegrees(-90);
				quaternion.mul(Axis.XP.rotationDegrees(-180));
				yield quaternion;
			}
			case WEST_UP -> {
				Quaternionf quaternion = Axis.ZP.rotationDegrees(90);
				quaternion.mul(Axis.YP.rotationDegrees(-90));
				yield quaternion;
			}
			case WEST_DOWN -> {
				Quaternionf quaternion = Axis.ZP.rotationDegrees(90);
				quaternion.mul(Axis.YP.rotationDegrees(90));
				yield quaternion;
			}
			case UP_NORTH, UP_DOWN -> new Quaternionf();
			case UP_SOUTH -> Axis.YP.rotationDegrees(-180);
			case UP_EAST -> Axis.YP.rotationDegrees(-90);
			case UP_WEST -> Axis.YP.rotationDegrees(90);
			case DOWN_NORTH, DOWN_UP -> Axis.ZP.rotationDegrees(-180);
			case DOWN_SOUTH -> Axis.XP.rotationDegrees(-180);
			case DOWN_EAST -> {
				Quaternionf quaternion = Axis.ZP.rotationDegrees(-180);
				quaternion.mul(Axis.YP.rotationDegrees(90));
				yield quaternion;
			}
			case DOWN_WEST -> {
				Quaternionf quaternion = Axis.ZP.rotationDegrees(-180);
				quaternion.mul(Axis.YP.rotationDegrees(-90));
				yield quaternion;
			}
		};
	}

	public static DirectedConnection from(Direction ingoing, Direction outgoing) {
		for (DirectedConnection connection : values()) {
			if (connection.ingoing == ingoing && connection.outgoing == outgoing) return connection;
		}
		return UP_DOWN;
	}

	public boolean isStraight() {
		return isStraight;
	}

	public boolean isCorner() {
		return !isStraight;
	}

	/**
	 * do not modify
	 */
	public Quaternionf getUnsafeQuaternion() {
		return quaternion;
	}

	public Quaternionf getQuaternion() {
		return new Quaternionf(quaternion);
	}

	public DirectedConnection rotate(Rotation rotation) {
		return from(rotation.rotate(ingoing), outgoing);
	}

	private DirectedConnection inverse() {
		for (DirectedConnection connection : values()) {
			if (connection.ingoing == outgoing && connection.outgoing == ingoing) return connection;
		}
		return UP_DOWN;
	}

	@Override
	public String getSerializedName() {
		return name().toLowerCase(Locale.ENGLISH);
	}

}
