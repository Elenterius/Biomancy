package com.github.elenterius.biomancy.init.tags;

import com.github.elenterius.biomancy.BiomancyMod;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

public final class ModItemTags {

	public static final TagKey<Item> C_FANGS = conventionalTag("fangs");
	public static final TagKey<Item> C_CLAWS = conventionalTag("claws");
	public static final TagKey<Item> C_WITHER_BONES = conventionalTag("bones/wither");

	public static final TagKey<Item> FORGE_TOOLS_KNIVES = forgeTag("tools/knives");

	public static final TagKey<Item> FRESH_RAW_MEATS = tag("raw_meats");
	public static final TagKey<Item> COOKED_MEATS = tag("cooked_meats");
	public static final TagKey<Item> SUGARS = tag("sugars");

	public static final TagKey<Item> CANNOT_BE_DIGESTED_IN_ACID = tag("cannot_be_digested_in_acid");
	public static final TagKey<Item> CANNOT_BE_EATEN_BY_CRADLE = tag("cannot_be_eaten_by_cradle");

	private ModItemTags() {}

	private static TagKey<Item> tag(String name) {
		return ItemTags.create(BiomancyMod.createRL(name));
	}

	private static TagKey<Item> forgeTag(String path) {
		return ItemTags.create(new ResourceLocation("forge", path));
	}

	/// conventional tags introduced in neo-forge/fabric for minecraft 1.21+
	private static TagKey<Item> conventionalTag(String path) {
		return ItemTags.create(new ResourceLocation("c", path));
	}

}
