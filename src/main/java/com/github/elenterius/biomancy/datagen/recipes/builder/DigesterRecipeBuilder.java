package com.github.elenterius.biomancy.datagen.recipes.builder;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.init.ModItems;
import com.github.elenterius.biomancy.init.ModRecipes;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.CriterionTriggerInstance;
import net.minecraft.advancements.RequirementsStrategy;
import net.minecraft.advancements.critereon.RecipeUnlockedTrigger;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.common.crafting.ConditionalAdvancement;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.crafting.conditions.ICondition;
import net.minecraftforge.common.crafting.conditions.ModLoadedCondition;
import net.minecraftforge.common.crafting.conditions.NotCondition;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.IntUnaryOperator;

public final class DigesterRecipeBuilder implements RecipeBuilder {

	public static final String RECIPE_SUB_FOLDER = ModRecipes.DIGESTING_RECIPE_TYPE.getId().getPath();
	public static final String SUFFIX = "_from_" + RECIPE_SUB_FOLDER;

	public static final short DEFAULT_CRAFTING_COST_NUTRIENTS = 2;

	private final ResourceLocation recipeId;
	private final List<ICondition> conditions = new ArrayList<>();
	private final ItemData recipeResult;
	private final Advancement.Builder advancement = Advancement.Builder.recipeAdvancement();
	private Ingredient recipeIngredient;
	private int craftingTimeTicks = -1;
	private int craftingCostNutrients = -1;
	@Nullable
	private String group;

	private DigesterRecipeBuilder(ResourceLocation recipeId, ItemData result) {
		this.recipeId = new ResourceLocation(recipeId.getNamespace(), RECIPE_SUB_FOLDER + "/" + recipeId.getPath());
		recipeResult = result;

		if (recipeResult.getRegistryName().equals(ModItems.NUTRIENT_PASTE.getId())) {
			craftingTimeTicks = Mth.ceil(200 + 190 * Math.log(recipeResult.getCount()));
		}
		else if (recipeResult.getRegistryName().equals(ModItems.NUTRIENT_BAR.getId())) {
			craftingTimeTicks = Mth.ceil(200 + 190 * Math.log(recipeResult.getCount() * 9d));
		}
	}

	public static DigesterRecipeBuilder create(ResourceLocation recipeId, ItemData result) {
		return new DigesterRecipeBuilder(recipeId, result);
	}

	public static DigesterRecipeBuilder create(String modId, String outputName, ItemData result) {
		ResourceLocation rl = new ResourceLocation(modId, outputName + SUFFIX);
		return new DigesterRecipeBuilder(rl, result);
	}

	public static DigesterRecipeBuilder create(String outputName, ItemData result) {
		ResourceLocation rl = BiomancyMod.createRL(outputName + SUFFIX);
		return new DigesterRecipeBuilder(rl, result);
	}

	public static DigesterRecipeBuilder create(ItemData result) {
		ResourceLocation rl = BiomancyMod.createRL(result.getItemPath() + SUFFIX);
		return new DigesterRecipeBuilder(rl, result);
	}

	public static DigesterRecipeBuilder create(ItemData result, String postSuffix) {
		ResourceLocation rl = BiomancyMod.createRL(result.getItemPath() + SUFFIX + "_" + postSuffix);
		return new DigesterRecipeBuilder(rl, result);
	}

