package com.github.elenterius.biomancy.util;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorldReader;

public final class WorldUtil {
	private WorldUtil() {}

	public static boolean isAir(IWorldReader reader, BlockPos pos) {
		return reader.getBlockState(pos).isAir(reader, pos); //TODO: update this in mc 1.17
	}

}
