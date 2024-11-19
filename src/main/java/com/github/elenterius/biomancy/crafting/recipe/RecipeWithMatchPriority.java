package com.github.elenterius.biomancy.crafting.recipe;

import com.github.elenterius.biomancy.mixin.accessor.IngredientAccessor;
import net.minecraft.world.Container;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;

import java.util.List;

public interface RecipeWithMatchPriority extends Recipe<Container> {

	int getMatchPriority();

	static int getOrComputeMatchPriority(Recipe<?> recipe) {
		if (recipe instanceof RecipeWithMatchPriority r) return r.getMatchPriority();
		return computeMatchPriority(recipe.getIngredients());
	}

	static int computeMatchPriority(List<Ingredient> ingredients) {
		int bias = 0;
		for (Ingredient ingredient : ingredients) {
			bias += computeItemBias(ingredient);
		}
		return bias;
	}

	private static int computeItemBias(Ingredient ingredient) {
		if (ingredient.getClass() != Ingredient.class) return -2;

		int bias = 0;
		Ingredient.Value[] values = ((IngredientAccessor) ingredient).biomancy$values();
		for (Ingredient.Value value : values) {
			if (value instanceof Ingredient.TagValue) bias--;
			else if (value instanceof Ingredient.ItemValue) bias += 2;
		}
		return bias;
	}

}
