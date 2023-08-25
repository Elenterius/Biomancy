package com.github.elenterius.biomancy.util;

public interface IntegerSerializable {
	int serializeToInteger();

	void deserializeFromInteger(int serializedValue);
}
