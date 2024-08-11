package com.github.elenterius.biomancy.init.tags;

import com.github.elenterius.biomancy.BiomancyMod;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

public final class ModBlockTags {
	public static final TagKey<Block> FLESHY_FENCES = tag("fleshy_fences");

	public static final TagKey<Block> FLESH_REPLACEABLE = tag("flesh_replaceable");
	public static final TagKey<Block> ALLOW_VEINS_TO_ATTACH = tag("allow_veins_to_attach");
	public static final TagKey<Block> DISALLOW_VEINS_TO_ATTACH = tag("disallow_veins_to_attach");
	public static final TagKey<Block> ACID_DESTRUCTIBLE = tag("acid_destructible");
	public static final TagKey<Block> LAVA_DESTRUCTIBLE = tag("lava_destructible");

	private ModBlockTags() {}

	private static TagKey<Block> tag(String name) {
		return BlockTags.create(BiomancyMod.createRL(name));
	}

}
