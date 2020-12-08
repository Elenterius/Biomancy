package com.github.elenterius.blightlings.init;

import com.github.elenterius.blightlings.BlightlingsMod;
import com.github.elenterius.blightlings.entity.BeetlingEntity;
import com.github.elenterius.blightlings.entity.BloblingEntity;
import com.github.elenterius.blightlings.entity.BroodmotherEntity;
import com.github.elenterius.blightlings.entity.PotionBeetleEntity;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntitySpawnPlacementRegistry;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.attributes.GlobalEntityTypeAttributes;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Difficulty;
import net.minecraft.world.IServerWorld;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.Heightmap;
import net.minecraftforge.event.world.BiomeLoadingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.logging.log4j.MarkerManager;

import java.util.Random;

@Mod.EventBusSubscriber(modid = BlightlingsMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public abstract class ModEntityTypes
{
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPE_REGISTRY = DeferredRegister.create(ForgeRegistries.ENTITIES, BlightlingsMod.MOD_ID);

    public static final RegistryObject<EntityType<BloblingEntity>> BLOBLING = ENTITY_TYPE_REGISTRY.register("blobling", () -> EntityType.Builder.create(BloblingEntity::new, EntityClassification.MONSTER).size(0.4F, 0.35F).build(BlightlingsMod.MOD_ID + ":" + "blobling"));
    public static final RegistryObject<EntityType<BroodmotherEntity>> BROOD_MOTHER = ENTITY_TYPE_REGISTRY.register("brood_mother", () -> EntityType.Builder.create(BroodmotherEntity::new, EntityClassification.MONSTER).size(1.6F, 0.7F).build(BlightlingsMod.MOD_ID + ":" + "brood_mother"));
    public static final RegistryObject<EntityType<BeetlingEntity>> BEETLING = ENTITY_TYPE_REGISTRY.register("beetling", () -> EntityType.Builder.create(BeetlingEntity::new, EntityClassification.CREATURE).size(0.475F, 0.34F).build(BlightlingsMod.MOD_ID + ":" + "beetling"));
    public static final RegistryObject<EntityType<PotionBeetleEntity>> POTION_BEETLE = ENTITY_TYPE_REGISTRY.register("potion_beetle", () -> EntityType.Builder.create(PotionBeetleEntity::new, EntityClassification.CREATURE).size(0.475F, 0.34F).build(BlightlingsMod.MOD_ID + ":" + "potion_beetle"));

    static void onPostSetup() {
        BlightlingsMod.LOGGER.info(MarkerManager.getMarker("ENTITIES"), "configuring entities");

        GlobalEntityTypeAttributes.put(BLOBLING.get(), BloblingEntity.createAttributes().create());
        GlobalEntityTypeAttributes.put(BROOD_MOTHER.get(), BroodmotherEntity.createAttributes().create());
        GlobalEntityTypeAttributes.put(BEETLING.get(), BeetlingEntity.createAttributes().create());
        GlobalEntityTypeAttributes.put(POTION_BEETLE.get(), PotionBeetleEntity.createAttributes().create());

        EntitySpawnPlacementRegistry.register(BLOBLING.get(), EntitySpawnPlacementRegistry.PlacementType.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, ModEntityTypes::canMonsterSpawn);
        EntitySpawnPlacementRegistry.register(BROOD_MOTHER.get(), EntitySpawnPlacementRegistry.PlacementType.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, ModEntityTypes::canMonsterSpawn);
        EntitySpawnPlacementRegistry.register(BEETLING.get(), EntitySpawnPlacementRegistry.PlacementType.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, ModEntityTypes::canAnimalSpawn);
    }

    public static boolean canMonsterSpawn(EntityType<? extends MonsterEntity> type, IServerWorld worldIn, SpawnReason reason, BlockPos pos, Random randomIn) {
        return worldIn.getDifficulty() != Difficulty.PEACEFUL && MonsterEntity.canSpawnOn(type, worldIn, reason, pos, randomIn);
    }

    public static boolean canAnimalSpawn(EntityType<? extends AnimalEntity> type, IWorld worldIn, SpawnReason reason, BlockPos pos, Random randomIn) {
        BlockState blockState = worldIn.getBlockState(pos.down());
        return (blockState.isIn(Blocks.GRASS_BLOCK) || blockState.isIn(ModBlocks.INFERTILE_SOIL.get())) && worldIn.getLightSubtracted(pos, 0) > 6;
    }

    @SubscribeEvent
    public static void registerBiomeSpawns(final BiomeLoadingEvent event) {
//        if (event.getCategory() == Biome.Category.SWAMP || event.getName().equals(Biomes.DARK_FOREST_HILLS.getRegistryName()) || event.getName().equals(Biomes.DARK_FOREST.getRegistryName())) {
//            event.getSpawns().getSpawner(EntityClassification.MONSTER).add(new MobSpawnInfo.Spawners(BLOBLING.get(), 200, 3, 6));
//            event.getSpawns().getSpawner(EntityClassification.MONSTER).add(new MobSpawnInfo.Spawners(BROOD_MOTHER.get(), 120, 1, 2));
//        }
    }
}
