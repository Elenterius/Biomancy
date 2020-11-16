package com.github.elenterius.blightlings.init;

import com.github.elenterius.blightlings.BlightlingsMod;
import com.github.elenterius.blightlings.mixin.WorldCarverMixinAccessor;
import com.github.elenterius.blightlings.world.gen.tree.LilyTreeFeature;
import com.google.common.collect.ImmutableSet;
import net.minecraft.block.Block;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.WorldGenRegistries;
import net.minecraft.world.gen.carver.WorldCarver;
import net.minecraft.world.gen.feature.*;
import net.minecraft.world.gen.placement.AtSurfaceWithExtraConfig;
import net.minecraft.world.gen.placement.Placement;
import net.minecraft.world.gen.surfacebuilders.SurfaceBuilder;
import net.minecraftforge.common.util.Lazy;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Mod.EventBusSubscriber(modid = BlightlingsMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public abstract class ModFeatures
{
    //we can't use deferred feature & deferred biome registry together because the features may not be registered yet when building biome settings
//    public static final DeferredRegister<Feature<?>> FEATURE_REGISTRY = DeferredRegister.create(ForgeRegistries.FEATURES, BlightlingsMod.MOD_ID);

    public abstract static class UNCONFIGURED
    {
        public static final Feature<NoFeatureConfig> LILY_TREE_FEATURE = createFeature("lily_tree", new LilyTreeFeature(NoFeatureConfig.field_236558_a_));

        private static <FC extends IFeatureConfig> Feature<FC> createFeature(String name, Feature<FC> feature) {
            feature.setRegistryName(new ResourceLocation(BlightlingsMod.MOD_ID, name));
            return feature;
        }
    }

    public abstract static class CONFIGURED
    {
        public static final Lazy<ConfiguredFeature<?, ?>> LILY_TREE_FEATURE = Lazy.of(() -> registerConfiguredFeature(
                Objects.requireNonNull(UNCONFIGURED.LILY_TREE_FEATURE.getRegistryName()),
                UNCONFIGURED.LILY_TREE_FEATURE
                        .withConfiguration(NoFeatureConfig.NO_FEATURE_CONFIG)
                        .withPlacement(Features.Placements.HEIGHTMAP_PLACEMENT)
                        .withPlacement(Placement.COUNT_EXTRA.configure(new AtSurfaceWithExtraConfig(2, 0.1f, 1)))
        ));

        private static <FC extends IFeatureConfig> ConfiguredFeature<FC, ?> registerConfiguredFeature(ResourceLocation registryName, ConfiguredFeature<FC, ?> configuredFeature) {
            return Registry.register(WorldGenRegistries.CONFIGURED_FEATURE, registryName, configuredFeature);
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
        BlightlingsMod.LOGGER.info("registering features");
        event.getRegistry().registerAll(
                UNCONFIGURED.LILY_TREE_FEATURE
        );

        // force initialization
        CONFIGURED.LILY_TREE_FEATURE.get();
    }
}
