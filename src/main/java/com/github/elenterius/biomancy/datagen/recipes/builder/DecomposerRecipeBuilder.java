package com.github.elenterius.biomancy.datagen.recipes.builder;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.crafting.recipe.DecomposerRecipe;
import com.github.elenterius.biomancy.crafting.recipe.IngredientStack;
import com.github.elenterius.biomancy.crafting.recipe.ItemCountRange;
import com.github.elenterius.biomancy.crafting.recipe.VariableProductionOutput;
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
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.common.crafting.ConditionalAdvancement;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.crafting.conditions.ICondition;
import net.minecraftforge.common.crafting.conditions.ModLoadedCondition;
import net.minecraftforge.common.crafting.conditions.NotCondition;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Consumer;

public final class DecomposerRecipeBuilder implements RecipeBuilder {

	public static final String RECIPE_SUB_FOLDER = ModRecipes.DECOMPOSING_RECIPE_TYPE.getId().getPath();
	public static final String SUFFIX = "_decomposing";

	private final List<VariableProductionOutput> outputs = new ArrayList<>();
	private final Advancement.Builder advancement = Advancement.Builder.recipeAdvancement();
	private final List<ICondition> conditions = new ArrayList<>();
	private ResourceLocation recipeId;
	private IngredientStack ingredientStack = null;
	private int craftingTimeTicks = -1;
	private int craftingCostNutrients = -1;
	@Nullable
	private String group;

	private DecomposerRecipeBuilder() {
		this.recipeId = BiomancyMod.createRL("unknown");
	}

	public static DecomposerRecipeBuilder create() {
		return new DecomposerRecipeBuilder();
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

	private static ResourceLocation getRegistryKey(ItemLike itemLike) {
		return Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(itemLike.asItem()));
	}

	public DecomposerRecipeBuilder ifModLoaded(String modId) {
		return withCondition(new ModLoadedCondition(modId));
	}

	public DecomposerRecipeBuilder ifModMissing(String modId) {
		return withCondition(new NotCondition(new ModLoadedCondition(modId)));
	}

	public DecomposerRecipeBuilder withCondition(ICondition condition) {
		conditions.add(condition);
		return this;
	}

	public DecomposerRecipeBuilder setCraftingTime(int time) {
		if (time < 0) throw new IllegalArgumentException("Invalid crafting time: " + time);
		craftingTimeTicks = time;
		return this;
	}

	public DecomposerRecipeBuilder setCraftingCost(int costNutrients) {
		if (costNutrients < 0) throw new IllegalArgumentException("Invalid crafting cost: " + costNutrients);
		craftingCostNutrients = costNutrients;
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
		setIngredient(Ingredient.of(itemLike), quantity, BiomancyMod.createRL(getRegistryKey(itemLike).getPath() + SUFFIX));
		return this;
	}

	public DecomposerRecipeBuilder setIngredient(DatagenIngredient ingredient) {
		setIngredient(ingredient, 1, BiomancyMod.createRL(ingredient.resourceLocation.getNamespace() + "_" + ingredient.resourceLocation.getPath() + SUFFIX));
		return this;
	}

	public DecomposerRecipeBuilder setIngredient(Ingredient ingredient, int count, ResourceLocation recipeId) {
		if (ingredientStack != null) throw new IllegalStateException("Ingredient is already set");
		ingredientStack = new IngredientStack(ingredient, count);
		this.recipeId = new ResourceLocation(recipeId.getNamespace(), RECIPE_SUB_FOLDER + "/" + recipeId.getPath());
		return this;
	}

	public DecomposerRecipeBuilder addOutput(ItemLike result) {
		return addOutput(result, 1);
	}

	public DecomposerRecipeBuilder addOutput(ItemLike result, int count) {
		return addOutput(new VariableProductionOutput(result, count));
	}

	public DecomposerRecipeBuilder addOutput(ItemLike result, int min, int max) {
		return addOutput(new VariableProductionOutput(result, min, max));
	}

	public DecomposerRecipeBuilder addOutput(ItemLike result, int n, float p) {
		return addOutput(new VariableProductionOutput(result, n, p));
	}

