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

	public record ProcGenValues(long seed, int maxBuildHeight, int seaLevel, float biomeTemperature, float biomeHumidity) {
		public void writeTo(CompoundTag tag) {
			tag.putLong("Seed", seed);
			tag.putInt("MaxBuildHeight", maxBuildHeight);
			tag.putInt("SeaLevel", seaLevel);
			tag.putFloat("BiomeTemperature", biomeTemperature);
			tag.putFloat("BiomeHumidity", biomeHumidity);
		}

		public static ProcGenValues readFrom(CompoundTag tag) {
			long seed = tag.getLong("Seed");
			int maxBuildHeight = tag.getInt("MaxBuildHeight");
			int seaLevel = tag.getInt("SeaLevel");
			float biomeTemperature = tag.getFloat("BiomeTemperature");
			float biomeHumidity = tag.getFloat("BiomeHumidity");
			return new ProcGenValues(seed, maxBuildHeight, seaLevel, biomeTemperature, biomeHumidity);
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


