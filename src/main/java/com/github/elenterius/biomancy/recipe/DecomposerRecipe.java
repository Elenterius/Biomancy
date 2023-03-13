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
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.List;

public class DecomposerRecipe extends AbstractProductionRecipe {

	public static final int MAX_INGREDIENTS = 1;
	public static final int MAX_OUTPUTS = 6;

	private final IngredientStack ingredientStack;
	private final NonNullList<Ingredient> vanillaIngredients;
	private final List<VariableProductionOutput> outputs;

	public DecomposerRecipe(ResourceLocation id, List<VariableProductionOutput> outputs, IngredientStack ingredientStack, int craftingTime) {
		super(id, craftingTime);
		this.ingredientStack = ingredientStack;
		this.outputs = outputs;

		List<Ingredient> flatIngredients = RecipeUtil.flattenIngredientStacks(List.of(ingredientStack));
		vanillaIngredients = NonNullList.createWithCapacity(flatIngredients.size());
		vanillaIngredients.addAll(flatIngredients);
	}

	@Override
	public boolean matches(Container inv, Level level) {
		ItemStack stack = inv.getItem(0);
		return ingredientStack.ingredient().test(stack) && stack.getCount() >= ingredientStack.count();
	}

	@Override
	public boolean isSpecial() {
		return true;
	}

	@Override
	public ItemStack getResultItem() {
		return outputs.get(0).getItemStack();
	}

	@Override
	public ItemStack assemble(Container inv) {
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

	public List<VariableProductionOutput> getOutputs() {
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

	public static class Serializer implements RecipeSerializer<DecomposerRecipe> {

		@Override
		public DecomposerRecipe fromJson(ResourceLocation recipeId, JsonObject json) {
			JsonObject input = GsonHelper.getAsJsonObject(json, "input");
			IngredientStack ingredientStack = IngredientStack.fromJson(input);

			List<VariableProductionOutput> outputs = RecipeUtil.readVariableProductionOutputs(GsonHelper.getAsJsonArray(json, "outputs"));
			if (outputs.size() > MAX_OUTPUTS) {
				throw new JsonParseException(String.format("Too many outputs for %s recipe. Max amount is %d", ForgeRegistries.RECIPE_SERIALIZERS.getKey(this), MAX_OUTPUTS));
			}

			int time = GsonHelper.getAsInt(json, "time", 100);

			return new DecomposerRecipe(recipeId, outputs, ingredientStack, time);
		}

		@Override
		public DecomposerRecipe fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer) {
			IngredientStack ingredientStack = IngredientStack.fromNetwork(buffer);

			int time = buffer.readVarInt();

			int outputCount = buffer.readVarInt();
			List<VariableProductionOutput> outputs = new ArrayList<>();
			for (int j = 0; j < outputCount; ++j) {
				outputs.add(VariableProductionOutput.fromNetwork(buffer));
			}

			return new DecomposerRecipe(recipeId, outputs, ingredientStack, time);
		}

		@Override
		public void toNetwork(FriendlyByteBuf buffer, DecomposerRecipe recipe) {
			recipe.ingredientStack.toNetwork(buffer);

			buffer.writeVarInt(recipe.getCraftingTime());

			buffer.writeVarInt(recipe.outputs.size());
			for (VariableProductionOutput output : recipe.outputs) {
				output.toNetwork(buffer);
			}
		}
	}

}
