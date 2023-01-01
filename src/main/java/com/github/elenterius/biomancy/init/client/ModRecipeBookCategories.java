package com.github.elenterius.biomancy.init.client;

import com.github.elenterius.biomancy.init.ModBioForgeTabs;
import com.github.elenterius.biomancy.init.ModRecipeBookTypes;
import com.github.elenterius.biomancy.init.ModRecipes;
import com.github.elenterius.biomancy.recipe.BioForgeRecipe;
import com.github.elenterius.biomancy.world.inventory.menu.BioForgeTab;
import net.minecraft.client.RecipeBookCategories;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraftforge.client.event.RegisterRecipeBookCategoriesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.registries.RegistryObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public final class ModRecipeBookCategories {

	private static final Map<String, RecipeBookCategories> BIO_FORGE_TAB_TO_CATEGORY = new HashMap<>();

	private static final Function<Recipe<?>, RecipeBookCategories> BIO_FORGE_BOOK_CATEGORIES_FINDER = recipe -> {
		if (recipe instanceof BioForgeRecipe bioForgeRecipe) {
			return BIO_FORGE_TAB_TO_CATEGORY.get(bioForgeRecipe.getTab().enumId());
		}
		return null;
	};

	private ModRecipeBookCategories() {}
	
	@SubscribeEvent
	public static void registerRecipeBooks(RegisterRecipeBookCategoriesEvent event) {
		//event.registerBookCategories(Biom );
	}
	
	//TODO: Update this code for 1.19+
	static void init() {
		initBioForgeCategories();

		//other, prevents warnings
		//RecipeBookRegistry.addCategoriesFinder(ModRecipes.BIO_BREWING_RECIPE_TYPE.get(), recipe -> RecipeBookCategories.UNKNOWN);
		//RecipeBookRegistry.addCategoriesFinder(ModRecipes.DECOMPOSING_RECIPE_TYPE.get(), recipe -> RecipeBookCategories.UNKNOWN);
		//RecipeBookRegistry.addCategoriesFinder(ModRecipes.DIGESTING_RECIPE_TYPE.get(), recipe -> RecipeBookCategories.UNKNOWN);
	}

	private static void initBioForgeCategories() {
		//accessing ModBioForgeTabs.REGISTRY in this context is problematic in regard to joining servers. So we have to do it manually for now...
		RecipeBookCategories searchCategory = createRecipeBookCategories(ModBioForgeTabs.SEARCH);
		RecipeBookCategories miscCategory = createRecipeBookCategories(ModBioForgeTabs.MISC);
		RecipeBookCategories blocksCategory = createRecipeBookCategories(ModBioForgeTabs.BLOCKS);
		RecipeBookCategories machinesCategory = createRecipeBookCategories(ModBioForgeTabs.MACHINES);
		RecipeBookCategories weaponsCategory = createRecipeBookCategories(ModBioForgeTabs.WEAPONS);

		//add stuff to the registry
		//RecipeBookRegistry.addCategoriesToType(ModRecipeBookTypes.BIO_FORGE, List.of(searchCategory, miscCategory, blocksCategory, machinesCategory, weaponsCategory));
		//RecipeBookRegistry.addAggregateCategories(searchCategory, List.of(miscCategory, blocksCategory, machinesCategory, weaponsCategory));
		//RecipeBookRegistry.addCategoriesFinder(ModRecipes.BIO_FORGING_RECIPE_TYPE.get(), BIO_FORGE_BOOK_CATEGORIES_FINDER);
	}

	private static RecipeBookCategories createRecipeBookCategories(RegistryObject<BioForgeTab> tab) {
		String name = tab.getId().toString().replace(":", "_");
		RecipeBookCategories categories = RecipeBookCategories.create(name, tab.get().getIcon());
		BIO_FORGE_TAB_TO_CATEGORY.put(name, categories);
		return categories;
	}

	public static RecipeBookCategories getRecipeBookCategories(BioForgeTab category) {
		return BIO_FORGE_TAB_TO_CATEGORY.get(category.enumId());
	}

}
