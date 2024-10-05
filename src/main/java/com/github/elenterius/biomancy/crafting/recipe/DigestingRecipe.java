package com.github.elenterius.biomancy.crafting.recipe;

import net.minecraft.world.item.crafting.Ingredient;

public non-sealed interface DigestingRecipe extends ProcessingRecipe {
	Ingredient getIngredient();
}
