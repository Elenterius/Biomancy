package com.github.elenterius.biomancy;

import com.github.elenterius.biomancy.config.ServerConfig;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;
import org.apache.commons.lang3.tuple.Pair;

public final class BiomancyConfig {

	public static final ForgeConfigSpec SERVER_SPECIFICATION;
	public static final ServerConfig SERVER;

	private BiomancyConfig() {}

	static {
		Pair<ServerConfig, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(ServerConfig::new);
		SERVER = specPair.getLeft();
		SERVER_SPECIFICATION = specPair.getRight();
	}

	public static void register(ModLoadingContext modLoadingContext) {
		modLoadingContext.registerConfig(ModConfig.Type.SERVER, BiomancyConfig.SERVER_SPECIFICATION);
	}

}
