package com.github.elenterius.biomancy.init.client;

import com.github.elenterius.biomancy.init.ModBioForgeCategories;
import com.github.elenterius.biomancy.init.ModRecipeBookTypes;
import com.github.elenterius.biomancy.init.ModRecipes;
import com.github.elenterius.biomancy.recipe.BioForgeRecipe;
import net.minecraft.client.RecipeBookCategories;
import net.minecraftforge.client.RecipeBookRegistry;

import java.util.*;

public final class ModRecipeBookCategories {

	private static final Map<String, RecipeBookCategories> RECIPE_BOOK_CATEGORIES = new HashMap<>();

	private ModRecipeBookCategories() {}

	public static Collection<ModBioForgeCategories.BioForgeCategory> getBioForgeCategories() {
		return ModBioForgeCategories.CATEGORIES.values();
	}

	static void init() {
		//create all
		List<RecipeBookCategories> recipeBookCategories = getBioForgeCategories().stream().map(ModRecipeBookCategories::createRecipeBookCategories).toList();
		recipeBookCategories.forEach(x -> RECIPE_BOOK_CATEGORIES.put(x.name(), x));

		Optional<RecipeBookCategories> searchRecipeBookCategory = recipeBookCategories.stream().filter(x -> x.name().equals(ModBioForgeCategories.SEARCH.nameId())).findAny();
		if (searchRecipeBookCategory.isEmpty()) throw new RuntimeException("bio-forge search category is missing");
		RecipeBookCategories searchCategory = searchRecipeBookCategory.get();

		RecipeBookRegistry.addCategoriesToType(ModRecipeBookTypes.BIO_FORGE, recipeBookCategories);
		RecipeBookRegistry.addAggregateCategories(searchCategory, recipeBookCategories.stream().filter(c -> c != searchCategory).toList());
		RecipeBookRegistry.addCategoriesFinder(ModRecipes.BIO_FORGING_RECIPE_TYPE.get(), recipe -> {
			if (recipe instanceof BioForgeRecipe bioForgeRecipe) {
				return RECIPE_BOOK_CATEGORIES.get(bioForgeRecipe.getCategory().nameId());
			}
			return null;
		});
	}

	private static RecipeBookCategories createRecipeBookCategories(ModBioForgeCategories.BioForgeCategory category) {
		return RecipeBookCategories.create(category.nameId(), category.getIcon());
	}

	public static RecipeBookCategories getRecipeBookCategories(ModBioForgeCategories.BioForgeCategory category) {
		return RECIPE_BOOK_CATEGORIES.get(category.nameId());
	}

}
