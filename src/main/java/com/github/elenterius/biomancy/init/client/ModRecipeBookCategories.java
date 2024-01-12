package com.github.elenterius.biomancy.init.client;

import com.github.elenterius.biomancy.crafting.recipe.BioForgeRecipe;
import com.github.elenterius.biomancy.init.ModBioForgeTabs;
import com.github.elenterius.biomancy.init.ModRecipeBookTypes;
import com.github.elenterius.biomancy.init.ModRecipes;
import com.github.elenterius.biomancy.menu.BioForgeTab;
import net.minecraft.client.RecipeBookCategories;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraftforge.client.RecipeBookRegistry;
import net.minecraftforge.registries.RegistryObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public final class ModRecipeBookCategories {

	private ModRecipeBookCategories() {}

	public static RecipeBookCategories getRecipeBookCategories(BioForgeTab category) {
		return BioForgeCategories.TAB_TO_CATEGORY.get(category.enumId());
	}

	static void registerRecipeBooks() {
		BioForgeCategories.register();

		RecipeBookRegistry.addCategoriesFinder(ModRecipes.BIO_BREWING_RECIPE_TYPE.get(), rc -> RecipeBookCategories.UNKNOWN);
		RecipeBookRegistry.addCategoriesFinder(ModRecipes.DECOMPOSING_RECIPE_TYPE.get(), rc -> RecipeBookCategories.UNKNOWN);
		RecipeBookRegistry.addCategoriesFinder(ModRecipes.DIGESTING_RECIPE_TYPE.get(), rc -> RecipeBookCategories.UNKNOWN);
	}

	private static final class BioForgeCategories {
		//inner class prevents pre-mature initialization from the EventBusSubscriber annotation

		private static final Map<String, RecipeBookCategories> TAB_TO_CATEGORY = new HashMap<>();
		public static final RecipeBookCategories SEARCH_CATEGORY = createRecipeBookCategories(ModBioForgeTabs.SEARCH);
		public static final RecipeBookCategories MISC_CATEGORY = createRecipeBookCategories(ModBioForgeTabs.MISC);
		public static final RecipeBookCategories BLOCKS_CATEGORY = createRecipeBookCategories(ModBioForgeTabs.BLOCKS);
		public static final RecipeBookCategories MACHINES_CATEGORY = createRecipeBookCategories(ModBioForgeTabs.MACHINES);
		public static final RecipeBookCategories WEAPONS_CATEGORY = createRecipeBookCategories(ModBioForgeTabs.WEAPONS);
		public static final Function<Recipe<?>, RecipeBookCategories> RECIPE_CATEGORY_FINDER = recipe -> {
			if (recipe instanceof BioForgeRecipe bioForgeRecipe) {
				return TAB_TO_CATEGORY.get(bioForgeRecipe.getTab().enumId());
			}
			return null;
		};

		private BioForgeCategories() {}

		private static RecipeBookCategories createRecipeBookCategories(RegistryObject<BioForgeTab> tab) {
			String name = tab.getId().toString().replace(":", "_");
			RecipeBookCategories categories = RecipeBookCategories.create(name, tab.get().getIcon());
			TAB_TO_CATEGORY.put(name, categories);
			return categories;
		}

		private static void register() {
			List<RecipeBookCategories> categories = TAB_TO_CATEGORY.values().stream().toList();

			RecipeBookRegistry.addCategoriesToType(ModRecipeBookTypes.BIO_FORGE, categories);
			RecipeBookRegistry.addAggregateCategories(BioForgeCategories.SEARCH_CATEGORY, categories);
			RecipeBookRegistry.addCategoriesFinder(ModRecipes.BIO_FORGING_RECIPE_TYPE.get(), RECIPE_CATEGORY_FINDER);
		}
	}
}
