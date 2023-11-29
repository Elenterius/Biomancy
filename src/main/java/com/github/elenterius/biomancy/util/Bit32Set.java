package com.github.elenterius.biomancy.util;

import com.github.elenterius.biomancy.util.serialization.IntegerSerializable;

public class Bit32Set implements IntegerSerializable {
	private int bits;

	private void validateIndex(int bitIndex) {
		if (bitIndex < 0) throw new IndexOutOfBoundsException("bitIndex < 0: " + bitIndex);
		if (bitIndex >= 32) throw new IndexOutOfBoundsException("bitIndex >= 32: " + bitIndex);
	}

	public void set(int bitIndex) {
		validateIndex(bitIndex);
		bits |= (1 << bitIndex);
	}

	public void set(int bitIndex, boolean value) {
		if (value) set(bitIndex);
		else clear(bitIndex);
	}

	public boolean get(int bitIndex) {
		validateIndex(bitIndex);
		return (bits & (1 << bitIndex)) != 0;
	}

	public void flip(int bitIndex) {
		validateIndex(bitIndex);
		bits ^= (1 << bitIndex);
	}

	public void clear(int bitIndex) {
		validateIndex(bitIndex);
		bits &= ~(1 << bitIndex);
	}

	public void clear() {
		bits = 0;
	}

	/**
	 * @return number of bits set to {@code true}
	 */
	public int cardinality() {
		return Integer.bitCount(bits);
	}

	public int nextSetBit(int fromIndex) {
		validateIndex(fromIndex);

		if (bits == 0) return 32;

		for (int i = fromIndex; i < 32; i++) {
			if ((bits & (1 << i)) != 0) {
				return i;
			}
		}

		return 32;
	}

	/**
	 * @return indices of all bits set to true
	 */
	public int[] getIndices() {
		if (bits == 0) return new int[]{};
		int[] indices = new int[Integer.bitCount(bits)];

		int n = 0;
		for (int i = 0; i < 32; i++) {
			if ((bits & (1 << i)) != 0) {
				indices[n++] = i;
			}
		}

		return indices;
	}

	public int getBits() {
		return bits;
	}

	@Override
	public int serializeToInteger() {
		return bits;
	}

	@Override
	public void deserializeFromInteger(int serializedValue) {
		bits = serializedValue;
	}

}
