package com.github.elenterius.biomancy.recipe;

import com.github.elenterius.biomancy.init.ModItems;
import com.github.elenterius.biomancy.init.ModRecipes;
import com.github.elenterius.biomancy.world.inventory.menu.BioForgeTab;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.core.NonNullList;
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

	private final ResourceLocation registryKey;
	public static final int MAX_INGREDIENTS = 5;
	private final BioForgeTab tab;
	private final List<IngredientStack> ingredients;
	private final ItemStack result;

	private final NonNullList<Ingredient> vanillaIngredients;

	public BioForgeRecipe(ResourceLocation id, BioForgeTab tab, ItemStack result, List<IngredientStack> ingredients) {
		registryKey = id;
		this.tab = tab;
		this.result = result;
		this.ingredients = ingredients;

		List<Ingredient> flatIngredients = RecipeUtil.flattenIngredientStacks(ingredients);
		vanillaIngredients = NonNullList.createWithCapacity(flatIngredients.size());
		vanillaIngredients.addAll(flatIngredients);
	}

	@Override
	public ResourceLocation getId() {
		return registryKey;
	}

	public boolean isRecipeEqual(BioForgeRecipe other) {
		return registryKey.equals(other.getId());
	}

	public static boolean areRecipesEqual(BioForgeRecipe recipeA, BioForgeRecipe recipeB) {
		return recipeA.isRecipeEqual(recipeB);
	}

	public boolean isCraftable(StackedContents itemCounter) {
		//TODO: make this more precise
		boolean isCraftable = true;
		for (IngredientStack ingredientStack : ingredients) {
			ItemStack stack = ingredientStack.ingredient().getItems()[0];
			int requiredAmount = ingredientStack.count();
			int foundAmount = itemCounter.contents.get(StackedContents.getStackingIndex(stack));
			if (foundAmount < requiredAmount) {
				isCraftable = false;
				break;
			}
		}
		return isCraftable;
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
	public ItemStack assemble(Container container) {
		return result.copy();
	}

	@Override
	public boolean canCraftInDimensions(int width, int height) {
		return width * height >= 0;
	}

	@Override
	public ItemStack getResultItem() {
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
			List<IngredientStack> ingredients = RecipeUtil.readIngredientStacks(GsonHelper.getAsJsonArray(json, "ingredient_quantities"));

			if (ingredients.isEmpty()) {
				throw new JsonParseException("No ingredients for recipe");
			}

			if (ingredients.size() > MAX_INGREDIENTS) {
				throw new JsonParseException("Too many ingredients for recipe. The maximum is " + MAX_INGREDIENTS);
			}

			ItemStack resultStack = ShapedRecipe.itemStackFromJson(GsonHelper.getAsJsonObject(json, "result"));
			BioForgeTab tab = BioForgeTab.fromJson(json);

			return new BioForgeRecipe(recipeId, tab, resultStack, ingredients);
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

			BioForgeTab tab = BioForgeTab.fromNetwork(buffer);

			return new BioForgeRecipe(recipeId, tab, resultStack, ingredients);
		}

		@Override
		public void toNetwork(FriendlyByteBuf buffer, BioForgeRecipe recipe) {
			buffer.writeItem(recipe.result);

			buffer.writeVarInt(recipe.ingredients.size());
			for (IngredientStack ingredientStack : recipe.ingredients) {
				ingredientStack.toNetwork(buffer);
			}

			recipe.tab.toNetwork(buffer);
		}

	}

}
