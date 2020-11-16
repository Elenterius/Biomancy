package com.github.elenterius.blightlings.init;

import com.github.elenterius.blightlings.BlightlingsMod;
import com.github.elenterius.blightlings.world.gen.tree.LilyTreeFeature;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityClassification;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.WorldGenRegistries;
import net.minecraft.world.biome.*;
import net.minecraft.world.chunk.IChunk;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.carver.WorldCarver;
import net.minecraft.world.gen.feature.*;
import net.minecraft.world.gen.placement.AtSurfaceWithExtraConfig;
import net.minecraft.world.gen.placement.Placement;
import net.minecraft.world.gen.surfacebuilders.ConfiguredSurfaceBuilder;
import net.minecraft.world.gen.surfacebuilders.ConfiguredSurfaceBuilders;
import net.minecraft.world.gen.surfacebuilders.SurfaceBuilder;
import net.minecraft.world.gen.surfacebuilders.SurfaceBuilderConfig;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.BiomeManager;
import net.minecraftforge.common.world.MobSpawnInfoBuilder;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.ForgeRegistry;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import java.util.Random;

public abstract class ModWorldGen
{
    public static final DeferredRegister<Biome> BIOME_REGISTRY = DeferredRegister.create(ForgeRegistries.BIOMES, BlightlingsMod.MOD_ID);

    public static final RegistryObject<Biome> BLIGHT_BIOME = BIOME_REGISTRY.register("blight_biome", ModWorldGen::makeBlightBiome);
    public static int BLIGHT_BIOME_ID;
    public static final RegistryObject<Biome> BLIGHT_BIOME_OUTER_EDGE = BIOME_REGISTRY.register("blight_biome_outer_edge", ModWorldGen::makeBlightBiomeEdge);
    public static int BLIGHT_BIOME_OUTER_EDGE_ID;
    public static final RegistryObject<Biome> BLIGHT_BIOME_INNER_EDGE = BIOME_REGISTRY.register("blight_biome_inner_edge", ModWorldGen::makeBlightBiomeEdge);
    public static int BLIGHT_BIOME_INNER_EDGE_ID;

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
        Marker marker = MarkerManager.getMarker("Biome Post-Setup");
        BlightlingsMod.LOGGER.info(marker, "doing important biome stuff...");

        ResourceLocation registryId = BLIGHT_BIOME.getId();
        BLIGHT_BIOME_ID = ((ForgeRegistry<Biome>) ForgeRegistries.BIOMES).getID(BLIGHT_BIOME.getId());
        BlightlingsMod.LOGGER.debug(marker, String.format("processing biome with registry key %s and id %d", registryId, BLIGHT_BIOME_ID));
        RegistryKey<Biome> key = RegistryKey.getOrCreateKey(Registry.BIOME_KEY, registryId);
        BiomeDictionary.addTypes(key, BiomeDictionary.Type.RARE, BiomeDictionary.Type.MAGICAL, BiomeDictionary.Type.SPOOKY, BiomeDictionary.Type.JUNGLE, BiomeDictionary.Type.OVERWORLD);
        BiomeManager.addBiome(BiomeManager.BiomeType.WARM, new BiomeManager.BiomeEntry(key, 20)); //make biome spawnable in the Overworld

        registryId = BLIGHT_BIOME_OUTER_EDGE.getId();
        BLIGHT_BIOME_OUTER_EDGE_ID = ((ForgeRegistry<Biome>) ForgeRegistries.BIOMES).getID(BLIGHT_BIOME_OUTER_EDGE.getId());
        BlightlingsMod.LOGGER.debug(marker, String.format("processing biome with registry key %s and id %d", registryId, BLIGHT_BIOME_OUTER_EDGE_ID));
        key = RegistryKey.getOrCreateKey(Registry.BIOME_KEY, registryId);
        BiomeDictionary.addTypes(key, BiomeDictionary.Type.RARE, BiomeDictionary.Type.MAGICAL, BiomeDictionary.Type.SPOOKY, BiomeDictionary.Type.JUNGLE, BiomeDictionary.Type.OVERWORLD);

