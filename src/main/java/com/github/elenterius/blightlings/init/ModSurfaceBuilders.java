package com.github.elenterius.blightlings.init;

import com.github.elenterius.blightlings.BlightlingsMod;
import com.github.elenterius.blightlings.world.gen.BlightSurfaceBuilder;
import net.minecraft.block.Blocks;
import net.minecraft.util.registry.WorldGenRegistries;
import net.minecraft.world.gen.surfacebuilders.*;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.MarkerManager;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Mod.EventBusSubscriber(modid = BlightlingsMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class ModSurfaceBuilders {
	public abstract static class CONFIGS {
		public static final SurfaceBuilderConfig BLIGHT_SOIL_CONFIG = new SurfaceBuilderConfig(ModBlocks.INFERTILE_SOIL.get().getDefaultState(), ModBlocks.INFERTILE_SOIL.get().getDefaultState(), ModBlocks.INFERTILE_SOIL.get().getDefaultState());
		public static final SurfaceBuilderConfig GRASS_BLIGHT_SOIL_CONFIG = new SurfaceBuilderConfig(Blocks.GRASS_BLOCK.getDefaultState(), ModBlocks.INFERTILE_SOIL.get().getDefaultState(), ModBlocks.INFERTILE_SOIL.get().getDefaultState());
	}

	public abstract static class UNCONFIGURED {
		public static final SurfaceBuilder<SurfaceBuilderConfig> BLIGHT_SURFACE_BUILDER = createSurfaceBuilder("blight_surface", new BlightSurfaceBuilder(SurfaceBuilderConfig.field_237203_a_));
		public static final SurfaceBuilder<SurfaceBuilderConfig> BLIGHT_GRASS_SURFACE_BUILDER = createSurfaceBuilder("blight_grass_surface", new DefaultSurfaceBuilder(SurfaceBuilderConfig.field_237203_a_));

		private static <T extends ISurfaceBuilderConfig> SurfaceBuilder<T> createSurfaceBuilder(String name, SurfaceBuilder<T> surfaceBuilder) {
			surfaceBuilder.setRegistryName(BlightlingsMod.MOD_ID, name);
			return surfaceBuilder;
		}
	}

	public abstract static class CONFIGURED {
		public static final ConfiguredSurfaceBuilder<SurfaceBuilderConfig> BLIGHT_SURFACE_BUILDER = UNCONFIGURED.BLIGHT_SURFACE_BUILDER.func_242929_a(ModSurfaceBuilders.CONFIGS.BLIGHT_SOIL_CONFIG);
		public static final ConfiguredSurfaceBuilder<SurfaceBuilderConfig> BLIGHT_GRASS_SURFACE_BUILDER = UNCONFIGURED.BLIGHT_GRASS_SURFACE_BUILDER.func_242929_a(ModSurfaceBuilders.CONFIGS.GRASS_BLIGHT_SOIL_CONFIG);

		private static void registerConfiguredSurfaceBuilder(ConfiguredSurfaceBuilder<? extends ISurfaceBuilderConfig> configuredSB) {
			WorldGenRegistries.register(WorldGenRegistries.CONFIGURED_SURFACE_BUILDER, Objects.requireNonNull(configuredSB.builder.getRegistryName()), configuredSB);
		}
	}

	@SubscribeEvent
	public static void onSurfaceBuilderRegistry(RegistryEvent.Register<SurfaceBuilder<?>> event) {
		BlightlingsMod.LOGGER.info(MarkerManager.getMarker("SurfaceBuilders"), "registering surface builders...");
		List<Field> surfaceBuilders = Arrays.stream(UNCONFIGURED.class.getFields()).collect(Collectors.toList());
		surfaceBuilders.forEach(field -> {
			try {
				event.getRegistry().register((SurfaceBuilder<?>) field.get(null));
			} catch (IllegalAccessException e) {
				BlightlingsMod.LOGGER.error(MarkerManager.getMarker("SurfaceBuilders"), "failed to register surface builder", e);
			}
		});

		BlightlingsMod.LOGGER.info(MarkerManager.getMarker("SurfaceBuilders"), "registering configured surface builders...");
		List<Field> configuredFeatures = Arrays.stream(CONFIGURED.class.getFields()).collect(Collectors.toList());
		configuredFeatures.forEach(field -> {
			try {
				CONFIGURED.registerConfiguredSurfaceBuilder((ConfiguredSurfaceBuilder<? extends ISurfaceBuilderConfig>) field.get(null));
			} catch (IllegalAccessException e) {
				BlightlingsMod.LOGGER.error(MarkerManager.getMarker("SurfaceBuilders"), "failed to register configured surface builders", e);
			}
		});
	}

	private ModSurfaceBuilders() {}
}
