package com.github.elenterius.biomancy.world.spatial;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.world.spatial.geometry.Shape;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.h2.mvstore.MVMap;
import org.h2.mvstore.rtree.MVRTreeMap;
import org.jetbrains.annotations.Nullable;

import java.util.function.Predicate;
import java.util.function.Supplier;

@Mod.EventBusSubscriber(modid = BiomancyMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class SpatialShapeManager {

	private SpatialShapeManager() {}

	@SubscribeEvent
	public static void onLevelUnload(WorldEvent.Unload event) {
		if (!event.getWorld().isClientSide() && event.getWorld() instanceof ServerLevel serverLevel && (serverLevel.dimension() == Level.OVERWORLD)) {
			SpatialShapeStorage storage = SpatialShapeStorage.getInstance(serverLevel);
			storage.close();
		}
	}

	private static String getLevelKey(ServerLevel level) {
		return level.dimension().location().toString();
	}

	public static Shape getOrCreateShape(ServerLevel level, BlockPos shapeId, Supplier<Shape> factory) {
		SpatialShapeStorage storage = SpatialShapeStorage.getInstance(level);
		return storage.getOrCreate(getLevelKey(level), shapeId.asLong(), factory);
	}

	public static void remove(ServerLevel level, BlockPos shapeId) {
		SpatialShapeStorage spatialStorage = SpatialShapeStorage.getInstance(level);
		spatialStorage.remove(getLevelKey(level), shapeId.asLong());
	}

	@Nullable
	public static Shape getClosestShape(ServerLevel level, BlockPos blockPos) {
		return getClosestShape(level, blockPos, shape -> true);
	}

	@Nullable
	public static Shape getClosestShape(ServerLevel level, BlockPos blockPos, Predicate<Shape> predicate) {
		SpatialQuery query = SpatialQuery.of(blockPos);
		SpatialShapeStorage spatialStorage = SpatialShapeStorage.getInstance(level);
		String levelKey = getLevelKey(level);

		final float x = Mth.lerp(0.5f, query.minX(), query.maxX());
		final float y = Mth.lerp(0.5f, query.minY(), query.maxY());
		final float z = Mth.lerp(0.5f, query.minZ(), query.maxZ());

		double minDistSqr = Double.MAX_VALUE;
		Shape closestShape = null;

		MVRTreeMap.RTreeCursor<Long> intersectingKeys = spatialStorage.findIntersecting(levelKey, query);
		MVMap<Long, Shape> shapes = spatialStorage.getShapes(levelKey);

		while (intersectingKeys.hasNext()) {
			long id = intersectingKeys.next().getId();
			Shape shape = shapes.get(id);
			if (shape != null && shape.contains(x, y, z) && predicate.test(shape)) {
				double distSqr = shape.distanceToSqr(x, y, z);
				if (distSqr < minDistSqr) {
					closestShape = shape;
					minDistSqr = distSqr;
				}
			}
		}

		return closestShape;
	}

	@Nullable
	public static Shape getAnyShape(ServerLevel level, Entity entity, QueryStrategy strategy, Predicate<Shape> predicate) {
		return getAnyShape(level, strategy, SpatialQuery.of(entity), predicate);
	}

	@Nullable
	public static Shape getAnyShape(ServerLevel level, QueryStrategy strategy, SpatialQuery query, Predicate<Shape> predicate) {
		SpatialShapeStorage spatialStorage = SpatialShapeStorage.getInstance(level);
		String levelKey = getLevelKey(level);

		MVRTreeMap.RTreeCursor<Long> foundKeys = strategy.find(levelKey, query, spatialStorage);
		MVMap<Long, Shape> shapes = spatialStorage.getShapes(levelKey);

		while (foundKeys.hasNext()) {
			long id = foundKeys.next().getId();
			Shape shape = shapes.get(id);
			if (shape != null && strategy.test(query, shape) && predicate.test(shape)) {
				return shape;
			}
		}

		return null;
	}

	public interface QueryStrategy {
		MVRTreeMap.RTreeCursor<Long> find(String levelKey, SpatialQuery query, SpatialShapeStorage spatialStorage);

		boolean test(SpatialQuery query, Shape shape);

		QueryStrategy INTERSECTION = new QueryStrategy() {
			@Override
			public MVRTreeMap.RTreeCursor<Long> find(String levelKey, SpatialQuery query, SpatialShapeStorage spatialStorage) {
				return spatialStorage.findIntersecting(levelKey, query);
			}

			@Override
			public boolean test(SpatialQuery query, Shape shape) {
				return shape.intersectsCuboid(query.minX(), query.minY(), query.minZ(), query.maxX(), query.maxY(), query.maxZ());
			}
		};
	}

}
