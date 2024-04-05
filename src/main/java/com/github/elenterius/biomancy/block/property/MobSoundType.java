package com.github.elenterius.biomancy.block.property;

import net.minecraft.util.StringRepresentable;

public enum MobSoundType implements StringRepresentable {
	AMBIENT("ambient"), HURT("hurt"), DEATH("death");

	private final String name;

	MobSoundType(String name) {
		this.name = name;
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
