package com.github.elenterius.biomancy.recipe;

import com.github.elenterius.biomancy.init.ModRecipes;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.*;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.util.RecipeMatcher;
import net.minecraftforge.registries.ForgeRegistryEntry;

import javax.annotation.Nullable;
import java.util.ArrayList;

public class EvolutionPoolRecipe extends AbstractProductionRecipe {

	public static final int MAX_INGREDIENTS = 6;

	private final NonNullList<Ingredient> recipeIngredients;
	private final ItemStack recipeResult;
	private final boolean isSimple;


	public EvolutionPoolRecipe(ResourceLocation registryKey, ItemStack result, int craftingTime, NonNullList<Ingredient> ingredients) {
		super(registryKey, craftingTime);
		recipeIngredients = ingredients;
		recipeResult = result;
		isSimple = ingredients.stream().allMatch(Ingredient::isSimple);
	}

	@Override
	public boolean matches(IInventory inv, World worldIn) {
		RecipeItemHelper recipeItemHelper = new RecipeItemHelper();
		ArrayList<ItemStack> inputs = new ArrayList<>();
		int ingredientCount = 0;

		for (int idx = 0; idx < inv.getContainerSize(); idx++) {
			ItemStack stack = inv.getItem(idx);
			if (!stack.isEmpty()) {
				ingredientCount++;
				if (isSimple) recipeItemHelper.accountStack(stack, 1);
				else inputs.add(stack);
			}
		}

		return ingredientCount == recipeIngredients.size() && (isSimple ? recipeItemHelper.canCraft(this, null) : RecipeMatcher.findMatches(inputs, recipeIngredients) != null);
	}

	@Override
	public ItemStack assemble(IInventory inv) {
		return recipeResult.copy();
	}

	@Override
	public boolean canCraftInDimensions(int width, int height) {
		return width * height >= recipeIngredients.size();
	}

	@Override
	public ItemStack getResultItem() {
		return recipeResult;
	}

	@Override
	public NonNullList<Ingredient> getIngredients() {
		return recipeIngredients;
	}

	@Override
	public IRecipeSerializer<?> getSerializer() {
		return ModRecipes.EVOLUTION_POOL_SERIALIZER.get();
	}

	@Override
	public IRecipeType<?> getType() {
		return ModRecipes.EVOLUTION_POOL_RECIPE_TYPE;
	}

	public static class Serializer extends ForgeRegistryEntry<IRecipeSerializer<?>> implements IRecipeSerializer<EvolutionPoolRecipe> {

		private static NonNullList<Ingredient> readIngredients(JsonArray jsonArray) {
			NonNullList<Ingredient> list = NonNullList.create();
			for (int i = 0; i < jsonArray.size(); i++) {
				Ingredient ingredient = Ingredient.fromJson(jsonArray.get(i));
				if (!ingredient.isEmpty()) {
					list.add(ingredient);
				}
			}
			return list;
		}

		@Override
		public EvolutionPoolRecipe fromJson(ResourceLocation recipeId, JsonObject json) {
			NonNullList<Ingredient> ingredients = readIngredients(JSONUtils.getAsJsonArray(json, "ingredients"));

			if (ingredients.isEmpty()) {
				throw new JsonParseException("No ingredients found for " + getRegistryName() + " recipe");
			}
			else if (ingredients.size() > MAX_INGREDIENTS) {
				throw new JsonParseException(String.format("Too many ingredients for %s recipe. Max amount is %d", getRegistryName(), MAX_INGREDIENTS));
			}

			ItemStack resultStack = ShapedRecipe.itemFromJson(JSONUtils.getAsJsonObject(json, "result"));
			int time = JSONUtils.getAsInt(json, "time", 100);

			return new EvolutionPoolRecipe(recipeId, resultStack, time, ingredients);
		}

		@Nullable
		@Override
		public EvolutionPoolRecipe fromNetwork(ResourceLocation recipeId, PacketBuffer buffer) {
			//client side
			ItemStack resultStack = buffer.readItem();
			int time = buffer.readInt();

			int ingredientCount = buffer.readVarInt();
			NonNullList<Ingredient> ingredients = NonNullList.withSize(ingredientCount, Ingredient.EMPTY);
			for (int j = 0; j < ingredients.size(); ++j) {
				ingredients.set(j, Ingredient.fromNetwork(buffer));
			}

			return new EvolutionPoolRecipe(recipeId, resultStack, time, ingredients);
		}

		@Override
		public void toNetwork(PacketBuffer buffer, EvolutionPoolRecipe recipe) {
			//server side
			buffer.writeItem(recipe.recipeResult);
			buffer.writeInt(recipe.getCraftingTime());

			buffer.writeVarInt(recipe.recipeIngredients.size());
			for (Ingredient ingredient : recipe.recipeIngredients) {
				ingredient.toNetwork(buffer);
			}
		}
	}
}
