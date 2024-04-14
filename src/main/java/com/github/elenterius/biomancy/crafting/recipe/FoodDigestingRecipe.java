package com.github.elenterius.biomancy.crafting.recipe;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.init.ModItems;
import com.github.elenterius.biomancy.init.ModRecipes;
import com.google.gson.JsonObject;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.Mth;
import net.minecraft.world.Container;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public class FoodDigestingRecipe extends DynamicProcessingRecipe implements DigestingRecipe {

	private final int multiplier;
	private final ItemStack resultBaseItem;
	private final AnyFoodIngredient ingredient;
	private final NonNullList<Ingredient> ingredients;

	protected FoodDigestingRecipe(ResourceLocation key, int multiplier, ItemStack resultBaseItem) {
		super(key, ModRecipes.DIGESTING_RECIPE_TYPE.get());
		this.multiplier = multiplier;
		this.resultBaseItem = resultBaseItem;

		ingredient = new AnyFoodIngredient();
		ingredients = NonNullList.of(Ingredient.EMPTY, ingredient);
	}

	@Override
	public Ingredient getIngredient() {
		return ingredient;
	}

	@Override
	public NonNullList<Ingredient> getIngredients() {
		return ingredients;
	}

	public static int getFoodNutrition(ItemStack stack) {
		if (stack.isEmpty()) return 0;
		if (!stack.isEdible()) return 0;

		FoodProperties foodProperties = stack.getFoodProperties(null);
		return foodProperties != null ? foodProperties.getNutrition() : 0;
	}

	@Override
	public int getCraftingTimeTicks(Container inputInventory) {
		int nutrition = getFoodNutrition(inputInventory.getItem(0));
		return nutrition > 0 ? Mth.ceil(200 + 190 * Math.log(nutrition)) : 0;
	}

	@Override
	public int getCraftingCostNutrients(Container inputInventory) {
		float sixtySecondsInTicks = 1200;
		return 1 + Mth.floor(getCraftingTimeTicks(inputInventory) / sixtySecondsInTicks);
	}

	@Override
	public boolean matches(Container inputInventory, Level pLevel) {
		int nutrition = getFoodNutrition(inputInventory.getItem(0));
		return nutrition > 0;
	}

	@Override
	public ItemStack assemble(Container inputInventory, RegistryAccess registryAccess) {
		int nutrition = getFoodNutrition(inputInventory.getItem(0));
		if (nutrition <= 0) return ItemStack.EMPTY;

		int count = Mth.clamp(nutrition * multiplier, 1, 64 * 2);
		return resultBaseItem.copyWithCount(count);
	}

	@Override
	public boolean canCraftInDimensions(int width, int height) {
		return width * height == 1;
	}

	@Override
	public RecipeSerializer<?> getSerializer() {
		return ModRecipes.FOOD_DIGESTING_SERIALIZER.get();
	}

	@Override
	public ItemStack getToastSymbol() {
		return new ItemStack(ModItems.DIGESTER.get());
	}

	public static class Serializer implements RecipeSerializer<FoodDigestingRecipe> {

		@Override
		public FoodDigestingRecipe fromJson(ResourceLocation recipeId, JsonObject json) {
			int multiplier = GsonHelper.getAsInt(json, "multiplier", 1);
			ItemStack resultBaseItem = ShapedRecipe.itemStackFromJson(GsonHelper.getAsJsonObject(json, "result_base"));
			return new FoodDigestingRecipe(recipeId, multiplier, resultBaseItem);
		}

		@Override
		public @Nullable FoodDigestingRecipe fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer) {
			int multiplier = buffer.readVarInt();
			ItemStack resultBaseItem = buffer.readItem();
			return new FoodDigestingRecipe(recipeId, multiplier, resultBaseItem);
		}

		@Override
		public void toNetwork(FriendlyByteBuf buffer, FoodDigestingRecipe recipe) {
			buffer.writeVarInt(recipe.multiplier);
			buffer.writeItem(recipe.resultBaseItem);
		}
	}

	public static class RecipeBuilder {

		public static void save(Consumer<FinishedRecipe> consumer, int multiplier, Item item) {
			save(consumer, multiplier, new ItemStack(item));
		}

		public static void save(Consumer<FinishedRecipe> consumer, int multiplier, ItemStack stack) {
			ResourceLocation key = BuiltInRegistries.ITEM.getKey(stack.getItem());
			ResourceLocation id = BiomancyMod.createRL(key.getPath() + "_from_digesting_dynamic_food");
			String subFolder = ModRecipes.DIGESTING_RECIPE_TYPE.getId().getPath();
			save(consumer, multiplier, stack, new ResourceLocation(id.getNamespace(), subFolder + "/" + id.getPath()));
		}

		public static void save(Consumer<FinishedRecipe> consumer, int multiplier, ItemStack stack, ResourceLocation id) {
			consumer.accept(new FinishedRecipe() {
				public RecipeSerializer<?> getType() {
					return ModRecipes.FOOD_DIGESTING_SERIALIZER.get();
				}

				@Override
				public void serializeRecipeData(JsonObject json) {
					json.addProperty("multiplier", multiplier);
					json.add("result_base", serialize(stack));
				}

				private JsonObject serialize(ItemStack stack) {
					JsonObject json = new JsonObject();
					json.addProperty("item", BuiltInRegistries.ITEM.getKey(stack.getItem()).toString());
					if (stack.hasTag()) json.addProperty("nbt", stack.getTag().getAsString());
					return json;
				}

				@Override
				public ResourceLocation getId() {
					return id;
				}

				@Override
				public @Nullable JsonObject serializeAdvancement() {
					return null;
				}

				@Override
				public ResourceLocation getAdvancementId() {
					return new ResourceLocation("");
				}

			});
		}
	}
}
