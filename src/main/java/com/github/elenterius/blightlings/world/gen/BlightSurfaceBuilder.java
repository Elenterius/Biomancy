package com.github.elenterius.blightlings.world.gen;

import com.github.elenterius.blightlings.init.ModSurfaceBuilders;
import com.mojang.serialization.Codec;
import net.minecraft.block.BlockState;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.IChunk;
import net.minecraft.world.gen.surfacebuilders.SurfaceBuilder;
import net.minecraft.world.gen.surfacebuilders.SurfaceBuilderConfig;

import java.util.Random;

public class BlightSurfaceBuilder extends SurfaceBuilder<SurfaceBuilderConfig> {
	public BlightSurfaceBuilder(Codec<SurfaceBuilderConfig> configCodec) {
		super(configCodec);
	}

	@Override
	public void buildSurface(Random random, IChunk chunkIn, Biome biomeIn, int x, int z, int startHeight, double noise, BlockState defaultBlock, BlockState defaultFluid, int seaLevel, long seed, SurfaceBuilderConfig config) {
		seaLevel = 13;

		if (noise > 1.75D) {
			SurfaceBuilder.DEFAULT.buildSurface(random, chunkIn, biomeIn, x, z, startHeight, noise, defaultBlock, defaultFluid, seaLevel, seed, ModSurfaceBuilders.CONFIGS.GRASS_BLIGHT_SOIL_CONFIG);
		} else if (noise > -1.45D) {
			SurfaceBuilder.DEFAULT.buildSurface(random, chunkIn, biomeIn, x, z, startHeight, noise, defaultBlock, defaultFluid, seaLevel, seed, ModSurfaceBuilders.CONFIGS.BLIGHT_SOIL_CONFIG);
		} else {
			SurfaceBuilder.DEFAULT.buildSurface(random, chunkIn, biomeIn, x, z, startHeight, noise, defaultBlock, defaultFluid, seaLevel, seed, SurfaceBuilder.field_237187_R_);
		}
	}
}
