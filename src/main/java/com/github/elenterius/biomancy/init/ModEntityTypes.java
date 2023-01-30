package com.github.elenterius.biomancy.init;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.world.entity.fleshblob.DomesticatedFleshBlob;
import com.github.elenterius.biomancy.world.entity.fleshblob.HungryFleshBlob;
import com.github.elenterius.biomancy.world.entity.projectile.AntiGravityProjectile;
import com.github.elenterius.biomancy.world.entity.projectile.CorrosiveAcidProjectile;
import com.github.elenterius.biomancy.world.entity.projectile.ToothProjectile;
import com.github.elenterius.biomancy.world.entity.projectile.WitherProjectile;
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

	public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, BiomancyMod.MOD_ID);

	//# Flesh Blobs
	public static final RegistryObject<EntityType<HungryFleshBlob>> HUNGRY_FLESH_BLOB = register("hungry_flesh_blob", EntityType.Builder.of(HungryFleshBlob::new, MobCategory.CREATURE).sized(1F, 1F));
	public static final RegistryObject<EntityType<DomesticatedFleshBlob>> FLESH_BLOB = register("flesh_blob", EntityType.Builder.of(DomesticatedFleshBlob::new, MobCategory.CREATURE).sized(1F, 1F));

	//# Aberrations
	//	public static final RegistryObject<EntityType<OculusObserverEntity>> OCULUS_OBSERVER = register("oculus_observer", EntityType.Builder.of(OculusObserverEntity::new, EntityClassification.CREATURE).sized(0.6F, 0.5F));
	//	public static final RegistryObject<EntityType<FailedSheepEntity>> FAILED_SHEEP = register("failed_sheep", EntityType.Builder.of(FailedSheepEntity::new, EntityClassification.CREATURE).sized(0.9f, 1.3f).clientTrackingRange(10));
	//	public static final RegistryObject<EntityType<FailedCowEntity>> FAILED_COW = register("failed_cow", EntityType.Builder.of(FailedCowEntity::new, EntityClassification.CREATURE).sized(0.9f, 1.4f).clientTrackingRange(10));

	//# Ownable
	//	public static final RegistryObject<EntityType<Boomling>> BOOMLING = register("boomling", EntityType.Builder.of(Boomling::new, MobCategory.MONSTER).sized(0.4F, 0.35F));
	//	public static final RegistryObject<EntityType<Fleshkin>> FLESHKIN = register("fleshkin", EntityType.Builder.of(Fleshkin::new, MobCategory.MONSTER).sized(0.6f, 1.95f).clientTrackingRange(10));
	//
	//	public static final RegistryObject<EntityType<BeetlingEntity>> BEETLING = register("beetling", EntityType.Builder.of(BeetlingEntity::new, EntityClassification.CREATURE).sized(0.475F, 0.34F));
	//	public static final RegistryObject<EntityType<CrocospiderEntity>> BROOD_MOTHER = register("brood_mother", EntityType.Builder.of(CrocospiderEntity::new, EntityClassification.MONSTER).sized(1.6F, 0.7F));

	//	//GMOs
	//	public static final RegistryObject<EntityType<ChromaSheepEntity>> CHROMA_SHEEP = register("chroma_sheep", EntityType.Builder.of(ChromaSheepEntity::new, EntityClassification.CREATURE).sized(0.9f, 1.3f).clientTrackingRange(10));
	//	public static final RegistryObject<EntityType<SilkyWoolSheepEntity>> SILKY_WOOL_SHEEP = register("silky_wool_sheep", EntityType.Builder.of(SilkyWoolSheepEntity::new, EntityClassification.CREATURE).sized(0.9f, 1.3f).clientTrackingRange(10));
	//	public static final RegistryObject<EntityType<ThickWoolSheepEntity>> THICK_WOOL_SHEEP = register("thick_wool_sheep", EntityType.Builder.of(ThickWoolSheepEntity::new, EntityClassification.CREATURE).sized(0.9f, 1.3f).clientTrackingRange(10));

	//Projectiles
	public static final RegistryObject<EntityType<ToothProjectile>> TOOTH_PROJECTILE = register("tooth_projectile", EntityType.Builder.<ToothProjectile>of(ToothProjectile::new, MobCategory.MISC).sized(0.25f, 0.25f).updateInterval(10));
	public static final RegistryObject<EntityType<WitherProjectile>> WITHER_SKULL_PROJECTILE = register("wither_projectile", EntityType.Builder.<WitherProjectile>of(WitherProjectile::new, MobCategory.MISC).sized(0.3125f, 0.3125f).updateInterval(10));
	public static final RegistryObject<EntityType<AntiGravityProjectile>> ANTI_GRAVITY_PROJECTILE = register("anti_gravity_projectile", EntityType.Builder.<AntiGravityProjectile>of(AntiGravityProjectile::new, MobCategory.MISC).sized(0.25f, 0.25f).updateInterval(10));
	public static final RegistryObject<EntityType<CorrosiveAcidProjectile>> CORROSIVE_ACID_PROJECTILE = register("corrosive_acid_projectile", EntityType.Builder.<CorrosiveAcidProjectile>of(CorrosiveAcidProjectile::new, MobCategory.MISC).sized(0.25f, 0.25f).updateInterval(10));

	private ModEntityTypes() {}

	private static <T extends Entity> RegistryObject<EntityType<T>> register(String name, EntityType.Builder<T> builder) {
		return ENTITIES.register(name, () -> builder.build(BiomancyMod.MOD_ID + ":" + name));
	}

	@SubscribeEvent
	public static void onAttributeCreation(final EntityAttributeCreationEvent event) {
		BiomancyMod.LOGGER.debug(MarkerManager.getMarker("ENTITIES"), "creating attributes...");
		event.put(HUNGRY_FLESH_BLOB.get(), HungryFleshBlob.createAttributes().build());
		event.put(FLESH_BLOB.get(), DomesticatedFleshBlob.createAttributes().build());
	}

}
