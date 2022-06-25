package com.github.elenterius.biomancy.datagen.recipes;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.init.ModItems;
import com.github.elenterius.biomancy.init.ModRecipes;
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
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.common.crafting.NBTIngredient;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

public class BioLabRecipeBuilder implements IRecipeBuilder {

	public static final String SUFFIX = "_from_bio_brewing";

	private final ResourceLocation recipeId;
	private final ItemData result;
	private final List<IngredientQuantity> ingredients = new ArrayList<>();
	private Ingredient reactant = Ingredient.of(ModItems.GLASS_VIAL.get());
	private int craftingTime = 4 * 20;

	private final Advancement.Builder advancement = Advancement.Builder.advancement();

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

	public BioLabRecipeBuilder addIngredient(TagKey<Item> tag, int quantity) {
		return addIngredient(Ingredient.of(tag), quantity);
	}

	public BioLabRecipeBuilder addIngredient(ItemLike item) {
		return addIngredient(item, 1);
	}

	public BioLabRecipeBuilder addIngredient(ItemStack stack) {
		return addIngredient(NBTIngredient.of(stack), 1);
	}

	public BioLabRecipeBuilder addIngredient(Ingredient ingredient) {
		return addIngredient(ingredient, 1);
	}

	public BioLabRecipeBuilder addIngredient(ItemLike item, int quantity) {
		addIngredient(Ingredient.of(item), quantity);
		return this;
	}

	public BioLabRecipeBuilder addIngredient(Ingredient ingredient, int quantity) {
		ingredients.add(new IngredientQuantity(ingredient, quantity));
		return this;
	}

	@Override
	public BioLabRecipeBuilder unlockedBy(String name, CriterionTriggerInstance criterionTrigger) {
		advancement.addCriterion(name, criterionTrigger);
		return this;
	}

	@Override
	public void save(Consumer<FinishedRecipe> consumer, @Nullable CreativeModeTab itemCategory) {
		validateCriteria();
		advancement.parent(new ResourceLocation("recipes/root"))
				.addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(recipeId))
				.rewards(AdvancementRewards.Builder.recipe(recipeId)).requirements(RequirementsStrategy.OR);
		ResourceLocation advancementId = new ResourceLocation(recipeId.getNamespace(), "recipes/%s/%s".formatted(getRecipeFolderName(itemCategory), recipeId.getPath()));
		consumer.accept(new RecipeResult(this, advancement, advancementId));
	}

	private String getRecipeFolderName(@Nullable CreativeModeTab itemCategory) {
		return itemCategory != null ? itemCategory.getRecipeFolderName() : BiomancyMod.MOD_ID;
	}

	private void validateCriteria() {
		if (advancement.getCriteria().isEmpty()) {
			throw new IllegalStateException("No way of obtaining recipe %s because Criteria are empty.".formatted(recipeId));
		}
	}

	public static class RecipeResult implements FinishedRecipe {
		private final ResourceLocation id;

		private final List<IngredientQuantity> ingredients;
		private final Ingredient reactant;
		private final ItemData result;
		private final int craftingTime;

		private final Advancement.Builder advancementBuilder;
		private final ResourceLocation advancementId;

		public RecipeResult(BioLabRecipeBuilder builder, Advancement.Builder advancement, ResourceLocation advancementId) {
			id = builder.recipeId;
			ingredients = builder.ingredients;
			reactant = builder.reactant;
			result = builder.result;
			craftingTime = builder.craftingTime;

			advancementBuilder = advancement;
			this.advancementId = advancementId;
		}

		public void serializeRecipeData(JsonObject json) {
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
