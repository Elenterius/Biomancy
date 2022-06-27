package com.github.elenterius.biomancy.datagen.recipes;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.init.ModRecipes;
import com.github.elenterius.biomancy.recipe.IngredientStack;
import com.github.elenterius.biomancy.recipe.VariableProductionOutput;
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
import net.minecraftforge.registries.RegistryObject;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

public class DecomposerRecipeBuilder implements IRecipeBuilder {

	public static final String SUFFIX = "_decomposing";
	private ResourceLocation recipeId;
	private final List<VariableProductionOutput> outputs = new ArrayList<>();
	private final Advancement.Builder advancement = Advancement.Builder.advancement();
	private IngredientStack ingredientStack = null;
	private int craftingTime = 4 * 20;
	@Nullable
	private String group;

	private DecomposerRecipeBuilder(ResourceLocation recipeId) {
		this.recipeId = recipeId;
	}

//	public static DecomposerRecipeBuilder create(String modId, String ingredientName) {
//		ResourceLocation rl = new ResourceLocation(modId, ingredientName + SUFFIX);
//		return new DecomposerRecipeBuilder(rl);
//	}
//
//	public static DecomposerRecipeBuilder create(String ingredientName) {
//		ResourceLocation rl = BiomancyMod.createRL(ingredientName + SUFFIX);
//		return new DecomposerRecipeBuilder(rl);
//	}
//
//	public static DecomposerRecipeBuilder create(ResourceLocation recipeId) {
//		return new DecomposerRecipeBuilder(recipeId);
//	}

	public static DecomposerRecipeBuilder create() {
		return new DecomposerRecipeBuilder(BiomancyMod.createRL("unknown"));
	}

	private static String getName(ItemLike itemLike) {
		ResourceLocation name = itemLike.asItem().getRegistryName();
		return name != null ? name.getPath() : "unknown";
	}

	public DecomposerRecipeBuilder setCraftingTime(int time) {
		if (time < 0) throw new IllegalArgumentException("Invalid crafting time: " + time);
		craftingTime = time;
		return this;
	}

	public DecomposerRecipeBuilder setIngredient(TagKey<Item> tagKey) {
		return setIngredient(tagKey, 1);
	}

	public DecomposerRecipeBuilder setIngredient(TagKey<Item> tagKey, int quantity) {
		return setIngredient(Ingredient.of(tagKey), quantity, BiomancyMod.createRL(tagKey.location().toDebugFileName() + SUFFIX));
	}

	public DecomposerRecipeBuilder setIngredient(RegistryObject<? extends Item> itemHolder) {
		return setIngredient(itemHolder.get(), 1);
	}

	public DecomposerRecipeBuilder setIngredient(ItemLike itemIn) {
		return setIngredient(itemIn, 1);
	}

	public DecomposerRecipeBuilder setIngredient(Ingredient ingredient, ResourceLocation recipeId) {
		return setIngredient(ingredient, 1, recipeId);
	}

	public DecomposerRecipeBuilder setIngredient(ItemLike itemLike, int quantity) {
		setIngredient(Ingredient.of(itemLike), quantity, BiomancyMod.createRL(getName(itemLike) + SUFFIX));
		return this;
	}

	public DecomposerRecipeBuilder setIngredient(Ingredient ingredient, int count, ResourceLocation recipeId) {
		if (ingredientStack != null) throw new IllegalStateException("Ingredient is already set");
		ingredientStack = new IngredientStack(ingredient, count);
		this.recipeId = recipeId;
		return this;
	}

	public DecomposerRecipeBuilder addOutput(ItemLike resultIn) {
		return addOutput(resultIn, 1);
	}

	public DecomposerRecipeBuilder addOutput(ItemLike resultIn, int count) {
		return addOutput(new VariableProductionOutput(resultIn, count));
	}

	public DecomposerRecipeBuilder addOutput(ItemLike resultIn, int min, int max) {
		return addOutput(new VariableProductionOutput(resultIn, min, max));
	}

	public DecomposerRecipeBuilder addOutput(VariableProductionOutput output) {
		outputs.add(output);
		return this;
	}

	public DecomposerRecipeBuilder addOutputs(VariableProductionOutput... outputsIn) {
		outputs.addAll(Arrays.asList(outputsIn));
		return this;
	}

	@Override
	public DecomposerRecipeBuilder unlockedBy(String name, CriterionTriggerInstance criterionTrigger) {
		advancement.addCriterion(name, criterionTrigger);
		return this;
	}

	public DecomposerRecipeBuilder setGroup(@Nullable String name) {
		group = name;
		return this;
	}

	@Override
	public void save(Consumer<FinishedRecipe> consumer, @Nullable CreativeModeTab itemCategory) {
		validate();

		advancement.parent(new ResourceLocation("recipes/root"))
				.addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(recipeId))
				.rewards(AdvancementRewards.Builder.recipe(recipeId)).requirements(RequirementsStrategy.OR);

		String folder = itemCategory != null ? itemCategory.getRecipeFolderName() : BiomancyMod.MOD_ID;
		ResourceLocation advancementId = new ResourceLocation(recipeId.getNamespace(), "recipes/%s/%s".formatted(folder, recipeId.getPath()));

		consumer.accept(new RecipeResult(this, advancementId));
	}

	private void validate() {
		if (recipeId.getPath().equals("unknown")) throw new IllegalStateException("Invalid recipe id: " + recipeId);
		if (ingredientStack == null) throw new IllegalStateException("No Ingredient was provided.");
		if (advancement.getCriteria().isEmpty()) {
			throw new IllegalStateException("No way of obtaining recipe %s because Criteria are empty.".formatted(recipeId));
		}
	}

	public static class RecipeResult implements FinishedRecipe {

		private final ResourceLocation id;
		private final String group;
		private final IngredientStack ingredientStack;
		private final List<VariableProductionOutput> outputs;
		private final int craftingTime;
		private final Advancement.Builder advancementBuilder;
		private final ResourceLocation advancementId;

		public RecipeResult(DecomposerRecipeBuilder builder, ResourceLocation advancementId) {
			id = builder.recipeId;
			group = builder.group == null ? "" : builder.group;
			ingredientStack = builder.ingredientStack;
			craftingTime = builder.craftingTime;
			outputs = builder.outputs;
			advancementBuilder = builder.advancement;
			this.advancementId = advancementId;
		}

		public void serializeRecipeData(JsonObject json) {
			if (!group.isEmpty()) {
				json.addProperty("group", group);
			}

			json.add("input", ingredientStack.toJson());

			JsonArray jsonArray = new JsonArray();
			for (VariableProductionOutput output : outputs) {
				jsonArray.add(output.serialize());
			}
			json.add("outputs", jsonArray);

			json.addProperty("time", craftingTime);
		}

		public RecipeSerializer<?> getType() {
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

