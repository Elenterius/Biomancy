package com.github.elenterius.biomancy.datagen.recipe;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.fluid.simibubi.FluidIngredient;
import com.github.elenterius.biomancy.init.ModRecipes;
import com.google.gson.JsonObject;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.ICriterionInstance;
import net.minecraft.advancements.IRequirementsStrategy;
import net.minecraft.advancements.criterion.RecipeUnlockedTrigger;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.Item;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.tags.ITag;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;
import java.util.function.Consumer;

public class SolidifierRecipeBuilder {

	private final Item result;
	private final int count;
	private final int craftingTime;
	private final Advancement.Builder advancementBuilder = Advancement.Builder.advancement();
	private FluidIngredient fluidIngredient;
	private String group;

	private SolidifierRecipeBuilder(IItemProvider resultIn, int craftingTimeIn, int countIn) {
		assert craftingTimeIn >= 0;
		assert countIn > 0;

		result = resultIn.asItem();
		craftingTime = craftingTimeIn;
		count = countIn;
	}

	public static SolidifierRecipeBuilder createRecipe(IItemProvider resultIn, int craftingTimeIn) {
		return new SolidifierRecipeBuilder(resultIn, craftingTimeIn, 1);
	}

	public static SolidifierRecipeBuilder createRecipe(IItemProvider resultIn, int craftingTimeIn, int countIn) {
		return new SolidifierRecipeBuilder(resultIn, craftingTimeIn, countIn);
	}

	public SolidifierRecipeBuilder setFluidIngredient(Fluid fluidIn) {
		return setFluidIngredient(fluidIn, 1);
	}

	public SolidifierRecipeBuilder setFluidIngredient(ITag.INamedTag<Fluid> tagIn) {
		return setFluidIngredient(tagIn, 1);
	}

	public SolidifierRecipeBuilder setFluidIngredient(ITag.INamedTag<Fluid> tagIn, int amountIn) {
		return setFluidIngredient(FluidIngredient.fromTag(tagIn, amountIn));
	}

	public SolidifierRecipeBuilder setFluidIngredient(Fluid fluidIn, int amountIn) {
		return setFluidIngredient(FluidIngredient.fromFluid(fluidIn, amountIn));
	}

	public SolidifierRecipeBuilder setFluidIngredient(FluidIngredient ingredientIn) {
		fluidIngredient = ingredientIn;
		return this;
	}

	public SolidifierRecipeBuilder addCriterion(String name, ICriterionInstance criterionIn) {
		advancementBuilder.addCriterion(name, criterionIn);
		return this;
	}

	public SolidifierRecipeBuilder setGroup(String groupIn) {
		group = groupIn;
		return this;
	}

	public void build(Consumer<IFinishedRecipe> consumerIn) {
		ResourceLocation registryKey = ForgeRegistries.ITEMS.getKey(result);
		assert registryKey != null;
		build(consumerIn, BiomancyMod.createRL(registryKey.getPath() + "_solidifying"));
	}

	public void build(Consumer<IFinishedRecipe> consumerIn, String id, boolean suffix) {
		if (suffix) {
			ResourceLocation registryKey = ForgeRegistries.ITEMS.getKey(result);
			assert registryKey != null;
			if (registryKey.getPath().equals(id)) {
				throw new IllegalStateException(String.format("Recipe suffix %s should be different from the recipe path %s", id, registryKey.getPath()));
			}
			build(consumerIn, BiomancyMod.createRL(registryKey.getPath() + "_" + id + "_solidifying"));
		}
		else {
			build(consumerIn, id);
		}
	}

	public void build(Consumer<IFinishedRecipe> consumerIn, String save) {
		ResourceLocation registryKey = ForgeRegistries.ITEMS.getKey(result);
		assert registryKey != null;
		ResourceLocation alternative = new ResourceLocation(BiomancyMod.MOD_ID, save + "_solidifying");
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
		consumerIn.accept(new Result(id, result, craftingTime, count, group == null ? "" : group, fluidIngredient, advancementBuilder, new ResourceLocation(id.getNamespace(), "recipes/" + (result.getItemCategory() != null ? result.getItemCategory().getRecipeFolderName() : BiomancyMod.MOD_ID) + "/" + id.getPath())));
	}

	private void validate(ResourceLocation id) {
		if (advancementBuilder.getCriteria().isEmpty()) {
			throw new IllegalStateException("No way of obtaining recipe " + id + " because Criteria are empty.");
		}
	}

	public static class Result implements IFinishedRecipe {
		private final ResourceLocation id;
		private final String group;
		private final FluidIngredient ingredient;
		private final Item result;
		private final int count;
		private final int craftingTime;
		private final Advancement.Builder advancementBuilder;
		private final ResourceLocation advancementId;

		public Result(ResourceLocation idIn, Item resultIn, int craftingTimeIn, int countIn, String groupIn, FluidIngredient ingredientIn, Advancement.Builder advancementBuilderIn, ResourceLocation advancementIdIn) {
			id = idIn;
			group = groupIn;
			ingredient = ingredientIn;
			result = resultIn;
			count = countIn;
			craftingTime = craftingTimeIn;
			advancementBuilder = advancementBuilderIn;
			advancementId = advancementIdIn;
		}

		public void serializeRecipeData(JsonObject json) {
			if (!group.isEmpty()) json.addProperty("group", group);

			json.add("ingredient", ingredient.serialize());

			JsonObject jsonObject = new JsonObject();
			jsonObject.addProperty("item", Registry.ITEM.getKey(result).toString());
			if (count > 1) {
				jsonObject.addProperty("count", count);
			}
			json.add("result", jsonObject);

			json.addProperty("time", craftingTime);
		}

		public IRecipeSerializer<?> getType() {
			return ModRecipes.SOLIDIFIER_SERIALIZER.get();
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

