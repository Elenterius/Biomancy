package com.github.elenterius.biomancy.crafting.recipe;

import net.minecraft.world.Container;
import net.minecraft.world.item.crafting.Recipe;

public sealed interface ProcessingRecipe<T extends Container> extends Recipe<T> permits DigestingRecipe, DynamicProcessingRecipe, StaticProcessingRecipe {

	int getCraftingTimeTicks(T inputInventory);

	int getCraftingCostNutrients(T inputInventory);

	default boolean isRecipeEqual(ProcessingRecipe<T> other) {
		return getId().equals(other.getId());
	}

}
