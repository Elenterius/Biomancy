package com.github.elenterius.biomancy.advancements.predicate;

import com.github.elenterius.biomancy.BiomancyMod;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public class FoodItemPredicate extends ItemPredicate {

	public static final ResourceLocation ID = BiomancyMod.createRL("is_food_item");

	public FoodItemPredicate() {}

	@Override
	public boolean matches(ItemStack stack) {
		if (!stack.isEdible()) return false;
		return stack.getFoodProperties(null) != null;
	}

	@Override
	public JsonElement serializeToJson() {
		JsonObject jsonObject = new JsonObject();
		jsonObject.addProperty("type", ID.toString());
		return jsonObject;
	}

	public static FoodItemPredicate deserializeFromJson(JsonObject jsonObject) {
		return new FoodItemPredicate();
	}

}
