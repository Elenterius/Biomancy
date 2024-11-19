package com.github.elenterius.biomancy.crafting.recipe;

import com.mojang.datafixers.util.Pair;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public abstract class SimpleRecipeType<T extends Recipe<Container>> implements RecipeType<T> {

	private final String identifier;

	public SimpleRecipeType(String identifier) {
		this.identifier = identifier;
	}

	public String getId() {
		return identifier;
	}

	@Override
	public String toString() {
		return identifier;
	}

	public static class AdvancedRecipeType<R extends Recipe<Container>> extends SimpleRecipeType<R> {

		public AdvancedRecipeType(String identifier) {
			super(identifier);
		}

		public Optional<R> getRecipeById(Level level, ResourceLocation id) {
			RecipeManager recipeManager = level.getRecipeManager();
			return Optional.ofNullable(castRecipe(recipeManager.byType(this).get(id)));
		}

		public Optional<R> getFirstRecipeFor(Level level, Container inputInventory) {
			RecipeManager recipeManager = level.getRecipeManager();
			return recipeManager.getRecipeFor(this, inputInventory, level);
		}

		/**
		 * It is recommended to cache the returned recipe.
		 *
		 * @return recipe biased towards item-value ingredients
		 */
		public Optional<R> getBestRecipeFor(Level level, Container inputInventory) {
			Collection<R> recipes = level.getRecipeManager().byType(this).values();

			R topRecipe = null;
			int topPriority = Integer.MIN_VALUE;

			for (R recipe : recipes) {
				if (!recipe.matches(inputInventory, level)) continue;

				int currentPriority = RecipeWithMatchPriority.getOrComputeMatchPriority(recipe);
				if (currentPriority > topPriority) {
					topRecipe = recipe;
					topPriority = currentPriority;
				}
			}

			return Optional.ofNullable(topRecipe);
		}

		private @Nullable R castRecipe(@Nullable Recipe<Container> recipe) {
			//noinspection unchecked
			return (R) recipe;
		}

		private boolean matches(R recipe, ItemStack stack) {
			for (Ingredient ingredient : recipe.getIngredients()) {
				if (ingredient.test(stack)) return true;
			}
			return false;
		}

		public Optional<R> getFirstRecipeForIngredient(Level level, ItemStack stack) {
			RecipeManager recipeManager = level.getRecipeManager();
			return recipeManager.byType(this).values().stream()
					.filter(recipe -> matches(recipe, stack))
					.findFirst().map(this::castRecipe);
		}

		/**
		 * It is recommended to cache the returned recipe.
		 *
		 * @return recipe biased towards item-value ingredients
		 */
		public Optional<R> getBestRecipeForIngredient(Level level, ItemStack stack) {
			Collection<R> recipes = level.getRecipeManager().byType(this).values();

			R topRecipe = null;
			int topPriority = Integer.MIN_VALUE;

			for (R recipe : recipes) {
				if (!matches(recipe, stack)) continue;

				int currentPriority = RecipeWithMatchPriority.getOrComputeMatchPriority(recipe);
				if (currentPriority > topPriority) {
					topRecipe = recipe;
					topPriority = currentPriority;
				}
			}

			return Optional.ofNullable(topRecipe);
		}

		public Optional<Pair<ResourceLocation, R>> getFirstRecipeForIngredient(Level level, ItemStack stack, @Nullable ResourceLocation lastRecipeId) {
			RecipeManager recipeManager = level.getRecipeManager();

			Map<ResourceLocation, R> map = recipeManager.byType(this);
			if (lastRecipeId != null) {
				R recipe = map.get(lastRecipeId);
				if (recipe != null && matches(recipe, stack)) {
					return Optional.of(Pair.of(lastRecipeId, recipe));
				}
			}

			return map.entrySet().stream()
					.filter(entry -> matches(entry.getValue(), stack))
					.findFirst()
					.map(entry -> Pair.of(entry.getKey(), entry.getValue()));
		}

		/**
		 * It is recommended to cache the returned recipe.
		 *
		 * @return recipe biased towards item-value ingredients
		 */
		public Optional<Pair<ResourceLocation, R>> getBestRecipeForIngredient(Level level, ItemStack stack, @Nullable ResourceLocation lastRecipeId) {
			RecipeManager recipeManager = level.getRecipeManager();

			Map<ResourceLocation, R> typedRecipes = recipeManager.byType(this);
			if (lastRecipeId != null) {
				R recipe = typedRecipes.get(lastRecipeId);
				if (recipe != null && matches(recipe, stack)) {
					return Optional.of(Pair.of(lastRecipeId, recipe));
				}
			}

			Set<Map.Entry<ResourceLocation, R>> recipeEntries = typedRecipes.entrySet();

			Map.Entry<ResourceLocation, R> topRecipeEntry = null;
			int topPriority = Integer.MIN_VALUE;

			for (Map.Entry<ResourceLocation, R> recipeEntry : recipeEntries) {
				R recipe = recipeEntry.getValue();
				if (!matches(recipe, stack)) continue;

				int currentPriority = RecipeWithMatchPriority.getOrComputeMatchPriority(recipe);
				if (currentPriority > topPriority) {
					topRecipeEntry = recipeEntry;
					topPriority = currentPriority;
				}
			}

			return Optional.ofNullable(topRecipeEntry)
					.map(entry -> Pair.of(entry.getKey(), entry.getValue()));
		}

	}

}
