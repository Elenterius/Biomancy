package com.github.elenterius.biomancy.world;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.saveddata.SavedData;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.Set;
import java.util.function.Function;

public final class RegionStore extends SavedData {

	private final RegionSectionMap regionMap = new RegionSectionMap(); //TODO: replace with R-Tree cache implementation & MVStore R-Tree database

	private RegionStore() {}

	public static RegionStore loadOrGet(ServerLevel level) {
		return level
				.getDataStorage()
				.computeIfAbsent(RegionStore::load, RegionStore::new, "biomancy_region");
	}

	public @Nullable Set<Region> getRegionsAt(BlockPos pos) {
		return regionMap.getSectionAt(pos);
	}

	void unloadChunk(ChunkAccess chunk) {
		//regionMap.removeChunk(chunk.getPos().toLong());
	}

	void loadChunk(ChunkAccess chunk) {
		//ChunkPos chunkPos = chunk.getPos();
		//long min = SectionPos.asLong(chunkPos.x, chunk.getMinSection(), chunkPos.z);
		//long max = SectionPos.asLong(chunkPos.x, chunk.getMaxSection() - 1, chunkPos.z);
		//sectionMap.updateOrLoad(new SABB(min, max));
	}

	public Region getOrCreate(BlockPos regionOrigin, Function<BlockPos, Region> factory) {
		long regionKey = regionOrigin.asLong();

		if (regionMap.loadedRegions.containsKey(regionKey)) {
			return regionMap.loadedRegions.get(regionKey);
		}

		Region region = factory.apply(regionOrigin);
		regionMap.add(region);

		setDirty();
		return region;
	}

	public void remove(BlockPos regionOrigin) {
		if (regionMap.remove(regionOrigin.asLong())) {
			setDirty();
		}
	}

	@Override
	public CompoundTag save(CompoundTag tag) {
		tag.put("RegionMap", regionMap.writeNBT());
		return tag;
	}

	private static RegionStore load(CompoundTag tag) {
		RegionStore levelData = new RegionStore();
		levelData.regionMap.readNBT(tag.getCompound("RegionMap"));
		return levelData;
	}

	@Override
	public void save(File file) {
		super.save(file);
		//TODO: database??
	}

}
