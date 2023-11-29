package com.github.elenterius.biomancy.util.serialization;

import net.minecraft.nbt.CompoundTag;

public interface NBTSerializer<T> {
	String id();

	CompoundTag serializeNBT(T t);

	T deserializeNBT(CompoundTag tag);

	@FunctionalInterface
	interface Factory<T> {
		NBTSerializer<T> create(String id);
	}
}
