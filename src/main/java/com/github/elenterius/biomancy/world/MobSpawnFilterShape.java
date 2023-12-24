package com.github.elenterius.biomancy.world;

import com.github.elenterius.biomancy.util.serialization.NBTSerializer;
import com.github.elenterius.biomancy.world.spatial.geometry.Shape;
import com.github.elenterius.biomancy.world.spatial.type.ShapeSerializers;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class MobSpawnFilterShape implements Shape, MobSpawnFilter {

	private final Shape shape;

	public MobSpawnFilterShape(Shape shape) {
		this.shape = shape;
	}

	@Override
	public boolean isMobAllowedToSpawn(Mob mob, MobSpawnType spawnReason, LevelAccessor level, double x, double y, double z) {
		return false;
	}

	@Override
	public boolean contains(double x, double y, double z) {
		return shape.contains(x, y, z);
	}

	@Override
	public boolean intersectsCuboid(double minX, double minY, double minZ, double maxX, double maxY, double maxZ) {
		return shape.intersectsCuboid(minX, minY, minZ, maxX, maxY, maxZ);
	}

	@Override
	public Vec3 center() {
		return shape.center();
	}

	@Override
	public double distanceToSqr(double x, double y, double z) {
		return shape.distanceToSqr(x, y, z);
	}

	@Override
	public AABB getAABB() {
		return shape.getAABB();
	}

	@Override
	public NBTSerializer<Shape> getNBTSerializer() {
		return ShapeSerializers.MOB_SPAWN_FILTER_SERIALIZER;
	}

	public record Serializer(String id) implements NBTSerializer<MobSpawnFilterShape> {
		@Override
		public CompoundTag write(MobSpawnFilterShape shape) {
			CompoundTag tag = new CompoundTag();
			tag.put("Shape", shape.shape.getNBTSerializer().write(shape.shape));
			return tag;
		}

		@Override
		public MobSpawnFilterShape read(CompoundTag tag) {
			Shape shape = EMPTY;

			CompoundTag shapeCompound = tag.getCompound("Shape");
			String serializerId = shapeCompound.getString("Serializer");
			NBTSerializer<Shape> serializer = ShapeSerializers.get(serializerId);
			if (serializer != null) {
				shape = serializer.read(shapeCompound);
			}

			return new MobSpawnFilterShape(shape);
		}
	}
}
