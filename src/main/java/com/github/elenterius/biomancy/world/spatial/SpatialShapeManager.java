package com.github.elenterius.biomancy.world.spatial;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.util.shape.Shape;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.level.LevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.h2.mvstore.MVMap;
import org.h2.mvstore.rtree.MVRTreeMap;
import org.h2.mvstore.rtree.Spatial;
import org.jetbrains.annotations.Nullable;

import java.util.function.Predicate;
import java.util.function.Supplier;

@Mod.EventBusSubscriber(modid = BiomancyMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class SpatialShapeManager {

	private SpatialShapeManager() {}

	@SubscribeEvent
	public static void onLevelUnload(LevelEvent.Unload event) {
		if (!event.getLevel().isClientSide() && event.getLevel() instanceof ServerLevel serverLevel && (serverLevel.dimension() == Level.OVERWORLD)) {
			SpatialShapeStorage storage = SpatialShapeStorage.getInstance(serverLevel);
			storage.close();
		}
	}

	private static String getLevelKey(ServerLevel level) {
		return level.dimension().location().toString();
	}

	public static Shape getOrCreateShape(ServerLevel level, BlockPos shapeId, Supplier<Shape> supplier) {
		SpatialShapeStorage storage = SpatialShapeStorage.getInstance(level);
		return storage.getOrCreate(getLevelKey(level), shapeId.asLong(), supplier);
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
		SpatialShapeStorage spatialStorage = SpatialShapeStorage.getInstance(level);
		String levelKey = getLevelKey(level);
		Spatial boundingBox = SpatialBoundingBox.of(blockPos);

		Vec3 position = Vec3.atCenterOf(blockPos);
		double minDistSqr = Double.MAX_VALUE;
		Shape closestShape = null;

		MVRTreeMap.RTreeCursor<Long> intersectingKeys = spatialStorage.findIntersecting(levelKey, boundingBox);
		MVMap<Long, Shape> shapes = spatialStorage.getShapes(levelKey);

		while (intersectingKeys.hasNext()) {
			long id = intersectingKeys.next().getId();
			Shape shape = shapes.get(id);
			if (shape != null && shape.contains(position.x, position.y, position.z) && predicate.test(shape)) {
				double distSqr = shape.distanceToSqr(position.x, position.y, position.z);
				if (distSqr < minDistSqr) {
					closestShape = shape;
					minDistSqr = distSqr;
				}
			}
		}

		return closestShape;
	}
}