        registryId = BLIGHT_BIOME_INNER_EDGE.getId();
        BLIGHT_BIOME_INNER_EDGE_ID = ((ForgeRegistry<Biome>) ForgeRegistries.BIOMES).getID(BLIGHT_BIOME_INNER_EDGE.getId());
        BlightlingsMod.LOGGER.debug(marker, String.format("processing biome with registry key %s and id %d", registryId, BLIGHT_BIOME_INNER_EDGE_ID));
        key = RegistryKey.getOrCreateKey(Registry.BIOME_KEY, registryId);
        BiomeDictionary.addTypes(key, BiomeDictionary.Type.RARE, BiomeDictionary.Type.MAGICAL, BiomeDictionary.Type.SPOOKY, BiomeDictionary.Type.JUNGLE, BiomeDictionary.Type.OVERWORLD);
    }

    private static Biome makeBlightBiome() {
        SurfaceBuilderConfig blightSoilConfig = new SurfaceBuilderConfig(ModBlocks.INFERTILE_SOIL.get().getDefaultState(), ModBlocks.INFERTILE_SOIL.get().getDefaultState(), ModBlocks.INFERTILE_SOIL.get().getDefaultState());
        SurfaceBuilderConfig grassBlightSoilConfig = new SurfaceBuilderConfig(Blocks.GRASS_BLOCK.getDefaultState(), ModBlocks.INFERTILE_SOIL.get().getDefaultState(), ModBlocks.INFERTILE_SOIL.get().getDefaultState());
        SurfaceBuilder<SurfaceBuilderConfig> blightSurfaceBuilder = Registry.register(Registry.SURFACE_BUILDER, "blight_surface", new SurfaceBuilder<SurfaceBuilderConfig>(SurfaceBuilderConfig.field_237203_a_)
        {
            @Override
            public void buildSurface(Random random, IChunk chunkIn, Biome biomeIn, int x, int z, int startHeight, double noise, BlockState defaultBlock, BlockState defaultFluid, int seaLevel, long seed, SurfaceBuilderConfig config) {
                seaLevel = 13;

                if (noise > 1.75D) {
                    SurfaceBuilder.DEFAULT.buildSurface(random, chunkIn, biomeIn, x, z, startHeight, noise, defaultBlock, defaultFluid, seaLevel, seed, grassBlightSoilConfig);
                }
                else if (noise > -1.45D) {
                    SurfaceBuilder.DEFAULT.buildSurface(random, chunkIn, biomeIn, x, z, startHeight, noise, defaultBlock, defaultFluid, seaLevel, seed, blightSoilConfig);
                }
                else {
                    SurfaceBuilder.DEFAULT.buildSurface(random, chunkIn, biomeIn, x, z, startHeight, noise, defaultBlock, defaultFluid, seaLevel, seed, SurfaceBuilder.field_237187_R_);
                }
            }
        });
        ConfiguredSurfaceBuilder<SurfaceBuilderConfig> configuredBlightSurface = WorldGenRegistries.register(
                WorldGenRegistries.CONFIGURED_SURFACE_BUILDER,
                new ResourceLocation(BlightlingsMod.MOD_ID, "blight_surface"),
                blightSurfaceBuilder.func_242929_a(blightSoilConfig)
        );

        BiomeGenerationSettings.Builder generationSettings = (new BiomeGenerationSettings.Builder()).withSurfaceBuilder(configuredBlightSurface)
                .withFeature(GenerationStage.Decoration.UNDERGROUND_ORES, Features.ORE_GOLD)
                .withFeature(GenerationStage.Decoration.UNDERGROUND_ORES, Features.ORE_IRON)
                .withFeature(GenerationStage.Decoration.UNDERGROUND_ORES, Features.ORE_COAL)
                .withFeature(GenerationStage.Decoration.UNDERGROUND_ORES, Features.ORE_DIAMOND)
                .withFeature(GenerationStage.Decoration.LAKES, Features.LAKE_WATER)
                .withFeature(GenerationStage.Decoration.VEGETAL_DECORATION, LILY_TREE_FEATURE)
                .withFeature(GenerationStage.Decoration.LOCAL_MODIFICATIONS, Features.FOREST_ROCK)
                .withFeature(GenerationStage.Decoration.VEGETAL_DECORATION, Features.SPRING_WATER)
                .withFeature(GenerationStage.Decoration.LOCAL_MODIFICATIONS, Features.BASALT_PILLAR)
                .withCarver(GenerationStage.Carving.AIR, WorldCarver.CAVE.func_242761_a(new ProbabilityConfig(0.42f)))
                .withCarver(GenerationStage.Carving.AIR, WorldCarver.CANYON.func_242761_a(new ProbabilityConfig(0.25f)))
//                .withStructure(StructureFeatures.RUINED_PORTAL)
                ;

        return new Biome.Builder()
                .scale(0.35f)
                .temperature(0.95F)
                .category(Biome.Category.JUNGLE)
                .depth(-1.8F)
                .precipitation(Biome.RainType.RAIN)
                .downfall(0.25f)
                .withMobSpawnSettings(
                        new MobSpawnInfoBuilder(MobSpawnInfo.EMPTY)
                                .withCreatureSpawnProbability(0.99f)
                                .withSpawner(EntityClassification.MONSTER, new MobSpawnInfo.Spawners(ModEntityTypes.BLOBLING.get(), 100, 5, 8))
                                .withSpawner(EntityClassification.MONSTER, new MobSpawnInfo.Spawners(ModEntityTypes.BROOD_MOTHER.get(), 80, 1, 2))
                                .copy()
                )
                .setEffects(
                        new BiomeAmbience.Builder()
                                .setFogColor(0xddc2247c)
                                .withFoliageColor(0xff823278)
                                .withGrassColor(0xff823278)
                                .setWaterColor(0xff9e4f9e)
                                .setWaterFogColor(0xff9e4f9e)
                                .withSkyColor(0xffb178b1)
                                .setMoodSound(MoodSoundAmbience.DEFAULT_CAVE)
                                .setParticle(new ParticleEffectAmbience(ParticleTypes.WARPED_SPORE, 0.02825F))
                                .build()
                )
                .withGenerationSettings(generationSettings.build())
                .build();
    }

    private static Biome makeBlightBiomeEdge() {
        BiomeGenerationSettings.Builder generationSettings = (new BiomeGenerationSettings.Builder()).withSurfaceBuilder(ConfiguredSurfaceBuilders.field_244178_j)
                .withFeature(GenerationStage.Decoration.UNDERGROUND_ORES, Features.ORE_GOLD)
                .withFeature(GenerationStage.Decoration.UNDERGROUND_ORES, Features.ORE_IRON)
                .withFeature(GenerationStage.Decoration.UNDERGROUND_ORES, Features.ORE_COAL)
                .withFeature(GenerationStage.Decoration.UNDERGROUND_ORES, Features.ORE_DIAMOND)
                .withFeature(GenerationStage.Decoration.VEGETAL_DECORATION, LILY_TREE_FEATURE)
                .withFeature(GenerationStage.Decoration.VEGETAL_DECORATION, Features.PATCH_GRASS_NORMAL)
                .withFeature(GenerationStage.Decoration.VEGETAL_DECORATION, Features.TREES_JUNGLE_EDGE)
                .withFeature(GenerationStage.Decoration.VEGETAL_DECORATION, Features.SPRING_WATER)
                .withCarver(GenerationStage.Carving.AIR, WorldCarver.CAVE.func_242761_a(new ProbabilityConfig(0.65f)))
                .withCarver(GenerationStage.Carving.AIR, WorldCarver.CANYON.func_242761_a(new ProbabilityConfig(0.025f)));

        return new Biome.Builder()
                .scale(0.1f)
                .temperature(0.75F)
                .category(Biome.Category.JUNGLE)
                .depth(0.3F)
                .precipitation(Biome.RainType.RAIN)
                .downfall(0.35f)
                .withMobSpawnSettings(
                        new MobSpawnInfoBuilder(MobSpawnInfo.EMPTY)
                                .withCreatureSpawnProbability(0.80f)
                                .withSpawner(EntityClassification.MONSTER, new MobSpawnInfo.Spawners(ModEntityTypes.BLOBLING.get(), 90, 3, 6))
                                .withSpawner(EntityClassification.MONSTER, new MobSpawnInfo.Spawners(ModEntityTypes.BROOD_MOTHER.get(), 70, 1, 1))
                                .copy()
                )
                .setEffects(
                        new BiomeAmbience.Builder()
                                .setFogColor(0xddc2247c)
                                .withFoliageColor(0xff823278)
                                .withGrassColor(0xff823278)
                                .setWaterColor(0xff9e4f9e)
                                .setWaterFogColor(0xff9e4f9e)
                                .withSkyColor(0xffb178b1)
                                .setMoodSound(MoodSoundAmbience.DEFAULT_CAVE)
                                .setParticle(new ParticleEffectAmbience(ParticleTypes.WARPED_SPORE, 0.02825F))
                                .build()
                )
                .withGenerationSettings(generationSettings.build())
                .build();
    }
}
