package com.github.elenterius.biomancy.init.tags;

import com.github.elenterius.biomancy.BiomancyMod;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

public final class ModItemTags {

	public static final TagKey<Item> FORGE_TOOLS_KNIVES = forgeTag("tools/knives");

	public static final TagKey<Item> FANGS = tag("fangs");
	public static final TagKey<Item> CLAWS = tag("claws");
	public static final TagKey<Item> RAW_MEATS = tag("raw_meats");
	public static final TagKey<Item> COOKED_MEATS = tag("cooked_meats");

	public static final TagKey<Item> CANNOT_BE_EATEN_BY_CRADLE = tag("cannot_be_eaten_by_cradle");

	public static final TagKey<Item> SUGARS = tag("sugars");

	private ModItemTags() {}

	private static TagKey<Item> tag(String name) {
		return ItemTags.create(BiomancyMod.createRL(name));
	}

	private static TagKey<Item> forgeTag(String path) {
		return ItemTags.create(new ResourceLocation("forge", path));
	}

}
