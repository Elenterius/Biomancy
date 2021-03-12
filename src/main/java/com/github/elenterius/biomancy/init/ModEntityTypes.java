package com.github.elenterius.biomancy.init;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.entity.BeetlingEntity;
import com.github.elenterius.biomancy.entity.aberration.FailedSheepEntity;
import com.github.elenterius.biomancy.entity.aberration.FleshBlobEntity;
import com.github.elenterius.biomancy.entity.golem.FleshkinEntity;
import com.github.elenterius.biomancy.entity.golem.MasonBeetleEntity;
import com.github.elenterius.biomancy.entity.golem.PotionBeetleEntity;
import com.github.elenterius.biomancy.entity.hybrid.BloblingEntity;
import com.github.elenterius.biomancy.entity.hybrid.CrocospiderEntity;
import com.github.elenterius.biomancy.entity.mutation.ChromaSheepEntity;
import com.github.elenterius.biomancy.entity.mutation.SilkyWoolSheepEntity;
import com.github.elenterius.biomancy.entity.mutation.ThickWoolSheepEntity;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.attributes.GlobalEntityTypeAttributes;
import net.minecraft.entity.passive.SheepEntity;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.logging.log4j.MarkerManager;

@Mod.EventBusSubscriber(modid = BiomancyMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class ModEntityTypes {
	public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(ForgeRegistries.ENTITIES, BiomancyMod.MOD_ID);

	public static final RegistryObject<EntityType<FleshBlobEntity>> FLESH_BLOB = ENTITIES.register("flesh_blob", () -> EntityType.Builder.create(FleshBlobEntity::new, EntityClassification.CREATURE).size(0.75F, 0.75F).build(BiomancyMod.MOD_ID + ":" + "flesh_blob"));

	//GMOs
	public static final RegistryObject<EntityType<ChromaSheepEntity>> CHROMA_SHEEP = ENTITIES.register("chroma_sheep", () -> EntityType.Builder.create(ChromaSheepEntity::new, EntityClassification.CREATURE).size(0.9f, 1.3f).trackingRange(10).build(BiomancyMod.MOD_ID + ":" + "chroma_sheep"));
	public static final RegistryObject<EntityType<SilkyWoolSheepEntity>> SILKY_WOOL_SHEEP = ENTITIES.register("silky_wool_sheep", () -> EntityType.Builder.create(SilkyWoolSheepEntity::new, EntityClassification.CREATURE).size(0.9f, 1.3f).trackingRange(10).build(BiomancyMod.MOD_ID + ":" + "silky_wool_sheep"));
	public static final RegistryObject<EntityType<ThickWoolSheepEntity>> THICK_WOOL_SHEEP = ENTITIES.register("thick_wool_sheep", () -> EntityType.Builder.create(ThickWoolSheepEntity::new, EntityClassification.CREATURE).size(0.9f, 1.3f).trackingRange(10).build(BiomancyMod.MOD_ID + ":" + "thick_wool_sheep"));
	public static final RegistryObject<EntityType<FailedSheepEntity>> FAILED_SHEEP = ENTITIES.register("failed_sheep", () -> EntityType.Builder.create(FailedSheepEntity::new, EntityClassification.CREATURE).size(0.9f, 1.3f).trackingRange(10).build(BiomancyMod.MOD_ID + ":" + "failed_sheep"));

	//Golems
	public static final RegistryObject<EntityType<FleshkinEntity>> FLESHKIN = ENTITIES.register("fleshkin", () -> EntityType.Builder.create(FleshkinEntity::new, EntityClassification.MONSTER).size(0.6f, 1.95f).trackingRange(10).build(BiomancyMod.MOD_ID + ":" + "fleshkin"));

	public static final RegistryObject<EntityType<BloblingEntity>> BLOBLING = ENTITIES.register("blobling", () -> EntityType.Builder.create(BloblingEntity::new, EntityClassification.MONSTER).size(0.4F, 0.35F).build(BiomancyMod.MOD_ID + ":" + "blobling"));
	public static final RegistryObject<EntityType<CrocospiderEntity>> BROOD_MOTHER = ENTITIES.register("brood_mother", () -> EntityType.Builder.create(CrocospiderEntity::new, EntityClassification.MONSTER).size(1.6F, 0.7F).build(BiomancyMod.MOD_ID + ":" + "brood_mother"));
	public static final RegistryObject<EntityType<BeetlingEntity>> BEETLING = ENTITIES.register("beetling", () -> EntityType.Builder.create(BeetlingEntity::new, EntityClassification.CREATURE).size(0.475F, 0.34F).build(BiomancyMod.MOD_ID + ":" + "beetling"));
	public static final RegistryObject<EntityType<PotionBeetleEntity>> POTION_BEETLE = ENTITIES.register("potion_beetle", () -> EntityType.Builder.create(PotionBeetleEntity::new, EntityClassification.CREATURE).size(0.475F, 0.34F).build(BiomancyMod.MOD_ID + ":" + "potion_beetle"));
	public static final RegistryObject<EntityType<MasonBeetleEntity>> MASON_BEETLE = ENTITIES.register("mason_beetle", () -> EntityType.Builder.create(MasonBeetleEntity::new, EntityClassification.CREATURE).size(0.475F, 0.34F).build(BiomancyMod.MOD_ID + ":" + "mason_beetle"));

	private ModEntityTypes() {}

	static void onPostSetup() {
		BiomancyMod.LOGGER.debug(MarkerManager.getMarker("ENTITIES"), "creating attributes...");

		GlobalEntityTypeAttributes.put(FLESH_BLOB.get(), FleshBlobEntity.createAttributes().create());
		GlobalEntityTypeAttributes.put(FAILED_SHEEP.get(), SheepEntity.registerAttributes().create());
		GlobalEntityTypeAttributes.put(CHROMA_SHEEP.get(), ChromaSheepEntity.createAttributes().create());
		GlobalEntityTypeAttributes.put(SILKY_WOOL_SHEEP.get(), SilkyWoolSheepEntity.createAttributes().create());
		GlobalEntityTypeAttributes.put(THICK_WOOL_SHEEP.get(), ThickWoolSheepEntity.createAttributes().create());

		GlobalEntityTypeAttributes.put(FLESHKIN.get(), FleshkinEntity.createAttributes().create());

		GlobalEntityTypeAttributes.put(BLOBLING.get(), BloblingEntity.createAttributes().create());
		GlobalEntityTypeAttributes.put(BROOD_MOTHER.get(), CrocospiderEntity.createAttributes().create());
		GlobalEntityTypeAttributes.put(BEETLING.get(), BeetlingEntity.createAttributes().create());
		GlobalEntityTypeAttributes.put(POTION_BEETLE.get(), PotionBeetleEntity.createAttributes().create());
		GlobalEntityTypeAttributes.put(MASON_BEETLE.get(), MasonBeetleEntity.createAttributes().create());

//		BiomancyMod.LOGGER.debug(MarkerManager.getMarker("ENTITIES"), "registering spawn placement predicates...");
//		EntitySpawnPlacementRegistry.register(BLOBLING.get(), EntitySpawnPlacementRegistry.PlacementType.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, ModEntityTypes::canMonsterSpawn);
//		EntitySpawnPlacementRegistry.register(BROOD_MOTHER.get(), EntitySpawnPlacementRegistry.PlacementType.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, ModEntityTypes::canMonsterSpawn);
//		EntitySpawnPlacementRegistry.register(BEETLING.get(), EntitySpawnPlacementRegistry.PlacementType.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, ModEntityTypes::canAnimalSpawn);
	}

//	public static boolean canMonsterSpawn(EntityType<? extends MonsterEntity> type, IServerWorld worldIn, SpawnReason reason, BlockPos pos, Random randomIn) {
//		BlockPos posDown = pos.down();
//		return worldIn.getDifficulty() != Difficulty.PEACEFUL && (reason == SpawnReason.SPAWNER || worldIn.getBlockState(posDown).isSolidSide(worldIn, posDown, Direction.UP));
//	}
//
//	public static boolean canAnimalSpawn(EntityType<? extends AnimalEntity> type, IWorld worldIn, SpawnReason reason, BlockPos pos, Random randomIn) {
//		BlockState blockState = worldIn.getBlockState(pos.down());
//		return blockState.isIn(Blocks.GRASS_BLOCK) && worldIn.getLightSubtracted(pos, 0) > 6;
//	}

//	@SubscribeEvent
//	public static void registerBiomeSpawns(final BiomeLoadingEvent event) {
//        if (event.getCategory() == Biome.Category.SWAMP || event.getName().equals(Biomes.DARK_FOREST_HILLS.getRegistryName()) || event.getName().equals(Biomes.DARK_FOREST.getRegistryName())) {
//            event.getSpawns().getSpawner(EntityClassification.MONSTER).add(new MobSpawnInfo.Spawners(BLOBLING.get(), 200, 3, 6));
//            event.getSpawns().getSpawner(EntityClassification.MONSTER).add(new MobSpawnInfo.Spawners(BROOD_MOTHER.get(), 120, 1, 2));
//        }
//	}
}
