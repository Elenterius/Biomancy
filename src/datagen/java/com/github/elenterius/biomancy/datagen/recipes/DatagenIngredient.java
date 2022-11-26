package com.github.elenterius.biomancy.datagen.recipes;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.Objects;
import java.util.stream.Stream;

public class DatagenIngredient extends Ingredient {

	final ResourceLocation resourceLocation;

	public DatagenIngredient(String namespacedItemId) {
		super(Stream.empty());
		resourceLocation = Objects.requireNonNull(ResourceLocation.tryParse(namespacedItemId));
	}

	public DatagenIngredient(ResourceLocation resourceLocation) {
		super(Stream.empty());
		this.resourceLocation = resourceLocation;
	}

	@Override
	public JsonElement toJson() {
		JsonObject jsonObj = new JsonObject();
		jsonObj.addProperty("item", resourceLocation.toString());
		return jsonObj;
	}

}
