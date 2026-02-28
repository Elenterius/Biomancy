package com.github.elenterius.biomancy.util.shooting;

public enum GunState {
	NONE((byte) 0), SHOOTING_OR_CHARGING((byte) 1), RELOADING((byte) 2);

	private final byte id;

	GunState(byte id) {
		this.id = id;
	}

	public static GunState fromId(int id) {
		if (id == 0) return NONE;
		if (id == 1) return SHOOTING_OR_CHARGING;
		if (id == 2) return RELOADING;
		return NONE;
	}

	public byte getId() {
		return id;
	}
}
