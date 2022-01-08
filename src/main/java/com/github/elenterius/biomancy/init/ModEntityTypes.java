package com.github.elenterius.biomancy.init;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.world.entity.flesh.FleshBlob;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.apache.logging.log4j.MarkerManager;

@Mod.EventBusSubscriber(modid = BiomancyMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class ModEntityTypes {

	public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(ForgeRegistries.ENTITIES, BiomancyMod.MOD_ID);

	//	//Aberrations
	public static final RegistryObject<EntityType<FleshBlob>> FLESH_BLOB = register("flesh_blob", EntityType.Builder.of(FleshBlob::new, MobCategory.CREATURE).sized(0.75F, 0.75F));
//	public static final RegistryObject<EntityType<OculusObserverEntity>> OCULUS_OBSERVER = register("oculus_observer", EntityType.Builder.of(OculusObserverEntity::new, EntityClassification.CREATURE).sized(0.6F, 0.5F));
//	public static final RegistryObject<EntityType<FailedSheepEntity>> FAILED_SHEEP = register("failed_sheep", EntityType.Builder.of(FailedSheepEntity::new, EntityClassification.CREATURE).sized(0.9f, 1.3f).clientTrackingRange(10));
//	public static final RegistryObject<EntityType<FailedCowEntity>> FAILED_COW = register("failed_cow", EntityType.Builder.of(FailedCowEntity::new, EntityClassification.CREATURE).sized(0.9f, 1.4f).clientTrackingRange(10));

//	//Golems & Pets
//	public static final RegistryObject<EntityType<FleshkinEntity>> FLESHKIN = register("fleshkin", EntityType.Builder.of(FleshkinEntity::new, EntityClassification.MONSTER).sized(0.6f, 1.95f).clientTrackingRange(10));
//	public static final RegistryObject<EntityType<BoomlingEntity>> BOOMLING = register("boomling", EntityType.Builder.of(BoomlingEntity::new, EntityClassification.MONSTER).sized(0.4F, 0.35F));
//
//	public static final RegistryObject<EntityType<BeetlingEntity>> BEETLING = register("beetling", EntityType.Builder.of(BeetlingEntity::new, EntityClassification.CREATURE).sized(0.475F, 0.34F));
//	public static final RegistryObject<EntityType<CrocospiderEntity>> BROOD_MOTHER = register("brood_mother", EntityType.Builder.of(CrocospiderEntity::new, EntityClassification.MONSTER).sized(1.6F, 0.7F));
//
//	//GMOs
//	public static final RegistryObject<EntityType<ChromaSheepEntity>> CHROMA_SHEEP = register("chroma_sheep", EntityType.Builder.of(ChromaSheepEntity::new, EntityClassification.CREATURE).sized(0.9f, 1.3f).clientTrackingRange(10));
//	public static final RegistryObject<EntityType<SilkyWoolSheepEntity>> SILKY_WOOL_SHEEP = register("silky_wool_sheep", EntityType.Builder.of(SilkyWoolSheepEntity::new, EntityClassification.CREATURE).sized(0.9f, 1.3f).clientTrackingRange(10));
//	public static final RegistryObject<EntityType<ThickWoolSheepEntity>> THICK_WOOL_SHEEP = register("thick_wool_sheep", EntityType.Builder.of(ThickWoolSheepEntity::new, EntityClassification.CREATURE).sized(0.9f, 1.3f).clientTrackingRange(10));
//	public static final RegistryObject<EntityType<NutrientSlurryCowEntity>> NUTRIENT_SLURRY_COW = register("nutrient_slurry_cow", EntityType.Builder.of(NutrientSlurryCowEntity::new, EntityClassification.CREATURE).sized(0.9f, 1.4f).clientTrackingRange(10));
//
//	//Projectiles
//	public static final RegistryObject<EntityType<ToothProjectileEntity>> TOOTH_PROJECTILE = register("tooth_projectile", EntityType.Builder.<ToothProjectileEntity>of(ToothProjectileEntity::new, EntityClassification.MISC).sized(0.25f, 0.25f).updateInterval(10));
//	public static final RegistryObject<EntityType<WitherSkullProjectileEntity>> WITHER_SKULL_PROJECTILE = register("wither_skull", EntityType.Builder.<WitherSkullProjectileEntity>of(WitherSkullProjectileEntity::new, EntityClassification.MISC).sized(0.3125f, 0.3125f).updateInterval(10));
//	public static final RegistryObject<EntityType<BoomlingProjectileEntity>> BOOMLING_PROJECTILE = register("boomling_projectile", EntityType.Builder.<BoomlingProjectileEntity>of(BoomlingProjectileEntity::new, EntityClassification.MISC).sized(0.4f, 0.35f).updateInterval(10));

	private ModEntityTypes() {}

	private static <T extends Entity> RegistryObject<EntityType<T>> register(String name, EntityType.Builder<T> builder) {
		return ENTITIES.register(name, () -> builder.build(BiomancyMod.MOD_ID + ":" + name));
	}

	@SubscribeEvent
	public static void onAttributeCreation(final EntityAttributeCreationEvent event) {
		BiomancyMod.LOGGER.debug(MarkerManager.getMarker("ENTITIES"), "creating attributes...");
		event.put(FLESH_BLOB.get(), FleshBlob.createAttributes().build());
//		event.put(OCULUS_OBSERVER.get(), OculusObserverEntity.createAttributes().build());
//		event.put(FAILED_SHEEP.get(), SheepEntity.createAttributes().build());
//		event.put(CHROMA_SHEEP.get(), ChromaSheepEntity.createAttributes().build());
//		event.put(SILKY_WOOL_SHEEP.get(), SilkyWoolSheepEntity.createAttributes().build());
//		event.put(THICK_WOOL_SHEEP.get(), ThickWoolSheepEntity.createAttributes().build());
//		event.put(NUTRIENT_SLURRY_COW.get(), CowEntity.createAttributes().build());
//		event.put(FAILED_COW.get(), CowEntity.createAttributes().build());
//		event.put(FLESHKIN.get(), FleshkinEntity.createAttributes().build());
//		event.put(BOOMLING.get(), BoomlingEntity.createAttributes().build());
//		event.put(BROOD_MOTHER.get(), CrocospiderEntity.createAttributes().build());
//		event.put(BEETLING.get(), BeetlingEntity.createAttributes().build());
	}

}
