package com.github.elenterius.biomancy.init;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.entity.BeetlingEntity;
import com.github.elenterius.biomancy.entity.aberration.FailedCowEntity;
import com.github.elenterius.biomancy.entity.aberration.FailedSheepEntity;
import com.github.elenterius.biomancy.entity.aberration.FleshBlobEntity;
import com.github.elenterius.biomancy.entity.aberration.OculusObserverEntity;
import com.github.elenterius.biomancy.entity.golem.BoomlingEntity;
import com.github.elenterius.biomancy.entity.golem.FleshkinEntity;
import com.github.elenterius.biomancy.entity.hybrid.CrocospiderEntity;
import com.github.elenterius.biomancy.entity.mutation.ChromaSheepEntity;
import com.github.elenterius.biomancy.entity.mutation.NutrientSlurryCowEntity;
import com.github.elenterius.biomancy.entity.mutation.SilkyWoolSheepEntity;
import com.github.elenterius.biomancy.entity.mutation.ThickWoolSheepEntity;
import com.github.elenterius.biomancy.entity.projectile.BoomlingProjectileEntity;
import com.github.elenterius.biomancy.entity.projectile.ToothProjectileEntity;
import com.github.elenterius.biomancy.entity.projectile.WitherSkullProjectileEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.attributes.GlobalEntityTypeAttributes;
import net.minecraft.entity.passive.CowEntity;
import net.minecraft.entity.passive.SheepEntity;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.logging.log4j.MarkerManager;

