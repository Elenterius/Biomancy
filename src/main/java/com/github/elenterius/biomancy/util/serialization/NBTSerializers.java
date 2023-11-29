package com.github.elenterius.biomancy.util.serialization;

import com.github.elenterius.biomancy.util.shape.OctantEllipsoidShape;
import com.github.elenterius.biomancy.util.shape.Shape;
import com.github.elenterius.biomancy.util.shape.SphereShape;
import com.github.elenterius.biomancy.world.Region;
import com.github.elenterius.biomancy.world.ShapeRegion;
import com.github.elenterius.biomancy.world.mound.MoundShape;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public final class NBTSerializers {
	private static final Map<String, NBTSerializer<?>> SERIALIZERS = new HashMap<>();
	public static final NBTSerializer<Shape> SPHERE_SERIALIZER = registerShape("sphere", SphereShape.Serializer::new);
	public static final NBTSerializer<Shape> OCTANT_ELLIPSOID_SERIALIZER = registerShape("octant_ellipsoid", OctantEllipsoidShape.Serializer::new);
	public static final NBTSerializer<Shape> MOUND_SERIALIZER = registerShape("mound", MoundShape.Serializer::new);
	public static final NBTSerializer<Region> SHAPE_REGION_SERIALIZER = registerRegion("shape_region", ShapeRegion.Serializer::new);

	private NBTSerializers() {}

	public static <T> NBTSerializer<T> register(String id, NBTSerializer.Factory<T> factory) {
		NBTSerializer<T> serializer = factory.create(id);
		SERIALIZERS.put(id, serializer);
		return serializer;
	}

	public static <T extends Shape> NBTSerializer<Shape> registerShape(String id, NBTSerializer.Factory<T> factory) {
		NBTSerializer<T> serializer = factory.create(id);
		SERIALIZERS.put(id, serializer);
		//noinspection unchecked
		return (NBTSerializer<Shape>) serializer;
	}

	public static <T extends Region> NBTSerializer<Region> registerRegion(String id, NBTSerializer.Factory<T> factory) {
		NBTSerializer<T> serializer = factory.create(id);
		SERIALIZERS.put(id, serializer);
		//noinspection unchecked
		return (NBTSerializer<Region>) serializer;
	}

	public static @Nullable NBTSerializer<?> get(String id) {
		return SERIALIZERS.get(id);
	}

}
