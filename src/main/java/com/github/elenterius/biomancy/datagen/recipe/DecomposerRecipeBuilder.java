package com.github.elenterius.biomancy.datagen.recipe;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.init.ModRecipes;
import com.github.elenterius.biomancy.recipe.Byproduct;
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
import net.minecraft.tags.ITag;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class DecomposerRecipeBuilder {

	private final Item result;
	private final int count;
	private Ingredient ingredient = null;
	private int ingredientCount = 0;
	private final List<Byproduct> byproducts = new ArrayList<>();
	private final int craftingTime;
	private final Advancement.Builder advancementBuilder = Advancement.Builder.advancement();
	private String group;

	private DecomposerRecipeBuilder(IItemProvider resultIn, int craftingTimeIn, int countIn) {
		assert craftingTimeIn >= 0;
		assert countIn > 0;

		result = resultIn.asItem();
		craftingTime = craftingTimeIn;
		count = countIn;
	}

	public static DecomposerRecipeBuilder createRecipe(IItemProvider resultIn, int craftingTimeIn) {
		return new DecomposerRecipeBuilder(resultIn, craftingTimeIn, 1);
	}

	public static DecomposerRecipeBuilder createRecipe(IItemProvider resultIn, int craftingTimeIn, int countIn) {
		return new DecomposerRecipeBuilder(resultIn, craftingTimeIn, countIn);
	}

	public DecomposerRecipeBuilder setIngredient(ITag<Item> tagIn) {
		return setIngredient(Ingredient.of(tagIn));
	}

	public DecomposerRecipeBuilder setIngredient(ITag<Item> tagIn, int quantity) {
		return setIngredient(Ingredient.of(tagIn), quantity);
	}

	public DecomposerRecipeBuilder setIngredient(IItemProvider itemIn) {
		return setIngredient(itemIn, 1);
	}

	public DecomposerRecipeBuilder setIngredient(Ingredient ingredientIn) {
		return setIngredient(ingredientIn, 1);
	}

	public DecomposerRecipeBuilder setIngredient(IItemProvider itemIn, int quantity) {
		setIngredient(Ingredient.of(itemIn), quantity);
		return this;
	}

	public DecomposerRecipeBuilder setIngredient(Ingredient ingredientIn, int quantity) {
		ingredient = ingredientIn;
		ingredientCount = quantity;
		return this;
	}

	public DecomposerRecipeBuilder addByproduct(IItemProvider resultIn) {
		return addByproduct(resultIn, 1, 1f);
	}

	public DecomposerRecipeBuilder addByproduct(IItemProvider resultIn, float chance) {
		return addByproduct(resultIn, 1, chance);
	}

	public DecomposerRecipeBuilder addByproduct(IItemProvider resultIn, int itemCount, float chance) {
		return addByproduct(new Byproduct(resultIn, itemCount, chance));
	}

	public DecomposerRecipeBuilder addByproduct(Byproduct byproduct) {
		byproducts.add(byproduct);
		return this;
	}

	public DecomposerRecipeBuilder addByproducts(Byproduct byproduct, int quantity) {
		for (int i = 0; i < quantity; i++) byproducts.add(byproduct);
		return this;
	}

	public DecomposerRecipeBuilder addCriterion(String name, ICriterionInstance criterionIn) {
		advancementBuilder.addCriterion(name, criterionIn);
		return this;
	}

	public DecomposerRecipeBuilder setGroup(String groupIn) {
		group = groupIn;
		return this;
	}

	public void build(Consumer<IFinishedRecipe> consumerIn) {
		ResourceLocation registryKey = Registry.ITEM.getKey(result);
		build(consumerIn, BiomancyMod.createRL(registryKey.getPath() + "_decomposing"));
	}

	public void build(Consumer<IFinishedRecipe> consumerIn, String id, boolean suffix) {
		if (suffix) {
			ResourceLocation registryKey = Registry.ITEM.getKey(result);
			if (registryKey.getPath().equals(id)) {
				throw new IllegalStateException(String.format("Recipe suffix %s should be different from the recipe path %s", id, registryKey.getPath()));
			}
			build(consumerIn, BiomancyMod.createRL(registryKey.getPath() + "_" + id + "_decomposing"));
		}
		else {
			build(consumerIn, id);
		}
	}

	public void build(Consumer<IFinishedRecipe> consumerIn, String save) {
		ResourceLocation registryKey = Registry.ITEM.getKey(result);
		ResourceLocation alternative = new ResourceLocation(BiomancyMod.MOD_ID, save + "_decomposing");
		if (alternative.equals(registryKey)) {
			throw new IllegalStateException("Recipe " + alternative + " should remove its 'save' argument");
		}
		else {
			build(consumerIn, alternative);
		}
	}

	private void build(Consumer<IFinishedRecipe> consumerIn, ResourceLocation id) {
		validate(id);
		advancementBuilder.parent(new ResourceLocation("recipes/root")).addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(id)).rewards(AdvancementRewards.Builder.recipe(id)).requirements(IRequirementsStrategy.OR);
		consumerIn.accept(new Result(id, result, craftingTime, count, this.group == null ? "" : group, ingredient, ingredientCount, byproducts, advancementBuilder, new ResourceLocation(id.getNamespace(), "recipes/" + (result.getItemCategory() != null ? result.getItemCategory().getRecipeFolderName() : BiomancyMod.MOD_ID) + "/" + id.getPath())));
	}

	private void validate(ResourceLocation id) {
		if (advancementBuilder.getCriteria().isEmpty()) {
			throw new IllegalStateException("No way of obtaining recipe " + id + " because Criteria are empty.");
		}
	}

	public static class Result implements IFinishedRecipe {
		private final ResourceLocation id;
		private final String group;
		private final Ingredient ingredient;
		private final int ingredientCount;
		private final List<Byproduct> byproducts;
		private final Item result;
		private final int count;
		private final int craftingTime;
		private final Advancement.Builder advancementBuilder;
		private final ResourceLocation advancementId;

		public Result(ResourceLocation idIn, Item resultIn, int craftingTimeIn, int countIn, String groupIn, Ingredient ingredientIn, int ingredientCountIn, List<Byproduct> byproductsIn, Advancement.Builder advancementBuilderIn, ResourceLocation advancementIdIn) {
			id = idIn;
			group = groupIn;
			ingredient = ingredientIn;
			ingredientCount = ingredientCountIn;
			byproducts = byproductsIn;
			result = resultIn;
			count = countIn;
			craftingTime = craftingTimeIn;
			advancementBuilder = advancementBuilderIn;
			advancementId = advancementIdIn;
		}

		public void serializeRecipeData(JsonObject json) {
			if (!this.group.isEmpty()) {
				json.addProperty("group", this.group);
			}

			JsonObject input = new JsonObject();
			input.add("ingredient", ingredient.toJson());
			if (ingredientCount > 0) input.addProperty("count", ingredientCount);
			json.add("input", input);

			JsonArray jsonArray = new JsonArray();
			for (Byproduct byproduct : byproducts) {
				jsonArray.add(byproduct.serialize());
			}
			json.add("byproducts", jsonArray);

			JsonObject jsonobject = new JsonObject();
			jsonobject.addProperty("item", Registry.ITEM.getKey(result).toString());
			if (count > 1) jsonobject.addProperty("count", count);
			json.add("result", jsonobject);

			json.addProperty("time", craftingTime);
		}

		public IRecipeSerializer<?> getType() {
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

