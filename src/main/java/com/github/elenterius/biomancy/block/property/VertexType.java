package com.github.elenterius.biomancy.block.property;

import net.minecraft.util.StringRepresentable;

import java.util.Locale;

public enum VertexType implements StringRepresentable {
	SOURCE, INNER, SINK;

	@Override
	public String getSerializedName() {
		return name().toLowerCase(Locale.ENGLISH);
	}
}
