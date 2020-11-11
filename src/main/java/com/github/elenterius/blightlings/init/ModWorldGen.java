package com.github.elenterius.blightlings.init;

import com.github.elenterius.blightlings.BlightlingsMod;
import com.github.elenterius.blightlings.world.gen.tree.LilyTreeFeature;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityClassification;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.WorldGenRegistries;
import net.minecraft.world.biome.*;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.carver.WorldCarver;
import net.minecraft.world.gen.feature.*;
import net.minecraft.world.gen.placement.AtSurfaceWithExtraConfig;
import net.minecraft.world.gen.placement.Placement;
import net.minecraft.world.gen.surfacebuilders.DefaultSurfaceBuilder;
import net.minecraft.world.gen.surfacebuilders.SurfaceBuilderConfig;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.BiomeManager;
import net.minecraftforge.common.world.BiomeGenerationSettingsBuilder;
import net.minecraftforge.common.world.MobSpawnInfoBuilder;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public abstract class ModWorldGen
{
    public static final DeferredRegister<Biome> BIOME_REGISTRY = DeferredRegister.create(ForgeRegistries.BIOMES, BlightlingsMod.MOD_ID);

    public static final RegistryObject<Biome> BLIGHT_BIOME = BIOME_REGISTRY.register("blight_biome", ModWorldGen::makeBlightBiome);

    public static final ConfiguredFeature<?, ?> LILY_TREE_FEATURE = registerConfiguredFeature(new ResourceLocation(BlightlingsMod.MOD_ID, "lily_tree"),
            registerFeature(new ResourceLocation(BlightlingsMod.MOD_ID, "lily_tree"), new LilyTreeFeature(NoFeatureConfig.field_236558_a_))
                    .withConfiguration(NoFeatureConfig.NO_FEATURE_CONFIG)
                    .withPlacement(Features.Placements.HEIGHTMAP_PLACEMENT)
                    .withPlacement(Placement.COUNT_EXTRA.configure(new AtSurfaceWithExtraConfig(2, 0.1f, 1)))
    );

    private static <FC extends IFeatureConfig, F extends Feature<FC>> ConfiguredFeature<FC, F> registerConfiguredFeature(ResourceLocation registryName, ConfiguredFeature<FC, F> configuredFeature) {
        Registry.register(WorldGenRegistries.CONFIGURED_FEATURE, registryName, configuredFeature);
        return configuredFeature;
    }

    private static <FC extends IFeatureConfig> Feature<FC> registerFeature(ResourceLocation registryName, Feature<FC> configuredFeature) {
        configuredFeature.setRegistryName(registryName);
        ForgeRegistries.FEATURES.register(configuredFeature);
        return configuredFeature;
    }

    public static void setupBiomes() {
        RegistryKey<Biome> key = RegistryKey.getOrCreateKey(Registry.BIOME_KEY, new ResourceLocation(BlightlingsMod.MOD_ID, "blight_biome"));
        BiomeDictionary.addTypes(key, BiomeDictionary.Type.RARE, BiomeDictionary.Type.MAGICAL, BiomeDictionary.Type.SPOOKY, BiomeDictionary.Type.JUNGLE, BiomeDictionary.Type.OVERWORLD);
        BiomeManager.addBiome(BiomeManager.BiomeType.WARM, new BiomeManager.BiomeEntry(key, 10));
    }

    private static Biome makeBlightBiome() {
        SurfaceBuilderConfig blightSoilConfig = new SurfaceBuilderConfig(ModBlocks.INFERTILE_SOIL.get().getDefaultState(), ModBlocks.INFERTILE_SOIL.get().getDefaultState(), Blocks.GRAVEL.getDefaultState());

        Biome biome = new Biome.Builder()
                .scale(0.1f)
                .temperature(0.95F)
                .category(Biome.Category.JUNGLE)
                .depth(0.35f)
                .precipitation(Biome.RainType.RAIN)
                .downfall(0.25f)
                .withMobSpawnSettings(
                        new MobSpawnInfoBuilder(MobSpawnInfo.EMPTY)
                                .withCreatureSpawnProbability(0.95f)
                                .withSpawner(EntityClassification.MONSTER, new MobSpawnInfo.Spawners(ModEntityTypes.BLOBLING.get(), 90, 3, 6))
                                .withSpawner(EntityClassification.MONSTER, new MobSpawnInfo.Spawners(ModEntityTypes.BROOD_MOTHER.get(), 80, 1, 2))
                                .copy()
                )
                .setEffects(
                        new BiomeAmbience.Builder()
                                .setFogColor(0xcbc3f4)
                                .withFoliageColor(0xff823278)
                                .withGrassColor(0xff823278)
                                .setWaterColor(0xff9e4f9e)
                                .setWaterFogColor(0xff9e4f9e)
                                .withSkyColor(0)
                                .setMoodSound(MoodSoundAmbience.DEFAULT_CAVE)
                                .build()
                )
                .withGenerationSettings(
                        new BiomeGenerationSettingsBuilder(BiomeGenerationSettings.DEFAULT_SETTINGS)
                                .withFeature(GenerationStage.Decoration.UNDERGROUND_ORES, Features.ORE_GOLD)
                                .withFeature(GenerationStage.Decoration.UNDERGROUND_ORES, Features.ORE_IRON)
                                .withFeature(GenerationStage.Decoration.UNDERGROUND_ORES, Features.ORE_COAL)
                                .withFeature(GenerationStage.Decoration.UNDERGROUND_ORES, Features.ORE_DIAMOND)
                                .withFeature(GenerationStage.Decoration.LAKES, Features.LAKE_WATER)
                                .withFeature(GenerationStage.Decoration.VEGETAL_DECORATION, LILY_TREE_FEATURE)
                                .withFeature(GenerationStage.Decoration.LOCAL_MODIFICATIONS, Features.FOREST_ROCK)
                                .withCarver(GenerationStage.Carving.AIR, WorldCarver.CAVE.func_242761_a(new ProbabilityConfig(0.5f)))
                                .withCarver(GenerationStage.Carving.AIR, WorldCarver.CANYON.func_242761_a(new ProbabilityConfig(0.7f)))
                                .withSurfaceBuilder(DefaultSurfaceBuilder.DEFAULT.func_242929_a(blightSoilConfig))
                                .build()
                )
                .build();

        return biome;
    }
}
