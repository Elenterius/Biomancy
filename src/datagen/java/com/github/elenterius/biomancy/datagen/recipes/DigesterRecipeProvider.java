package com.github.elenterius.biomancy.datagen.recipes;

import com.github.elenterius.biomancy.advancements.predicate.FoodItemPredicate;
import com.github.elenterius.biomancy.crafting.recipe.FoodNutritionIngredient;
import com.github.elenterius.biomancy.datagen.recipes.builder.DigesterRecipeBuilder;
import com.github.elenterius.biomancy.init.ModItems;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.food.Foods;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.common.Tags;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.function.Consumer;

public class DigesterRecipeProvider extends RecipeProvider {

	protected DigesterRecipeProvider(PackOutput output) {
		super(output);
	}

	protected static String getItemName(ItemLike itemLike) {
		ResourceLocation key = ForgeRegistries.ITEMS.getKey(itemLike.asItem());
		return key != null ? key.getPath() : "unknown";
	}

	protected static String getTagName(TagKey<Item> tag) {
		return tag.location().getPath();
	}

	@Override
	protected void buildRecipes(Consumer<FinishedRecipe> consumer) {
		buildFromFoodRecipes(consumer);
		buildFromOrganicRecipes(consumer);
	}

	private void buildFromFoodRecipes(Consumer<FinishedRecipe> consumer) {
		String predicateName = "has_food";
		FoodItemPredicate predicate = new FoodItemPredicate();

		DigesterRecipeBuilder.create(ModItems.NUTRIENT_PASTE.get(), 1, "poor_food")
				.setIngredient(new FoodNutritionIngredient(0, 1))
				.unlockedBy(predicateName, predicate).save(consumer);

		DigesterRecipeBuilder.create(ModItems.NUTRIENT_PASTE.get(), 2, "average_food")
				.setIngredient(new FoodNutritionIngredient(2, 3))
				.unlockedBy(predicateName, predicate).save(consumer);

		DigesterRecipeBuilder.create(ModItems.NUTRIENT_PASTE.get(), 4, "good_food")
				.setIngredient(new FoodNutritionIngredient(4, 5))
				.unlockedBy(predicateName, predicate).save(consumer);

		DigesterRecipeBuilder.create(ModItems.NUTRIENT_PASTE.get(), 6, "superb_food")
				.setIngredient(new FoodNutritionIngredient(6, 7))
				.unlockedBy(predicateName, predicate).save(consumer);

		DigesterRecipeBuilder.create(ModItems.NUTRIENT_PASTE.get(), 8, "excellent_food")
				.setIngredient(new FoodNutritionIngredient(8, 9))
				.unlockedBy(predicateName, predicate).save(consumer);

		DigesterRecipeBuilder.create(ModItems.NUTRIENT_PASTE.get(), 10, "godly_food")
				.setIngredient(new FoodNutritionIngredient(10, Integer.MAX_VALUE))
				.unlockedBy(predicateName, predicate).save(consumer);
	}

