package com.github.elenterius.biomancy.util;

import java.util.Set;

public interface MinMaxIntegerProvider {

	static MinMaxIntegerProvider of(Set<Integer> values) {
		int min = Integer.MAX_VALUE;
		int max = Integer.MIN_VALUE;

		for (int v : values) {
			if (v > max) {
				max = v;
			}
			if (v < min) {
				min = v;
			}
		}

		int finalMin = min;
		int finalMax = max;

		return new MinMaxIntegerProvider() {
			@Override
			public int biomancy$getMin() {
				return finalMin;
			}

			@Override
			public int biomancy$getMax() {
				return finalMax;
			}
		};
	}

	int biomancy$getMin();

	int biomancy$getMax();
}
