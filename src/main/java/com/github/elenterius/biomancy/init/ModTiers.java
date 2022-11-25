package com.github.elenterius.biomancy.init;

import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.common.ForgeTier;
import net.minecraftforge.common.Tags;

import java.util.stream.Stream;

public final class ModTiers {

	public static final ForgeTier BONE = new ForgeTier(1, 142, 4.5f, 1f, 7, BlockTags.NEEDS_STONE_TOOL, ModTiers::buildBoneIngredients);
	public static final ForgeTier LESSER_BIOFLESH = new ForgeTier(3, 1650, 6f, 2f, 14, BlockTags.NEEDS_IRON_TOOL, () -> Ingredient.of(ModItems.FLESH_BITS.get()));
	public static final ForgeTier BIOFLESH = new ForgeTier(4, 2031, 9f, 4f, 15, Tags.Blocks.NEEDS_NETHERITE_TOOL, () -> Ingredient.of(ModTags.Items.RAW_MEATS));

	private ModTiers() {}

	private static Ingredient.Value tagIngredient(TagKey<Item> tagKey) {
		return new Ingredient.TagValue(tagKey);
	}

	private static Ingredient.Value itemIngredient(Item item, int amount) {
		return new Ingredient.ItemValue(new ItemStack(item, amount));
	}

	private static Ingredient buildBoneIngredients() {
		return Ingredient.fromValues(Stream.of(
				tagIngredient(Tags.Items.BONES),
				itemIngredient(ModItems.BONE_FRAGMENTS.get(), 4)
		));
	}

}
