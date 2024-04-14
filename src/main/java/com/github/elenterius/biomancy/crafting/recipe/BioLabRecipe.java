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
import java.util.ArrayList;
import java.util.List;

public class BioLabRecipe extends StaticProcessingRecipe {

	public static final short DEFAULT_CRAFTING_COST_NUTRIENTS = 2;
	public static final int MAX_INGREDIENTS = 4;
	public static final int MAX_REACTANT = 1;
	private final List<IngredientStack> ingredients;
	private final Ingredient recipeReactant;
	private final ItemStack result;

	private final NonNullList<Ingredient> vanillaIngredients;

	public BioLabRecipe(ResourceLocation id, ItemStack result, int craftingTimeTicks, int craftingCostNutrients, List<IngredientStack> ingredients, Ingredient reactant) {
		super(id, craftingTimeTicks, craftingCostNutrients);
		this.ingredients = ingredients;
		recipeReactant = reactant;
		this.result = result;

		List<Ingredient> flatIngredients = RecipeUtil.flattenIngredientStacks(ingredients);
		vanillaIngredients = NonNullList.createWithCapacity(flatIngredients.size());
		vanillaIngredients.addAll(flatIngredients);
	}

	@Override
	public boolean matches(Container inv, Level worldIn) {
		int lastIndex = inv.getContainerSize() - 1;
		if (!recipeReactant.test(inv.getItem(lastIndex))) return false;

		int[] countedIngredients = new int[ingredients.size()];
		for (int idx = 0; idx < lastIndex; idx++) {
			ItemStack stack = inv.getItem(idx);
			if (stack.isEmpty()) continue;

			for (int i = 0; i < ingredients.size(); i++) {
				IngredientStack requiredIngredient = ingredients.get(i);
				if (requiredIngredient.testItem(stack) && countedIngredients[i] < requiredIngredient.count()) {
					countedIngredients[i] += stack.getCount();
					break;
				}
			}
		}

		for (int i = 0; i < ingredients.size(); i++) {
			if (countedIngredients[i] < ingredients.get(i).count()) return false;
		}

		return true;
	}

	@Override
	public ItemStack assemble(Container inv, RegistryAccess registryAccess) {
		return result.copy();
	}

	@Override
	public boolean canCraftInDimensions(int width, int height) {
		return width * height >= ingredients.size();
	}

	@Override
	public ItemStack getResultItem(RegistryAccess registryAccess) {
		return result;
	}

	@Override
	public NonNullList<Ingredient> getIngredients() {
		return vanillaIngredients;
	}

	public List<IngredientStack> getIngredientQuantities() {
		return ingredients;
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
		return ModRecipes.BIO_BREWING_RECIPE_TYPE.get();
	}

	@Override
	public ItemStack getToastSymbol() {
		return new ItemStack(ModItems.BIO_LAB.get());
	}

	public static class Serializer implements RecipeSerializer<BioLabRecipe> {

		@Override
		public BioLabRecipe fromJson(ResourceLocation recipeId, JsonObject json) {
			List<IngredientStack> ingredients = RecipeUtil.readIngredientStacks(GsonHelper.getAsJsonArray(json, "ingredients"));

			if (ingredients.isEmpty()) {
				throw new JsonParseException("No ingredients found for %s recipe".formatted(ForgeRegistries.RECIPE_SERIALIZERS.getKey(this)));
			}

			if (ingredients.size() > MAX_INGREDIENTS) {
				throw new JsonParseException("Too many ingredients for %s recipe. Max amount is %d".formatted(ForgeRegistries.RECIPE_SERIALIZERS.getKey(this), MAX_INGREDIENTS));
			}

			for (IngredientStack ingredientStack : ingredients) {
				int count = ingredientStack.count();
				if (count > 64) throw new IllegalArgumentException("Ingredient quantity of %d is larger than 64".formatted(count));
			}

			Ingredient reactant = json.has("reactant") ? RecipeUtil.readIngredient(json, "reactant") : Ingredient.EMPTY;

			ItemStack resultStack = ShapedRecipe.itemStackFromJson(GsonHelper.getAsJsonObject(json, "result"));

			int time = GsonHelper.getAsInt(json, "processingTime", 100);
			int cost = GsonHelper.getAsInt(json, "nutrientsCost", DEFAULT_CRAFTING_COST_NUTRIENTS);

			return new BioLabRecipe(recipeId, resultStack, time, cost, ingredients, reactant);
		}

		@Nullable
		@Override
		public BioLabRecipe fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer) {
			ItemStack resultStack = buffer.readItem();

			Ingredient reactant = Ingredient.fromNetwork(buffer);
			int craftingTime = buffer.readVarInt();
			int craftingCost = buffer.readVarInt();

			int ingredientCount = buffer.readVarInt();
			List<IngredientStack> ingredients = new ArrayList<>();
			for (int i = 0; i < ingredientCount; i++) {
				ingredients.add(IngredientStack.fromNetwork(buffer));
			}

			return new BioLabRecipe(recipeId, resultStack, craftingTime, craftingCost, ingredients, reactant);
		}

		@Override
		public void toNetwork(FriendlyByteBuf buffer, BioLabRecipe recipe) {
			buffer.writeItem(recipe.result);

			recipe.recipeReactant.toNetwork(buffer);
			buffer.writeVarInt(recipe.craftingTimeTicks);
			buffer.writeVarInt(recipe.craftingCostNutrients);

			buffer.writeVarInt(recipe.ingredients.size());
			for (IngredientStack ingredientStack : recipe.ingredients) {
				ingredientStack.toNetwork(buffer);
			}
		}
	}
}
