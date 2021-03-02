package com.github.elenterius.biomancy.datagen;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.init.ModRecipes;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.ICriterionInstance;
import net.minecraft.advancements.IRequirementsStrategy;
import net.minecraft.advancements.criterion.RecipeUnlockedTrigger;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.item.Item;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.tags.ITag;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class DecomposingRecipeBuilder {

	private final Item result;
	private final int count;
	private final List<Ingredient> ingredients = new ArrayList<>();
	private final List<Byproduct> byproducts = new ArrayList<>();
	private final int craftingTime;
	private final Advancement.Builder advancementBuilder = Advancement.Builder.builder();
	private String group;

	private DecomposingRecipeBuilder(IItemProvider resultIn, int craftingTimeIn, int countIn) {
		assert craftingTimeIn >= 0;
		assert countIn > 0;

		result = resultIn.asItem();
		craftingTime = craftingTimeIn;
		count = countIn;
	}

	public static DecomposingRecipeBuilder createRecipe(IItemProvider resultIn, int craftingTimeIn) {
		return new DecomposingRecipeBuilder(resultIn, craftingTimeIn, 1);
	}

	public static DecomposingRecipeBuilder createRecipe(IItemProvider resultIn, int craftingTimeIn, int countIn) {
		return new DecomposingRecipeBuilder(resultIn, craftingTimeIn, countIn);
	}

	public DecomposingRecipeBuilder addIngredient(ITag<Item> tagIn) {
		return addIngredient(Ingredient.fromTag(tagIn));
	}

	public DecomposingRecipeBuilder addIngredients(ITag<Item> tagIn, int quantity) {
		return addIngredients(Ingredient.fromTag(tagIn), quantity);
	}

	public DecomposingRecipeBuilder addIngredient(IItemProvider itemIn) {
		return addIngredients(itemIn, 1);
	}

	public DecomposingRecipeBuilder addIngredient(Ingredient ingredientIn) {
		return addIngredients(ingredientIn, 1);
	}

	public DecomposingRecipeBuilder addIngredients(Ingredient ingredientIn, int quantity) {
		for (int i = 0; i < quantity; i++) ingredients.add(ingredientIn);
		return this;
	}

	public DecomposingRecipeBuilder addIngredients(IItemProvider itemIn, int quantity) {
		for (int i = 0; i < quantity; i++) addIngredient(Ingredient.fromItems(itemIn));
		return this;
	}

	public DecomposingRecipeBuilder addByproduct(IItemProvider resultIn) {
		return addByproduct(resultIn, 1, 1f);
	}

	public DecomposingRecipeBuilder addByproduct(IItemProvider resultIn, float chance) {
		return addByproduct(resultIn, 1, chance);
	}

	public DecomposingRecipeBuilder addByproduct(IItemProvider resultIn, int itemCount, float chance) {
		return addByproduct(new Byproduct(resultIn, itemCount, chance));
	}

	public DecomposingRecipeBuilder addByproduct(Byproduct byproduct) {
		byproducts.add(byproduct);
		return this;
	}

	public DecomposingRecipeBuilder addByproducts(Byproduct byproduct, int quantity) {
		for (int i = 0; i < quantity; i++) byproducts.add(byproduct);
		return this;
	}

	public DecomposingRecipeBuilder addCriterion(String name, ICriterionInstance criterionIn) {
		advancementBuilder.withCriterion(name, criterionIn);
		return this;
	}

	public DecomposingRecipeBuilder setGroup(String groupIn) {
		group = groupIn;
		return this;
	}

	public void build(Consumer<IFinishedRecipe> consumerIn) {
		ResourceLocation registryKey = Registry.ITEM.getKey(result);
		build(consumerIn, BiomancyMod.createRL(registryKey.getPath() + "_decomposing"));
	}

	public void build(Consumer<IFinishedRecipe> consumerIn, String id, boolean suffix) {
		if (suffix) {
			ResourceLocation registryKey = Registry.ITEM.getKey(result);
			if (registryKey.getPath().equals(id)) {
				throw new IllegalStateException(String.format("Recipe suffix %s should be different from the recipe path %s", id, registryKey.getPath()));
			}
			build(consumerIn, BiomancyMod.createRL(registryKey.getPath() + "_" + id + "_decomposing"));
		}
		else {
			build(consumerIn, id);
		}
	}

	public void build(Consumer<IFinishedRecipe> consumerIn, String save) {
		ResourceLocation registryKey = Registry.ITEM.getKey(result);
		ResourceLocation alternative = new ResourceLocation(BiomancyMod.MOD_ID, save + "_decomposing");
		if (alternative.equals(registryKey)) {
			throw new IllegalStateException("Recipe " + alternative + " should remove its 'save' argument");
		}
		else {
			build(consumerIn, alternative);
		}
	}

	private void build(Consumer<IFinishedRecipe> consumerIn, ResourceLocation id) {
		validate(id);
		advancementBuilder.withParentId(new ResourceLocation("recipes/root")).withCriterion("has_the_recipe", RecipeUnlockedTrigger.create(id)).withRewards(AdvancementRewards.Builder.recipe(id)).withRequirementsStrategy(IRequirementsStrategy.OR);
		consumerIn.accept(new Result(id, result, craftingTime, count, this.group == null ? "" : group, ingredients, byproducts, advancementBuilder, new ResourceLocation(id.getNamespace(), "recipes/" + (result.getGroup() != null ? result.getGroup().getPath() : BiomancyMod.MOD_ID) + "/" + id.getPath())));
	}

	private void validate(ResourceLocation id) {
		if (advancementBuilder.getCriteria().isEmpty()) {
			throw new IllegalStateException("No way of obtaining recipe " + id);
		}
	}

	public static class Byproduct {
		private final Item result;
		private final int count;
		private final float chance;

		public Byproduct(IItemProvider resultIn, int countIn, float chanceIn) {
			assert chanceIn > 0f && chanceIn <= 1f;
			assert countIn > 0;

			result = resultIn.asItem();
			count = countIn;
			chance = chanceIn;
		}

		public JsonElement serialize() {
			JsonObject parent = new JsonObject();

			JsonObject itemObject = new JsonObject();
			itemObject.addProperty("item", Registry.ITEM.getKey(result).toString());
			if (count > 1) {
				itemObject.addProperty("count", count);
			}
			parent.add("result", itemObject);

			parent.addProperty("chance", chance);

			return parent;
		}
	}

	public static class Result implements IFinishedRecipe {
		private final ResourceLocation id;
		private final String group;
		private final List<Ingredient> ingredients;
		private final List<Byproduct> byproducts;
		private final Item result;
		private final int count;
		private final int craftingTime;
		private final Advancement.Builder advancementBuilder;
		private final ResourceLocation advancementId;

		public Result(ResourceLocation idIn, Item resultIn, int craftingTimeIn, int countIn, String groupIn, List<Ingredient> ingredientsIn, List<Byproduct> byproductsIn, Advancement.Builder advancementBuilderIn, ResourceLocation advancementIdIn) {
			id = idIn;
			group = groupIn;
			ingredients = ingredientsIn;
			byproducts = byproductsIn;
			result = resultIn;
			count = countIn;
			craftingTime = craftingTimeIn;
			advancementBuilder = advancementBuilderIn;
			advancementId = advancementIdIn;
		}

		public void serialize(JsonObject json) {
			if (!this.group.isEmpty()) {
				json.addProperty("group", this.group);
			}

			JsonArray jsonArray = new JsonArray();
			for (Ingredient ingredient : ingredients) {
				jsonArray.add(ingredient.serialize());
			}
			json.add("ingredients", jsonArray);

			jsonArray = new JsonArray();
			for (Byproduct byproduct : byproducts) {
				jsonArray.add(byproduct.serialize());
			}
			json.add("byproducts", jsonArray);

			JsonObject jsonobject = new JsonObject();
			jsonobject.addProperty("item", Registry.ITEM.getKey(result).toString());
			if (count > 1) {
				jsonobject.addProperty("count", count);
			}
			json.add("result", jsonobject);

			json.addProperty("time", craftingTime);
		}

		public IRecipeSerializer<?> getSerializer() {
			return ModRecipes.DECOMPOSING_SERIALIZER.get();
		}

		public ResourceLocation getID() {
			return id;
		}

		@Nullable
		public JsonObject getAdvancementJson() {
			return advancementBuilder.serialize();
		}

		@Nullable
		public ResourceLocation getAdvancementID() {
			return advancementId;
		}
	}
}

