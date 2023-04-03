package com.github.elenterius.biomancy.datagen.recipes;

import com.github.alexthe666.alexsmobs.AlexsMobs;
import com.github.alexthe666.alexsmobs.item.AMItemRegistry;
import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.init.ModBioForgeTabs;
import com.github.elenterius.biomancy.init.ModItems;
import com.github.elenterius.biomancy.init.ModTags;
import com.github.elenterius.biomancy.recipe.FoodNutritionIngredient;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.recipes.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.food.Foods;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.common.Tags;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import java.util.Arrays;
import java.util.function.Consumer;

import static com.github.elenterius.biomancy.BiomancyMod.LOGGER;

public class ModRecipeProvider extends RecipeProvider {

	private final Marker logMarker = MarkerManager.getMarker("RecipeProvider");

	public ModRecipeProvider(DataGenerator generatorIn) {
		super(generatorIn);
	}

	protected static ItemPredicate createPredicate(ItemLike item) {
		return ItemPredicate.Builder.item().of(item).build();
	}

	protected static ItemPredicate createPredicate(TagKey<Item> tag) {
		return ItemPredicate.Builder.item().of(tag).build();
	}

	protected static InventoryChangeTrigger.TriggerInstance hasItems(ItemLike... itemProviders) {
		ItemPredicate[] predicates = Arrays.stream(itemProviders).map(ModRecipeProvider::createPredicate).toArray(ItemPredicate[]::new);
		return inventoryTrigger(predicates);
	}

	protected static String hasName(ItemLike itemLike) {
		return "has_" + getItemName(itemLike);
	}

	protected static String getItemName(ItemLike itemLike) {
		ResourceLocation key = ForgeRegistries.ITEMS.getKey(itemLike.asItem());
		return key != null ? key.getPath() : "unknown";
	}

	protected static String getTagName(TagKey<Item> tag) {
		return tag.location().getPath();
	}

	protected static ResourceLocation getSimpleRecipeId(ItemLike itemLike) {
		return BiomancyMod.createRL(getItemName(itemLike));
	}

	protected static ResourceLocation getConversionRecipeId(ItemLike result, ItemLike ingredient) {
		return BiomancyMod.createRL(getItemName(result) + "_from_" + getItemName(ingredient));
	}

	protected static ResourceLocation getSmeltingRecipeId(ItemLike itemLike) {
		return BiomancyMod.createRL(getItemName(itemLike) + "_from_smelting");
	}

	protected static ResourceLocation getBlastingRecipeId(ItemLike itemLike) {
		return BiomancyMod.createRL(getItemName(itemLike) + "_from_blasting");
	}

	@Override
	public String getName() {
		return StringUtils.capitalize(BiomancyMod.MOD_ID) + " " + super.getName();
	}

	@Override
	protected void buildCraftingRecipes(Consumer<FinishedRecipe> consumer) {
		LOGGER.info(logMarker, "registering workbench recipes...");
		registerWorkbenchRecipes(consumer);

		LOGGER.info(logMarker, "registering cooking recipes...");
		registerCookingRecipes(consumer);

		LOGGER.info(logMarker, "registering digesting recipes...");
		registerDigestingRecipes(consumer);

		LOGGER.info(logMarker, "registering decomposing recipes...");
		registerDecomposingRecipes(consumer);

		LOGGER.info(logMarker, "registering bio-forge recipes...");
		registerBioForgeRecipes(consumer);

		LOGGER.info(logMarker, "registering bio-lab recipes...");
		registerBioLabRecipes(consumer);
	}

	private void registerCookingRecipes(Consumer<FinishedRecipe> consumer) {
		SimpleCookingRecipeBuilder.blasting(Ingredient.of(ModItems.STONE_POWDER.get()), Items.GLASS_PANE, 0.01F, 100).unlockedBy(hasName(ModItems.STONE_POWDER.get()), has(ModItems.STONE_POWDER.get())).save(consumer, getBlastingRecipeId(Items.GLASS_PANE));
	}

	protected static void stairs(Consumer<FinishedRecipe> consumer, ItemLike result, ItemLike ingredient) {
		stairBuilder(result, Ingredient.of(ingredient)).unlockedBy(hasName(ingredient), has(ingredient)).save(consumer);
	}

	private void registerWorkbenchRecipes(Consumer<FinishedRecipe> consumer) {

		WorkbenchRecipeBuilder.shaped(ModItems.BONE_CLEAVER.get())
				.define('B', Tags.Items.BONES).define('F', Items.FLINT)
				.pattern(" FB")
				.pattern("FB ")
				.pattern("B  ")
				.unlockedBy(hasName(Items.BONE), has(Tags.Items.BONES)).save(consumer);

		WorkbenchRecipeBuilder.shapeless(ModItems.PRIMORDIAL_LIVING_OCULUS.get())
				.requires(Items.SPIDER_EYE)
				.requires(ModTags.Items.RAW_MEATS)
				.requires(ModTags.Items.RAW_MEATS)
				.requires(ModItems.MOB_SINEW.get())
				.requires(ModItems.MOB_MARROW.get())
				.requires(ModItems.PRIMORDIAL_LIVING_FLESH.get())
				.unlockedBy(hasName(ModItems.PRIMORDIAL_LIVING_FLESH.get()), has(ModItems.PRIMORDIAL_LIVING_FLESH.get())).save(consumer);

		WorkbenchRecipeBuilder.shapeless(ModItems.GUIDE_BOOK.get())
				.requires(ModItems.MOB_SINEW.get())
				.requires(Items.BOOK)
				.requires(ModTags.Items.RAW_MEATS)
				.requires(ModItems.PRIMORDIAL_LIVING_OCULUS.get())
				.requires(ModItems.MOB_FANG.get())
				.unlockedBy(hasName(ModItems.PRIMORDIAL_LIVING_FLESH.get()), has(ModItems.PRIMORDIAL_LIVING_FLESH.get())).save(consumer);

		WorkbenchRecipeBuilder.shaped(ModItems.GLASS_VIAL.get(), 8)
				.define('G', Tags.Items.GLASS).define('T', Items.CLAY_BALL)
				.pattern("GTG")
				.pattern("G G")
				.pattern(" G ")
				.unlockedBy(hasName(Items.GLASS), has(Tags.Items.GLASS)).save(consumer);

		// machines ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

		WorkbenchRecipeBuilder.shaped(ModItems.PRIMORDIAL_CRADLE.get())
				.define('E', ModItems.PRIMORDIAL_LIVING_OCULUS.get())
				.define('M', ModTags.Items.RAW_MEATS)
				.define('F', ModItems.MOB_FANG.get())
				.pattern("F F")
				.pattern("MEM")
				.pattern("MMM")
				.unlockedBy(hasName(Items.ENDER_EYE), has(Items.ENDER_EYE)).save(consumer);

		WorkbenchRecipeBuilder.shaped(ModItems.DECOMPOSER.get())
				.define('M', ModTags.Items.RAW_MEATS)
				.define('F', ModItems.MOB_FANG.get())
				.define('G', ModItems.GENERIC_MOB_GLAND.get())
				.define('E', ModItems.LIVING_FLESH.get())
				.pattern("F F")
				.pattern("MGM")
				.pattern("MEM")
				.unlockedBy(hasName(ModItems.LIVING_FLESH.get()), has(ModItems.LIVING_FLESH.get())).save(consumer);

		WorkbenchRecipeBuilder.shaped(ModItems.BIO_FORGE.get())
				.define('S', Items.SLIME_BALL)
				.define('M', ModTags.Items.RAW_MEATS)
				.define('C', ModItems.MOB_CLAW.get())
				.define('E', ModItems.LIVING_FLESH.get())
				.pattern("C C")
				.pattern("MSM")
				.pattern("MEM")
				.unlockedBy(hasName(ModItems.LIVING_FLESH.get()), has(ModItems.LIVING_FLESH.get())).save(consumer);

		// fuel ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

		WorkbenchRecipeBuilder.shapeless(ModItems.NUTRIENT_BAR.get())
				.requires(ModItems.NUTRIENT_PASTE.get(), 9)
				.unlockedBy(hasName(ModItems.NUTRIENTS.get()), has(ModItems.NUTRIENTS.get()))
				.save(consumer);

		WorkbenchRecipeBuilder.shaped(ModItems.NUTRIENT_PASTE.get())
				.define('P', ModItems.ORGANIC_MATTER.get())
				.define('L', ModItems.NUTRIENTS.get())
				.pattern("LPL")
				.pattern("PLP")
				.pattern("LPL")
				.unlockedBy(hasName(ModItems.NUTRIENTS.get()), has(ModItems.NUTRIENTS.get()))
				.save(consumer);

		WorkbenchRecipeBuilder.shapeless(ModItems.NUTRIENTS.get(), 5)
				.requires(ModItems.NUTRIENT_PASTE.get())
				.unlockedBy(hasName(ModItems.NUTRIENTS.get()), has(ModItems.NUTRIENTS.get()))
				.save(consumer, getConversionRecipeId(ModItems.NUTRIENTS.get(), ModItems.NUTRIENT_PASTE.get()));

		WorkbenchRecipeBuilder.shapeless(ModItems.NUTRIENT_PASTE.get(), 9)
				.requires(ModItems.NUTRIENT_BAR.get())
				.unlockedBy(hasName(ModItems.NUTRIENTS.get()), has(ModItems.NUTRIENTS.get()))
				.save(consumer, getConversionRecipeId(ModItems.NUTRIENT_PASTE.get(), ModItems.NUTRIENT_BAR.get()));

		// misc ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		WorkbenchRecipeBuilder.shapeless(Items.DIORITE)
				.requires(Items.COBBLESTONE)
				.requires(ModItems.MINERAL_FRAGMENT.get())
				.requires(ModItems.STONE_POWDER.get())
				.unlockedBy(hasName(ModItems.STONE_POWDER.get()), has(ModItems.STONE_POWDER.get()))
				.save(consumer, getConversionRecipeId(Items.DIORITE, ModItems.STONE_POWDER.get()));

		WorkbenchRecipeBuilder.shapeless(Items.RED_SAND)
				.requires(Items.SAND)
				.requires(ModItems.MINERAL_FRAGMENT.get(), 2)
				.unlockedBy(hasName(ModItems.MINERAL_FRAGMENT.get()), has(ModItems.MINERAL_FRAGMENT.get()))
				.save(consumer, getConversionRecipeId(Items.RED_SAND, ModItems.MINERAL_FRAGMENT.get()));

		WorkbenchRecipeBuilder.shaped(Items.DIRT)
				.define('P', ModItems.ORGANIC_MATTER.get())
				.define('L', ModItems.STONE_POWDER.get())
				.pattern("LPL")
				.pattern("PLP")
				.pattern("LPL")
				.unlockedBy(hasName(ModItems.ORGANIC_MATTER.get()), has(ModItems.ORGANIC_MATTER.get()))
				.save(consumer, getConversionRecipeId(Items.DIRT, ModItems.STONE_POWDER.get()));

		WorkbenchRecipeBuilder.shapeless(Items.CLAY_BALL)
				.requires(Items.WATER_BUCKET)
				.requires(ModItems.STONE_POWDER.get(), 8)
				.unlockedBy(hasName(ModItems.STONE_POWDER.get()), has(ModItems.STONE_POWDER.get()))
				.save(consumer, getConversionRecipeId(Items.CLAY_BALL, ModItems.STONE_POWDER.get()));

		WorkbenchRecipeBuilder.shapeless(Items.GUNPOWDER)
				.requires(Items.CHARCOAL)
				.requires(ModItems.EXOTIC_DUST.get(), 4)
				.requires(Items.BLAZE_POWDER, 2)
				.unlockedBy(hasName(ModItems.EXOTIC_DUST.get()), has(ModItems.EXOTIC_DUST.get()))
				.save(consumer, getConversionRecipeId(Items.GUNPOWDER, ModItems.EXOTIC_DUST.get()));

		WorkbenchRecipeBuilder.shaped(Items.GLOW_ITEM_FRAME)
				.define('F', Items.ITEM_FRAME)
				.define('L', ModItems.BIO_LUMENS.get())
				.pattern(" L ")
				.pattern("LFL")
				.pattern(" L ")
				.unlockedBy(hasName(ModItems.BIO_LUMENS.get()), has(ModItems.BIO_LUMENS.get()))
				.save(consumer, getSimpleRecipeId(Items.GLOW_ITEM_FRAME));

		// A recipe for converting between two versions of Flesh Door.
		WorkbenchRecipeBuilder.shapeless(ModItems.FLESH_DOOR.get())
				.requires(ModItems.FULL_FLESH_DOOR.get())
				.unlockedBy(hasName(ModItems.FULL_FLESH_DOOR.get()), has(ModItems.FULL_FLESH_DOOR.get()))
				.save(consumer, getConversionRecipeId(ModItems.FLESH_DOOR.get(), ModItems.FULL_FLESH_DOOR.get()));

		WorkbenchRecipeBuilder.shapeless(ModItems.FULL_FLESH_DOOR.get())
				.requires(ModItems.FLESH_DOOR.get())
				.unlockedBy(hasName(ModItems.FLESH_DOOR.get()), has(ModItems.FLESH_DOOR.get()))
				.save(consumer, getConversionRecipeId(ModItems.FULL_FLESH_DOOR.get(), ModItems.FLESH_DOOR.get()));

		stairs(consumer, ModItems.FLESH_STAIRS.get(), ModItems.FLESH_BLOCK.get());
		slab(consumer, ModItems.FLESH_SLAB.get(), ModItems.FLESH_BLOCK.get());
		wall(consumer, ModItems.FLESH_WALL.get(), ModItems.FLESH_BLOCK.get());
		stonecutterResultFromBase(consumer, ModItems.FLESH_STAIRS.get(), ModItems.FLESH_BLOCK.get());
		stonecutterResultFromBase(consumer, ModItems.FLESH_SLAB.get(), ModItems.FLESH_BLOCK.get(), 2);
		stonecutterResultFromBase(consumer, ModItems.FLESH_WALL.get(), ModItems.FLESH_BLOCK.get());

		stairs(consumer, ModItems.PACKED_FLESH_STAIRS.get(), ModItems.PACKED_FLESH_BLOCK.get());
		slab(consumer, ModItems.PACKED_FLESH_SLAB.get(), ModItems.PACKED_FLESH_BLOCK.get());
		wall(consumer, ModItems.PACKED_FLESH_WALL.get(), ModItems.PACKED_FLESH_BLOCK.get());
		stonecutterResultFromBase(consumer, ModItems.PACKED_FLESH_STAIRS.get(), ModItems.PACKED_FLESH_BLOCK.get());
		stonecutterResultFromBase(consumer, ModItems.PACKED_FLESH_SLAB.get(), ModItems.PACKED_FLESH_BLOCK.get(), 2);
		stonecutterResultFromBase(consumer, ModItems.PACKED_FLESH_WALL.get(), ModItems.PACKED_FLESH_BLOCK.get());

		stairs(consumer, ModItems.PRIMAL_FLESH_STAIRS.get(), ModItems.PRIMAL_FLESH_BLOCK.get());
		slab(consumer, ModItems.PRIMAL_FLESH_SLAB.get(), ModItems.PRIMAL_FLESH_BLOCK.get());
		stonecutterResultFromBase(consumer, ModItems.PRIMAL_FLESH_STAIRS.get(), ModItems.PRIMAL_FLESH_BLOCK.get());
		stonecutterResultFromBase(consumer, ModItems.PRIMAL_FLESH_SLAB.get(), ModItems.PRIMAL_FLESH_BLOCK.get(), 2);

		stairs(consumer, ModItems.MALIGNANT_FLESH_STAIRS.get(), ModItems.MALIGNANT_FLESH_BLOCK.get());
		slab(consumer, ModItems.MALIGNANT_FLESH_SLAB.get(), ModItems.MALIGNANT_FLESH_BLOCK.get());
		stonecutterResultFromBase(consumer, ModItems.MALIGNANT_FLESH_STAIRS.get(), ModItems.MALIGNANT_FLESH_BLOCK.get());
		stonecutterResultFromBase(consumer, ModItems.MALIGNANT_FLESH_SLAB.get(), ModItems.MALIGNANT_FLESH_BLOCK.get(), 2);
	}

