package com.github.elenterius.blightlings.world.gen;

import com.github.elenterius.blightlings.init.ModWorldGen;
import net.minecraft.world.gen.INoiseRandom;
import net.minecraft.world.gen.layer.traits.ICastleTransformer;

public enum InnerEdgeBiomeLayer implements ICastleTransformer
{
    INSTANCE;

    public int apply(INoiseRandom context, int north, int west, int south, int east, int center) {
        if (center == ModWorldGen.BLIGHT_BIOME_ID) {
            int mainBiome = ModWorldGen.BLIGHT_BIOME_ID;
            if (north == mainBiome && west == mainBiome && east == mainBiome && south == mainBiome) {
                return mainBiome;
            }
            else {
                return ModWorldGen.BLIGHT_BIOME_OUTER_EDGE_ID;
            }
        }
        return center;
    }
}
