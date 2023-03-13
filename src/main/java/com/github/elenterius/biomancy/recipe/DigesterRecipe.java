package com.github.elenterius.biomancy.recipe;

import com.github.elenterius.biomancy.init.ModItems;
import com.github.elenterius.biomancy.init.ModRecipes;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.core.NonNullList;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;

public class DigesterRecipe extends AbstractProductionRecipe {

	private final Ingredient recipeIngredient;
	private final ItemStack recipeResult;

	public DigesterRecipe(ResourceLocation id, ItemStack result, int craftingTime, Ingredient ingredient) {
		super(id, craftingTime);
		recipeIngredient = ingredient;
		recipeResult = result;
	}

	@Override
	public boolean matches(Container inv, Level worldIn) {
		return recipeIngredient.test(inv.getItem(0));
	}

	@Override
	public ItemStack assemble(Container inv) {
		return recipeResult.copy();
	}

	@Override
	public boolean canCraftInDimensions(int width, int height) {
		return width * height <= 1;
	}

	@Override
	public ItemStack getResultItem() {
		return recipeResult;
	}

	public Ingredient getIngredient() {
		return recipeIngredient;
	}

	@Override
	public NonNullList<Ingredient> getIngredients() {
		return NonNullList.of(Ingredient.EMPTY, recipeIngredient);
	}


	@Override
	public RecipeSerializer<?> getSerializer() {
		return ModRecipes.DIGESTING_SERIALIZER.get();
	}

	@Override
	public RecipeType<?> getType() {
		return ModRecipes.DIGESTING_RECIPE_TYPE.get();
	}

	@Override
	public ItemStack getToastSymbol() {
		return new ItemStack(ModItems.DIGESTER.get());
	}

	public static class Serializer implements RecipeSerializer<DigesterRecipe> {

		@Override
		public DigesterRecipe fromJson(ResourceLocation recipeId, JsonObject json) {

			Ingredient ingredient = Ingredient.fromJson(GsonHelper.getAsJsonObject(json, "ingredient"));

			if (ingredient.isEmpty()) {
				throw new JsonParseException("No ingredient found for %s/%s recipe".formatted(ForgeRegistries.RECIPE_SERIALIZERS.getKey(this), recipeId));
			}

			ItemStack resultStack = ShapedRecipe.itemStackFromJson(GsonHelper.getAsJsonObject(json, "result"));
			int time = GsonHelper.getAsInt(json, "time", 100);

			return new DigesterRecipe(recipeId, resultStack, time, ingredient);
		}

		//client side
		@Nullable
		@Override
		public DigesterRecipe fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer) {
			ItemStack resultStack = buffer.readItem();
			int time = buffer.readVarInt();
			Ingredient ingredient = Ingredient.fromNetwork(buffer);

			return new DigesterRecipe(recipeId, resultStack, time, ingredient);
		}

		//server side
		@Override
		public void toNetwork(FriendlyByteBuf buffer, DigesterRecipe recipe) {
			buffer.writeItem(recipe.recipeResult);
			buffer.writeVarInt(recipe.getCraftingTime());
			recipe.recipeIngredient.toNetwork(buffer);
		}
	}
}
