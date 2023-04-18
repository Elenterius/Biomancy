package com.github.elenterius.biomancy.datagen.recipes;

import com.github.elenterius.biomancy.recipe.ItemStackIngredient;
import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.ICriterionInstance;
import net.minecraft.advancements.IRequirementsStrategy;
import net.minecraft.advancements.criterion.RecipeUnlockedTrigger;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tags.ITag;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Consumer;

public class ShapelessNbtRecipeBuilder {

	private final Item result;
	private final CompoundNBT resultNbt;
	private final int count;
	private final List<Ingredient> ingredients = Lists.newArrayList();
	private final Advancement.Builder advancementBuilder = Advancement.Builder.advancement();
	private String group;

	public ShapelessNbtRecipeBuilder(IItemProvider resultIn, CompoundNBT resultNbtIn, int countIn) {
		result = resultIn.asItem();
		resultNbt = resultNbtIn;
		count = countIn;
	}

	public static ShapelessNbtRecipeBuilder shapelessRecipe(ItemStack resultIn) {
		return new ShapelessNbtRecipeBuilder(resultIn.getItem(), resultIn.getOrCreateTag().copy(), resultIn.getCount());
	}

	public static ShapelessNbtRecipeBuilder shapelessRecipe(ItemStack resultIn, int countIn) {
		return new ShapelessNbtRecipeBuilder(resultIn.getItem(), resultIn.getOrCreateTag().copy(), countIn);
	}

	public static ShapelessNbtRecipeBuilder shapelessRecipe(IItemProvider resultIn) {
		return new ShapelessNbtRecipeBuilder(resultIn, new CompoundNBT(), 1);
	}

	public static ShapelessNbtRecipeBuilder shapelessRecipe(IItemProvider resultIn, int countIn) {
		return new ShapelessNbtRecipeBuilder(resultIn, new CompoundNBT(), countIn);
	}

	public ShapelessNbtRecipeBuilder addIngredient(ITag<Item> tagIn) {
		return addIngredient(Ingredient.of(tagIn));
	}

	public ShapelessNbtRecipeBuilder addIngredient(IItemProvider itemIn) {
		return addIngredient(itemIn, 1);
	}

	public ShapelessNbtRecipeBuilder addIngredient(IItemProvider itemIn, int quantity) {
		for (int i = 0; i < quantity; ++i) {
			addIngredient(Ingredient.of(itemIn));
		}
		return this;
	}

	public ShapelessNbtRecipeBuilder addIngredient(ItemStack ingredientIn) {
		return addIngredient(new ItemStackIngredient(ingredientIn), ingredientIn.getCount());
	}

	public ShapelessNbtRecipeBuilder addIngredient(ItemStack ingredientIn, int countIn) {
		return addIngredient(new ItemStackIngredient(ingredientIn), countIn);
	}

	public ShapelessNbtRecipeBuilder addIngredient(Ingredient ingredientIn) {
		return addIngredient(ingredientIn, 1);
	}

	public ShapelessNbtRecipeBuilder addIngredient(Ingredient ingredientIn, int quantity) {
		for (int i = 0; i < quantity; ++i) {
			ingredients.add(ingredientIn);
		}
		return this;
	}

	public ShapelessNbtRecipeBuilder addCriterion(String name, ICriterionInstance criterionIn) {
		advancementBuilder.addCriterion(name, criterionIn);
		return this;
	}

	public ShapelessNbtRecipeBuilder setGroup(String groupIn) {
		group = groupIn;
		return this;
	}

	public void build(Consumer<IFinishedRecipe> consumerIn) {
		build(consumerIn, Registry.ITEM.getKey(result));
	}

	public void build(Consumer<IFinishedRecipe> consumerIn, String save) {
		ResourceLocation resourcelocation = Registry.ITEM.getKey(result);
		if ((new ResourceLocation(save)).equals(resourcelocation)) {
			throw new IllegalStateException("Shapeless Recipe " + save + " should remove its 'save' argument");
		}
		else {
			build(consumerIn, new ResourceLocation(save));
		}
	}

	public void build(Consumer<IFinishedRecipe> consumerIn, ResourceLocation id) {
		validate(id);
		advancementBuilder.parent(new ResourceLocation("recipes/root")).addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(id)).rewards(AdvancementRewards.Builder.recipe(id)).requirements(IRequirementsStrategy.OR);
		consumerIn.accept(new ShapelessNbtRecipeBuilder.Result(id, result, resultNbt, count, group == null ? "" : group, ingredients, advancementBuilder, new ResourceLocation(id.getNamespace(), "recipes/" + result.getItemCategory().getRecipeFolderName() + "/" + id.getPath())));
	}

	private void validate(ResourceLocation id) {
		if (advancementBuilder.getCriteria().isEmpty()) {
			throw new IllegalStateException("No way of obtaining recipe " + id + " because Criteria are empty.");
		}
	}

	public static class Result implements IFinishedRecipe {
		private final ResourceLocation id;
		private final Item result;
		private final CompoundNBT resultNbt;
		private final int count;
		private final String group;
		private final List<Ingredient> ingredients;
		private final Advancement.Builder advancementBuilder;
		private final ResourceLocation advancementId;

		public Result(ResourceLocation idIn, Item resultIn, CompoundNBT resultNbtIn, int countIn, String groupIn, List<Ingredient> ingredientsIn, Advancement.Builder advancementBuilderIn, ResourceLocation advancementIdIn) {
			id = idIn;
			result = resultIn;
			resultNbt = resultNbtIn;
			count = countIn;
			group = groupIn;
			ingredients = ingredientsIn;
			advancementBuilder = advancementBuilderIn;
			advancementId = advancementIdIn;
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

			JsonObject jsonObject = new JsonObject();
			jsonObject.addProperty("item", Registry.ITEM.getKey(result).toString());
			if (count > 1) {
				jsonObject.addProperty("count", count);
			}
			if (!resultNbt.isEmpty()) {
				jsonObject.addProperty("nbt", resultNbt.toString());
			}
			json.add("result", jsonObject);
		}

		public IRecipeSerializer<?> getType() {
			return IRecipeSerializer.SHAPELESS_RECIPE;
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