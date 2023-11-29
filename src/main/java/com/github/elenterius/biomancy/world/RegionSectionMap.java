package com.github.elenterius.biomancy.world;

import com.github.elenterius.biomancy.util.serialization.NBTSerializable;
import com.github.elenterius.biomancy.util.serialization.NBTSerializer;
import com.github.elenterius.biomancy.util.serialization.NBTSerializers;
import it.unimi.dsi.fastutil.ints.IntArraySet;
import it.unimi.dsi.fastutil.ints.IntSet;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.level.ChunkPos;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;

public class RegionSectionMap {

	/**
	 * spatial hash map based on a 16x16x16 cell grid
	 */
	protected final Long2ObjectMap<Set<Region>> sections = new Long2ObjectOpenHashMap<>();
	protected final Long2ObjectMap<IntSet> chunks = new Long2ObjectOpenHashMap<>();
	protected final Long2ObjectMap<Region> loadedRegions = new Long2ObjectOpenHashMap<>();

	public RegionSectionMap() {}

	public RegionSectionMap(Iterable<Region> regions) {
		for (Region region : regions) {
			add(region);
		}
	}

	public boolean add(Region region) {
		if (loadedRegions.containsKey(region.getId())) return false;

		SABB sectionRegion = region.getSABB();

		sectionRegion.forEachSection((sectionX, sectionY, sectionZ) -> {
			long sectionKey = SectionPos.asLong(sectionX, sectionY, sectionZ);
			sections.computeIfAbsent(sectionKey, k -> new HashSet<>()).add(region);

			long chunkKey = ChunkPos.asLong(sectionX, sectionZ);
			chunks.computeIfAbsent(chunkKey, k -> new IntArraySet()).add(sectionY);
		});

		loadedRegions.put(region.getId(), region);
		return true;
	}

	public boolean remove(long regionId) {
		Region region = loadedRegions.get(regionId);
		if (region == null) return false;

		return remove(region);
	}

	public boolean remove(Region region) {
		if (!loadedRegions.containsKey(region.getId())) return false;

		SABB sectionRange = region.getSABB();

		sectionRange.forEachSection((sectionX, sectionY, sectionZ) -> {
			long sectionKey = SectionPos.asLong(sectionX, sectionY, sectionZ);

			Set<Region> section = sections.get(sectionKey);
			if (section != null) {
				section.remove(region);

				if (section.isEmpty()) {
					sections.remove(sectionKey);
					removeChunkSection(sectionX, sectionY, sectionZ);
				}
			}
			else {
				removeChunkSection(sectionX, sectionY, sectionZ);
			}
		});

		loadedRegions.remove(region.getId());
		return true;
	}

	public void removeSectionRange(SABB sectionRange) {
		Set<Region> removedRegions = new HashSet<>();

		sectionRange.forEachSection((sectionX, sectionY, sectionZ) -> {
			long sectionKey = SectionPos.asLong(sectionX, sectionY, sectionZ);
			Set<Region> removedValues = sections.remove(sectionKey);
			if (removedValues != null) {
				removedRegions.addAll(removedValues);
			}
			removeChunkSection(sectionX, sectionY, sectionZ);
		});

		removeUnloadedRegions(removedRegions);
	}

	public void removeSection(long sectionKey) {
		Set<Region> removedRegions = sections.remove(sectionKey);
		removeChunkSection(SectionPos.x(sectionKey), SectionPos.y(sectionKey), SectionPos.z(sectionKey));
		if (removedRegions == null) return;

		removeUnloadedRegions(removedRegions);
	}

	public void removeChunk(long chunkKey) {
		IntSet chunkSections = chunks.remove(chunkKey);
		if (chunkSections == null) return;

		int sectionX = ChunkPos.getX(chunkKey);
		int sectionZ = ChunkPos.getZ(chunkKey);

		Set<Region> removedRegions = new HashSet<>();

		for (int sectionY : chunkSections) {
			long sectionKey = SectionPos.asLong(sectionX, sectionY, sectionZ);
			Set<Region> removedValues = sections.remove(sectionKey);
			if (removedValues != null) {
				removedRegions.addAll(removedValues);
			}
		}

		removeUnloadedRegions(removedRegions);
	}

	private void removeChunkSection(int sectionX, int sectionY, int sectionZ) {
		long chunkKey = ChunkPos.asLong(sectionX, sectionZ);

		IntSet chunkSections = chunks.get(chunkKey);
		if (chunkSections != null) {
			chunkSections.remove(sectionY);
			if (chunkSections.isEmpty()) {
				chunks.remove(chunkKey);
			}
		}
	}

