package com.github.elenterius.biomancy.crafting.recipe;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.item.crafting.Recipe;

public abstract class ProcessingRecipe implements Recipe<Container> {

	private final ResourceLocation registryKey;
	private final int ticks;
	private final int cost;

	protected ProcessingRecipe(ResourceLocation registryKey, int craftingTimeTicks, int craftingCostNutrients) {
		this.registryKey = registryKey;
		ticks = craftingTimeTicks;
		cost = craftingCostNutrients;
	}

	public static boolean areRecipesEqual(ProcessingRecipe recipeA, ProcessingRecipe recipeB) {
		return recipeA.isRecipeEqual(recipeB);
	}

	@Override
	public ResourceLocation getId() {
		return registryKey;
	}

	public int getCraftingTimeTicks() {
		return ticks;
	}

	public int getCraftingCostNutrients() {
		return cost;
	}

	public boolean isRecipeEqual(ProcessingRecipe other) {
		return registryKey.equals(other.getId());
	}

}
