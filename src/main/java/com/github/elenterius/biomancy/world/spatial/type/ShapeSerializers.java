package com.github.elenterius.biomancy.world.spatial.type;

import com.github.elenterius.biomancy.util.serialization.NBTSerializer;
import com.github.elenterius.biomancy.world.MobSpawnFilterShape;
import com.github.elenterius.biomancy.world.mound.MoundShape;
import com.github.elenterius.biomancy.world.spatial.geometry.CuboidShape;
import com.github.elenterius.biomancy.world.spatial.geometry.OctantEllipsoidShape;
import com.github.elenterius.biomancy.world.spatial.geometry.Shape;
import com.github.elenterius.biomancy.world.spatial.geometry.SphereShape;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public final class ShapeSerializers {
	private static final Map<String, NBTSerializer<Shape>> SERIALIZERS = new HashMap<>();
	public static final NBTSerializer<Shape> CUBOID_SERIALIZER = register("cuboid", CuboidShape.Serializer::new);
	public static final NBTSerializer<Shape> SPHERE_SERIALIZER = register("sphere", SphereShape.Serializer::new);
	public static final NBTSerializer<Shape> OCTANT_ELLIPSOID_SERIALIZER = register("octant_ellipsoid", OctantEllipsoidShape.Serializer::new);
	public static final NBTSerializer<Shape> MOUND_SERIALIZER = register("mound", MoundShape.Serializer::new);
	public static final NBTSerializer<Shape> MOB_SPAWN_FILTER_SERIALIZER = register("mob_spawn_filter", MobSpawnFilterShape.Serializer::new);

	private ShapeSerializers() {}

	public static <T extends Shape> NBTSerializer<Shape> register(String id, Factory<T> factory) {
		NBTSerializer<Shape> serializer = cast(factory.create(id));
		SERIALIZERS.put(id, serializer);
		return serializer;
	}

	public static @Nullable NBTSerializer<Shape> get(String id) {
		return SERIALIZERS.get(id);
	}

	private static NBTSerializer<Shape> cast(NBTSerializer<? extends Shape> serializer) {
		//noinspection unchecked
		return (NBTSerializer<Shape>) serializer;
	}

	@FunctionalInterface
	public interface Factory<T extends Shape> {
		NBTSerializer<T> create(String id);
	}
}