	private void removeUnloadedRegions(Set<Region> regions) {
		for (Region region : regions) {
			SABB sectionRange = region.getSABB();

			boolean isLoaded = sectionRange.stream()
					.map(pos -> sections.get(pos.asLong()))
					.anyMatch(section -> section != null && section.contains(region));

			if (!isLoaded) {
				loadedRegions.remove(region);
			}
		}
	}

	public boolean has(Region region) {
		return loadedRegions.containsKey(region.getId());
	}


	protected @Nullable Set<Region> getSection(int sectionX, int sectionY, int sectionZ) {
		long sectionKey = SectionPos.asLong(sectionX, sectionY, sectionZ);
		return sections.get(sectionKey);
	}

	protected @Nullable Set<Region> getSectionAt(double x, double y, double z) {
		int sectionX = SectionPos.blockToSectionCoord(x);
		int sectionY = SectionPos.blockToSectionCoord(y);
		int sectionZ = SectionPos.blockToSectionCoord(z);
		return getSection(sectionX, sectionY, sectionZ);
	}

	protected @Nullable Set<Region> getSectionAt(int x, int y, int z) {
		int sectionX = SectionPos.blockToSectionCoord(x);
		int sectionY = SectionPos.blockToSectionCoord(y);
		int sectionZ = SectionPos.blockToSectionCoord(z);
		return getSection(sectionX, sectionY, sectionZ);
	}

	protected @Nullable Set<Region> getSectionAt(BlockPos pos) {
		int sectionX = SectionPos.blockToSectionCoord(pos.getX());
		int sectionY = SectionPos.blockToSectionCoord(pos.getY());
		int sectionZ = SectionPos.blockToSectionCoord(pos.getZ());
		return getSection(sectionX, sectionY, sectionZ);
	}

	public @Nullable Region getClosestContainingAt(double x, double y, double z) {
		Region closestRegion = null;

		Set<Region> section = getSectionAt(x, y, z);
		if (section != null) {
			double minDistSqr = Double.MAX_VALUE;
			for (Region region : section) {
				if (region.contains(x, y, z)) {
					double distSqr = region.distanceToSqr(x, y, z);
					if (distSqr < minDistSqr) {
						closestRegion = region;
						minDistSqr = distSqr;
					}
				}
			}
		}

		return closestRegion;
	}

	public boolean hasAt(double x, double y, double z) {
		Set<Region> section = getSectionAt(x, y, z);
		if (section != null) {
			for (Region region : section) {
				if (region.contains(x, y, z)) {
					return true;
				}
			}
		}

		return false;
	}

	public boolean testAt(double x, double y, double z, Predicate<Region> predicate) {
		Set<Region> section = getSectionAt(x, y, z);
		if (section != null) {
			for (Region region : section) {
				if (region.contains(x, y, z) && predicate.test(region)) {
					return true;
				}
			}
		}

		return false;
	}

	public CompoundTag writeNBT() {
		ListTag listTag = new ListTag();

		for (Region region : loadedRegions.values()) {
			if (region instanceof NBTSerializable<?> serializable) {
				//noinspection unchecked
				NBTSerializer<Region> serializer = (NBTSerializer<Region>) serializable.getNBTSerializer();
				CompoundTag serialized = serializer.serializeNBT(region);
				serialized.putString("Serializer", serializer.id());

				CompoundTag tag = new CompoundTag();
				tag.put("Region", serialized);
				listTag.add(tag);
			}
		}

		CompoundTag tag = new CompoundTag();
		tag.put("Regions", listTag);

		return tag;
	}

	public void readNBT(CompoundTag tag) {
		sections.clear();
		chunks.clear();
		loadedRegions.clear();

		ListTag tagList = tag.getList("Regions", Tag.TAG_COMPOUND);
		for (int i = 0; i < tagList.size(); i++) {
			CompoundTag entry = tagList.getCompound(i);
			CompoundTag serialized = entry.getCompound("Region");
			String serializerId = serialized.getString("Serializer");
			NBTSerializer<?> nbtSerializer = NBTSerializers.get(serializerId);
			if (nbtSerializer != null) {
				Object o = nbtSerializer.deserializeNBT(serialized);
				if (o instanceof Region region) {
					add(region);
				}
			}
		}
	}
}
