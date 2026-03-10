package com.github.elenterius.biomancy.util.shooting;

import java.text.DecimalFormat;

public record ProjectileRange(float min, float max, boolean isInfinite) {

	public ProjectileRange(float min, float max) {
		this(min, max, Float.isInfinite(min) || Float.isInfinite(max));
	}

	public float mean() {
		return (min + max) * 0.5f;
	}

	public String format(DecimalFormat df) {
		if (isInfinite) return "∞";
		return String.format("%s - %s", df.format(min), df.format(max));
	}
}
