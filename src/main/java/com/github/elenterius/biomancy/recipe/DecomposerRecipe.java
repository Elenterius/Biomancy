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

import java.util.ArrayList;
import java.util.List;

public class DecomposerRecipe extends AbstractBioMechanicalRecipe {

	public static final int MAX_INGREDIENTS = 2 * 3;
	public static final int MAX_BYPRODUCTS = 3;

	private final NonNullList<Ingredient> recipeIngredients;
	private final ItemStack recipeResult;
	private final List<Byproduct> byproducts;
	private final boolean isSimple;

	public DecomposerRecipe(ResourceLocation registryKey, ItemStack result, int craftingTime, NonNullList<Ingredient> ingredients, List<Byproduct> byproducts) {
		super(registryKey, craftingTime);
		recipeIngredients = ingredients;
		recipeResult = result;
		this.byproducts = byproducts;
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
	public NonNullList<Ingredient> getIngredients() {
		return recipeIngredients;
	}

	public List<Byproduct> getByproducts() {
		return byproducts;
	}

	@Override
	public ItemStack getRecipeOutput() {
		return recipeResult;
	}

	@Override
	public IRecipeSerializer<?> getSerializer() {
		return ModRecipes.DECOMPOSING_SERIALIZER.get();
	}

	@Override
	public IRecipeType<?> getType() {
		return ModRecipes.DECOMPOSING_RECIPE_TYPE;
	}

	public static class Serializer extends ForgeRegistryEntry<IRecipeSerializer<?>> implements IRecipeSerializer<DecomposerRecipe> {

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

		private static List<Byproduct> readByproducts(JsonArray jsonArray) {
			List<Byproduct> list = new ArrayList<>();
			for (int i = 0; i < jsonArray.size(); i++) {
				list.add(Byproduct.deserialize(jsonArray.get(i).getAsJsonObject()));
			}
			return list;
		}

		@Override
		public DecomposerRecipe read(ResourceLocation recipeId, JsonObject json) {
			NonNullList<Ingredient> ingredients = readIngredients(JSONUtils.getJsonArray(json, "ingredients"));

			if (ingredients.isEmpty()) {
				throw new JsonParseException("No ingredients found for " + getRegistryName() + " recipe");
			}
			else if (ingredients.size() > MAX_INGREDIENTS) {
				throw new JsonParseException(String.format("Too many ingredients for %s recipe. Max amount is %d", getRegistryName(), MAX_INGREDIENTS));
			}

			List<Byproduct> byproducts = readByproducts(JSONUtils.getJsonArray(json, "byproducts"));
			if (byproducts.size() > MAX_BYPRODUCTS) {
				throw new JsonParseException(String.format("Too many byproducts for %s recipe. Max amount is %d", getRegistryName(), MAX_BYPRODUCTS));
			}

			ItemStack resultStack = ShapedRecipe.deserializeItem(JSONUtils.getJsonObject(json, "result"));
			int time = JSONUtils.getInt(json, "time", 100);

			return new DecomposerRecipe(recipeId, resultStack, time, ingredients, byproducts);
		}

		@Override
		public DecomposerRecipe read(ResourceLocation recipeId, PacketBuffer buffer) {
			//client side
			ItemStack resultStack = buffer.readItemStack();
			int time = buffer.readInt();

			int byproductCount = buffer.readVarInt();
			List<Byproduct> byproducts = new ArrayList<>();
			for (int j = 0; j < byproductCount; ++j) {
				byproducts.add(Byproduct.read(buffer));
			}

			int ingredientCount = buffer.readVarInt();
			NonNullList<Ingredient> ingredients = NonNullList.withSize(ingredientCount, Ingredient.EMPTY);
			for (int j = 0; j < ingredients.size(); ++j) {
				ingredients.set(j, Ingredient.read(buffer));
			}

			return new DecomposerRecipe(recipeId, resultStack, time, ingredients, byproducts);
		}

		@Override
		public void write(PacketBuffer buffer, DecomposerRecipe recipe) {
			//server side
			buffer.writeItemStack(recipe.recipeResult);
			buffer.writeInt(recipe.getCraftingTime());

			buffer.writeVarInt(recipe.byproducts.size());
			for (Byproduct byproduct : recipe.byproducts) {
				byproduct.write(buffer);
			}

			buffer.writeVarInt(recipe.recipeIngredients.size());
			for (Ingredient ingredient : recipe.recipeIngredients) {
				ingredient.write(buffer);
			}
		}
	}

}
