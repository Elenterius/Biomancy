package com.github.elenterius.biomancy.init;

import com.github.elenterius.biomancy.BiomancyMod;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

public final class ModTags {
	private ModTags() {}

	static void init() {
		//force initialization of static fields
		Items.forceInit();
		Blocks.forceInit();
		EntityTypes.forceInit();
	}

	public static final class Items {

		public static final TagKey<Item> TOOLS_KNIVES = ItemTags.create(new ResourceLocation("forge", "tools/knives"));

		public static final TagKey<Item> RAW_MEATS = tag("raw_meats");
		public static final TagKey<Item> COOKED_MEATS = tag("cooked_meats");
		public static final TagKey<Item> SUGARS = tag("sugars");

		public static final TagKey<Item> BIOMASS = tag("biomass");
		public static final TagKey<Item> POOR_BIOMASS = tag("poor_biomass");
		public static final TagKey<Item> AVERAGE_BIOMASS = tag("average_biomass");
		public static final TagKey<Item> GOOD_BIOMASS = tag("good_biomass");
		public static final TagKey<Item> SUPERB_BIOMASS = tag("superb_biomass");

		private Items() {}

		private static TagKey<Item> tag(String name) {
			return ItemTags.create(BiomancyMod.createRL(name));
		}

		private static void forceInit() {
			//internal
		}

	}

	public static final class Blocks {
		public static final TagKey<Block> FLESHY_FENCES = tag("fleshy_fences");

		private Blocks() {}

		private static TagKey<Block> tag(String name) {
			return BlockTags.create(BiomancyMod.createRL(name));
		}

		private static void forceInit() {
			//internal
		}

	}

	public static final class EntityTypes {
		public static final TagKey<EntityType<?>> INFERNAL = tag("infernal");
		public static final TagKey<EntityType<?>> BOSSES = TagKey.create(Registry.ENTITY_TYPE_REGISTRY, new ResourceLocation("forge", "bosses"));
		public static final TagKey<EntityType<?>> NOT_CLONEABLE = tag("not_cloneable");

		//Special Mob Loot
		public static final TagKey<EntityType<?>> SHARP_FANG = tag("sharp_fang");
		public static final TagKey<EntityType<?>> SHARP_CLAW = tag("sharp_claw");
		public static final TagKey<EntityType<?>> SINEW = tag("sinew");
		public static final TagKey<EntityType<?>> TOXIN_GLAND = tag("toxin_gland");
		public static final TagKey<EntityType<?>> VOLATILE_GLAND = tag("volatile_gland");
		public static final TagKey<EntityType<?>> BILE_GLAND = tag("bile_gland");
		public static final TagKey<EntityType<?>> BONE_MARROW = tag("bone_marrow");
		public static final TagKey<EntityType<?>> WITHERED_BONE_MARROW = tag("withered_bone_marrow");

		private EntityTypes() {}

		private static TagKey<EntityType<?>> tag(String name) {
			return TagKey.create(Registry.ENTITY_TYPE_REGISTRY, BiomancyMod.createRL(name));
		}

		private static void forceInit() {
			//internal
		}

	}
}
