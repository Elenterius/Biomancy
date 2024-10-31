package com.github.elenterius.biomancy.datagen.recipes;

import com.github.alexmodguy.alexscaves.AlexsCaves;
import com.github.alexmodguy.alexscaves.server.block.ACBlockRegistry;
import com.github.alexthe666.alexsmobs.AlexsMobs;
import com.github.alexthe666.alexsmobs.block.AMBlockRegistry;
import com.github.elenterius.biomancy.crafting.recipe.FoodDigestingRecipe;
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

public class DigestingRecipeProvider extends RecipeProvider {

	protected DigestingRecipeProvider(PackOutput output) {
		super(output);
	}

	protected static String getItemName(ItemLike itemLike) {
		ResourceLocation key = ForgeRegistries.ITEMS.getKey(itemLike.asItem());
		return key != null ? key.getPath() : "unknown";
	}

	protected static String getTagName(TagKey<Item> tag) {
		return tag.location().getPath();
	}

	protected DigesterRecipeBuilder nutrientPasteRecipe(int count, TagKey<Item> ingredient) {
		return simpleRecipe(ModItems.NUTRIENT_PASTE.get(), count, ingredient);
	}

	protected DigesterRecipeBuilder nutrientPasteRecipe(int count, ItemLike ingredient) {
		return simpleRecipe(ModItems.NUTRIENT_PASTE.get(), count, ingredient);
	}

	protected DigesterRecipeBuilder simpleRecipe(ItemLike result, int count, TagKey<Item> ingredient) {
		return DigesterRecipeBuilder.create(result, count, getTagName(ingredient)).setIngredient(ingredient).unlockedBy(ingredient);
	}

	protected DigesterRecipeBuilder simpleRecipe(ItemLike result, int count, ItemLike ingredient) {
		return DigesterRecipeBuilder.create(result, count, getItemName(ingredient)).setIngredient(ingredient).unlockedBy(ingredient);
	}

	@Override
	protected void buildRecipes(Consumer<FinishedRecipe> consumer) {
		buildFromFoodRecipes(consumer);
		buildFromOrganicRecipes(consumer);
		buildAlexsMobsRecipes(consumer);
		buildAlexsCavesRecipes(consumer);
	}

	private void buildFromFoodRecipes(Consumer<FinishedRecipe> consumer) {
		FoodDigestingRecipe.RecipeBuilder.save(consumer, 1, ModItems.NUTRIENT_PASTE.get()); //dynamic recipe that handles all food items
	}

