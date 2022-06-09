package com.github.elenterius.biomancy.datagen.recipes;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.init.ModItems;
import com.github.elenterius.biomancy.init.ModTags;
import com.github.elenterius.biomancy.recipe.BioForgeCategory;
import com.github.elenterius.biomancy.util.FuelUtil;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.recipes.*;
import net.minecraft.resources.ResourceLocation;
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
		SimpleCookingRecipeBuilder.blasting(Ingredient.of(ModItems.LITHIC_POWDER.get()), Items.GLASS_PANE, 0.1F, 100)
				.unlockedBy(hasName(ModItems.LITHIC_POWDER.get()), has(ModItems.LITHIC_POWDER.get())).save(consumer, getBlastingRecipeId(Items.GLASS_PANE));
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

		ShapelessRecipeBuilder.shapeless(Items.BONE_MEAL)
				.requires(ModItems.BONE_SCRAPS.get(), 4)
				.unlockedBy(hasName(ModItems.BONE_SCRAPS.get()), has(ModItems.BONE_SCRAPS.get())).save(consumer, getConversionRecipeId(Items.BONE_MEAL, ModItems.BONE_SCRAPS.get()));

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
		ShapelessRecipeBuilder.shapeless(Items.DIORITE)
				.requires(Items.COBBLESTONE).requires(ModItems.MINERAL_DUST.get()).requires(ModItems.LITHIC_POWDER.get())
				.unlockedBy(hasName(ModItems.LITHIC_POWDER.get()), has(ModItems.LITHIC_POWDER.get())).save(consumer, getConversionRecipeId(Items.DIORITE, ModItems.LITHIC_POWDER.get()));

		ShapelessRecipeBuilder.shapeless(Items.RED_SAND)
				.requires(Items.SAND).requires(ModItems.MINERAL_DUST.get(), 2)
				.unlockedBy(hasName(ModItems.MINERAL_DUST.get()), has(ModItems.MINERAL_DUST.get())).save(consumer, getConversionRecipeId(Items.RED_SAND, ModItems.MINERAL_DUST.get()));

		ShapedRecipeBuilder.shaped(Items.DIRT)
				.define('P', ModItems.ORGANIC_MATTER.get())
				.define('L', ModItems.LITHIC_POWDER.get())
				.pattern("LPL").pattern("PLP").pattern("LPL")
				.unlockedBy(hasName(ModItems.ORGANIC_MATTER.get()), has(ModItems.ORGANIC_MATTER.get())).save(consumer, getConversionRecipeId(Items.DIRT, ModItems.LITHIC_POWDER.get()));

		ShapelessRecipeBuilder.shapeless(Items.CLAY_BALL)
				.requires(Items.WATER_BUCKET)
				.requires(ModItems.LITHIC_POWDER.get(), 8)
				.unlockedBy(hasName(ModItems.LITHIC_POWDER.get()), has(ModItems.LITHIC_POWDER.get())).save(consumer, getConversionRecipeId(Items.CLAY_BALL, ModItems.LITHIC_POWDER.get()));

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
		DecomposerRecipeBuilder.create(getItemName(Items.ENCHANTED_GOLDEN_APPLE))
				.setIngredient(Items.ENCHANTED_GOLDEN_APPLE)
				.setCraftingTime(200)
				.addOutput(ModItems.BIO_LUMENS.get(), 1, 3) //uniform
				.addOutput(ModItems.MINERAL_DUST.get(), 1) // constant value
				.addOutput(ModItems.ORGANIC_MATTER.get(), 1, 3)
				.addOutput(ModItems.EXOTIC_DUST.get(), 15, 20)
				.addOutput(Items.SUGAR, 2, 4)
				.addOutput(ModItems.BILE_EXTRACT.get(), 2, 4)
				.unlockedBy(Items.ENCHANTED_GOLDEN_APPLE)
				.save(consumer);

		DecomposerRecipeBuilder.create(getItemName(ModItems.MOB_SINEW.get()))
				.setIngredient(ModItems.MOB_SINEW.get())
				.addOutput(ModItems.FLESH_BITS.get(), 1, 3)
				.addOutput(ModItems.ELASTIC_FIBERS.get(), 1, 2)
				.unlockedBy(ModItems.MOB_SINEW.get())
				.save(consumer);

//		DecomposerRecipeBuilder.create(getItemName(ModItems.LARYNX.get()))
//				.setIngredient(ModItems.LARYNX.get())
//				.addOutput(ModItems.FLESH_BITS.get(), 1, 2)
//				.addOutput(ModItems.ELASTIC_FIBERS.get(), 2, 4)
//				.addOutput(ModItems.BILE_EXTRACT.get(), 1, 2)
//				.unlockedBy(ModItems.LARYNX.get())
//				.save(consumer);

