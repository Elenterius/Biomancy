package com.github.elenterius.biomancy.init;

import com.github.elenterius.biomancy.BiomancyMod;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.Tag;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.Tags;

public final class ModTags {
	private ModTags() {}

	public static void init() {
		//force initialization of static fields
		Items.forceInit();
		EntityTypes.forceInit();
	}

	public static final class Items {
		public static final Tags.IOptionalNamedTag<Item> RAW_MEATS = ItemTags.createOptional(new ResourceLocation(BiomancyMod.MOD_ID, "raw_meats"));
		public static final Tags.IOptionalNamedTag<Item> COOKED_MEATS = ItemTags.createOptional(new ResourceLocation(BiomancyMod.MOD_ID, "cooked_meats"));
		public static final Tags.IOptionalNamedTag<Item> SUGARS = ItemTags.createOptional(new ResourceLocation(BiomancyMod.MOD_ID, "sugars"));

		public static final Tags.IOptionalNamedTag<Item> BIOMASS = ItemTags.createOptional(new ResourceLocation(BiomancyMod.MOD_ID, "biomass"));
		public static final Tags.IOptionalNamedTag<Item> POOR_BIOMASS = ItemTags.createOptional(new ResourceLocation(BiomancyMod.MOD_ID, "poor_biomass"));
		public static final Tags.IOptionalNamedTag<Item> AVERAGE_BIOMASS = ItemTags.createOptional(new ResourceLocation(BiomancyMod.MOD_ID, "average_biomass"));
		public static final Tags.IOptionalNamedTag<Item> GOOD_BIOMASS = ItemTags.createOptional(new ResourceLocation(BiomancyMod.MOD_ID, "good_biomass"));
		public static final Tags.IOptionalNamedTag<Item> SUPERB_BIOMASS = ItemTags.createOptional(new ResourceLocation(BiomancyMod.MOD_ID, "superb_biomass"));

		public static final Tags.IOptionalNamedTag<Item> OXIDES = ItemTags.createOptional(new ResourceLocation(BiomancyMod.MOD_ID, "oxides"));
		public static final Tags.IOptionalNamedTag<Item> SILICATES = ItemTags.createOptional(new ResourceLocation(BiomancyMod.MOD_ID, "silicates"));
		public static final Tags.IOptionalNamedTag<Item> KERATINS = ItemTags.createOptional(new ResourceLocation(BiomancyMod.MOD_ID, "keratins"));
		public static final Tags.IOptionalNamedTag<Item> HORMONES = ItemTags.createOptional(new ResourceLocation(BiomancyMod.MOD_ID, "hormones"));

		public static final Tags.IOptionalNamedTag<Item> STOMACHS = ItemTags.createOptional(new ResourceLocation(BiomancyMod.MOD_ID, "stomachs"));

		private Items() {}

		private static void forceInit() {}
	}

	public static final class EntityTypes {
		public static final Tags.IOptionalNamedTag<EntityType<?>> INFERNAL = EntityTypeTags.createOptional(new ResourceLocation(BiomancyMod.MOD_ID, "infernal"));

		public static final Tag.Named<EntityType<?>> BOSSES = EntityTypeTags.bind("forge:bosses");
		public static final Tag.Named<EntityType<?>> NOT_CLONEABLE = EntityTypeTags.bind(BiomancyMod.createRLString("not_cloneable"));
		public static final Tag.Named<EntityType<?>> SHARP_FANG = EntityTypeTags.bind(BiomancyMod.createRLString("sharp_fang"));
		public static final Tag.Named<EntityType<?>> SHARP_CLAW = EntityTypeTags.bind(BiomancyMod.createRLString("sharp_claw"));
		public static final Tag.Named<EntityType<?>> VENOM_GLAND = EntityTypeTags.bind(BiomancyMod.createRLString("venom_gland"));
		public static final Tag.Named<EntityType<?>> VOLATILE_GLAND = EntityTypeTags.bind(BiomancyMod.createRLString("volatile_gland"));

		private EntityTypes() {}

		private static void forceInit() {}
	}
}
