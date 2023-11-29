package com.github.elenterius.biomancy.world;

import com.github.elenterius.biomancy.util.serialization.NBTSerializable;
import com.github.elenterius.biomancy.util.serialization.NBTSerializer;
import com.github.elenterius.biomancy.util.serialization.NBTSerializers;
import com.github.elenterius.biomancy.util.shape.Shape;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;

public class ShapeRegion extends Region implements NBTSerializable<Region> {

	private final Shape shape;
	private final SABB sabb;

	protected ShapeRegion(BlockPos origin, Shape shape) {
		super(origin);
		this.shape = shape;
		sabb = SABB.from(shape.getAABB());
	}

	@Override
	public SABB getSABB() {
		return sabb;
	}

	@Override
	boolean contains(double x, double y, double z) {
		return shape.contains(x, y, z);
	}

	@Override
	boolean contains(int x, int y, int z) {
		return shape.contains(x, y, z);
	}

	@Override
	double distanceToSqr(double x, double y, double z) {
		return shape.distanceToSqr(x, y, z);
	}

	public Shape getShape() {
		return shape;
	}

	@Override
	public NBTSerializer<Region> getNBTSerializer() {
		return NBTSerializers.SHAPE_REGION_SERIALIZER;
	}

	public record Serializer(String id) implements NBTSerializer<ShapeRegion> {

		@Override
		public CompoundTag serializeNBT(ShapeRegion region) {
			CompoundTag tag = new CompoundTag();
			tag.putLong("Id", region.origin.asLong());

			NBTSerializer<Shape> serializer = region.shape.getNBTSerializer();
			CompoundTag serialized = serializer.serializeNBT(region.shape);
			serialized.putString("Serializer", serializer.id());

			tag.put("Shape", serialized);
			return tag;
		}

		@Override
		public ShapeRegion deserializeNBT(CompoundTag tag) {
			long origin = tag.getLong("Id");

			CompoundTag serialized = tag.getCompound("Shape");
			String serializerId = serialized.getString("Serializer");
			NBTSerializer<?> nbtSerializer = NBTSerializers.get(serializerId);
			if (nbtSerializer != null) {
				Object o = nbtSerializer.deserializeNBT(serialized);
				if (o instanceof Shape shape) {
					return new ShapeRegion(BlockPos.of(origin), shape);
				}
			}

			return new ShapeRegion(BlockPos.of(origin), Shape.EMPTY);
		}
	}
}