	private void buildFromOrganicRecipes(Consumer<FinishedRecipe> consumer) {
		simpleDigesterRecipe(ModItems.NUTRIENT_PASTE.get(), 1, Items.GRASS).save(consumer);
		simpleDigesterRecipe(ModItems.NUTRIENT_PASTE.get(), 1, Items.SEAGRASS).save(consumer);
		simpleDigesterRecipe(ModItems.NUTRIENT_PASTE.get(), 1, Items.VINE).save(consumer);
		simpleDigesterRecipe(ModItems.NUTRIENT_PASTE.get(), 1, Items.FERN).save(consumer);

		simpleDigesterRecipe(ModItems.NUTRIENT_PASTE.get(), 1, Items.BAMBOO).modifyCraftingTime(x -> x + 40).save(consumer);
		simpleDigesterRecipe(ModItems.NUTRIENT_PASTE.get(), 1, Items.SUGAR_CANE).modifyCraftingTime(x -> x + 20).save(consumer);
		simpleDigesterRecipe(ModItems.NUTRIENT_PASTE.get(), 1, Tags.Items.SEEDS).modifyCraftingTime(x -> x + 20).save(consumer);
		simpleDigesterRecipe(ModItems.NUTRIENT_PASTE.get(), 1, ItemTags.FLOWERS).modifyCraftingTime(x -> x - 20).save(consumer);
		simpleDigesterRecipe(ModItems.NUTRIENT_PASTE.get(), 1, ItemTags.LEAVES).modifyCraftingTime(x -> x - 20).save(consumer);

		simpleDigesterRecipe(ModItems.NUTRIENT_PASTE.get(), 2, ItemTags.SAPLINGS).modifyCraftingTime(x -> x + 20).save(consumer);
		simpleDigesterRecipe(ModItems.NUTRIENT_PASTE.get(), 2, Items.NETHER_WART).modifyCraftingTime(x -> x - 40).save(consumer);
		simpleDigesterRecipe(ModItems.NUTRIENT_PASTE.get(), 2, Items.CACTUS).save(consumer);
		simpleDigesterRecipe(ModItems.NUTRIENT_PASTE.get(), 2, Items.LARGE_FERN).save(consumer);
		simpleDigesterRecipe(ModItems.NUTRIENT_PASTE.get(), 2, Items.TALL_GRASS).save(consumer);
		simpleDigesterRecipe(ModItems.NUTRIENT_PASTE.get(), 2, Items.NETHER_SPROUTS).save(consumer);
		simpleDigesterRecipe(ModItems.NUTRIENT_PASTE.get(), 2, Items.WEEPING_VINES).save(consumer);
		simpleDigesterRecipe(ModItems.NUTRIENT_PASTE.get(), 2, Items.TWISTING_VINES).save(consumer);
		simpleDigesterRecipe(ModItems.NUTRIENT_PASTE.get(), 2, Items.WARPED_ROOTS).save(consumer);
		simpleDigesterRecipe(ModItems.NUTRIENT_PASTE.get(), 2, Items.CRIMSON_ROOTS).save(consumer);
		simpleDigesterRecipe(ModItems.NUTRIENT_PASTE.get(), 2, Items.LILY_PAD).save(consumer);

		simpleDigesterRecipe(ModItems.NUTRIENT_PASTE.get(), 4, Items.HONEYCOMB).save(consumer);
		simpleDigesterRecipe(ModItems.NUTRIENT_PASTE.get(), 4, Items.SEA_PICKLE).save(consumer);
		simpleDigesterRecipe(ModItems.NUTRIENT_PASTE.get(), 4, Items.WARPED_WART_BLOCK).save(consumer);
		simpleDigesterRecipe(ModItems.NUTRIENT_PASTE.get(), 9 * 2, Items.NETHER_WART_BLOCK).modifyCraftingTime(x -> x - 120).save(consumer);
		simpleDigesterRecipe(ModItems.NUTRIENT_PASTE.get(), 4, Items.SHROOMLIGHT).save(consumer);

		simpleDigesterRecipe(ModItems.NUTRIENT_PASTE.get(), Foods.DRIED_KELP.getNutrition(), Items.KELP).modifyCraftingTime(x -> x + 20).setCraftingCost(2).save(consumer);
		simpleDigesterRecipe(ModItems.NUTRIENT_PASTE.get(), Foods.DRIED_KELP.getNutrition(), Items.DRIED_KELP).modifyCraftingTime(x -> x - 20).save(consumer);
		simpleDigesterRecipe(ModItems.NUTRIENT_PASTE.get(), 9 * Foods.DRIED_KELP.getNutrition(), Items.DRIED_KELP_BLOCK).modifyCraftingTime(x -> x - Math.round(20 * 4.5f)).save(consumer);

		int wheatNutrition = Math.max(1, Foods.BREAD.getNutrition() / 3);
		simpleDigesterRecipe(ModItems.NUTRIENT_PASTE.get(), wheatNutrition, Items.WHEAT).save(consumer);
		simpleDigesterRecipe(ModItems.NUTRIENT_PASTE.get(), 9 * wheatNutrition, Items.HAY_BLOCK).save(consumer);

		simpleDigesterRecipe(ModItems.NUTRIENT_PASTE.get(), 4, Items.COCOA_BEANS).modifyCraftingTime(x -> x + 25).save(consumer);

		int mushroomNutrition = Math.max(1, Foods.MUSHROOM_STEW.getNutrition() / 2);
		simpleDigesterRecipe(ModItems.NUTRIENT_PASTE.get(), mushroomNutrition, Tags.Items.MUSHROOMS).save(consumer);
		simpleDigesterRecipe(ModItems.NUTRIENT_PASTE.get(), mushroomNutrition, Items.WARPED_FUNGUS).save(consumer);
		simpleDigesterRecipe(ModItems.NUTRIENT_PASTE.get(), mushroomNutrition, Items.CRIMSON_FUNGUS).save(consumer);
		simpleDigesterRecipe(ModItems.NUTRIENT_PASTE.get(), mushroomNutrition * 2, Items.RED_MUSHROOM_BLOCK).save(consumer);
		simpleDigesterRecipe(ModItems.NUTRIENT_PASTE.get(), mushroomNutrition * 2, Items.BROWN_MUSHROOM_BLOCK).save(consumer);
		simpleDigesterRecipe(ModItems.NUTRIENT_PASTE.get(), 2, Items.MUSHROOM_STEM).save(consumer);

		simpleDigesterRecipe(ModItems.NUTRIENT_PASTE.get(), 7 * 2, Items.CAKE).save(consumer);
		simpleDigesterRecipe(ModItems.NUTRIENT_PASTE.get(), 9 * 2, Items.MELON).save(consumer);
		simpleDigesterRecipe(ModItems.NUTRIENT_PASTE.get(), 4 * 3, Items.PUMPKIN).save(consumer);
		simpleDigesterRecipe(ModItems.NUTRIENT_PASTE.get(), 4 * 3 - 4, Items.CARVED_PUMPKIN).modifyCraftingTime(x -> x - 100).save(consumer);
	}

	protected DigesterRecipeBuilder simpleDigesterRecipe(ItemLike result, int count, TagKey<Item> ingredient) {
		return (DigesterRecipeBuilder) DigesterRecipeBuilder.create(result, count, getTagName(ingredient)).setIngredient(ingredient).unlockedBy(ingredient);
	}

	protected DigesterRecipeBuilder simpleDigesterRecipe(ItemLike result, int count, ItemLike ingredient) {
		return (DigesterRecipeBuilder) DigesterRecipeBuilder.create(result, count, getItemName(ingredient)).setIngredient(ingredient).unlockedBy(ingredient);
	}

}
