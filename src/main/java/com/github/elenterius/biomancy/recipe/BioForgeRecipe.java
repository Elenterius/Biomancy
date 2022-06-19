package com.github.elenterius.biomancy.recipe;

import com.github.elenterius.biomancy.init.ModRecipeBooks;
import com.github.elenterius.biomancy.init.ModRecipes;
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
import net.minecraftforge.registries.ForgeRegistryEntry;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class BioForgeRecipe implements Recipe<Container> {

	private final ResourceLocation registryKey;
	public static final int MAX_INGREDIENTS = 5;
	private final ModRecipeBooks.BioForgeCategory category;
	private final List<IngredientQuantity> ingredients;
	private final ItemStack result;

	private final NonNullList<Ingredient> vanillaIngredients;

	public BioForgeRecipe(ResourceLocation id, ModRecipeBooks.BioForgeCategory category, ItemStack result, List<IngredientQuantity> ingredients) {
		registryKey = id;
		this.category = category;
		this.result = result;
		this.ingredients = ingredients;
		//isSimple = ingredients.stream().allMatch(ingredientQuantity -> ingredientQuantity.ingredient().isSimple());

		//TODO: remove this shite
		//Note: we can't :(
		List<Ingredient> unrolledIngredients = new ArrayList<>();
		for (IngredientQuantity ingredientQuantity : ingredients) {
			Ingredient ingredient = ingredientQuantity.ingredient();
			for (int i = 0; i < ingredientQuantity.count(); i++) {
				unrolledIngredients.add(ingredient);
			}
		}

		vanillaIngredients = NonNullList.createWithCapacity(unrolledIngredients.size());
		vanillaIngredients.addAll(unrolledIngredients);
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
		for (IngredientQuantity ingredientQuantity : ingredients) {
			ItemStack stack = ingredientQuantity.ingredient().getItems()[0];
			int requiredAmount = ingredientQuantity.count();
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

	public List<IngredientQuantity> getIngredientQuantities() {
		return ingredients;
	}

	public ModRecipeBooks.BioForgeCategory getCategory() {
		return category;
	}

	@Override
	public RecipeSerializer<?> getSerializer() {
		return ModRecipes.BIO_FORGING_SERIALIZER.get();
	}

	@Override
	public RecipeType<?> getType() {
		return ModRecipes.BIO_FORGING_RECIPE_TYPE;
	}

	public static class Serializer extends ForgeRegistryEntry<RecipeSerializer<?>> implements RecipeSerializer<BioForgeRecipe> {

		@Override
		public BioForgeRecipe fromJson(ResourceLocation recipeId, JsonObject json) {
			List<IngredientQuantity> ingredients = RecipeUtil.readQuantitativeIngredients(GsonHelper.getAsJsonArray(json, "ingredient_quantities"));

			if (ingredients.isEmpty()) {
				throw new JsonParseException("No ingredients for recipe");
			}

			if (ingredients.size() > MAX_INGREDIENTS) {
				throw new JsonParseException("Too many ingredients for recipe. The maximum is " + MAX_INGREDIENTS);
			}

			ItemStack resultStack = ShapedRecipe.itemStackFromJson(GsonHelper.getAsJsonObject(json, "result"));
			ModRecipeBooks.BioForgeCategory category = ModRecipeBooks.BioForgeCategory.fromJson(json);

			return new BioForgeRecipe(recipeId, category, resultStack, ingredients);
		}

		@Nullable
		@Override
		public BioForgeRecipe fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer) {
			ItemStack resultStack = buffer.readItem();

			int ingredientCount = buffer.readVarInt();
			List<IngredientQuantity> ingredients = new ArrayList<>();
			for (int j = 0; j < ingredientCount; ++j) {
				ingredients.add(IngredientQuantity.fromNetwork(buffer));
			}

			ModRecipeBooks.BioForgeCategory category = ModRecipeBooks.BioForgeCategory.fromNetwork(buffer);

			return new BioForgeRecipe(recipeId, category, resultStack, ingredients);
		}

		@Override
		public void toNetwork(FriendlyByteBuf buffer, BioForgeRecipe recipe) {
			buffer.writeItem(recipe.result);

			buffer.writeVarInt(recipe.ingredients.size());
			for (IngredientQuantity input : recipe.ingredients) {
				input.toNetwork(buffer);
			}

			recipe.category.toNetwork(buffer);
		}

	}

}
