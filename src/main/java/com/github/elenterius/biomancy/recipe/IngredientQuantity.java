package com.github.elenterius.biomancy.recipe;

import com.google.gson.JsonObject;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;

public record IngredientQuantity(Ingredient ingredient, int count) {

	public static final String INGREDIENT_KEY = "ingredient";

	public static IngredientQuantity fromJson(JsonObject json) {
		Ingredient ingredient = readIngredient(json);
		int count = GsonHelper.getAsInt(json, "count", 1);
		return new IngredientQuantity(ingredient, count);
	}

	private static Ingredient readIngredient(JsonObject jsonObj) {
		if (GsonHelper.isArrayNode(jsonObj, INGREDIENT_KEY)) return Ingredient.fromJson(GsonHelper.getAsJsonArray(jsonObj, INGREDIENT_KEY));
		else return Ingredient.fromJson(GsonHelper.getAsJsonObject(jsonObj, INGREDIENT_KEY));
	}

	public static IngredientQuantity fromNetwork(FriendlyByteBuf buffer) {
		Ingredient ingredient = Ingredient.fromNetwork(buffer);
		int count = buffer.readVarInt();
		return new IngredientQuantity(ingredient, count);
	}

	public boolean testItem(@Nullable ItemStack stack) {
		return ingredient.test(stack);
	}

	public List<ItemStack> getItemsWithCount() {
		if (count == 1) return List.of(ingredient.getItems());
		return Arrays.stream(ingredient.getItems()).map(this::copyStackWithCount).toList();
	}

	private ItemStack copyStackWithCount(ItemStack stack) {
		if (count == 0) return ItemStack.EMPTY;
		ItemStack copy = stack.copy();
		copy.setCount(count);
		return copy;
	}

	public JsonObject toJson() {
		JsonObject json = new JsonObject();
		json.add(INGREDIENT_KEY, ingredient.toJson());
		if (count > 0) json.addProperty("count", count);
		return json;
	}

	public void toNetwork(FriendlyByteBuf buffer) {
		ingredient.toNetwork(buffer);
		buffer.writeVarInt(count);
	}

}
