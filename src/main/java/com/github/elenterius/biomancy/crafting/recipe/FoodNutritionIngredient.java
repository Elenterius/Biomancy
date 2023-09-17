package com.github.elenterius.biomancy.crafting.recipe;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.common.crafting.AbstractIngredient;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.crafting.IIngredientSerializer;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.function.Predicate;

public class FoodNutritionIngredient extends AbstractIngredient {

	private final int minNutrition;
	private final int maxNutrition;

	private final Predicate<FoodProperties> nutritionPredicate;

	@Nullable
	private ItemStack[] stacks = null;

	public FoodNutritionIngredient(int minNutritionInclusive, int maxNutritionInclusive) {
		minNutrition = minNutritionInclusive;
		maxNutrition = maxNutritionInclusive;
		nutritionPredicate = foodProperties -> foodProperties != null && foodProperties.getNutrition() >= minNutrition && foodProperties.getNutrition() <= maxNutrition;
	}

	@Override
	public boolean isEmpty() {
		return false;
	}

	@Override
	public ItemStack[] getItems() {
		resolve();
		return stacks;
	}

	private void resolve() {
		if (stacks == null) {
			stacks = ForgeRegistries.ITEMS.getValues().stream()
					.filter(Item::isEdible)
					.map(ItemStack::new)
					.filter(item -> nutritionPredicate.test(item.getFoodProperties(null)))
					.toArray(ItemStack[]::new);
		}
	}

	@Override
	protected void invalidate() {
		stacks = null;
	}

	@Override
	public boolean test(@Nullable ItemStack stack) {
		if (stack == null) return false;
		if (stack.isEmpty()) return false;
		if (!stack.isEdible()) return false;

		return nutritionPredicate.test(stack.getFoodProperties(null));
	}

	@Override
	public boolean isSimple() {
		return false;
	}

	@Override
	public IIngredientSerializer<? extends Ingredient> getSerializer() {
		return Serializer.INSTANCE;
	}

	@Override
	public JsonElement toJson() {
		JsonObject json = new JsonObject();
		json.addProperty("type", Objects.requireNonNull(CraftingHelper.getID(Serializer.INSTANCE)).toString());
		json.addProperty("minNutrition", minNutrition);
		json.addProperty("maxNutrition", maxNutrition);
		return json;
	}

	public static class Serializer implements IIngredientSerializer<FoodNutritionIngredient> {

		public static final Serializer INSTANCE = new Serializer();

		@Override
		public FoodNutritionIngredient parse(FriendlyByteBuf buffer) {
			return new FoodNutritionIngredient(buffer.readVarInt(), buffer.readVarInt());
		}

		@Override
		public FoodNutritionIngredient parse(JsonObject json) {
			int minNutrition = GsonHelper.getAsInt(json, "minNutrition");
			int maxNutrition = GsonHelper.getAsInt(json, "maxNutrition");
			return new FoodNutritionIngredient(minNutrition, maxNutrition);
		}

		@Override
		public void write(FriendlyByteBuf buffer, FoodNutritionIngredient ingredient) {
			buffer.writeVarInt(ingredient.minNutrition);
			buffer.writeVarInt(ingredient.maxNutrition);
		}
	}

}
