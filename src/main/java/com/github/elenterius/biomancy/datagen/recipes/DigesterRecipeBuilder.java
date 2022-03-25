package com.github.elenterius.biomancy.datagen.recipes;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.init.ModRecipes;
import com.github.elenterius.biomancy.recipe.ItemStackIngredient;
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
import java.util.function.Consumer;

public class DigesterRecipeBuilder implements IRecipeBuilder {

	public static final String SUFFIX = "_from_digesting";

	private final Advancement.Builder advancement = Advancement.Builder.advancement();

	private final ResourceLocation recipeId;
	private final ItemData recipeResult;
	private Ingredient recipeIngredient;
	private int craftingTime = 4 * 20;

	@Nullable
	private String group;

	private DigesterRecipeBuilder(ResourceLocation recipeId, ItemData result) {
		this.recipeId = recipeId;
		recipeResult = result;
	}

	public static DigesterRecipeBuilder create(String modId, String outputName, ItemData result) {
		ResourceLocation rl = new ResourceLocation(modId, outputName + SUFFIX);
		return new DigesterRecipeBuilder(rl, result);
	}

	public static DigesterRecipeBuilder create(String outputName, ItemData result) {
		ResourceLocation rl = BiomancyMod.createRL(outputName + SUFFIX);
		return new DigesterRecipeBuilder(rl, result);
	}

	public static DigesterRecipeBuilder create(ResourceLocation recipeId, ItemData result) {
		return new DigesterRecipeBuilder(recipeId, result);
	}

	public static DigesterRecipeBuilder create(ItemData result) {
		ResourceLocation rl = BiomancyMod.createRL(result.getItemNamedId() + SUFFIX);
		return new DigesterRecipeBuilder(rl, result);
	}

	public static DigesterRecipeBuilder create(ItemData result, String postSuffix) {
		ResourceLocation rl = BiomancyMod.createRL(result.getItemNamedId() + SUFFIX + "_" + postSuffix);
		return new DigesterRecipeBuilder(rl, result);
	}

	public static DigesterRecipeBuilder create(ItemLike item) {
		return create(new ItemData(item));
	}

	public static DigesterRecipeBuilder create(ItemLike item, int count) {
		return create(new ItemData(item, count));
	}

	public static DigesterRecipeBuilder create(ItemLike item, int count, String postSuffix) {
		return create(new ItemData(item, count), postSuffix);
	}

	public DigesterRecipeBuilder setCraftingTime(int time) {
		if (time < 0) throw new IllegalArgumentException("Invalid crafting time: " + time);
		craftingTime = time;
		return this;
	}

	public DigesterRecipeBuilder setIngredient(ItemLike item) {
		return setIngredient(Ingredient.of(item));
	}

	public DigesterRecipeBuilder setIngredient(Tag<Item> tag) {
		return setIngredient(Ingredient.of(tag));
	}

	public DigesterRecipeBuilder setIngredient(ItemStack stack) {
		return setIngredient(new ItemStackIngredient(stack));
	}

	public DigesterRecipeBuilder setIngredient(Ingredient ingredient) {
		this.recipeIngredient = ingredient;
		return this;
	}

	@Override
	public DigesterRecipeBuilder unlockedBy(String name, CriterionTriggerInstance criterionTrigger) {
		advancement.addCriterion(name, criterionTrigger);
		return this;
	}

	public DigesterRecipeBuilder setGroup(@Nullable String name) {
		this.group = name;
		return this;
	}

	@Override
	public void save(Consumer<FinishedRecipe> consumer, @Nullable CreativeModeTab itemCategory) {
		validateCriteria(recipeId);
		advancement.parent(new ResourceLocation("recipes/root")).addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(recipeId)).rewards(AdvancementRewards.Builder.recipe(recipeId)).requirements(RequirementsStrategy.OR);
		ResourceLocation advancementId = new ResourceLocation(recipeId.getNamespace(), "recipes/" + (itemCategory != null ? itemCategory.getRecipeFolderName() : BiomancyMod.MOD_ID) + "/" + recipeId.getPath());
		consumer.accept(new Result(recipeId, group == null ? "" : group, recipeResult, craftingTime, recipeIngredient, advancement, advancementId));
	}

	private void validateCriteria(ResourceLocation id) {
		if (advancement.getCriteria().isEmpty()) {
			throw new IllegalStateException("No way of obtaining recipe " + id + " because Criteria are empty.");
		}
	}

	public static class Result implements FinishedRecipe {
		private final ResourceLocation id;
		private final String group;

		private final Ingredient ingredient;
		private final ItemData recipeResult;
		private final int craftingTime;

		private final Advancement.Builder advancementBuilder;
		private final ResourceLocation advancementId;

		public Result(ResourceLocation recipeId, String group, ItemData result, int craftingTime, Ingredient ingredient, Advancement.Builder advancement, ResourceLocation advancementId) {
			id = recipeId;
			this.group = group;
			this.ingredient = ingredient;
			this.recipeResult = result;
			this.craftingTime = craftingTime;
			advancementBuilder = advancement;
			this.advancementId = advancementId;
		}

		public void serializeRecipeData(JsonObject json) {
			if (!group.isEmpty()) {
				json.addProperty("group", group);
			}

			json.add("ingredient", ingredient.toJson());

			json.add("result", recipeResult.toJson());

			json.addProperty("time", craftingTime);
		}

		public RecipeSerializer<?> getType() {
			return ModRecipes.DIGESTING_SERIALIZER.get();
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
