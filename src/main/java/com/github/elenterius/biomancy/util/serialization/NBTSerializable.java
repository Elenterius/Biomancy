package com.github.elenterius.biomancy.util.serialization;

public interface NBTSerializable<T> {
	NBTSerializer<T> getNBTSerializer();
}
