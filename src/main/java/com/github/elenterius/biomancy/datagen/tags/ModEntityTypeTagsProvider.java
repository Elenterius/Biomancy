package com.github.elenterius.biomancy.datagen.tags;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.init.ModEntityTypes;
import com.github.elenterius.biomancy.init.ModTags;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.EntityTypeTagsProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.world.entity.EntityType;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Nullable;
import java.util.Objects;
import java.util.Set;
import java.util.function.Predicate;

public class ModEntityTypeTagsProvider extends EntityTypeTagsProvider {

	protected static final Set<EntityType<?>> INVALID_VANILLA_ENTITIES = Set.of(
			EntityType.AREA_EFFECT_CLOUD,
			EntityType.ARMOR_STAND,
			EntityType.ARROW,
			EntityType.BOAT,
			EntityType.DRAGON_FIREBALL,
			EntityType.END_CRYSTAL,
			EntityType.EVOKER_FANGS,
			EntityType.EXPERIENCE_ORB,
			EntityType.EYE_OF_ENDER,
			EntityType.FALLING_BLOCK,
			EntityType.FIREWORK_ROCKET,
			EntityType.GLOW_ITEM_FRAME,
			EntityType.ITEM_FRAME,
			EntityType.ITEM,
			EntityType.FIREBALL,
			EntityType.LEASH_KNOT,
			EntityType.LIGHTNING_BOLT,
			EntityType.LLAMA_SPIT,
			EntityType.MARKER,
			EntityType.MINECART,
			EntityType.CHEST_MINECART,
			EntityType.COMMAND_BLOCK_MINECART,
			EntityType.FURNACE_MINECART,
			EntityType.HOPPER_MINECART,
			EntityType.SPAWNER_MINECART,
			EntityType.TNT_MINECART,
			EntityType.PAINTING,
			EntityType.TNT,
			EntityType.SHULKER_BULLET,
			EntityType.SMALL_FIREBALL,
			EntityType.SNOWBALL,
			EntityType.SPECTRAL_ARROW,
			EntityType.EGG,
			EntityType.ENDER_PEARL,
			EntityType.EXPERIENCE_BOTTLE,
			EntityType.POTION,
			EntityType.TRIDENT,
			EntityType.WITHER_SKULL,
			EntityType.FISHING_BOBBER
	);

	protected static final Set<EntityType<?>> INVALID_BIOMANCY_ENTITIES = Set.of(
			ModEntityTypes.TOOTH_PROJECTILE.get(),
			ModEntityTypes.ANTI_GRAVITY_PROJECTILE.get(),
			ModEntityTypes.WITHER_SKULL_PROJECTILE.get(),
			ModEntityTypes.CORROSIVE_ACID_PROJECTILE.get()
	);

	protected static final EntityType<?>[] TOXIC_MOBS = {
			EntityType.CAVE_SPIDER,
			EntityType.PUFFERFISH,
			EntityType.BEE
	};
	protected static final EntityType<?>[] VOLATILE_MOBS = {
			EntityType.CREEPER,
			EntityType.GHAST, EntityType.BLAZE,
			EntityType.WITHER, EntityType.ENDER_DRAGON
	};
	protected static final EntityType<?>[] SHARP_CLAW_MOBS = {
			EntityType.BAT,
			EntityType.CAT, EntityType.OCELOT,
			EntityType.WOLF, EntityType.FOX,
			EntityType.POLAR_BEAR, EntityType.PANDA,
			EntityType.ENDER_DRAGON
	};
	protected static final EntityType<?>[] SHARP_FANG_MOBS = {
			EntityType.BAT,
			EntityType.CAT, EntityType.OCELOT,
			EntityType.WOLF, EntityType.FOX,
			EntityType.POLAR_BEAR, EntityType.PANDA,
			EntityType.HOGLIN, EntityType.ZOGLIN,
			EntityType.ENDER_DRAGON,
			EntityType.DOLPHIN
	};
	protected static final Set<EntityType<?>> INVALID_MOBS_FOR_MEATY_LOOT = Set.of(
			EntityType.SLIME,
			EntityType.IRON_GOLEM, EntityType.SNOW_GOLEM, EntityType.SHULKER,
			EntityType.VEX,
			EntityType.SKELETON, EntityType.SKELETON_HORSE, EntityType.STRAY, EntityType.WITHER_SKELETON, EntityType.WITHER
	);