	private void buildFromOrganicRecipes(Consumer<FinishedRecipe> consumer) {
		nutrientPasteRecipe(1, Items.GRASS).save(consumer);
		nutrientPasteRecipe(1, Items.SEAGRASS).save(consumer);
		nutrientPasteRecipe(1, Items.VINE).save(consumer);
		nutrientPasteRecipe(1, Items.FERN).save(consumer);
		nutrientPasteRecipe(1, Items.HANGING_ROOTS).save(consumer);

		nutrientPasteRecipe(1, Items.BAMBOO).addCraftingTimeModifier(60).save(consumer);

		int sugarNutrition = 1;
		nutrientPasteRecipe(sugarNutrition, Items.SUGAR_CANE).addCraftingTimeModifier(20).save(consumer);

		int seedNutrition = 1;
		nutrientPasteRecipe(seedNutrition, Tags.Items.SEEDS).addCraftingTimeModifier(40).save(consumer);

		nutrientPasteRecipe(1, ItemTags.SMALL_FLOWERS).addCraftingTimeModifier(-20).save(consumer);
		nutrientPasteRecipe(1, ItemTags.LEAVES).addCraftingTimeModifier(25).save(consumer);
		nutrientPasteRecipe(1, ItemTags.SAPLINGS).addCraftingTimeModifier(15).save(consumer);

		nutrientPasteRecipe(1, Items.MOSS_CARPET).addCraftingTimeModifier(-20).save(consumer);
		nutrientPasteRecipe(2, Items.MOSS_BLOCK).setCraftingCost(3).addCraftingTimeModifier(20).save(consumer);

		nutrientPasteRecipe(2, Items.NETHER_WART).setCraftingCost(2).addCraftingTimeModifier(-40).save(consumer);
		nutrientPasteRecipe(2, Items.CACTUS).setCraftingCost(3).save(consumer);
		nutrientPasteRecipe(2, Items.LARGE_FERN).save(consumer);
		nutrientPasteRecipe(2, Items.TALL_GRASS).save(consumer);
		nutrientPasteRecipe(2, ItemTags.TALL_FLOWERS).save(consumer);
		nutrientPasteRecipe(2, Items.NETHER_SPROUTS).save(consumer);
		nutrientPasteRecipe(2, Items.WEEPING_VINES).save(consumer);
		nutrientPasteRecipe(2, Items.TWISTING_VINES).save(consumer);
		nutrientPasteRecipe(2, Items.WARPED_ROOTS).save(consumer);
		nutrientPasteRecipe(2, Items.CRIMSON_ROOTS).save(consumer);
		nutrientPasteRecipe(2, Items.LILY_PAD).save(consumer);

		nutrientPasteRecipe(2, Items.SMALL_DRIPLEAF).save(consumer);
		nutrientPasteRecipe(4, Items.BIG_DRIPLEAF).setCraftingCost(3).save(consumer);

		nutrientPasteRecipe(3, Items.HONEYCOMB).save(consumer);
		nutrientPasteRecipe(3, Items.SEA_PICKLE).save(consumer);
		nutrientPasteRecipe(3, Items.WARPED_WART_BLOCK).setCraftingCost(3).save(consumer);
		nutrientPasteRecipe(9 * 2, Items.NETHER_WART_BLOCK).setCraftingCost(9 * 2 - 2).save(consumer);
		nutrientPasteRecipe(4, Items.SHROOMLIGHT).setCraftingCost(3).save(consumer);

		nutrientPasteRecipe(Foods.DRIED_KELP.getNutrition(), Items.KELP).setCraftingCost(3).addCraftingTimeModifier(35).save(consumer);
		nutrientPasteRecipe(9 * Foods.DRIED_KELP.getNutrition(), Items.DRIED_KELP_BLOCK).addCraftingTimeModifier(-Math.round(20 * 4.5f)).save(consumer);

		int wheatNutrition = Math.max(1, Foods.BREAD.getNutrition() / 3);
		nutrientPasteRecipe(wheatNutrition, Items.WHEAT).save(consumer);
		nutrientPasteRecipe(9 * wheatNutrition, Items.HAY_BLOCK).addCraftingTimeModifier(40).save(consumer);

		nutrientPasteRecipe(4, Items.COCOA_BEANS).setCraftingCost(3).addCraftingTimeModifier(60).save(consumer);

		int eggNutrition = 1;
		nutrientPasteRecipe(eggNutrition, Items.EGG).addCraftingTimeModifier(20).save(consumer);
		nutrientPasteRecipe(2, Items.TURTLE_EGG).save(consumer);
		nutrientPasteRecipe(6, Items.SNIFFER_EGG).setCraftingCost(3).save(consumer);

		int mushroomNutrition = Math.max(1, Foods.MUSHROOM_STEW.getNutrition() / 2);
		nutrientPasteRecipe(mushroomNutrition, Tags.Items.MUSHROOMS).save(consumer);
		nutrientPasteRecipe(mushroomNutrition, Items.WARPED_FUNGUS).save(consumer);
		nutrientPasteRecipe(mushroomNutrition, Items.CRIMSON_FUNGUS).save(consumer);
		nutrientPasteRecipe(mushroomNutrition * 2, Items.RED_MUSHROOM_BLOCK).setCraftingCost(3).save(consumer);
		nutrientPasteRecipe(mushroomNutrition * 2, Items.BROWN_MUSHROOM_BLOCK).setCraftingCost(3).save(consumer);
		nutrientPasteRecipe(2, Items.MUSHROOM_STEM).setCraftingCost(3).save(consumer);

		int milkNutrition = 2;

		int cakeNutrition = 3 * wheatNutrition + 2 * sugarNutrition + eggNutrition + 3 * milkNutrition;
		nutrientPasteRecipe(cakeNutrition, Items.CAKE).save(consumer);

		nutrientPasteRecipe(7 * Foods.MELON_SLICE.getNutrition(), Items.MELON).setCraftingCost(3).save(consumer);

		int pumpkinNutrition = Foods.PUMPKIN_PIE.getNutrition() - sugarNutrition - eggNutrition;
		nutrientPasteRecipe(pumpkinNutrition, Items.PUMPKIN).setCraftingCost(3).save(consumer);
		nutrientPasteRecipe(pumpkinNutrition - 4 * seedNutrition, Items.CARVED_PUMPKIN).addCraftingTimeModifier(-100).save(consumer);
	}

