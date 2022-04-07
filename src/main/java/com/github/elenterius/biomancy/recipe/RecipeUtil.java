package com.github.elenterius.biomancy.recipe;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.core.NonNullList;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.ArrayList;
import java.util.List;

public final class RecipeUtil {

	private RecipeUtil() {}

	public static Ingredient readIngredient(JsonObject json, String memberName) {
		return Ingredient.fromJson((GsonHelper.isArrayNode(json, memberName) ? GsonHelper.getAsJsonArray(json, memberName) : GsonHelper.getAsJsonObject(json, memberName)));
	}

	public static NonNullList<Ingredient> readIngredients(JsonArray jsonArray) {
		NonNullList<Ingredient> list = NonNullList.create();
		for (int i = 0; i < jsonArray.size(); i++) {
			Ingredient ingredient = Ingredient.fromJson(jsonArray.get(i));
			if (!ingredient.isEmpty()) {
				list.add(ingredient);
			}
		}
		return list;
	}

	public static List<VariableProductionOutput> readVariableProductionOutputs(JsonArray jsonArray) {
		List<VariableProductionOutput> list = new ArrayList<>();
		for (int i = 0; i < jsonArray.size(); i++) {
			list.add(VariableProductionOutput.deserialize(jsonArray.get(i).getAsJsonObject()));
		}
		return list;
	}

	public static List<IngredientQuantity> readQuantitativeIngredients(JsonArray jsonArray) {
		List<IngredientQuantity> list = new ArrayList<>();
		for (int i = 0; i < jsonArray.size(); ++i) {
			IngredientQuantity ingredientQuantity = IngredientQuantity.fromJson(jsonArray.get(i).getAsJsonObject());
			if (!ingredientQuantity.ingredient().isEmpty()) {
				list.add(ingredientQuantity);
			}
		}
		return list;
	}

}
