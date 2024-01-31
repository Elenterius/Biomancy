package com.github.elenterius.biomancy.datagen.tags;

import com.github.alexthe666.alexsmobs.AlexsMobs;
import com.github.alexthe666.alexsmobs.entity.AMEntityRegistry;
import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.init.ModEntityTypes;
import com.github.elenterius.biomancy.init.tags.ModEntityTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.EntityTypeTagsProvider;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.Predicate;

public class ModEntityTypeTagsProvider extends EntityTypeTagsProvider {

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
			EntityType.ENDER_DRAGON
	};
	protected static final Set<EntityType<?>> INVALID_MOBS_FOR_MEATY_LOOT = Set.of(
			EntityType.SLIME, EntityType.MAGMA_CUBE,
			EntityType.IRON_GOLEM, EntityType.SNOW_GOLEM, EntityType.SHULKER,
			EntityType.VEX, EntityType.GHAST, EntityType.ALLAY,
			EntityType.BLAZE,
			EntityType.HUSK, EntityType.DROWNED, EntityType.ZOMBIE, EntityType.ZOMBIE_HORSE, EntityType.ZOMBIE_VILLAGER,
			EntityType.SKELETON, EntityType.SKELETON_HORSE, EntityType.STRAY, EntityType.WITHER_SKELETON, EntityType.WITHER,

			AMEntityRegistry.SPECTRE.get(), AMEntityRegistry.VOID_WORM.get(), AMEntityRegistry.SKELEWAG.get(), AMEntityRegistry.BONE_SERPENT.get(),
			AMEntityRegistry.MIMICUBE.get(), AMEntityRegistry.FLUTTER.get(), AMEntityRegistry.GUSTER.get()
	);
	protected static final Set<EntityType<?>> SLIME_TYPES = Set.of(
			EntityType.SLIME,
			EntityType.MAGMA_CUBE
	);
	protected static final Set<EntityType<?>> GOLEM_TYPES = Set.of(
			EntityType.IRON_GOLEM,
			EntityType.SNOW_GOLEM
	);
	protected static final Set<EntityType<?>> WITHER_TYPES = Set.of(
			EntityType.WITHER,
			EntityType.WITHER_SKELETON
	);
	protected static final Set<EntityType<?>> ZOMBIE_TYPES = Set.of(
			EntityType.ZOMBIE,
			EntityType.ZOMBIE_HORSE,
			EntityType.ZOMBIE_VILLAGER,
			EntityType.ZOMBIFIED_PIGLIN
	);
	protected static final Set<EntityType<?>> SKELETON_TYPES = Set.of(
			EntityType.SKELETON,
			EntityType.SKELETON_HORSE,
			EntityType.WITHER_SKELETON,
			EntityType.STRAY,
			AMEntityRegistry.SKELEWAG.get(),
			AMEntityRegistry.BONE_SERPENT.get()
	);
	protected static final Set<EntityType<?>> UNDEAD_TYPES = createUndeadTypes();

	public ModEntityTypeTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, @Nullable ExistingFileHelper existingFileHelper) {
		super(output, lookupProvider, BiomancyMod.MOD_ID, existingFileHelper);
	}

	private static Set<EntityType<?>> createUndeadTypes() {
		Set<EntityType<?>> set = new HashSet<>();
		set.addAll(SKELETON_TYPES);
		set.addAll(ZOMBIE_TYPES);
		set.addAll(WITHER_TYPES);

		set.add(EntityType.PHANTOM);

		return Set.copyOf(set);
	}

	@Override
	protected void addTags(HolderLookup.Provider provider) {
		createTag(ModEntityTags.NOT_CLONEABLE)
				.addTag(ModEntityTags.FORGE_BOSSES)
				.add(EntityType.IRON_GOLEM, EntityType.SNOW_GOLEM)
				.addOptional("strawgolem:strawgolem", "strawgolem:strawnggolem");

		createTag(ModEntityTags.FLESHKIN).add(
				ModEntityTypes.FLESH_BLOB.get(), ModEntityTypes.HUNGRY_FLESH_BLOB.get(), ModEntityTypes.LEGACY_FLESH_BLOB.get(),
				ModEntityTypes.PRIMORDIAL_FLESH_BLOB.get(), ModEntityTypes.PRIMORDIAL_HUNGRY_FLESH_BLOB.get()
		);

		addDespoilLootTags();
	}

	private void addDespoilLootTags() {
		createTag(ModEntityTags.SHARP_FANG)
				.add(SHARP_FANG_MOBS)
				.addOptional(
						AMEntityRegistry.GRIZZLY_BEAR, AMEntityRegistry.DROPBEAR, AMEntityRegistry.SEA_BEAR,
						AMEntityRegistry.GORILLA, AMEntityRegistry.GELADA_MONKEY, AMEntityRegistry.CAPUCHIN_MONKEY,
						AMEntityRegistry.RATTLESNAKE, AMEntityRegistry.ANACONDA,
						AMEntityRegistry.TIGER, AMEntityRegistry.MANED_WOLF, AMEntityRegistry.SNOW_LEOPARD,
						AMEntityRegistry.TUSKLIN
				);

		createTag(ModEntityTags.SHARP_CLAW)
				.add(SHARP_CLAW_MOBS)
				.addOptional(
						AMEntityRegistry.GRIZZLY_BEAR, AMEntityRegistry.DROPBEAR, AMEntityRegistry.SEA_BEAR,
						AMEntityRegistry.ROADRUNNER, AMEntityRegistry.SOUL_VULTURE, AMEntityRegistry.BALD_EAGLE, AMEntityRegistry.EMU,
						AMEntityRegistry.PLATYPUS,
						AMEntityRegistry.RACCOON, AMEntityRegistry.TASMANIAN_DEVIL,
						AMEntityRegistry.TIGER, AMEntityRegistry.MANED_WOLF, AMEntityRegistry.SNOW_LEOPARD
				);

		createTag(ModEntityTags.TOXIN_GLAND)
				.add(TOXIC_MOBS)
				.addOptional(
						AMEntityRegistry.KOMODO_DRAGON,
						AMEntityRegistry.PLATYPUS
				);

		createTag(ModEntityTags.VOLATILE_GLAND)
				.add(VOLATILE_MOBS);

		createTag(ModEntityTags.BONE_MARROW)
				.add(EntityType.SKELETON_HORSE, EntityType.WARDEN)
				.addTag(EntityTypeTags.SKELETONS)
				.addOptional(AMEntityRegistry.SKELEWAG, AMEntityRegistry.BONE_SERPENT);

		createTag(ModEntityTags.WITHERED_BONE_MARROW)
				.add(EntityType.WITHER_SKELETON, EntityType.WITHER);

		buildSinewAndBileTag();
	}

	private void buildSinewAndBileTag() {
		Set<String> validNamespaces = Set.of("minecraft", BiomancyMod.MOD_ID, AlexsMobs.MODID);
		Predicate<EntityType<?>> allowedNamespace = entityType -> validNamespaces.contains(Objects.requireNonNull(ForgeRegistries.ENTITY_TYPES.getKey(entityType)).getNamespace());

		Set<EntityType<?>> toxicMobs = Set.of(TOXIC_MOBS);
		Set<EntityType<?>> volatileMobs = Set.of(VOLATILE_MOBS);
		Predicate<EntityType<?>> canHaveGland = entityType -> !toxicMobs.contains(entityType) && !volatileMobs.contains(entityType);

		EnhancedTagAppender<EntityType<?>> sinewTag = createTag(ModEntityTags.SINEW);
		EnhancedTagAppender<EntityType<?>> bileGlandTag = createTag(ModEntityTags.BILE_GLAND);

		for (EntityType<?> entityType : ForgeRegistries.ENTITY_TYPES) {
			if (!allowedNamespace.test(entityType)) continue;

			if (isValidForMeatyLoot(entityType)) {
				sinewTag.add(entityType);
				if (canHaveGland.test(entityType)) bileGlandTag.add(entityType);
			}
			else if (ZOMBIE_TYPES.contains(entityType)) {
				sinewTag.add(entityType);
			}
		}
	}

	private boolean isValidForMeatyLoot(EntityType<?> entityType) {
		if (entityType == EntityType.PLAYER) return true;

		if (entityType.getCategory() == MobCategory.MISC) return false; //should catch all non-living mobs

		if (INVALID_MOBS_FOR_MEATY_LOOT.contains(entityType)) return false;
		if (UNDEAD_TYPES.contains(entityType)) return false;
		if (GOLEM_TYPES.contains(entityType)) return false;
		if (SLIME_TYPES.contains(entityType)) return false;
		if (entityType == EntityType.WARDEN) return false;
		return true;
	}

	protected EnhancedTagAppender<EntityType<?>> createTag(TagKey<EntityType<?>> tag) {
		return new EnhancedTagAppender<>(tag(tag), ForgeRegistries.ENTITY_TYPES);
	}

	@Override
	public String getName() {
		return StringUtils.capitalize(modId) + " " + super.getName();
	}

}
