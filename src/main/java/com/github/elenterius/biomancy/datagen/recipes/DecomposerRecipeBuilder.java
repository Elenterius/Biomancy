package com.github.elenterius.biomancy.datagen.recipes;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.init.ModRecipes;
import com.github.elenterius.biomancy.recipe.IngredientQuantity;
import com.github.elenterius.biomancy.recipe.VariableProductionOutput;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.CriterionTriggerInstance;
import net.minecraft.advancements.RequirementsStrategy;
import net.minecraft.advancements.critereon.RecipeUnlockedTrigger;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.Tag;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.ItemLike;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

public class DecomposerRecipeBuilder {

	private final ResourceLocation recipeId;
	private final List<VariableProductionOutput> outputs = new ArrayList<>();
	private final Advancement.Builder advancement = Advancement.Builder.advancement();
	private IngredientQuantity ingredientQuantity = null;
	private int craftingTime = 4 * 20;
	@Nullable
	private String group;

	private DecomposerRecipeBuilder(ResourceLocation recipeId) {
		this.recipeId = recipeId;
	}

	public static DecomposerRecipeBuilder create(String modId, String ingredientName) {
		ResourceLocation rl = new ResourceLocation(modId, ingredientName + "_decomposing");
		return new DecomposerRecipeBuilder(rl);
	}

	public static DecomposerRecipeBuilder create(String ingredientName) {
		ResourceLocation rl = BiomancyMod.createRL(ingredientName + "_decomposing");
		return new DecomposerRecipeBuilder(rl);
	}

	public static DecomposerRecipeBuilder create(ResourceLocation recipeId) {
		return new DecomposerRecipeBuilder(recipeId);
	}

	public DecomposerRecipeBuilder setCraftingTime(int time) {
		if (time < 0) throw new IllegalArgumentException("Invalid crafting time: " + time);
		craftingTime = time;
		return this;
	}

	public DecomposerRecipeBuilder setIngredient(Tag<Item> tagIn) {
		return setIngredient(Ingredient.of(tagIn));
	}

	public DecomposerRecipeBuilder setIngredient(Tag<Item> tagIn, int quantity) {
		return setIngredient(Ingredient.of(tagIn), quantity);
	}

	public DecomposerRecipeBuilder setIngredient(ItemLike itemIn) {
		return setIngredient(itemIn, 1);
	}

	public DecomposerRecipeBuilder setIngredient(Ingredient ingredientIn) {
		return setIngredient(ingredientIn, 1);
	}

	public DecomposerRecipeBuilder setIngredient(ItemLike itemIn, int quantity) {
		setIngredient(Ingredient.of(itemIn), quantity);
		return this;
	}

	public DecomposerRecipeBuilder setIngredient(Ingredient ingredient, int count) {
		if (ingredientQuantity != null) throw new IllegalStateException("Ingredient is already set");
		ingredientQuantity = new IngredientQuantity(ingredient, count);
		return this;
	}

	public DecomposerRecipeBuilder addOutput(ItemLike resultIn) {
		return addOutput(resultIn, 1);
	}

	public DecomposerRecipeBuilder addOutput(ItemLike resultIn, int count) {
		return addOutput(new VariableProductionOutput(resultIn, count));
	}

	public DecomposerRecipeBuilder addOutput(ItemLike resultIn, int min, int max) {
		return addOutput(new VariableProductionOutput(resultIn, min, max));
	}

	public DecomposerRecipeBuilder addOutput(VariableProductionOutput output) {
		outputs.add(output);
		return this;
	}

	public DecomposerRecipeBuilder addOutputs(VariableProductionOutput... outputsIn) {
		outputs.addAll(Arrays.asList(outputsIn));
		return this;
	}

	public DecomposerRecipeBuilder unlockedBy(String name, CriterionTriggerInstance criterionTrigger) {
		advancement.addCriterion(name, criterionTrigger);
		return this;
	}

	public DecomposerRecipeBuilder setGroup(@Nullable String name) {
		group = name;
		return this;
	}

	public void save(Consumer<FinishedRecipe> consumer) {
		save(consumer, null);
	}

	public void save(Consumer<FinishedRecipe> consumer, @Nullable CreativeModeTab itemCategory) {
		validateCriteria(recipeId);
		advancement.parent(new ResourceLocation("recipes/root"))
				.addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(recipeId))
				.rewards(AdvancementRewards.Builder.recipe(recipeId)).requirements(RequirementsStrategy.OR);
		ResourceLocation advancementId = new ResourceLocation(recipeId.getNamespace(),
				"recipes/" + (itemCategory != null ? itemCategory.getRecipeFolderName() : BiomancyMod.MOD_ID) + "/" + recipeId.getPath());
		consumer.accept(new Result(recipeId, group == null ? "" : group, ingredientQuantity, craftingTime, outputs, advancement, advancementId));
	}

	private void validateCriteria(ResourceLocation id) {
		if (advancement.getCriteria().isEmpty()) {
			throw new IllegalStateException("No way of obtaining recipe " + id + " because Criteria are empty.");
		}
	}

	public static class Result implements FinishedRecipe {

		private final ResourceLocation id;
		private final String group;
		private final IngredientQuantity ingredientQuantity;
		private final List<VariableProductionOutput> outputs;
		private final int craftingTime;
		private final Advancement.Builder advancementBuilder;
		private final ResourceLocation advancementId;

		public Result(ResourceLocation idIn, String groupIn, IngredientQuantity ingredientQuantityIn, int craftingTimeIn, List<VariableProductionOutput> outputsIn, Advancement.Builder advancementBuilderIn, ResourceLocation advancementIdIn) {
			id = idIn;
			group = groupIn;
			ingredientQuantity = ingredientQuantityIn;
			craftingTime = craftingTimeIn;
			outputs = outputsIn;
			advancementBuilder = advancementBuilderIn;
			advancementId = advancementIdIn;
		}

		public void serializeRecipeData(JsonObject json) {
			if (!group.isEmpty()) {
				json.addProperty("group", group);
			}

			json.add("input", ingredientQuantity.toJson());

			JsonArray jsonArray = new JsonArray();
			for (VariableProductionOutput output : outputs) {
				jsonArray.add(output.serialize());
			}
			json.add("outputs", jsonArray);

			json.addProperty("time", craftingTime);
		}

		public RecipeSerializer<?> getType() {
			return ModRecipes.DECOMPOSING_SERIALIZER.get();
		}

		public ResourceLocation getId() {
			return id;
		}

		@Nullable
		public JsonObject serializeAdvancement() {
			return advancementBuilder.serializeToJson();
		}

		@Nullable
		public ResourceLocation getAdvancementId() {
			return advancementId;
		}
	}
}