	public static DigesterRecipeBuilder create(ItemStack stack) {
		return create(new ItemData(stack));
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

	public DigesterRecipeBuilder ifModLoaded(String modId) {
		return withCondition(new ModLoadedCondition(modId));
	}

	public DigesterRecipeBuilder ifModMissing(String modId) {
		return withCondition(new NotCondition(new ModLoadedCondition(modId)));
	}

	public DigesterRecipeBuilder withCondition(ICondition condition) {
		conditions.add(condition);
		return this;
	}

	public DigesterRecipeBuilder modifyCraftingTime(IntUnaryOperator func) {
		craftingTimeTicks = func.applyAsInt(craftingTimeTicks);
		return this;
	}

	public DigesterRecipeBuilder setCraftingCost(int costNutrients) {
		if (costNutrients < 0) throw new IllegalArgumentException("Invalid crafting cost: " + costNutrients);
		craftingCostNutrients = costNutrients;
		return this;
	}

	public DigesterRecipeBuilder setIngredient(ItemLike item) {
		return setIngredient(Ingredient.of(item));
	}

	public DigesterRecipeBuilder setIngredient(TagKey<Item> tag) {
		return setIngredient(Ingredient.of(tag));
	}

	public DigesterRecipeBuilder setIngredient(ItemStack stack) {
		return setIngredient(Ingredient.of(stack));
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
	public void save(Consumer<FinishedRecipe> consumer, @Nullable RecipeCategory category) {
		validateCriteria();

		if (craftingTimeTicks < 0) {
			throw new IllegalArgumentException("Invalid crafting time: " + craftingTimeTicks);
		}

		if (craftingCostNutrients < 0) {
			craftingCostNutrients = RecipeCostUtil.getCost(DEFAULT_CRAFTING_COST_NUTRIENTS, craftingTimeTicks);
		}

		advancement.parent(new ResourceLocation("recipes/root")).addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(recipeId)).rewards(AdvancementRewards.Builder.recipe(recipeId)).requirements(RequirementsStrategy.OR);

		String folderName = RecipeBuilder.getRecipeFolderName(category, BiomancyMod.MOD_ID);
		ResourceLocation advancementId = new ResourceLocation(recipeId.getNamespace(), "recipes/%s/%s".formatted(folderName, recipeId.getPath()));

		consumer.accept(new Result(this, advancementId));
	}

	private void validateCriteria() {
		if (advancement.getCriteria().isEmpty()) {
			throw new IllegalStateException("No way of obtaining recipe %s because Criteria are empty.".formatted(recipeId));
		}
	}

	public static class Result implements FinishedRecipe {

		private final ResourceLocation id;
		private final String group;
		private final Ingredient ingredient;
		private final ItemData recipeResult;
		private final int craftingTime;
		private final int craftingCost;
		private final List<ICondition> conditions;
		private final Advancement.Builder advancementBuilder;
		private final ResourceLocation advancementId;

		public Result(DigesterRecipeBuilder builder, ResourceLocation advancementId) {
			id = builder.recipeId;
			group = builder.group == null ? "" : builder.group;
			ingredient = builder.recipeIngredient;
			recipeResult = builder.recipeResult;
			craftingTime = builder.craftingTimeTicks;
			craftingCost = builder.craftingCostNutrients;
			conditions = builder.conditions;

			advancementBuilder = builder.advancement;

			this.advancementId = advancementId;
		}

		public void serializeRecipeData(JsonObject json) {
			if (!group.isEmpty()) {
				json.addProperty("group", group);
			}

			json.add("ingredient", ingredient.toJson());

			json.add("result", recipeResult.toJson());

			json.addProperty("processingTime", craftingTime);
			json.addProperty("nutrientsCost", craftingCost);

			//serialize conditions
			if (!conditions.isEmpty()) {
				JsonArray array = new JsonArray();
				conditions.forEach(c -> array.add(CraftingHelper.serialize(c)));
				json.add("conditions", array);
			}
		}

		public RecipeSerializer<?> getType() {
			return ModRecipes.DIGESTING_SERIALIZER.get();
		}

		public ResourceLocation getId() {
			return id;
		}

		@Nullable
		public JsonObject serializeAdvancement() {
			if (conditions.isEmpty()) return advancementBuilder.serializeToJson();

			ConditionalAdvancement.Builder conditionalBuilder = ConditionalAdvancement.builder();
			conditions.forEach(conditionalBuilder::addCondition);
			conditionalBuilder.addAdvancement(advancementBuilder);
			return conditionalBuilder.write();
		}

		@Nullable
		public ResourceLocation getAdvancementId() {
			return advancementId;
		}
	}

}
