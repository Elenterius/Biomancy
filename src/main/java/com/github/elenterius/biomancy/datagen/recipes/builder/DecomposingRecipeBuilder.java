package com.github.elenterius.biomancy.datagen.recipes.builder;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.crafting.IngredientStack;
import com.github.elenterius.biomancy.crafting.ItemCountRange;
import com.github.elenterius.biomancy.crafting.VariableOutput;
import com.github.elenterius.biomancy.crafting.recipe.DecomposingRecipe;
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

public final class DecomposingRecipeBuilder implements RecipeBuilder<DecomposingRecipeBuilder> {

	public static final String RECIPE_SUB_FOLDER = ModRecipes.DECOMPOSING_RECIPE_TYPE.getId().getPath();

	private final List<VariableOutput> outputs = new ArrayList<>();
	private final Advancement.Builder advancement = Advancement.Builder.recipeAdvancement();
	private final List<ICondition> conditions = new ArrayList<>();
	private ResourceLocation recipeId;
	private IngredientStack ingredientStack = null;
	private int craftingTimeTicks = -1;
	private int extraCraftingTimeTicks = 0;
	private int craftingCostNutrients = -1;
	private int extraCraftingCostNutrients = 0;
	@Nullable
	private String group;

	private DecomposingRecipeBuilder() {
		this.recipeId = BiomancyMod.createRL("unknown");
	}

	public static DecomposingRecipeBuilder create() {
		return new DecomposingRecipeBuilder();
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

	public DecomposingRecipeBuilder ifModLoaded(String modId) {
		return withCondition(new ModLoadedCondition(modId));
	}

	public DecomposingRecipeBuilder ifModMissing(String modId) {
		return withCondition(new NotCondition(new ModLoadedCondition(modId)));
	}

	public DecomposingRecipeBuilder withCondition(ICondition condition) {
		conditions.add(condition);
		return this;
	}

	public DecomposingRecipeBuilder setCraftingTime(int time) {
		if (time < 0) throw new IllegalArgumentException("Invalid crafting time: " + time);
		craftingTimeTicks = time;
		return this;
	}

	public DecomposingRecipeBuilder addExtraCraftingTime(int time) {
		if (time < 0) throw new IllegalArgumentException("Invalid extra crafting time: " + time);
		extraCraftingTimeTicks = time;
		return this;
	}

	public DecomposingRecipeBuilder setCraftingCost(int costNutrients) {
		if (costNutrients < 0) throw new IllegalArgumentException("Invalid crafting cost: " + costNutrients);
		craftingCostNutrients = costNutrients;
		return this;
	}

	public DecomposingRecipeBuilder addExtraCraftingCost(int costNutrients) {
		if (costNutrients < 0) throw new IllegalArgumentException("Invalid extra crafting cost: " + costNutrients);
		extraCraftingCostNutrients = costNutrients;
		return this;
	}

	public DecomposingRecipeBuilder setIngredient(TagKey<Item> tagKey) {
		return setIngredient(tagKey, 1);
	}

	public DecomposingRecipeBuilder setIngredient(TagKey<Item> tagKey, int quantity) {
		return setIngredient(Ingredient.of(tagKey), quantity, BiomancyMod.createRL(tagKey.location().toDebugFileName()));
	}

	public DecomposingRecipeBuilder setIngredient(RegistryObject<? extends Item> itemHolder) {
		return setIngredient(itemHolder.get(), 1);
	}

	public DecomposingRecipeBuilder setIngredient(ItemLike itemIn) {
		return setIngredient(itemIn, 1);
	}

	public DecomposingRecipeBuilder setIngredient(Ingredient ingredient, ResourceLocation recipeId) {
		return setIngredient(ingredient, 1, recipeId);
	}

	public DecomposingRecipeBuilder setIngredient(ItemLike itemLike, int quantity) {
		setIngredient(Ingredient.of(itemLike), quantity, BiomancyMod.createRL(getRegistryKey(itemLike).getPath()));
		return this;
	}

	public DecomposingRecipeBuilder setIngredient(DatagenIngredient ingredient) {
		setIngredient(ingredient, 1, BiomancyMod.createRL(ingredient.resourceLocation.getNamespace() + "_" + ingredient.resourceLocation.getPath()));
		return this;
	}

	public DecomposingRecipeBuilder setIngredient(Ingredient ingredient, int count, ResourceLocation recipeId) {
		if (ingredientStack != null) throw new IllegalStateException("Ingredient is already set");
		ingredientStack = new IngredientStack(ingredient, count);
		this.recipeId = new ResourceLocation(recipeId.getNamespace(), RECIPE_SUB_FOLDER + "/" + recipeId.getPath());
		return this;
	}

	public DecomposingRecipeBuilder addOutput(ItemLike result) {
		return addOutput(result, 1);
	}

	public DecomposingRecipeBuilder addOutput(ItemLike result, int count) {
		return addOutput(new VariableOutput(result, count));
	}

	public DecomposingRecipeBuilder addOutput(ItemLike result, int min, int max) {
		return addOutput(new VariableOutput(result, min, max));
	}

	public DecomposingRecipeBuilder addOutput(ItemLike result, int n, float p) {
		return addOutput(new VariableOutput(result, n, p));
	}

	public DecomposingRecipeBuilder addRecyclingOutput(ItemLike result, int originalCount) {
		return addExtraCraftingTime(3 * 20).addExtraCraftingCost(1).addOutput(new VariableOutput(result, (originalCount - 1) / 2, originalCount - 1));
	}

	public DecomposingRecipeBuilder addOutput(VariableOutput output) {
		outputs.add(output);
		return this;
	}

	public DecomposingRecipeBuilder addOutputs(VariableOutput... outputsIn) {
		outputs.addAll(Arrays.asList(outputsIn));
		return this;
	}

	@Override
	public DecomposingRecipeBuilder unlockedBy(String name, CriterionTriggerInstance criterionTrigger) {
		advancement.addCriterion(name, criterionTrigger);
		return this;
	}

	public DecomposingRecipeBuilder setGroup(@Nullable String name) {
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
			craftingCostNutrients = RecipeCostUtil.getCost(DecomposingRecipe.DEFAULT_CRAFTING_COST_NUTRIENTS, craftingTimeTicks);
		}

		craftingTimeTicks += extraCraftingTimeTicks;
		craftingCostNutrients += extraCraftingCostNutrients;

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
		private final List<VariableOutput> outputs;
		private final int craftingTime;
		private final int craftingCost;
		private final List<ICondition> conditions;
		private final Advancement.Builder advancementBuilder;
		private final ResourceLocation advancementId;

		public RecipeResult(DecomposingRecipeBuilder builder, ResourceLocation advancementId) {
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
			for (VariableOutput output : outputs) {
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

		public static int getTotalTicks(List<VariableOutput> outputs) {
			float ticks = 0;
			for (VariableOutput output : outputs) {
				ticks += getTicks(output.getItem(), getMaxAmount(output));
			}
			return Math.round(ticks);
		}

		static int getMaxAmount(VariableOutput output) {
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

