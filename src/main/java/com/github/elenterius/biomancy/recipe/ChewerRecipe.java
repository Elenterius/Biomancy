package com.github.elenterius.biomancy.recipe;

import com.github.elenterius.biomancy.init.ModRecipes;
import com.google.gson.JsonObject;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.*;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.registries.ForgeRegistryEntry;

import javax.annotation.Nullable;

public class ChewerRecipe implements IRecipe<IInventory> {

	private final ResourceLocation registryId;
	private final Ingredient ingredient;
	private final ItemStack recipeResult;
	private final int time;

	public ChewerRecipe(ResourceLocation keyIn, ItemStack result, int timeIn, Ingredient ingredientIn) {
		registryId = keyIn;
		ingredient = ingredientIn;
		recipeResult = result;
		time = timeIn;
	}

	@Override
	public boolean matches(IInventory inv, World worldIn) {
		return ingredient.test(inv.getStackInSlot(0));
	}

	@Override
	public ItemStack getCraftingResult(IInventory inv) {
		return recipeResult.copy();
	}

	@Override
	public boolean canFit(int width, int height) {
		return true;
	}

	@Override
	public ItemStack getRecipeOutput() {
		return recipeResult;
	}

	@Override
	public NonNullList<Ingredient> getIngredients() {
		NonNullList<Ingredient> list = NonNullList.create();
		list.add(ingredient);
		return list;
	}

	@Override
	public ResourceLocation getId() {
		return registryId;
	}

	@Override
	public IRecipeSerializer<?> getSerializer() {
		return ModRecipes.CHEWER_SERIALIZER.get();
	}

	@Override
	public IRecipeType<?> getType() {
		return ModRecipes.CHEWER_RECIPE_TYPE;
	}

	public int getCraftingTime() {
		return time;
	}

	public static class Serializer extends ForgeRegistryEntry<IRecipeSerializer<?>> implements IRecipeSerializer<ChewerRecipe> {

		private static Ingredient readIngredient(JsonObject jsonObj) {
			if (JSONUtils.isJsonArray(jsonObj, "ingredient")) return Ingredient.deserialize(JSONUtils.getJsonArray(jsonObj, "ingredient"));
			else return Ingredient.deserialize(JSONUtils.getJsonObject(jsonObj, "ingredient"));
		}

		@Override
		public ChewerRecipe read(ResourceLocation recipeId, JsonObject json) {
			Ingredient ingredient = readIngredient(json);
			ItemStack resultStack = ShapedRecipe.deserializeItem(JSONUtils.getJsonObject(json, "result"));
			int time = JSONUtils.getInt(json, "time", 100);
			return new ChewerRecipe(recipeId, resultStack, time, ingredient);
		}

		@Nullable
		@Override
		public ChewerRecipe read(ResourceLocation recipeId, PacketBuffer buffer) {
			//client side
			ItemStack resultStack = buffer.readItemStack();
			int time = buffer.readInt();
			Ingredient ingredient = Ingredient.read(buffer);

			return new ChewerRecipe(recipeId, resultStack, time, ingredient);
		}

		@Override
		public void write(PacketBuffer buffer, ChewerRecipe recipe) {
			//server side
			buffer.writeItemStack(recipe.recipeResult);
			buffer.writeInt(recipe.time);
			recipe.ingredient.write(buffer);
		}
	}
}
