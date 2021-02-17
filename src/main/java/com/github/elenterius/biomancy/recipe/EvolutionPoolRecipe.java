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

public class EvolutionPoolRecipe implements IRecipe<IInventory> {

	public static final int MAX_INGREDIENTS = 6;

	private final NonNullList<Ingredient> recipeIngredients;
	private final ItemStack recipeResult;
	private final int time;
	private final boolean isSimple;

	private final ResourceLocation registryId;

	public EvolutionPoolRecipe(ResourceLocation keyIn, ItemStack result, int timeIn, NonNullList<Ingredient> ingredients) {
		registryId = keyIn;
		recipeIngredients = ingredients;
		recipeResult = result;
		time = timeIn;
		isSimple = ingredients.stream().allMatch(Ingredient::isSimple);
	}

	@Override
	public boolean matches(IInventory inv, World worldIn) {
		RecipeItemHelper recipeItemHelper = new RecipeItemHelper();
		ArrayList<ItemStack> inputs = new ArrayList<>();
		int ingredientCount = 0;

		for (int idx = 0; idx < inv.getSizeInventory(); idx++) {
			ItemStack stack = inv.getStackInSlot(idx);
			if (!stack.isEmpty()) {
				ingredientCount++;
				if (isSimple) recipeItemHelper.func_221264_a(stack, 1);
				else inputs.add(stack);
			}
		}

		return ingredientCount == recipeIngredients.size() && (isSimple ? recipeItemHelper.canCraft(this, null) : RecipeMatcher.findMatches(inputs, recipeIngredients) != null);
	}

	@Override
	public ItemStack getCraftingResult(IInventory inv) {
		return recipeResult.copy();
	}

	@Override
	public boolean canFit(int width, int height) {
		return width * height >= recipeIngredients.size();
	}

	@Override
	public ItemStack getRecipeOutput() {
		return recipeResult;
	}

	@Override
	public NonNullList<Ingredient> getIngredients() {
		return recipeIngredients;
	}

	@Override
	public ResourceLocation getId() {
		return registryId;
	}

	@Override
	public IRecipeSerializer<?> getSerializer() {
		return ModRecipes.EVOLUTION_POOL_SERIALIZER.get();
	}

	@Override
	public IRecipeType<?> getType() {
		return ModRecipes.EVOLUTION_POOL_RECIPE_TYPE;
	}

	public int getCraftingTime() {
		return time;
	}

	public static class Serializer extends ForgeRegistryEntry<IRecipeSerializer<?>> implements IRecipeSerializer<EvolutionPoolRecipe> {

		private static NonNullList<Ingredient> readIngredients(JsonArray jsonArray) {
			NonNullList<Ingredient> list = NonNullList.create();
			for (int i = 0; i < jsonArray.size(); i++) {
				Ingredient ingredient = Ingredient.deserialize(jsonArray.get(i));
				if (!ingredient.hasNoMatchingItems()) {
					list.add(ingredient);
				}
			}
			return list;
		}

		@Override
		public EvolutionPoolRecipe read(ResourceLocation recipeId, JsonObject json) {
			NonNullList<Ingredient> ingredients = readIngredients(JSONUtils.getJsonArray(json, "ingredients"));

			if (ingredients.isEmpty()) {
				throw new JsonParseException("No ingredients found for " + getRegistryName() + " recipe");
			}
			else if (ingredients.size() > MAX_INGREDIENTS) {
				throw new JsonParseException(String.format("Too many ingredients for %s recipe. Max amount is %d", getRegistryName(), MAX_INGREDIENTS));
			}

			ItemStack resultStack = ShapedRecipe.deserializeItem(JSONUtils.getJsonObject(json, "result"));
			int time = JSONUtils.getInt(json, "time", 100);

			return new EvolutionPoolRecipe(recipeId, resultStack, time, ingredients);
		}

		@Nullable
		@Override
		public EvolutionPoolRecipe read(ResourceLocation recipeId, PacketBuffer buffer) {
			//client side
			ItemStack resultStack = buffer.readItemStack();
			int time = buffer.readInt();

			int ingredientCount = buffer.readVarInt();
			NonNullList<Ingredient> ingredients = NonNullList.withSize(ingredientCount, Ingredient.EMPTY);
			for (int j = 0; j < ingredients.size(); ++j) {
				ingredients.set(j, Ingredient.read(buffer));
			}

			return new EvolutionPoolRecipe(recipeId, resultStack, time, ingredients);
		}

		@Override
		public void write(PacketBuffer buffer, EvolutionPoolRecipe recipe) {
			//server side
			buffer.writeItemStack(recipe.recipeResult);
			buffer.writeInt(recipe.time);

			buffer.writeVarInt(recipe.recipeIngredients.size());
			for (Ingredient ingredient : recipe.recipeIngredients) {
				ingredient.write(buffer);
			}
		}
	}
}
