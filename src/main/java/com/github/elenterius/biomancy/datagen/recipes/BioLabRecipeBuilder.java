package com.github.elenterius.biomancy.datagen.recipes;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.init.ModItems;
import com.github.elenterius.biomancy.init.ModRecipes;
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
import net.minecraft.tags.TagKey;
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

public class BioLabRecipeBuilder implements IRecipeBuilder {

	public static final String SUFFIX = "_from_bio_brewing";

	private final ResourceLocation recipeId;
	private final ItemData result;
	private final List<Ingredient> ingredients = new ArrayList<>();
	private Ingredient reactant = Ingredient.of(ModItems.GLASS_VIAL.get());
	private int craftingTime = 4 * 20;

	private final Advancement.Builder advancement = Advancement.Builder.advancement();
	@Nullable
	private String group;

	private BioLabRecipeBuilder(ResourceLocation recipeId, ItemData result) {
		this.recipeId = recipeId;
		this.result = result;
	}

	public static BioLabRecipeBuilder create(String modId, String outputName, ItemData result) {
		ResourceLocation rl = new ResourceLocation(modId, outputName + SUFFIX);
		return new BioLabRecipeBuilder(rl, result);
	}

	public static BioLabRecipeBuilder create(String outputName, ItemData result) {
		ResourceLocation rl = BiomancyMod.createRL(outputName + SUFFIX);
		return new BioLabRecipeBuilder(rl, result);
	}

	public static BioLabRecipeBuilder create(ResourceLocation recipeId, ItemData result) {
		return new BioLabRecipeBuilder(recipeId, result);
	}

	public static BioLabRecipeBuilder create(ItemData result) {
		ResourceLocation rl = BiomancyMod.createRL(result.getItemNamedId() + SUFFIX);
		return new BioLabRecipeBuilder(rl, result);
	}

	public static BioLabRecipeBuilder create(ItemLike item) {
		ItemData itemData = new ItemData(item);
		ResourceLocation rl = BiomancyMod.createRL(Objects.requireNonNull(item.asItem().getRegistryName()).getPath() + SUFFIX);
		return new BioLabRecipeBuilder(rl, itemData);
	}

	public static BioLabRecipeBuilder create(ItemLike item, int count) {
		ItemData itemData = new ItemData(item, count);
		ResourceLocation rl = BiomancyMod.createRL(Objects.requireNonNull(item.asItem().getRegistryName()).getPath() + SUFFIX);
		return new BioLabRecipeBuilder(rl, itemData);
	}

	public BioLabRecipeBuilder setCraftingTime(int time) {
		if (time < 0) throw new IllegalArgumentException("Invalid crafting time: " + time);
		craftingTime = time;
		return this;
	}

	public BioLabRecipeBuilder setReactant(ItemLike item) {
		return setReactant(Ingredient.of(item));
	}

	public BioLabRecipeBuilder setReactant(TagKey<Item> tag) {
		return setReactant(Ingredient.of(tag));
	}

	public BioLabRecipeBuilder setReactant(ItemStack stack) {
		return setReactant(new ItemStackIngredient(stack));
	}

	public BioLabRecipeBuilder setReactant(Ingredient ingredient) {
		reactant = ingredient;
		return this;
	}

	public BioLabRecipeBuilder addIngredient(TagKey<Item> tag) {
		return addIngredient(Ingredient.of(tag));
	}

	public BioLabRecipeBuilder addIngredients(TagKey<Item> tag, int quantity) {
		return addIngredients(Ingredient.of(tag), quantity);
	}

	public BioLabRecipeBuilder addIngredient(ItemLike item) {
		return addIngredients(item, 1);
	}

	public BioLabRecipeBuilder addIngredient(ItemStack stack) {
		return addIngredients(new ItemStackIngredient(stack), 1);
	}

	public BioLabRecipeBuilder addIngredient(Ingredient ingredient) {
		return addIngredients(ingredient, 1);
	}

	public BioLabRecipeBuilder addIngredients(Ingredient ingredient, int quantity) {
		for (int i = 0; i < quantity; i++) ingredients.add(ingredient);
		return this;
	}

	public BioLabRecipeBuilder addIngredients(ItemLike itemIn, int quantity) {
		for (int i = 0; i < quantity; i++) addIngredient(Ingredient.of(itemIn));
		return this;
	}

	@Override
	public BioLabRecipeBuilder unlockedBy(String name, CriterionTriggerInstance criterionTrigger) {
		advancement.addCriterion(name, criterionTrigger);
		return this;
	}

	public BioLabRecipeBuilder setGroup(@Nullable String name) {
		this.group = name;
		return this;
	}

	@Override
	public void save(Consumer<FinishedRecipe> consumer, @Nullable CreativeModeTab itemCategory) {
		validateCriteria(recipeId);
		advancement.parent(new ResourceLocation("recipes/root")).addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(recipeId)).rewards(AdvancementRewards.Builder.recipe(recipeId)).requirements(RequirementsStrategy.OR);
		ResourceLocation advancementId = new ResourceLocation(recipeId.getNamespace(), "recipes/" + (itemCategory != null ? itemCategory.getRecipeFolderName() : BiomancyMod.MOD_ID) + "/" + recipeId.getPath());
		consumer.accept(new Result(recipeId, group == null ? "" : group, result, craftingTime, ingredients, reactant, advancement, advancementId));
	}

	private void validateCriteria(ResourceLocation id) {
		if (advancement.getCriteria().isEmpty()) {
			throw new IllegalStateException("No way of obtaining recipe " + id + " because Criteria are empty.");
		}
	}

	public static class Result implements FinishedRecipe {
		private final ResourceLocation id;
		private final String group;

		private final List<Ingredient> ingredients;
		private final Ingredient reactant;
		private final ItemData result;
		private final int craftingTime;

		private final Advancement.Builder advancementBuilder;
		private final ResourceLocation advancementId;

		public Result(ResourceLocation recipeId, String group, ItemData result, int craftingTime, List<Ingredient> ingredients, Ingredient reactant, Advancement.Builder advancement, ResourceLocation advancementId) {
			id = recipeId;
			this.group = group;

			this.ingredients = ingredients;
			this.reactant = reactant;
			this.result = result;
			this.craftingTime = craftingTime;

			advancementBuilder = advancement;
			this.advancementId = advancementId;
		}

		public void serializeRecipeData(JsonObject json) {
			if (!group.isEmpty()) {
				json.addProperty("group", group);
			}

			JsonArray jsonArray = new JsonArray();
			for (Ingredient ingredient : ingredients) {
				jsonArray.add(ingredient.toJson());
			}
			json.add("ingredients", jsonArray);

			if (!reactant.isEmpty()) {
				json.add("reactant", reactant.toJson());
			}

			json.add("result", result.toJson());

			json.addProperty("time", craftingTime);
		}

		public RecipeSerializer<?> getType() {
			return ModRecipes.BIO_BREWING_SERIALIZER.get();
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
