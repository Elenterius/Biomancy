package com.github.elenterius.biomancy.util;

import com.github.elenterius.biomancy.mixin.accessor.BiomeAccessor;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;

public class ClimateUtil {
	public static final float MIN_TEMP = -0.7f; //frozen peaks biome
	public static final float MAX_TEMP = 2; //desert biome
	public static final float TEMP_DIFF = MAX_TEMP - MIN_TEMP;

	public static float rescale(float minecraftTemperature) {
		return (minecraftTemperature - MIN_TEMP) / TEMP_DIFF;
	}

	public static boolean isFreezing(float minecraftTemperature) {
		return minecraftTemperature < 0.15;
	}

	public static float convertMinecraftTemperatureToCelsius(float minecraftTemperature) {
		return 27.8f * minecraftTemperature - 4.17f;
	}

	public static float getTemperature(Level level, BlockPos pos) {
		Biome biome = level.getBiome(pos).get();
		return getTemperature(biome, pos);
	}

	public static float getTemperature(Biome biome, BlockPos pos) {
		return ((BiomeAccessor) (Object) biome).biomancy$getTemperature(pos);
	}

	public static float getHumidity(Biome biome) {
		return biome.getModifiedClimateSettings().downfall();
	}

}
