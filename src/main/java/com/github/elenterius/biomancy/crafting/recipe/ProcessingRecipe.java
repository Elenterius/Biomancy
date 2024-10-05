package com.github.elenterius.biomancy.crafting.recipe;

import net.minecraft.world.Container;
import net.minecraft.world.item.crafting.Recipe;

public sealed interface ProcessingRecipe extends Recipe<Container> permits DigestingRecipe, DynamicProcessingRecipe, StaticProcessingRecipe {

	int getCraftingTimeTicks(Container inputInventory);

	int getCraftingCostNutrients(Container inputInventory);

	default boolean isRecipeEqual(ProcessingRecipe other) {
		return getId().equals(other.getId());
	}

}
