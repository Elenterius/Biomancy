package com.github.elenterius.biomancy.datagen.recipes.builder;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.Objects;
import java.util.stream.Stream;

public final class DatagenIngredient extends Ingredient {

	final ResourceLocation resourceLocation;

	public DatagenIngredient(String itemKey) {
		super(Stream.empty());
		resourceLocation = Objects.requireNonNull(ResourceLocation.tryParse(itemKey));
	}

	public DatagenIngredient(String namespace, String path) {
		super(Stream.empty());
		resourceLocation = Objects.requireNonNull(ResourceLocation.tryBuild(namespace, path));
	}

	public DatagenIngredient(ResourceLocation itemKey) {
		super(Stream.empty());
		this.resourceLocation = itemKey;
	}

	@Override
	public JsonElement toJson() {
		JsonObject jsonObj = new JsonObject();
		jsonObj.addProperty("item", resourceLocation.toString());
		return jsonObj;
	}

}
