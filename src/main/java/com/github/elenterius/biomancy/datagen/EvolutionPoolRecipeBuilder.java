package com.github.elenterius.biomancy.datagen;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.init.ModRecipes;
import com.google.gson.JsonArray;
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
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tags.ITag;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class EvolutionPoolRecipeBuilder {

	private final Item result;
	private final CompoundNBT resultNbt;
	private final int count;
	private final List<Ingredient> ingredients = new ArrayList<>();
	private final int craftingTime;
	private final Advancement.Builder advancementBuilder = Advancement.Builder.builder();
	private String group;

	private EvolutionPoolRecipeBuilder(IItemProvider resultIn, CompoundNBT resultNbtIn, int craftingTimeIn, int countIn) {
		assert craftingTimeIn >= 0;
		assert countIn > 0;

		result = resultIn.asItem();
		resultNbt = resultNbtIn;
		craftingTime = craftingTimeIn;
		count = countIn;
	}

	public static EvolutionPoolRecipeBuilder createRecipe(IItemProvider resultIn, int craftingTimeIn) {
		return new EvolutionPoolRecipeBuilder(resultIn, new CompoundNBT(), craftingTimeIn, 1);
	}

	public static EvolutionPoolRecipeBuilder createRecipe(IItemProvider resultIn, CompoundNBT resultNbtIn, int craftingTimeIn) {
		return new EvolutionPoolRecipeBuilder(resultIn, resultNbtIn, craftingTimeIn, 1);
	}

	public static EvolutionPoolRecipeBuilder createRecipe(IItemProvider resultIn, int craftingTimeIn, int countIn) {
		return new EvolutionPoolRecipeBuilder(resultIn, new CompoundNBT(), craftingTimeIn, countIn);
	}

	public static EvolutionPoolRecipeBuilder createRecipe(IItemProvider resultIn, CompoundNBT resultNbtIn, int craftingTimeIn, int countIn) {
		return new EvolutionPoolRecipeBuilder(resultIn, resultNbtIn, craftingTimeIn, countIn);
	}

	public EvolutionPoolRecipeBuilder addIngredient(ITag<Item> tagIn) {
		return addIngredient(Ingredient.fromTag(tagIn));
	}

	public EvolutionPoolRecipeBuilder addIngredient(IItemProvider itemIn) {
		return addIngredients(itemIn, 1);
	}

	public EvolutionPoolRecipeBuilder addIngredient(Ingredient ingredientIn) {
		return addIngredients(ingredientIn, 1);
	}

	public EvolutionPoolRecipeBuilder addIngredients(Ingredient ingredientIn, int quantity) {
		for (int i = 0; i < quantity; i++) ingredients.add(ingredientIn);
		return this;
	}

	public EvolutionPoolRecipeBuilder addIngredients(IItemProvider itemIn, int quantity) {
		for (int i = 0; i < quantity; i++) addIngredient(Ingredient.fromItems(itemIn));
		return this;
	}

	public EvolutionPoolRecipeBuilder addCriterion(String name, ICriterionInstance criterionIn) {
		advancementBuilder.withCriterion(name, criterionIn);
		return this;
	}

	public EvolutionPoolRecipeBuilder setGroup(String groupIn) {
		group = groupIn;
		return this;
	}

	public void build(Consumer<IFinishedRecipe> consumerIn) {
		ResourceLocation registryKey = Registry.ITEM.getKey(result);
		build(consumerIn, BiomancyMod.createRL(registryKey.getPath() + "_evolution_pool"));
	}

	public void build(Consumer<IFinishedRecipe> consumerIn, String id, boolean suffix) {
		if (suffix) {
			ResourceLocation registryKey = Registry.ITEM.getKey(result);
			if (registryKey.getPath().equals(id)) {
				throw new IllegalStateException(String.format("Recipe suffix %s should be different from the recipe path %s", id, registryKey.getPath()));
			}
			build(consumerIn, BiomancyMod.createRL(registryKey.getPath() + "_" + id + "_evolution_pool"));
		}
		else {
			build(consumerIn, id);
		}
	}

	public void build(Consumer<IFinishedRecipe> consumerIn, String save) {
		ResourceLocation registryKey = Registry.ITEM.getKey(result);
		ResourceLocation alternative = new ResourceLocation(BiomancyMod.MOD_ID, save + "_evolution_pool");
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
		consumerIn.accept(new Result(id, result, resultNbt, craftingTime, count, this.group == null ? "" : group, ingredients, advancementBuilder, new ResourceLocation(id.getNamespace(), "recipes/" + result.getGroup().getPath() + "/" + id.getPath())));
	}

	private void validate(ResourceLocation id) {
		if (advancementBuilder.getCriteria().isEmpty()) {
			throw new IllegalStateException("No way of obtaining recipe " + id);
		}
	}

	public static class Result implements IFinishedRecipe {
		private final ResourceLocation id;
		private final String group;
		private final List<Ingredient> ingredients;
		private final Item result;
		private final int count;
		private final CompoundNBT resultNbt;
		private final int craftingTime;
		private final Advancement.Builder advancementBuilder;
		private final ResourceLocation advancementId;

		public Result(ResourceLocation idIn, Item resultIn, CompoundNBT resultNbtIn, int craftingTimeIn, int countIn, String groupIn, List<Ingredient> ingredientsIn, Advancement.Builder advancementBuilderIn, ResourceLocation advancementIdIn) {
			id = idIn;
			group = groupIn;
			ingredients = ingredientsIn;
			result = resultIn;
			count = countIn;
			resultNbt = resultNbtIn;
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

			JsonObject jsonObject = new JsonObject();
			jsonObject.addProperty("item", Registry.ITEM.getKey(result).toString());
			if (count > 1) {
				jsonObject.addProperty("count", count);
			}
			if (!resultNbt.isEmpty()) {
				jsonObject.addProperty("nbt", resultNbt.toString());
			}
			json.add("result", jsonObject);

			json.addProperty("time", craftingTime);
		}

		public IRecipeSerializer<?> getSerializer() {
			return ModRecipes.EVOLUTION_POOL_SERIALIZER.get();
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
