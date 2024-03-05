package com.github.elenterius.biomancy.crafting.recipe;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.init.ModBioForgeTabs;
import com.github.elenterius.biomancy.init.ModItems;
import com.github.elenterius.biomancy.init.ModRecipes;
import com.github.elenterius.biomancy.menu.BioForgeTab;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.StackedContents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class BioForgeRecipe implements Recipe<Container> {

	public static final byte DEFAULT_CRAFTING_COST_NUTRIENTS = 1;
	public static final int MAX_INGREDIENTS = 5;
	private final ResourceLocation registryKey;
	private final BioForgeTab tab;
	private final List<IngredientStack> ingredients;
	private final ItemStack result;

	private final NonNullList<Ingredient> vanillaIngredients;

	private final int cost;

	public BioForgeRecipe(ResourceLocation id, BioForgeTab tab, ItemStack result, List<IngredientStack> ingredients, int craftingCostNutrients) {
		registryKey = id;
		this.tab = tab;
		this.result = result;
		this.ingredients = ingredients;

		List<Ingredient> flatIngredients = RecipeUtil.flattenIngredientStacks(ingredients);
		vanillaIngredients = NonNullList.createWithCapacity(flatIngredients.size());
		vanillaIngredients.addAll(flatIngredients);

		cost = craftingCostNutrients;
	}

	public static boolean areRecipesEqual(BioForgeRecipe recipeA, BioForgeRecipe recipeB) {
		return recipeA.isRecipeEqual(recipeB);
	}

	@Override
	public ResourceLocation getId() {
		return registryKey;
	}

	public int getCraftingCostNutrients() {
		return cost;
	}

	public boolean isRecipeEqual(BioForgeRecipe other) {
		return registryKey.equals(other.getId());
	}

	public boolean isCraftable(StackedContents itemCounter) {
		for (IngredientStack ingredientStack : ingredients) {
			if (!ingredientStack.hasSufficientCount(itemCounter)) {
				return false;
			}
		}

		return true;
	}

	@Override
	public boolean matches(Container inv, Level level) {
		int[] countedIngredients = new int[ingredients.size()];
		for (int idx = 0; idx < inv.getContainerSize(); idx++) {
			ItemStack stack = inv.getItem(idx);
			if (!stack.isEmpty()) {
				for (int i = 0; i < ingredients.size(); i++) {
					if (ingredients.get(i).testItem(stack)) {
						countedIngredients[i] += stack.getCount();
						break;
					}
				}
			}
		}

		for (int i = 0; i < ingredients.size(); i++) {
			if (countedIngredients[i] < ingredients.get(i).count()) return false;
		}

		return true;
	}

	@Override
	public ItemStack assemble(Container container, RegistryAccess registryAccess) {
		return result.copy();
	}

	@Override
	public boolean canCraftInDimensions(int width, int height) {
		return width * height >= 0;
	}

	@Override
	public ItemStack getResultItem(RegistryAccess registryAccess) {
		return result;
	}

	@Override
	public NonNullList<Ingredient> getIngredients() {
		return vanillaIngredients;
	}

	public List<IngredientStack> getIngredientQuantities() {
		return ingredients;
	}

	public BioForgeTab getTab() {
		return tab;
	}

	@Override
	public RecipeSerializer<?> getSerializer() {
		return ModRecipes.BIO_FORGING_SERIALIZER.get();
	}

	@Override
	public RecipeType<?> getType() {
		return ModRecipes.BIO_FORGING_RECIPE_TYPE.get();
	}

	@Override
	public ItemStack getToastSymbol() {
		return new ItemStack(ModItems.BIO_FORGE.get());
	}

	public static class Serializer implements RecipeSerializer<BioForgeRecipe> {

		@Override
		public BioForgeRecipe fromJson(ResourceLocation recipeId, JsonObject json) {
			List<IngredientStack> ingredients = RecipeUtil.readIngredientStacks(GsonHelper.getAsJsonArray(json, "ingredients"));

			if (ingredients.isEmpty()) {
				throw new JsonParseException("No ingredients for recipe");
			}

			if (ingredients.size() > MAX_INGREDIENTS) {
				throw new JsonParseException("Too many ingredients for recipe. The maximum is " + MAX_INGREDIENTS);
			}

			ItemStack resultStack = ShapedRecipe.itemStackFromJson(GsonHelper.getAsJsonObject(json, "result"));

			int cost = GsonHelper.getAsInt(json, "nutrientsCost", DEFAULT_CRAFTING_COST_NUTRIENTS);

			BioForgeTab tab = BioForgeTab.fromJson(json);
			if (tab == null) {
				String tabId = BioForgeTab.getTabId(json);
				if (tabId.equals("biomancy:weapons")) {
					tab = ModBioForgeTabs.TOOLS.get();
					BiomancyMod.LOGGER.warn("Recipe {} uses the deprecated \"biomancy:weapons\" bio-forge tab instead of \"biomancy:tools\". Using \"biomancy:tools\" fallback, please update your recipe.", recipeId);
				}
				else throw new JsonSyntaxException("Unknown Bio-Forge tab '%s'".formatted(tabId));
			}

			return new BioForgeRecipe(recipeId, tab, resultStack, ingredients, cost);
		}

		@Nullable
		@Override
		public BioForgeRecipe fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer) {
			ItemStack resultStack = buffer.readItem();

			int ingredientCount = buffer.readVarInt();
			List<IngredientStack> ingredients = new ArrayList<>();
			for (int i = 0; i < ingredientCount; i++) {
				ingredients.add(IngredientStack.fromNetwork(buffer));
			}

			int craftingCost = buffer.readVarInt();

			BioForgeTab tab = BioForgeTab.fromNetwork(buffer);

			return new BioForgeRecipe(recipeId, tab, resultStack, ingredients, craftingCost);
		}

		@Override
		public void toNetwork(FriendlyByteBuf buffer, BioForgeRecipe recipe) {
			buffer.writeItem(recipe.result);

			buffer.writeVarInt(recipe.ingredients.size());
			for (IngredientStack ingredientStack : recipe.ingredients) {
				ingredientStack.toNetwork(buffer);
			}

			buffer.writeVarInt(recipe.getCraftingCostNutrients());

			recipe.tab.toNetwork(buffer);
		}

	}

}
