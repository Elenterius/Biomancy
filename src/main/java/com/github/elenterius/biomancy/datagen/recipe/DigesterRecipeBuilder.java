package com.github.elenterius.biomancy.datagen.recipe;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.init.ModRecipes;
import com.github.elenterius.biomancy.recipe.Byproduct;
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
import java.util.function.Consumer;

public class DigesterRecipeBuilder {

	private final Item result;
	private final int count;
	private Ingredient ingredient = null;
	private Byproduct byproduct = null;
	private final int craftingTime;
	private final Advancement.Builder advancementBuilder = Advancement.Builder.builder();
	private String group;

	private DigesterRecipeBuilder(IItemProvider resultIn, int craftingTimeIn, int countIn) {
		assert craftingTimeIn >= 0;
		assert countIn > 0;

		result = resultIn.asItem();
		craftingTime = craftingTimeIn;
		count = countIn;
	}

	public static DigesterRecipeBuilder createRecipe(IItemProvider resultIn, int craftingTimeIn) {
		return new DigesterRecipeBuilder(resultIn, craftingTimeIn, 1);
	}

	public static DigesterRecipeBuilder createRecipe(IItemProvider resultIn, int craftingTimeIn, int countIn) {
		return new DigesterRecipeBuilder(resultIn, craftingTimeIn, countIn);
	}

	public DigesterRecipeBuilder setIngredient(ITag<Item> tagIn) {
		return setIngredient(Ingredient.fromTag(tagIn));
	}

	public DigesterRecipeBuilder setIngredient(IItemProvider itemIn) {
		return setIngredient(Ingredient.fromItems(itemIn));
	}

	public DigesterRecipeBuilder setIngredient(Ingredient ingredientIn) {
		ingredient = ingredientIn;
		return this;
	}

	public DigesterRecipeBuilder setByproduct(IItemProvider resultIn) {
		return setByproduct(resultIn, 1, 1f);
	}

	public DigesterRecipeBuilder setByproduct(IItemProvider resultIn, float chance) {
		return setByproduct(resultIn, 1, chance);
	}

	public DigesterRecipeBuilder setByproduct(IItemProvider resultIn, int itemCount, float chance) {
		return setByproduct(new Byproduct(resultIn, itemCount, chance));
	}

	public DigesterRecipeBuilder setByproduct(Byproduct byproductIn) {
		byproduct = byproductIn;
		return this;
	}

	public DigesterRecipeBuilder addCriterion(String name, ICriterionInstance criterionIn) {
		advancementBuilder.withCriterion(name, criterionIn);
		return this;
	}

	public DigesterRecipeBuilder setGroup(String groupIn) {
		group = groupIn;
		return this;
	}

	public void build(Consumer<IFinishedRecipe> consumerIn) {
		ResourceLocation registryKey = Registry.ITEM.getKey(result);
		build(consumerIn, BiomancyMod.createRL(registryKey.getPath() + "_digesting"));
	}

	public void build(Consumer<IFinishedRecipe> consumerIn, String id, boolean suffix) {
		if (suffix) {
			ResourceLocation registryKey = Registry.ITEM.getKey(result);
			if (registryKey.getPath().equals(id)) {
				throw new IllegalStateException(String.format("Recipe suffix %s should be different from the recipe path %s", id, registryKey.getPath()));
			}
			build(consumerIn, BiomancyMod.createRL(registryKey.getPath() + "_" + id + "_digesting"));
		}
		else {
			build(consumerIn, id);
		}
	}

	public void build(Consumer<IFinishedRecipe> consumerIn, String save) {
		ResourceLocation registryKey = Registry.ITEM.getKey(result);
		ResourceLocation alternative = new ResourceLocation(BiomancyMod.MOD_ID, save + "_digesting");
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
		consumerIn.accept(new Result(id, result, craftingTime, count, byproduct, this.group == null ? "" : group, ingredient, advancementBuilder, new ResourceLocation(id.getNamespace(), "recipes/" + (result.getGroup() != null ? result.getGroup().getPath() : BiomancyMod.MOD_ID) + "/" + id.getPath())));
	}

	private void validate(ResourceLocation id) {
		if (advancementBuilder.getCriteria().isEmpty()) {
			throw new IllegalStateException("No way of obtaining recipe " + id);
		}
	}

	public static class Result implements IFinishedRecipe {
		private final ResourceLocation id;
		private final String group;
		private final Ingredient ingredient;
		private final Byproduct byproduct;
		private final Item result;
		private final int count;
		private final int craftingTime;
		private final Advancement.Builder advancementBuilder;
		private final ResourceLocation advancementId;

		public Result(ResourceLocation idIn, Item resultIn, int craftingTimeIn, int countIn, Byproduct byproductIn, String groupIn, Ingredient ingredientIn, Advancement.Builder advancementBuilderIn, ResourceLocation advancementIdIn) {
			id = idIn;
			group = groupIn;
			ingredient = ingredientIn;
			byproduct = byproductIn;
			result = resultIn;
			count = countIn;
			craftingTime = craftingTimeIn;
			advancementBuilder = advancementBuilderIn;
			advancementId = advancementIdIn;
		}

		public void serialize(JsonObject json) {
			if (!this.group.isEmpty()) {
				json.addProperty("group", this.group);
			}

			json.add("ingredient", ingredient.serialize());

			JsonObject jsonObject = new JsonObject();
			jsonObject.addProperty("item", Registry.ITEM.getKey(result).toString());
			if (count > 1) {
				jsonObject.addProperty("count", count);
			}
			json.add("result", jsonObject);

			json.addProperty("time", craftingTime);

			json.add("byproduct", byproduct.serialize());
		}

		public IRecipeSerializer<?> getSerializer() {
			return ModRecipes.DIGESTER_SERIALIZER.get();
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

