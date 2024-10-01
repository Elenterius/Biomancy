package com.github.elenterius.biomancy.crafting.recipe;

import com.github.elenterius.biomancy.util.ItemStackCounter;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.player.StackedContents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;

public record IngredientStack(Ingredient ingredient, int count) {

	public static final String ALT_INGREDIENT_KEY = "alt"; //legacy support, unused by Biomancy
	public static final String COUNT_KEY = "count";

	public ItemStack[] getItems() {
		return ingredient.getItems();
	}

	public boolean testItem(@Nullable ItemStack stack) {
		return ingredient.test(stack);
	}

	public boolean hasSufficientCount(StackedContents itemCounter) {
		IntList stackingIds = ingredient.getStackingIds();

		int n = 0;
		for (int i = 0; i < stackingIds.size(); i++) {
			n += itemCounter.contents.get(stackingIds.getInt(i));
			if (n >= count) return true;
		}

		return false;
	}

	public boolean hasSufficientCount(ItemStackCounter itemCounter) {
		List<ItemStackCounter.CountedItem> itemCounts = itemCounter.getItemCounts();

		int n = 0;
		for (ItemStackCounter.CountedItem countedItem : itemCounts) {
			if (ingredient.test(countedItem.stack())) n += countedItem.amount();
			if (n >= count) return true;
		}

		return false;
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
		JsonElement ingredientJson = ingredient.toJson();

		if (ingredientJson.isJsonArray()) {
			JsonObject json = new JsonObject();
			json.add(ALT_INGREDIENT_KEY, ingredientJson);
			if (count > 1) json.addProperty(COUNT_KEY, count);
			return json;
		}

		JsonObject json = (JsonObject) ingredientJson;
		if (count > 1) json.addProperty(COUNT_KEY, count);
		return json;
	}

	public static IngredientStack fromJson(JsonObject json) {
		Ingredient ingredient = readIngredient(json);
		int count = GsonHelper.getAsInt(json, COUNT_KEY, 1);
		return new IngredientStack(ingredient, count);
	}

	private static Ingredient readIngredient(JsonObject json) {
		if (GsonHelper.isArrayNode(json, ALT_INGREDIENT_KEY)) {
			return Ingredient.fromJson(GsonHelper.getAsJsonArray(json, ALT_INGREDIENT_KEY));
		}
		return Ingredient.fromJson(json);
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

}
