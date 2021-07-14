package com.github.elenterius.biomancy.recipe;

import com.github.elenterius.biomancy.init.ModRecipes;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.ShapedRecipe;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.registries.ForgeRegistryEntry;

import java.util.ArrayList;
import java.util.List;

public class DecomposerRecipe extends AbstractProductionRecipe {

	public static final int MAX_INGREDIENTS = 1;
	public static final int MAX_BYPRODUCTS = 5;

	private final Ingredient ingredient;
	private final int ingredientCount;
	private final ItemStack recipeResult;
	private final List<Byproduct> byproducts;

	public DecomposerRecipe(ResourceLocation registryKey, ItemStack result, int craftingTime, Ingredient ingredientIn, int ingredientCountIn, List<Byproduct> byproductsIn) {
		super(registryKey, craftingTime);
		ingredient = ingredientIn;
		recipeResult = result;
		ingredientCount = ingredientCountIn;
		byproducts = byproductsIn;
	}

	@Override
	public boolean matches(IInventory inv, World worldIn) {
		ItemStack stack = inv.getStackInSlot(0);
		return ingredient.test(stack) && stack.getCount() >= ingredientCount;
	}

	@Override
	public ItemStack getCraftingResult(IInventory inv) {
		return recipeResult.copy();
	}

	@Override
	public boolean canFit(int width, int height) {
		return width * height == 1;
	}

	@Override
	public NonNullList<Ingredient> getIngredients() {
		NonNullList<Ingredient> list = NonNullList.create();
		list.add(ingredient);
		return list;
	}

	public Ingredient getIngredient() {
		return ingredient;
	}

	public int getIngredientCount() {
		return ingredientCount;
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

		private static Ingredient readIngredient(JsonObject jsonObj) {
			if (JSONUtils.isJsonArray(jsonObj, "ingredient")) return Ingredient.deserialize(JSONUtils.getJsonArray(jsonObj, "ingredient"));
			else return Ingredient.deserialize(JSONUtils.getJsonObject(jsonObj, "ingredient"));
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
			JsonObject input = JSONUtils.getJsonObject(json, "input");
			Ingredient ingredient = readIngredient(input);
			int ingredientCount = JSONUtils.getInt(input, "count", 1);

			List<Byproduct> byproducts = readByproducts(JSONUtils.getJsonArray(json, "byproducts"));
			if (byproducts.size() > MAX_BYPRODUCTS) {
				throw new JsonParseException(String.format("Too many byproducts for %s recipe. Max amount is %d", getRegistryName(), MAX_BYPRODUCTS));
			}

			ItemStack resultStack = ShapedRecipe.deserializeItem(JSONUtils.getJsonObject(json, "result"));
			int time = JSONUtils.getInt(json, "time", 100);

			return new DecomposerRecipe(recipeId, resultStack, time, ingredient, ingredientCount, byproducts);
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

			Ingredient ingredient = Ingredient.read(buffer);
			int ingredientCount = buffer.readVarInt();

			return new DecomposerRecipe(recipeId, resultStack, time, ingredient, ingredientCount, byproducts);
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

			recipe.ingredient.write(buffer);
			buffer.writeVarInt(recipe.ingredientCount);
		}
	}

}
