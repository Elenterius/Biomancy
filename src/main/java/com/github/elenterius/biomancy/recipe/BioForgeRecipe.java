package com.github.elenterius.biomancy.recipe;

import com.github.elenterius.biomancy.init.ModRecipes;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.core.NonNullList;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.ForgeRegistryEntry;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class BioForgeRecipe implements Recipe<Container> {

	public static final int MAX_INGREDIENTS = 9;
	private final ResourceLocation registryKey;
	private final List<IngredientQuantity> ingredients;
	private final NonNullList<Ingredient> vanillaIngredients;
	private final ItemStack resultStack;
	private final boolean isSimple;

	public BioForgeRecipe(ResourceLocation registryKey, ItemStack resultStack, List<IngredientQuantity> ingredients) {
		this.registryKey = registryKey;
		this.resultStack = resultStack;
		this.ingredients = ingredients;
		isSimple = ingredients.stream().allMatch(ingredientQuantity -> ingredientQuantity.ingredient().isSimple());
		vanillaIngredients = NonNullList.createWithCapacity(ingredients.size());
		for (IngredientQuantity ingredientQuantity : ingredients) {
			vanillaIngredients.add(ingredientQuantity.ingredient());
		}
	}

	@Override
	public boolean matches(Container container, Level level) {
		return false;
	}

	@Override
	public ItemStack assemble(Container container) {
		return resultStack.copy();
	}

	@Override
	public boolean canCraftInDimensions(int width, int height) {
		return false;
	}

	@Override
	public ItemStack getResultItem() {
		return resultStack;
	}

	@Override
	public ResourceLocation getId() {
		return registryKey;
	}

	@Override
	public RecipeSerializer<?> getSerializer() {
		return ModRecipes.BIO_FORGING_SERIALIZER.get();
	}

	@Override
	public RecipeType<?> getType() {
		return ModRecipes.BIO_FORGING_RECIPE_TYPE;
	}

	@Override
	public NonNullList<Ingredient> getIngredients() {
		return vanillaIngredients;
	}

	public List<IngredientQuantity> getIngredientQuantities() {
		return ingredients;
	}

	public static class Serializer extends ForgeRegistryEntry<RecipeSerializer<?>> implements RecipeSerializer<BioForgeRecipe> {

		@Override
		public BioForgeRecipe fromJson(ResourceLocation recipeId, JsonObject jsonObject) {
			List<IngredientQuantity> list = RecipeUtil.readQuantitativeIngredients(GsonHelper.getAsJsonArray(jsonObject, "inputs"));
			if (list.isEmpty()) {
				throw new JsonParseException("No ingredients for recipe");
			}
			else if (list.size() > MAX_INGREDIENTS) {
				throw new JsonParseException("Too many ingredients for recipe. The maximum is " + MAX_INGREDIENTS);
			}
			else {
				ItemStack stack = ShapedRecipe.itemStackFromJson(GsonHelper.getAsJsonObject(jsonObject, "result"));
				return new BioForgeRecipe(recipeId, stack, list);
			}
		}

		@Nullable
		@Override
		public BioForgeRecipe fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer) {
			int inputCount = buffer.readVarInt();
			List<IngredientQuantity> inputs = new ArrayList<>();
			for (int j = 0; j < inputCount; ++j) {
				inputs.add(IngredientQuantity.fromNetwork(buffer));
			}

			ItemStack stack = buffer.readItem();
			return new BioForgeRecipe(recipeId, stack, inputs);
		}

		@Override
		public void toNetwork(FriendlyByteBuf buffer, BioForgeRecipe recipe) {
			buffer.writeVarInt(recipe.ingredients.size());
			for (IngredientQuantity input : recipe.ingredients) {
				input.toNetwork(buffer);
			}

			buffer.writeItem(recipe.resultStack);
		}

	}

}
