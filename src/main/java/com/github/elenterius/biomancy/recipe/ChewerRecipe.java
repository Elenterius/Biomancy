package com.github.elenterius.biomancy.recipe;

import com.github.elenterius.biomancy.init.ModRecipes;
import com.google.gson.JsonObject;
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

import javax.annotation.Nullable;

public class ChewerRecipe extends AbstractProductionRecipe {

	private final Ingredient ingredient;
	private final ItemStack recipeResult;

	public ChewerRecipe(ResourceLocation registryKey, ItemStack result, int craftingTimeIn, Ingredient ingredientIn) {
		super(registryKey, craftingTimeIn);
		ingredient = ingredientIn;
		recipeResult = result;
	}

	@Override
	public boolean matches(IInventory inv, World worldIn) {
		return ingredient.test(inv.getItem(0));
	}

	@Override
	public ItemStack assemble(IInventory inv) {
		return recipeResult.copy();
	}

	@Override
	public boolean canCraftInDimensions(int width, int height) {
		return true;
	}

	@Override
	public ItemStack getResultItem() {
		return recipeResult;
	}

	@Override
	public NonNullList<Ingredient> getIngredients() {
		NonNullList<Ingredient> list = NonNullList.create();
		list.add(ingredient);
		return list;
	}

	@Override
	public IRecipeSerializer<?> getSerializer() {
		return ModRecipes.CHEWER_SERIALIZER.get();
	}

	@Override
	public IRecipeType<?> getType() {
		return ModRecipes.CHEWER_RECIPE_TYPE;
	}

	public static class Serializer extends ForgeRegistryEntry<IRecipeSerializer<?>> implements IRecipeSerializer<ChewerRecipe> {

		private static Ingredient readIngredient(JsonObject jsonObj) {
			if (JSONUtils.isArrayNode(jsonObj, "ingredient")) return Ingredient.fromJson(JSONUtils.getAsJsonArray(jsonObj, "ingredient"));
			else return Ingredient.fromJson(JSONUtils.getAsJsonObject(jsonObj, "ingredient"));
		}

		@Override
		public ChewerRecipe fromJson(ResourceLocation recipeId, JsonObject json) {
			Ingredient ingredient = readIngredient(json);
			ItemStack resultStack = ShapedRecipe.itemFromJson(JSONUtils.getAsJsonObject(json, "result"));
			int time = JSONUtils.getAsInt(json, "time", 100);
			return new ChewerRecipe(recipeId, resultStack, time, ingredient);
		}

		@Nullable
		@Override
		public ChewerRecipe fromNetwork(ResourceLocation recipeId, PacketBuffer buffer) {
			//client side
			ItemStack resultStack = buffer.readItem();
			int time = buffer.readInt();
			Ingredient ingredient = Ingredient.fromNetwork(buffer);

			return new ChewerRecipe(recipeId, resultStack, time, ingredient);
		}

		@Override
		public void toNetwork(PacketBuffer buffer, ChewerRecipe recipe) {
			//server side
			buffer.writeItem(recipe.recipeResult);
			buffer.writeInt(recipe.getCraftingTime());
			recipe.ingredient.toNetwork(buffer);
		}
	}
}
