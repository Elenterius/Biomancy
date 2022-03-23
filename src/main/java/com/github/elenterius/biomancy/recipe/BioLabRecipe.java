package com.github.elenterius.biomancy.recipe;

import com.github.elenterius.biomancy.init.ModRecipes;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.core.NonNullList;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.StackedContents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.util.RecipeMatcher;
import net.minecraftforge.registries.ForgeRegistryEntry;

import javax.annotation.Nullable;
import java.util.ArrayList;

public class BioLabRecipe extends AbstractProductionRecipe {

	public static final int MAX_INGREDIENTS = 4;
	public static final int MAX_REACTANT = 1;

	private final NonNullList<Ingredient> recipeIngredients;
	private final Ingredient recipeReactant;
	private final ItemStack recipeResult;
	private final boolean isSimple;

	public BioLabRecipe(ResourceLocation id, ItemStack result, int craftingTime, NonNullList<Ingredient> ingredients, Ingredient reactant) {
		super(id, craftingTime);
		recipeIngredients = ingredients;
		recipeReactant = reactant;
		recipeResult = result;
		isSimple = ingredients.stream().allMatch(Ingredient::isSimple);
	}

	@Override
	public boolean matches(Container inv, Level worldIn) {
		int index = inv.getContainerSize() - 1;
		if (!recipeReactant.test(inv.getItem(index))) return false;

		StackedContents stackedContents = new StackedContents();
		ArrayList<ItemStack> inputs = new ArrayList<>();
		int ingredientCount = 0;

		for (int idx = 0; idx < index; idx++) {
			ItemStack stack = inv.getItem(idx);
			if (!stack.isEmpty()) {
				ingredientCount++;
				if (isSimple) stackedContents.accountStack(stack, 1);
				else inputs.add(stack);
			}
		}

		return ingredientCount == recipeIngredients.size() && (isSimple ? stackedContents.canCraft(this, null) : RecipeMatcher.findMatches(inputs, recipeIngredients) != null);
	}

	@Override
	public ItemStack assemble(Container inv) {
		return recipeResult.copy();
	}

	@Override
	public boolean canCraftInDimensions(int width, int height) {
		return width * height >= recipeIngredients.size();
	}

	@Override
	public ItemStack getResultItem() {
		return recipeResult;
	}

	@Override
	public NonNullList<Ingredient> getIngredients() {
		return recipeIngredients;
	}

	public Ingredient getReactant() {
		return recipeReactant;
	}

	@Override
	public RecipeSerializer<?> getSerializer() {
		return ModRecipes.BIO_BREWING_SERIALIZER.get();
	}

	@Override
	public RecipeType<?> getType() {
		return ModRecipes.BIO_BREWING_RECIPE_TYPE;
	}

	public static class Serializer extends ForgeRegistryEntry<RecipeSerializer<?>> implements RecipeSerializer<BioLabRecipe> {

		@Override
		public BioLabRecipe fromJson(ResourceLocation recipeId, JsonObject json) {
			NonNullList<Ingredient> ingredients = RecipeUtil.readIngredients(GsonHelper.getAsJsonArray(json, "ingredients"));

			if (ingredients.isEmpty()) {
				throw new JsonParseException("No ingredients found for " + getRegistryName() + " recipe");
			}
			else if (ingredients.size() > MAX_INGREDIENTS) {
				throw new JsonParseException(String.format("Too many ingredients for %s recipe. Max amount is %d", getRegistryName(), MAX_INGREDIENTS));
			}

			Ingredient reactant = Ingredient.fromJson(GsonHelper.getAsJsonObject(json, "reactant"));

			ItemStack resultStack = ShapedRecipe.itemStackFromJson(GsonHelper.getAsJsonObject(json, "result"));
			int time = GsonHelper.getAsInt(json, "time", 100);

			return new BioLabRecipe(recipeId, resultStack, time, ingredients, reactant);
		}

		@Nullable
		@Override
		public BioLabRecipe fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer) {
			//client side
			ItemStack resultStack = buffer.readItem();
			int time = buffer.readInt();

			Ingredient reactant = Ingredient.fromNetwork(buffer);

			int ingredientCount = buffer.readVarInt();
			NonNullList<Ingredient> ingredients = NonNullList.withSize(ingredientCount, Ingredient.EMPTY);
			for (int j = 0; j < ingredients.size(); ++j) {
				ingredients.set(j, Ingredient.fromNetwork(buffer));
			}

			return new BioLabRecipe(recipeId, resultStack, time, ingredients, reactant);
		}

		@Override
		public void toNetwork(FriendlyByteBuf buffer, BioLabRecipe recipe) {
			//server side
			buffer.writeItem(recipe.recipeResult);
			buffer.writeInt(recipe.getCraftingTime());

			recipe.recipeReactant.toNetwork(buffer);

			buffer.writeVarInt(recipe.recipeIngredients.size());
			for (Ingredient ingredient : recipe.recipeIngredients) {
				ingredient.toNetwork(buffer);
			}
		}
	}
}
