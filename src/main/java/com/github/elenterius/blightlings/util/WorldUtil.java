package com.github.elenterius.blightlings.util;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorldReader;

public abstract class WorldUtil {

    public static boolean isAir(IWorldReader reader, BlockPos pos) {
        return reader.getBlockState(pos).isAir(reader, pos); //TODO: update this in mc 1.17
    }

}
