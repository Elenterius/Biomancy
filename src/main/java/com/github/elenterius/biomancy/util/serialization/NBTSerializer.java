package com.github.elenterius.biomancy.util.serialization;

import net.minecraft.nbt.CompoundTag;

public interface NBTSerializer<T> {
	String id();

	CompoundTag write(T t);

	T read(CompoundTag tag);

}
