package com.github.elenterius.blightlings.init;

import com.github.elenterius.blightlings.BlightlingsMod;
import com.github.elenterius.blightlings.mixin.WorldCarverMixinAccessor;
import com.github.elenterius.blightlings.world.gen.tree.LilyTreeFeature;
import com.google.common.collect.ImmutableSet;
import net.minecraft.block.Block;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.WorldGenRegistries;
import net.minecraft.world.gen.carver.WorldCarver;
import net.minecraft.world.gen.feature.*;
import net.minecraft.world.gen.placement.AtSurfaceWithExtraConfig;
import net.minecraft.world.gen.placement.Placement;
import net.minecraft.world.gen.surfacebuilders.SurfaceBuilder;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.logging.log4j.MarkerManager;

import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

@Mod.EventBusSubscriber(modid = BlightlingsMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public abstract class ModFeatures
{
    //we can't use deferred feature & deferred biome registry together because the features may not be registered yet when building biome settings
//    public static final DeferredRegister<Feature<?>> FEATURE_REGISTRY = DeferredRegister.create(ForgeRegistries.FEATURES, BlightlingsMod.MOD_ID);

    public abstract static class UNCONFIGURED
    {
        public static final Feature<NoFeatureConfig> LILY_TREE = createFeature("lily_tree", new LilyTreeFeature(NoFeatureConfig.field_236558_a_));
        public static final Feature<BlockStateFeatureConfig> LUMINOUS_SPORE_BLOB = createFeature("luminous_spore_blob", new BlockBlobFeature(BlockStateFeatureConfig.field_236455_a_));

        private static <FC extends IFeatureConfig> Feature<FC> createFeature(String name, Feature<FC> feature) {
            feature.setRegistryName(BlightlingsMod.MOD_ID, name);
            return feature;
        }
    }

    public abstract static class CONFIGURED
    {
        public static final ConfiguredFeature<?, ?> LILY_TREE = UNCONFIGURED.LILY_TREE
                .withConfiguration(NoFeatureConfig.NO_FEATURE_CONFIG)
                .withPlacement(Features.Placements.HEIGHTMAP_PLACEMENT)
                .withPlacement(Placement.COUNT_EXTRA.configure(new AtSurfaceWithExtraConfig(2, 0.1f, 1)));

        public static final ConfiguredFeature<?, ?> LUMINOUS_SPORE_BLOB = UNCONFIGURED.LUMINOUS_SPORE_BLOB
                .withConfiguration(new BlockStateFeatureConfig(ModBlocks.LUMINOUS_SOIL.get().getDefaultState()))
                .withPlacement(Features.Placements.HEIGHTMAP_PLACEMENT).func_242732_c(2);

        @SuppressWarnings("UnusedReturnValue")
        private static <FC extends IFeatureConfig> ConfiguredFeature<FC, ?> registerConfiguredFeature(ConfiguredFeature<FC, ?> configuredFeature) {
            return Registry.register(WorldGenRegistries.CONFIGURED_FEATURE, Objects.requireNonNull(configuredFeature.feature.getRegistryName()), configuredFeature);
        }
    }

    public static final DeferredRegister<SurfaceBuilder<?>> SURFACE_BUILDER_REGISTRY = DeferredRegister.create(ForgeRegistries.SURFACE_BUILDERS, BlightlingsMod.MOD_ID);

    public static void injectCarvableBlocks() {
        Set<Block> canyonCarvables = new HashSet<>(((WorldCarverMixinAccessor) WorldCarver.CANYON).getCarvableBlocks());
        canyonCarvables.add(ModBlocks.INFERTILE_SOIL.get());
        canyonCarvables.add(ModBlocks.LUMINOUS_SOIL.get());
        ((WorldCarverMixinAccessor) WorldCarver.CANYON).setCarvableBlocks(ImmutableSet.copyOf(canyonCarvables));

        Set<Block> caveCarvables = new HashSet<>(((WorldCarverMixinAccessor) WorldCarver.CAVE).getCarvableBlocks());
        caveCarvables.add(ModBlocks.INFERTILE_SOIL.get());
        caveCarvables.add(ModBlocks.LUMINOUS_SOIL.get());
        ((WorldCarverMixinAccessor) WorldCarver.CAVE).setCarvableBlocks(ImmutableSet.copyOf(caveCarvables));
    }

    @SubscribeEvent
    public static void onFeatureRegistry(RegistryEvent.Register<Feature<?>> event) {
        BlightlingsMod.LOGGER.info(MarkerManager.getMarker("BiomeFeatures"), "registering features...");
        List<Field> features = Arrays.stream(UNCONFIGURED.class.getFields()).collect(Collectors.toList());
        features.forEach(field -> {
            try {
                event.getRegistry().register((Feature<?>) field.get(null));
            }
            catch (IllegalAccessException e) {
                BlightlingsMod.LOGGER.error(MarkerManager.getMarker("BiomeFeatures"), "failed to register feature", e);
            }
        });

        BlightlingsMod.LOGGER.info(MarkerManager.getMarker("BiomeFeatures"), "registering configured features...");
        List<Field> configuredFeatures = Arrays.stream(CONFIGURED.class.getFields()).collect(Collectors.toList());
        configuredFeatures.forEach(field -> {
            try {
                CONFIGURED.registerConfiguredFeature((ConfiguredFeature<?, ?>) field.get(null));
            }
            catch (IllegalAccessException e) {
                BlightlingsMod.LOGGER.error(MarkerManager.getMarker("BiomeFeatures"), "failed to register configured feature", e);
            }
        });
    }
}
