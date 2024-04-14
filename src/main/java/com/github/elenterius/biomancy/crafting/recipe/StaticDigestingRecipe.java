package com.github.elenterius.biomancy.crafting.recipe;

import com.github.elenterius.biomancy.init.ModItems;
import com.github.elenterius.biomancy.init.ModRecipes;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
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

public class StaticDigestingRecipe extends StaticProcessingRecipe implements DigestingRecipe {

	private final Ingredient recipeIngredient;
	private final ItemStack recipeResult;

	public StaticDigestingRecipe(ResourceLocation id, ItemStack result, int craftingTimeTicks, int craftingCostNutrients, Ingredient ingredient) {
		super(id, craftingTimeTicks, craftingCostNutrients);
		recipeIngredient = ingredient;
		recipeResult = result;
	}

	@Override
	public boolean matches(Container inputInventory, Level worldIn) {
		return recipeIngredient.test(inputInventory.getItem(0));
	}

	@Override
	public ItemStack assemble(Container inputInventory, RegistryAccess registryAccess) {
		return recipeResult.copy();
	}

	@Override
	public boolean canCraftInDimensions(int width, int height) {
		return width * height == 1;
	}

	@Override
	public ItemStack getResultItem(RegistryAccess registryAccess) {
		return recipeResult;
	}

	@Override
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

	public static class Serializer implements RecipeSerializer<StaticDigestingRecipe> {

		@Override
		public StaticDigestingRecipe fromJson(ResourceLocation recipeId, JsonObject json) {

			Ingredient ingredient = Ingredient.fromJson(GsonHelper.getAsJsonObject(json, "ingredient"));

			if (ingredient.isEmpty()) {
				throw new JsonParseException("No ingredient found for %s/%s recipe".formatted(ForgeRegistries.RECIPE_SERIALIZERS.getKey(this), recipeId));
			}

			ItemStack resultStack = ShapedRecipe.itemStackFromJson(GsonHelper.getAsJsonObject(json, "result"));
			int time = GsonHelper.getAsInt(json, "processingTime", 100);
			int cost = GsonHelper.getAsInt(json, "nutrientsCost", 1);

			return new StaticDigestingRecipe(recipeId, resultStack, time, cost, ingredient);
		}

		//client side
		@Nullable
		@Override
		public StaticDigestingRecipe fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer) {
			ItemStack resultStack = buffer.readItem();

			int craftingTime = buffer.readVarInt();
			int craftingCost = buffer.readVarInt();

			Ingredient ingredient = Ingredient.fromNetwork(buffer);

			return new StaticDigestingRecipe(recipeId, resultStack, craftingTime, craftingCost, ingredient);
		}

		//server side
		@Override
		public void toNetwork(FriendlyByteBuf buffer, StaticDigestingRecipe recipe) {
			buffer.writeItem(recipe.recipeResult);
			buffer.writeVarInt(recipe.craftingTimeTicks);
			buffer.writeVarInt(recipe.craftingCostNutrients);
			recipe.recipeIngredient.toNetwork(buffer);
		}
	}
}
