package com.github.elenterius.biomancy.util.serialization;

public interface IntegerSerializable {
	int serializeToInteger();

	void deserializeFromInteger(int serializedValue);
}