	private void registerDigestingRecipes(Consumer<FinishedRecipe> consumer) {

		DigesterRecipeBuilder.create(ModItems.NUTRIENT_PASTE.get(), 1, "poor_food")
				.setIngredient(new FoodNutritionIngredient(0, 1))
				.unlockedBy(ModTags.Items.POOR_BIOMASS).save(consumer);

		DigesterRecipeBuilder.create(ModItems.NUTRIENT_PASTE.get(), 2, "average_food")
				.setIngredient(new FoodNutritionIngredient(2, 3))
				.unlockedBy(ModTags.Items.AVERAGE_BIOMASS).save(consumer);

		DigesterRecipeBuilder.create(ModItems.NUTRIENT_PASTE.get(), 4, "good_food")
				.setIngredient(new FoodNutritionIngredient(4, 5))
				.unlockedBy(ModTags.Items.GOOD_BIOMASS).save(consumer);

		DigesterRecipeBuilder.create(ModItems.NUTRIENT_PASTE.get(), 6, "superb_food")
				.setIngredient(new FoodNutritionIngredient(6, 7))
				.unlockedBy(ModTags.Items.SUPERB_BIOMASS).save(consumer);

		DigesterRecipeBuilder.create(ModItems.NUTRIENT_PASTE.get(), 8, "excellent_food")
				.setIngredient(new FoodNutritionIngredient(8, 9))
				.unlockedBy(ModTags.Items.SUPERB_BIOMASS).save(consumer);

		DigesterRecipeBuilder.create(ModItems.NUTRIENT_PASTE.get(), 10, "godly_food")
				.setIngredient(new FoodNutritionIngredient(10, Integer.MAX_VALUE))
				.unlockedBy(ModTags.Items.SUPERB_BIOMASS).save(consumer);

		//////////////////////////

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

		simpleDigesterRecipe(ModItems.NUTRIENT_PASTE.get(), Foods.DRIED_KELP.getNutrition(), Items.KELP).modifyCraftingTime(x -> x + 20).save(consumer);
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

	private DigesterRecipeBuilder simpleDigesterRecipe(ItemLike result, int count, TagKey<Item> ingredient) {
		return (DigesterRecipeBuilder) DigesterRecipeBuilder.create(result, count, getTagName(ingredient)).setIngredient(ingredient).unlockedBy(ingredient);
	}

	private DigesterRecipeBuilder simpleDigesterRecipe(ItemLike result, int count, ItemLike ingredient) {
		return (DigesterRecipeBuilder) DigesterRecipeBuilder.create(result, count, getItemName(ingredient)).setIngredient(ingredient).unlockedBy(ingredient);
	}

	private void registerDecomposingRecipes(Consumer<FinishedRecipe> consumer) {
		DecomposerRecipeBuilder.create().setIngredient(Items.GRASS_BLOCK).addOutput(ModItems.ORGANIC_MATTER.get(), 1).addOutput(ModItems.STONE_POWDER.get(), 0, 1).unlockedBy(Items.GRASS_BLOCK).save(consumer);
		DecomposerRecipeBuilder.create().setIngredient(Items.DIRT).addOutput(ModItems.ORGANIC_MATTER.get(), 0, 1).addOutput(ModItems.STONE_POWDER.get(), 0, 1).unlockedBy(Items.DIRT).save(consumer);
		DecomposerRecipeBuilder.create().setIngredient(Items.COARSE_DIRT).addOutput(ModItems.ORGANIC_MATTER.get(), 0, 1).addOutput(ModItems.STONE_POWDER.get(), 1).unlockedBy(Items.COARSE_DIRT).save(consumer);
		DecomposerRecipeBuilder.create().setIngredient(Items.PODZOL).addOutput(ModItems.ORGANIC_MATTER.get(), 1).addOutput(ModItems.STONE_POWDER.get(), 0, 1).unlockedBy(Items.PODZOL).save(consumer);
		DecomposerRecipeBuilder.create().setIngredient(Items.ROOTED_DIRT).addOutput(ModItems.ORGANIC_MATTER.get(), 0, 1).addOutput(ModItems.STONE_POWDER.get(), 0, 1).unlockedBy(Items.ROOTED_DIRT).save(consumer);
		DecomposerRecipeBuilder.create().setIngredient(Items.SAND).addOutput(ModItems.STONE_POWDER.get(), 1, 3).addOutput(ModItems.MINERAL_FRAGMENT.get(), 0, 1).unlockedBy(Items.SAND).save(consumer);
		DecomposerRecipeBuilder.create().setIngredient(Items.RED_SAND).addOutput(ModItems.STONE_POWDER.get(), 1, 3).addOutput(ModItems.MINERAL_FRAGMENT.get(), 1, 2).unlockedBy(Items.RED_SAND).save(consumer);
		DecomposerRecipeBuilder.create().setIngredient(Items.GRAVEL).addOutput(ModItems.STONE_POWDER.get(), 3, 6).unlockedBy(Items.GRAVEL).save(consumer);
		DecomposerRecipeBuilder.create().setIngredient(Items.SPONGE).addOutput(ModItems.ORGANIC_MATTER.get(), 2, 4).unlockedBy(Items.SPONGE).save(consumer);
		DecomposerRecipeBuilder.create().setIngredient(Items.SEA_PICKLE).addOutput(ModItems.MINERAL_FRAGMENT.get(), 1, 2).addOutput(ModItems.BIO_LUMENS.get(), 1, 2).addOutput(ModItems.ORGANIC_MATTER.get(), 1, 2).unlockedBy(Items.SEA_PICKLE).save(consumer);
		DecomposerRecipeBuilder.create().setIngredient(Items.LILY_OF_THE_VALLEY).addOutput(ModItems.ORGANIC_MATTER.get(), 1, 2).addOutput(ModItems.TOXIN_EXTRACT.get(), 0, 1).unlockedBy(Items.LILY_OF_THE_VALLEY).save(consumer);
		DecomposerRecipeBuilder.create().setIngredient(Items.WITHER_ROSE).addOutput(ModItems.ORGANIC_MATTER.get(), 1, 2).addOutput(ModItems.EXOTIC_DUST.get(), 0, 1).addOutput(ModItems.WITHERING_OOZE.get(), 3, 5).unlockedBy(Items.WITHER_ROSE).save(consumer);
		DecomposerRecipeBuilder.create().setIngredient(Items.SPORE_BLOSSOM).addOutput(ModItems.ORGANIC_MATTER.get(), 1, 2).addOutput(ModItems.BIO_LUMENS.get(), 3, 5).unlockedBy(Items.SPORE_BLOSSOM).save(consumer);
		DecomposerRecipeBuilder.create().setIngredient(Items.BROWN_MUSHROOM).addOutput(ModItems.ORGANIC_MATTER.get(), 1, 2).unlockedBy(Items.BROWN_MUSHROOM).save(consumer);
		DecomposerRecipeBuilder.create().setIngredient(Items.RED_MUSHROOM).addOutput(ModItems.ORGANIC_MATTER.get(), 1, 2).unlockedBy(Items.RED_MUSHROOM).save(consumer);
		DecomposerRecipeBuilder.create().setIngredient(Items.CRIMSON_FUNGUS).addOutput(ModItems.ORGANIC_MATTER.get(), 1, 2).addOutput(ModItems.EXOTIC_DUST.get(), 0, 1).unlockedBy(Items.CRIMSON_FUNGUS).save(consumer);
		DecomposerRecipeBuilder.create().setIngredient(Items.WARPED_FUNGUS).addOutput(ModItems.ORGANIC_MATTER.get(), 1, 2).addOutput(ModItems.EXOTIC_DUST.get(), 0, 1).unlockedBy(Items.WARPED_FUNGUS).save(consumer);
		DecomposerRecipeBuilder.create().setIngredient(Items.CRIMSON_ROOTS).addOutput(ModItems.ORGANIC_MATTER.get(), 1, 2).addOutput(ModItems.EXOTIC_DUST.get(), 0, 1).unlockedBy(Items.CRIMSON_ROOTS).save(consumer);
		DecomposerRecipeBuilder.create().setIngredient(Items.WARPED_ROOTS).addOutput(ModItems.ORGANIC_MATTER.get(), 1, 2).addOutput(ModItems.EXOTIC_DUST.get(), 0, 1).unlockedBy(Items.WARPED_ROOTS).save(consumer);
		DecomposerRecipeBuilder.create().setIngredient(Items.NETHER_SPROUTS).addOutput(ModItems.ORGANIC_MATTER.get(), 1, 2).unlockedBy(Items.NETHER_SPROUTS).save(consumer);
		DecomposerRecipeBuilder.create().setIngredient(Items.SUGAR_CANE).addOutput(ModItems.ORGANIC_MATTER.get(), 1, 2).addOutput(Items.SUGAR, 1, 2).unlockedBy(Items.SUGAR_CANE).save(consumer);
		DecomposerRecipeBuilder.create().setIngredient(Items.KELP).addOutput(ModItems.ORGANIC_MATTER.get(), 1, 2).unlockedBy(Items.KELP).save(consumer);
		DecomposerRecipeBuilder.create().setIngredient(Items.BAMBOO).addOutput(ModItems.ORGANIC_MATTER.get(), 1, 2).unlockedBy(Items.BAMBOO).save(consumer);
		DecomposerRecipeBuilder.create().setIngredient(Items.CHORUS_FLOWER).addOutput(ModItems.MINERAL_FRAGMENT.get(), 3, 5).addOutput(ModItems.EXOTIC_DUST.get(), 2, 4).addOutput(ModItems.ORGANIC_MATTER.get(), 2, 5).unlockedBy(Items.CHORUS_FLOWER).save(consumer);
		DecomposerRecipeBuilder.create().setIngredient(Items.CLAY).addOutput(ModItems.STONE_POWDER.get(), 1, 2).addOutput(ModItems.MINERAL_FRAGMENT.get(), 1, 2).unlockedBy(Items.CLAY).save(consumer);
		DecomposerRecipeBuilder.create().setIngredient(Items.GLOWSTONE).addOutput(ModItems.STONE_POWDER.get(), 2, 4).addOutput(ModItems.EXOTIC_DUST.get(), 1, 4).addOutput(ModItems.BIO_LUMENS.get(), -4, 4).unlockedBy(Items.GLOWSTONE).save(consumer);
		DecomposerRecipeBuilder.create().setIngredient(Items.GLOW_LICHEN).addOutput(ModItems.BIO_LUMENS.get(), 1, 2).addOutput(ModItems.ORGANIC_MATTER.get(), 0, 1).unlockedBy(Items.GLOW_LICHEN).save(consumer);
		DecomposerRecipeBuilder.create().setIngredient(Items.DRAGON_EGG).addOutput(ModItems.EXOTIC_DUST.get(), 97, 128).addOutput(ModItems.BIO_LUMENS.get(), 6, 10).addOutput(ModItems.MINERAL_FRAGMENT.get(), 17, 23).unlockedBy(Items.DRAGON_EGG).save(consumer);
		DecomposerRecipeBuilder.create().setIngredient(Items.TURTLE_EGG).addOutput(ModItems.MINERAL_FRAGMENT.get(), 1, 2).addOutput(ModItems.ORGANIC_MATTER.get(), 0, 1).unlockedBy(Items.TURTLE_EGG).save(consumer);
		DecomposerRecipeBuilder.create().setIngredient(Items.TUBE_CORAL).addOutput(ModItems.ORGANIC_MATTER.get(), 1).unlockedBy(Items.TUBE_CORAL).save(consumer);
		DecomposerRecipeBuilder.create().setIngredient(Items.BRAIN_CORAL).addOutput(ModItems.ORGANIC_MATTER.get(), 1).unlockedBy(Items.BRAIN_CORAL).save(consumer);
		DecomposerRecipeBuilder.create().setIngredient(Items.BUBBLE_CORAL).addOutput(ModItems.ORGANIC_MATTER.get(), 1).unlockedBy(Items.BUBBLE_CORAL).save(consumer);
		DecomposerRecipeBuilder.create().setIngredient(Items.FIRE_CORAL).addOutput(ModItems.ORGANIC_MATTER.get(), 1).unlockedBy(Items.FIRE_CORAL).save(consumer);
		DecomposerRecipeBuilder.create().setIngredient(Items.HORN_CORAL).addOutput(ModItems.ORGANIC_MATTER.get(), 1).unlockedBy(Items.HORN_CORAL).save(consumer);
		DecomposerRecipeBuilder.create().setIngredient(Items.DEAD_BRAIN_CORAL).addOutput(ModItems.STONE_POWDER.get(), 1).unlockedBy(Items.DEAD_BRAIN_CORAL).save(consumer);
		DecomposerRecipeBuilder.create().setIngredient(Items.DEAD_BUBBLE_CORAL).addOutput(ModItems.STONE_POWDER.get(), 1).unlockedBy(Items.DEAD_BUBBLE_CORAL).save(consumer);
		DecomposerRecipeBuilder.create().setIngredient(Items.DEAD_FIRE_CORAL).addOutput(ModItems.STONE_POWDER.get(), 1).unlockedBy(Items.DEAD_FIRE_CORAL).save(consumer);
		DecomposerRecipeBuilder.create().setIngredient(Items.DEAD_HORN_CORAL).addOutput(ModItems.STONE_POWDER.get(), 1).unlockedBy(Items.DEAD_HORN_CORAL).save(consumer);
		DecomposerRecipeBuilder.create().setIngredient(Items.DEAD_TUBE_CORAL).addOutput(ModItems.STONE_POWDER.get(), 1).unlockedBy(Items.DEAD_TUBE_CORAL).save(consumer);
		DecomposerRecipeBuilder.create().setIngredient(Items.TUBE_CORAL_FAN).addOutput(ModItems.ORGANIC_MATTER.get(), 1).unlockedBy(Items.TUBE_CORAL_FAN).save(consumer);
		DecomposerRecipeBuilder.create().setIngredient(Items.BRAIN_CORAL_FAN).addOutput(ModItems.ORGANIC_MATTER.get(), 1).unlockedBy(Items.BRAIN_CORAL_FAN).save(consumer);
		DecomposerRecipeBuilder.create().setIngredient(Items.BUBBLE_CORAL_FAN).addOutput(ModItems.ORGANIC_MATTER.get(), 1).unlockedBy(Items.BUBBLE_CORAL_FAN).save(consumer);
		DecomposerRecipeBuilder.create().setIngredient(Items.FIRE_CORAL_FAN).addOutput(ModItems.ORGANIC_MATTER.get(), 1).unlockedBy(Items.FIRE_CORAL_FAN).save(consumer);
		DecomposerRecipeBuilder.create().setIngredient(Items.HORN_CORAL_FAN).addOutput(ModItems.ORGANIC_MATTER.get(), 1).unlockedBy(Items.HORN_CORAL_FAN).save(consumer);
		DecomposerRecipeBuilder.create().setIngredient(Items.DEAD_TUBE_CORAL_FAN).addOutput(ModItems.STONE_POWDER.get(), 1).unlockedBy(Items.DEAD_TUBE_CORAL_FAN).save(consumer);
		DecomposerRecipeBuilder.create().setIngredient(Items.DEAD_BRAIN_CORAL_FAN).addOutput(ModItems.STONE_POWDER.get(), 1).unlockedBy(Items.DEAD_BRAIN_CORAL_FAN).save(consumer);
		DecomposerRecipeBuilder.create().setIngredient(Items.DEAD_BUBBLE_CORAL_FAN).addOutput(ModItems.STONE_POWDER.get(), 1).unlockedBy(Items.DEAD_BUBBLE_CORAL_FAN).save(consumer);
		DecomposerRecipeBuilder.create().setIngredient(Items.DEAD_FIRE_CORAL_FAN).addOutput(ModItems.STONE_POWDER.get(), 1).unlockedBy(Items.DEAD_FIRE_CORAL_FAN).save(consumer);
		DecomposerRecipeBuilder.create().setIngredient(Items.DEAD_HORN_CORAL_FAN).addOutput(ModItems.STONE_POWDER.get(), 1).unlockedBy(Items.DEAD_HORN_CORAL_FAN).save(consumer);
		DecomposerRecipeBuilder.create().setIngredient(Items.REDSTONE).addOutput(ModItems.BIO_LUMENS.get(), -2, 1).addOutput(ModItems.MINERAL_FRAGMENT.get(), 0, 1).unlockedBy(Items.REDSTONE).save(consumer);
		DecomposerRecipeBuilder.create().setIngredient(Items.SLIME_BLOCK).addOutput(ModItems.REGENERATIVE_FLUID.get(), 27, 45).addOutput(ModItems.BILE.get(), 10, 18).unlockedBy(Items.SLIME_BLOCK).save(consumer);
		DecomposerRecipeBuilder.create().setIngredient(Items.TURTLE_HELMET).addOutput(ModItems.TOUGH_FIBERS.get(), 15, 25).addOutput(ModItems.MINERAL_FRAGMENT.get(), 9, 15).unlockedBy(Items.TURTLE_HELMET).save(consumer);
		DecomposerRecipeBuilder.create().setIngredient(Items.SCUTE).addOutput(ModItems.TOUGH_FIBERS.get(), 3, 5).addOutput(ModItems.MINERAL_FRAGMENT.get(), 1, 3).unlockedBy(Items.SCUTE).save(consumer);
		DecomposerRecipeBuilder.create().setIngredient(Items.APPLE).addOutput(ModItems.ORGANIC_MATTER.get(), 1, 2).unlockedBy(Items.APPLE).save(consumer);
		DecomposerRecipeBuilder.create().setIngredient(Items.DIAMOND).addOutput(ModItems.GEM_FRAGMENTS.get(), 4, 8).unlockedBy(Items.DIAMOND).save(consumer);
		DecomposerRecipeBuilder.create().setIngredient(Items.EMERALD).addOutput(ModItems.GEM_FRAGMENTS.get(), 5, 9).unlockedBy(Items.EMERALD).save(consumer);
		DecomposerRecipeBuilder.create().setIngredient(Items.LAPIS_LAZULI).addOutput(ModItems.GEM_FRAGMENTS.get(), 0, 1).addOutput(ModItems.EXOTIC_DUST.get(), 0, 1).unlockedBy(Items.LAPIS_LAZULI).save(consumer);
		DecomposerRecipeBuilder.create().setIngredient(Items.QUARTZ).addOutput(ModItems.GEM_FRAGMENTS.get(), 1, 2).addOutput(ModItems.MINERAL_FRAGMENT.get(), 1, 2).unlockedBy(Items.QUARTZ).save(consumer);
		DecomposerRecipeBuilder.create().setIngredient(Items.AMETHYST_SHARD).addOutput(ModItems.GEM_FRAGMENTS.get(), 3, 5).unlockedBy(Items.AMETHYST_SHARD).save(consumer);
		DecomposerRecipeBuilder.create().setIngredient(Items.RAW_IRON).addOutput(ModItems.MINERAL_FRAGMENT.get(), 5, 9).addOutput(ModItems.STONE_POWDER.get(), 1, 2).unlockedBy(Items.RAW_IRON).save(consumer);
		DecomposerRecipeBuilder.create().setIngredient(Items.IRON_INGOT).addOutput(ModItems.MINERAL_FRAGMENT.get(), 5, 9).unlockedBy(Items.IRON_INGOT).save(consumer);
		DecomposerRecipeBuilder.create().setIngredient(Items.RAW_COPPER).addOutput(ModItems.MINERAL_FRAGMENT.get(), 5, 9).addOutput(ModItems.STONE_POWDER.get(), 1, 2).unlockedBy(Items.RAW_COPPER).save(consumer);
		DecomposerRecipeBuilder.create().setIngredient(Items.COPPER_INGOT).addOutput(ModItems.MINERAL_FRAGMENT.get(), 5, 9).unlockedBy(Items.COPPER_INGOT).save(consumer);
		DecomposerRecipeBuilder.create().setIngredient(Items.RAW_GOLD).addOutput(ModItems.MINERAL_FRAGMENT.get(), 5, 9).addOutput(ModItems.STONE_POWDER.get(), 1, 2).unlockedBy(Items.RAW_GOLD).save(consumer);
		DecomposerRecipeBuilder.create().setIngredient(Items.GOLD_INGOT).addOutput(ModItems.MINERAL_FRAGMENT.get(), 5, 9).unlockedBy(Items.GOLD_INGOT).save(consumer);
		DecomposerRecipeBuilder.create().setIngredient(Items.NETHERITE_INGOT).addOutput(ModItems.MINERAL_FRAGMENT.get(), 43, 72).unlockedBy(Items.NETHERITE_INGOT).save(consumer);
		DecomposerRecipeBuilder.create().setIngredient(Items.NETHERITE_SCRAP).addOutput(ModItems.MINERAL_FRAGMENT.get(), 5, 9).unlockedBy(Items.NETHERITE_SCRAP).save(consumer);
		DecomposerRecipeBuilder.create().setIngredient(Tags.Items.STRING).addOutput(ModItems.MINERAL_FRAGMENT.get(), -1, 1).unlockedBy(Tags.Items.STRING).save(consumer);
		DecomposerRecipeBuilder.create().setIngredient(Tags.Items.FEATHERS).addOutput(ModItems.TOUGH_FIBERS.get(), 1, 2).addOutput(ModItems.MINERAL_FRAGMENT.get(), 0, 1).unlockedBy(Tags.Items.FEATHERS).save(consumer);
		DecomposerRecipeBuilder.create().setIngredient(Items.FLINT).addOutput(ModItems.STONE_POWDER.get(), 1).addOutput(ModItems.MINERAL_FRAGMENT.get(), 0, 1).unlockedBy(Items.FLINT).save(consumer);
		DecomposerRecipeBuilder.create().setIngredient(Items.PORKCHOP).addOutput(ModItems.FLESH_BITS.get(), 3, 5).addOutput(ModItems.BONE_FRAGMENTS.get(), 2, 3).addOutput(ModItems.ELASTIC_FIBERS.get(), 1, 2).unlockedBy(Items.PORKCHOP).save(consumer);
		DecomposerRecipeBuilder.create().setIngredient(Items.GOLDEN_APPLE).addOutput(ModItems.MINERAL_FRAGMENT.get(), 37, 63).addOutput(ModItems.ORGANIC_MATTER.get(), 4, 6).unlockedBy(Items.GOLDEN_APPLE).save(consumer);
		DecomposerRecipeBuilder.create().setIngredient(Items.ENCHANTED_GOLDEN_APPLE).addOutput(ModItems.MINERAL_FRAGMENT.get(), 43, 72).addOutput(ModItems.REGENERATIVE_FLUID.get(), 12, 20).addOutput(ModItems.EXOTIC_DUST.get(), 6, 10).unlockedBy(Items.ENCHANTED_GOLDEN_APPLE).save(consumer);
		DecomposerRecipeBuilder.create().setIngredient(Tags.Items.LEATHER).addOutput(ModItems.TOUGH_FIBERS.get(), 1, 4).unlockedBy(Tags.Items.LEATHER).save(consumer);
		DecomposerRecipeBuilder.create().setIngredient(Items.CLAY_BALL).addOutput(ModItems.STONE_POWDER.get(), 1, 2).addOutput(ModItems.MINERAL_FRAGMENT.get(), 0, 1).unlockedBy(Items.CLAY_BALL).save(consumer);
		DecomposerRecipeBuilder.create().setIngredient(Items.DRIED_KELP_BLOCK).addOutput(ModItems.ORGANIC_MATTER.get(), 1, 3).unlockedBy(Items.DRIED_KELP_BLOCK).save(consumer);
		DecomposerRecipeBuilder.create().setIngredient(Items.SLIME_BALL).addOutput(ModItems.REGENERATIVE_FLUID.get(), 3, 5).addOutput(ModItems.BILE.get(), 1, 2).unlockedBy(Items.SLIME_BALL).save(consumer);
		DecomposerRecipeBuilder.create().setIngredient(Tags.Items.EGGS).addOutput(ModItems.MINERAL_FRAGMENT.get(), 1).addOutput(ModItems.ORGANIC_MATTER.get(), 0, 2).unlockedBy(Tags.Items.EGGS).save(consumer);
		DecomposerRecipeBuilder.create().setIngredient(Items.GLOWSTONE_DUST).addOutput(ModItems.STONE_POWDER.get(), 1).addOutput(ModItems.EXOTIC_DUST.get(), 0, 1).addOutput(ModItems.BIO_LUMENS.get(), -1, 1).unlockedBy(Items.GLOWSTONE_DUST).save(consumer);
		DecomposerRecipeBuilder.create().setIngredient(Items.COD).addOutput(ModItems.FLESH_BITS.get(), 2, 4).addOutput(ModItems.BONE_FRAGMENTS.get(), 1, 2).addOutput(ModItems.ELASTIC_FIBERS.get(), 1, 2).unlockedBy(Items.COD).save(consumer);
		DecomposerRecipeBuilder.create().setIngredient(Items.SALMON).addOutput(ModItems.FLESH_BITS.get(), 2, 4).addOutput(ModItems.BONE_FRAGMENTS.get(), 1, 2).addOutput(ModItems.ELASTIC_FIBERS.get(), 1, 2).unlockedBy(Items.SALMON).save(consumer);
		DecomposerRecipeBuilder.create().setIngredient(Items.TROPICAL_FISH).addOutput(ModItems.FLESH_BITS.get(), 2, 4).addOutput(ModItems.BONE_FRAGMENTS.get(), 1, 2).addOutput(ModItems.ELASTIC_FIBERS.get(), 1, 2).unlockedBy(Items.TROPICAL_FISH).save(consumer);
		DecomposerRecipeBuilder.create().setIngredient(Items.PUFFERFISH).addOutput(ModItems.FLESH_BITS.get(), 2, 4).addOutput(ModItems.BONE_FRAGMENTS.get(), 1, 2).addOutput(ModItems.ELASTIC_FIBERS.get(), 1, 2).addOutput(ModItems.TOXIN_EXTRACT.get(), 1, 3).unlockedBy(Items.PUFFERFISH).save(consumer);
		DecomposerRecipeBuilder.create().setIngredient(Items.INK_SAC).addOutput(ModItems.BILE.get(), 1, 2).addOutput(ModItems.ELASTIC_FIBERS.get(), 1, 2).unlockedBy(Items.INK_SAC).save(consumer);
		DecomposerRecipeBuilder.create().setIngredient(Items.GLOW_INK_SAC).addOutput(ModItems.BIO_LUMENS.get(), 3, 5).addOutput(ModItems.ELASTIC_FIBERS.get(), 1, 2).unlockedBy(Items.GLOW_INK_SAC).save(consumer);
		DecomposerRecipeBuilder.create().setIngredient(Items.COCOA_BEANS).addOutput(ModItems.ORGANIC_MATTER.get(), 2, 4).unlockedBy(Items.COCOA_BEANS).save(consumer);
		DecomposerRecipeBuilder.create().setIngredient(Items.BONE_MEAL).addOutput(ModItems.BONE_FRAGMENTS.get(), 1, 2).unlockedBy(Items.BONE_MEAL).save(consumer);
		DecomposerRecipeBuilder.create().setIngredient(Tags.Items.BONES).addOutput(ModItems.BONE_FRAGMENTS.get(), 3, 6).unlockedBy(Tags.Items.BONES).save(consumer);
		DecomposerRecipeBuilder.create().setIngredient(Items.CAKE).addOutput(ModItems.ORGANIC_MATTER.get(), 10, 18).unlockedBy(Items.CAKE).save(consumer);
		DecomposerRecipeBuilder.create().setIngredient(Items.COOKIE).addOutput(ModItems.ORGANIC_MATTER.get(), 2, 4).unlockedBy(Items.COOKIE).save(consumer);
		DecomposerRecipeBuilder.create().setIngredient(Items.MELON_SLICE).addOutput(ModItems.ORGANIC_MATTER.get(), 2, 4).unlockedBy(Items.MELON_SLICE).save(consumer);
		DecomposerRecipeBuilder.create().setIngredient(Items.DRIED_KELP).addOutput(ModItems.ORGANIC_MATTER.get(), -2, 2).unlockedBy(Items.DRIED_KELP).save(consumer);
		DecomposerRecipeBuilder.create().setIngredient(Tags.Items.SEEDS).addOutput(ModItems.ORGANIC_MATTER.get(), 1, 2).unlockedBy(Tags.Items.SEEDS).save(consumer);
		DecomposerRecipeBuilder.create().setIngredient(Items.BEEF).addOutput(ModItems.FLESH_BITS.get(), 3, 6).unlockedBy(Items.BEEF).save(consumer);
		DecomposerRecipeBuilder.create().setIngredient(Items.CHICKEN).addOutput(ModItems.FLESH_BITS.get(), 3, 4).addOutput(ModItems.BONE_FRAGMENTS.get(), 2, 4).addOutput(ModItems.ELASTIC_FIBERS.get(), 2, 3).unlockedBy(Items.CHICKEN).save(consumer);
		DecomposerRecipeBuilder.create().setIngredient(Items.ROTTEN_FLESH).addOutput(ModItems.FLESH_BITS.get(), 1, 3).addOutput(ModItems.ELASTIC_FIBERS.get(), 0, 1).unlockedBy(Items.ROTTEN_FLESH).save(consumer);
		DecomposerRecipeBuilder.create().setIngredient(Items.ENDER_PEARL).addOutput(ModItems.EXOTIC_DUST.get(), 2, 3).unlockedBy(Items.ENDER_PEARL).save(consumer);
		DecomposerRecipeBuilder.create().setIngredient(Items.BLAZE_ROD).addOutput(ModItems.BIO_LUMENS.get(), 2, 4).addOutput(ModItems.EXOTIC_DUST.get(), 2).unlockedBy(Items.BLAZE_ROD).save(consumer);
		DecomposerRecipeBuilder.create().setIngredient(Items.BLAZE_POWDER).addOutput(ModItems.BIO_LUMENS.get(), 1, 2).addOutput(ModItems.EXOTIC_DUST.get(), 1).unlockedBy(Items.BLAZE_POWDER).save(consumer);
		DecomposerRecipeBuilder.create().setIngredient(Items.GHAST_TEAR).addOutput(ModItems.HORMONE_SECRETION.get(), 1, 2).addOutput(ModItems.BILE.get(), 1, 2).addOutput(ModItems.EXOTIC_DUST.get(), 1, 2).unlockedBy(Items.GHAST_TEAR).save(consumer);
		DecomposerRecipeBuilder.create().setIngredient(Items.GOLD_NUGGET).addOutput(ModItems.MINERAL_FRAGMENT.get(), 0, 1).unlockedBy(Items.GOLD_NUGGET).save(consumer);
		DecomposerRecipeBuilder.create().setIngredient(Items.NETHER_WART).addOutput(ModItems.ORGANIC_MATTER.get(), 1, 2).addOutput(ModItems.EXOTIC_DUST.get(), 0, 1).unlockedBy(Items.NETHER_WART).save(consumer);
		DecomposerRecipeBuilder.create().setIngredient(Items.SPIDER_EYE).addOutput(ModItems.BILE.get(), 0, 1).addOutput(ModItems.FLESH_BITS.get(), 1).addOutput(ModItems.ELASTIC_FIBERS.get(), 1, 2).unlockedBy(Items.SPIDER_EYE).save(consumer);
		DecomposerRecipeBuilder.create().setIngredient(Items.FERMENTED_SPIDER_EYE).addOutput(ModItems.FLESH_BITS.get(), 1).addOutput(ModItems.ELASTIC_FIBERS.get(), 1, 2).unlockedBy(Items.FERMENTED_SPIDER_EYE).save(consumer);
		DecomposerRecipeBuilder.create().setIngredient(Items.MAGMA_CREAM).addOutput(ModItems.BILE.get(), 1, 3).addOutput(ModItems.BIO_LUMENS.get(), 1, 3).addOutput(ModItems.VOLATILE_FLUID.get(), 0, 1).unlockedBy(Items.MAGMA_CREAM).save(consumer);
		DecomposerRecipeBuilder.create().setIngredient(Items.ENDER_EYE).addOutput(ModItems.EXOTIC_DUST.get(), 5, 6).unlockedBy(Items.ENDER_EYE).save(consumer);
		DecomposerRecipeBuilder.create().setIngredient(Items.GLISTERING_MELON_SLICE).addOutput(ModItems.MINERAL_FRAGMENT.get(), 3, 6).addOutput(ModItems.ORGANIC_MATTER.get(), 2).unlockedBy(Items.GLISTERING_MELON_SLICE).save(consumer);
		DecomposerRecipeBuilder.create().setIngredient(Items.CARROT).addOutput(ModItems.ORGANIC_MATTER.get(), 1, 2).unlockedBy(Items.CARROT).save(consumer);
		DecomposerRecipeBuilder.create().setIngredient(Items.POTATO).addOutput(ModItems.ORGANIC_MATTER.get(), 1, 2).unlockedBy(Items.POTATO).save(consumer);
		DecomposerRecipeBuilder.create().setIngredient(Items.BAKED_POTATO).addOutput(ModItems.ORGANIC_MATTER.get(), 2, 5).unlockedBy(Items.BAKED_POTATO).save(consumer);
		DecomposerRecipeBuilder.create().setIngredient(Items.POISONOUS_POTATO).addOutput(ModItems.TOXIN_EXTRACT.get(), 2, 4).addOutput(ModItems.ORGANIC_MATTER.get(), 1, 3).unlockedBy(Items.POISONOUS_POTATO).save(consumer);
		DecomposerRecipeBuilder.create().setIngredient(Items.GOLDEN_CARROT).addOutput(ModItems.MINERAL_FRAGMENT.get(), 4, 8).addOutput(ModItems.REGENERATIVE_FLUID.get(), 0, 1).addOutput(ModItems.ORGANIC_MATTER.get(), 2, 4).unlockedBy(Items.GOLDEN_CARROT).save(consumer);
		DecomposerRecipeBuilder.create().setIngredient(Items.SKELETON_SKULL).addOutput(ModItems.BONE_FRAGMENTS.get(), 28, 48).addOutput(ModItems.MINERAL_FRAGMENT.get(), 4, 7).unlockedBy(Items.SKELETON_SKULL).save(consumer);
		DecomposerRecipeBuilder.create().setIngredient(Items.WITHER_SKELETON_SKULL).addOutput(ModItems.BONE_FRAGMENTS.get(), 28, 48).addOutput(ModItems.WITHERING_OOZE.get(), 8, 16).addOutput(ModItems.MINERAL_FRAGMENT.get(), 4, 7).unlockedBy(Items.WITHER_SKELETON_SKULL).save(consumer);
		DecomposerRecipeBuilder.create().setIngredient(Items.PLAYER_HEAD).addOutput(ModItems.FLESH_BITS.get(), 19, 32).addOutput(ModItems.ELASTIC_FIBERS.get(), 5, 9).addOutput(Items.SKELETON_SKULL, 1).unlockedBy(Items.PLAYER_HEAD).save(consumer);
		DecomposerRecipeBuilder.create().setIngredient(Items.ZOMBIE_HEAD).addOutput(ModItems.FLESH_BITS.get(), 14, 24).addOutput(ModItems.ELASTIC_FIBERS.get(), 5, 9).addOutput(Items.SKELETON_SKULL, 1).unlockedBy(Items.ZOMBIE_HEAD).save(consumer);
		DecomposerRecipeBuilder.create().setIngredient(Items.CREEPER_HEAD).addOutput(ModItems.FLESH_BITS.get(), 19, 32).addOutput(ModItems.ELASTIC_FIBERS.get(), 5, 9).addOutput(Items.SKELETON_SKULL, 1).unlockedBy(Items.CREEPER_HEAD).save(consumer);
		DecomposerRecipeBuilder.create().setIngredient(Items.DRAGON_HEAD).addOutput(ModItems.FLESH_BITS.get(), 50).addOutput(ModItems.EXOTIC_DUST.get(), 50).addOutput(ModItems.TOUGH_FIBERS.get(), 25).addOutput(ModItems.MINERAL_FRAGMENT.get(), 20).addOutput(ModItems.BONE_FRAGMENTS.get(), 50).unlockedBy(Items.DRAGON_HEAD).save(consumer);
		DecomposerRecipeBuilder.create().setIngredient(Items.NETHER_STAR).addOutput(ModItems.EXOTIC_DUST.get(), 50).addOutput(ModItems.BIO_LUMENS.get(), 25).addOutput(ModItems.GEM_FRAGMENTS.get(), 20).unlockedBy(Items.NETHER_STAR).save(consumer);
		DecomposerRecipeBuilder.create().setIngredient(Items.PRISMARINE_SHARD).addOutput(ModItems.MINERAL_FRAGMENT.get(), 1, 2).unlockedBy(Items.PRISMARINE_SHARD).save(consumer);
		DecomposerRecipeBuilder.create().setIngredient(Items.PRISMARINE_CRYSTALS).addOutput(ModItems.GEM_FRAGMENTS.get(), 1, 3).addOutput(ModItems.MINERAL_FRAGMENT.get(), 0, 1).addOutput(ModItems.BIO_LUMENS.get(), 0, 1).unlockedBy(Items.PRISMARINE_CRYSTALS).save(consumer);
		DecomposerRecipeBuilder.create().setIngredient(Items.RABBIT).addOutput(ModItems.FLESH_BITS.get(), 3, 6).addOutput(ModItems.BONE_FRAGMENTS.get(), 2, 3).addOutput(ModItems.ELASTIC_FIBERS.get(), 1, 3).unlockedBy(Items.RABBIT).save(consumer);
		DecomposerRecipeBuilder.create().setIngredient(Items.RABBIT_FOOT).addOutput(ModItems.ELASTIC_FIBERS.get(), 3, 5).addOutput(ModItems.FLESH_BITS.get(), 2, 3).addOutput(ModItems.BONE_FRAGMENTS.get(), 1, 2).unlockedBy(Items.RABBIT_FOOT).save(consumer);
		DecomposerRecipeBuilder.create().setIngredient(Items.RABBIT_HIDE).addOutput(ModItems.TOUGH_FIBERS.get(), 0, 1).unlockedBy(Items.RABBIT_HIDE).save(consumer);
		DecomposerRecipeBuilder.create().setIngredient(Items.MUTTON).addOutput(ModItems.FLESH_BITS.get(), 2, 4).addOutput(ModItems.ELASTIC_FIBERS.get(), 1, 2).addOutput(ModItems.BONE_FRAGMENTS.get(), 2, 3).unlockedBy(Items.MUTTON).save(consumer);
		DecomposerRecipeBuilder.create().setIngredient(Items.CHORUS_FRUIT).addOutput(ModItems.MINERAL_FRAGMENT.get(), 1, 3).addOutput(ModItems.EXOTIC_DUST.get(), 1, 2).addOutput(ModItems.BILE.get(), 0, 1).addOutput(ModItems.ORGANIC_MATTER.get(), 1).unlockedBy(Items.CHORUS_FRUIT).save(consumer);
		DecomposerRecipeBuilder.create().setIngredient(Items.POPPED_CHORUS_FRUIT).addOutput(ModItems.MINERAL_FRAGMENT.get(), 1, 2).addOutput(ModItems.EXOTIC_DUST.get(), 1, 2).addOutput(ModItems.BILE.get(), 0, 1).addOutput(ModItems.ORGANIC_MATTER.get(), 1).unlockedBy(Items.POPPED_CHORUS_FRUIT).save(consumer);
		DecomposerRecipeBuilder.create().setIngredient(Items.BEETROOT).addOutput(ModItems.ORGANIC_MATTER.get(), 2, 4).unlockedBy(Items.BEETROOT).save(consumer);
		DecomposerRecipeBuilder.create().setIngredient(Items.TOTEM_OF_UNDYING).addOutput(ModItems.EXOTIC_DUST.get(), 25).addOutput(ModItems.GEM_FRAGMENTS.get(), 15).addOutput(ModItems.MINERAL_FRAGMENT.get(), 10).unlockedBy(Items.TOTEM_OF_UNDYING).save(consumer);
		DecomposerRecipeBuilder.create().setIngredient(Items.SHULKER_SHELL).addOutput(ModItems.MINERAL_FRAGMENT.get(), 6, 10).addOutput(ModItems.TOUGH_FIBERS.get(), 4, 7).addOutput(ModItems.STONE_POWDER.get(), 1, 2).unlockedBy(Items.SHULKER_SHELL).save(consumer);
		DecomposerRecipeBuilder.create().setIngredient(Items.IRON_NUGGET).addOutput(ModItems.MINERAL_FRAGMENT.get(), 0, 1).unlockedBy(Items.IRON_NUGGET).save(consumer);
		DecomposerRecipeBuilder.create().setIngredient(Items.PHANTOM_MEMBRANE).addOutput(ModItems.TOUGH_FIBERS.get(), 4, 7).addOutput(ModItems.MINERAL_FRAGMENT.get(), 1, 2).addOutput(ModItems.EXOTIC_DUST.get(), 1, 3).unlockedBy(Items.PHANTOM_MEMBRANE).save(consumer);
		DecomposerRecipeBuilder.create().setIngredient(Items.NAUTILUS_SHELL).addOutput(ModItems.MINERAL_FRAGMENT.get(), 6, 10).addOutput(ModItems.TOUGH_FIBERS.get(), 4, 7).unlockedBy(Items.NAUTILUS_SHELL).save(consumer);
		DecomposerRecipeBuilder.create().setIngredient(Items.HEART_OF_THE_SEA).addOutput(ModItems.GEM_FRAGMENTS.get(), 8).addOutput(ModItems.EXOTIC_DUST.get(), 15).addOutput(ModItems.MINERAL_FRAGMENT.get(), 5).unlockedBy(Items.HEART_OF_THE_SEA).save(consumer);
		DecomposerRecipeBuilder.create().setIngredient(Items.GLOW_BERRIES).addOutput(ModItems.BIO_LUMENS.get(), 0, 1).addOutput(ModItems.ORGANIC_MATTER.get(), -1, 1).unlockedBy(Items.GLOW_BERRIES).save(consumer);
		DecomposerRecipeBuilder.create().setIngredient(Items.SHROOMLIGHT).addOutput(ModItems.BIO_LUMENS.get(), 5, 9).addOutput(ModItems.ORGANIC_MATTER.get(), 2, 3).unlockedBy(Items.SHROOMLIGHT).save(consumer);
		DecomposerRecipeBuilder.create().setIngredient(Items.POINTED_DRIPSTONE).addOutput(ModItems.STONE_POWDER.get(), 1, 2).addOutput(ModItems.MINERAL_FRAGMENT.get(), 1, 2).unlockedBy(Items.POINTED_DRIPSTONE).save(consumer);
		DecomposerRecipeBuilder.create().setIngredient(ModItems.MOB_FANG).addOutput(ModItems.MINERAL_FRAGMENT.get(), 2, 4).addOutput(ModItems.BONE_FRAGMENTS.get(), 4, 6).unlockedBy(ModItems.MOB_FANG).save(consumer);
		DecomposerRecipeBuilder.create().setIngredient(ModItems.MOB_CLAW).addOutput(ModItems.MINERAL_FRAGMENT.get(), 3, 5).addOutput(ModItems.TOUGH_FIBERS.get(), 4, 6).unlockedBy(ModItems.MOB_CLAW).save(consumer);
		DecomposerRecipeBuilder.create().setIngredient(ModItems.MOB_SINEW).addOutput(ModItems.ELASTIC_FIBERS.get(), 4, 8).addOutput(ModItems.FLESH_BITS.get(), 1, 2).unlockedBy(ModItems.MOB_SINEW).save(consumer);
		DecomposerRecipeBuilder.create().setIngredient(ModItems.MOB_MARROW).addOutput(ModItems.HORMONE_SECRETION.get(), 2, 5).addOutput(ModItems.BONE_FRAGMENTS.get(), 2, 4).addOutput(ModItems.MINERAL_FRAGMENT.get(), 1, 2).unlockedBy(ModItems.MOB_MARROW).save(consumer);
		DecomposerRecipeBuilder.create().setIngredient(ModItems.WITHERED_MOB_MARROW).addOutput(ModItems.WITHERING_OOZE.get(), 3, 5).addOutput(ModItems.BONE_FRAGMENTS.get(), 2, 4).unlockedBy(ModItems.WITHERED_MOB_MARROW).save(consumer);
		DecomposerRecipeBuilder.create().setIngredient(ModItems.GENERIC_MOB_GLAND).addOutput(ModItems.BILE.get(), 4, 6).addOutput(ModItems.FLESH_BITS.get(), 2, 3).addOutput(ModItems.ELASTIC_FIBERS.get(), 1, 3).unlockedBy(ModItems.GENERIC_MOB_GLAND).save(consumer);
		DecomposerRecipeBuilder.create().setIngredient(ModItems.TOXIN_GLAND).addOutput(ModItems.TOXIN_EXTRACT.get(), 2, 5).addOutput(ModItems.FLESH_BITS.get(), 2, 3).addOutput(ModItems.ELASTIC_FIBERS.get(), 1, 3).unlockedBy(ModItems.TOXIN_GLAND).save(consumer);
		DecomposerRecipeBuilder.create().setIngredient(ModItems.VOLATILE_GLAND).addOutput(ModItems.VOLATILE_FLUID.get(), 2, 5).addOutput(ModItems.FLESH_BITS.get(), 2, 3).addOutput(ModItems.ELASTIC_FIBERS.get(), 1, 3).unlockedBy(ModItems.VOLATILE_GLAND).save(consumer);

		//Special
		DecomposerRecipeBuilder.create().setIngredient(ModItems.LIVING_FLESH)
				.addOutput(ModItems.FLESH_BITS.get(), 3, 6)
				.addOutput(ModItems.EXOTIC_DUST.get(), 0, 2)
				.unlockedBy(ModItems.LIVING_FLESH).save(consumer);

		registerDecomposerRecipesFor119(consumer);
		registerDecomposerRecipesForBiomesOPlenty(consumer);
		registerDecomposerRecipesForAlexsMobs(consumer);
	}

	private void registerDecomposerRecipesFor119(Consumer<FinishedRecipe> consumer) {
		DecomposerRecipeBuilder.create().setIngredient(Items.ECHO_SHARD).addOutput(ModItems.EXOTIC_DUST.get(), 8, 12).unlockedBy(Items.ECHO_SHARD).save(consumer);
		DecomposerRecipeBuilder.create().setIngredient(Items.GOAT_HORN).addOutput(ModItems.MINERAL_FRAGMENT.get(), 5, 7).addOutput(ModItems.TOUGH_FIBERS.get(), 6, 8).unlockedBy(Items.GOAT_HORN).save(consumer);
		DecomposerRecipeBuilder.create().setIngredient(Items.PEARLESCENT_FROGLIGHT).addOutput(ModItems.BIO_LUMENS.get(), 5, 9).addOutput(ModItems.BILE.get(), 2, 3).unlockedBy(Items.PEARLESCENT_FROGLIGHT).save(consumer);
		DecomposerRecipeBuilder.create().setIngredient(Items.VERDANT_FROGLIGHT).addOutput(ModItems.BIO_LUMENS.get(), 5, 9).addOutput(ModItems.BILE.get(), 2, 3).unlockedBy(Items.VERDANT_FROGLIGHT).save(consumer);
		DecomposerRecipeBuilder.create().setIngredient(Items.OCHRE_FROGLIGHT).addOutput(ModItems.BIO_LUMENS.get(), 5, 9).addOutput(ModItems.BILE.get(), 2, 3).unlockedBy(Items.OCHRE_FROGLIGHT).save(consumer);
		DecomposerRecipeBuilder.create().setIngredient(Items.FROGSPAWN).addOutput(ModItems.BILE.get(), 0, 1).unlockedBy(Items.FROGSPAWN).save(consumer);
	}

	private void registerDecomposerRecipesForBiomesOPlenty(Consumer<FinishedRecipe> consumer) {
		DecomposerRecipeBuilder.create().ifModLoaded("biomesoplenty").setIngredient(new DatagenIngredient("biomesoplenty:flesh_tendons")).addOutput(ModItems.ELASTIC_FIBERS.get(), 4, 8).addOutput(ModItems.FLESH_BITS.get(), 1, 2).unlockedBy(ModItems.MOB_SINEW).save(consumer);
	}

	private void registerDecomposerRecipesForAlexsMobs(Consumer<FinishedRecipe> consumer) {
		DecomposerRecipeBuilder.create().ifModLoaded(AlexsMobs.MODID)
				.setIngredient(AMItemRegistry.ROADRUNNER_FEATHER)
				.addOutput(ModItems.TOUGH_FIBERS.get(), 1, 2).addOutput(ModItems.MINERAL_FRAGMENT.get(), 0, 1)
				.unlockedBy(AMItemRegistry.ROADRUNNER_FEATHER).save(consumer);

		DecomposerRecipeBuilder.create().ifModLoaded(AlexsMobs.MODID)
				.setIngredient(AMItemRegistry.BONE_SERPENT_TOOTH)
				.addOutput(ModItems.MINERAL_FRAGMENT.get(), 2, 4).addOutput(ModItems.BONE_FRAGMENTS.get(), 4, 6)
				.unlockedBy(AMItemRegistry.BONE_SERPENT_TOOTH).save(consumer);

		DecomposerRecipeBuilder.create().ifModLoaded(AlexsMobs.MODID)
				.setIngredient(AMItemRegistry.GAZELLE_HORN)
				.addOutput(ModItems.MINERAL_FRAGMENT.get(), 3, 5).addOutput(ModItems.TOUGH_FIBERS.get(), 4, 6)
				.unlockedBy(AMItemRegistry.GAZELLE_HORN).save(consumer);

		DecomposerRecipeBuilder.create().ifModLoaded(AlexsMobs.MODID)
				.setIngredient(AMItemRegistry.CROCODILE_SCUTE)
				.addOutput(ModItems.TOUGH_FIBERS.get(), 3, 5).addOutput(ModItems.MINERAL_FRAGMENT.get(), 1, 3)
				.unlockedBy(AMItemRegistry.CROCODILE_SCUTE).save(consumer);

		DecomposerRecipeBuilder.create().ifModLoaded(AlexsMobs.MODID)
				.setIngredient(AMItemRegistry.MAGGOT)
				.addOutput(ModItems.FLESH_BITS.get(), 0, 1)
				.unlockedBy(AMItemRegistry.MAGGOT).save(consumer);

		DecomposerRecipeBuilder.create().ifModLoaded(AlexsMobs.MODID)
				.setIngredient(AMItemRegistry.BLOOD_SAC)
				.addOutput(ModItems.BILE.get(), 4, 6).addOutput(ModItems.ELASTIC_FIBERS.get(), 1, 3)
				.unlockedBy(AMItemRegistry.BLOOD_SAC).save(consumer);

		DecomposerRecipeBuilder.create().ifModLoaded(AlexsMobs.MODID)
				.setIngredient(AMItemRegistry.RATTLESNAKE_RATTLE)
				.addOutput(ModItems.TOXIN_EXTRACT.get(), 2, 5).addOutput(ModItems.TOUGH_FIBERS.get(), 2, 3).addOutput(ModItems.MINERAL_FRAGMENT.get(), 1, 3)
				.unlockedBy(AMItemRegistry.RATTLESNAKE_RATTLE).save(consumer);

		DecomposerRecipeBuilder.create().ifModLoaded(AlexsMobs.MODID)
				.setIngredient(AMItemRegistry.SHARK_TOOTH)
				.addOutput(ModItems.MINERAL_FRAGMENT.get(), 1, 2).addOutput(ModItems.BONE_FRAGMENTS.get(), 2, 4)
				.unlockedBy(AMItemRegistry.SHARK_TOOTH).save(consumer);

		DecomposerRecipeBuilder.create().ifModLoaded(AlexsMobs.MODID)
				.setIngredient(AMItemRegistry.KOMODO_SPIT)
				.addOutput(ModItems.BILE.get(), 1, 2).addOutput(ModItems.TOXIN_EXTRACT.get(), 0, 1)
				.unlockedBy(AMItemRegistry.KOMODO_SPIT).save(consumer);

		DecomposerRecipeBuilder.create().ifModLoaded(AlexsMobs.MODID)
				.setIngredient(AMItemRegistry.CENTIPEDE_LEG)
				.addOutput(ModItems.MINERAL_FRAGMENT.get(), 6, 10).addOutput(ModItems.TOUGH_FIBERS.get(), 4, 7)
				.unlockedBy(AMItemRegistry.CENTIPEDE_LEG).save(consumer);

		DecomposerRecipeBuilder.create().ifModLoaded(AlexsMobs.MODID)
				.setIngredient(AMItemRegistry.MOOSE_ANTLER)
				.addOutput(ModItems.MINERAL_FRAGMENT.get(), 3, 5).addOutput(ModItems.TOUGH_FIBERS.get(), 4, 6)
				.unlockedBy(AMItemRegistry.MOOSE_ANTLER).save(consumer);

		DecomposerRecipeBuilder.create().ifModLoaded(AlexsMobs.MODID)
				.setIngredient(AMItemRegistry.MOOSE_RIBS)
				.addOutput(ModItems.FLESH_BITS.get(), 2, 4).addOutput(ModItems.ELASTIC_FIBERS.get(), 1, 2)
				.addOutput(ModItems.BONE_FRAGMENTS.get(), 2, 5).addOutput(ModItems.NUTRIENTS.get(), 0, 1)
				.unlockedBy(AMItemRegistry.MOOSE_RIBS).save(consumer);

		DecomposerRecipeBuilder.create().ifModLoaded(AlexsMobs.MODID)
				.setIngredient(AMItemRegistry.RACCOON_TAIL)
				.addOutput(ModItems.MINERAL_FRAGMENT.get(), 2, 4).addOutput(ModItems.FLESH_BITS.get(), 2, 3).addOutput(ModItems.ELASTIC_FIBERS.get(), 1, 3)
				.unlockedBy(AMItemRegistry.RACCOON_TAIL).save(consumer);

		DecomposerRecipeBuilder.create().ifModLoaded(AlexsMobs.MODID)
				.setIngredient(AMItemRegistry.BLOBFISH)
				.addOutput(ModItems.BILE.get(), 2, 4).addOutput(ModItems.FLESH_BITS.get(), 2, 4).addOutput(ModItems.BONE_FRAGMENTS.get(), 1, 2)
				.addOutput(ModItems.ELASTIC_FIBERS.get(), 1, 2)
				.unlockedBy(AMItemRegistry.BLOBFISH).save(consumer);

		DecomposerRecipeBuilder.create().ifModLoaded(AlexsMobs.MODID)
				.setIngredient(AMItemRegistry.SPIKED_SCUTE)
				.addOutput(ModItems.TOUGH_FIBERS.get(), 3, 5).addOutput(ModItems.MINERAL_FRAGMENT.get(), 1, 3)
				.unlockedBy(AMItemRegistry.SPIKED_SCUTE).save(consumer);

		DecomposerRecipeBuilder.create().ifModLoaded(AlexsMobs.MODID)
				.setIngredient(AMItemRegistry.HEMOLYMPH_SAC)
				.addOutput(ModItems.BILE.get(), 4, 6).addOutput(ModItems.ELASTIC_FIBERS.get(), 1, 3).addOutput(ModItems.BIO_LUMENS.get(), 2, 4)
				.unlockedBy(AMItemRegistry.HEMOLYMPH_SAC).save(consumer);

		DecomposerRecipeBuilder.create().ifModLoaded(AlexsMobs.MODID)
				.setIngredient(AMItemRegistry.EMU_FEATHER)
				.addOutput(ModItems.TOUGH_FIBERS.get(), 1, 2).addOutput(ModItems.MINERAL_FRAGMENT.get(), 0, 1)
				.unlockedBy(AMItemRegistry.EMU_FEATHER).save(consumer);

		DecomposerRecipeBuilder.create().ifModLoaded(AlexsMobs.MODID)
				.setIngredient(AMItemRegistry.DROPBEAR_CLAW)
				.addOutput(ModItems.MINERAL_FRAGMENT.get(), 3, 5).addOutput(ModItems.TOUGH_FIBERS.get(), 4, 6)
				.unlockedBy(AMItemRegistry.DROPBEAR_CLAW).save(consumer);

		DecomposerRecipeBuilder.create().ifModLoaded(AlexsMobs.MODID)
				.setIngredient(AMItemRegistry.KANGAROO_MEAT)
				.addOutput(ModItems.FLESH_BITS.get(), 2, 4).addOutput(ModItems.ELASTIC_FIBERS.get(), 1, 2)
				.addOutput(ModItems.BONE_FRAGMENTS.get(), 0, 2).addOutput(ModItems.NUTRIENTS.get(), 0, 1)
				.unlockedBy(AMItemRegistry.KANGAROO_MEAT).save(consumer);

		DecomposerRecipeBuilder.create().ifModLoaded(AlexsMobs.MODID)
				.setIngredient(AMItemRegistry.KANGAROO_HIDE)
				.addOutput(ModItems.TOUGH_FIBERS.get(), 1, 2)
				.unlockedBy(AMItemRegistry.KANGAROO_HIDE).save(consumer);

		DecomposerRecipeBuilder.create().ifModLoaded(AlexsMobs.MODID)
				.setIngredient(AMItemRegistry.CACHALOT_WHALE_TOOTH)
				.addOutput(ModItems.MINERAL_FRAGMENT.get(), 2, 4).addOutput(ModItems.BONE_FRAGMENTS.get(), 4, 6)
				.unlockedBy(AMItemRegistry.CACHALOT_WHALE_TOOTH).save(consumer);

		DecomposerRecipeBuilder.create().ifModLoaded(AlexsMobs.MODID)
				.setIngredient(AMItemRegistry.VOID_WORM_MANDIBLE)
				.addOutput(ModItems.EXOTIC_DUST.get(), 20, 25).addOutput(ModItems.MINERAL_FRAGMENT.get(), 6, 10).addOutput(ModItems.TOUGH_FIBERS.get(), 4, 7)
				.unlockedBy(AMItemRegistry.VOID_WORM_MANDIBLE).save(consumer);

		DecomposerRecipeBuilder.create().ifModLoaded(AlexsMobs.MODID)
				.setIngredient(AMItemRegistry.VOID_WORM_EYE)
				.addOutput(ModItems.BILE.get(), 5, 10).addOutput(ModItems.EXOTIC_DUST.get(), 40, 50).addOutput(ModItems.MINERAL_FRAGMENT.get(), 6, 10)
				.addOutput(ModItems.ELASTIC_FIBERS.get(), 4, 7)
				.unlockedBy(AMItemRegistry.VOID_WORM_EYE).save(consumer);

		DecomposerRecipeBuilder.create().ifModLoaded(AlexsMobs.MODID)
				.setIngredient(AMItemRegistry.FROSTSTALKER_HORN)
				.addOutput(ModItems.MINERAL_FRAGMENT.get(), 3, 5).addOutput(ModItems.TOUGH_FIBERS.get(), 4, 6)
				.unlockedBy(AMItemRegistry.FROSTSTALKER_HORN).save(consumer);

		DecomposerRecipeBuilder.create().ifModLoaded(AlexsMobs.MODID)
				.setIngredient(AMItemRegistry.SHED_SNAKE_SKIN)
				.addOutput(ModItems.TOUGH_FIBERS.get(), 0, 2)
				.unlockedBy(AMItemRegistry.SHED_SNAKE_SKIN).save(consumer);

		DecomposerRecipeBuilder.create().ifModLoaded(AlexsMobs.MODID)
				.setIngredient(AMItemRegistry.LOST_TENTACLE)
				.addOutput(ModItems.ELASTIC_FIBERS.get(), 8, 15).addOutput(ModItems.FLESH_BITS.get(), 3, 5)
				.unlockedBy(AMItemRegistry.LOST_TENTACLE).save(consumer);

		DecomposerRecipeBuilder.create().ifModLoaded(AlexsMobs.MODID)
				.setIngredient(AMItemRegistry.RAW_CATFISH)
				.addOutput(ModItems.FLESH_BITS.get(), 2, 4).addOutput(ModItems.BONE_FRAGMENTS.get(), 1, 2).addOutput(ModItems.ELASTIC_FIBERS.get(), 1, 2)
				.unlockedBy(AMItemRegistry.RAW_CATFISH).save(consumer);

		DecomposerRecipeBuilder.create().ifModLoaded(AlexsMobs.MODID)
				.setIngredient(AMItemRegistry.FISH_BONES)
				.addOutput(ModItems.BONE_FRAGMENTS.get(), 2, 5)
				.unlockedBy(AMItemRegistry.FISH_BONES).save(consumer);
	}

	private void registerBioForgeRecipes(Consumer<FinishedRecipe> consumer) {
		//////////// MACHINES ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		BioForgeRecipeBuilder.create(new ItemData(ModItems.DECOMPOSER.get()))
				.addIngredient(ModItems.LIVING_FLESH.get())
				.addIngredient(ModItems.FLESH_BITS.get(), 8)
				.addIngredient(ModItems.BONE_FRAGMENTS.get(), 5)
				.addIngredient(ModItems.ELASTIC_FIBERS.get(), 5)
				.addIngredient(ModItems.MINERAL_FRAGMENT.get(), 8)
				.setCategory(ModBioForgeTabs.MACHINES)
				.unlockedBy(ModItems.LIVING_FLESH.get()).save(consumer);

		BioForgeRecipeBuilder.create(new ItemData(ModItems.DIGESTER.get()))
				.addIngredient(ModItems.LIVING_FLESH.get())
				.addIngredient(ModItems.FLESH_BITS.get(), 10)
				.addIngredient(ModItems.BONE_FRAGMENTS.get(), 3)
				.addIngredient(ModItems.ELASTIC_FIBERS.get(), 8)
				.addIngredient(ModItems.BILE.get(), 4)
				.setCategory(ModBioForgeTabs.MACHINES)
				.unlockedBy(ModItems.LIVING_FLESH.get()).save(consumer);

		BioForgeRecipeBuilder.create(new ItemData(ModItems.BIO_LAB.get()))
				.addIngredient(ModItems.LIVING_FLESH.get())
				.addIngredient(ModItems.FLESH_BITS.get(), 5)
				.addIngredient(ModItems.BONE_FRAGMENTS.get(), 8)
				.addIngredient(ModItems.TOUGH_FIBERS.get(), 4)
				.addIngredient(ModItems.EXOTIC_DUST.get(), 2)
				.setCategory(ModBioForgeTabs.MACHINES)
				.unlockedBy(ModItems.LIVING_FLESH.get()).save(consumer);

		BioForgeRecipeBuilder.create(new ItemData(ModItems.BIO_FORGE.get()))
				.addIngredient(ModItems.LIVING_FLESH.get())
				.addIngredient(ModItems.FLESH_BITS.get(), 5)
				.addIngredient(ModItems.BONE_FRAGMENTS.get(), 8)
				.addIngredient(ModItems.ELASTIC_FIBERS.get(), 6)
				.addIngredient(ModItems.EXOTIC_DUST.get(), 2)
				.setCategory(ModBioForgeTabs.MACHINES)
				.unlockedBy(ModItems.LIVING_FLESH.get()).save(consumer);

		BioForgeRecipeBuilder.create(new ItemData(ModItems.PRIMORDIAL_CRADLE.get()))
				.addIngredient(ModItems.LIVING_FLESH.get())
				.addIngredient(ModItems.FLESH_BITS.get(), 5)
				.addIngredient(ModItems.BONE_FRAGMENTS.get(), 8)
				.addIngredient(ModItems.ELASTIC_FIBERS.get(), 4)
				.addIngredient(ModItems.EXOTIC_DUST.get(), 10)
				.setCategory(ModBioForgeTabs.MACHINES)
				.unlockedBy(ModItems.LIVING_FLESH.get()).save(consumer);

		//////////// CONTRAPTIONS ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		BioForgeRecipeBuilder.create(ModItems.TONGUE.get())
				.addIngredient(ModItems.LIVING_FLESH.get())
				.addIngredient(ModItems.FLESH_BITS.get(), 5)
				.addIngredient(ModItems.BONE_FRAGMENTS.get(), 3)
				.addIngredient(ModItems.ELASTIC_FIBERS.get(), 10)
				.setCategory(ModBioForgeTabs.MACHINES)
				.unlockedBy(ModItems.LIVING_FLESH.get()).save(consumer);

		BioForgeRecipeBuilder.create(ModItems.MAW_HOPPER.get())
				.addIngredient(ModItems.LIVING_FLESH.get())
				.addIngredient(ModItems.FLESH_BITS.get(), 5)
				.addIngredient(ModItems.BONE_FRAGMENTS.get(), 3)
				.addIngredient(ModItems.ELASTIC_FIBERS.get(), 10)
				.setCategory(ModBioForgeTabs.MACHINES)
				.unlockedBy(ModItems.LIVING_FLESH.get()).save(consumer);

		BioForgeRecipeBuilder.create(new ItemData(ModItems.FLESHKIN_PRESSURE_PLATE.get()))
				.addIngredient(ModItems.LIVING_FLESH.get())
				.addIngredient(ModItems.BONE_FRAGMENTS.get(), 3)
				.addIngredient(ModItems.FLESH_BITS.get(), 5)
				.addIngredient(ModItems.ELASTIC_FIBERS.get(), 4)
				.addIngredient(ModItems.TOUGH_FIBERS.get(), 10)
				.setCategory(ModBioForgeTabs.MISC)
				.unlockedBy(ModItems.LIVING_FLESH.get()).save(consumer);

		//BioForgeRecipeBuilder.create(new ItemData(ModItems.VOICE_BOX.get()))
		//		.addIngredient(Items.NOTE_BLOCK)
		//		.addIngredient(ModItems.FLESH_BITS.get(), 6)
		//		.addIngredient(ModItems.BONE_FRAGMENTS.get(), 4)
		//		.addIngredient(ModItems.ELASTIC_FIBERS.get(), 8)
		//		.setCategory(ModRecipeBooks.BioForgeCategory.MISC)
		//		.unlockedBy(ModItems.ELASTIC_FIBERS.get()).save(consumer);

		//////////// STORAGE ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		BioForgeRecipeBuilder.create(ModItems.STORAGE_SAC.get())
				.addIngredient(ModItems.FLESH_BITS.get(), 8)
				.addIngredient(ModItems.TOUGH_FIBERS.get(), 5)
				.addIngredient(ModItems.ELASTIC_FIBERS.get(), 8)
				.setCategory(ModBioForgeTabs.MISC)
				.unlockedBy(ModItems.ELASTIC_FIBERS.get()).save(consumer);

		BioForgeRecipeBuilder.create(ModItems.FLESHKIN_CHEST.get())
				.addIngredient(ModItems.LIVING_FLESH.get())
				.addIngredient(ModItems.FLESH_BITS.get(), 10)
				.addIngredient(ModItems.BONE_FRAGMENTS.get(), 12)
				.addIngredient(ModItems.TOUGH_FIBERS.get(), 14)
				.addIngredient(ModItems.ELASTIC_FIBERS.get(), 10)
				.setCategory(ModBioForgeTabs.MISC)
				.unlockedBy(ModItems.LIVING_FLESH.get()).save(consumer);

		//////////// BLOCKS ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		int fleshBlockCost = 8;
		int fleshSlabCost = 4;
		int fleshStairsCost = 6;

		BioForgeRecipeBuilder.create(ModItems.FLESH_BLOCK.get(), 4).addIngredient(ModItems.FLESH_BITS.get(), fleshBlockCost).setCategory(ModBioForgeTabs.BLOCKS).unlockedBy(ModItems.FLESH_BITS.get()).save(consumer);
		BioForgeRecipeBuilder.create(ModItems.FLESH_STAIRS.get(), 4).addIngredient(ModItems.FLESH_BITS.get(), fleshStairsCost).setCategory(ModBioForgeTabs.BLOCKS).unlockedBy(ModItems.FLESH_BITS.get()).save(consumer);
		BioForgeRecipeBuilder.create(ModItems.FLESH_SLAB.get(), 4).addIngredient(ModItems.FLESH_BITS.get(), fleshSlabCost).setCategory(ModBioForgeTabs.BLOCKS).unlockedBy(ModItems.FLESH_BITS.get()).save(consumer);
		BioForgeRecipeBuilder.create(ModItems.FLESH_WALL.get(), 4).addIngredient(ModItems.FLESH_BITS.get(), fleshBlockCost).setCategory(ModBioForgeTabs.BLOCKS).unlockedBy(ModItems.FLESH_BITS.get()).save(consumer);
		BioForgeRecipeBuilder.create(ModItems.PACKED_FLESH_BLOCK.get(), 4).addIngredient(ModItems.FLESH_BITS.get(), fleshBlockCost * 2).addIngredient(ModItems.TOUGH_FIBERS.get(), 4).setCategory(ModBioForgeTabs.BLOCKS).unlockedBy(ModItems.FLESH_BITS.get()).save(consumer);
		BioForgeRecipeBuilder.create(ModItems.PACKED_FLESH_STAIRS.get(), 4).addIngredient(ModItems.FLESH_BITS.get(), fleshStairsCost * 2).addIngredient(ModItems.TOUGH_FIBERS.get(), 4).setCategory(ModBioForgeTabs.BLOCKS).unlockedBy(ModItems.FLESH_BITS.get()).save(consumer);
		BioForgeRecipeBuilder.create(ModItems.PACKED_FLESH_SLAB.get(), 4).addIngredient(ModItems.FLESH_BITS.get(), fleshSlabCost * 2).addIngredient(ModItems.TOUGH_FIBERS.get(), 4).setCategory(ModBioForgeTabs.BLOCKS).unlockedBy(ModItems.FLESH_BITS.get()).save(consumer);
		BioForgeRecipeBuilder.create(ModItems.PACKED_FLESH_WALL.get(), 4).addIngredient(ModItems.FLESH_BITS.get(), fleshBlockCost * 2).addIngredient(ModItems.TOUGH_FIBERS.get(), 4).setCategory(ModBioForgeTabs.BLOCKS).unlockedBy(ModItems.FLESH_BITS.get()).save(consumer);

		BioForgeRecipeBuilder.create(ModItems.FLESH_FENCE.get(), 4)
				.addIngredient(ModItems.FLESH_BITS.get(), fleshBlockCost)
				.addIngredient(ModItems.BONE_FRAGMENTS.get(), 2)
				.addIngredient(ModItems.MINERAL_FRAGMENT.get(), 2)
				.setCategory(ModBioForgeTabs.BLOCKS)
				.unlockedBy(ModItems.FLESH_BITS.get()).save(consumer);

		BioForgeRecipeBuilder.create(ModItems.BONE_SPIKE.get(), 4)
				.addIngredient(ModItems.BONE_FRAGMENTS.get(), 2)
				.addIngredient(ModItems.MINERAL_FRAGMENT.get(), 2)
				.setCategory(ModBioForgeTabs.BLOCKS)
				.unlockedBy(ModItems.BONE_FRAGMENTS.get()).save(consumer);

		BioForgeRecipeBuilder.create(ModItems.FLESH_FENCE_GATE.get(), 4)
				.addIngredient(ModItems.FLESH_BITS.get(), fleshSlabCost)
				.addIngredient(ModItems.BONE_FRAGMENTS.get(), fleshBlockCost)
				.addIngredient(ModItems.MINERAL_FRAGMENT.get(), 4)
				.setCategory(ModBioForgeTabs.BLOCKS)
				.unlockedBy(ModItems.BONE_FRAGMENTS.get()).save(consumer);

		BioForgeRecipeBuilder.create(ModItems.FLESH_DOOR.get())
				.addIngredient(ModItems.FLESH_BITS.get(), 6)
				.addIngredient(ModItems.BONE_FRAGMENTS.get(), 6)
				.addIngredient(ModItems.ELASTIC_FIBERS.get(), 4)
				.addIngredient(ModItems.TOUGH_FIBERS.get(), 2)
				.setCategory(ModBioForgeTabs.BLOCKS)
				.unlockedBy(ModItems.FLESH_BITS.get()).save(consumer);
		
		BioForgeRecipeBuilder.create(ModItems.FULL_FLESH_DOOR.get())
				.addIngredient(ModItems.FLESH_BITS.get(), 6)
				.addIngredient(ModItems.BONE_FRAGMENTS.get(), 6)
				.addIngredient(ModItems.ELASTIC_FIBERS.get(), 4)
				.addIngredient(ModItems.TOUGH_FIBERS.get(), 2)
				.setCategory(ModBioForgeTabs.BLOCKS)
				.unlockedBy(ModItems.FLESH_BITS.get()).save(consumer);

		BioForgeRecipeBuilder.create(new ItemData(new ResourceLocation("dramaticdoors", "tall_flesh_door")))
				.ifModLoaded("dramaticdoors")
				.addIngredient(ModItems.FLESH_BITS.get(), 9)
				.addIngredient(ModItems.BONE_FRAGMENTS.get(), 9)
				.addIngredient(ModItems.ELASTIC_FIBERS.get(), 6)
				.addIngredient(ModItems.TOUGH_FIBERS.get(), 3)
				.setCategory(ModBioForgeTabs.BLOCKS)
				.unlockedBy(ModItems.FLESH_BITS.get()).save(consumer);
		
		BioForgeRecipeBuilder.create(new ItemData(new ResourceLocation("dramaticdoors", "tall_full_flesh_door")))
				.ifModLoaded("dramaticdoors")
				.addIngredient(ModItems.FLESH_BITS.get(), 9)
				.addIngredient(ModItems.BONE_FRAGMENTS.get(), 9)
				.addIngredient(ModItems.ELASTIC_FIBERS.get(), 6)
				.addIngredient(ModItems.TOUGH_FIBERS.get(), 3)
				.setCategory(ModBioForgeTabs.BLOCKS)
				.unlockedBy(ModItems.FLESH_BITS.get()).save(consumer);

		BioForgeRecipeBuilder.create(ModItems.FLESH_IRIS_DOOR.get())
				.addIngredient(ModItems.FLESH_BITS.get(), 4)
				.addIngredient(ModItems.BONE_FRAGMENTS.get(), 4)
				.addIngredient(ModItems.ELASTIC_FIBERS.get(), 2)
				.addIngredient(ModItems.TOUGH_FIBERS.get(), 1)
				.setCategory(ModBioForgeTabs.BLOCKS)
				.unlockedBy(ModItems.FLESH_BITS.get()).save(consumer);

		BioForgeRecipeBuilder.create(ModItems.FLESH_LADDER.get(), 4)
				.addIngredient(ModItems.FLESH_BITS.get(), 2)
				.addIngredient(ModItems.BONE_FRAGMENTS.get(), 10)
				.setCategory(ModBioForgeTabs.BLOCKS)
				.unlockedBy(ModItems.BONE_FRAGMENTS.get()).save(consumer);

		BioForgeRecipeBuilder.create(ModItems.TENDON_CHAIN.get())
				.addIngredient(ModItems.ELASTIC_FIBERS.get(), 8)
				.setCategory(ModBioForgeTabs.BLOCKS)
				.unlockedBy(ModItems.ELASTIC_FIBERS.get()).save(consumer);

		BioForgeRecipeBuilder.create(ModItems.BIO_LANTERN.get())
				.addIngredient(ModItems.BIO_LUMENS.get(), 10)
				.addIngredient(ModItems.FLESH_BITS.get(), 2)
				.addIngredient(ModItems.ELASTIC_FIBERS.get(), 4)
				.setCategory(ModBioForgeTabs.BLOCKS)
				.unlockedBy(ModItems.BIO_LUMENS.get()).save(consumer);

		BioForgeRecipeBuilder.create(Items.SHROOMLIGHT)
				.addIngredient(ModItems.BIO_LUMENS.get(), 10)
				.addIngredient(ModItems.ORGANIC_MATTER.get(), 4)
				.setCategory(ModBioForgeTabs.BLOCKS)
				.unlockedBy(Items.SHROOMLIGHT).save(consumer);

		//////////// WEAPONS ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////

		BioForgeRecipeBuilder.create(new ItemData(ModItems.LONG_CLAWS.get()))
				.addIngredient(ModItems.LIVING_FLESH.get())
				.addIngredient(ModItems.BONE_FRAGMENTS.get(), 4)
				.addIngredient(ModItems.FLESH_BITS.get(), 16)
				.addIngredient(ModItems.TOUGH_FIBERS.get(), 6 * 3)
				.addIngredient(ModItems.MINERAL_FRAGMENT.get(), 5 * 3)
				.setCategory(ModBioForgeTabs.WEAPONS)
				.unlockedBy(ModItems.LIVING_FLESH.get()).save(consumer);

		BioForgeRecipeBuilder.create(new ItemData(ModItems.INJECTOR.get()))
				.addIngredient(ModItems.FLESH_BITS.get(), 20)
				.addIngredient(ModItems.MINERAL_FRAGMENT.get(), 10)
				.addIngredient(ModItems.ELASTIC_FIBERS.get(), 3)
				.addIngredient(ModItems.TOUGH_FIBERS.get(), 5)
				.setCategory(ModBioForgeTabs.WEAPONS)
				.unlockedBy(ModItems.FLESH_BITS.get()).save(consumer);

		BioForgeRecipeBuilder.create(ModItems.BONE_CLEAVER.get())
				.addIngredient(ModItems.BONE_FRAGMENTS.get(), 3 * 4)
				.setCategory(ModBioForgeTabs.WEAPONS)
				.unlockedBy(ModItems.BONE_FRAGMENTS.get()).save(consumer);

		//////////// MISC ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////

		BioForgeRecipeBuilder.create(ModItems.CREATOR_MIX.get())
				.addIngredient(ModItems.FLESH_BITS.get(), 4)
				.addIngredient(ModItems.BONE_FRAGMENTS.get(), 3)
				.addIngredient(ModItems.EXOTIC_DUST.get(), 2)
				.addIngredient(ModItems.NUTRIENTS.get(), 3)
				.setCategory(ModBioForgeTabs.MISC)
				.unlockedBy(ModItems.EXOTIC_DUST.get()).save(consumer);

		BioForgeRecipeBuilder.create(ModItems.FERTILIZER.get())
				.addIngredient(ModItems.NUTRIENTS.get(), 4)
				.addIngredient(ModItems.ORGANIC_MATTER.get(), 4)
				.addIngredient(ModItems.AGEING_SERUM.get())
				.setCategory(ModBioForgeTabs.MISC)
				.unlockedBy(ModItems.AGEING_SERUM.get()).save(consumer);

		BioForgeRecipeBuilder.create(Items.BONE)
				.addIngredient(ModItems.BONE_FRAGMENTS.get(), 5)
				.addIngredient(ModItems.MINERAL_FRAGMENT.get(), 1)
				.addIngredient(ModItems.STONE_POWDER.get(), 1)
				.setCategory(ModBioForgeTabs.MISC)
				.unlockedBy(ModItems.BONE_FRAGMENTS.get()).save(consumer);

		BioForgeRecipeBuilder.create(Items.LEATHER)
				.addIngredient(ModItems.TOUGH_FIBERS.get(), 5)
				.addIngredient(ModItems.ELASTIC_FIBERS.get(), 1)
				.setCategory(ModBioForgeTabs.MISC)
				.unlockedBy(Items.LEATHER).save(consumer);

		BioForgeRecipeBuilder.create(Items.STRING)
				.addIngredient(ModItems.ELASTIC_FIBERS.get(), 2)
				.addIngredient(ModItems.MINERAL_FRAGMENT.get(), 1)
				.setCategory(ModBioForgeTabs.MISC)
				.unlockedBy(Items.STRING).save(consumer);

		BioForgeRecipeBuilder.create(Items.SCUTE)
				.addIngredient(ModItems.TOUGH_FIBERS.get(), 6)
				.addIngredient(ModItems.MINERAL_FRAGMENT.get(), 4)
				.addIngredient(ModItems.STONE_POWDER.get(), 1)
				.setCategory(ModBioForgeTabs.MISC)
				.unlockedBy(Items.SCUTE).save(consumer);

		BioForgeRecipeBuilder.create(Items.NAUTILUS_SHELL)
				.addIngredient(ModItems.MINERAL_FRAGMENT.get(), 11)
				.addIngredient(ModItems.TOUGH_FIBERS.get(), 8)
				.addIngredient(ModItems.STONE_POWDER.get(), 1)
				.setCategory(ModBioForgeTabs.MISC)
				.unlockedBy(Items.NAUTILUS_SHELL).save(consumer);

		BioForgeRecipeBuilder.create(ModItems.MASCOT_BANNER_PATTERNS.get())
				.addIngredient(ModItems.ORGANIC_MATTER.get(), 8)
				.addIngredient(Items.SPIDER_EYE, 1)
				.addIngredient(ModItems.BILE.get(), 4)
				.setCategory(ModBioForgeTabs.MISC)
				.unlockedBy(ModItems.ORGANIC_MATTER.get()).save(consumer);
	}

	private void registerBioLabRecipes(Consumer<FinishedRecipe> consumer) {

		BioLabRecipeBuilder.create(ModItems.ORGANIC_COMPOUND.get())
				.addIngredient(ModItems.BILE.get())
				.addIngredient(ModItems.NUTRIENTS.get())
				.setCraftingTime(2 * 20)
				.unlockedBy(ModItems.BILE.get()).save(consumer);

		BioLabRecipeBuilder.create(ModItems.EXOTIC_COMPOUND.get())
				.addIngredient(ModItems.EXOTIC_DUST.get())
				.addIngredient(ModItems.EXOTIC_DUST.get())
				.addIngredient(ModItems.MINERAL_FRAGMENT.get())
				.setReactant(ModItems.ORGANIC_COMPOUND.get())
				.setCraftingTime(4 * 20)
				.unlockedBy(ModItems.ORGANIC_COMPOUND.get()).save(consumer);

		BioLabRecipeBuilder.create(ModItems.GENETIC_COMPOUND.get())
				.addIngredient(ModItems.HORMONE_SECRETION.get())
				.addIngredient(ModItems.NUTRIENTS.get())
				.setReactant(ModItems.ORGANIC_COMPOUND.get())
				.setCraftingTime(4 * 20)
				.unlockedBy(ModItems.ORGANIC_COMPOUND.get()).save(consumer);

		BioLabRecipeBuilder.create(ModItems.UNSTABLE_COMPOUND.get())
				.addIngredient(ModItems.VOLATILE_FLUID.get())
				.setReactant(ModItems.ORGANIC_COMPOUND.get())
				.setCraftingTime(4 * 20)
				.unlockedBy(ModItems.ORGANIC_COMPOUND.get()).save(consumer);

		BioLabRecipeBuilder.create(ModItems.HEALING_ADDITIVE.get())
				.addIngredient(ModItems.REGENERATIVE_FLUID.get())
				.addIngredient(ModItems.BILE.get())
				.setReactant(ModItems.ORGANIC_COMPOUND.get())
				.setCraftingTime(2 * 20)
				.unlockedBy(ModItems.ORGANIC_COMPOUND.get()).save(consumer);

		BioLabRecipeBuilder.create(ModItems.CORROSIVE_ADDITIVE.get())
				.addIngredient(ModItems.WITHERING_OOZE.get())
				.addIngredient(ModItems.BILE.get())
				.setReactant(ModItems.ORGANIC_COMPOUND.get())
				.setCraftingTime(2 * 20)
				.unlockedBy(ModItems.ORGANIC_COMPOUND.get()).save(consumer);

		BioLabRecipeBuilder.create(ModItems.INSOMNIA_CURE.get())
				.addIngredient(Items.SUGAR)
				.addIngredient(ModItems.BILE.get())
				.setReactant(ModItems.EXOTIC_COMPOUND.get())
				.setCraftingTime(8 * 20)
				.unlockedBy(ModItems.EXOTIC_COMPOUND.get()).save(consumer);

		BioLabRecipeBuilder.create(ModItems.ABSORPTION_BOOST.get())
				.addIngredient(ModItems.HEALING_ADDITIVE.get())
				.addIngredient(ModItems.EXOTIC_DUST.get())
				.addIngredient(ModItems.MINERAL_FRAGMENT.get())
				.setReactant(ModItems.EXOTIC_COMPOUND.get())
				.setCraftingTime(8 * 20)
				.unlockedBy(ModItems.EXOTIC_COMPOUND.get()).save(consumer);

		BioLabRecipeBuilder.create(ModItems.CLEANSING_SERUM.get())
				.addIngredient(ModItems.CORROSIVE_ADDITIVE.get())
				.addIngredient(ModItems.HEALING_ADDITIVE.get())
				.setReactant(ModItems.EXOTIC_COMPOUND.get())
				.setCraftingTime(8 * 20)
				.unlockedBy(ModItems.EXOTIC_COMPOUND.get()).save(consumer);

		BioLabRecipeBuilder.create(ModItems.BREEDING_STIMULANT.get())
				.addIngredient(ModItems.NUTRIENTS.get())
				.addIngredient(ItemTags.FLOWERS)
				.addIngredient(ModItems.HORMONE_SECRETION.get())
				.addIngredient(Items.COCOA_BEANS)
				.setReactant(ModItems.GENETIC_COMPOUND.get())
				.setCraftingTime(6 * 20)
				.unlockedBy(ModItems.GENETIC_COMPOUND.get()).save(consumer);

		BioLabRecipeBuilder.create(ModItems.REJUVENATION_SERUM.get())
				.addIngredient(ModItems.HEALING_ADDITIVE.get())
				.addIngredient(ModItems.NUTRIENTS.get())
				.addIngredient(ModItems.CORROSIVE_ADDITIVE.get())
				.setReactant(ModItems.GENETIC_COMPOUND.get())
				.setCraftingTime(8 * 20)
				.unlockedBy(ModItems.GENETIC_COMPOUND.get()).save(consumer);

		BioLabRecipeBuilder.create(ModItems.AGEING_SERUM.get())
				.addIngredient(ModItems.NUTRIENTS.get())
				.addIngredient(ModItems.NUTRIENTS.get())
				.addIngredient(ModItems.MINERAL_FRAGMENT.get())
				.addIngredient(ModItems.CORROSIVE_ADDITIVE.get())
				.setReactant(ModItems.GENETIC_COMPOUND.get())
				.setCraftingTime(6 * 20)
				.unlockedBy(ModItems.GENETIC_COMPOUND.get()).save(consumer);

		BioLabRecipeBuilder.create(ModItems.ENLARGEMENT_SERUM.get())
				.addIngredient(ModItems.NUTRIENT_PASTE.get())
				.addIngredient(ModItems.HORMONE_SECRETION.get())
				.addIngredient(ModItems.HEALING_ADDITIVE.get())
				.addIngredient(ModItems.MINERAL_FRAGMENT.get())
				.setReactant(ModItems.GENETIC_COMPOUND.get())
				.setCraftingTime(6 * 20)
				.unlockedBy(ModItems.GENETIC_COMPOUND.get()).save(consumer);

		BioLabRecipeBuilder.create(ModItems.SHRINKING_SERUM.get())
				.addIngredient(ModItems.EXOTIC_DUST.get())
				.addIngredient(ModItems.HEALING_ADDITIVE.get())
				.addIngredient(ModItems.CORROSIVE_ADDITIVE.get())
				.addIngredient(ModItems.CORROSIVE_ADDITIVE.get())
				.setReactant(ModItems.GENETIC_COMPOUND.get())
				.setCraftingTime(8 * 20)
				.unlockedBy(ModItems.GENETIC_COMPOUND.get()).save(consumer);
	}

	static final class WorkbenchRecipeBuilder {
		private WorkbenchRecipeBuilder() {}

		public static ShapedRecipeBuilder shaped(ItemLike result) {
			return ShapedRecipeBuilder.shaped(result);
		}

		public static ShapedRecipeBuilder shaped(ItemLike result, int count) {
			return ShapedRecipeBuilder.shaped(result, count);
		}

		public static ShapelessRecipeBuilder shapeless(ItemLike result) {
			return ShapelessRecipeBuilder.shapeless(result);
		}

		public static ShapelessRecipeBuilder shapeless(ItemLike result, int count) {
			return ShapelessRecipeBuilder.shapeless(result, count);
		}
	}

}
