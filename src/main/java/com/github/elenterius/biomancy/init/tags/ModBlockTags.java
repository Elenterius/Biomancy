package com.github.elenterius.biomancy.init.tags;

import com.github.elenterius.biomancy.BiomancyMod;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

public final class ModBlockTags {
	public static final TagKey<Block> FLESHY_FENCES = tag("fleshy_fences");

	private ModBlockTags() {}

	private static TagKey<Block> tag(String name) {
		return BlockTags.create(BiomancyMod.createRL(name));
	}

}