@Mod.EventBusSubscriber(modid = BiomancyMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class ModEntityTypes {
	public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(ForgeRegistries.ENTITIES, BiomancyMod.MOD_ID);

	//Aberrations
	public static final RegistryObject<EntityType<FleshBlobEntity>> FLESH_BLOB = register("flesh_blob", EntityType.Builder.of(FleshBlobEntity::new, EntityClassification.CREATURE).sized(0.75F, 0.75F));
	public static final RegistryObject<EntityType<OculusObserverEntity>> OCULUS_OBSERVER = register("oculus_observer", EntityType.Builder.of(OculusObserverEntity::new, EntityClassification.CREATURE).sized(0.6F, 0.5F));
	public static final RegistryObject<EntityType<FailedSheepEntity>> FAILED_SHEEP = register("failed_sheep", EntityType.Builder.of(FailedSheepEntity::new, EntityClassification.CREATURE).sized(0.9f, 1.3f).clientTrackingRange(10));
	public static final RegistryObject<EntityType<FailedCowEntity>> FAILED_COW = register("failed_cow", EntityType.Builder.of(FailedCowEntity::new, EntityClassification.CREATURE).sized(0.9f, 1.4f).clientTrackingRange(10));

	//Golems & Pets
	public static final RegistryObject<EntityType<FleshkinEntity>> FLESHKIN = register("fleshkin", EntityType.Builder.of(FleshkinEntity::new, EntityClassification.MONSTER).sized(0.6f, 1.95f).clientTrackingRange(10));
	public static final RegistryObject<EntityType<BoomlingEntity>> BOOMLING = register("boomling", EntityType.Builder.of(BoomlingEntity::new, EntityClassification.MONSTER).sized(0.4F, 0.35F));

	public static final RegistryObject<EntityType<BeetlingEntity>> BEETLING = register("beetling", EntityType.Builder.of(BeetlingEntity::new, EntityClassification.CREATURE).sized(0.475F, 0.34F));
	public static final RegistryObject<EntityType<CrocospiderEntity>> BROOD_MOTHER = register("brood_mother", EntityType.Builder.of(CrocospiderEntity::new, EntityClassification.MONSTER).sized(1.6F, 0.7F));

	//GMOs
	public static final RegistryObject<EntityType<ChromaSheepEntity>> CHROMA_SHEEP = register("chroma_sheep", EntityType.Builder.of(ChromaSheepEntity::new, EntityClassification.CREATURE).sized(0.9f, 1.3f).clientTrackingRange(10));
	public static final RegistryObject<EntityType<SilkyWoolSheepEntity>> SILKY_WOOL_SHEEP = register("silky_wool_sheep", EntityType.Builder.of(SilkyWoolSheepEntity::new, EntityClassification.CREATURE).sized(0.9f, 1.3f).clientTrackingRange(10));
	public static final RegistryObject<EntityType<ThickWoolSheepEntity>> THICK_WOOL_SHEEP = register("thick_wool_sheep", EntityType.Builder.of(ThickWoolSheepEntity::new, EntityClassification.CREATURE).sized(0.9f, 1.3f).clientTrackingRange(10));
	public static final RegistryObject<EntityType<NutrientSlurryCowEntity>> NUTRIENT_SLURRY_COW = register("nutrient_slurry_cow", EntityType.Builder.of(NutrientSlurryCowEntity::new, EntityClassification.CREATURE).sized(0.9f, 1.4f).clientTrackingRange(10));

	//Projectiles
	public static final RegistryObject<EntityType<ToothProjectileEntity>> TOOTH_PROJECTILE = register("tooth_projectile", EntityType.Builder.<ToothProjectileEntity>of(ToothProjectileEntity::new, EntityClassification.MISC).sized(0.25f, 0.25f).updateInterval(10));
	public static final RegistryObject<EntityType<WitherSkullProjectileEntity>> WITHER_SKULL_PROJECTILE = register("wither_skull", EntityType.Builder.<WitherSkullProjectileEntity>of(WitherSkullProjectileEntity::new, EntityClassification.MISC).sized(0.3125f, 0.3125f).updateInterval(10));
	public static final RegistryObject<EntityType<BoomlingProjectileEntity>> BOOMLING_PROJECTILE = register("boomling_projectile", EntityType.Builder.<BoomlingProjectileEntity>of(BoomlingProjectileEntity::new, EntityClassification.MISC).sized(0.4f, 0.35f).updateInterval(10));

	private ModEntityTypes() {
	}

	private static <T extends Entity> RegistryObject<EntityType<T>> register(String name, EntityType.Builder<T> builder) {
		return ENTITIES.register(name, () -> builder.build(BiomancyMod.MOD_ID + ":" + name));
	}

	static void onPostSetup() {
		BiomancyMod.LOGGER.debug(MarkerManager.getMarker("ENTITIES"), "creating attributes...");

		GlobalEntityTypeAttributes.put(FLESH_BLOB.get(), FleshBlobEntity.createAttributes().build());
		GlobalEntityTypeAttributes.put(OCULUS_OBSERVER.get(), OculusObserverEntity.createAttributes().build());
		GlobalEntityTypeAttributes.put(FAILED_SHEEP.get(), SheepEntity.createAttributes().build());
		GlobalEntityTypeAttributes.put(CHROMA_SHEEP.get(), ChromaSheepEntity.createAttributes().build());
		GlobalEntityTypeAttributes.put(SILKY_WOOL_SHEEP.get(), SilkyWoolSheepEntity.createAttributes().build());
		GlobalEntityTypeAttributes.put(THICK_WOOL_SHEEP.get(), ThickWoolSheepEntity.createAttributes().build());
		GlobalEntityTypeAttributes.put(NUTRIENT_SLURRY_COW.get(), CowEntity.createAttributes().build());
		GlobalEntityTypeAttributes.put(FAILED_COW.get(), CowEntity.createAttributes().build());

		GlobalEntityTypeAttributes.put(FLESHKIN.get(), FleshkinEntity.createAttributes().build());

		GlobalEntityTypeAttributes.put(BOOMLING.get(), BoomlingEntity.createAttributes().build());
		GlobalEntityTypeAttributes.put(BROOD_MOTHER.get(), CrocospiderEntity.createAttributes().build());
		GlobalEntityTypeAttributes.put(BEETLING.get(), BeetlingEntity.createAttributes().build());

//		BiomancyMod.LOGGER.debug(MarkerManager.getMarker("ENTITIES"), "registering spawn placement predicates...");
//		EntitySpawnPlacementRegistry.register(BLOBLING.get(), EntitySpawnPlacementRegistry.PlacementType.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, ModEntityTypes::canMonsterSpawn);
//		EntitySpawnPlacementRegistry.register(BROOD_MOTHER.get(), EntitySpawnPlacementRegistry.PlacementType.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, ModEntityTypes::canMonsterSpawn);
//		EntitySpawnPlacementRegistry.register(BEETLING.get(), EntitySpawnPlacementRegistry.PlacementType.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, ModEntityTypes::canAnimalSpawn);

//		ForgeRegistries.ENTITIES.getValues().forEach(entityType -> BiomancyMod.LOGGER.debug(MarkerManager.getMarker("Entity Volume"), MessageFormat.format("{0}: {1} m³", entityType.getRegistryName(), entityType.getWidth() * entityType.getWidth() * entityType.getHeight())));
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
