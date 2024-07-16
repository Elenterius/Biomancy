package com.github.elenterius.biomancy.datagen.recipes.builder;

import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.world.level.ItemLike;

public final class WorkbenchRecipeBuilder {

	private WorkbenchRecipeBuilder() {}

	public static ShapedRecipeBuilder shaped(RecipeCategory category, ItemLike result) {
		return ShapedRecipeBuilder.shaped(category, result);
	}

	public static ShapedRecipeBuilder shaped(RecipeCategory category, ItemLike result, int count) {
		return ShapedRecipeBuilder.shaped(category, result, count);
	}

	public static ShapelessRecipeBuilder shapeless(RecipeCategory category, ItemLike result) {
		return ShapelessRecipeBuilder.shapeless(category, result);
	}

	public static ShapelessRecipeBuilder shapeless(RecipeCategory category, ItemLike result, int count) {
		return ShapelessRecipeBuilder.shapeless(category, result, count);
	}

}
