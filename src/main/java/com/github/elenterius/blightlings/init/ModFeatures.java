package com.github.elenterius.blightlings.init;

import com.github.elenterius.blightlings.BlightlingsMod;
import com.github.elenterius.blightlings.mixin.WorldCarverMixinAccessor;
import com.github.elenterius.blightlings.world.gen.feature.MoonMonolithFeature;
import com.github.elenterius.blightlings.world.gen.tree.LilyTreeFeature;
import com.google.common.collect.ImmutableSet;
import net.minecraft.block.Block;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.WorldGenRegistries;
import net.minecraft.world.gen.blockplacer.SimpleBlockPlacer;
import net.minecraft.world.gen.blockstateprovider.SimpleBlockStateProvider;
import net.minecraft.world.gen.blockstateprovider.WeightedBlockStateProvider;
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
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Mod.EventBusSubscriber(modid = BlightlingsMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public abstract class ModFeatures
{
    //we can't use deferred feature & deferred biome registry together because the features may not be registered yet when building biome settings
//    public static final DeferredRegister<Feature<?>> FEATURE_REGISTRY = DeferredRegister.create(ForgeRegistries.FEATURES, BlightlingsMod.MOD_ID);
    public abstract static class CONFIGS
    {
        public static final BlockClusterFeatureConfig BLIGHT_SPROUT_CONFIG = new BlockClusterFeatureConfig.Builder(new WeightedBlockStateProvider().addWeightedBlockstate(ModBlocks.BLIGHT_SPROUT.get().getDefaultState(), 1).addWeightedBlockstate(ModBlocks.BLIGHT_SPROUT_SMALL.get().getDefaultState(), 4), SimpleBlockPlacer.PLACER).tries(32).build();
        public static final BlockClusterFeatureConfig BLIGHTSHROOM_CONFIG = new BlockClusterFeatureConfig.Builder(new SimpleBlockStateProvider(ModBlocks.BLIGHT_SHROOM_TALL.get().getDefaultState()), SimpleBlockPlacer.PLACER).tries(64).func_227317_b_().build();
    }

    public abstract static class UNCONFIGURED
    {
        public static final Feature<NoFeatureConfig> LILY_TREE = createFeature("lily_tree", new LilyTreeFeature(NoFeatureConfig.field_236558_a_));
        public static final Feature<BlockStateFeatureConfig> LUMINOUS_SPORE_BLOB = createFeature("luminous_spore_blob", new BlockBlobFeature(BlockStateFeatureConfig.field_236455_a_));
        public static final Feature<NoFeatureConfig> MOON_MONOLITH = createFeature("moon_monolith", new MoonMonolithFeature(NoFeatureConfig.field_236558_a_));

        private static <FC extends IFeatureConfig> Feature<FC> createFeature(String name, Feature<FC> feature) {
            feature.setRegistryName(BlightlingsMod.MOD_ID, name);
            return feature;
        }
    }

    public abstract static class CONFIGURED {
        public static final ConfiguredFeature<?, ?> LILY_TREE = UNCONFIGURED.LILY_TREE.withConfiguration(NoFeatureConfig.NO_FEATURE_CONFIG)
                .withPlacement(Features.Placements.HEIGHTMAP_PLACEMENT).withPlacement(Placement.COUNT_EXTRA.configure(new AtSurfaceWithExtraConfig(2, 0.1f, 1)));

        public static final ConfiguredFeature<?, ?> LUMINOUS_SPORE_BLOB = UNCONFIGURED.LUMINOUS_SPORE_BLOB.withConfiguration(new BlockStateFeatureConfig(ModBlocks.LUMINOUS_SOIL.get().getDefaultState()))
                .withPlacement(Features.Placements.HEIGHTMAP_PLACEMENT).func_242732_c(2);

        public static final ConfiguredFeature<?, ?> PATCH_BLIGHT_SPROUTS = Feature.RANDOM_PATCH.withConfiguration(CONFIGS.BLIGHT_SPROUT_CONFIG).withPlacement(Features.Placements.PATCH_PLACEMENT).func_242731_b(7);
        public static final ConfiguredFeature<?, ?> PATCH_BLIGHTSHROOM = Feature.RANDOM_PATCH.withConfiguration(CONFIGS.BLIGHTSHROOM_CONFIG).chance(4).withPlacement(Features.Placements.HEIGHTMAP_PLACEMENT);

        public static final ConfiguredFeature<?, ?> MOON_MONOLITH = UNCONFIGURED.MOON_MONOLITH.withConfiguration(NoFeatureConfig.NO_FEATURE_CONFIG)
                .withPlacement(Features.Placements.HEIGHTMAP_PLACEMENT).chance(250);

        private static void registerAll() {
            registerConfiguredFeature("lily_tree", LILY_TREE);
            registerConfiguredFeature("luminous_spore_blob", LUMINOUS_SPORE_BLOB);
            registerConfiguredFeature("patch_blight_sprouts", PATCH_BLIGHT_SPROUTS);
            registerConfiguredFeature("patch_blightshroom", PATCH_BLIGHTSHROOM);
            registerConfiguredFeature("moon_monolith", MOON_MONOLITH);
        }

        private static void registerConfiguredFeature(String name, ConfiguredFeature<?, ?> configuredFeature) {
            Registry.register(WorldGenRegistries.CONFIGURED_FEATURE, new ResourceLocation(BlightlingsMod.MOD_ID, name), configuredFeature);
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
        CONFIGURED.registerAll();
    }
}
