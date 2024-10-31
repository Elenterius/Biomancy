package com.github.elenterius.biomancy.crafting.recipe;

import com.github.elenterius.biomancy.crafting.IngredientStack;
import com.github.elenterius.biomancy.crafting.VariableOutput;
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
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.List;

public class DecomposingRecipe extends StaticProcessingRecipe {
	public static final short DEFAULT_CRAFTING_COST_NUTRIENTS = 1;
	public static final int MAX_INGREDIENTS = 1;
	public static final int MAX_OUTPUTS = 6;

	private final IngredientStack ingredientStack;
	private final NonNullList<Ingredient> vanillaIngredients;
	private final List<VariableOutput> outputs;

	public DecomposingRecipe(ResourceLocation id, List<VariableOutput> outputs, IngredientStack ingredientStack, int craftingTimeTicks, int craftingCostNutrients) {
		super(id, craftingTimeTicks, craftingCostNutrients);
		this.ingredientStack = ingredientStack;
		this.outputs = outputs;

		List<Ingredient> flatIngredients = RecipeUtil.flattenIngredientStacks(List.of(ingredientStack));
		vanillaIngredients = NonNullList.createWithCapacity(flatIngredients.size());
		vanillaIngredients.addAll(flatIngredients);
	}

	@Override
	public boolean matches(Container inputInventory, Level level) {
		ItemStack stack = inputInventory.getItem(0);
		return ingredientStack.ingredient().test(stack) && stack.getCount() >= ingredientStack.count();
	}

	@Override
	public boolean isSpecial() {
		return true;
	}

	@Override
	public ItemStack getResultItem(RegistryAccess registryAccess) {
		return outputs.get(0).getItemStack();
	}

	@Override
	public ItemStack assemble(Container inputInventory, RegistryAccess registryAccess) {
		return outputs.get(0).getItemStack().copy();
	}

	@Override
	public boolean canCraftInDimensions(int width, int height) {
		return width * height == 1;
	}

	@Override
	public NonNullList<Ingredient> getIngredients() {
		return vanillaIngredients;
	}

	public IngredientStack getIngredientQuantity() {
		return ingredientStack;
	}

	public List<VariableOutput> getOutputs() {
		return outputs;
	}

	@Override
	public RecipeSerializer<?> getSerializer() {
		return ModRecipes.DECOMPOSING_SERIALIZER.get();
	}

	@Override
	public RecipeType<?> getType() {
		return ModRecipes.DECOMPOSING_RECIPE_TYPE.get();
	}

	@Override
	public ItemStack getToastSymbol() {
		return new ItemStack(ModItems.DECOMPOSER.get());
	}

	public static class Serializer implements RecipeSerializer<DecomposingRecipe> {

		@Override
		public DecomposingRecipe fromJson(ResourceLocation recipeId, JsonObject json) {
			JsonObject input = GsonHelper.getAsJsonObject(json, "ingredient");
			IngredientStack ingredientStack = IngredientStack.fromJson(input);

			List<VariableOutput> results = RecipeUtil.readVariableProductionOutputs(GsonHelper.getAsJsonArray(json, "results"));
			if (results.size() > MAX_OUTPUTS) {
				throw new JsonParseException(String.format("Too many outputs for %s recipe. Max amount is %d", ForgeRegistries.RECIPE_SERIALIZERS.getKey(this), MAX_OUTPUTS));
			}

			int time = GsonHelper.getAsInt(json, "processingTime", 100);
			int cost = GsonHelper.getAsInt(json, "nutrientsCost", DEFAULT_CRAFTING_COST_NUTRIENTS);

			return new DecomposingRecipe(recipeId, results, ingredientStack, time, cost);
		}

		@Override
		public DecomposingRecipe fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer) {
			IngredientStack ingredientStack = IngredientStack.fromNetwork(buffer);

			int craftingTime = buffer.readVarInt();
			int craftingCost = buffer.readVarInt();

			int outputCount = buffer.readVarInt();
			List<VariableOutput> outputs = new ArrayList<>();
			for (int j = 0; j < outputCount; ++j) {
				outputs.add(VariableOutput.fromNetwork(buffer));
			}

			return new DecomposingRecipe(recipeId, outputs, ingredientStack, craftingTime, craftingCost);
		}

		@Override
		public void toNetwork(FriendlyByteBuf buffer, DecomposingRecipe recipe) {
			recipe.ingredientStack.toNetwork(buffer);

			buffer.writeVarInt(recipe.craftingTimeTicks);
			buffer.writeVarInt(recipe.craftingCostNutrients);

			buffer.writeVarInt(recipe.outputs.size());
			for (VariableOutput output : recipe.outputs) {
				output.toNetwork(buffer);
			}
		}
	}

}
