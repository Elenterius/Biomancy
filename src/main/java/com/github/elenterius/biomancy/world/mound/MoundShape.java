package com.github.elenterius.biomancy.world.mound;

import com.github.elenterius.biomancy.util.serialization.NBTSerializer;
import com.github.elenterius.biomancy.world.MobSpawnFilter;
import com.github.elenterius.biomancy.world.spatial.geometry.Shape;
import com.github.elenterius.biomancy.world.spatial.geometry.ShapeHierarchy;
import com.github.elenterius.biomancy.world.spatial.type.ShapeSerializers;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class MoundShape implements Shape, MobSpawnFilter {
	private final ProcGenValues procGenValues;
	BlockPos origin;
	ShapeHierarchy<Shape> boundingShapes;
	ShapeHierarchy<MoundChamber> chamberShapes;

	MoundShape(BlockPos origin, List<Shape> boundingShapes, List<MoundChamber> chamberShapes, ProcGenValues procGenValues) {
		this.boundingShapes = new ShapeHierarchy<>(boundingShapes);
		this.chamberShapes = new ShapeHierarchy<>(chamberShapes);
		this.origin = origin;
		this.procGenValues = procGenValues;
	}

	public BlockPos getOrigin() {
		return origin;
	}

	@Override
	public boolean contains(double x, double y, double z) {
		return boundingShapes.contains(x, y, z);
	}

	@Override
	public boolean intersectsCuboid(double minX, double minY, double minZ, double maxX, double maxY, double maxZ) {
		return boundingShapes.intersectsCuboid(minX, minY, minZ, maxX, maxY, maxZ);
	}

	@Override
	public Vec3 center() {
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

	public boolean hasChamberAt(BlockPos pos) {
		return chamberShapes.contains(pos.getX() + 0.5d, pos.getY() + 0.5d, pos.getZ() + 0.5d);
	}

	@Nullable
	public MoundChamber getChamberAt(BlockPos pos) {
		return getChamberAt(pos.getX() + 0.5d, pos.getY() + 0.5d, pos.getZ() + 0.5d);
	}

	@Nullable
	public MoundChamber getChamberAt(double x, double y, double z) {
		return chamberShapes.getClosestShapeContaining(x, y, z);
	}

	@Nullable
	public List<MoundChamber> getChambersAt(BlockPos pos) {
		return getChambersAt(pos.getX() + 0.5d, pos.getY() + 0.5d, pos.getZ() + 0.5d);
	}

	@Nullable
	public List<MoundChamber> getChambersAt(double x, double y, double z) {
		return chamberShapes.getShapesContaining(x, y, z);
	}

	@Nullable
	public Shape getBoundingShapeAt(BlockPos pos) {
		return getBoundingShapeAt(pos.getX() + 0.5d, pos.getY() + 0.5d, pos.getZ() + 0.5d);
	}

	@Nullable
	public Shape getBoundingShapeAt(double x, double y, double z) {
		return boundingShapes.getClosestShapeContaining(x, y, z);
	}

	public ProcGenValues getProcGenValues() {
		return procGenValues;
	}

	@Override
	public NBTSerializer<Shape> getNBTSerializer() {
		return ShapeSerializers.MOUND_SERIALIZER;
	}

	@Override
	public boolean isMobAllowedToSpawn(Mob mob, MobSpawnType spawnReason, LevelAccessor level, double x, double y, double z) {
		return false;
	}

	public record ProcGenValues(long seed, byte extraRadius, byte extraHeight, byte subSpires, int maxBuildHeight, int seaLevel, float biomeTemperature, float biomeHumidity) {

		public float heightMultiplier() {
			return extraHeight / 100f;
		}

		public float radiusMultiplier() {
			return extraRadius / 100f;
		}

		public void writeTo(CompoundTag tag) {
			tag.putLong("Seed", seed);

			tag.putByte("Radius", extraRadius);
			tag.putByte("Height", extraHeight);
			tag.putByte("SubSpires", subSpires);

			tag.putInt("MaxBuildHeight", maxBuildHeight);
			tag.putInt("SeaLevel", seaLevel);
			tag.putFloat("BiomeTemperature", biomeTemperature);
			tag.putFloat("BiomeHumidity", biomeHumidity);
		}

		public static ProcGenValues readFrom(CompoundTag tag) {
			long seed = tag.getLong("Seed");

			byte extraRadius = tag.getByte("Radius");
			byte height = tag.getByte("Height");
			byte subSpires = tag.getByte("SubSpires");

			int maxBuildHeight = tag.getInt("MaxBuildHeight");
			int seaLevel = tag.getInt("SeaLevel");
			float biomeTemperature = tag.getFloat("BiomeTemperature");
			float biomeHumidity = tag.getFloat("BiomeHumidity");

			return new ProcGenValues(seed, extraRadius, height, subSpires, maxBuildHeight, seaLevel, biomeTemperature, biomeHumidity);
		}
	}

	public record Serializer(String id) implements NBTSerializer<MoundShape> {

		@Override
		public CompoundTag write(MoundShape shape) {
			CompoundTag tag = new CompoundTag();
			tag.putLong("Origin", shape.origin.asLong());
			shape.procGenValues.writeTo(tag);
			return tag;
		}

		@Override
		public MoundShape read(CompoundTag tag) {
			BlockPos origin = BlockPos.of(tag.getLong("Origin"));
			ProcGenValues procGenValues = ProcGenValues.readFrom(tag);
			return MoundGenerator.constructShape(origin, procGenValues);
		}
	}
}


