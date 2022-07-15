package com.github.elenterius.biomancy.world.entity.fleshblob;

import java.util.Random;

public enum TumorFlag {
	TUMOR1, TUMOR2, TUMOR3, TUMOR4, TUMOR5, TUMOR6, TUMOR7;

	private static final String[] BONE_IDS = new String[]{"tumor", "tumor2", "tumor3", "tumor4", "tumor5", "tumor6", "tumor7"};
	private final int bitPosition = 1 << ordinal();

	public static int getMaxNumber() {
		return (int) Math.pow(2, TumorFlag.values().length) - 1;
	}

	public static boolean isFlagSet(int value, TumorFlag flag) {
		return (value & flag.bitPosition) != 0;
	}

	public static int setFlag(int value, TumorFlag flag) {
		return value | flag.bitPosition;
	}

	public static int unsetFlag(int value, TumorFlag flag) {
		return value & ~flag.bitPosition;
	}

	public static byte randomFlags(Random random) {
		return (byte) random.nextInt(Byte.MAX_VALUE + 1);
	}

	public String getBoneId() {
		return BONE_IDS[ordinal()];
	}

	public boolean isNotSet(int flag) {
		return !isSet(flag);
	}

	public boolean isSet(int flag) {
		return (flag & bitPosition) != 0;
	}

	public int getBitPosition() {
		return bitPosition;
	}

}