package com.github.elenterius.biomancy.init;

import com.github.elenterius.biomancy.recipe.BioForgeRecipe;
import com.google.gson.JsonObject;
import net.minecraft.client.RecipeBookCategories;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.inventory.RecipeBookType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.client.RecipeBookRegistry;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public final class ModRecipeBooks {

	public static final RecipeBookType BIO_FORGE_TYPE = RecipeBookType.create("biomancy_bio_forge");

	private ModRecipeBooks() {}

	static void setupCategories() {
		BioForgeCategory.setup();
	}

	public record BioForgeCategory(String nameId, int sortPriority, RecipeBookCategories recipeBookCategory) {
		public static final Map<String, BioForgeCategory> CATEGORIES = new HashMap<>();
		public static final String PREFIX = "biomancy_bio_forge_";

		public static final BioForgeCategory SEARCH = new BioForgeCategory("search", 1, Items.COMPASS);
		public static final BioForgeCategory MISC = new BioForgeCategory("misc", -1, ModItems.LIVING_FLESH.get());
		public static final BioForgeCategory BLOCKS = new BioForgeCategory("blocks", ModItems.FLESH_BLOCK.get());
		public static final BioForgeCategory MACHINES = new BioForgeCategory("machines", ModItems.DECOMPOSER.get());
		public static final BioForgeCategory WEAPONS = new BioForgeCategory("weapons", ModItems.LONG_CLAWS.get());

		public BioForgeCategory(String name, int sortPriority, Item icon) {
			this(PREFIX + name, sortPriority, RecipeBookCategories.create(PREFIX + name, new ItemStack(icon)));
		}

		public BioForgeCategory(String name, Item icon) {
			this(PREFIX + name, 0, RecipeBookCategories.create(PREFIX + name, new ItemStack(icon)));
		}

		public BioForgeCategory(String nameId, int sortPriority, RecipeBookCategories recipeBookCategory) {
			this.nameId = nameId;
			this.sortPriority = sortPriority;
			this.recipeBookCategory = recipeBookCategory;
			CATEGORIES.put(nameId, this);
		}

		private static void setup() {
			RecipeBookRegistry.addCategoriesToType(BIO_FORGE_TYPE, getRecipeBookCategories(true));
			RecipeBookRegistry.addAggregateCategories(SEARCH.recipeBookCategory, getRecipeBookCategories(false));
			RecipeBookRegistry.addCategoriesFinder(ModRecipes.BIO_FORGING_RECIPE_TYPE, recipe -> {
				if (recipe instanceof BioForgeRecipe bioForgeRecipe) {
					return bioForgeRecipe.getCategory().recipeBookCategory;
				}
				return null;
			});
		}

		public static Collection<BioForgeCategory> getCategories() {
			return CATEGORIES.values();
		}

		public static List<RecipeBookCategories> getRecipeBookCategories(boolean includeSearchCategory) {
			Stream<BioForgeCategory> stream = includeSearchCategory ? CATEGORIES.values().stream() : CATEGORIES.values().stream().filter(c -> c != SEARCH);
			return stream.map(BioForgeCategory::recipeBookCategory).toList();
		}

		public static BioForgeCategory byNameId(String id) {
			return CATEGORIES.getOrDefault(id, MISC);
		}

		public static BioForgeCategory fromJson(JsonObject json) {
			String nameId = GsonHelper.getAsString(json, "category", MISC.nameId);
			return byNameId(nameId);
		}

		public static BioForgeCategory fromNetwork(FriendlyByteBuf buffer) {
			return byNameId(buffer.readUtf());
		}

		public void toJson(JsonObject json) {
			json.addProperty("category", nameId);
		}

		public void toNetwork(FriendlyByteBuf buffer) {
			buffer.writeUtf(nameId);
		}

		public ItemStack getIcon() {
			return recipeBookCategory.getIconItems().get(0);
		}

	}
}
