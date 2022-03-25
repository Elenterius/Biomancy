package com.github.elenterius.biomancy.datagen.recipes;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.init.ModRecipes;
import com.github.elenterius.biomancy.recipe.IngredientQuantity;
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
import java.util.List;
import java.util.function.Consumer;

public class BioForgeRecipeBuilder implements IRecipeBuilder {

	public static final String SUFFIX = "_from_bio_forging";

	private final ResourceLocation recipeId;
	private final ItemData result;
	private final List<IngredientQuantity> ingredients = new ArrayList<>();

	private final Advancement.Builder advancement = Advancement.Builder.advancement();
	@Nullable
	private String group;

	private BioForgeRecipeBuilder(ResourceLocation recipeId, ItemData result) {
		this.recipeId = recipeId;
		this.result = result;
	}

	public static BioForgeRecipeBuilder create(String modId, String outputName, ItemData result) {
		ResourceLocation rl = new ResourceLocation(modId, outputName + SUFFIX);
		return new BioForgeRecipeBuilder(rl, result);
	}

	public static BioForgeRecipeBuilder create(String outputName, ItemData result) {
		ResourceLocation rl = BiomancyMod.createRL(outputName + SUFFIX);
		return new BioForgeRecipeBuilder(rl, result);
	}

	public static BioForgeRecipeBuilder create(ResourceLocation recipeId, ItemData result) {
		return new BioForgeRecipeBuilder(recipeId, result);
	}

	public static BioForgeRecipeBuilder create(ItemData result) {
		ResourceLocation rl = BiomancyMod.createRL(result.getItemNamedId() + SUFFIX);
		return new BioForgeRecipeBuilder(rl, result);
	}

	public BioForgeRecipeBuilder addIngredient(Tag<Item> tagIn) {
		return addIngredient(Ingredient.of(tagIn));
	}

	public BioForgeRecipeBuilder addIngredient(Tag<Item> tagIn, int quantity) {
		return addIngredient(Ingredient.of(tagIn), quantity);
	}

	public BioForgeRecipeBuilder addIngredient(ItemLike itemIn) {
		return addIngredient(itemIn, 1);
	}

	public BioForgeRecipeBuilder addIngredient(Ingredient ingredientIn) {
		return addIngredient(ingredientIn, 1);
	}

	public BioForgeRecipeBuilder addIngredient(ItemLike itemIn, int quantity) {
		addIngredient(Ingredient.of(itemIn), quantity);
		return this;
	}

	public BioForgeRecipeBuilder addIngredient(Ingredient ingredient, int count) {
		ingredients.add(new IngredientQuantity(ingredient, count));
		return this;
	}

	@Override
	public BioForgeRecipeBuilder unlockedBy(String name, CriterionTriggerInstance criterionTrigger) {
		advancement.addCriterion(name, criterionTrigger);
		return this;
	}

	public BioForgeRecipeBuilder setGroup(@Nullable String name) {
		group = name;
		return this;
	}

	@Override
	public void save(Consumer<FinishedRecipe> consumer, @Nullable CreativeModeTab itemCategory) {
		validateCriteria(recipeId);
		advancement.parent(new ResourceLocation("recipes/root"))
				.addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(recipeId))
				.rewards(AdvancementRewards.Builder.recipe(recipeId)).requirements(RequirementsStrategy.OR);
		ResourceLocation advancementId = new ResourceLocation(recipeId.getNamespace(),
				"recipes/" + (itemCategory != null ? itemCategory.getRecipeFolderName() : BiomancyMod.MOD_ID) + "/" + recipeId.getPath());
		consumer.accept(new Result(recipeId, group == null ? "" : group, result, ingredients, advancement, advancementId));
	}

	private void validateCriteria(ResourceLocation id) {
		if (advancement.getCriteria().isEmpty()) {
			throw new IllegalStateException("No way of obtaining recipe " + id + " because Criteria are empty.");
		}
	}

	public static class Result implements FinishedRecipe {

		private final ResourceLocation id;
		private final String group;

		private final ItemData result;
		private final List<IngredientQuantity> ingredients;

		private final Advancement.Builder advancementBuilder;
		private final ResourceLocation advancementId;

		public Result(ResourceLocation recipeId, String groupIn, ItemData result, List<IngredientQuantity> ingredients, Advancement.Builder advancement, ResourceLocation advancementId) {
			id = recipeId;
			group = groupIn;
			this.result = result;
			this.ingredients = ingredients;
			advancementBuilder = advancement;
			this.advancementId = advancementId;
		}

		public void serializeRecipeData(JsonObject json) {
			if (!group.isEmpty()) {
				json.addProperty("group", group);
			}

			JsonArray jsonArray = new JsonArray();
			for (IngredientQuantity output : ingredients) {
				jsonArray.add(output.toJson());
			}
			json.add("inputs", jsonArray);

			json.add("result", result.toJson());
		}

		public RecipeSerializer<?> getType() {
			return ModRecipes.BIO_FORGING_SERIALIZER.get();
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

