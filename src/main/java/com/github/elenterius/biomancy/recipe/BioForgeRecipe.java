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
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.ForgeRegistryEntry;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class BioForgeRecipe extends AbstractProductionRecipe {

	public static final int MAX_INGREDIENTS = 5;
	public static final int MAX_REACTANT = 1;

	private final BioForgeCategory category;
	private final List<IngredientQuantity> ingredients;
	private final Ingredient reactant;
	private final ItemStack result;

	private final NonNullList<Ingredient> vanillaIngredients;
	private final boolean isSimple;

	public BioForgeRecipe(ResourceLocation id, BioForgeCategory category, ItemStack result, int craftingTime, List<IngredientQuantity> ingredients, Ingredient reactant) {
		super(id, craftingTime);
		this.category = category;
		this.result = result;
		this.ingredients = ingredients;
		this.reactant = reactant;

		isSimple = ingredients.stream().allMatch(ingredientQuantity -> ingredientQuantity.ingredient().isSimple());

		//TODO: remove this shite
		List<Ingredient> unrolledIngredients = new ArrayList<>();
		for (IngredientQuantity ingredientQuantity : ingredients) {
			Ingredient ingredient = ingredientQuantity.ingredient();
			for (int i = 0; i < ingredientQuantity.count(); i++) {
				unrolledIngredients.add(ingredient);
			}
		}

		vanillaIngredients = NonNullList.createWithCapacity(unrolledIngredients.size());
		vanillaIngredients.addAll(unrolledIngredients);
	}

	@Override
	public boolean matches(Container inv, Level level) {
		int lastIndex = inv.getContainerSize() - 1;
		if (!reactant.test(inv.getItem(lastIndex))) return false;

		int[] countedIngredients = new int[ingredients.size()];
		for (int idx = 0; idx < lastIndex; idx++) {
			ItemStack stack = inv.getItem(idx);
			if (!stack.isEmpty()) {
				for (int i = 0; i < ingredients.size(); i++) {
					if (ingredients.get(i).testItem(stack)) {
						countedIngredients[i] += stack.getCount();
						break;
					}
				}
			}
		}

		for (int i = 0; i < ingredients.size(); i++) {
			if (countedIngredients[i] < ingredients.get(i).count()) return false;
		}

		return true;
	}

	@Override
	public ItemStack assemble(Container container) {
		return result.copy();
	}

	@Override
	public boolean canCraftInDimensions(int width, int height) {
		return false;
	}

	@Override
	public ItemStack getResultItem() {
		return result;
	}

	@Override
	public NonNullList<Ingredient> getIngredients() {
		return vanillaIngredients;
	}

	public List<IngredientQuantity> getIngredientQuantities() {
		return ingredients;
	}

	public Ingredient getReactant() {
		return reactant;
	}

	public BioForgeCategory getCategory() {
		return category;
	}

	@Override
	public RecipeSerializer<?> getSerializer() {
		return ModRecipes.BIO_FORGING_SERIALIZER.get();
	}

	@Override
	public RecipeType<?> getType() {
		return ModRecipes.BIO_FORGING_RECIPE_TYPE;
	}

	public static class Serializer extends ForgeRegistryEntry<RecipeSerializer<?>> implements RecipeSerializer<BioForgeRecipe> {

		@Override
		public BioForgeRecipe fromJson(ResourceLocation recipeId, JsonObject json) {
			List<IngredientQuantity> ingredients = RecipeUtil.readQuantitativeIngredients(GsonHelper.getAsJsonArray(json, "ingredient_quantities"));

			if (ingredients.isEmpty()) {
				throw new JsonParseException("No ingredients for recipe");
			}

			if (ingredients.size() > MAX_INGREDIENTS) {
				throw new JsonParseException("Too many ingredients for recipe. The maximum is " + MAX_INGREDIENTS);
			}

			Ingredient reactant = json.has("reactant") ? RecipeUtil.readIngredient(json, "reactant") : Ingredient.EMPTY;
			ItemStack resultStack = ShapedRecipe.itemStackFromJson(GsonHelper.getAsJsonObject(json, "result"));
			int time = GsonHelper.getAsInt(json, "time", 100);

			BioForgeCategory category = BioForgeCategory.fromJson(json);

			return new BioForgeRecipe(recipeId, category, resultStack, time, ingredients, reactant);
		}

		@Nullable
		@Override
		public BioForgeRecipe fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer) {
			ItemStack resultStack = buffer.readItem();
			Ingredient reactant = Ingredient.fromNetwork(buffer);
			int time = buffer.readVarInt();

			int ingredientCount = buffer.readVarInt();
			List<IngredientQuantity> ingredients = new ArrayList<>();
			for (int j = 0; j < ingredientCount; ++j) {
				ingredients.add(IngredientQuantity.fromNetwork(buffer));
			}

			BioForgeCategory category = BioForgeCategory.fromNetwork(buffer);

			return new BioForgeRecipe(recipeId, category, resultStack, time, ingredients, reactant);
		}

		@Override
		public void toNetwork(FriendlyByteBuf buffer, BioForgeRecipe recipe) {
			buffer.writeItem(recipe.result);
			recipe.reactant.toNetwork(buffer);
			buffer.writeVarInt(recipe.getCraftingTime());

			buffer.writeVarInt(recipe.ingredients.size());
			for (IngredientQuantity input : recipe.ingredients) {
				input.toNetwork(buffer);
			}

			recipe.category.toNetwork(buffer);
		}

	}

}