	private DigesterRecipeBuilder alexsMobsRecipe(int count, ItemLike ingredient) {
		return nutrientPasteRecipe(count, ingredient).ifModLoaded(AlexsMobs.MODID);
	}

	private DigesterRecipeBuilder alexsCavesRecipe(int count, ItemLike ingredient) {
		return nutrientPasteRecipe(count, ingredient).ifModLoaded(AlexsCaves.MODID);
	}

	private void buildAlexsMobsRecipes(Consumer<FinishedRecipe> consumer) {
		alexsMobsRecipe(1, AMBlockRegistry.CAIMAN_EGG.get()).save(consumer);
		alexsMobsRecipe(1, AMBlockRegistry.CROCODILE_EGG.get()).save(consumer);
		alexsMobsRecipe(1, AMBlockRegistry.TERRAPIN_EGG.get()).save(consumer);
		alexsMobsRecipe(2, AMBlockRegistry.PLATYPUS_EGG.get()).save(consumer);
	}

	private void buildAlexsCavesRecipes(Consumer<FinishedRecipe> consumer) {
		alexsCavesRecipe(2 * 12, ACBlockRegistry.COOKED_DINOSAUR_CHOP.get()).addCraftingTimeModifier(20 * 12).save(consumer);
		alexsCavesRecipe(2, ACBlockRegistry.PEWEN_PINES.get()).addCraftingTimeModifier(20).save(consumer);
		alexsCavesRecipe(2, ACBlockRegistry.PEWEN_BRANCH.get()).addCraftingTimeModifier(25).save(consumer);
		alexsCavesRecipe(2, ACBlockRegistry.FIDDLEHEAD.get()).save(consumer);
		alexsCavesRecipe(2, ACBlockRegistry.CURLY_FERN.get()).save(consumer);
		alexsCavesRecipe(4, ACBlockRegistry.FLYTRAP.get()).addCraftingTimeModifier(10).save(consumer);
		alexsCavesRecipe(2, ACBlockRegistry.CYCAD.get()).addCraftingTimeModifier(40).save(consumer);
		alexsCavesRecipe(4, ACBlockRegistry.TREE_STAR.get()).addCraftingTimeModifier(-10).save(consumer);
		alexsCavesRecipe(2, ACBlockRegistry.ARCHAIC_VINE.get()).save(consumer);
		alexsCavesRecipe(4, ACBlockRegistry.FERN_THATCH.get()).save(consumer);
		alexsCavesRecipe(1, ACBlockRegistry.UNDERWEED.get()).save(consumer);
		alexsCavesRecipe(1, ACBlockRegistry.THORNWOOD_BRANCH.get()).save(consumer);

		alexsCavesRecipe(4, ACBlockRegistry.SUBTERRANODON_EGG.get()).setCraftingCost(3).save(consumer);
		alexsCavesRecipe(4, ACBlockRegistry.VALLUMRAPTOR_EGG.get()).setCraftingCost(3).save(consumer);
		alexsCavesRecipe(4, ACBlockRegistry.GROTTOCERATOPS_EGG.get()).setCraftingCost(3).save(consumer);
		alexsCavesRecipe(4, ACBlockRegistry.TREMORSAURUS_EGG.get()).setCraftingCost(3).save(consumer);
		alexsCavesRecipe(4, ACBlockRegistry.RELICHEIRUS_EGG.get()).setCraftingCost(3).save(consumer);
		alexsCavesRecipe(4, ACBlockRegistry.ATLATITAN_EGG.get()).setCraftingCost(3).save(consumer);
	}
}
