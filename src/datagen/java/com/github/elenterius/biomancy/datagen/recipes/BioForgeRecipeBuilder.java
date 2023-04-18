package com.github.elenterius.biomancy.datagen.recipes;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.init.ModBioForgeTabs;
import com.github.elenterius.biomancy.init.ModRecipes;
import com.github.elenterius.biomancy.inventory.menu.BioForgeTab;
import com.github.elenterius.biomancy.recipe.IngredientStack;
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
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.crafting.conditions.ICondition;
import net.minecraftforge.common.crafting.conditions.ModLoadedCondition;
import net.minecraftforge.common.crafting.conditions.NotCondition;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class BioForgeRecipeBuilder implements IRecipeBuilder {

	public static final String SUFFIX = "_from_bio_forging";

	private final ResourceLocation recipeId;

	private final List<ICondition> conditions = new ArrayList<>();
	private final ItemData result;
	private final List<IngredientStack> ingredients = new ArrayList<>();
	private final Advancement.Builder advancement = Advancement.Builder.advancement();
	private BioForgeTab category = ModBioForgeTabs.MISC.get();

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
		ResourceLocation rl = BiomancyMod.createRL(Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(item.asItem())).getPath() + SUFFIX);
		return new BioForgeRecipeBuilder(rl, itemData);
	}

	public static BioForgeRecipeBuilder create(ItemLike item, int count) {
		ItemData itemData = new ItemData(item, count);
		ResourceLocation rl = BiomancyMod.createRL(Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(item.asItem())).getPath() + SUFFIX);
		return new BioForgeRecipeBuilder(rl, itemData);
	}

	//	public BioForgeRecipeBuilder setCraftingTime(int time) {
	//		if (time < 0) throw new IllegalArgumentException("Invalid crafting time: " + time);
	//		craftingTime = time;
	//		return this;
	//	}

	public BioForgeRecipeBuilder ifModLoaded(String modId) {
		return withCondition(new ModLoadedCondition(modId));
	}

	public BioForgeRecipeBuilder ifModMissing(String modId) {
		return withCondition(new NotCondition(new ModLoadedCondition(modId)));
	}

	public BioForgeRecipeBuilder withCondition(ICondition condition) {
		conditions.add(condition);
		return this;
	}

	public BioForgeRecipeBuilder setCategory(BioForgeTab category) {
		this.category = category;
		return this;
	}

	public BioForgeRecipeBuilder setCategory(Supplier<BioForgeTab> category) {
		this.category = category.get();
		return this;
	}

	public BioForgeRecipeBuilder addIngredient(TagKey<Item> tag) {
		return addIngredient(Ingredient.of(tag));
	}

	public BioForgeRecipeBuilder addIngredient(TagKey<Item> tag, int quantity) {
		return addIngredient(Ingredient.of(tag), quantity);
	}

	public BioForgeRecipeBuilder addIngredient(ItemLike item) {
		return addIngredient(item, 1);
	}

	public BioForgeRecipeBuilder addIngredient(Ingredient ingredient) {
		return addIngredient(ingredient, 1);
	}

	public BioForgeRecipeBuilder addIngredient(ItemLike item, int quantity) {
		addIngredient(Ingredient.of(item), quantity);
		return this;
	}

	public BioForgeRecipeBuilder addIngredient(Ingredient ingredient, int quantity) {
		ingredients.add(new IngredientStack(ingredient, quantity));
		return this;
	}

	@Override
	public BioForgeRecipeBuilder unlockedBy(String name, CriterionTriggerInstance criterionTrigger) {
		advancement.addCriterion(name, criterionTrigger);
		return this;
	}

	@Override
	public void save(Consumer<FinishedRecipe> consumer, @Nullable CreativeModeTab itemCategory) {
		validateCriteria();
		advancement.parent(new ResourceLocation("recipes/root"))
				.addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(recipeId))
				.rewards(AdvancementRewards.Builder.recipe(recipeId)).requirements(RequirementsStrategy.OR);
		ResourceLocation advancementId = new ResourceLocation(recipeId.getNamespace(),
				"recipes/" + (itemCategory != null ? itemCategory.getRecipeFolderName() : BiomancyMod.MOD_ID) + "/" + recipeId.getPath());
		consumer.accept(new RecipeResult(this, advancementId));
	}

	private void validateCriteria() {
		if (advancement.getCriteria().isEmpty()) {
			throw new IllegalStateException("No way of obtaining recipe %s because Criteria are empty.".formatted(recipeId));
		}
	}

	public static class RecipeResult implements FinishedRecipe {

		private final ResourceLocation id;
		private final List<IngredientStack> ingredients;
		private final ItemData result;
		private final BioForgeTab category;
		private final List<ICondition> conditions;
		private final Advancement.Builder advancementBuilder;
		private final ResourceLocation advancementId;

		public RecipeResult(BioForgeRecipeBuilder builder, ResourceLocation advancementId) {
			id = builder.recipeId;
			category = builder.category;
			result = builder.result;
			ingredients = builder.ingredients;
			conditions = builder.conditions;

			advancementBuilder = builder.advancement;
			this.advancementId = advancementId;
		}

		@Override
		public void serializeRecipeData(JsonObject json) {
			JsonArray jsonArray = new JsonArray();
			for (IngredientStack ingredient : ingredients) {
				jsonArray.add(ingredient.toJson());
			}
			json.add("ingredient_quantities", jsonArray);

			json.add("result", result.toJson());

			category.toJson(json);

			//serialize conditions
			if (!conditions.isEmpty()) {
				JsonArray array = new JsonArray();
				conditions.forEach(c -> array.add(CraftingHelper.serialize(c)));
				json.add("conditions", array);
			}
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

