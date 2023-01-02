package com.github.elenterius.biomancy.init.client;

import com.github.elenterius.biomancy.init.ModBioForgeTabs;
import com.github.elenterius.biomancy.init.ModRecipeBookTypes;
import com.github.elenterius.biomancy.init.ModRecipes;
import com.github.elenterius.biomancy.recipe.BioForgeRecipe;
import com.github.elenterius.biomancy.world.inventory.menu.BioForgeTab;
import com.google.common.collect.ImmutableList;

import net.minecraft.client.RecipeBookCategories;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterRecipeBookCategoriesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.RegistryObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

//@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class ModRecipeBookCategories {

	private static final Map<String, RecipeBookCategories> BIO_FORGE_TAB_TO_CATEGORY = new HashMap<>();

	public static final RecipeBookCategories SEARCH_CATEGORY = createRecipeBookCategories(ModBioForgeTabs.SEARCH);
	public static final RecipeBookCategories MISC_CATEGORY = createRecipeBookCategories(ModBioForgeTabs.MISC);
	public static final RecipeBookCategories BLOCKS_CATEGORY = createRecipeBookCategories(ModBioForgeTabs.BLOCKS);
	public static final RecipeBookCategories MACHINES_CATEGORY = createRecipeBookCategories(ModBioForgeTabs.MACHINES);
	public static final RecipeBookCategories WEAPONS_CATEGORY = createRecipeBookCategories(ModBioForgeTabs.WEAPONS);
	public static final List<RecipeBookCategories> BIOFORGE_CATEGORIES = ImmutableList.of(SEARCH_CATEGORY, MISC_CATEGORY, BLOCKS_CATEGORY, MACHINES_CATEGORY, WEAPONS_CATEGORY);
	
	private static final Function<Recipe<?>, RecipeBookCategories> BIO_FORGE_BOOK_CATEGORIES_FINDER = recipe -> {
		if (recipe instanceof BioForgeRecipe bioForgeRecipe) {
			return BIO_FORGE_TAB_TO_CATEGORY.get(bioForgeRecipe.getTab().enumId());
		}
		return null;
	};

	private ModRecipeBookCategories() {}
	
	//TODO: Update this code for 1.19+
	/*static void init() {
		initBioForgeCategories();

		//other, prevents warnings
		//RecipeBookRegistry.addCategoriesFinder(ModRecipes.BIO_BREWING_RECIPE_TYPE.get(), recipe -> RecipeBookCategories.UNKNOWN);
		//RecipeBookRegistry.addCategoriesFinder(ModRecipes.DECOMPOSING_RECIPE_TYPE.get(), recipe -> RecipeBookCategories.UNKNOWN);
		//RecipeBookRegistry.addCategoriesFinder(ModRecipes.DIGESTING_RECIPE_TYPE.get(), recipe -> RecipeBookCategories.UNKNOWN);
	}

	private static void initBioForgeCategories() {
		//accessing ModBioForgeTabs.REGISTRY in this context is problematic in regard to joining servers. So we have to do it manually for now...

		//add stuff to the registry
		//RecipeBookRegistry.addCategoriesToType(ModRecipeBookTypes.BIO_FORGE, List.of(searchCategory, miscCategory, blocksCategory, machinesCategory, weaponsCategory));
		//RecipeBookRegistry.addAggregateCategories(searchCategory, List.of(miscCategory, blocksCategory, machinesCategory, weaponsCategory));
		//RecipeBookRegistry.addCategoriesFinder(ModRecipes.BIO_FORGING_RECIPE_TYPE.get(), BIO_FORGE_BOOK_CATEGORIES_FINDER);
	}*/

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
