package com.github.elenterius.biomancy.datagen.recipes;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.init.ModRecipeBooks;
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
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.ItemLike;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

public class BioForgeRecipeBuilder implements IRecipeBuilder {

	public static final String SUFFIX = "_from_bio_forging";

	private final ResourceLocation recipeId;
	private final ItemData result;
	private final List<IngredientQuantity> ingredients = new ArrayList<>();
	private final Advancement.Builder advancement = Advancement.Builder.advancement();
	private ModRecipeBooks.BioForgeCategory category = ModRecipeBooks.BioForgeCategory.MISC;

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

	public static BioForgeRecipeBuilder create(ItemLike item) {
		ItemData itemData = new ItemData(item);
		ResourceLocation rl = BiomancyMod.createRL(Objects.requireNonNull(item.asItem().getRegistryName()).getPath() + SUFFIX);
		return new BioForgeRecipeBuilder(rl, itemData);
	}

	public static BioForgeRecipeBuilder create(ItemLike item, int count) {
		ItemData itemData = new ItemData(item, count);
		ResourceLocation rl = BiomancyMod.createRL(Objects.requireNonNull(item.asItem().getRegistryName()).getPath() + SUFFIX);
		return new BioForgeRecipeBuilder(rl, itemData);
	}

	//	public BioForgeRecipeBuilder setCraftingTime(int time) {
	//		if (time < 0) throw new IllegalArgumentException("Invalid crafting time: " + time);
	//		craftingTime = time;
	//		return this;
	//	}

	public BioForgeRecipeBuilder setCategory(ModRecipeBooks.BioForgeCategory category) {
		this.category = category;
		return this;
	}

	public BioForgeRecipeBuilder addIngredient(TagKey<Item> tagIn) {
		return addIngredient(Ingredient.of(tagIn));
	}

	public BioForgeRecipeBuilder addIngredient(TagKey<Item> tagIn, int quantity) {
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

	@Override
	public void save(Consumer<FinishedRecipe> consumer, @Nullable CreativeModeTab itemCategory) {
		validateCriteria(recipeId);
		advancement.parent(new ResourceLocation("recipes/root"))
				.addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(recipeId))
				.rewards(AdvancementRewards.Builder.recipe(recipeId)).requirements(RequirementsStrategy.OR);
		ResourceLocation advancementId = new ResourceLocation(recipeId.getNamespace(),
				"recipes/" + (itemCategory != null ? itemCategory.getRecipeFolderName() : BiomancyMod.MOD_ID) + "/" + recipeId.getPath());
		consumer.accept(new RecipeResult(recipeId, category, result, ingredients, advancement, advancementId));
	}

	private void validateCriteria(ResourceLocation id) {
		if (advancement.getCriteria().isEmpty()) {
			throw new IllegalStateException("No way of obtaining recipe " + id + " because Criteria are empty.");
		}
	}

	public static class RecipeResult implements FinishedRecipe {
		private final ResourceLocation id;
		private final List<IngredientQuantity> ingredients;
		private final ItemData result;
		private final ModRecipeBooks.BioForgeCategory category;

		private final Advancement.Builder advancementBuilder;
		private final ResourceLocation advancementId;

		public RecipeResult(ResourceLocation recipeId, ModRecipeBooks.BioForgeCategory category, ItemData result, List<IngredientQuantity> ingredients, Advancement.Builder advancement, ResourceLocation advancementId) {
			id = recipeId;
			this.category = category;
			this.result = result;
			this.ingredients = ingredients;

			advancementBuilder = advancement;
			this.advancementId = advancementId;
		}

		@Override
		public void serializeRecipeData(JsonObject json) {
			JsonArray jsonArray = new JsonArray();
			for (IngredientQuantity ingredient : ingredients) {
				jsonArray.add(ingredient.toJson());
			}
			json.add("ingredient_quantities", jsonArray);

			json.add("result", result.toJson());

			category.toJson(json);
		}

		@Override
		public RecipeSerializer<?> getType() {
			return ModRecipes.BIO_FORGING_SERIALIZER.get();
		}

		@Override
		public ResourceLocation getId() {
			return id;
		}

		@Override
		@Nullable
		public JsonObject serializeAdvancement() {
			return advancementBuilder.serializeToJson();
		}

		@Override
		@Nullable
		public ResourceLocation getAdvancementId() {
			return advancementId;
		}

	}
}

