package com.github.elenterius.biomancy.recipe;

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
import net.minecraftforge.registries.ForgeRegistryEntry;

import java.util.ArrayList;
import java.util.List;

public class DecomposerRecipe extends AbstractProductionRecipe {

	public static final int MAX_INGREDIENTS = 1;
	public static final int MAX_OUTPUTS = 4;

	private final IngredientQuantity ingredientQuantity;
	private final List<VariableProductionOutput> outputs;
	private final List<VariableProductionOutput> byproducts;

	public DecomposerRecipe(ResourceLocation id, List<VariableProductionOutput> outputs, List<VariableProductionOutput> byproducts, IngredientQuantity ingredientQuantity, int craftingTime) {
		super(id, craftingTime);
		this.ingredientQuantity = ingredientQuantity;
		this.outputs = outputs;
		this.byproducts = byproducts;
	}

	@Override
	public boolean matches(Container inv, Level level) {
		ItemStack stack = inv.getItem(0);
		return ingredientQuantity.ingredient().test(stack) && stack.getCount() >= ingredientQuantity.count();
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
		return NonNullList.of(Ingredient.EMPTY, ingredientQuantity.ingredient());
	}

	public Ingredient getIngredient() {
		return ingredientQuantity.ingredient();
	}

	public int getIngredientCount() {
		return ingredientQuantity.count();
	}

	public List<VariableProductionOutput> getOutputs() {
		return outputs;
	}

	public List<VariableProductionOutput> getByproducts() {
		return byproducts;
	}

	@Override
	public RecipeSerializer<?> getSerializer() {
		return ModRecipes.DECOMPOSING_SERIALIZER.get();
	}

	@Override
	public RecipeType<?> getType() {
		return ModRecipes.DECOMPOSING_RECIPE_TYPE;
	}

	public static class Serializer extends ForgeRegistryEntry<RecipeSerializer<?>> implements RecipeSerializer<DecomposerRecipe> {

		@Override
		public DecomposerRecipe fromJson(ResourceLocation recipeId, JsonObject json) {
			JsonObject input = GsonHelper.getAsJsonObject(json, "input");
			IngredientQuantity ingredientQuantity = IngredientQuantity.fromJson(input);

			List<VariableProductionOutput> outputs = RecipeUtil.readVariableProductionOutputs(GsonHelper.getAsJsonArray(json, "outputs"));
			if (outputs.size() > MAX_OUTPUTS) {
				throw new JsonParseException(String.format("Too many outputs for %s recipe. Max amount is %d", getRegistryName(), MAX_OUTPUTS));
			}

			List<VariableProductionOutput> byproducts = RecipeUtil.readVariableProductionOutputs(GsonHelper.getAsJsonArray(json, "byproducts"));
			if (byproducts.size() > MAX_OUTPUTS) {
				throw new JsonParseException(String.format("Too many byproducts for %s recipe. Max amount is %d", getRegistryName(), MAX_OUTPUTS));
			}

			int time = GsonHelper.getAsInt(json, "time", 100);

			return new DecomposerRecipe(recipeId, outputs, byproducts, ingredientQuantity, time);
		}

		@Override
		public DecomposerRecipe fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer) {
			IngredientQuantity ingredientQuantity = IngredientQuantity.fromNetwork(buffer);

			int time = buffer.readVarInt();

			int outputCount = buffer.readVarInt();
			List<VariableProductionOutput> outputs = new ArrayList<>();
			for (int j = 0; j < outputCount; ++j) {
				outputs.add(VariableProductionOutput.read(buffer));
			}

			int byproductCount = buffer.readVarInt();
			List<VariableProductionOutput> byproducts = new ArrayList<>();
			for (int j = 0; j < byproductCount; ++j) {
				byproducts.add(VariableProductionOutput.read(buffer));
			}

			return new DecomposerRecipe(recipeId, outputs, byproducts, ingredientQuantity, time);
		}

		@Override
		public void toNetwork(FriendlyByteBuf buffer, DecomposerRecipe recipe) {
			recipe.ingredientQuantity.toNetwork(buffer);

			buffer.writeInt(recipe.getCraftingTime());

			buffer.writeVarInt(recipe.outputs.size());
			for (VariableProductionOutput output : recipe.outputs) {
				output.write(buffer);
			}

			buffer.writeVarInt(recipe.byproducts.size());
			for (VariableProductionOutput byproduct : recipe.byproducts) {
				byproduct.write(buffer);
			}
		}
	}

}
