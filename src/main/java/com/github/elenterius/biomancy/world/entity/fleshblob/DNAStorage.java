package com.github.elenterius.biomancy.world.entity.fleshblob;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.entity.EntityType;

import java.util.ArrayList;
import java.util.List;

@Deprecated
final class DNAStorage {

	private final List<EntityType<?>> entities;
	private int maxSize;

	public DNAStorage(int maxSize) {
		this.maxSize = maxSize;
		entities = new ArrayList<>(maxSize);
	}

	public boolean addDNA(EntityType<?> entityType) {
		if (entities.size() >= maxSize || !entityType.canSerialize()) return false;
		entities.add(entityType);
		return true;
	}

	public boolean isEmpty() {
		return entities.isEmpty();
	}

	public void clear() {
		entities.clear();
	}

	public List<EntityType<?>> entities() {return entities;}

	public int maxSize() {return maxSize;}

	public CompoundTag toJson() {
		CompoundTag tag = new CompoundTag();
		ListTag listTag = new ListTag();
		if (!entities.isEmpty()) {
			for (EntityType<?> entityType : entities) {
				listTag.add(StringTag.valueOf(EntityType.getKey(entityType).toString()));
			}
		}
		tag.putInt("MaxSize", maxSize);
		tag.put("Entities", listTag);
		return tag;
	}

	public void fromJson(CompoundTag tag) {
		entities.clear();
		maxSize = tag.getInt("MaxSize");
		ListTag listTag = tag.getList("Entities", Tag.TAG_STRING);
		if (!listTag.isEmpty()) {
			for (int i = 0; i < listTag.size(); i++) {
				String entityTypeId = listTag.getString(i);
				if (!entityTypeId.isEmpty()) {
					EntityType.byString(entityTypeId).ifPresent(this::addDNA);
				}
			}
		}
	}

	@Override
	public String toString() {
		return "DNAStorage[" +
				"entities=" + entities + ", " +
				"maxSize=" + maxSize + ']';
	}

}
