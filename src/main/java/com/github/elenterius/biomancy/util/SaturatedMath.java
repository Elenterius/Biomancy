package com.github.elenterius.biomancy.util;

public final class SaturatedMath {

	private SaturatedMath() {}

	public static int castToInteger(long value) {
		if (value > Integer.MAX_VALUE) return Integer.MAX_VALUE;
		if (value < Integer.MIN_VALUE) return Integer.MIN_VALUE;
		return (int) value;
	}

	public static int add(int a, int b) {
		return castToInteger((long) a + b);
	}

	public static int subtract(int a, int b) {
		return castToInteger((long) a - b);
	}

	public static int clampToPositiveInteger(long value) {
		if (value > Integer.MAX_VALUE) return Integer.MAX_VALUE;
		if (value < 0) return 0;
		return (int) value;
	}

	public static int addAndClampToPositiveInteger(int a, int b) {
		return clampToPositiveInteger((long) a + b);
	}

}
