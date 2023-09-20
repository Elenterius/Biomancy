package com.github.elenterius.biomancy.util.random;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;

public interface Noise {
	float getValue(float x, float y);

	float getValue(float x, float y, float z);

	default float getValue(Vec3i pos) {
		return getValue(pos.getX(), pos.getY(), pos.getZ());
	}

	default float getValueAtCenter(BlockPos pos) {
		return getValue(pos.getX() + 0.5f, pos.getY() + 0.5f, pos.getZ() + 0.5f);
	}
}
