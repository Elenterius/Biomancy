package com.github.elenterius.biomancy.recipe;

import com.google.gson.JsonObject;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;

public record IngredientStack(Ingredient ingredient, int count) {

	public static final String INGREDIENT_KEY = "ingredient";
	public static final String COUNT_KEY = "count";

	public static IngredientStack fromJson(JsonObject json) {
		Ingredient ingredient = readIngredient(json);
		int count = GsonHelper.getAsInt(json, COUNT_KEY, 1);
		return new IngredientStack(ingredient, count);
	}

	private static Ingredient readIngredient(JsonObject json) {
		if (GsonHelper.isArrayNode(json, INGREDIENT_KEY)) return Ingredient.fromJson(GsonHelper.getAsJsonArray(json, INGREDIENT_KEY));
		else return Ingredient.fromJson(GsonHelper.getAsJsonObject(json, INGREDIENT_KEY));
	}

	public static IngredientStack fromNetwork(FriendlyByteBuf buffer) {
		Ingredient ingredient = Ingredient.fromNetwork(buffer);
		int count = buffer.readVarInt();
		return new IngredientStack(ingredient, count);
	}

	public void toNetwork(FriendlyByteBuf buffer) {
		ingredient.toNetwork(buffer);
		buffer.writeVarInt(count);
	}

	public boolean testItem(@Nullable ItemStack stack) {
		return ingredient.test(stack);
	}

	public List<ItemStack> getItemsWithCount() {
		if (count == 1) return List.of(ingredient.getItems());
		return Arrays.stream(ingredient.getItems()).map(this::copyItemStackWithCount).toList();
	}

	private ItemStack copyItemStackWithCount(ItemStack stack) {
		if (count == 0) return ItemStack.EMPTY;
		ItemStack copy = stack.copy();
		copy.setCount(count);
		return copy;
	}

	public JsonObject toJson() {
		JsonObject json = new JsonObject();
		json.add(INGREDIENT_KEY, ingredient.toJson());
		if (count > 0) json.addProperty(COUNT_KEY, count);
		return json;
	}

}
