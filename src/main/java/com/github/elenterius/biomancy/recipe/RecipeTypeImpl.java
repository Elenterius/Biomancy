package com.github.elenterius.biomancy.recipe;

import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

import java.util.Optional;

public abstract class RecipeTypeImpl<T extends Recipe<Container>> implements RecipeType<T> {

	private final String identifier;

	protected RecipeTypeImpl(String identifier) {
		this.identifier = identifier;
	}

	public String getId() {
		return identifier;
	}

	@Override
	public String toString() {
		return identifier;
	}

	public static class ItemStackRecipeType<R extends Recipe<Container>> extends RecipeTypeImpl<R> {

		public ItemStackRecipeType(String identifier) {
			super(identifier);
		}

		public Optional<R> getRecipeFromContainer(Level level, Container inputInv) {
			RecipeManager recipeManager = level.getRecipeManager();
			return recipeManager.getRecipeFor(this, inputInv, level);
		}

		private R castRecipe(Recipe<Container> recipe) {
			//noinspection unchecked
			return (R) recipe;
		}

		public Optional<R> getRecipeForIngredient(Level level, ItemStack stack) {
			RecipeManager recipeManager = level.getRecipeManager();
			return recipeManager.byType(this).values().stream()
					.filter(recipe -> {
						for (Ingredient ingredient : recipe.getIngredients()) {
							if (ingredient.test(stack)) return true;
						}
						return false;
					}).findFirst().map(this::castRecipe);
		}

	}

}
