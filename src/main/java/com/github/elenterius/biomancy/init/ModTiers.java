package com.github.elenterius.biomancy.init;

import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.common.ForgeTier;
import net.minecraftforge.common.Tags;

public final class ModTiers {

	public static final ForgeTier BONE = new ForgeTier(1, 131, 4f, 1f, 5, BlockTags.NEEDS_STONE_TOOL, () -> Ingredient.of(Items.BONE_MEAL, ModItems.BONE_SCRAPS.get()));
	public static final ForgeTier LESSER_BIOMETAL = new ForgeTier(3, 1650, 6f, 2f, 14, BlockTags.NEEDS_IRON_TOOL, () -> Ingredient.of(ModItems.FLESH_BLOCK.get()));
	public static final ForgeTier BIOMETAL = new ForgeTier(4, 2031, 9f, 4f, 15, Tags.Blocks.NEEDS_NETHERITE_TOOL, () -> Ingredient.of(ModItems.BIOMETAL.get()));

}
