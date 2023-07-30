package com.github.elenterius.biomancy.init.tags;

import com.github.elenterius.biomancy.BiomancyMod;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

public final class ModItemTags {

	public static final TagKey<Item> FORGE_TOOLS_KNIVES = forgeTag("tools/knives");
	//		public static final TagKey<Item> FORGE_RAW_BACON = forgeTag("raw_bacon");
	//		public static final TagKey<Item> FORGE_RAW_BEEF = forgeTag("raw_beef");
	//		public static final TagKey<Item> FORGE_RAW_CHICKEN = forgeTag("raw_chicken");
	//		public static final TagKey<Item> FORGE_RAW_PORK = forgeTag("raw_pork");
	//		public static final TagKey<Item> FORGE_RAW_MUTTON = forgeTag("raw_mutton");
	//		public static final TagKey<Item> FORGE_RAW_FISHES = forgeTag("raw_fishes");

	public static final TagKey<Item> FANGS = tag("fangs");
	public static final TagKey<Item> CLAWS = tag("claws");
	public static final TagKey<Item> RAW_MEATS = tag("raw_meats");
	public static final TagKey<Item> COOKED_MEATS = tag("cooked_meats");
	public static final TagKey<Item> SUGARS = tag("sugars");

	public static final TagKey<Item> BIOMASS = tag("biomass");
	public static final TagKey<Item> POOR_BIOMASS = tag("poor_biomass");
	public static final TagKey<Item> AVERAGE_BIOMASS = tag("average_biomass");
	public static final TagKey<Item> GOOD_BIOMASS = tag("good_biomass");
	public static final TagKey<Item> SUPERB_BIOMASS = tag("superb_biomass");

	private ModItemTags() {}

	private static TagKey<Item> tag(String name) {
		return ItemTags.create(BiomancyMod.createRL(name));
	}

	private static TagKey<Item> forgeTag(String path) {
		return ItemTags.create(new ResourceLocation("forge", path));
	}

}