	public DecomposerRecipeBuilder addRecyclingOutput(ItemLike result, int originalCount) {
		return addOutput(new VariableProductionOutput(result, (originalCount - 1) / 2, originalCount - 1));
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
	public void save(Consumer<FinishedRecipe> consumer, @Nullable RecipeCategory category) {
		validate();

		if (craftingTimeTicks < 0) {
			craftingTimeTicks = CraftingTimeUtil.getTotalTicks(outputs);
		}

		if (craftingCostNutrients < 0) {
			craftingCostNutrients = RecipeCostUtil.getCost(DecomposerRecipe.DEFAULT_CRAFTING_COST_NUTRIENTS, craftingTimeTicks);
		}

		advancement.parent(new ResourceLocation("recipes/root"))
				.addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(recipeId))
				.rewards(AdvancementRewards.Builder.recipe(recipeId)).requirements(RequirementsStrategy.OR);

		String folderName = RecipeBuilder.getRecipeFolderName(category, BiomancyMod.MOD_ID);
		ResourceLocation advancementId = new ResourceLocation(recipeId.getNamespace(), "recipes/%s/%s".formatted(folderName, recipeId.getPath()));

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
		private final int craftingCost;
		private final List<ICondition> conditions;
		private final Advancement.Builder advancementBuilder;
		private final ResourceLocation advancementId;

		public RecipeResult(DecomposerRecipeBuilder builder, ResourceLocation advancementId) {
			id = builder.recipeId;
			group = builder.group == null ? "" : builder.group;
			ingredientStack = builder.ingredientStack;
			craftingTime = builder.craftingTimeTicks;
			craftingCost = builder.craftingCostNutrients;
			outputs = builder.outputs;
			conditions = builder.conditions;

			advancementBuilder = builder.advancement;
			this.advancementId = advancementId;
		}

		@Override
		public void serializeRecipeData(JsonObject json) {
			if (!group.isEmpty()) {
				json.addProperty("group", group);
			}

			json.add("ingredient", ingredientStack.toJson());

			JsonArray jsonArray = new JsonArray();
			for (VariableProductionOutput output : outputs) {
				jsonArray.add(output.serialize());
			}
			json.add("results", jsonArray);

			json.addProperty("processingTime", craftingTime);
			json.addProperty("nutrientsCost", craftingCost);

			//serialize conditions
			if (!conditions.isEmpty()) {
				JsonArray array = new JsonArray();
				conditions.forEach(c -> array.add(CraftingHelper.serialize(c)));
				json.add("conditions", array);
			}
		}

		@Override
		public RecipeSerializer<?> getType() {
			return ModRecipes.DECOMPOSING_SERIALIZER.get();
		}

		@Override
		public ResourceLocation getId() {
			return id;
		}

		@Override
		@Nullable
		public JsonObject serializeAdvancement() {
			if (conditions.isEmpty()) return advancementBuilder.serializeToJson();

			ConditionalAdvancement.Builder conditionalBuilder = ConditionalAdvancement.builder();
			conditions.forEach(conditionalBuilder::addCondition);
			conditionalBuilder.addAdvancement(advancementBuilder);
			return conditionalBuilder.write();
		}

		@Override
		@Nullable
		public ResourceLocation getAdvancementId() {
			return advancementId;
		}
	}

	public static class CraftingTimeUtil {
		static final Map<Item, Float> TICK_MULTIPLIERS = new HashMap<>();
		static final float BASE_TICKS = 20;

		static {
			TICK_MULTIPLIERS.put(ModItems.FLESH_BITS.get(), 0.9f);
			TICK_MULTIPLIERS.put(ModItems.BONE_FRAGMENTS.get(), 1.25f);
			TICK_MULTIPLIERS.put(ModItems.TOUGH_FIBERS.get(), 1.5f);
			TICK_MULTIPLIERS.put(ModItems.ELASTIC_FIBERS.get(), 0.5f);
			TICK_MULTIPLIERS.put(ModItems.STONE_POWDER.get(), 1.5f);
			TICK_MULTIPLIERS.put(ModItems.MINERAL_FRAGMENT.get(), 2f);
			TICK_MULTIPLIERS.put(ModItems.GEM_FRAGMENTS.get(), 2.5f);
			TICK_MULTIPLIERS.put(ModItems.EXOTIC_DUST.get(), 2.5f);
			TICK_MULTIPLIERS.put(ModItems.BIO_LUMENS.get(), 1.1f);
			TICK_MULTIPLIERS.put(ModItems.BILE.get(), 1f);
			TICK_MULTIPLIERS.put(ModItems.TOXIN_EXTRACT.get(), 1f);
			TICK_MULTIPLIERS.put(ModItems.VOLATILE_FLUID.get(), 1.25f);
			TICK_MULTIPLIERS.put(ModItems.HORMONE_SECRETION.get(), 1f);
			TICK_MULTIPLIERS.put(ModItems.WITHERING_OOZE.get(), 1.25f);
			TICK_MULTIPLIERS.put(ModItems.REGENERATIVE_FLUID.get(), 1.75f);
		}

		private CraftingTimeUtil() {}

		public static float getTicks(Item item, int maxAmount) {
			return BASE_TICKS * TICK_MULTIPLIERS.getOrDefault(item, 1f) * Math.max(1, maxAmount);
		}

		public static int getTotalTicks(List<VariableProductionOutput> outputs) {
			float ticks = 0;
			for (VariableProductionOutput output : outputs) {
				ticks += getTicks(output.getItem(), getMaxAmount(output));
			}
			return Math.round(ticks);
		}

		static int getMaxAmount(VariableProductionOutput output) {
			ItemCountRange countRange = output.getCountRange();
			if (countRange instanceof ItemCountRange.UniformRange uniform) {
				return uniform.max();
			}
			else if (countRange instanceof ItemCountRange.ConstantValue constant) {
				return constant.value();
			}
			else if (countRange instanceof ItemCountRange.BinomialRange binomialRange) {
				return binomialRange.n();
			}
			return 1;
		}
	}

}

