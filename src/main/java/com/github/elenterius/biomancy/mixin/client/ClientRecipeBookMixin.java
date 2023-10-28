package com.github.elenterius.biomancy.mixin.client;

import com.github.elenterius.biomancy.init.ModRecipes;
import com.github.elenterius.biomancy.integration.BioForgeCompat;
import com.google.common.collect.*;
import net.minecraft.client.ClientRecipeBook;
import net.minecraft.client.RecipeBookCategories;
import net.minecraft.client.gui.screens.recipebook.RecipeCollection;
import net.minecraft.world.item.crafting.Recipe;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Mixin(value = ClientRecipeBook.class, priority = 999)
public abstract class ClientRecipeBookMixin {

	@Shadow
	private static RecipeBookCategories getCategory(Recipe<?> pRecipe) {
		return null;
	}

	@Shadow
	private List<RecipeCollection> allCollections;

	@Shadow
	private Map<RecipeBookCategories, List<RecipeCollection>> collectionsByTab;

	@Inject(method = "setupCollections", at = @At("HEAD"))
	private void onSetupCollections(Iterable<Recipe<?>> recipes, CallbackInfo ci) {
		if (BioForgeCompat.isRecipeCollectionOverwriteEnabled()) {
			biomancy$overwriteRecipeCollections(recipes);
		}
	}

	@Unique
	private void biomancy$overwriteRecipeCollections(Iterable<Recipe<?>> allRecipes) {
		Map<RecipeBookCategories, List<List<Recipe<?>>>> categorizedRecipes = biomancy$categorizeBioForgeRecipes(allRecipes);
		Map<RecipeBookCategories, List<RecipeCollection>> recipeCategories = new HashMap<>(); //we can't use EnumMap because of Forge modifying the enum at runtime

		ImmutableList.Builder<RecipeCollection> builder = ImmutableList.builder();

		categorizedRecipes.forEach((category, groupedRecipes) -> recipeCategories.put(category, groupedRecipes.stream().map(recipes -> {
			RecipeCollection collection = new RecipeCollection(recipes);
			builder.add(collection);
			return collection;
		}).toList()));

		RecipeBookCategories.AGGREGATE_CATEGORIES.forEach((mainCategory, subCategories) -> recipeCategories.put(mainCategory, subCategories.stream()
				.flatMap(category -> recipeCategories.getOrDefault(category, List.of()).stream()).toList())
		);

		collectionsByTab = Map.copyOf(recipeCategories);
		allCollections = builder.build();
	}

	@Unique
	private static Map<RecipeBookCategories, List<List<Recipe<?>>>> biomancy$categorizeBioForgeRecipes(Iterable<Recipe<?>> recipes) {
		Map<RecipeBookCategories, List<List<Recipe<?>>>> map = Maps.newHashMap();
		Table<RecipeBookCategories, String, List<Recipe<?>>> table = HashBasedTable.create();

		for (Recipe<?> recipe : recipes) {
			if (recipe.getType() != ModRecipes.BIO_FORGING_RECIPE_TYPE.get() || recipe.isSpecial() || recipe.isIncomplete()) continue;

			RecipeBookCategories category = getCategory(recipe);
			String group = recipe.getGroup().isEmpty() ? recipe.getId().toString() : recipe.getGroup();
			if (group.isEmpty()) {
				map.computeIfAbsent(category, categories -> Lists.newArrayList()).add(List.of(recipe));
			}
			else {
				List<Recipe<?>> list = table.get(category, group);
				if (list == null) {
					list = Lists.newArrayList();
					table.put(category, group, list);
					map.computeIfAbsent(category, categories -> Lists.newArrayList()).add(list);
				}
				list.add(recipe);
			}
		}

		return map;
	}
}
