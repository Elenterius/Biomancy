package com.github.elenterius.biomancy.recipe;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.item.crafting.Recipe;

public abstract class AbstractProductionRecipe implements Recipe<Container> {

	private final ResourceLocation registryKey;
	private final int time;

	protected AbstractProductionRecipe(ResourceLocation registryKeyIn, int craftingTimeIn) {
		registryKey = registryKeyIn;
		time = craftingTimeIn;
	}

	@Override
	public ResourceLocation getId() {
		return registryKey;
	}

	public int getCraftingTime() {
		return time;
	}

	public boolean areRecipesEqual(AbstractProductionRecipe other) {
		return registryKey.equals(other.getId());
	}

	public static boolean areRecipesEqual(AbstractProductionRecipe recipeA, AbstractProductionRecipe recipeB) {
		return recipeA.areRecipesEqual(recipeB);
	}

}
