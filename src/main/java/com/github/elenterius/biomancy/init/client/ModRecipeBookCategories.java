package com.github.elenterius.biomancy.init.client;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.crafting.recipe.BioForgeRecipe;
import com.github.elenterius.biomancy.init.ModBioForgeTabs;
import com.github.elenterius.biomancy.init.ModRecipeBookTypes;
import com.github.elenterius.biomancy.init.ModRecipes;
import com.github.elenterius.biomancy.menu.BioForgeTab;
import net.minecraft.client.RecipeBookCategories;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterRecipeBookCategoriesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

@Mod.EventBusSubscriber(modid = BiomancyMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class ModRecipeBookCategories {

	private ModRecipeBookCategories() {}

	public static RecipeBookCategories getRecipeBookCategories(BioForgeTab category) {
		return BioForgeCategories.TAB_TO_CATEGORY.get(category.enumId());
	}

	@SubscribeEvent
	public static void registerRecipeBooks(RegisterRecipeBookCategoriesEvent event) {
		BioForgeCategories.register(event);

		event.registerRecipeCategoryFinder(ModRecipes.BIO_BREWING_RECIPE_TYPE.get(), rc -> RecipeBookCategories.UNKNOWN);
		event.registerRecipeCategoryFinder(ModRecipes.DECOMPOSING_RECIPE_TYPE.get(), rc -> RecipeBookCategories.UNKNOWN);
		event.registerRecipeCategoryFinder(ModRecipes.DIGESTING_RECIPE_TYPE.get(), rc -> RecipeBookCategories.UNKNOWN);
	}

	private static final class BioForgeCategories {
		//inner class prevents pre-mature initialization from the EventBusSubscriber annotation

		private static final Map<String, RecipeBookCategories> TAB_TO_CATEGORY = new HashMap<>();
		public static final RecipeBookCategories SEARCH_CATEGORY = createAndRegisterSearchCategory();

		public static final Function<Recipe<?>, RecipeBookCategories> RECIPE_CATEGORY_FINDER = recipe -> {
			if (recipe instanceof BioForgeRecipe bioForgeRecipe) {
				return TAB_TO_CATEGORY.get(bioForgeRecipe.getTab().enumId());
			}
			return null;
		};

		private BioForgeCategories() {}

		private static RecipeBookCategories createAndRegisterSearchCategory() {
			BioForgeTab tab = ModBioForgeTabs.SEARCH.get();
			String name = tab.enumId();
			RecipeBookCategories category = RecipeBookCategories.create(name, tab.getIcon());
			TAB_TO_CATEGORY.put(name, category);
			return category;
		}

		private static void registerCategories() {
			for (Map.Entry<ResourceKey<BioForgeTab>, BioForgeTab> entry : ModBioForgeTabs.REGISTRY.get().getEntries()) {
				BioForgeTab tab = entry.getValue();

				if (tab == ModBioForgeTabs.SEARCH.get()) continue;

				String name = tab.enumId();
				RecipeBookCategories categories = RecipeBookCategories.create(name, tab.getIcon());
				TAB_TO_CATEGORY.put(name, categories);
			}
		}

		private static void register(RegisterRecipeBookCategoriesEvent event) {
			registerCategories();

			List<RecipeBookCategories> categories = TAB_TO_CATEGORY.values().stream().toList();

			event.registerBookCategories(ModRecipeBookTypes.BIO_FORGE, categories);
			event.registerAggregateCategory(BioForgeCategories.SEARCH_CATEGORY, categories);
			event.registerRecipeCategoryFinder(ModRecipes.BIO_FORGING_RECIPE_TYPE.get(), RECIPE_CATEGORY_FINDER);
		}
	}
}