	public ModEntityTypeTagsProvider(DataGenerator pGenerator, @Nullable ExistingFileHelper existingFileHelper) {
		super(pGenerator, BiomancyMod.MOD_ID, existingFileHelper);
	}

	@Override
	protected void addTags() {
		//noinspection SpellCheckingInspection
		tag(ModTags.EntityTypes.NOT_CLONEABLE)
				.addTag(ModTags.EntityTypes.BOSSES)
				.add(EntityType.IRON_GOLEM, EntityType.SNOW_GOLEM)
				.addOptional(new ResourceLocation("strawgolem", "strawgolem"))
				.addOptional(new ResourceLocation("strawgolem", "strawnggolem"));

		tag(ModTags.EntityTypes.SHARP_FANG).add(SHARP_FANG_MOBS);
		tag(ModTags.EntityTypes.SHARP_CLAW).add(SHARP_CLAW_MOBS);
		tag(ModTags.EntityTypes.TOXIN_GLAND).add(TOXIC_MOBS);
		tag(ModTags.EntityTypes.VOLATILE_GLAND).add(VOLATILE_MOBS);
		tag(ModTags.EntityTypes.BONE_MARROW).add(EntityType.SKELETON_HORSE).addTag(EntityTypeTags.SKELETONS);
		tag(ModTags.EntityTypes.WITHERED_BONE_MARROW).add(EntityType.WITHER_SKELETON, EntityType.WITHER);

		Predicate<EntityType<?>> sinewPredicate = createSinewPredicate();
		Predicate<EntityType<?>> bileGlandPredicate = createBileGlandPredicate();
		Predicate<EntityType<?>> validEntityPredicate = createValidEntityTypePredicate();

		TagAppender<EntityType<?>> sinewTag = tag(ModTags.EntityTypes.SINEW);
		TagAppender<EntityType<?>> bileGlandTag = tag(ModTags.EntityTypes.BILE_GLAND);

		for (EntityType<?> entityType : ForgeRegistries.ENTITIES) {
			if (validEntityPredicate.test(entityType)) {
				if (sinewPredicate.test(entityType)) sinewTag.add(entityType);
				if (bileGlandPredicate.test(entityType)) bileGlandTag.add(entityType);
			}
		}
	}

	private Predicate<EntityType<?>> createValidEntityTypePredicate() {
		Set<String> validNamespaces = Set.of("minecraft", BiomancyMod.MOD_ID);
		return entityType -> !INVALID_VANILLA_ENTITIES.contains(entityType)
				&& !INVALID_BIOMANCY_ENTITIES.contains(entityType)
				&& validNamespaces.contains(Objects.requireNonNull(entityType.getRegistryName()).getNamespace());
	}

	private Predicate<EntityType<?>> createSinewPredicate() {
		return entityType -> !INVALID_MOBS_FOR_MEATY_LOOT.contains(entityType);
	}

	private Predicate<EntityType<?>> createBileGlandPredicate() {
		Set<EntityType<?>> toxicMobs = Set.of(TOXIC_MOBS);
		Set<EntityType<?>> volatileMobs = Set.of(VOLATILE_MOBS);
		return entityType -> !toxicMobs.contains(entityType) && !volatileMobs.contains(entityType) && !INVALID_MOBS_FOR_MEATY_LOOT.contains(entityType);
	}

	@Override
	public String getName() {
		return StringUtils.capitalize(modId) + " " + super.getName();
	}

}
