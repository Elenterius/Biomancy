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
		buildAlexsMobsRecipes(consumer);
		buildAlexsCavesRecipes(consumer);
	}

	private void buildFromFoodRecipes(Consumer<FinishedRecipe> consumer) {
		FoodDigestingRecipe.RecipeBuilder.save(consumer, 1, ModItems.NUTRIENT_PASTE.get()); //dynamic recipe that handles all food items
	}

	private void buildFromOrganicRecipes(Consumer<FinishedRecipe> consumer) {
		simpleDigesterRecipe(ModItems.NUTRIENT_PASTE.get(), 1, Items.GRASS).save(consumer);
		simpleDigesterRecipe(ModItems.NUTRIENT_PASTE.get(), 1, Items.SEAGRASS).save(consumer);
		simpleDigesterRecipe(ModItems.NUTRIENT_PASTE.get(), 1, Items.VINE).save(consumer);
		simpleDigesterRecipe(ModItems.NUTRIENT_PASTE.get(), 1, Items.FERN).save(consumer);
		simpleDigesterRecipe(ModItems.NUTRIENT_PASTE.get(), 1, Items.HANGING_ROOTS).save(consumer);

		simpleDigesterRecipe(ModItems.NUTRIENT_PASTE.get(), 1, Items.BAMBOO).modifyCraftingTime(x -> x + 60).save(consumer);

		int sugarNutrition = 1;
		simpleDigesterRecipe(ModItems.NUTRIENT_PASTE.get(), sugarNutrition, Items.SUGAR_CANE).modifyCraftingTime(x -> x + 20).save(consumer);

		int seedNutrition = 1;
		simpleDigesterRecipe(ModItems.NUTRIENT_PASTE.get(), seedNutrition, Tags.Items.SEEDS).modifyCraftingTime(x -> x + 40).save(consumer);

		simpleDigesterRecipe(ModItems.NUTRIENT_PASTE.get(), 1, ItemTags.SMALL_FLOWERS).modifyCraftingTime(x -> x - 20).save(consumer);
		simpleDigesterRecipe(ModItems.NUTRIENT_PASTE.get(), 1, ItemTags.LEAVES).modifyCraftingTime(x -> x + 25).save(consumer);
		simpleDigesterRecipe(ModItems.NUTRIENT_PASTE.get(), 1, ItemTags.SAPLINGS).modifyCraftingTime(x -> x + 15).save(consumer);

		simpleDigesterRecipe(ModItems.NUTRIENT_PASTE.get(), 1, Items.MOSS_CARPET).modifyCraftingTime(x -> x - 20).save(consumer);
		simpleDigesterRecipe(ModItems.NUTRIENT_PASTE.get(), 2, Items.MOSS_BLOCK).modifyCraftingTime(x -> x + 20).save(consumer);

		simpleDigesterRecipe(ModItems.NUTRIENT_PASTE.get(), 2, Items.NETHER_WART).modifyCraftingTime(x -> x - 40).save(consumer);
		simpleDigesterRecipe(ModItems.NUTRIENT_PASTE.get(), 2, Items.CACTUS).save(consumer);
		simpleDigesterRecipe(ModItems.NUTRIENT_PASTE.get(), 2, Items.LARGE_FERN).save(consumer);
		simpleDigesterRecipe(ModItems.NUTRIENT_PASTE.get(), 2, Items.TALL_GRASS).save(consumer);
		simpleDigesterRecipe(ModItems.NUTRIENT_PASTE.get(), 2, ItemTags.TALL_FLOWERS).save(consumer);
		simpleDigesterRecipe(ModItems.NUTRIENT_PASTE.get(), 2, Items.NETHER_SPROUTS).save(consumer);
		simpleDigesterRecipe(ModItems.NUTRIENT_PASTE.get(), 2, Items.WEEPING_VINES).save(consumer);
		simpleDigesterRecipe(ModItems.NUTRIENT_PASTE.get(), 2, Items.TWISTING_VINES).save(consumer);
		simpleDigesterRecipe(ModItems.NUTRIENT_PASTE.get(), 2, Items.WARPED_ROOTS).save(consumer);
		simpleDigesterRecipe(ModItems.NUTRIENT_PASTE.get(), 2, Items.CRIMSON_ROOTS).save(consumer);
		simpleDigesterRecipe(ModItems.NUTRIENT_PASTE.get(), 2, Items.LILY_PAD).save(consumer);

		simpleDigesterRecipe(ModItems.NUTRIENT_PASTE.get(), 2, Items.SMALL_DRIPLEAF).save(consumer);
		simpleDigesterRecipe(ModItems.NUTRIENT_PASTE.get(), 4, Items.BIG_DRIPLEAF).save(consumer);

		simpleDigesterRecipe(ModItems.NUTRIENT_PASTE.get(), 4, Items.HONEYCOMB).save(consumer);
		simpleDigesterRecipe(ModItems.NUTRIENT_PASTE.get(), 4, Items.SEA_PICKLE).save(consumer);
		simpleDigesterRecipe(ModItems.NUTRIENT_PASTE.get(), 4, Items.WARPED_WART_BLOCK).save(consumer);
		simpleDigesterRecipe(ModItems.NUTRIENT_PASTE.get(), 9 + 5, Items.NETHER_WART_BLOCK).modifyCraftingTime(x -> x - 120).save(consumer);
		simpleDigesterRecipe(ModItems.NUTRIENT_PASTE.get(), 4, Items.SHROOMLIGHT).save(consumer);

		simpleDigesterRecipe(ModItems.NUTRIENT_PASTE.get(), Foods.DRIED_KELP.getNutrition(), Items.KELP).modifyCraftingTime(x -> x + 35).setCraftingCost(2).save(consumer);
		simpleDigesterRecipe(ModItems.NUTRIENT_PASTE.get(), 9 * Foods.DRIED_KELP.getNutrition(), Items.DRIED_KELP_BLOCK).modifyCraftingTime(x -> x - Math.round(20 * 4.5f)).save(consumer);

		int wheatNutrition = Math.max(1, Foods.BREAD.getNutrition() / 3);
		simpleDigesterRecipe(ModItems.NUTRIENT_PASTE.get(), wheatNutrition, Items.WHEAT).save(consumer);
		simpleDigesterRecipe(ModItems.NUTRIENT_PASTE.get(), 9 * wheatNutrition, Items.HAY_BLOCK).save(consumer);

		simpleDigesterRecipe(ModItems.NUTRIENT_PASTE.get(), 4, Items.COCOA_BEANS).modifyCraftingTime(x -> x + 60).save(consumer);

		int eggNutrition = 1;
		simpleDigesterRecipe(ModItems.NUTRIENT_PASTE.get(), eggNutrition, Items.EGG).setCraftingCost(2).modifyCraftingTime(x -> x + 20).save(consumer);
		simpleDigesterRecipe(ModItems.NUTRIENT_PASTE.get(), 2, Items.TURTLE_EGG).setCraftingCost(2).save(consumer);
		simpleDigesterRecipe(ModItems.NUTRIENT_PASTE.get(), 6, Items.SNIFFER_EGG).setCraftingCost(2).save(consumer);

		int mushroomNutrition = Math.max(1, Foods.MUSHROOM_STEW.getNutrition() / 2);
		simpleDigesterRecipe(ModItems.NUTRIENT_PASTE.get(), mushroomNutrition, Tags.Items.MUSHROOMS).save(consumer);
		simpleDigesterRecipe(ModItems.NUTRIENT_PASTE.get(), mushroomNutrition, Items.WARPED_FUNGUS).save(consumer);
		simpleDigesterRecipe(ModItems.NUTRIENT_PASTE.get(), mushroomNutrition, Items.CRIMSON_FUNGUS).save(consumer);
		simpleDigesterRecipe(ModItems.NUTRIENT_PASTE.get(), mushroomNutrition * 2, Items.RED_MUSHROOM_BLOCK).save(consumer);
		simpleDigesterRecipe(ModItems.NUTRIENT_PASTE.get(), mushroomNutrition * 2, Items.BROWN_MUSHROOM_BLOCK).save(consumer);
		simpleDigesterRecipe(ModItems.NUTRIENT_PASTE.get(), 2, Items.MUSHROOM_STEM).save(consumer);

		int milkNutrition = 2;

		int cakeNutrition = 3 * wheatNutrition + 2 * sugarNutrition + eggNutrition + 3 * milkNutrition;
		simpleDigesterRecipe(ModItems.NUTRIENT_PASTE.get(), cakeNutrition, Items.CAKE).save(consumer);

		simpleDigesterRecipe(ModItems.NUTRIENT_PASTE.get(), 7 * Foods.MELON_SLICE.getNutrition(), Items.MELON).save(consumer);

		int pumpkinNutrition = Foods.PUMPKIN_PIE.getNutrition() - sugarNutrition - eggNutrition;
		simpleDigesterRecipe(ModItems.NUTRIENT_PASTE.get(), pumpkinNutrition, Items.PUMPKIN).save(consumer);
		simpleDigesterRecipe(ModItems.NUTRIENT_PASTE.get(), pumpkinNutrition - 4 * seedNutrition, Items.CARVED_PUMPKIN).modifyCraftingTime(x -> x - 100).save(consumer);
	}

	protected DigesterRecipeBuilder simpleDigesterRecipe(ItemLike result, int count, TagKey<Item> ingredient) {
		return (DigesterRecipeBuilder) DigesterRecipeBuilder.create(result, count, getTagName(ingredient)).setIngredient(ingredient).unlockedBy(ingredient);
	}

	protected DigesterRecipeBuilder simpleDigesterRecipe(ItemLike result, int count, ItemLike ingredient) {
		return (DigesterRecipeBuilder) DigesterRecipeBuilder.create(result, count, getItemName(ingredient)).setIngredient(ingredient).unlockedBy(ingredient);
	}

	private DigesterRecipeBuilder createAlexsMobsRecipe(int count, ItemLike ingredient) {
		return simpleDigesterRecipe(ModItems.NUTRIENT_PASTE.get(), count, ingredient).ifModLoaded(AlexsMobs.MODID);
	}

	private DigesterRecipeBuilder createAlexsCavesRecipe(int count, ItemLike ingredient) {
		return simpleDigesterRecipe(ModItems.NUTRIENT_PASTE.get(), count, ingredient).ifModLoaded(AlexsCaves.MODID);
	}

	private void buildAlexsMobsRecipes(Consumer<FinishedRecipe> consumer) {
		createAlexsMobsRecipe(1, AMBlockRegistry.CAIMAN_EGG.get()).setCraftingCost(2).save(consumer);
		createAlexsMobsRecipe(1, AMBlockRegistry.CROCODILE_EGG.get()).setCraftingCost(2).save(consumer);
		createAlexsMobsRecipe(1, AMBlockRegistry.TERRAPIN_EGG.get()).setCraftingCost(2).save(consumer);
		createAlexsMobsRecipe(2, AMBlockRegistry.PLATYPUS_EGG.get()).setCraftingCost(2).save(consumer);
	}

	private void buildAlexsCavesRecipes(Consumer<FinishedRecipe> consumer) {
		createAlexsCavesRecipe(2 * 12, ACBlockRegistry.COOKED_DINOSAUR_CHOP.get()).modifyCraftingTime(x -> x + 20 * 12).save(consumer);
		createAlexsCavesRecipe(2, ACBlockRegistry.PEWEN_PINES.get()).modifyCraftingTime(x -> x + 20).save(consumer);
		createAlexsCavesRecipe(2, ACBlockRegistry.PEWEN_BRANCH.get()).modifyCraftingTime(x -> x + 25).save(consumer);
		createAlexsCavesRecipe(2, ACBlockRegistry.FIDDLEHEAD.get()).save(consumer);
		createAlexsCavesRecipe(2, ACBlockRegistry.CURLY_FERN.get()).save(consumer);
		createAlexsCavesRecipe(4, ACBlockRegistry.FLYTRAP.get()).modifyCraftingTime(x -> x + 10).save(consumer);
		createAlexsCavesRecipe(2, ACBlockRegistry.CYCAD.get()).modifyCraftingTime(x -> x + 40).save(consumer);
		createAlexsCavesRecipe(4, ACBlockRegistry.TREE_STAR.get()).modifyCraftingTime(x -> x - 10).save(consumer);
		createAlexsCavesRecipe(2, ACBlockRegistry.ARCHAIC_VINE.get()).save(consumer);
		createAlexsCavesRecipe(4, ACBlockRegistry.FERN_THATCH.get()).save(consumer);
		createAlexsCavesRecipe(1, ACBlockRegistry.UNDERWEED.get()).save(consumer);
		createAlexsCavesRecipe(1, ACBlockRegistry.THORNWOOD_BRANCH.get()).save(consumer);

		createAlexsCavesRecipe(4, ACBlockRegistry.SUBTERRANODON_EGG.get()).setCraftingCost(2).save(consumer);
		createAlexsCavesRecipe(4, ACBlockRegistry.VALLUMRAPTOR_EGG.get()).setCraftingCost(2).save(consumer);
		createAlexsCavesRecipe(4, ACBlockRegistry.GROTTOCERATOPS_EGG.get()).setCraftingCost(2).save(consumer);
		createAlexsCavesRecipe(4, ACBlockRegistry.TREMORSAURUS_EGG.get()).setCraftingCost(2).save(consumer);
		createAlexsCavesRecipe(4, ACBlockRegistry.RELICHEIRUS_EGG.get()).setCraftingCost(2).save(consumer);
	}
}
