package com.github.elenterius.biomancy.init.client;

import com.github.elenterius.biomancy.init.ModBioForgeCategories;
import com.github.elenterius.biomancy.init.ModRecipeBookTypes;
import com.github.elenterius.biomancy.init.ModRecipes;
import com.github.elenterius.biomancy.recipe.BioForgeCategory;
import com.github.elenterius.biomancy.recipe.BioForgeRecipe;
import net.minecraft.client.RecipeBookCategories;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraftforge.client.RecipeBookRegistry;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

public final class ModRecipeBookCategories {

	private static final Map<String, RecipeBookCategories> RECIPE_BOOK_CATEGORIES = new HashMap<>();
	private static final Function<Recipe<?>, RecipeBookCategories> BIO_FORGE_BOOK_CATEGORIES_FINDER = recipe -> {
		if (recipe instanceof BioForgeRecipe bioForgeRecipe) {
			return RECIPE_BOOK_CATEGORIES.get(bioForgeRecipe.getCategory().enumId());
		}
		return null;
	};

	private ModRecipeBookCategories() {}

	static void init() {
		//bio-forge
		List<RecipeBookCategories> recipeBookCategories = ModBioForgeCategories.REGISTRY.get().getValues().stream().map(ModRecipeBookCategories::createRecipeBookCategories).toList();

		recipeBookCategories.forEach(c -> RECIPE_BOOK_CATEGORIES.put(c.name(), c));

		String enumId = ModBioForgeCategories.SEARCH.get().enumId();
		Optional<RecipeBookCategories> optional = recipeBookCategories.stream().filter(c -> c.name().equals(enumId)).findAny();
		if (optional.isEmpty()) throw new RuntimeException("bio-forge search category is missing");
		RecipeBookCategories searchCategory = optional.get();

		RecipeBookRegistry.addCategoriesToType(ModRecipeBookTypes.BIO_FORGE, recipeBookCategories);
		RecipeBookRegistry.addAggregateCategories(searchCategory, recipeBookCategories.stream().filter(c -> c != searchCategory).toList());
		RecipeBookRegistry.addCategoriesFinder(ModRecipes.BIO_FORGING_RECIPE_TYPE.get(), BIO_FORGE_BOOK_CATEGORIES_FINDER);

		//other
		RecipeBookRegistry.addCategoriesFinder(ModRecipes.BIO_BREWING_RECIPE_TYPE.get(), recipe -> RecipeBookCategories.UNKNOWN);
		RecipeBookRegistry.addCategoriesFinder(ModRecipes.DECOMPOSING_RECIPE_TYPE.get(), recipe -> RecipeBookCategories.UNKNOWN);
		RecipeBookRegistry.addCategoriesFinder(ModRecipes.DIGESTING_RECIPE_TYPE.get(), recipe -> RecipeBookCategories.UNKNOWN);
	}

	private static RecipeBookCategories createRecipeBookCategories(BioForgeCategory category) {
		return RecipeBookCategories.create(category.enumId(), category.getIcon());
	}

	public static RecipeBookCategories getRecipeBookCategories(BioForgeCategory category) {
		return RECIPE_BOOK_CATEGORIES.get(category.enumId());
	}

}
