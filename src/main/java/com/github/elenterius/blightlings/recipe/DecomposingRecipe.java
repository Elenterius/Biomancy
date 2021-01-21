package com.github.elenterius.blightlings.recipe;

import com.github.elenterius.blightlings.init.ModRecipes;
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
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.util.RecipeMatcher;
import net.minecraftforge.registries.ForgeRegistryEntry;

import java.util.ArrayList;
import java.util.List;

public class DecomposingRecipe implements IRecipe<IInventory> {

	public static final int MAX_INGREDIENTS = 2 * 3;
	public static final int MAX_BYPRODUCTS = 3;

	private final NonNullList<Ingredient> recipeIngredients;
	private final ItemStack recipeResult;
	private final int time;
	private final List<OptionalByproduct> optionalByproducts;
	private final ResourceLocation key;
	private final boolean isSimple;

	public DecomposingRecipe(ResourceLocation keyIn, ItemStack result, int timeIn, NonNullList<Ingredient> ingredients, List<OptionalByproduct> byproducts) {
		key = keyIn;
		recipeIngredients = ingredients;
		recipeResult = result;
		time = timeIn;
		optionalByproducts = byproducts;
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

	public List<OptionalByproduct> getOptionalByproducts() {
		return optionalByproducts;
	}

	public int getDecomposingTime() {
		return time;
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

	@Override
	public ResourceLocation getId() {
		return key;
	}

	public static class OptionalByproduct {
		ItemStack stack;
		float chance;

		public OptionalByproduct(ItemStack item, float chance) {
			this.stack = item;
			this.chance = MathHelper.clamp(chance, 0f, 1f);
		}

		public ItemStack getItemStack() {
			return stack;
		}

		public float getChance() {
			return chance;
		}
	}

	public static class Serializer extends ForgeRegistryEntry<IRecipeSerializer<?>> implements IRecipeSerializer<DecomposingRecipe> {

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

		private static List<OptionalByproduct> readByproducts(JsonArray jsonArray) {
			List<OptionalByproduct> list = new ArrayList<>();
			for (int i = 0; i < jsonArray.size(); i++) {
				JsonObject jsonObject = jsonArray.get(i).getAsJsonObject();
				ItemStack stack = ShapedRecipe.deserializeItem(JSONUtils.getJsonObject(jsonObject, "result"));

				float chance = JSONUtils.getFloat(jsonObject, "chance", 1f);
				if (chance <= 0f || chance > 1f) throw new JsonParseException(String.format("Chance %f is outside interval (0, 1]", chance));

				if (!stack.isEmpty()) list.add(new OptionalByproduct(stack, chance));
				else throw new JsonParseException("Defined byproduct is Empty for decomposing recipe");
			}
			return list;
		}

		@Override
		public DecomposingRecipe read(ResourceLocation recipeId, JsonObject json) {
			NonNullList<Ingredient> ingredients = readIngredients(JSONUtils.getJsonArray(json, "ingredients"));

			if (ingredients.isEmpty()) {
				throw new JsonParseException("No ingredients found for " + getRegistryName() + " recipe");
			}
			else if (ingredients.size() > MAX_INGREDIENTS) {
				throw new JsonParseException(String.format("Too many ingredients for %s recipe. Max amount is %d", getRegistryName(), MAX_INGREDIENTS));
			}

			List<OptionalByproduct> byproducts = readByproducts(JSONUtils.getJsonArray(json, "byproducts"));
			if (byproducts.size() > MAX_BYPRODUCTS) {
				throw new JsonParseException(String.format("Too many byproducts for %s recipe. Max amount is %d", getRegistryName(), MAX_BYPRODUCTS));
			}

			ItemStack resultStack = ShapedRecipe.deserializeItem(JSONUtils.getJsonObject(json, "result"));
			int time = JSONUtils.getInt(json, "decomposingtime", 100);

			return new DecomposingRecipe(recipeId, resultStack, time, ingredients, byproducts);
		}

		@Override
		public DecomposingRecipe read(ResourceLocation recipeId, PacketBuffer buffer) {
			//client side
			ItemStack resultStack = buffer.readItemStack();
			int time = buffer.readInt();

			int byproductCount = buffer.readVarInt();
			List<OptionalByproduct> byproducts = new ArrayList<>();
			for (int j = 0; j < byproductCount; ++j) {
				byproducts.add(new OptionalByproduct(buffer.readItemStack(), buffer.readFloat()));
			}

			int ingredientCount = buffer.readVarInt();
			NonNullList<Ingredient> ingredients = NonNullList.withSize(ingredientCount, Ingredient.EMPTY);
			for (int j = 0; j < ingredients.size(); ++j) {
				ingredients.set(j, Ingredient.read(buffer));
			}

			return new DecomposingRecipe(recipeId, resultStack, time, ingredients, byproducts);
		}

		@Override
		public void write(PacketBuffer buffer, DecomposingRecipe recipe) {
			//server side
			buffer.writeItemStack(recipe.recipeResult);
			buffer.writeInt(recipe.time);

			buffer.writeVarInt(recipe.optionalByproducts.size());
			for (OptionalByproduct byproduct : recipe.optionalByproducts) {
				buffer.writeItemStack(byproduct.stack);
				buffer.writeFloat(byproduct.chance);
			}

			buffer.writeVarInt(recipe.recipeIngredients.size());
			for (Ingredient ingredient : recipe.recipeIngredients) {
				ingredient.write(buffer);
			}
		}
	}

}
