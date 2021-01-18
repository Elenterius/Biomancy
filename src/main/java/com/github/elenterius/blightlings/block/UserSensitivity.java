package com.github.elenterius.blightlings.block;

import net.minecraft.util.IStringSerializable;

public enum UserSensitivity implements IStringSerializable {
	NONE("none"),
	AUTHORIZED("friend"),
	UNAUTHORIZED("enemy");

	private final String name;

	UserSensitivity(String name) {
		this.name = name;
	}

	public boolean isNone() {
		return this == NONE;
	}

	/**
	 * switches between AUTHORIZED and UNAUTHORIZED
	 */
	public UserSensitivity switchAuth() {
		return this == AUTHORIZED ? UNAUTHORIZED : AUTHORIZED;
	}

	@Override
	public String getString() {
		return name;
	}

	@Override
	public String toString() {
		return name;
	}
}
