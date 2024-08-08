package com.github.elenterius.biomancy.init;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.entity.mob.*;
import com.github.elenterius.biomancy.entity.mob.fleshblob.AdulteratedEaterFleshBlob;
import com.github.elenterius.biomancy.entity.mob.fleshblob.AdulteratedHangryEaterFleshBlob;
import com.github.elenterius.biomancy.entity.mob.fleshblob.PrimordialEaterFleshBlob;
import com.github.elenterius.biomancy.entity.mob.fleshblob.PrimordialHangryEaterFleshBlob;
import com.github.elenterius.biomancy.entity.projectile.*;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.animal.Chicken;
import net.minecraft.world.entity.animal.Cow;
import net.minecraft.world.entity.animal.Sheep;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.UnaryOperator;

@Mod.EventBusSubscriber(modid = BiomancyMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class ModEntityTypes {

	public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, BiomancyMod.MOD_ID);

	//# Flesh Blobs
	public static final RegistryObject<EntityType<AdulteratedEaterFleshBlob>> FLESH_BLOB = register("flesh_blob", EntityType.Builder.of(AdulteratedEaterFleshBlob::new, MobCategory.CREATURE).sized(1f, 1f));
	public static final RegistryObject<EntityType<AdulteratedEaterFleshBlob>> LEGACY_FLESH_BLOB = register("legacy_flesh_blob", EntityType.Builder.of(AdulteratedEaterFleshBlob::new, MobCategory.CREATURE).sized(1f, 1f));
	public static final RegistryObject<EntityType<AdulteratedHangryEaterFleshBlob>> HUNGRY_FLESH_BLOB = register("hungry_flesh_blob", EntityType.Builder.of(AdulteratedHangryEaterFleshBlob::new, MobCategory.CREATURE).sized(1f, 1f));
	public static final RegistryObject<EntityType<PrimordialEaterFleshBlob>> PRIMORDIAL_FLESH_BLOB = register("primordial_flesh_blob", EntityType.Builder.of(PrimordialEaterFleshBlob::new, MobCategory.MONSTER).sized(1f, 1f));
	public static final RegistryObject<EntityType<PrimordialHangryEaterFleshBlob>> PRIMORDIAL_HUNGRY_FLESH_BLOB = register("primordial_hungry_flesh_blob", EntityType.Builder.of(PrimordialHangryEaterFleshBlob::new, MobCategory.MONSTER).sized(1f, 1f));

	//# Aberrations
	public static final RegistryObject<EntityType<FleshCow>> FLESH_COW = register("flesh_cow", EntityType.Builder.of(FleshCow::new, MobCategory.CREATURE).sized(0.9f, 1.4f).clientTrackingRange(10));
	public static final RegistryObject<EntityType<FleshSheep>> FLESH_SHEEP = register("flesh_sheep", EntityType.Builder.of(FleshSheep::new, MobCategory.CREATURE).sized(0.9f, 1.3f).clientTrackingRange(10));
	public static final RegistryObject<EntityType<FleshPig>> FLESH_PIG = register("flesh_pig", EntityType.Builder.of(FleshPig::new, MobCategory.CREATURE).sized(0.9f, 0.9f).clientTrackingRange(10));
	public static final RegistryObject<EntityType<FleshChicken>> FLESH_CHICKEN = register("flesh_chicken", EntityType.Builder.of(FleshChicken::new, MobCategory.CREATURE).sized(0.4f, 0.7f).clientTrackingRange(10));

	//# Genetic Mutations
	public static final RegistryObject<EntityType<ChromaSheep>> CHROMA_SHEEP = register("chroma_sheep", EntityType.Builder.of(ChromaSheep::new, MobCategory.CREATURE).sized(0.9f, 1.3f).clientTrackingRange(10));
	public static final RegistryObject<EntityType<ThickFurSheep>> THICK_FUR_SHEEP = register("thick_fur_sheep", EntityType.Builder.of(ThickFurSheep::new, MobCategory.CREATURE).sized(0.9f, 1.3f).clientTrackingRange(10));

	//Projectiles
	public static final RegistryObject<EntityType<ToothProjectile>> TOOTH_PROJECTILE = registerProjectile("tooth_projectile", ToothProjectile::new, builder -> builder.sized(0.25f, 0.25f));
	public static final RegistryObject<EntityType<AcidSpitProjectile>> CORROSIVE_ACID_PROJECTILE = registerProjectile("corrosive_acid_projectile", AcidSpitProjectile::new, builder -> builder.sized(0.25f, 0.25f));
	public static final RegistryObject<EntityType<BloomberryProjectile>> BLOOMBERRY_PROJECTILE = registerProjectile("bloomberry_projectile", BloomberryProjectile::new, builder -> builder.sized(8f / 16f, 8f / 16f));
	public static final RegistryObject<EntityType<AcidBlobProjectile>> ACID_BLOB_PROJECTILE = registerProjectile("acid_blob_projectile", AcidBlobProjectile::new, builder -> builder.sized(6f / 16f, 6f / 16f));

	private ModEntityTypes() {}

	private static <T extends Entity> RegistryObject<EntityType<T>> register(String name, EntityType.Builder<T> builder) {
		return ENTITIES.register(name, () -> builder.build(BiomancyMod.MOD_ID + ":" + name));
	}

	private static <T extends BaseProjectile> RegistryObject<EntityType<T>> registerProjectile(String name, EntityType.EntityFactory<T> factory, UnaryOperator<EntityType.Builder<T>> builder) {
		return ENTITIES.register(name, () -> builder.apply(EntityType.Builder.of(factory, MobCategory.MISC)).updateInterval(10).build(BiomancyMod.MOD_ID + ":" + name));
	}

	@SubscribeEvent
	public static void onAttributeCreation(final EntityAttributeCreationEvent event) {
		event.put(FLESH_BLOB.get(), AdulteratedEaterFleshBlob.createAttributes().build());
		event.put(LEGACY_FLESH_BLOB.get(), AdulteratedEaterFleshBlob.createAttributes().build());
		event.put(HUNGRY_FLESH_BLOB.get(), AdulteratedHangryEaterFleshBlob.createAttributes().build());
		event.put(PRIMORDIAL_FLESH_BLOB.get(), PrimordialEaterFleshBlob.createAttributes().build());
		event.put(PRIMORDIAL_HUNGRY_FLESH_BLOB.get(), PrimordialHangryEaterFleshBlob.createAttributes().build());
		event.put(FLESH_COW.get(), Cow.createAttributes().build());
		event.put(FLESH_SHEEP.get(), Sheep.createAttributes().build());
		event.put(FLESH_PIG.get(), FleshPig.createAttributes().build());
		event.put(FLESH_CHICKEN.get(), Chicken.createAttributes().build());
		event.put(CHROMA_SHEEP.get(), Sheep.createAttributes().build());
		event.put(THICK_FUR_SHEEP.get(), Sheep.createAttributes().build());
	}

}
