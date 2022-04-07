package com.github.elenterius.biomancy.datagen.recipes;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.init.ModRecipes;
import com.github.elenterius.biomancy.recipe.BioForgeCategory;
import com.github.elenterius.biomancy.recipe.IngredientQuantity;
import com.github.elenterius.biomancy.recipe.ItemStackIngredient;
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
import net.minecraft.world.item.ItemStack;
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
	private Ingredient reactant = Ingredient.EMPTY;
	private int craftingTime = 20;
	private BioForgeCategory category = BioForgeCategory.MISC;
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

	public BioForgeRecipeBuilder setCraftingTime(int time) {
		if (time < 0) throw new IllegalArgumentException("Invalid crafting time: " + time);
		craftingTime = time;
		return this;
	}

	public BioForgeRecipeBuilder setCategory(BioForgeCategory category) {
		this.category = category;
		return this;
	}

	public BioForgeRecipeBuilder setReactant(ItemLike item) {
		return setReactant(Ingredient.of(item));
	}

	public BioForgeRecipeBuilder setReactant(Tag<Item> tag) {
		return setReactant(Ingredient.of(tag));
	}

	public BioForgeRecipeBuilder setReactant(ItemStack stack) {
		return setReactant(new ItemStackIngredient(stack));
	}

	public BioForgeRecipeBuilder setReactant(Ingredient ingredient) {
		reactant = ingredient;
		return this;
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
		consumer.accept(new Result(recipeId, category, group == null ? "" : group, result, craftingTime, ingredients, reactant, advancement, advancementId));
	}

	private void validateCriteria(ResourceLocation id) {
		if (advancement.getCriteria().isEmpty()) {
			throw new IllegalStateException("No way of obtaining recipe " + id + " because Criteria are empty.");
		}
	}

	public static class Result implements FinishedRecipe {
		private final ResourceLocation id;
		private final String group;

		private final List<IngredientQuantity> ingredients;
		private final Ingredient reactant;
		private final ItemData result;
		private final int craftingTime;
		private final BioForgeCategory category;

		private final Advancement.Builder advancementBuilder;
		private final ResourceLocation advancementId;

		public Result(ResourceLocation recipeId, BioForgeCategory category, String group, ItemData result, int craftingTime, List<IngredientQuantity> ingredients, Ingredient reactant, Advancement.Builder advancement, ResourceLocation advancementId) {
			id = recipeId;
			this.group = group;

			this.category = category;
			this.result = result;
			this.reactant = reactant;
			this.ingredients = ingredients;
			this.craftingTime = craftingTime;

			advancementBuilder = advancement;
			this.advancementId = advancementId;
		}

		@Override
		public void serializeRecipeData(JsonObject json) {
			if (!group.isEmpty()) {
				json.addProperty("group", group);
			}

			JsonArray jsonArray = new JsonArray();
			for (IngredientQuantity ingredient : ingredients) {
				jsonArray.add(ingredient.toJson());
			}
			json.add("ingredient_quantities", jsonArray);

			if (!reactant.isEmpty()) {
				json.add("reactant", reactant.toJson());
			}

			json.add("result", result.toJson());

			json.addProperty("time", craftingTime);

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