//		DecomposerRecipeBuilder.create(getItemName(ModItems.EXALTED_LIVING_FLESH.get()))
//				.setIngredient(ModItems.LIVING_FLESH.get())
//				.addOutput(ModItems.FLESH_BITS.get(), 2, 5)
//				.addOutput(ModItems.ELASTIC_FIBERS.get(), 1, 4)
//				.addOutput(ModItems.EXOTIC_DUST.get(), 4, 8)
//				.addOutput(ModItems.BILE_EXTRACT.get(), 2, 4)
//				.unlockedBy(ModItems.EXALTED_LIVING_FLESH.get())
//				.save(consumer);

		DecomposerRecipeBuilder.create("raw_meats")
				.setIngredient(ModTags.Items.RAW_MEATS)
				.addOutput(ModItems.FLESH_BITS.get(), 2, 4)
				.addOutput(ModItems.ELASTIC_FIBERS.get(), 0, 1)
				.addOutput(ModItems.BONE_SCRAPS.get(), 0, 2)
				.unlockedBy(ModTags.Items.RAW_MEATS)
				.save(consumer);
	}

	private void registerBioForgeRecipes(Consumer<FinishedRecipe> consumer) {
//		BioForgeRecipeBuilder.create(new ItemData(ModItems.OCULUS.get()))
//				.addIngredient(ModItems.FLESH_BITS.get(), 20)
//				.addIngredient(ModItems.ELASTIC_FIBERS.get(), 10)
//				.addIngredient(Items.QUARTZ, 2)
//				.setReactant(Items.SPIDER_EYE)
//				.setCategory(BioForgeCategory.MISC)
//				.unlockedBy(ModItems.ELASTIC_FIBERS.get())
//				.save(consumer);

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

		BioForgeRecipeBuilder.create(new ItemData(ModItems.LONG_CLAW.get()))
				.setCraftingTime(5 * 20)
				.addIngredient(ModItems.MOB_CLAW.get(), 6)
				.addIngredient(ModItems.BONE_SCRAPS.get(), 22)
				.addIngredient(ModItems.TOUGH_FIBERS.get(), 32)
				.addIngredient(ModItems.FLESH_BITS.get(), 12)
				.addIngredient(ModItems.ELASTIC_FIBERS.get(), 32)
				.setReactant(ModItems.LIVING_FLESH.get())
				.setCategory(BioForgeCategory.WEAPON)
				.unlockedBy(ModItems.LIVING_FLESH.get())
				.save(consumer);

		BioForgeRecipeBuilder.create(new ItemData(ModItems.FLESHKIN_CHEST.get()))
				.setCraftingTime(5 * 20)
				.addIngredient(ModItems.MOB_FANG.get(), 6)
				.addIngredient(ModItems.FLESH_BITS.get(), 64)
				.addIngredient(ModItems.BONE_SCRAPS.get(), 22)
				.addIngredient(ModItems.ELASTIC_FIBERS.get(), 22)
				.addIngredient(ModItems.TOUGH_FIBERS.get(), 64)
				.setReactant(ModItems.LIVING_FLESH.get())
				.setCategory(BioForgeCategory.BLOCKS)
				.unlockedBy(ModItems.LIVING_FLESH.get())
				.save(consumer);

//		BioForgeRecipeBuilder.create(new ItemData(ModItems.GULGE.get()))
//				.setCraftingTime(5 * 20)
//				.addIngredient(ModItems.MOB_FANG.get(), 4)
//				.addIngredient(ModItems.FLESH_BITS.get(), 32)
//				.addIngredient(ModItems.BONE_SCRAPS.get(), 22)
//				.addIngredient(ModItems.ELASTIC_FIBERS.get(), 64)
//				.addIngredient(ModItems.ELASTIC_FIBERS.get(), 64)
//				.setReactant(ModItems.LIVING_FLESH.get())
//				.setCategory(BioForgeCategory.BLOCKS)
//				.unlockedBy(ModItems.LIVING_FLESH.get())
//				.save(consumer);

		BioForgeRecipeBuilder.create(new ItemData(ModItems.DECOMPOSER.get()))
				.setCraftingTime(5 * 20)
				.addIngredient(ModItems.FLESH_BITS.get(), 64)
				.addIngredient(ModItems.BONE_SCRAPS.get(), 24)
				.addIngredient(ModItems.VOLATILE_GLAND.get(), 1)
				.addIngredient(ModItems.MOB_FANG.get(), 12)
				.setReactant(ModItems.LIVING_FLESH.get())
				.setCategory(BioForgeCategory.MACHINES)
				.unlockedBy(ModItems.LIVING_FLESH.get())
				.save(consumer);

		BioForgeRecipeBuilder.create(new ItemData(ModItems.DIGESTER.get()))
				.setCraftingTime(4 * 20)
				.addIngredient(ModItems.FLESH_BITS.get(), 64)
				.addIngredient(ModItems.BONE_SCRAPS.get(), 24)
				.addIngredient(ModItems.MOB_GLAND.get(), 1)
				.setReactant(ModItems.LIVING_FLESH.get())
				.setCategory(BioForgeCategory.MACHINES)
				.unlockedBy(ModItems.LIVING_FLESH.get())
				.save(consumer);

		BioForgeRecipeBuilder.create(new ItemData(ModItems.BIO_LAB.get()))
				.setCraftingTime(5 * 20)
				.addIngredient(ModItems.FLESH_BITS.get(), 64)
				.addIngredient(ModItems.BONE_SCRAPS.get(), 24)
				.addIngredient(ModItems.MOB_GLAND.get(), 1)
				.addIngredient(ModItems.EXOTIC_DUST.get(), 20)
				.setReactant(ModItems.LIVING_FLESH.get())
				.setCategory(BioForgeCategory.MACHINES)
				.unlockedBy(ModItems.LIVING_FLESH.get())
				.save(consumer);

		BioForgeRecipeBuilder.create(new ItemData(ModItems.BIO_FORGE.get()))
				.setCraftingTime(5 * 20)
				.addIngredient(ModItems.FLESH_BITS.get(), 64)
				.addIngredient(ModItems.BONE_SCRAPS.get(), 24)
				.addIngredient(ModItems.MOB_CLAW.get(), 3)
				.addIngredient(ModItems.ELASTIC_FIBERS.get(), 64)
				.setReactant(ModItems.LIVING_FLESH.get())
				.setCategory(BioForgeCategory.MACHINES)
				.unlockedBy(ModItems.LIVING_FLESH.get())
				.save(consumer);

		BioForgeRecipeBuilder.create(new ItemData(ModItems.CREATOR.get()))
				.setCraftingTime(5 * 20)
				.addIngredient(ModItems.FLESH_BITS.get(), 64)
				.addIngredient(ModItems.BONE_SCRAPS.get(), 24)
				.addIngredient(ModItems.REJUVENATING_MUCUS.get(), 16)
				.addIngredient(ModItems.EXOTIC_DUST.get(), 32)
				.setReactant(ModItems.LIVING_FLESH.get())
				.setCategory(BioForgeCategory.BLOCKS)
				.unlockedBy(ModItems.LIVING_FLESH.get())
				.save(consumer);

		BioForgeRecipeBuilder.create(new ItemData(ModItems.TONGUE.get()))
				.setCraftingTime(4 * 20)
				.addIngredient(ModItems.FLESH_BITS.get(), 64)
				.addIngredient(ModItems.BONE_SCRAPS.get(), 12)
				.addIngredient(ModItems.ELASTIC_FIBERS.get(), 128)
				.setReactant(ModItems.LIVING_FLESH.get())
				.setCategory(BioForgeCategory.BLOCKS)
				.unlockedBy(ModItems.LIVING_FLESH.get())
				.save(consumer);

//		BioForgeRecipeBuilder.create(new ItemData(ModItems.SAC.get()))
//				.setCraftingTime(4 * 20)
//				.addIngredient(ModItems.ELASTIC_FIBERS.get(), 64)
//				.addIngredient(ModItems.ELASTIC_FIBERS.get(), 64)
//				.addIngredient(ModItems.FLESH_BITS.get(), 64)
//				.addIngredient(ModItems.BONE_SCRAPS.get(), 12)
//				.addIngredient(ModItems.MOB_FANG.get(), 8)
//				.setReactant(ModItems.LIVING_FLESH.get())
//				.setCategory(BioForgeCategory.BLOCKS)
//				.unlockedBy(ModItems.LIVING_FLESH.get())
//				.save(consumer);

		BioForgeRecipeBuilder.create(new ItemData(ModItems.VOICE_BOX.get()))
				.setCraftingTime(4 * 20)
				.addIngredient(ModItems.FLESH_BITS.get(), 64)
				.addIngredient(ModItems.BONE_SCRAPS.get(), 12)
				.addIngredient(ModItems.ELASTIC_FIBERS.get(), 64)
				.addIngredient(ModItems.ELASTIC_FIBERS.get(), 64)
				.setReactant(Items.NOTE_BLOCK)
				.setCategory(BioForgeCategory.BLOCKS)
				.unlockedBy(ModItems.ELASTIC_FIBERS.get())
				.save(consumer);

		BioForgeRecipeBuilder.create(new ItemData(ModItems.FLESH_BLOCK.get()))
				.addIngredient(ModItems.FLESH_BITS.get(), 32)
				.setCategory(BioForgeCategory.BLOCKS)
				.unlockedBy(ModItems.FLESH_BITS.get())
				.save(consumer);

		BioForgeRecipeBuilder.create(new ItemData(ModItems.FLESH_STAIRS.get()))
				.addIngredient(ModItems.FLESH_BITS.get(), 24)
				.setCategory(BioForgeCategory.BLOCKS)
				.unlockedBy(ModItems.FLESH_BITS.get())
				.save(consumer);

		BioForgeRecipeBuilder.create(new ItemData(ModItems.FLESH_SLAB.get(), 2))
				.addIngredient(ModItems.FLESH_BITS.get(), 32)
				.setCategory(BioForgeCategory.BLOCKS)
				.unlockedBy(ModItems.FLESH_BITS.get())
				.save(consumer);

//		BioForgeRecipeBuilder.create(new ItemData(ModItems.BONE_SWORD.get()))
//				.addIngredient(ModItems.BONE_SCRAPS.get(), 10)
//				.setReactant(ModItems.BONE_SCRAPS.get())
//				.setCategory(BioForgeCategory.WEAPON)
//				.unlockedBy(ModItems.BONE_SCRAPS.get())
//				.save(consumer);

		BioForgeRecipeBuilder.create(new ItemData(ModItems.BOOMLING.get()))
				.setCraftingTime(5 * 20)
				.addIngredient(ModItems.VOLATILE_GLAND.get(), 1)
				.addIngredient(ModItems.FLESH_BITS.get(), 14)
				.addIngredient(ModItems.BONE_SCRAPS.get(), 10)
				.addIngredient(ModItems.VOLATILE_EXTRACT.get(), 24)
				.addIngredient(ModItems.ELASTIC_FIBERS.get(), 32)
				.setReactant(ModItems.LIVING_FLESH.get())
				.setCategory(BioForgeCategory.WEAPON)
				.unlockedBy(ModItems.LIVING_FLESH.get())
				.save(consumer);
	}

	private void registerBioLabRecipes(Consumer<FinishedRecipe> consumer) {

		BioLabRecipeBuilder.create(ModItems.ORGANIC_COMPOUND.get())
				.addIngredients(ModItems.BILE_EXTRACT.get(), 3)
				.addIngredient(ModItems.NUTRIENTS.get())
				.setCraftingTime(2 * 20)
				.unlockedBy(ModItems.BILE_EXTRACT.get()).save(consumer);

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

		BioLabRecipeBuilder.create(ModItems.REJUVENATION_SERUM.get())
				.addIngredients(ModItems.EXOTIC_DUST.get(), 2)
				.addIngredients(ModItems.REJUVENATING_MUCUS.get(), 2)
				.setReactant(ModItems.ORGANIC_COMPOUND.get())
				.setCraftingTime(5 * 20)
				.unlockedBy(ModItems.ORGANIC_COMPOUND.get()).save(consumer);

		BioLabRecipeBuilder.create(ModItems.CLEANSING_SERUM.get())
				.addIngredients(ModItems.REJUVENATING_MUCUS.get(), 2)
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

		BioLabRecipeBuilder.create(ModItems.ADRENALINE_SERUM.get())
				.addIngredient(Items.COCOA_BEANS)
				.addIngredient(ModItems.REJUVENATING_MUCUS.get())
				.addIngredient(PotionUtils.setPotion(new ItemStack(Items.POTION), Potions.STRONG_STRENGTH))
				.addIngredient(PotionUtils.setPotion(new ItemStack(Items.POTION), Potions.STRONG_SWIFTNESS))
				.setReactant(ModItems.ORGANIC_COMPOUND.get())
				.setCraftingTime(12 * 20)
				.unlockedBy(ModItems.NUTRIENTS.get()).save(consumer);

		BioLabRecipeBuilder.create(ModItems.UNSTABLE_COMPOUND.get())
				.addIngredient(Items.SLIME_BALL)
				.addIngredients(ModItems.VOLATILE_EXTRACT.get(), 2)
				.addIngredient(ModItems.VENOM_EXTRACT.get())
				.setCraftingTime(5 * 20)
				.unlockedBy(ModItems.VOLATILE_EXTRACT.get()).save(consumer);

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
