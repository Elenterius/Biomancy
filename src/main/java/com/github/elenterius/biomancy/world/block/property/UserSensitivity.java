package com.github.elenterius.biomancy.world.block.property;


import net.minecraft.util.StringRepresentable;

public enum UserSensitivity implements StringRepresentable {
	NONE("none"),
	FRIENDLY("friend"),
	HOSTILE("enemy");

	private final String name;

	UserSensitivity(String name) {
		this.name = name;
	}

	public boolean isNone() {
		return this == NONE;
	}

	public UserSensitivity cycle() {
		return this == FRIENDLY ? HOSTILE : FRIENDLY;
	}

	@Override
	public String getSerializedName() {
		return name;
	}

	@Override
	public String toString() {
		return name;
	}

}
