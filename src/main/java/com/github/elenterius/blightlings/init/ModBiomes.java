package com.github.elenterius.blightlings.init;

import com.github.elenterius.blightlings.BlightlingsMod;
import net.minecraft.entity.EntityClassification;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.*;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.carver.WorldCarver;
import net.minecraft.world.gen.feature.Features;
import net.minecraft.world.gen.feature.ProbabilityConfig;
import net.minecraft.world.gen.surfacebuilders.ConfiguredSurfaceBuilders;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.BiomeManager;
import net.minecraftforge.common.world.MobSpawnInfoBuilder;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.ForgeRegistry;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import java.util.Objects;

@Mod.EventBusSubscriber(modid = BlightlingsMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public abstract class ModBiomes
{
    //we can't use deferred feature & deferred biome registry together because the features may not be registered yet when building biome settings
//    public static final DeferredRegister<Biome> BIOME_REGISTRY = DeferredRegister.create(ForgeRegistries.BIOMES, BlightlingsMod.MOD_ID);

    public static Biome BLIGHT_BIOME;
    public static Biome BLIGHT_BIOME_OUTER_EDGE;
    public static Biome BLIGHT_BIOME_INNER_EDGE;
    public static int BLIGHT_BIOME_ID;
    public static int BLIGHT_BIOME_OUTER_EDGE_ID;
    public static int BLIGHT_BIOME_INNER_EDGE_ID;

    @SubscribeEvent
    public static void onBiomeRegistry(RegistryEvent.Register<Biome> event) {
        BlightlingsMod.LOGGER.info("registering biomes");
        event.getRegistry().registerAll(
                BLIGHT_BIOME = ModBiomes.makeBlightBiome().setRegistryName(BlightlingsMod.MOD_ID, "blight_biome"),
                BLIGHT_BIOME_OUTER_EDGE = ModBiomes.makeBlightBiomeOuterEdge().setRegistryName(BlightlingsMod.MOD_ID, "blight_biome_outer_edge"),
                BLIGHT_BIOME_INNER_EDGE = ModBiomes.makeBlightBiomeInnerEdge().setRegistryName(BlightlingsMod.MOD_ID, "blight_biome_inner_edge")
        );
    }

    public static void onPostSetupBiomes() {
        Marker marker = MarkerManager.getMarker("Biome Post-Setup");
        BlightlingsMod.LOGGER.info(marker, "doing important biome stuff...");

        ResourceLocation registryId = BLIGHT_BIOME.getRegistryName();
        BLIGHT_BIOME_ID = ((ForgeRegistry<Biome>) ForgeRegistries.BIOMES).getID(registryId);
        BlightlingsMod.LOGGER.debug(marker, String.format("processing biome with registry key %s and id %d", registryId, BLIGHT_BIOME_ID));
        RegistryKey<Biome> key = RegistryKey.getOrCreateKey(Registry.BIOME_KEY, Objects.requireNonNull(registryId));
        BiomeDictionary.addTypes(key, BiomeDictionary.Type.RARE, BiomeDictionary.Type.MAGICAL, BiomeDictionary.Type.SPOOKY, BiomeDictionary.Type.JUNGLE, BiomeDictionary.Type.OVERWORLD);
        BiomeManager.addBiome(BiomeManager.BiomeType.WARM, new BiomeManager.BiomeEntry(key, 20)); //make biome spawnable in the Overworld

        registryId = BLIGHT_BIOME_OUTER_EDGE.getRegistryName();
        BLIGHT_BIOME_OUTER_EDGE_ID = ((ForgeRegistry<Biome>) ForgeRegistries.BIOMES).getID(registryId);
        BlightlingsMod.LOGGER.debug(marker, String.format("processing biome with registry key %s and id %d", registryId, BLIGHT_BIOME_OUTER_EDGE_ID));
        key = RegistryKey.getOrCreateKey(Registry.BIOME_KEY, Objects.requireNonNull(registryId));
        BiomeDictionary.addTypes(key, BiomeDictionary.Type.RARE, BiomeDictionary.Type.MAGICAL, BiomeDictionary.Type.SPOOKY, BiomeDictionary.Type.JUNGLE, BiomeDictionary.Type.OVERWORLD);

        registryId = BLIGHT_BIOME_INNER_EDGE.getRegistryName();
        BLIGHT_BIOME_INNER_EDGE_ID = ((ForgeRegistry<Biome>) ForgeRegistries.BIOMES).getID(registryId);
        BlightlingsMod.LOGGER.debug(marker, String.format("processing biome with registry key %s and id %d", registryId, BLIGHT_BIOME_INNER_EDGE_ID));
        key = RegistryKey.getOrCreateKey(Registry.BIOME_KEY, Objects.requireNonNull(registryId));
        BiomeDictionary.addTypes(key, BiomeDictionary.Type.RARE, BiomeDictionary.Type.MAGICAL, BiomeDictionary.Type.SPOOKY, BiomeDictionary.Type.JUNGLE, BiomeDictionary.Type.OVERWORLD);
    }

    private static Biome makeBlightBiome() {
        BiomeGenerationSettings.Builder generationSettings = (new BiomeGenerationSettings.Builder()).withSurfaceBuilder(ModSurfaceBuilders.CONFIGURED.BLIGHT_SURFACE_BUILDER)
                .withFeature(GenerationStage.Decoration.UNDERGROUND_ORES, Features.ORE_IRON)
                .withFeature(GenerationStage.Decoration.UNDERGROUND_ORES, Features.ORE_COAL)
                .withFeature(GenerationStage.Decoration.UNDERGROUND_ORES, Features.ORE_DIAMOND)
                .withFeature(GenerationStage.Decoration.UNDERGROUND_ORES, Features.ORE_EMERALD)
                .withFeature(GenerationStage.Decoration.UNDERGROUND_ORES, Features.ORE_ANDESITE)
                .withFeature(GenerationStage.Decoration.LAKES, Features.LAKE_WATER)
                .withFeature(GenerationStage.Decoration.VEGETAL_DECORATION, Features.SPRING_WATER)
                .withFeature(GenerationStage.Decoration.VEGETAL_DECORATION, ModFeatures.CONFIGURED.LILY_TREE)
                .withFeature(GenerationStage.Decoration.VEGETAL_DECORATION, ModFeatures.CONFIGURED.PATCH_BLIGHT_SPROUTS)
                .withFeature(GenerationStage.Decoration.VEGETAL_DECORATION, ModFeatures.CONFIGURED.PATCH_BLIGHTSHROOM)
                .withFeature(GenerationStage.Decoration.LOCAL_MODIFICATIONS, ModFeatures.CONFIGURED.LUMINOUS_SPORE_BLOB)
                .withFeature(GenerationStage.Decoration.LOCAL_MODIFICATIONS, Features.BASALT_PILLAR)
                .withCarver(GenerationStage.Carving.AIR, WorldCarver.CAVE.func_242761_a(new ProbabilityConfig(0.425f)))
                .withCarver(GenerationStage.Carving.AIR, WorldCarver.CANYON.func_242761_a(new ProbabilityConfig(0.225f)))
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
                                .setFogColor(0xccc2247c)
                                .withFoliageColor(0xff823278)
                                .withGrassColor(0xff823278)
                                .setWaterColor(0xff9e4f9e)
                                .setWaterFogColor(0xff9e4f9e)
                                .withSkyColor(0xccb178b1)
                                .setMoodSound(MoodSoundAmbience.DEFAULT_CAVE)
                                .setParticle(new ParticleEffectAmbience(ParticleTypes.WARPED_SPORE, 0.029F))
                                .build()
                )
                .withGenerationSettings(generationSettings.build())
                .build();
    }

    private static Biome makeBlightBiomeInnerEdge() {
        BiomeGenerationSettings.Builder generationSettings = (new BiomeGenerationSettings.Builder()).withSurfaceBuilder(ModSurfaceBuilders.CONFIGURED.BLIGHT_GRASS_SURFACE_BUILDER)
                .withFeature(GenerationStage.Decoration.UNDERGROUND_ORES, Features.ORE_GOLD)
                .withFeature(GenerationStage.Decoration.UNDERGROUND_ORES, Features.ORE_IRON)
                .withFeature(GenerationStage.Decoration.UNDERGROUND_ORES, Features.ORE_COAL)
                .withFeature(GenerationStage.Decoration.UNDERGROUND_ORES, Features.ORE_DIAMOND)
                .withFeature(GenerationStage.Decoration.VEGETAL_DECORATION, ModFeatures.CONFIGURED.LILY_TREE)
                .withFeature(GenerationStage.Decoration.VEGETAL_DECORATION, ModFeatures.CONFIGURED.PATCH_BLIGHT_SPROUTS)
                .withFeature(GenerationStage.Decoration.VEGETAL_DECORATION, Features.PATCH_GRASS_NORMAL)
                .withFeature(GenerationStage.Decoration.VEGETAL_DECORATION, Features.TREES_JUNGLE_EDGE)
                .withFeature(GenerationStage.Decoration.VEGETAL_DECORATION, Features.SPRING_WATER)
                .withCarver(GenerationStage.Carving.AIR, WorldCarver.CAVE.func_242761_a(new ProbabilityConfig(0.7f)))
                .withCarver(GenerationStage.Carving.AIR, WorldCarver.CANYON.func_242761_a(new ProbabilityConfig(0.015f)));

        return new Biome.Builder()
                .scale(0.4f)
                .temperature(0.55F)
                .category(Biome.Category.JUNGLE)
                .depth(1.8F)
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
                                .setFogColor(0xaac2247c)
                                .withFoliageColor(0xdd823278)
                                .withGrassColor(0xdd823278)
                                .setWaterColor(0xdd9e4f9e)
                                .setWaterFogColor(0xdd9e4f9e)
                                .withSkyColor(0xaab178b1)
                                .setMoodSound(MoodSoundAmbience.DEFAULT_CAVE)
                                .setParticle(new ParticleEffectAmbience(ParticleTypes.WARPED_SPORE, 0.018F))
                                .build()
                )
                .withGenerationSettings(generationSettings.build())
                .build();
    }

    private static Biome makeBlightBiomeOuterEdge() {
        BiomeGenerationSettings.Builder generationSettings = (new BiomeGenerationSettings.Builder()).withSurfaceBuilder(ConfiguredSurfaceBuilders.field_244178_j)
                .withFeature(GenerationStage.Decoration.UNDERGROUND_ORES, Features.ORE_GOLD)
                .withFeature(GenerationStage.Decoration.UNDERGROUND_ORES, Features.ORE_IRON)
                .withFeature(GenerationStage.Decoration.UNDERGROUND_ORES, Features.ORE_COAL)
                .withFeature(GenerationStage.Decoration.UNDERGROUND_ORES, Features.ORE_DIAMOND)
                .withFeature(GenerationStage.Decoration.VEGETAL_DECORATION, ModFeatures.CONFIGURED.LILY_TREE)
                .withFeature(GenerationStage.Decoration.VEGETAL_DECORATION, Features.PATCH_GRASS_NORMAL)
                .withFeature(GenerationStage.Decoration.VEGETAL_DECORATION, Features.TREES_JUNGLE_EDGE)
                .withFeature(GenerationStage.Decoration.VEGETAL_DECORATION, Features.SPRING_WATER)
                .withCarver(GenerationStage.Carving.AIR, WorldCarver.CAVE.func_242761_a(new ProbabilityConfig(0.65f)))
                .withCarver(GenerationStage.Carving.AIR, WorldCarver.CANYON.func_242761_a(new ProbabilityConfig(0.025f)));

        return new Biome.Builder()
                .scale(0.35f)
                .temperature(0.75F)
                .category(Biome.Category.JUNGLE)
                .depth(0.5F)
                .precipitation(Biome.RainType.RAIN)
                .downfall(0.35f)
                .withMobSpawnSettings(
                        new MobSpawnInfoBuilder(MobSpawnInfo.EMPTY)
                                .withCreatureSpawnProbability(0.70f)
                                .withSpawner(EntityClassification.MONSTER, new MobSpawnInfo.Spawners(ModEntityTypes.BLOBLING.get(), 90, 3, 6))
                                .withSpawner(EntityClassification.MONSTER, new MobSpawnInfo.Spawners(ModEntityTypes.BROOD_MOTHER.get(), 70, 1, 1))
                                .copy()
                )
                .setEffects(
                        new BiomeAmbience.Builder()
                                .setFogColor(0x88c2247c)
                                .withFoliageColor(0xbb823278)
                                .withGrassColor(0xbb823278)
                                .setWaterColor(0xbb9e4f9e)
                                .setWaterFogColor(0xbb9e4f9e)
                                .withSkyColor(0x88b178b1)
                                .setMoodSound(MoodSoundAmbience.DEFAULT_CAVE)
                                .setParticle(new ParticleEffectAmbience(ParticleTypes.WARPED_SPORE, 0.0085F))
                                .build()
                )
                .withGenerationSettings(generationSettings.build())
                .build();
    }
}
