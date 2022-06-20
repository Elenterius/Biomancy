package com.github.elenterius.biomancy.datagen.recipes;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.init.ModItems;
import com.github.elenterius.biomancy.init.ModRecipeBooks;
import com.github.elenterius.biomancy.init.ModTags;
import com.github.elenterius.biomancy.util.FuelUtil;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.recipes.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
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
		SimpleCookingRecipeBuilder.blasting(Ingredient.of(ModItems.STONE_POWDER.get()), Items.GLASS_PANE, 0.1F, 100).unlockedBy(hasName(ModItems.STONE_POWDER.get()), has(ModItems.STONE_POWDER.get())).save(consumer, getBlastingRecipeId(Items.GLASS_PANE));
	}

	private void registerWorkbenchRecipes(Consumer<FinishedRecipe> consumer) {

//		ShapedRecipeBuilder.shaped(ModItems.BONE_SWORD.get())
//				.define('S', Tags.Items.RODS_WOODEN).define('B', Tags.Items.BONES).define('C', ModItems.BONE_SCRAPS.get())
//				.pattern(" B ").pattern("CBC").pattern(" S ")
//				.unlockedBy(hasName(Items.BONE), has(Tags.Items.BONES)).save(consumer);

		ShapedRecipeBuilder.shaped(ModItems.GLASS_VIAL.get(), 8)
				.define('G', Tags.Items.GLASS).define('T', Items.CLAY_BALL)
				.pattern("GTG").pattern("G G").pattern(" G ")
				.unlockedBy(hasName(Items.GLASS), has(Tags.Items.GLASS)).save(consumer);

		ShapelessRecipeBuilder.shapeless(Items.BONE_MEAL).requires(ModItems.BONE_FRAGMENTS.get(), 4).unlockedBy(hasName(ModItems.BONE_FRAGMENTS.get()), has(ModItems.BONE_FRAGMENTS.get())).save(consumer, getConversionRecipeId(Items.BONE_MEAL, ModItems.BONE_FRAGMENTS.get()));

		ShapedRecipeBuilder.shaped(ModItems.FLESH_SLAB.get(), 3 * 2)
				.define('F', ModItems.FLESH_BLOCK.get())
				.pattern("FFF")
				.unlockedBy(hasName(ModItems.FLESH_BLOCK.get()), has(ModItems.FLESH_BLOCK.get())).save(consumer);

		ShapedRecipeBuilder.shaped(ModItems.FLESH_STAIRS.get(), 4)
				.define('F', ModItems.FLESH_BLOCK.get())
				.pattern("F  ")
				.pattern("FF ")
				.pattern("FFF")
				.unlockedBy(hasName(ModItems.FLESH_BLOCK.get()), has(ModItems.FLESH_BLOCK.get())).save(consumer);

//		ShapelessRecipeBuilder.shapeless(ModItems.NECROTIC_FLESH_LUMP.get(), 9)
//				.requires(ModItems.NECROTIC_FLESH_BLOCK.get())
//				.unlockedBy(hasName(ModItems.NECROTIC_FLESH_BLOCK.get()), has(ModItems.NECROTIC_FLESH_BLOCK.get())).save(consumer);
//
//		ShapelessRecipeBuilder.shapeless(ModItems.NECROTIC_FLESH_BLOCK.get())
//				.requires(ModItems.NECROTIC_FLESH_LUMP.get(), 9)
//				.unlockedBy(hasName(ModItems.NECROTIC_FLESH_LUMP.get()), has(ModItems.NECROTIC_FLESH_LUMP.get())).save(consumer);

		ShapelessRecipeBuilder.shapeless(ModItems.FLESH_BLOCK.get())
				.requires(ModItems.FLESH_BITS.get(), 9)
				.unlockedBy(hasName(ModItems.FLESH_BITS.get()), has(ModItems.FLESH_BITS.get())).save(consumer);

		ShapelessRecipeBuilder.shapeless(ModItems.FLESH_BITS.get(), 9)
				.requires(ModItems.FLESH_BLOCK.get())
				.unlockedBy(hasName(ModItems.FLESH_BLOCK.get()), has(ModItems.FLESH_BLOCK.get())).save(consumer);

//		ShapelessRecipeBuilder.shapeless(ModItems.FLESH_BITS.get())
//				.requires(ModItems.NECROTIC_FLESH_LUMP.get()).requires(ModItems.NUTRIENTS.get())
//				.unlockedBy(hasName(ModItems.NECROTIC_FLESH_LUMP.get()), has(ModItems.NECROTIC_FLESH_LUMP.get())).save(consumer, getConversionRecipeId(ModItems.FLESH_BITS.get(), ModItems.NECROTIC_FLESH_LUMP.get()));

//		ShapelessRecipeBuilder.shapeless(ModItems.FLESH_BLOCK.get())
//				.requires(ModItems.NECROTIC_FLESH_BLOCK.get()).requires(ModItems.NUTRIENTS.get(), 6)
//				.unlockedBy(hasName(ModItems.NECROTIC_FLESH_BLOCK.get()), has(ModItems.NECROTIC_FLESH_BLOCK.get())).save(consumer, getConversionRecipeId(ModItems.FLESH_BLOCK.get(), ModItems.NECROTIC_FLESH_BLOCK.get()));

		// machines ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

		ShapedRecipeBuilder.shaped(ModItems.CREATOR.get())
				.define('T', Items.TOTEM_OF_UNDYING)
				.define('M', ModTags.Items.RAW_MEATS)
				.define('B', Tags.Items.BONES)
				.define('E', Items.SPIDER_EYE)
				.pattern("BMB").pattern("BTB").pattern("MEM")
				.unlockedBy(hasName(Items.TOTEM_OF_UNDYING), has(Items.TOTEM_OF_UNDYING)).save(consumer);

		// food ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

		ShapedRecipeBuilder.shaped(ModItems.NUTRIENT_BAR.get())
				.define('N', ModItems.NUTRIENTS.get())
				.define('B', Items.SWEET_BERRIES)
				.define('S', Tags.Items.SEEDS)
				.pattern("SBS").pattern("NNN")
				.unlockedBy(hasName(ModItems.NUTRIENTS.get()), has(ModItems.NUTRIENTS.get())).save(consumer);

//		ShapedRecipeBuilder.shaped(ModItems.PROTEIN_BAR.get())
//				.define('N', ModItems.NUTRIENTS.get())
//				.define('B', ModItems.FLESH_BITS.get())
//				.define('S', Tags.Items.SEEDS)
//				.pattern("SBS").pattern("NNN")
//				.unlockedBy(hasName(ModItems.NUTRIENTS.get()), has(ModItems.NUTRIENTS.get())).save(consumer);

		// misc ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		ShapelessRecipeBuilder.shapeless(Items.DIORITE).requires(Items.COBBLESTONE).requires(ModItems.MINERAL_FRAGMENT.get()).requires(ModItems.STONE_POWDER.get()).unlockedBy(hasName(ModItems.STONE_POWDER.get()), has(ModItems.STONE_POWDER.get())).save(consumer, getConversionRecipeId(Items.DIORITE, ModItems.STONE_POWDER.get()));

		ShapelessRecipeBuilder.shapeless(Items.RED_SAND).requires(Items.SAND).requires(ModItems.MINERAL_FRAGMENT.get(), 2).unlockedBy(hasName(ModItems.MINERAL_FRAGMENT.get()), has(ModItems.MINERAL_FRAGMENT.get())).save(consumer, getConversionRecipeId(Items.RED_SAND, ModItems.MINERAL_FRAGMENT.get()));

		//ShapedRecipeBuilder.shaped(Items.DIRT).define('P', ModItems.ORGANIC_MATTER.get()).define('L', ModItems.STONE_POWDER.get()).pattern("LPL").pattern("PLP").pattern("LPL").unlockedBy(hasName(ModItems.ORGANIC_MATTER.get()), has(ModItems.ORGANIC_MATTER.get())).save(consumer, getConversionRecipeId(Items.DIRT, ModItems.STONE_POWDER.get()));

		ShapelessRecipeBuilder.shapeless(Items.CLAY_BALL).requires(Items.WATER_BUCKET).requires(ModItems.STONE_POWDER.get(), 8).unlockedBy(hasName(ModItems.STONE_POWDER.get()), has(ModItems.STONE_POWDER.get())).save(consumer, getConversionRecipeId(Items.CLAY_BALL, ModItems.STONE_POWDER.get()));

		ShapelessRecipeBuilder.shapeless(Items.GUNPOWDER, 2)
				.requires(Items.CHARCOAL).requires(ModItems.EXOTIC_DUST.get(), 4).requires(Items.BLAZE_POWDER, 2)
				.unlockedBy(hasName(ModItems.EXOTIC_DUST.get()), has(ModItems.EXOTIC_DUST.get())).save(consumer, getConversionRecipeId(Items.GUNPOWDER, ModItems.EXOTIC_DUST.get()));
	}

	private void registerDigestingRecipes(Consumer<FinishedRecipe> consumer) {

		DigesterRecipeBuilder.create(ModItems.NUTRIENTS.get(), FuelUtil.getNutrientsFromFuelItem(ModItems.NUTRIENT_PASTE.get()), "nutrient_paste")
				.setIngredient(ModItems.NUTRIENT_PASTE.get())
				.setCraftingTime(100)
				.unlockedBy(ModItems.NUTRIENT_PASTE.get()).save(consumer);

		DigesterRecipeBuilder.create(ModItems.NUTRIENTS.get(), FuelUtil.getNutrientsFromFuelItem(ModItems.NUTRIENT_BAR.get()), "nutrient_bar")
				.setIngredient(ModItems.NUTRIENT_BAR.get())
				.setCraftingTime(600)
				.unlockedBy(ModItems.NUTRIENT_BAR.get()).save(consumer);

		final int itemCount = FuelUtil.DEFAULT_FUEL_VALUE / FuelUtil.NUTRIENTS_FUEL_VALUE;

		DigesterRecipeBuilder.create(ModItems.NUTRIENTS.get(), 2 * itemCount, "poor_biomass")
				.setIngredient(ModTags.Items.POOR_BIOMASS)
				.setCraftingTime(189)
				.unlockedBy(ModTags.Items.POOR_BIOMASS).save(consumer);

		DigesterRecipeBuilder.create(ModItems.NUTRIENTS.get(), 4 * 2 * itemCount, "average_biomass")
				.setIngredient(ModTags.Items.AVERAGE_BIOMASS)
				.setCraftingTime(351)
				.unlockedBy(ModTags.Items.AVERAGE_BIOMASS).save(consumer);

		DigesterRecipeBuilder.create(ModItems.NUTRIENTS.get(), 4 * 2 * itemCount, "raw_meat")
				.setIngredient(ModTags.Items.RAW_MEATS)
				.setCraftingTime(351)
				.unlockedBy(ModTags.Items.RAW_MEATS).save(consumer);

		DigesterRecipeBuilder.create(ModItems.NUTRIENTS.get(), 6 * 3 * itemCount, "good_biomass")
				.setIngredient(ModTags.Items.GOOD_BIOMASS)
				.setCraftingTime(490)
				.unlockedBy(ModTags.Items.GOOD_BIOMASS).save(consumer);

		DigesterRecipeBuilder.create(ModItems.NUTRIENTS.get(), 6 * 3 * itemCount, "cooked_meat")
				.setIngredient(ModTags.Items.COOKED_MEATS)
				.setCraftingTime(490)
				.unlockedBy(ModTags.Items.COOKED_MEATS).save(consumer);

		DigesterRecipeBuilder.create(ModItems.NUTRIENTS.get(), 8 * 4 * itemCount, "superb_biomass")
				.setIngredient(ModTags.Items.SUPERB_BIOMASS)
				.setCraftingTime(540)
				.unlockedBy(ModTags.Items.SUPERB_BIOMASS).save(consumer);
	}

	private void registerDecomposingRecipes(Consumer<FinishedRecipe> consumer) {
		DecomposerRecipeBuilder.create().setIngredient(Items.GRANITE).addOutput(ModItems.MINERAL_FRAGMENT.get(), 2).addOutput(Items.DIORITE, 1).setCraftingTime(100).unlockedBy(Items.GRANITE).save(consumer);
		DecomposerRecipeBuilder.create().setIngredient(Items.DIORITE).addOutput(ModItems.MINERAL_FRAGMENT.get(), 2).addOutput(Items.COBBLESTONE, 1).setCraftingTime(100).unlockedBy(Items.DIORITE).save(consumer);
		DecomposerRecipeBuilder.create().setIngredient(Items.ANDESITE).addOutput(ModItems.MINERAL_FRAGMENT.get(), 1).setCraftingTime(40).unlockedBy(Items.ANDESITE).save(consumer);
		DecomposerRecipeBuilder.create().setIngredient(Items.CALCITE).addOutput(ModItems.STONE_POWDER.get(), 3).addOutput(ModItems.MINERAL_FRAGMENT.get(), 2).addOutput(ModItems.GEM_FRAGMENTS.get(), 1).setCraftingTime(220).unlockedBy(Items.CALCITE).save(consumer);
		DecomposerRecipeBuilder.create().setIngredient(Items.COARSE_DIRT).addOutput(ModItems.STONE_POWDER.get(), 4).setCraftingTime(120).unlockedBy(Items.COARSE_DIRT).save(consumer);
		DecomposerRecipeBuilder.create().setIngredient(Items.SAND).addOutput(ModItems.STONE_POWDER.get(), 5).setCraftingTime(150).unlockedBy(Items.SAND).save(consumer);
		DecomposerRecipeBuilder.create().setIngredient(Items.RED_SAND).addOutput(ModItems.STONE_POWDER.get(), 5).addOutput(ModItems.MINERAL_FRAGMENT.get(), 4).setCraftingTime(310).unlockedBy(Items.RED_SAND).save(consumer);
		DecomposerRecipeBuilder.create().setIngredient(Items.GRAVEL).addOutput(ModItems.STONE_POWDER.get(), 9).setCraftingTime(270).unlockedBy(Items.GRAVEL).save(consumer);
		DecomposerRecipeBuilder.create().setIngredient(Items.TINTED_GLASS).addOutput(ModItems.GEM_FRAGMENTS.get(), 28).setCraftingTime(1400).unlockedBy(Items.TINTED_GLASS).save(consumer);
		DecomposerRecipeBuilder.create().setIngredient(Items.COBWEB).addOutput(ModItems.ELASTIC_FIBERS.get(), 9).addOutput(ModItems.TOUGH_FIBERS.get(), 9).setCraftingTime(360).unlockedBy(Items.COBWEB).save(consumer);
		DecomposerRecipeBuilder.create().setIngredient(Items.SEA_PICKLE).addOutput(ModItems.BIO_MINERALS.get(), 2).addOutput(ModItems.BIO_LUMENS.get(), 2).setCraftingTime(88).unlockedBy(Items.SEA_PICKLE).save(consumer);
		DecomposerRecipeBuilder.create().setIngredient(Items.WITHER_ROSE).addOutput(ModItems.WITHERING_OOZE.get(), 10).addOutput(ModItems.EXOTIC_DUST.get(), 5).setCraftingTime(500).unlockedBy(Items.WITHER_ROSE).save(consumer);
		DecomposerRecipeBuilder.create().setIngredient(Items.SPORE_BLOSSOM).addOutput(ModItems.BIO_LUMENS.get(), 10).addOutput(ModItems.BILE.get(), 5).addOutput(ModItems.HORMONE_SECRETION.get(), 5).setCraftingTime(400).unlockedBy(Items.SPORE_BLOSSOM).save(consumer);
		DecomposerRecipeBuilder.create().setIngredient(Items.BROWN_MUSHROOM).addOutput(ModItems.BILE.get(), 2).setCraftingTime(32).unlockedBy(Items.BROWN_MUSHROOM).save(consumer);
		DecomposerRecipeBuilder.create().setIngredient(Items.RED_MUSHROOM).addOutput(ModItems.BILE.get(), 2).addOutput(ModItems.TOXIN_EXTRACT.get(), 1).setCraftingTime(52).unlockedBy(Items.RED_MUSHROOM).save(consumer);
		DecomposerRecipeBuilder.create().setIngredient(Items.CRIMSON_FUNGUS).addOutput(ModItems.BILE.get(), 2).setCraftingTime(32).unlockedBy(Items.CRIMSON_FUNGUS).save(consumer);
		DecomposerRecipeBuilder.create().setIngredient(Items.WARPED_FUNGUS).addOutput(ModItems.BILE.get(), 2).addOutput(ModItems.HORMONE_SECRETION.get(), 2).setCraftingTime(72).unlockedBy(Items.WARPED_FUNGUS).save(consumer);
		DecomposerRecipeBuilder.create().setIngredient(Items.CRIMSON_ROOTS).addOutput(ModItems.BILE.get(), 2).setCraftingTime(32).unlockedBy(Items.CRIMSON_ROOTS).save(consumer);
		DecomposerRecipeBuilder.create().setIngredient(Items.WARPED_ROOTS).addOutput(ModItems.BILE.get(), 2).addOutput(ModItems.HORMONE_SECRETION.get(), 2).setCraftingTime(72).unlockedBy(Items.WARPED_ROOTS).save(consumer);
		DecomposerRecipeBuilder.create().setIngredient(Items.NETHER_SPROUTS).addOutput(ModItems.BILE.get(), 1).addOutput(ModItems.HORMONE_SECRETION.get(), 1).setCraftingTime(36).unlockedBy(Items.NETHER_SPROUTS).save(consumer);
		DecomposerRecipeBuilder.create().setIngredient(Items.WEEPING_VINES).addOutput(ModItems.BILE.get(), 2).setCraftingTime(32).unlockedBy(Items.WEEPING_VINES).save(consumer);
		DecomposerRecipeBuilder.create().setIngredient(Items.TWISTING_VINES).addOutput(ModItems.BILE.get(), 2).addOutput(ModItems.HORMONE_SECRETION.get(), 2).setCraftingTime(72).unlockedBy(Items.TWISTING_VINES).save(consumer);
		DecomposerRecipeBuilder.create().setIngredient(Items.CHORUS_FLOWER).addOutput(ModItems.BIO_MINERALS.get(), 5).addOutput(ModItems.EXOTIC_DUST.get(), 4).addOutput(ModItems.BILE.get(), 2).setCraftingTime(342).unlockedBy(Items.CHORUS_FLOWER).save(consumer);
		DecomposerRecipeBuilder.create().setIngredient(Items.CLAY).addOutput(ModItems.STONE_POWDER.get(), 4).addOutput(ModItems.MINERAL_FRAGMENT.get(), 4).setCraftingTime(280).unlockedBy(Items.CLAY).save(consumer);
		DecomposerRecipeBuilder.create().setIngredient(Items.GLOWSTONE).addOutput(ModItems.STONE_POWDER.get(), 8).addOutput(ModItems.EXOTIC_DUST.get(), 4).setCraftingTime(440).unlockedBy(Items.GLOWSTONE).save(consumer);
		DecomposerRecipeBuilder.create().setIngredient(Items.GLOW_LICHEN).addOutput(ModItems.BIO_LUMENS.get(), 2).setCraftingTime(44).unlockedBy(Items.GLOW_LICHEN).save(consumer);
		DecomposerRecipeBuilder.create().setIngredient(Items.DRAGON_EGG).addOutput(ModItems.EXOTIC_DUST.get(), 128).addOutput(ModItems.BIO_LUMENS.get(), 10).addOutput(ModItems.BIO_MINERALS.get(), 10).setCraftingTime(6840).unlockedBy(Items.DRAGON_EGG).save(consumer);
		DecomposerRecipeBuilder.create().setIngredient(Items.TURTLE_EGG).addOutput(ModItems.BIO_MINERALS.get(), 1).addOutput(ModItems.STONE_POWDER.get(), 1).setCraftingTime(52).unlockedBy(Items.TURTLE_EGG).save(consumer);
		DecomposerRecipeBuilder.create().setIngredient(Items.REDSTONE).addOutput(ModItems.STONE_POWDER.get(), 1).addOutput(ModItems.MINERAL_FRAGMENT.get(), 1).setCraftingTime(70).unlockedBy(Items.REDSTONE).save(consumer);
		DecomposerRecipeBuilder.create().setIngredient(Items.SLIME_BLOCK).addOutput(ModItems.REGENERATIVE_FLUID.get(), 45).addOutput(ModItems.BILE.get(), 18).setCraftingTime(1863).unlockedBy(Items.SLIME_BLOCK).save(consumer);
		DecomposerRecipeBuilder.create().setIngredient(Items.HONEY_BLOCK).addOutput(ModItems.REGENERATIVE_FLUID.get(), 8).addOutput(ModItems.BILE.get(), 4).setCraftingTime(344).unlockedBy(Items.HONEY_BLOCK).save(consumer);
		DecomposerRecipeBuilder.create().setIngredient(Items.SADDLE).addOutput(ModItems.TOUGH_FIBERS.get(), 30).setCraftingTime(900).unlockedBy(Items.SADDLE).save(consumer);
		DecomposerRecipeBuilder.create().setIngredient(Items.TURTLE_HELMET).addOutput(ModItems.TOUGH_FIBERS.get(), 26).addOutput(ModItems.BIO_MINERALS.get(), 16).setCraftingTime(1132).unlockedBy(Items.TURTLE_HELMET).save(consumer);
		DecomposerRecipeBuilder.create().setIngredient(Items.SCUTE).addOutput(ModItems.TOUGH_FIBERS.get(), 5).addOutput(ModItems.BIO_MINERALS.get(), 3).setCraftingTime(216).unlockedBy(Items.SCUTE).save(consumer);
		DecomposerRecipeBuilder.create().setIngredient(Items.BOW).addOutput(ModItems.ELASTIC_FIBERS.get(), 1).addOutput(ModItems.TOUGH_FIBERS.get(), 1).setCraftingTime(40).unlockedBy(Items.BOW).save(consumer);
		DecomposerRecipeBuilder.create().setIngredient(Items.DIAMOND).addOutput(ModItems.GEM_FRAGMENTS.get(), 8).setCraftingTime(400).unlockedBy(Items.DIAMOND).save(consumer);
		DecomposerRecipeBuilder.create().setIngredient(Items.EMERALD).addOutput(ModItems.GEM_FRAGMENTS.get(), 9).setCraftingTime(450).unlockedBy(Items.EMERALD).save(consumer);
		DecomposerRecipeBuilder.create().setIngredient(Items.LAPIS_LAZULI).addOutput(ModItems.GEM_FRAGMENTS.get(), 2).setCraftingTime(100).unlockedBy(Items.LAPIS_LAZULI).save(consumer);
		DecomposerRecipeBuilder.create().setIngredient(Items.QUARTZ).addOutput(ModItems.GEM_FRAGMENTS.get(), 2).setCraftingTime(100).unlockedBy(Items.QUARTZ).save(consumer);
		DecomposerRecipeBuilder.create().setIngredient(Items.AMETHYST_SHARD).addOutput(ModItems.GEM_FRAGMENTS.get(), 7).setCraftingTime(350).unlockedBy(Items.AMETHYST_SHARD).save(consumer);
		DecomposerRecipeBuilder.create().setIngredient(Items.RAW_IRON).addOutput(ModItems.MINERAL_FRAGMENT.get(), 9).addOutput(ModItems.STONE_POWDER.get(), 1).setCraftingTime(390).unlockedBy(Items.RAW_IRON).save(consumer);
		DecomposerRecipeBuilder.create().setIngredient(Items.IRON_INGOT).addOutput(ModItems.MINERAL_FRAGMENT.get(), 9).setCraftingTime(360).unlockedBy(Items.IRON_INGOT).save(consumer);
		DecomposerRecipeBuilder.create().setIngredient(Items.RAW_COPPER).addOutput(ModItems.MINERAL_FRAGMENT.get(), 9).addOutput(ModItems.STONE_POWDER.get(), 1).setCraftingTime(390).unlockedBy(Items.RAW_COPPER).save(consumer);
		DecomposerRecipeBuilder.create().setIngredient(Items.COPPER_INGOT).addOutput(ModItems.MINERAL_FRAGMENT.get(), 9).setCraftingTime(360).unlockedBy(Items.COPPER_INGOT).save(consumer);
		DecomposerRecipeBuilder.create().setIngredient(Items.RAW_GOLD).addOutput(ModItems.MINERAL_FRAGMENT.get(), 9).addOutput(ModItems.STONE_POWDER.get(), 1).setCraftingTime(390).unlockedBy(Items.RAW_GOLD).save(consumer);
		DecomposerRecipeBuilder.create().setIngredient(Items.GOLD_INGOT).addOutput(ModItems.MINERAL_FRAGMENT.get(), 9).setCraftingTime(360).unlockedBy(Items.GOLD_INGOT).save(consumer);
		DecomposerRecipeBuilder.create().setIngredient(Items.NETHERITE_INGOT).addOutput(ModItems.MINERAL_FRAGMENT.get(), 72).setCraftingTime(2880).unlockedBy(Items.NETHERITE_INGOT).save(consumer);
		DecomposerRecipeBuilder.create().setIngredient(Items.NETHERITE_SCRAP).addOutput(ModItems.MINERAL_FRAGMENT.get(), 9).setCraftingTime(360).unlockedBy(Items.NETHERITE_SCRAP).save(consumer);
		DecomposerRecipeBuilder.create().setIngredient(Items.STRING).addOutput(ModItems.ELASTIC_FIBERS.get(), 1).addOutput(ModItems.TOUGH_FIBERS.get(), 1).setCraftingTime(40).unlockedBy(Items.STRING).save(consumer);
		DecomposerRecipeBuilder.create().setIngredient(Items.GUNPOWDER).addOutput(ModItems.VOLATILE_FLUID.get(), 1).setCraftingTime(25).unlockedBy(Items.GUNPOWDER).save(consumer);
		DecomposerRecipeBuilder.create().setIngredient(Items.LEATHER_HELMET).addOutput(ModItems.TOUGH_FIBERS.get(), 40).setCraftingTime(1200).unlockedBy(Items.LEATHER_HELMET).save(consumer);
		DecomposerRecipeBuilder.create().setIngredient(Items.LEATHER_CHESTPLATE).addOutput(ModItems.TOUGH_FIBERS.get(), 70).setCraftingTime(2100).unlockedBy(Items.LEATHER_CHESTPLATE).save(consumer);
		DecomposerRecipeBuilder.create().setIngredient(Items.LEATHER_LEGGINGS).addOutput(ModItems.TOUGH_FIBERS.get(), 60).setCraftingTime(1800).unlockedBy(Items.LEATHER_LEGGINGS).save(consumer);
		DecomposerRecipeBuilder.create().setIngredient(Items.LEATHER_BOOTS).addOutput(ModItems.TOUGH_FIBERS.get(), 30).setCraftingTime(900).unlockedBy(Items.LEATHER_BOOTS).save(consumer);
		DecomposerRecipeBuilder.create().setIngredient(Items.FLINT).addOutput(ModItems.STONE_POWDER.get(), 2).setCraftingTime(60).unlockedBy(Items.FLINT).save(consumer);
		DecomposerRecipeBuilder.create().setIngredient(Items.GOLDEN_APPLE).addOutput(ModItems.MINERAL_FRAGMENT.get(), 63).addOutput(ModItems.REGENERATIVE_FLUID.get(), 3).setCraftingTime(2625).unlockedBy(Items.GOLDEN_APPLE).save(consumer);
		DecomposerRecipeBuilder.create().setIngredient(Items.ENCHANTED_GOLDEN_APPLE).addOutput(ModItems.MINERAL_FRAGMENT.get(), 72).addOutput(ModItems.REGENERATIVE_FLUID.get(), 20).addOutput(ModItems.EXOTIC_DUST.get(), 10).setCraftingTime(4080).unlockedBy(Items.ENCHANTED_GOLDEN_APPLE).save(consumer);
		DecomposerRecipeBuilder.create().setIngredient(Items.LEATHER).addOutput(ModItems.TOUGH_FIBERS.get(), 10).setCraftingTime(300).unlockedBy(Items.LEATHER).save(consumer);
		DecomposerRecipeBuilder.create().setIngredient(Items.CLAY_BALL).addOutput(ModItems.STONE_POWDER.get(), 1).addOutput(ModItems.MINERAL_FRAGMENT.get(), 1).setCraftingTime(70).unlockedBy(Items.CLAY_BALL).save(consumer);
		DecomposerRecipeBuilder.create().setIngredient(Items.SLIME_BALL).addOutput(ModItems.REGENERATIVE_FLUID.get(), 5).addOutput(ModItems.BILE.get(), 2).setCraftingTime(207).unlockedBy(Items.SLIME_BALL).save(consumer);
		DecomposerRecipeBuilder.create().setIngredient(Items.GLOWSTONE_DUST).addOutput(ModItems.STONE_POWDER.get(), 2).addOutput(ModItems.EXOTIC_DUST.get(), 1).setCraftingTime(110).unlockedBy(Items.GLOWSTONE_DUST).save(consumer);
		DecomposerRecipeBuilder.create().setIngredient(Items.COD).addOutput(ModItems.FLESH_BITS.get(), 4).addOutput(ModItems.BONE_FRAGMENTS.get(), 2).setCraftingTime(122).unlockedBy(Items.COD).save(consumer);
		DecomposerRecipeBuilder.create().setIngredient(Items.SALMON).addOutput(ModItems.FLESH_BITS.get(), 4).addOutput(ModItems.BONE_FRAGMENTS.get(), 2).setCraftingTime(122).unlockedBy(Items.SALMON).save(consumer);
		DecomposerRecipeBuilder.create().setIngredient(Items.TROPICAL_FISH).addOutput(ModItems.FLESH_BITS.get(), 2).addOutput(ModItems.BONE_FRAGMENTS.get(), 2).setCraftingTime(86).unlockedBy(Items.TROPICAL_FISH).save(consumer);
		DecomposerRecipeBuilder.create().setIngredient(Items.PUFFERFISH).addOutput(ModItems.FLESH_BITS.get(), 2).addOutput(ModItems.BONE_FRAGMENTS.get(), 4).addOutput(ModItems.TOXIN_EXTRACT.get(), 5).setCraftingTime(236).unlockedBy(Items.PUFFERFISH).save(consumer);
		DecomposerRecipeBuilder.create().setIngredient(Items.INK_SAC).addOutput(ModItems.HORMONE_SECRETION.get(), 5).addOutput(ModItems.ELASTIC_FIBERS.get(), 2).setCraftingTime(120).unlockedBy(Items.INK_SAC).save(consumer);
		DecomposerRecipeBuilder.create().setIngredient(Items.GLOW_INK_SAC).addOutput(ModItems.BIO_LUMENS.get(), 5).addOutput(ModItems.HORMONE_SECRETION.get(), 2).addOutput(ModItems.ELASTIC_FIBERS.get(), 1).setCraftingTime(160).unlockedBy(Items.GLOW_INK_SAC).save(consumer);
		DecomposerRecipeBuilder.create().setIngredient(Items.COCOA_BEANS).addOutput(ModItems.BILE.get(), 1).addOutput(ModItems.REGENERATIVE_FLUID.get(), 1).setCraftingTime(51).unlockedBy(Items.COCOA_BEANS).save(consumer);
		DecomposerRecipeBuilder.create().setIngredient(Items.BONE_MEAL).addOutput(ModItems.BONE_FRAGMENTS.get(), 2).setCraftingTime(50).unlockedBy(Items.BONE_MEAL).save(consumer);
		DecomposerRecipeBuilder.create().setIngredient(Items.BEEF).addOutput(ModItems.FLESH_BITS.get(), 6).setCraftingTime(108).unlockedBy(Items.BEEF).save(consumer);
		DecomposerRecipeBuilder.create().setIngredient(Items.CHICKEN).addOutput(ModItems.FLESH_BITS.get(), 4).addOutput(ModItems.BONE_FRAGMENTS.get(), 4).addOutput(ModItems.BILE.get(), 2).setCraftingTime(204).unlockedBy(Items.CHICKEN).save(consumer);
		DecomposerRecipeBuilder.create().setIngredient(Items.ROTTEN_FLESH).addOutput(ModItems.FLESH_BITS.get(), 4).addOutput(ModItems.WITHERING_OOZE.get(), 1).setCraftingTime(97).unlockedBy(Items.ROTTEN_FLESH).save(consumer);
		DecomposerRecipeBuilder.create().setIngredient(Items.ENDER_PEARL).addOutput(ModItems.EXOTIC_DUST.get(), 3).addOutput(ModItems.BILE.get(), 1).setCraftingTime(166).unlockedBy(Items.ENDER_PEARL).save(consumer);
		DecomposerRecipeBuilder.create().setIngredient(Items.BLAZE_ROD).addOutput(ModItems.BIO_LUMENS.get(), 4).addOutput(ModItems.BILE.get(), 2).setCraftingTime(120).unlockedBy(Items.BLAZE_ROD).save(consumer);
		DecomposerRecipeBuilder.create().setIngredient(Items.GHAST_TEAR).addOutput(ModItems.HORMONE_SECRETION.get(), 9).addOutput(ModItems.VOLATILE_FLUID.get(), 5).setCraftingTime(305).unlockedBy(Items.GHAST_TEAR).save(consumer);
		DecomposerRecipeBuilder.create().setIngredient(Items.GOLD_NUGGET).addOutput(ModItems.MINERAL_FRAGMENT.get(), 1).setCraftingTime(40).unlockedBy(Items.GOLD_NUGGET).save(consumer);
		DecomposerRecipeBuilder.create().setIngredient(Items.NETHER_WART).addOutput(ModItems.BILE.get(), 1).setCraftingTime(16).unlockedBy(Items.NETHER_WART).save(consumer);
		DecomposerRecipeBuilder.create().setIngredient(Items.SPIDER_EYE).addOutput(ModItems.BILE.get(), 2).addOutput(ModItems.FLESH_BITS.get(), 1).setCraftingTime(50).unlockedBy(Items.SPIDER_EYE).save(consumer);
		DecomposerRecipeBuilder.create().setIngredient(Items.FERMENTED_SPIDER_EYE).addOutput(ModItems.BILE.get(), 4).addOutput(ModItems.FLESH_BITS.get(), 1).setCraftingTime(82).unlockedBy(Items.FERMENTED_SPIDER_EYE).save(consumer);
		DecomposerRecipeBuilder.create().setIngredient(Items.BLAZE_POWDER).addOutput(ModItems.BIO_LUMENS.get(), 2).addOutput(ModItems.BILE.get(), 1).setCraftingTime(60).unlockedBy(Items.BLAZE_POWDER).save(consumer);
		DecomposerRecipeBuilder.create().setIngredient(Items.MAGMA_CREAM).addOutput(ModItems.BILE.get(), 3).addOutput(ModItems.BIO_LUMENS.get(), 3).addOutput(ModItems.VOLATILE_FLUID.get(), 1).setCraftingTime(139).unlockedBy(Items.MAGMA_CREAM).save(consumer);
		DecomposerRecipeBuilder.create().setIngredient(Items.ENDER_EYE).addOutput(ModItems.EXOTIC_DUST.get(), 5).addOutput(ModItems.BILE.get(), 2).setCraftingTime(282).unlockedBy(Items.ENDER_EYE).save(consumer);
		DecomposerRecipeBuilder.create().setIngredient(Items.GLISTERING_MELON_SLICE).addOutput(ModItems.MINERAL_FRAGMENT.get(), 8).setCraftingTime(320).unlockedBy(Items.GLISTERING_MELON_SLICE).save(consumer);
		DecomposerRecipeBuilder.create().setIngredient(Items.POISONOUS_POTATO).addOutput(ModItems.TOXIN_EXTRACT.get(), 5).setCraftingTime(100).unlockedBy(Items.POISONOUS_POTATO).save(consumer);
		DecomposerRecipeBuilder.create().setIngredient(Items.GOLDEN_CARROT).addOutput(ModItems.MINERAL_FRAGMENT.get(), 8).addOutput(ModItems.REGENERATIVE_FLUID.get(), 1).setCraftingTime(355).unlockedBy(Items.GOLDEN_CARROT).save(consumer);
		DecomposerRecipeBuilder.create().setIngredient(Items.SKELETON_SKULL).addOutput(ModItems.BONE_FRAGMENTS.get(), 48).setCraftingTime(1200).unlockedBy(Items.SKELETON_SKULL).save(consumer);
		DecomposerRecipeBuilder.create().setIngredient(Items.WITHER_SKELETON_SKULL).addOutput(ModItems.BONE_FRAGMENTS.get(), 48).addOutput(ModItems.WITHERING_OOZE.get(), 8).setCraftingTime(1400).unlockedBy(Items.WITHER_SKELETON_SKULL).save(consumer);
		DecomposerRecipeBuilder.create().setIngredient(Items.PLAYER_HEAD).addOutput(ModItems.FLESH_BITS.get(), 32).addOutput(Items.SKELETON_SKULL, 1).setCraftingTime(596).unlockedBy(Items.PLAYER_HEAD).save(consumer);
		DecomposerRecipeBuilder.create().setIngredient(Items.ZOMBIE_HEAD).addOutput(ModItems.FLESH_BITS.get(), 24).addOutput(ModItems.WITHERING_OOZE.get(), 3).addOutput(Items.SKELETON_SKULL, 1).setCraftingTime(527).unlockedBy(Items.ZOMBIE_HEAD).save(consumer);
		DecomposerRecipeBuilder.create().setIngredient(Items.CREEPER_HEAD).addOutput(ModItems.FLESH_BITS.get(), 32).addOutput(Items.SKELETON_SKULL, 1).setCraftingTime(596).unlockedBy(Items.CREEPER_HEAD).save(consumer);
		DecomposerRecipeBuilder.create().setIngredient(Items.DRAGON_HEAD).addOutput(ModItems.FLESH_BITS.get(), 40).addOutput(ModItems.EXOTIC_DUST.get(), 50).addOutput(ModItems.TOUGH_FIBERS.get(), 20).addOutput(Items.SKELETON_SKULL, 1).setCraftingTime(3820).unlockedBy(Items.DRAGON_HEAD).save(consumer);
		DecomposerRecipeBuilder.create().setIngredient(Items.NETHER_STAR).addOutput(ModItems.EXOTIC_DUST.get(), 20).setCraftingTime(1000).unlockedBy(Items.NETHER_STAR).save(consumer);
		DecomposerRecipeBuilder.create().setIngredient(Items.PRISMARINE_SHARD).addOutput(ModItems.BIO_MINERALS.get(), 3).setCraftingTime(66).unlockedBy(Items.PRISMARINE_SHARD).save(consumer);
		DecomposerRecipeBuilder.create().setIngredient(Items.PRISMARINE_CRYSTALS).addOutput(ModItems.GEM_FRAGMENTS.get(), 3).addOutput(ModItems.BIO_MINERALS.get(), 1).addOutput(ModItems.BIO_LUMENS.get(), 1).setCraftingTime(194).unlockedBy(Items.PRISMARINE_CRYSTALS).save(consumer);
		DecomposerRecipeBuilder.create().setIngredient(Items.RABBIT).addOutput(ModItems.FLESH_BITS.get(), 6).addOutput(ModItems.BONE_FRAGMENTS.get(), 3).setCraftingTime(183).unlockedBy(Items.RABBIT).save(consumer);
		DecomposerRecipeBuilder.create().setIngredient(Items.RABBIT_FOOT).addOutput(ModItems.ELASTIC_FIBERS.get(), 5).addOutput(ModItems.HORMONE_SECRETION.get(), 10).addOutput(ModItems.TOUGH_FIBERS.get(), 4).setCraftingTime(370).unlockedBy(Items.RABBIT_FOOT).save(consumer);
		DecomposerRecipeBuilder.create().setIngredient(Items.RABBIT_HIDE).addOutput(ModItems.TOUGH_FIBERS.get(), 2).setCraftingTime(60).unlockedBy(Items.RABBIT_HIDE).save(consumer);
		DecomposerRecipeBuilder.create().setIngredient(Items.LEATHER_HORSE_ARMOR).addOutput(ModItems.TOUGH_FIBERS.get(), 70).setCraftingTime(2100).unlockedBy(Items.LEATHER_HORSE_ARMOR).save(consumer);
		DecomposerRecipeBuilder.create().setIngredient(Items.MUTTON).addOutput(ModItems.FLESH_BITS.get(), 4).setCraftingTime(72).unlockedBy(Items.MUTTON).save(consumer);
		DecomposerRecipeBuilder.create().setIngredient(Items.END_CRYSTAL).addOutput(ModItems.EXOTIC_DUST.get(), 7).addOutput(ModItems.VOLATILE_FLUID.get(), 7).setCraftingTime(525).unlockedBy(Items.END_CRYSTAL).save(consumer);
		DecomposerRecipeBuilder.create().setIngredient(Items.CHORUS_FRUIT).addOutput(ModItems.BIO_MINERALS.get(), 3).addOutput(ModItems.EXOTIC_DUST.get(), 2).addOutput(ModItems.BILE.get(), 1).setCraftingTime(182).unlockedBy(Items.CHORUS_FRUIT).save(consumer);
		DecomposerRecipeBuilder.create().setIngredient(Items.POPPED_CHORUS_FRUIT).addOutput(ModItems.BIO_MINERALS.get(), 2).addOutput(ModItems.EXOTIC_DUST.get(), 2).addOutput(ModItems.STONE_POWDER.get(), 1).setCraftingTime(174).unlockedBy(Items.POPPED_CHORUS_FRUIT).save(consumer);
		DecomposerRecipeBuilder.create().setIngredient(Items.DRAGON_BREATH).addOutput(ModItems.EXOTIC_DUST.get(), 5).addOutput(ModItems.VOLATILE_FLUID.get(), 10).addOutput(Items.GLASS_BOTTLE, 1).setCraftingTime(520).unlockedBy(Items.DRAGON_BREATH).save(consumer);
		DecomposerRecipeBuilder.create().setIngredient(Items.TOTEM_OF_UNDYING).addOutput(ModItems.EXOTIC_DUST.get(), 50).addOutput(ModItems.REGENERATIVE_FLUID.get(), 50).setCraftingTime(4250).unlockedBy(Items.TOTEM_OF_UNDYING).save(consumer);
		DecomposerRecipeBuilder.create().setIngredient(Items.SHULKER_SHELL).addOutput(ModItems.BIO_MINERALS.get(), 5).addOutput(ModItems.EXOTIC_DUST.get(), 5).addOutput(ModItems.STONE_POWDER.get(), 1).setCraftingTime(390).unlockedBy(Items.SHULKER_SHELL).save(consumer);
		DecomposerRecipeBuilder.create().setIngredient(Items.IRON_NUGGET).addOutput(ModItems.MINERAL_FRAGMENT.get(), 1).setCraftingTime(40).unlockedBy(Items.IRON_NUGGET).save(consumer);
		DecomposerRecipeBuilder.create().setIngredient(Items.PHANTOM_MEMBRANE).addOutput(ModItems.TOUGH_FIBERS.get(), 10).addOutput(ModItems.EXOTIC_DUST.get(), 5).setCraftingTime(550).unlockedBy(Items.PHANTOM_MEMBRANE).save(consumer);
		DecomposerRecipeBuilder.create().setIngredient(Items.NAUTILUS_SHELL).addOutput(ModItems.BIO_MINERALS.get(), 10).setCraftingTime(220).unlockedBy(Items.NAUTILUS_SHELL).save(consumer);
		DecomposerRecipeBuilder.create().setIngredient(Items.HEART_OF_THE_SEA).addOutput(ModItems.GEM_FRAGMENTS.get(), 9).addOutput(ModItems.EXOTIC_DUST.get(), 30).setCraftingTime(1950).unlockedBy(Items.HEART_OF_THE_SEA).save(consumer);
		DecomposerRecipeBuilder.create().setIngredient(Items.CROSSBOW).addOutput(ModItems.TOUGH_FIBERS.get(), 1).addOutput(ModItems.ELASTIC_FIBERS.get(), 1).setCraftingTime(40).unlockedBy(Items.CROSSBOW).save(consumer);
		DecomposerRecipeBuilder.create().setIngredient(Items.SUSPICIOUS_STEW).addOutput(ModItems.BILE.get(), 2).addOutput(ModItems.TOXIN_EXTRACT.get(), 1).setCraftingTime(52).unlockedBy(Items.SUSPICIOUS_STEW).save(consumer);
		DecomposerRecipeBuilder.create().setIngredient(Items.GLOW_BERRIES).addOutput(ModItems.BIO_LUMENS.get(), 1).setCraftingTime(22).unlockedBy(Items.GLOW_BERRIES).save(consumer);
		DecomposerRecipeBuilder.create().setIngredient(Items.SHROOMLIGHT).addOutput(ModItems.BIO_LUMENS.get(), 9).setCraftingTime(198).unlockedBy(Items.SHROOMLIGHT).save(consumer);
		DecomposerRecipeBuilder.create().setIngredient(Items.HONEYCOMB).addOutput(ModItems.BILE.get(), 2).setCraftingTime(32).unlockedBy(Items.HONEYCOMB).save(consumer);
		DecomposerRecipeBuilder.create().setIngredient(Items.HONEY_BOTTLE).addOutput(ModItems.REGENERATIVE_FLUID.get(), 2).addOutput(ModItems.BILE.get(), 1).addOutput(Items.GLASS_BOTTLE, 1).setCraftingTime(106).unlockedBy(Items.HONEY_BOTTLE).save(consumer);
		DecomposerRecipeBuilder.create().setIngredient(Items.HONEYCOMB_BLOCK).addOutput(ModItems.BILE.get(), 8).setCraftingTime(128).unlockedBy(Items.HONEYCOMB_BLOCK).save(consumer);
		DecomposerRecipeBuilder.create().setIngredient(Items.CRYING_OBSIDIAN).addOutput(ModItems.EXOTIC_DUST.get(), 20).addOutput(Items.OBSIDIAN, 1).setCraftingTime(1020).unlockedBy(Items.CRYING_OBSIDIAN).save(consumer);
		DecomposerRecipeBuilder.create().setIngredient(Items.GILDED_BLACKSTONE).addOutput(ModItems.MINERAL_FRAGMENT.get(), 4).addOutput(Items.BLACKSTONE, 1).setCraftingTime(180).unlockedBy(Items.GILDED_BLACKSTONE).save(consumer);
		DecomposerRecipeBuilder.create().setIngredient(Items.POINTED_DRIPSTONE).addOutput(ModItems.STONE_POWDER.get(), 2).addOutput(ModItems.MINERAL_FRAGMENT.get(), 2).setCraftingTime(140).unlockedBy(Items.POINTED_DRIPSTONE).save(consumer);
		DecomposerRecipeBuilder.create().setIngredient(ItemTags.BANNERS).addOutput(ModItems.TOUGH_FIBERS.get(), 24).addOutput(ModItems.ELASTIC_FIBERS.get(), 24).setCraftingTime(960).unlockedBy(ItemTags.BANNERS).save(consumer);
		DecomposerRecipeBuilder.create().setIngredient(ItemTags.WOOL).addOutput(ModItems.TOUGH_FIBERS.get(), 4).addOutput(ModItems.ELASTIC_FIBERS.get(), 4).setCraftingTime(160).unlockedBy(ItemTags.WOOL).save(consumer);
		DecomposerRecipeBuilder.create().setIngredient(ItemTags.CARPETS).addOutput(ModItems.TOUGH_FIBERS.get(), 2).addOutput(ModItems.ELASTIC_FIBERS.get(), 2).setCraftingTime(80).unlockedBy(ItemTags.CARPETS).save(consumer);
		DecomposerRecipeBuilder.create().setIngredient(Tags.Items.EGGS).addOutput(ModItems.BIO_MINERALS.get(), 1).addOutput(ModItems.STONE_POWDER.get(), 1).setCraftingTime(52).unlockedBy(Tags.Items.EGGS).save(consumer);
		DecomposerRecipeBuilder.create().setIngredient(Tags.Items.FEATHERS).addOutput(ModItems.TOUGH_FIBERS.get(), 3).setCraftingTime(90).unlockedBy(Tags.Items.FEATHERS).save(consumer);
		DecomposerRecipeBuilder.create().setIngredient(Tags.Items.BONES).addOutput(ModItems.BONE_FRAGMENTS.get(), 6).setCraftingTime(150).unlockedBy(Tags.Items.BONES).save(consumer);
	}

	private void registerBioForgeRecipes(Consumer<FinishedRecipe> consumer) {

//		BioForgeRecipeBuilder.create(new ItemData(ModItems.TOOTH_GUN.get()))
//				.setCraftingTime(5 * 20)
//				.addIngredient(ModItems.FLESH_BITS.get(), 40)
//				.addIngredient(ModItems.BONE_SCRAPS.get(), 64)
//				.addIngredient(ModItems.ELASTIC_FIBERS.get(), 40)
//				.setReactant(ModItems.LIVING_FLESH.get())
//				.setCategory(BioForgeCategory.WEAPON)
//				.unlockedBy(ModItems.LIVING_FLESH.get())
//				.save(consumer);

//		BioForgeRecipeBuilder.create(new ItemData(ModItems.WITHERSHOT.get()))
		//				.setCraftingTime(10 * 20)
		//				.addIngredient(Items.WITHER_SKELETON_SKULL, 1)
		//				.addIngredient(Items.NETHER_STAR, 1)
		//				.addIngredient(ModItems.BONE_SCRAPS.get(), 32)
		//				.addIngredient(ModItems.TOUGH_FIBERS.get(), 40)
		//				.setReactant(ModItems.LIVING_FLESH.get())
		//				.setCategory(BioForgeCategory.WEAPON)
		//				.unlockedBy(ModItems.LIVING_FLESH.get())
		//				.save(consumer);

		BioForgeRecipeBuilder.create(new ItemData(ModItems.LONG_CLAW.get())).addIngredient(ModItems.LIVING_FLESH.get()).addIngredient(ModItems.MOB_CLAW.get(), 6).addIngredient(ModItems.BONE_FRAGMENTS.get(), 22).addIngredient(ModItems.TOUGH_FIBERS.get(), 32).addIngredient(ModItems.FLESH_BITS.get(), 12 + 32).setCategory(ModRecipeBooks.BioForgeCategory.WEAPONS).unlockedBy(ModItems.LIVING_FLESH.get()).save(consumer);

		BioForgeRecipeBuilder.create(new ItemData(ModItems.DECOMPOSER.get())).addIngredient(ModItems.LIVING_FLESH.get()).addIngredient(ModItems.FLESH_BITS.get(), 64).addIngredient(ModItems.BONE_FRAGMENTS.get(), 24).addIngredient(ModItems.VOLATILE_GLAND.get(), 1).addIngredient(ModItems.MOB_FANG.get(), 12).setCategory(ModRecipeBooks.BioForgeCategory.MACHINES).unlockedBy(ModItems.LIVING_FLESH.get()).save(consumer);

		BioForgeRecipeBuilder.create(new ItemData(ModItems.DIGESTER.get())).addIngredient(ModItems.LIVING_FLESH.get()).addIngredient(ModItems.FLESH_BITS.get(), 64).addIngredient(ModItems.BONE_FRAGMENTS.get(), 24).addIngredient(ModItems.GENERIC_MOB_GLAND.get(), 1).setCategory(ModRecipeBooks.BioForgeCategory.MACHINES).unlockedBy(ModItems.LIVING_FLESH.get()).save(consumer);

		BioForgeRecipeBuilder.create(new ItemData(ModItems.BIO_LAB.get())).addIngredient(ModItems.LIVING_FLESH.get()).addIngredient(ModItems.FLESH_BITS.get(), 64).addIngredient(ModItems.BONE_FRAGMENTS.get(), 24).addIngredient(ModItems.GENERIC_MOB_GLAND.get(), 1).addIngredient(ModItems.EXOTIC_DUST.get(), 20).setCategory(ModRecipeBooks.BioForgeCategory.MACHINES).unlockedBy(ModItems.LIVING_FLESH.get()).save(consumer);

		BioForgeRecipeBuilder.create(new ItemData(ModItems.BIO_FORGE.get())).addIngredient(ModItems.LIVING_FLESH.get()).addIngredient(ModItems.FLESH_BITS.get(), 64).addIngredient(ModItems.BONE_FRAGMENTS.get(), 24).addIngredient(ModItems.MOB_CLAW.get(), 3).addIngredient(ModItems.ELASTIC_FIBERS.get(), 64).setCategory(ModRecipeBooks.BioForgeCategory.MACHINES).unlockedBy(ModItems.LIVING_FLESH.get()).save(consumer);

		BioForgeRecipeBuilder.create(new ItemData(ModItems.CREATOR.get())).addIngredient(ModItems.LIVING_FLESH.get()).addIngredient(ModItems.FLESH_BITS.get(), 64).addIngredient(ModItems.BONE_FRAGMENTS.get(), 24).addIngredient(ModItems.REGENERATIVE_FLUID.get(), 16).addIngredient(ModItems.EXOTIC_DUST.get(), 32).setCategory(ModRecipeBooks.BioForgeCategory.MACHINES).unlockedBy(ModItems.LIVING_FLESH.get()).save(consumer);

		BioForgeRecipeBuilder.create(new ItemData(ModItems.TONGUE.get())).addIngredient(ModItems.LIVING_FLESH.get()).addIngredient(ModItems.FLESH_BITS.get(), 64).addIngredient(ModItems.BONE_FRAGMENTS.get(), 12).addIngredient(ModItems.ELASTIC_FIBERS.get(), 128).setCategory(ModRecipeBooks.BioForgeCategory.MACHINES).unlockedBy(ModItems.LIVING_FLESH.get()).save(consumer);

		BioForgeRecipeBuilder.create(new ItemData(ModItems.VOICE_BOX.get())).addIngredient(Items.NOTE_BLOCK).addIngredient(ModItems.FLESH_BITS.get(), 64).addIngredient(ModItems.BONE_FRAGMENTS.get(), 12).addIngredient(ModItems.ELASTIC_FIBERS.get(), 64 * 2).setCategory(ModRecipeBooks.BioForgeCategory.MISC).unlockedBy(ModItems.ELASTIC_FIBERS.get()).save(consumer);

		BioForgeRecipeBuilder.create(new ItemData(ModItems.FLESH_BLOCK.get())).addIngredient(ModItems.FLESH_BITS.get(), 32).setCategory(ModRecipeBooks.BioForgeCategory.BLOCKS).unlockedBy(ModItems.FLESH_BITS.get()).save(consumer);

		BioForgeRecipeBuilder.create(new ItemData(ModItems.FLESH_STAIRS.get())).addIngredient(ModItems.FLESH_BITS.get(), 24).setCategory(ModRecipeBooks.BioForgeCategory.BLOCKS).unlockedBy(ModItems.FLESH_BITS.get()).save(consumer);

		BioForgeRecipeBuilder.create(new ItemData(ModItems.FLESH_SLAB.get(), 2)).addIngredient(ModItems.FLESH_BITS.get(), 32).setCategory(ModRecipeBooks.BioForgeCategory.BLOCKS).unlockedBy(ModItems.FLESH_BITS.get()).save(consumer);

		BioForgeRecipeBuilder.create(new ItemData(ModItems.PACKED_FLESH_BLOCK.get())).addIngredient(ModItems.FLESH_BITS.get(), 32 * 2).setCategory(ModRecipeBooks.BioForgeCategory.BLOCKS).unlockedBy(ModItems.FLESH_BITS.get()).save(consumer);

		BioForgeRecipeBuilder.create(new ItemData(ModItems.PACKED_FLESH_STAIRS.get())).addIngredient(ModItems.FLESH_BITS.get(), 24 * 2).setCategory(ModRecipeBooks.BioForgeCategory.BLOCKS).unlockedBy(ModItems.FLESH_BITS.get()).save(consumer);

		BioForgeRecipeBuilder.create(new ItemData(ModItems.PACKED_FLESH_SLAB.get(), 2)).addIngredient(ModItems.FLESH_BITS.get(), 32 * 2).setCategory(ModRecipeBooks.BioForgeCategory.BLOCKS).unlockedBy(ModItems.FLESH_BITS.get()).save(consumer);

		BioForgeRecipeBuilder.create(new ItemData(ModItems.FLESH_DOOR.get())).addIngredient(ModItems.FLESH_BITS.get(), 32 * 2).addIngredient(ModItems.ELASTIC_FIBERS.get(), 16).addIngredient(ModItems.BONE_FRAGMENTS.get(), 16).setCategory(ModRecipeBooks.BioForgeCategory.MISC).unlockedBy(ModItems.FLESH_BITS.get()).save(consumer);

		BioForgeRecipeBuilder.create(new ItemData(ModItems.FLESH_IRIS_DOOR.get())).addIngredient(ModItems.FLESH_BITS.get(), 32).addIngredient(ModItems.ELASTIC_FIBERS.get(), 24).addIngredient(ModItems.BONE_FRAGMENTS.get(), 8).setCategory(ModRecipeBooks.BioForgeCategory.MISC).unlockedBy(ModItems.FLESH_BITS.get()).save(consumer);

		BioForgeRecipeBuilder.create(new ItemData(ModItems.FLESH_FENCE.get())).addIngredient(ModItems.FLESH_BITS.get(), 32).addIngredient(ModItems.BONE_FRAGMENTS.get(), 32).setCategory(ModRecipeBooks.BioForgeCategory.MISC).unlockedBy(ModItems.FLESH_BITS.get()).save(consumer);

		BioForgeRecipeBuilder.create(new ItemData(ModItems.FLESH_FENCE_GATE.get())).addIngredient(ModItems.BONE_FRAGMENTS.get(), 32).addIngredient(ModItems.ELASTIC_FIBERS.get(), 8).setCategory(ModRecipeBooks.BioForgeCategory.MISC).unlockedBy(ModItems.BONE_FRAGMENTS.get()).save(consumer);

		BioForgeRecipeBuilder.create(new ItemData(ModItems.FLESH_LADDER.get(), 3)).addIngredient(ModItems.BONE_FRAGMENTS.get(), 7 * 6).addIngredient(ModItems.FLESH_BITS.get(), 6).setCategory(ModRecipeBooks.BioForgeCategory.MISC).unlockedBy(ModItems.BONE_FRAGMENTS.get()).save(consumer);

		BioForgeRecipeBuilder.create(new ItemData(ModItems.FLESHKIN_CHEST.get())).addIngredient(ModItems.LIVING_FLESH.get()).addIngredient(ModItems.MOB_FANG.get(), 6).addIngredient(ModItems.FLESH_BITS.get(), 64).addIngredient(ModItems.BONE_FRAGMENTS.get(), 22).addIngredient(ModItems.TOUGH_FIBERS.get(), 64).setCategory(ModRecipeBooks.BioForgeCategory.MISC).unlockedBy(ModItems.LIVING_FLESH.get()).save(consumer);

		BioForgeRecipeBuilder.create(new ItemData(ModItems.FLESHKIN_PRESSURE_PLATE.get())).addIngredient(ModItems.LIVING_FLESH.get()).addIngredient(ModItems.BONE_FRAGMENTS.get(), 16).addIngredient(ModItems.FLESH_BITS.get(), 16).addIngredient(ModItems.ELASTIC_FIBERS.get(), 8).setCategory(ModRecipeBooks.BioForgeCategory.MISC).unlockedBy(ModItems.LIVING_FLESH.get()).save(consumer);

		BioForgeRecipeBuilder.create(new ItemData(ModItems.INJECTOR.get())).addIngredient(ModItems.FLESH_BITS.get(), 32).addIngredient(ModItems.BONE_FRAGMENTS.get(), 16).addIngredient(ModItems.ELASTIC_FIBERS.get(), 16).addIngredient(ModItems.GLASS_VIAL.get()).addIngredient(ModItems.MOB_FANG.get()).setCategory(ModRecipeBooks.BioForgeCategory.WEAPONS).unlockedBy(ModItems.FLESH_BITS.get()).save(consumer);
	}

	private void registerBioLabRecipes(Consumer<FinishedRecipe> consumer) {

		BioLabRecipeBuilder.create(ModItems.ORGANIC_COMPOUND.get())
				.addIngredients(ModItems.BILE.get(), 3)
				.addIngredient(ModItems.NUTRIENTS.get())
				.setCraftingTime(2 * 20)
				.unlockedBy(ModItems.BILE.get()).save(consumer);

		BioLabRecipeBuilder.create(ModItems.GROWTH_SERUM.get())
				.addIngredients(Items.BONE_MEAL, 2)
				.addIngredients(ModItems.NUTRIENTS.get(), 2)
				.setReactant(ModItems.ORGANIC_COMPOUND.get())
				.setCraftingTime(5 * 20)
				.unlockedBy(ModItems.ORGANIC_COMPOUND.get()).save(consumer);

		BioLabRecipeBuilder.create(ModItems.BREEDING_STIMULANT.get())
				.addIngredient(Items.SUGAR)
				.addIngredient(ModItems.NUTRIENTS.get())
				.setReactant(ModItems.ORGANIC_COMPOUND.get())
				.setCraftingTime(5 * 20)
				.unlockedBy(ModItems.ORGANIC_COMPOUND.get()).save(consumer);

		BioLabRecipeBuilder.create(ModItems.REJUVENATION_SERUM.get()).addIngredients(ModItems.EXOTIC_DUST.get(), 2).addIngredients(ModItems.REGENERATIVE_FLUID.get(), 2)
				.setReactant(ModItems.ORGANIC_COMPOUND.get())
				.setCraftingTime(5 * 20)
				.unlockedBy(ModItems.ORGANIC_COMPOUND.get()).save(consumer);

		BioLabRecipeBuilder.create(ModItems.CLEANSING_SERUM.get()).addIngredients(ModItems.REGENERATIVE_FLUID.get(), 2)
				.addIngredient(ModItems.NUTRIENTS.get())
				.addIngredient(Items.MILK_BUCKET)
				.setReactant(ModItems.ORGANIC_COMPOUND.get())
				.setCraftingTime(8 * 20)
				.unlockedBy(ModItems.ORGANIC_COMPOUND.get()).save(consumer);

		BioLabRecipeBuilder.create(ModItems.INSOMNIA_CURE.get())
				.addIngredients(Items.COCOA_BEANS, 2)
				.addIngredient(ModItems.NUTRIENTS.get())
				.setReactant(ModItems.CLEANSING_SERUM.get())
				.setCraftingTime(8 * 20)
				.unlockedBy(ModItems.CLEANSING_SERUM.get()).save(consumer);

		BioLabRecipeBuilder.create(ModItems.ABSORPTION_BOOST.get())
				.addIngredient(Items.GOLDEN_APPLE)
				.addIngredients(ModItems.TOUGH_FIBERS.get(), 2)
				.setReactant(ModItems.GROWTH_SERUM.get())
				.setCraftingTime(8 * 20)
				.unlockedBy(ModItems.GROWTH_SERUM.get()).save(consumer);

		BioLabRecipeBuilder.create(ModItems.ADRENALINE_SERUM.get()).addIngredient(Items.COCOA_BEANS).addIngredient(ModItems.REGENERATIVE_FLUID.get())
				.addIngredient(PotionUtils.setPotion(new ItemStack(Items.POTION), Potions.STRONG_STRENGTH))
				.addIngredient(PotionUtils.setPotion(new ItemStack(Items.POTION), Potions.STRONG_SWIFTNESS))
				.setReactant(ModItems.ORGANIC_COMPOUND.get())
				.setCraftingTime(12 * 20)
				.unlockedBy(ModItems.NUTRIENTS.get()).save(consumer);

		BioLabRecipeBuilder.create(ModItems.UNSTABLE_COMPOUND.get()).addIngredient(Items.SLIME_BALL).addIngredients(ModItems.VOLATILE_FLUID.get(), 2).addIngredient(ModItems.TOXIN_EXTRACT.get()).setCraftingTime(5 * 20).unlockedBy(ModItems.VOLATILE_FLUID.get()).save(consumer);

		BioLabRecipeBuilder.create(ModItems.DECAY_AGENT.get())
				.addIngredients(Items.ROTTEN_FLESH, 4)
				.setReactant(ModItems.UNSTABLE_COMPOUND.get())
				.setCraftingTime(8 * 20)
				.unlockedBy(ModItems.UNSTABLE_COMPOUND.get()).save(consumer);

//		BioLabRecipeBuilder.create(ModItems.ICHOR_SERUM.get())
//				.addIngredients(ModItems.EXOTIC_DUST.get(), 2)
//				.addIngredients(Items.HONEYCOMB, 2)
//				.setReactant(ModItems.ORGANIC_COMPOUND.get())
//				.setCraftingTime(12 * 20)
//				.unlockedBy(ModItems.ORGANIC_COMPOUND.get()).save(consumer);
	}

}
