package com.github.elenterius.biomancy.util;

import java.util.Arrays;
import java.util.Random;

public final class ArrayUtil {

	private ArrayUtil() {}

	public static <T> T[] shuffleCopy(T[] array, Random random) {
		T[] copy = Arrays.copyOf(array, array.length);
		shuffle(copy, random);
		return copy;
	}

	/**
	 * shuffle the array in place using Durstenfeld / Fisher-Yates
	 */
	public static void shuffle(Object[] array, Random random) {
		for (int i = array.length - 1; i > 0; i--) {
			int j = random.nextInt(i + 1);
			Object temp = array[j];
			array[j] = array[i];
			array[i] = temp;
		}
	}

	/**
	 * shuffle the array in place using Durstenfeld / Fisher-Yates
	 */
	public static void shuffle(int[] array, Random random) {
		for (int i = array.length - 1; i > 0; i--) {
			int j = random.nextInt(i + 1);
			int temp = array[j];
			array[j] = array[i];
			array[i] = temp;
		}
	}

	/**
	 * shuffle the array in place using Durstenfeld / Fisher-Yates
	 */
	public static void shuffle(float[] array, Random random) {
		for (int i = array.length - 1; i > 0; i--) {
			int j = random.nextInt(i + 1);
			float temp = array[j];
			array[j] = array[i];
			array[i] = temp;
		}
	}

	/**
	 * shuffle the array in place using Durstenfeld / Fisher-Yates
	 */
	public static void shuffle(double[] array, Random random) {
		for (int i = array.length - 1; i > 0; i--) {
			int j = random.nextInt(i + 1);
			double temp = array[j];
			array[j] = array[i];
			array[i] = temp;
		}
	}

}
