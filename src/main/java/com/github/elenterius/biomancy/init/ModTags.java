package com.github.elenterius.biomancy.init;

import com.github.elenterius.biomancy.BiomancyMod;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.Tag;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.Tags;

public final class ModTags {
	private ModTags() {}

	static void init() {
		//force initialization of static fields
		Items.forceInit();
		Blocks.forceInit();
		EntityTypes.forceInit();
	}

	public static final class Items {
		public static final Tags.IOptionalNamedTag<Item> RAW_MEATS = ItemTags.createOptional(BiomancyMod.createRL("raw_meats"));
		public static final Tags.IOptionalNamedTag<Item> COOKED_MEATS = ItemTags.createOptional(BiomancyMod.createRL("cooked_meats"));
		public static final Tags.IOptionalNamedTag<Item> SUGARS = ItemTags.createOptional(BiomancyMod.createRL("sugars"));

		public static final Tags.IOptionalNamedTag<Item> BIOMASS = ItemTags.createOptional(BiomancyMod.createRL("biomass"));
		public static final Tags.IOptionalNamedTag<Item> POOR_BIOMASS = ItemTags.createOptional(BiomancyMod.createRL("poor_biomass"));
		public static final Tags.IOptionalNamedTag<Item> AVERAGE_BIOMASS = ItemTags.createOptional(BiomancyMod.createRL("average_biomass"));
		public static final Tags.IOptionalNamedTag<Item> GOOD_BIOMASS = ItemTags.createOptional(BiomancyMod.createRL("good_biomass"));
		public static final Tags.IOptionalNamedTag<Item> SUPERB_BIOMASS = ItemTags.createOptional(BiomancyMod.createRL("superb_biomass"));

		private Items() {}

		private static void forceInit() {
			//internal
		}

	}

	public static final class Blocks {
		public static final Tag.Named<Block> FLESHY_FENCES = BlockTags.bind(BiomancyMod.createRLString("fleshy_fences"));

		private Blocks() {}

		private static void forceInit() {
			//internal
		}

	}

	public static final class EntityTypes {
		public static final Tags.IOptionalNamedTag<EntityType<?>> INFERNAL = EntityTypeTags.createOptional(BiomancyMod.createRL("infernal"));

		public static final Tag.Named<EntityType<?>> BOSSES = EntityTypeTags.bind("forge:bosses");
		public static final Tag.Named<EntityType<?>> NOT_CLONEABLE = EntityTypeTags.bind(BiomancyMod.createRLString("not_cloneable"));
		public static final Tag.Named<EntityType<?>> SHARP_FANG = EntityTypeTags.bind(BiomancyMod.createRLString("sharp_fang"));
		public static final Tag.Named<EntityType<?>> SHARP_CLAW = EntityTypeTags.bind(BiomancyMod.createRLString("sharp_claw"));
		public static final Tag.Named<EntityType<?>> VENOM_GLAND = EntityTypeTags.bind(BiomancyMod.createRLString("venom_gland"));
		public static final Tag.Named<EntityType<?>> VOLATILE_GLAND = EntityTypeTags.bind(BiomancyMod.createRLString("volatile_gland"));

		private EntityTypes() {}

		private static void forceInit() {
			//internal
		}

	}
}
