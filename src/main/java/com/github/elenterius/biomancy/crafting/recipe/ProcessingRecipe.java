package com.github.elenterius.biomancy.crafting.recipe;

import net.minecraft.world.Container;

public sealed interface ProcessingRecipe extends RecipeWithMatchPriority permits DigestingRecipe, DynamicProcessingRecipe, StaticProcessingRecipe {

	int getCraftingTimeTicks(Container inputInventory);

	int getCraftingCostNutrients(Container inputInventory);

	default boolean isRecipeEqual(ProcessingRecipe other) {
		return getId().equals(other.getId());
	}

}
