package com.github.elenterius.biomancy.world.spatial;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.world.spatial.geometry.Shape;
import com.github.elenterius.biomancy.world.spatial.type.ShapeDataType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.storage.LevelResource;
import org.apache.logging.log4j.MarkerManager;
import org.h2.mvstore.MVMap;
import org.h2.mvstore.MVStore;
import org.h2.mvstore.rtree.MVRTreeMap;
import org.h2.mvstore.rtree.Spatial;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public final class SpatialShapeStorage extends SavedData implements AutoCloseable {

	public static final String LOG_MARKER = "SpatialStore";

	public static final LevelResource DATA_DIR = new LevelResource("data");

	private final MVStore store;
	private final Map<String, MVRTreeMap<Long>> levelTrees = new HashMap<>();
	private final Map<String, MVMap<Long, Shape>> levelShapes = new HashMap<>();

	private SpatialShapeStorage(String filePath) {
		store = new MVStore.Builder().fileName(filePath).cacheSize(8).open();
		BiomancyMod.LOGGER.debug(MarkerManager.getMarker(LOG_MARKER), "initialized database using file {}", filePath);
	}

	@SuppressWarnings("unused")
	private static SpatialShapeStorage load(CompoundTag tag, String path) {
		return new SpatialShapeStorage(path);
	}

	public static SpatialShapeStorage getInstance(ServerLevel level) {
		String path = level.getServer().getWorldPath(DATA_DIR).resolve("biomancy.spatial.db").toFile().getPath();

		//noinspection resource
		return level.getServer()
				.overworld()
				.getDataStorage()
				.computeIfAbsent(tag -> load(tag, path), () -> new SpatialShapeStorage(path), "biomancy.spatial");
	}

	@Override
	public void close() {
		levelTrees.clear();
		levelShapes.clear();

		BiomancyMod.LOGGER.debug(MarkerManager.getMarker(LOG_MARKER), "compacting & saving database");
		store.close(250);
	}

	MVRTreeMap<Long> getTree(String levelKey) {
		return levelTrees.computeIfAbsent(levelKey, this::createTreeForLevel);
	}

	MVMap<Long, Shape> getShapes(String levelKey) {
		return levelShapes.computeIfAbsent(levelKey, this::createMapForLevel);
	}

	private MVRTreeMap<Long> createTreeForLevel(String levelKey) {
		MVRTreeMap.Builder<Long> builder = new MVRTreeMap.Builder<Long>().dimensions(3);
		MVRTreeMap<Long> tree = store.openMap(levelKey + "_rtree", builder);
		if (!tree.isQuadraticSplit()) {
			tree.setQuadraticSplit(true);
		}
		return tree;
	}

	private MVMap<Long, Shape> createMapForLevel(String levelKey) {
		MVMap.Builder<Long, Shape> builder = new MVMap.Builder<Long, Shape>().valueType(new ShapeDataType());
		return store.openMap(levelKey + "_shapes", builder);
	}

	public MVRTreeMap.RTreeCursor<Long> findIntersecting(String levelKey, Spatial boundingBox) {
		return getTree(levelKey).findIntersectingKeys(boundingBox);
	}

	public MVRTreeMap.RTreeCursor<Long> findContained(String levelKey, Spatial boundingBox) {
		return getTree(levelKey).findContainedKeys(boundingBox);
	}

	public Shape getOrCreate(String levelKey, long shapeId, Supplier<Shape> factory) {
		MVRTreeMap<Long> tree = getTree(levelKey);
		MVMap<Long, Shape> shapes = getShapes(levelKey);

		if (shapes.containsKey(shapeId)) {
			return shapes.get(shapeId);
		}

		Shape shape = factory.get();
		shapes.put(shapeId, shape);
		tree.add(new SpatialKey(shapeId, shape.getAABB()), shapeId);

		BiomancyMod.LOGGER.debug(MarkerManager.getMarker(LOG_MARKER), "created id={} shape={}", shapeId, shape);

		setDirty();

		return shape;
	}

	public void remove(String levelKey, long shapeId) {
		MVMap<Long, Shape> shapes = getShapes(levelKey);
		MVRTreeMap<Long> tree = getTree(levelKey);

		Shape shape = shapes.get(shapeId);
		if (shape == null) return;

		Long removed = tree.remove(new SpatialKey(shapeId, shape.getAABB()));
		shapes.remove(shapeId);

		BiomancyMod.LOGGER.debug(MarkerManager.getMarker(LOG_MARKER), "removed id={} shape={}", removed, shape);

		setDirty();
	}

	@Override
	public CompoundTag save(CompoundTag tag) {
		return tag;
	}

	@Override
	public void save(File file) {
		super.save(file);

		if (!store.isClosed()) {
			if (store.hasUnsavedChanges()) {
				long version = store.commit();

				if (BiomancyMod.LOGGER.isDebugEnabled()) {
					String message = version >= 0 ? "flushed changes to disk" : "failed to flush changes";
					BiomancyMod.LOGGER.debug(MarkerManager.getMarker(LOG_MARKER), message);
				}
			}
			else {
				BiomancyMod.LOGGER.debug(MarkerManager.getMarker(LOG_MARKER), "has no unsaved changes");
				//TODO: compact?
				//store.compactFile(10);
			}
		}
	}

}
