package com.github.elenterius.biomancy.datagen.recipes;

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
import net.minecraft.item.ItemStack;
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
	private final NbtString resultNbt;
	private final int count;
	private final List<Ingredient> ingredients = new ArrayList<>();
	private final int craftingTime;
	private final Advancement.Builder advancementBuilder = Advancement.Builder.advancement();
	private String group;

	private EvolutionPoolRecipeBuilder(IItemProvider resultIn, NbtString resultNbtIn, int craftingTimeIn, int countIn) {
		assert craftingTimeIn >= 0;
		assert countIn > 0;

		result = resultIn.asItem();
		resultNbt = resultNbtIn;
		craftingTime = craftingTimeIn;
		count = countIn;
	}

	public static EvolutionPoolRecipeBuilder createRecipe(IItemProvider resultIn, int craftingTimeIn) {
		return new EvolutionPoolRecipeBuilder(resultIn, NbtString.EMPTY, craftingTimeIn, 1);
	}

	public static EvolutionPoolRecipeBuilder createRecipe(ItemStack stack, int craftingTimeIn) {
		return createRecipe(stack.getItem(), stack.getOrCreateTag(), craftingTimeIn, 1);
	}

	public static EvolutionPoolRecipeBuilder createRecipe(IItemProvider resultIn, CompoundNBT itemNbtTag, int craftingTimeIn) {
		return new EvolutionPoolRecipeBuilder(resultIn, new NbtString(itemNbtTag), craftingTimeIn, 1);
	}

	public static EvolutionPoolRecipeBuilder createRecipe(IItemProvider resultIn, int craftingTimeIn, int countIn) {
		return new EvolutionPoolRecipeBuilder(resultIn, NbtString.EMPTY, craftingTimeIn, countIn);
	}

	public static EvolutionPoolRecipeBuilder createRecipe(ItemStack stack, int craftingTimeIn, int countIn) {
		return createRecipe(stack.getItem(), stack.getOrCreateTag(), craftingTimeIn, countIn);
	}

	public static EvolutionPoolRecipeBuilder createRecipe(IItemProvider resultIn, CompoundNBT itemNbtTag, int craftingTimeIn, int countIn) {
		return new EvolutionPoolRecipeBuilder(resultIn, new NbtString(itemNbtTag), craftingTimeIn, countIn);
	}

	public EvolutionPoolRecipeBuilder addIngredient(ITag<Item> tagIn) {
		return addIngredient(Ingredient.of(tagIn));
	}

	public EvolutionPoolRecipeBuilder addIngredients(ITag<Item> tagIn, int quantity) {
		return addIngredients(Ingredient.of(tagIn), quantity);
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
		for (int i = 0; i < quantity; i++) addIngredient(Ingredient.of(itemIn));
		return this;
	}

	public EvolutionPoolRecipeBuilder addCriterion(String name, ICriterionInstance criterionIn) {
		advancementBuilder.addCriterion(name, criterionIn);
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
		advancementBuilder.parent(new ResourceLocation("recipes/root")).addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(id)).rewards(AdvancementRewards.Builder.recipe(id)).requirements(IRequirementsStrategy.OR);
		consumerIn.accept(new Result(id, result, resultNbt, craftingTime, count, group == null ? "" : group, ingredients, advancementBuilder, new ResourceLocation(id.getNamespace(), "recipes/" + (result.getItemCategory() != null ? result.getItemCategory().getRecipeFolderName() : BiomancyMod.MOD_ID) + "/" + id.getPath())));
	}

	private void validate(ResourceLocation id) {
		if (advancementBuilder.getCriteria().isEmpty()) {
			throw new IllegalStateException("No way of obtaining recipe " + id + " because Criteria are empty.");
		}
	}

	public static class NbtString {
		public static NbtString EMPTY = new NbtString();

		final String nbtStr;

		private NbtString() {nbtStr = null;}

		public NbtString(CompoundNBT nbt) {
			if (!nbt.isEmpty()) nbtStr = nbt.toString();
			else nbtStr = null;
		}

		String getString() {
			return nbtStr;
		}

		boolean isEmpty() {
			return nbtStr == null;
		}
	}

	public static class Result implements IFinishedRecipe {
		private final ResourceLocation id;
		private final String group;
		private final List<Ingredient> ingredients;
		private final Item result;
		private final int count;
		private final NbtString resultNbt;
		private final int craftingTime;
		private final Advancement.Builder advancementBuilder;
		private final ResourceLocation advancementId;

		public Result(ResourceLocation idIn, Item resultIn, NbtString resultNbtIn, int craftingTimeIn, int countIn, String groupIn, List<Ingredient> ingredientsIn, Advancement.Builder advancementBuilderIn, ResourceLocation advancementIdIn) {
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
				jsonObject.addProperty("nbt", resultNbt.getString());
			}
			json.add("result", jsonObject);

			json.addProperty("time", craftingTime);
		}

		public IRecipeSerializer<?> getType() {
			return ModRecipes.EVOLUTION_POOL_SERIALIZER.get();
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
