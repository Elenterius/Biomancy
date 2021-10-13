package com.github.elenterius.biomancy.datagen;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.datagen.recipe.*;
import com.github.elenterius.biomancy.init.*;
import com.github.elenterius.biomancy.item.ReagentItem;
import com.github.elenterius.biomancy.recipe.ItemStackIngredient;
import com.github.elenterius.biomancy.util.BiofuelUtil;
import net.minecraft.advancements.ICriterionInstance;
import net.minecraft.advancements.criterion.EnchantmentPredicate;
import net.minecraft.advancements.criterion.InventoryChangeTrigger;
import net.minecraft.advancements.criterion.ItemPredicate;
import net.minecraft.advancements.criterion.MinMaxBounds;
import net.minecraft.data.*;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentData;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.item.EnchantedBookItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.potion.PotionUtils;
import net.minecraft.potion.Potions;
import net.minecraft.tags.ITag;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.Tags;
import net.minecraftforge.fluids.FluidAttributes;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import java.util.Arrays;
import java.util.function.Consumer;

public class ModRecipeProvider extends RecipeProvider {

	public static final Logger LOGGER = BiomancyMod.LOGGER;
	public static final Marker LOG_MARKER = MarkerManager.getMarker("RecipeProvider");

	private static final String HAS_OCULUS = "has_oculus";
	private static final String HAS_MUTAGENIC_BILE = "has_mutagenic_bile";
	private static final String HAS_KERATINS = "has_keratins";
	private static final String HAS_HORMONES = "has_hormones";
	private static final String HAS_FLESH_BLOCK = "has_flesh_block";
	private static final String HAS_GLASS_VIAL = "has_glass_vial";
	private static final String HAS_SILICATE_PASTE = "has_silicate_paste";
	private static final String HAS_OXIDE = "has_oxide";
	private static final String HAS_SILICATES = "has_silicates";
	private static final String HAS_OXIDE_POWDER = "has_oxide_powder";
	private static final ICriterionInstance OXIDE_CRITERION = has(ModTags.Items.OXIDES);
	private static final ICriterionInstance SILICATES_CRITERION = has(ModTags.Items.SILICATES);

	public ModRecipeProvider(DataGenerator generatorIn) {
		super(generatorIn);
	}

	protected static ItemPredicate createPredicate(IItemProvider item) {
		return ItemPredicate.Builder.item().of(item).build();
	}

	protected static ItemPredicate createPredicate(ITag<Item> tag) {
		return ItemPredicate.Builder.item().of(tag).build();
	}

	protected static InventoryChangeTrigger.Instance hasItems(IItemProvider... itemProviders) {
		ItemPredicate[] predicates = Arrays.stream(itemProviders).map(ModRecipeProvider::createPredicate).toArray(ItemPredicate[]::new);
		return inventoryTrigger(predicates);
	}

	@Override
	protected void buildShapelessRecipes(Consumer<IFinishedRecipe> consumer) {
		LOGGER.info(LOG_MARKER, "registering workbench recipes...");
		registerWorkbenchRecipes(consumer);
		LOGGER.info(LOG_MARKER, "registering cooking recipes...");
		registerCookingRecipes(consumer);

		LOGGER.info(LOG_MARKER, "registering chewer recipes...");
		registerChewerRecipes(consumer);
		LOGGER.info(LOG_MARKER, "registering digester recipes...");
		registerDigesterRecipes(consumer);
		LOGGER.info(LOG_MARKER, "registering solidifier recipes...");
		registerSolidifierRecipes(consumer);
		LOGGER.info(LOG_MARKER, "registering decomposer recipes...");
		registerDecomposerRecipes(consumer);

		LOGGER.info(LOG_MARKER, "registering evolution pool recipes...");
		registerEvolutionPoolRecipes(consumer);
	}

	private void registerEvolutionPoolRecipes(Consumer<IFinishedRecipe> consumer) {
		final int defaultTime = 400;

		// Duplication /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		EvolutionPoolRecipeBuilder.createRecipe(Items.IRON_NUGGET, defaultTime, 5)
				.addIngredient(Items.IRON_NUGGET).addIngredients(ModItems.OXIDE_POWDER.get(), 5)
				.addCriterion(HAS_OXIDE_POWDER, has(ModItems.OXIDE_POWDER.get())).build(consumer);

		EvolutionPoolRecipeBuilder.createRecipe(Items.GOLD_NUGGET, defaultTime, 5)
				.addIngredient(Items.GOLD_NUGGET).addIngredients(ModItems.OXIDE_POWDER.get(), 5)
				.addCriterion(HAS_OXIDE_POWDER, has(ModItems.OXIDE_POWDER.get())).build(consumer);

		EvolutionPoolRecipeBuilder.createRecipe(Items.QUARTZ, defaultTime, 3)
				.addIngredient(Items.QUARTZ).addIngredients(ModItems.SILICATE_PASTE.get(), 5)
				.addCriterion(HAS_SILICATE_PASTE, has(ModItems.SILICATE_PASTE.get())).build(consumer);

		// Transmutation////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		EvolutionPoolRecipeBuilder.createRecipe(Items.PRISMARINE_CRYSTALS, defaultTime * 2)
				.addIngredient(Items.QUARTZ).addIngredient(Items.LAPIS_LAZULI).addIngredients(ModItems.SILICATE_PASTE.get(), 2).addIngredient(ItemTags.FISHES)
				.addCriterion(HAS_SILICATE_PASTE, has(ModItems.SILICATE_PASTE.get())).build(consumer);

		EvolutionPoolRecipeBuilder.createRecipe(Items.PRISMARINE_SHARD, defaultTime * 2)
				.addIngredient(Items.QUARTZ).addIngredient(Items.LAPIS_LAZULI).addIngredients(ModItems.KERATIN_FILAMENTS.get(), 2).addIngredient(ItemTags.FISHES)
				.addCriterion("has_keratin_filaments", has(ModItems.KERATIN_FILAMENTS.get())).build(consumer);

		EvolutionPoolRecipeBuilder.createRecipe(Items.SHULKER_SHELL, defaultTime * 2)
				.addIngredients(Items.POPPED_CHORUS_FRUIT, 2).addIngredients(ModItems.SILICATE_PASTE.get(), 4)
				.addCriterion(HAS_SILICATE_PASTE, has(ModItems.SILICATE_PASTE.get())).build(consumer);

		EvolutionPoolRecipeBuilder.createRecipe(Items.ZOMBIE_HEAD, defaultTime * 2)
				.addIngredient(Items.PLAYER_HEAD).addIngredients(ModItems.ERODING_BILE.get(), 2).addIngredient(Items.ROTTEN_FLESH)
				.addCriterion("has_eroding_bile", has(ModItems.ERODING_BILE.get())).build(consumer);

		EvolutionPoolRecipeBuilder.createRecipe(Items.WITHER_SKELETON_SKULL, defaultTime * 3)
				.addIngredient(Items.SKELETON_SKULL).addIngredient(Items.WITHER_ROSE).addIngredients(ModItems.ERODING_BILE.get(), 2)
				.addCriterion("has_eroding_bile_and_wither_rose", inventoryTrigger(createPredicate(ModItems.ERODING_BILE.get()), createPredicate(Items.WITHER_ROSE)))
				.build(consumer);

		// Biometal ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		EvolutionPoolRecipeBuilder.createRecipe(ModItems.BIOMETAL.get(), defaultTime)
				.addIngredient(Items.IRON_INGOT).addIngredient(ModItems.FLESH_BLOCK.get()).addIngredients(ModItems.OXIDE_POWDER.get(), 2)
				.addIngredient(ModItems.KERATIN_FILAMENTS.get()).addIngredient(ModItems.HORMONE_BILE.get())
				.addCriterion(HAS_MUTAGENIC_BILE, has(ModItems.MUTAGENIC_BILE.get())).build(consumer);

		// Blocks //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		EvolutionPoolRecipeBuilder.createRecipe(ModItems.FLESHBORN_DOOR.get(), defaultTime)
				.addIngredient(Items.IRON_DOOR).addIngredient(ModItems.BIOMETAL.get()).addIngredients(ModItems.KERATIN_FILAMENTS.get(), 2)
				.addIngredient(ModItems.HORMONE_BILE.get()).addIngredient(ModItems.BONE_GEAR.get())
				.addCriterion(HAS_MUTAGENIC_BILE, has(ModItems.MUTAGENIC_BILE.get())).build(consumer);

		EvolutionPoolRecipeBuilder.createRecipe(ModItems.FLESHBORN_TRAPDOOR.get(), defaultTime)
				.addIngredient(Items.IRON_TRAPDOOR).addIngredient(ModItems.OCULUS.get()).addIngredient(ModItems.BIOMETAL.get())
				.addIngredient(ModItems.KERATIN_FILAMENTS.get()).addIngredient(ModItems.HORMONE_BILE.get()).addIngredient(ModItems.BONE_GEAR.get())
				.addCriterion(HAS_MUTAGENIC_BILE, has(ModItems.MUTAGENIC_BILE.get())).build(consumer);

		EvolutionPoolRecipeBuilder.createRecipe(ModItems.FLESHBORN_PRESSURE_PLATE.get(), defaultTime)
				.addIngredient(Items.HEAVY_WEIGHTED_PRESSURE_PLATE).addIngredient(ModItems.OCULUS.get()).addIngredient(ModItems.BIOMETAL.get())
				.addIngredient(ModItems.KERATIN_FILAMENTS.get()).addIngredient(ModItems.HORMONE_BILE.get()).addIngredient(ModItems.BONE_GEAR.get())
				.addCriterion(HAS_MUTAGENIC_BILE, has(ModItems.MUTAGENIC_BILE.get())).build(consumer);

		EvolutionPoolRecipeBuilder.createRecipe(ModItems.GULGE.get(), defaultTime * 2)
				.addIngredient(ModItems.FLESH_CHEST.get()).addIngredients(ModTags.Items.STOMACHS, 4).addIngredient(ModItems.BIOMETAL.get())
				.addCriterion(HAS_MUTAGENIC_BILE, has(ModItems.MUTAGENIC_BILE.get())).build(consumer);

		EvolutionPoolRecipeBuilder.createRecipe(ModItems.FLESH_CHEST.get(), defaultTime)
				.addIngredients(Tags.Items.CHESTS_WOODEN, 2).addIngredient(ModItems.BIOMETAL.get()).addIngredients(ModItems.KERATIN_FILAMENTS.get(), 2).addIngredient(ModItems.HORMONE_BILE.get())
				.addCriterion(HAS_MUTAGENIC_BILE, has(ModItems.MUTAGENIC_BILE.get())).build(consumer);

		// Tools ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		EvolutionPoolRecipeBuilder.createRecipe(ModItems.SINGLE_ITEM_BAG_ITEM.get(), defaultTime + 150)
				.addIngredients(ModTags.Items.STOMACHS, 2).addIngredient(Items.HOPPER).addIngredient(ModItems.FLESH_CHEST.get()).addIngredients(ModItems.KERATIN_FILAMENTS.get(), 2)
				.addCriterion(HAS_MUTAGENIC_BILE, has(ModItems.MUTAGENIC_BILE.get())).build(consumer);

		EvolutionPoolRecipeBuilder.createRecipe(ModItems.SMALL_ENTITY_BAG_ITEM.get(), defaultTime + 150)
				.addIngredients(ModTags.Items.STOMACHS, 2).addIngredient(Items.EGG).addIngredient(Tags.Items.CHESTS_ENDER).addIngredients(ModItems.KERATIN_FILAMENTS.get(), 2)
				.addCriterion(HAS_MUTAGENIC_BILE, has(ModItems.MUTAGENIC_BILE.get())).build(consumer);

		EvolutionPoolRecipeBuilder.createRecipe(ModItems.LARGE_ENTITY_BAG_ITEM.get(), defaultTime + 210)
				.addIngredients(ModItems.SMALL_ENTITY_BAG_ITEM.get(), 3).addIngredients(ModItems.KERATIN_FILAMENTS.get(), 3)
				.addCriterion("has_entity_bag", has(ModItems.SMALL_ENTITY_BAG_ITEM.get())).build(consumer);

		EvolutionPoolRecipeBuilder.createRecipe(ModItems.OCULI_OF_UNVEILING.get(), defaultTime * 2)
				.addIngredient(Items.IRON_HELMET).addIngredients(ModItems.OCULUS.get(), 4).addIngredient(ModItems.BIOMETAL.get())
				.addCriterion(HAS_MUTAGENIC_BILE, hasItems(ModItems.MUTAGENIC_BILE.get(), ModItems.MENISCUS_LENS.get())).build(consumer);

		EvolutionPoolRecipeBuilder.createRecipe(ModItems.FLESHBORN_AXE.get(), defaultTime)
				.addIngredient(Items.IRON_AXE).addIngredient(Tags.Items.BONES).addIngredients(ModItems.BIOMETAL.get(), 3).addIngredient(ModItems.HORMONE_BILE.get())
				.addCriterion(HAS_MUTAGENIC_BILE, has(ModItems.MUTAGENIC_BILE.get())).build(consumer);

		EvolutionPoolRecipeBuilder.createRecipe(ModItems.FLESHBORN_SHOVEL.get(), defaultTime)
				.addIngredient(Items.IRON_SHOVEL).addIngredient(Tags.Items.BONES).addIngredient(ModItems.BIOMETAL.get()).addIngredients(ModItems.KERATIN_FILAMENTS.get(), 2).addIngredient(ModItems.HORMONE_BILE.get())
				.addCriterion(HAS_MUTAGENIC_BILE, has(ModItems.MUTAGENIC_BILE.get())).build(consumer);

		EvolutionPoolRecipeBuilder.createRecipe(ModItems.FLESHBORN_PICKAXE.get(), defaultTime)
				.addIngredient(Items.IRON_PICKAXE).addIngredient(Tags.Items.BONES).addIngredients(ModItems.BIOMETAL.get(), 3).addIngredient(ModItems.HORMONE_BILE.get())
				.addCriterion(HAS_MUTAGENIC_BILE, has(ModItems.MUTAGENIC_BILE.get())).build(consumer);

		// Weapons /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		EvolutionPoolRecipeBuilder.createRecipe(ModItems.FLESHBORN_WAR_AXE.get(), defaultTime * 2)
				.addIngredient(ModItems.FLESHBORN_AXE.get()).addIngredient(ModItems.OCULUS.get()).addIngredient(ModItems.HORMONE_BILE.get()).addIngredient(ModTags.Items.STOMACHS)
				.addIngredients(ModItems.KERATIN_FILAMENTS.get(), 2)
				.addCriterion("has_fleshborn_axe", has(ModItems.FLESHBORN_AXE.get())).build(consumer);

		ItemStackIngredient sweepingBook = new ItemStackIngredient(EnchantedBookItem.createForEnchantment(new EnchantmentData(Enchantments.SWEEPING_EDGE, Enchantments.SWEEPING_EDGE.getMaxLevel())));
		EvolutionPoolRecipeBuilder.createRecipe(ModItems.LONG_RANGE_CLAW.get(), defaultTime * 2)
				.addIngredient(ModItems.OCULUS.get()).addIngredient(sweepingBook).addIngredients(ModItems.SHARP_BONE.get(), 2).addIngredient(Items.DIAMOND_SWORD).addIngredient(ModItems.BIOMETAL.get())
				.addCriterion(HAS_OCULUS, has(ModItems.OCULUS.get())).build(consumer);

		ItemStackIngredient mendingBook = new ItemStackIngredient(EnchantedBookItem.createForEnchantment(new EnchantmentData(Enchantments.MENDING, Enchantments.MENDING.getMaxLevel())));
		EvolutionPoolRecipeBuilder.createRecipe(ModItems.LEECH_CLAW.get(), defaultTime * 2)
				.addIngredient(ModItems.OCULUS.get()).addIngredient(ModItems.INJECTION_DEVICE.get()).addIngredient(mendingBook).addIngredient(Items.DIAMOND_SWORD).addIngredient(ModItems.BIOMETAL.get())
				.addCriterion(HAS_OCULUS, has(ModItems.OCULUS.get())).build(consumer);

		ItemStackIngredient maxBaneBook = new ItemStackIngredient(EnchantedBookItem.createForEnchantment(new EnchantmentData(ModEnchantments.ATTUNED_BANE.get(), ModEnchantments.ATTUNED_BANE.get().getMaxLevel())));
		EvolutionPoolRecipeBuilder.createRecipe(ModItems.FLESHBORN_GUAN_DAO.get(), defaultTime * 2)
				.addIngredient(ModItems.OCULUS.get()).addIngredient(maxBaneBook).addIngredient(Tags.Items.BONES).addIngredient(Items.DIAMOND_SWORD).addIngredient(Items.DIAMOND_AXE).addIngredient(ModItems.BIOMETAL.get())
				.addCriterion(HAS_OCULUS, has(ModItems.OCULUS.get())).build(consumer);

		EvolutionPoolRecipeBuilder.createRecipe(ModItems.TOOTH_GUN.get(), defaultTime * 2)
				.addIngredient(ModItems.INJECTION_DEVICE.get()).addIngredient(Items.SKELETON_SKULL).addIngredient(Items.CROSSBOW)
				.addIngredient(ModTags.Items.STOMACHS).addIngredient(ModItems.BIOMETAL.get()).addIngredient(ModItems.BONE_GEAR.get())
				.addCriterion("has_injection_device", has(ModItems.INJECTION_DEVICE.get())).build(consumer);

		EvolutionPoolRecipeBuilder.createRecipe(ModItems.WITHERSHOT.get(), defaultTime * 2)
				.addIngredient(ModItems.TOOTH_GUN.get()).addIngredient(Items.NETHER_STAR).addIngredient(ModItems.ERODING_BILE.get()).addIngredient(Items.WITHER_SKELETON_SKULL).addIngredient(ModItems.BONE_GEAR.get())
				.addCriterion("has_tooth_gun", has(ModItems.TOOTH_GUN.get())).build(consumer);

		// Enchantments ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		Enchantment enchantment = ModEnchantments.ATTUNED_BANE.get();
		for (int level = enchantment.getMinLevel(); level <= enchantment.getMaxLevel(); ++level) {
			ItemStack minBaneBook = EnchantedBookItem.createForEnchantment(new EnchantmentData(ModEnchantments.ATTUNED_BANE.get(), level));
			ItemStackIngredient sharpnessBook = new ItemStackIngredient(EnchantedBookItem.createForEnchantment(new EnchantmentData(Enchantments.SHARPNESS, level)));
			ItemStackIngredient smiteBook = new ItemStackIngredient(EnchantedBookItem.createForEnchantment(new EnchantmentData(Enchantments.SMITE, level)));
			ItemStackIngredient arthropodsBook = new ItemStackIngredient(EnchantedBookItem.createForEnchantment(new EnchantmentData(Enchantments.BANE_OF_ARTHROPODS, level)));
			EvolutionPoolRecipeBuilder.createRecipe(minBaneBook, defaultTime + 125 * level)
					.addIngredient(sharpnessBook).addIngredient(smiteBook).addIngredient(arthropodsBook).addIngredient(ModTags.Items.STOMACHS).addIngredient(ModItems.ERODING_BILE.get()).addIngredient(ModItems.REJUVENATING_MUCUS.get())
					.addCriterion("has_smite_enchant", inventoryTrigger(ItemPredicate.Builder.item().hasEnchantment(new EnchantmentPredicate(Enchantments.SMITE, MinMaxBounds.IntBound.atLeast(1))).build())).build(consumer, "attuned_bane_" + level, true);
		}
	}

	private void registerOxidesRecipes(Consumer<IFinishedRecipe> consumer) {
		final int defaultDecomposingTime = 300;
		int id = 0;

		DecomposerRecipeBuilder.createRecipe(ModItems.OXIDE_POWDER.get(), defaultDecomposingTime, 1)
				.setIngredient(Items.RED_SAND, 2)
				.addByproduct(ModItems.OXIDE_POWDER.get(), 0.5f)
				.addByproduct(ModItems.SILICATE_PASTE.get(), 2, 0.8f)
				.addCriterion("has_red_sand", has(Items.RED_SAND)).build(consumer, "from_red_sand", true);

		DecomposerRecipeBuilder.createRecipe(ModItems.OXIDE_POWDER.get(), defaultDecomposingTime - 100)
				.setIngredient(Tags.Items.EGGS, 6)
				.addByproduct(ModItems.MUTAGENIC_BILE.get(), 0.1f)
				.addCriterion(HAS_OXIDE, OXIDE_CRITERION).build(consumer, "" + id++, true);

		DecomposerRecipeBuilder.createRecipe(ModItems.OXIDE_POWDER.get(), defaultDecomposingTime - 100)
				.setIngredient(Items.TURTLE_EGG, 6)
				.addByproduct(ModItems.MUTAGENIC_BILE.get(), 0.2f)
				.addCriterion(HAS_OXIDE, OXIDE_CRITERION).build(consumer, "" + id++, true);

		DecomposerRecipeBuilder.createRecipe(ModItems.OXIDE_POWDER.get(), defaultDecomposingTime - 100)
				.setIngredient(Tags.Items.BONES, 2)
				.addCriterion(HAS_OXIDE, OXIDE_CRITERION).build(consumer, "" + id++, true);

		DecomposerRecipeBuilder.createRecipe(ModItems.OXIDE_POWDER.get(), defaultDecomposingTime - 100)
				.setIngredient(Items.BONE_MEAL, 6)
				.addCriterion(HAS_OXIDE, OXIDE_CRITERION).build(consumer, "" + id++, true);

		DecomposerRecipeBuilder.createRecipe(ModItems.OXIDE_POWDER.get(), defaultDecomposingTime - 100)
				.setIngredient(ModItems.MILK_GEL.get(), 2)
				.addCriterion(HAS_OXIDE, OXIDE_CRITERION).build(consumer, "" + id, true);
	}

	private void registerSilicatesRecipes(Consumer<IFinishedRecipe> consumer) {
		final int defaultDecomposingTime = 200;
		int id = 0;

		DecomposerRecipeBuilder.createRecipe(ModItems.SILICATE_PASTE.get(), defaultDecomposingTime * 2)
				.setIngredient(Items.ANDESITE, 2)
				.addCriterion(HAS_SILICATES, SILICATES_CRITERION).build(consumer, "" + id++, true);

		DecomposerRecipeBuilder.createRecipe(ModItems.SILICATE_PASTE.get(), defaultDecomposingTime * 2)
				.setIngredient(Items.DIORITE, 2)
				.addCriterion(HAS_SILICATES, SILICATES_CRITERION).build(consumer, "" + id++, true);

		DecomposerRecipeBuilder.createRecipe(ModItems.SILICATE_PASTE.get(), defaultDecomposingTime * 2)
				.setIngredient(Items.GRANITE, 2)
				.addByproduct(ModItems.SILICATE_PASTE.get(), 0.5f)
				.addCriterion(HAS_SILICATES, SILICATES_CRITERION).build(consumer, "" + id++, true);

		DecomposerRecipeBuilder.createRecipe(ModItems.SILICATE_PASTE.get(), defaultDecomposingTime + 150)
				.setIngredient(Items.REDSTONE, 6)
				.addCriterion(HAS_SILICATES, SILICATES_CRITERION).build(consumer, "" + id++, true);

		DecomposerRecipeBuilder.createRecipe(ModItems.SILICATE_PASTE.get(), defaultDecomposingTime, 3)
				.setIngredient(Items.NAUTILUS_SHELL)
				.addByproduct(ModItems.MUTAGENIC_BILE.get(), 2, 1f)
				.addCriterion(HAS_SILICATES, SILICATES_CRITERION).build(consumer, "" + id++, true);

		DecomposerRecipeBuilder.createRecipe(ModItems.SILICATE_PASTE.get(), defaultDecomposingTime, 4)
				.setIngredient(Items.SHULKER_SHELL)
				.addCriterion(HAS_SILICATES, SILICATES_CRITERION).build(consumer, "" + id++, true);

		DecomposerRecipeBuilder.createRecipe(ModItems.SILICATE_PASTE.get(), defaultDecomposingTime + 150)
				.setIngredient(Items.GLOWSTONE_DUST, 6)
				.addCriterion(HAS_SILICATES, SILICATES_CRITERION).build(consumer, "" + id++, true);

		DecomposerRecipeBuilder.createRecipe(ModItems.SILICATE_PASTE.get(), defaultDecomposingTime * 2, 3)
				.setIngredient(Tags.Items.GEMS_EMERALD)
				.addCriterion(HAS_SILICATES, SILICATES_CRITERION).build(consumer, "" + id++, true);

		DecomposerRecipeBuilder.createRecipe(ModItems.SILICATE_PASTE.get(), defaultDecomposingTime * 2)
				.setIngredient(Tags.Items.GEMS_LAPIS)
				.addCriterion(HAS_SILICATES, SILICATES_CRITERION).build(consumer, "" + id++, true);

		DecomposerRecipeBuilder.createRecipe(ModItems.SILICATE_PASTE.get(), defaultDecomposingTime * 2)
				.setIngredient(Tags.Items.GEMS_QUARTZ)
				.addByproduct(ModItems.SILICATE_PASTE.get(), 0.5f)
				.addCriterion(HAS_SILICATES, SILICATES_CRITERION).build(consumer, "" + id++, true);

		DecomposerRecipeBuilder.createRecipe(ModItems.SILICATE_PASTE.get(), defaultDecomposingTime * 2, 3)
				.setIngredient(Tags.Items.GEMS_PRISMARINE)
				.addCriterion(HAS_SILICATES, SILICATES_CRITERION).build(consumer, "" + id++, true);

		DecomposerRecipeBuilder.createRecipe(ModItems.SILICATE_PASTE.get(), defaultDecomposingTime + 100)
				.setIngredient(Items.KELP, 6)
				.addByproduct(ModItems.DIGESTATE.get(), 0.3f)
				.addCriterion(HAS_SILICATES, SILICATES_CRITERION).build(consumer, "" + id++, true);

		DecomposerRecipeBuilder.createRecipe(ModItems.SILICATE_PASTE.get(), defaultDecomposingTime)
				.setIngredient(Items.DRIED_KELP, 6)
				.addByproduct(ModItems.DIGESTATE.get(), 0.3f)
				.addCriterion(HAS_SILICATES, SILICATES_CRITERION).build(consumer, "" + id++, true);

		DecomposerRecipeBuilder.createRecipe(ModItems.SILICATE_PASTE.get(), defaultDecomposingTime)
				.setIngredient(Items.DRIED_KELP_BLOCK)
				.addByproduct(ModItems.SILICATE_PASTE.get(), 0.5f)
				.addByproduct(ModItems.DIGESTATE.get(), 0.4f)
				.addCriterion(HAS_SILICATES, SILICATES_CRITERION).build(consumer, "" + id, true);
	}

	private void registerKeratinsRecipes(Consumer<IFinishedRecipe> consumer) {
		final int defaultDecomposingTime = 200;
		int id = 0;

		DecomposerRecipeBuilder.createRecipe(ModItems.KERATIN_FILAMENTS.get(), defaultDecomposingTime * 2, 3)
				.setIngredient(Items.SCUTE)
				.addCriterion(HAS_KERATINS, has(ModTags.Items.KERATINS)).build(consumer, "" + id++, true);

		DecomposerRecipeBuilder.createRecipe(ModItems.KERATIN_FILAMENTS.get(), defaultDecomposingTime)
				.setIngredient(Tags.Items.LEATHER)
				.addCriterion(HAS_KERATINS, has(ModTags.Items.KERATINS)).build(consumer, "" + id++, true);

		DecomposerRecipeBuilder.createRecipe(ModItems.KERATIN_FILAMENTS.get(), defaultDecomposingTime, 2)
				.setIngredient(Items.RABBIT_HIDE)
				.addCriterion(HAS_KERATINS, has(ModTags.Items.KERATINS)).build(consumer, "" + id++, true);

		DecomposerRecipeBuilder.createRecipe(ModItems.KERATIN_FILAMENTS.get(), defaultDecomposingTime)
				.setIngredient(Tags.Items.FEATHERS, 4)
				.addCriterion(HAS_KERATINS, has(ModTags.Items.KERATINS)).build(consumer, "" + id++, true);

		DecomposerRecipeBuilder.createRecipe(ModItems.KERATIN_FILAMENTS.get(), defaultDecomposingTime)
				.setIngredient(Tags.Items.STRING, 4)
				.addCriterion(HAS_KERATINS, has(ModTags.Items.KERATINS)).build(consumer, "" + id++, true);

		DecomposerRecipeBuilder.createRecipe(ModItems.KERATIN_FILAMENTS.get(), defaultDecomposingTime)
				.setIngredient(ItemTags.WOOL)
				.addCriterion(HAS_KERATINS, has(ModTags.Items.KERATINS)).build(consumer, "" + id++, true);

		DecomposerRecipeBuilder.createRecipe(ModItems.KERATIN_FILAMENTS.get(), defaultDecomposingTime)
				.setIngredient(ItemTags.CARPETS)
				.addCriterion(HAS_KERATINS, has(ModTags.Items.KERATINS)).build(consumer, "" + id++, true);

		DecomposerRecipeBuilder.createRecipe(ModItems.KERATIN_FILAMENTS.get(), defaultDecomposingTime)
				.setIngredient(Items.LEAD)
				.addCriterion(HAS_KERATINS, has(ModTags.Items.KERATINS)).build(consumer, "" + id++, true);

		DecomposerRecipeBuilder.createRecipe(ModItems.KERATIN_FILAMENTS.get(), defaultDecomposingTime * 2, 3)
				.setIngredient(Items.PHANTOM_MEMBRANE)
				.addCriterion(HAS_KERATINS, has(ModTags.Items.KERATINS)).build(consumer, "" + id++, true);

		DecomposerRecipeBuilder.createRecipe(ModItems.KERATIN_FILAMENTS.get(), defaultDecomposingTime * 2)
				.setIngredient(Items.PRISMARINE_SHARD)
				.addCriterion(HAS_KERATINS, has(ModTags.Items.KERATINS)).build(consumer, "" + id, true);
	}

	private void registerHormonesRecipes(Consumer<IFinishedRecipe> consumer) {
		final int defaultDecomposingTime = 200;
		int id = 0;

		DecomposerRecipeBuilder.createRecipe(ModItems.HORMONE_BILE.get(), defaultDecomposingTime)
				.setIngredient(Items.INK_SAC, 2)
				.addCriterion(HAS_HORMONES, has(ModTags.Items.HORMONES)).build(consumer, "" + id++, true);

		DecomposerRecipeBuilder.createRecipe(ModItems.HORMONE_BILE.get(), defaultDecomposingTime, 3)
				.setIngredient(Items.RABBIT_FOOT)
				.addCriterion(HAS_HORMONES, has(ModTags.Items.HORMONES)).build(consumer, "" + id++, true);

		DecomposerRecipeBuilder.createRecipe(ModItems.HORMONE_BILE.get(), defaultDecomposingTime, 3)
				.setIngredient(Items.GHAST_TEAR)
				.addCriterion(HAS_HORMONES, has(ModTags.Items.HORMONES)).build(consumer, "" + id++, true);

		DecomposerRecipeBuilder.createRecipe(ModItems.HORMONE_BILE.get(), defaultDecomposingTime * 3)
				.setIngredient(Items.SPIDER_EYE, 6)
				.addCriterion(HAS_HORMONES, has(ModTags.Items.HORMONES)).build(consumer, "" + id++, true);

		DecomposerRecipeBuilder.createRecipe(ModItems.HORMONE_BILE.get(), defaultDecomposingTime * 2)
				.setIngredient(Items.FERMENTED_SPIDER_EYE, 4)
				.addCriterion(HAS_HORMONES, has(ModTags.Items.HORMONES)).build(consumer, "" + id++, true);

		DecomposerRecipeBuilder.createRecipe(ModItems.HORMONE_BILE.get(), defaultDecomposingTime)
				.setIngredient(Items.HONEYCOMB, 2)
				.addCriterion(HAS_HORMONES, has(ModTags.Items.HORMONES)).build(consumer, "" + id++, true);

		DecomposerRecipeBuilder.createRecipe(ModItems.HORMONE_BILE.get(), defaultDecomposingTime)
				.setIngredient(Items.SLIME_BALL)
				.addByproduct(ModItems.MUTAGENIC_BILE.get(), 0.15f)
				.addByproduct(ModItems.ERODING_BILE.get(), 0.2f)
				.addByproduct(ModItems.REJUVENATING_MUCUS.get(), 0.2f)
				.addCriterion("has_slime_ball", has(Items.SLIME_BALL)).build(consumer, "" + id, true);
	}

	private void registerMutagenRecipes(Consumer<IFinishedRecipe> consumer) {
		final int defaultDecomposingTime = 200;
		int id = 0;

		DecomposerRecipeBuilder.createRecipe(ModItems.MUTAGENIC_BILE.get(), defaultDecomposingTime)
				.setIngredient(Items.WARPED_FUNGUS)
				.addByproduct(ModItems.DIGESTATE.get(), 0.4f)
				.addCriterion("has_warped_fungus", has(Items.WARPED_FUNGUS)).build(consumer, "" + id++, true);

		DecomposerRecipeBuilder.createRecipe(ModItems.MUTAGENIC_BILE.get(), defaultDecomposingTime, 6)
				.setIngredient(Items.WARPED_WART_BLOCK)
				.addByproduct(ModItems.DIGESTATE.get(), 0.6f)
				.addCriterion("has_warped_fungus", has(Items.WARPED_FUNGUS)).build(consumer, "" + id++, true);

		DecomposerRecipeBuilder.createRecipe(ModItems.MUTAGENIC_BILE.get(), defaultDecomposingTime)
				.setIngredient(Items.WARPED_ROOTS, 2)
				.addByproduct(ModItems.DIGESTATE.get(), 0.4f)
				.addCriterion("has_warped_roots", has(Items.WARPED_ROOTS)).build(consumer, "" + id, true);
	}

	private void registerChewerRecipes(Consumer<IFinishedRecipe> consumer) {
		final int defaultChewingTime = 200;

		// crushed biomass /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		ChewerRecipeBuilder.createRecipe(ModItems.BOLUS.get(), 105, 2)
				.setIngredient(ModTags.Items.POOR_BIOMASS)
				.addCriterion("has_poor_biomass", has(ModTags.Items.POOR_BIOMASS)).build(consumer, "from_poor_biomass", true);

		ChewerRecipeBuilder.createRecipe(ModItems.BOLUS.get(), 195, 4)
				.setIngredient(ModTags.Items.AVERAGE_BIOMASS)
				.addCriterion("has_average_biomass", has(ModTags.Items.AVERAGE_BIOMASS)).build(consumer, "from_average_biomass", true);

		ChewerRecipeBuilder.createRecipe(ModItems.BOLUS.get(), 272, 6)
				.setIngredient(ModTags.Items.GOOD_BIOMASS)
				.addCriterion("has_good_biomass", has(ModTags.Items.GOOD_BIOMASS)).build(consumer, "from_good_biomass", true);

		ChewerRecipeBuilder.createRecipe(ModItems.BOLUS.get(), 300, 8)
				.setIngredient(ModTags.Items.SUPERB_BIOMASS)
				.addCriterion("has_superb_biomass", has(ModTags.Items.SUPERB_BIOMASS)).build(consumer, "from_superb_biomass", true);

		// misc ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		ChewerRecipeBuilder.createRecipe(Items.COBBLESTONE, defaultChewingTime)
				.setIngredient(Items.STONE)
				.addCriterion("has_stone", has(Items.STONE)).build(consumer, "from_stone", true);

		ChewerRecipeBuilder.createRecipe(Items.GRAVEL, defaultChewingTime * 2)
				.setIngredient(Items.COBBLESTONE)
				.addCriterion("has_cobble", has(Items.COBBLESTONE)).build(consumer, "from_cobblestone", true);

		ChewerRecipeBuilder.createRecipe(Items.SAND, defaultChewingTime * 3)
				.setIngredient(Items.GRAVEL)
				.addCriterion("has_gravel", has(Items.GRAVEL)).build(consumer, "from_gravel", true);

		ChewerRecipeBuilder.createRecipe(Items.SAND, defaultChewingTime * 3, 4)
				.setIngredient(Items.SANDSTONE)
				.addCriterion("has_sandstone", has(Items.SANDSTONE)).build(consumer, "from_sandstone", true);

		ChewerRecipeBuilder.createRecipe(Items.RED_SAND, defaultChewingTime * 3, 4)
				.setIngredient(Items.RED_SANDSTONE)
				.addCriterion("has_red_sandstone", has(Items.RED_SANDSTONE)).build(consumer, "from_red_sandstone", true);

		ChewerRecipeBuilder.createRecipe(ModItems.FLESH_LUMP.get(), defaultChewingTime, 9)
				.setIngredient(ModItems.FLESH_BLOCK.get())
				.addCriterion(HAS_FLESH_BLOCK, has(ModItems.FLESH_BLOCK.get())).build(consumer, "from_flesh_block", true);

		ChewerRecipeBuilder.createRecipe(ModItems.FLESH_LUMP.get(), defaultChewingTime, 4)
				.setIngredient(ModItems.FLESH_BLOCK_SLAB.get())
				.addCriterion(HAS_FLESH_BLOCK, has(ModItems.FLESH_BLOCK_SLAB.get())).build(consumer, "from_flesh_block_slab", true);

		ChewerRecipeBuilder.createRecipe(ModItems.FLESH_LUMP.get(), defaultChewingTime, 13)
				.setIngredient(ModItems.FLESH_BLOCK_STAIRS.get())
				.addCriterion(HAS_FLESH_BLOCK, has(ModItems.FLESH_BLOCK_STAIRS.get())).build(consumer, "from_flesh_block_stairs", true);

		// silicates ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		ChewerRecipeBuilder.createRecipe(ModItems.SILICATE_PASTE.get(), defaultChewingTime * 4, 2)
				.setIngredient(Items.SAND)
				.addCriterion("has_sand", has(Items.SAND)).build(consumer, "from_sand", true);

		ChewerRecipeBuilder.createRecipe(ModItems.SILICATE_PASTE.get(), defaultChewingTime * 4, 1) //give less silicate because the sand contains oxides
				.setIngredient(Items.RED_SAND)
				.addCriterion("has_red_sand", has(Items.RED_SAND)).build(consumer, "from_red_sand", true);

		// oxides //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		ChewerRecipeBuilder.createRecipe(ModItems.OXIDE_POWDER.get(), defaultChewingTime * 3, 9)
				.setIngredient(Tags.Items.INGOTS_IRON)
				.addCriterion(HAS_OXIDE, OXIDE_CRITERION).build(consumer, "from_iron_ingot", true);

		ChewerRecipeBuilder.createRecipe(ModItems.OXIDE_POWDER.get(), defaultChewingTime)
				.setIngredient(Tags.Items.NUGGETS_IRON)
				.addCriterion(HAS_OXIDE, OXIDE_CRITERION).build(consumer, "from_iron_nugget", true);

		ChewerRecipeBuilder.createRecipe(ModItems.OXIDE_POWDER.get(), defaultChewingTime * 3, 9)
				.setIngredient(Tags.Items.INGOTS_GOLD)
				.addCriterion(HAS_OXIDE, OXIDE_CRITERION).build(consumer, "from_gold_ingot", true);

		ChewerRecipeBuilder.createRecipe(ModItems.OXIDE_POWDER.get(), defaultChewingTime)
				.setIngredient(Tags.Items.NUGGETS_GOLD)
				.addCriterion(HAS_OXIDE, OXIDE_CRITERION).build(consumer, "from_gold_nugget", true);

		ChewerRecipeBuilder.createRecipe(ModItems.OXIDE_POWDER.get(), defaultChewingTime, 6)
				.setIngredient(Items.NETHERITE_SCRAP)
				.addCriterion(HAS_OXIDE, OXIDE_CRITERION).build(consumer, "from_netherite_scrap", true);

		ChewerRecipeBuilder.createRecipe(ModItems.OXIDE_POWDER.get(), defaultChewingTime * 3, 4 * 6 + 4 * 9)
				.setIngredient(Tags.Items.INGOTS_NETHERITE)
				.addCriterion(HAS_OXIDE, OXIDE_CRITERION).build(consumer, "from_netherite_ingot", true);
	}

	private void registerDigesterRecipes(Consumer<IFinishedRecipe> consumer) {
		final int defaultFuelAmount = BiofuelUtil.DEFAULT_FUEL_VALUE * BiofuelUtil.NUTRIENT_PASTE_MULTIPLIER;

		DigesterRecipeBuilder.createRecipe(ModFluids.NUTRIENT_SLURRY.get(), 100, defaultFuelAmount)
				.setIngredient(ModItems.NUTRIENT_PASTE.get())
				.addCriterion("has_nutrient_paste", has(ModItems.NUTRIENT_PASTE.get())).build(consumer, "from_nutrient_paste", true);

		DigesterRecipeBuilder.createRecipe(ModFluids.NUTRIENT_SLURRY.get(), 600, BiofuelUtil.DEFAULT_FUEL_VALUE * BiofuelUtil.NUTRIENT_BAR_MULTIPLIER)
				.setIngredient(ModItems.NUTRIENT_BAR.get())
				.addCriterion("has_nutrient_bar", has(ModItems.NUTRIENT_BAR.get())).build(consumer, "from_nutrient_bar", true);

		DigesterRecipeBuilder.createRecipe(ModFluids.NUTRIENT_SLURRY.get(), 100, 2 * defaultFuelAmount)
				.setIngredient(ModItems.BOLUS.get())
				.setByproduct(ModItems.DIGESTATE.get(), 0.85f)
				.addCriterion("has_crushed_biomass", has(ModItems.BOLUS.get())).build(consumer, "from_bolus", true);

		DigesterRecipeBuilder.createRecipe(ModFluids.NUTRIENT_SLURRY.get(), 189, defaultFuelAmount)
				.setIngredient(ModTags.Items.POOR_BIOMASS)
				.setByproduct(ModItems.DIGESTATE.get(), 0.15f)
				.addCriterion("has_poor_biomass", has(ModTags.Items.POOR_BIOMASS)).build(consumer, "from_poor_biomass", true);

		DigesterRecipeBuilder.createRecipe(ModFluids.NUTRIENT_SLURRY.get(), 351, 2 * defaultFuelAmount)
				.setIngredient(ModTags.Items.AVERAGE_BIOMASS)
				.setByproduct(ModItems.DIGESTATE.get(), 0.3f)
				.addCriterion("has_average_biomass", has(ModTags.Items.AVERAGE_BIOMASS)).build(consumer, "from_average_biomass", true);

		DigesterRecipeBuilder.createRecipe(ModFluids.NUTRIENT_SLURRY.get(), 351, 2 * defaultFuelAmount)
				.setIngredient(ModTags.Items.RAW_MEATS)
				.setByproduct(ModItems.DIGESTATE.get(), 0.3f)
				.addCriterion("has_raw_meat", has(ModTags.Items.RAW_MEATS)).build(consumer, "from_raw_meat", true);

		DigesterRecipeBuilder.createRecipe(ModFluids.NUTRIENT_SLURRY.get(), 490, 3 * defaultFuelAmount)
				.setIngredient(ModTags.Items.GOOD_BIOMASS)
				.setByproduct(ModItems.DIGESTATE.get(), 0.5f)
				.addCriterion("has_good_biomass", has(ModTags.Items.GOOD_BIOMASS)).build(consumer, "from_good_biomass", true);

		DigesterRecipeBuilder.createRecipe(ModFluids.NUTRIENT_SLURRY.get(), 490, 3 * defaultFuelAmount)
				.setIngredient(ModTags.Items.COOKED_MEATS)
				.setByproduct(ModItems.DIGESTATE.get(), 0.5f)
				.addCriterion("has_cooked_meat", has(ModTags.Items.COOKED_MEATS)).build(consumer, "from_cooked_meat", true);

		DigesterRecipeBuilder.createRecipe(ModFluids.NUTRIENT_SLURRY.get(), 540, 4 * defaultFuelAmount)
				.setIngredient(ModTags.Items.SUPERB_BIOMASS)
				.setByproduct(ModItems.DIGESTATE.get(), 0.6f)
				.addCriterion("has_superb_biomass", has(ModTags.Items.SUPERB_BIOMASS)).build(consumer, "from_superb_biomass", true);
	}

	private void registerSolidifierRecipes(Consumer<IFinishedRecipe> consumer) {
		final int defaultFuelAmount = BiofuelUtil.DEFAULT_FUEL_VALUE * BiofuelUtil.NUTRIENT_PASTE_MULTIPLIER;
		SolidifierRecipeBuilder.createRecipe(ModItems.NUTRIENT_PASTE.get(), 40, 1)
				.setFluidIngredient(ModFluids.NUTRIENT_SLURRY.get(), defaultFuelAmount)
				.addCriterion("has_nutrient_slurry", has(ModItems.NUTRIENT_SLURRY_BUCKET.get())).build(consumer, "from_nutrient_slurry", true);

		SolidifierRecipeBuilder.createRecipe(ModItems.MILK_GEL.get(), 200, 1)
				.setFluidIngredient(Tags.Fluids.MILK, FluidAttributes.BUCKET_VOLUME)
				.addCriterion("has_milk", has(Items.MILK_BUCKET)).build(consumer, "from_milk", true);
	}

	private void registerDecomposerRecipes(Consumer<IFinishedRecipe> consumer) {
		final int defaultDecomposingTime = 200;

		registerMutagenRecipes(consumer);
		registerOxidesRecipes(consumer);
		registerSilicatesRecipes(consumer);
		registerKeratinsRecipes(consumer);
		registerHormonesRecipes(consumer);

		//removed mutagenic bile from raw chicken recipe due to recipe clash with flesh lumps from RAW_MEATS item tag

		DecomposerRecipeBuilder.createRecipe(ModItems.FLESH_LUMP.get(), defaultDecomposingTime, 2)
				.setIngredient(ModTags.Items.RAW_MEATS)
				.addByproduct(ModItems.SKIN_CHUNK.get(), 0.4f)
				.addByproduct(ModItems.BONE_SCRAPS.get(), 0.2f)
				.addCriterion("has_raw_meat", has(ModTags.Items.RAW_MEATS)).build(consumer, "from_raw_meat", true);

		DecomposerRecipeBuilder.createRecipe(ModItems.REJUVENATING_MUCUS.get(), defaultDecomposingTime)
				.setIngredient(Items.SUSPICIOUS_STEW)
				.addByproduct(ModItems.MUTAGENIC_BILE.get(), 0.15f)
				.addCriterion("has_suspicious_stew", has(Items.SUSPICIOUS_STEW)).build(consumer);

		//rejuvenating mucus from golden food
		DecomposerRecipeBuilder.createRecipe(ModItems.REJUVENATING_MUCUS.get(), defaultDecomposingTime, 2)
				.setIngredient(Items.GOLDEN_CARROT)
				.addByproduct(ModItems.OXIDE_POWDER.get(), 4, 1f)
				.addCriterion("has_golden_carrot", has(Items.GOLDEN_CARROT)).build(consumer, "from_golden_carrot", true);

		DecomposerRecipeBuilder.createRecipe(ModItems.REJUVENATING_MUCUS.get(), defaultDecomposingTime, 6)
				.setIngredient(Items.GOLDEN_APPLE)
				.addByproduct(ModItems.OXIDE_POWDER.get(), 8 * 2, 1f)
				.addCriterion("has_golden_apple", has(Items.GOLDEN_APPLE)).build(consumer, "from_golden_apple", true);

		DecomposerRecipeBuilder.createRecipe(ModItems.REJUVENATING_MUCUS.get(), defaultDecomposingTime, 8)
				.setIngredient(Items.ENCHANTED_GOLDEN_APPLE)
				.addByproduct(ModItems.OXIDE_POWDER.get(), 8 * 2, 1f)
				.addCriterion("has_enchanted_golden_apple", has(Items.ENCHANTED_GOLDEN_APPLE)).build(consumer, "from_enchanted_golden_apple", true);

		//rejuvenating mucus from healing/health potions
		DecomposerRecipeBuilder.createRecipe(ModItems.REJUVENATING_MUCUS.get(), defaultDecomposingTime / 2, 4)
				.setIngredient(new ItemStackIngredient(PotionUtils.setPotion(new ItemStack(Items.POTION), Potions.REGENERATION)))
				.addByproduct(Items.GLASS_BOTTLE)
				.addCriterion("has_potion", has(Items.POTION)).build(consumer, "from_regen_potion", true);

		DecomposerRecipeBuilder.createRecipe(ModItems.REJUVENATING_MUCUS.get(), defaultDecomposingTime / 2, 6)
				.setIngredient(new ItemStackIngredient(PotionUtils.setPotion(new ItemStack(Items.POTION), Potions.LONG_REGENERATION)))
				.addByproduct(Items.GLASS_BOTTLE)
				.addCriterion("has_potion", has(Items.POTION)).build(consumer, "from_long_regen_potion", true);

		DecomposerRecipeBuilder.createRecipe(ModItems.REJUVENATING_MUCUS.get(), defaultDecomposingTime / 2, 6)
				.setIngredient(new ItemStackIngredient(PotionUtils.setPotion(new ItemStack(Items.POTION), Potions.STRONG_REGENERATION)))
				.addByproduct(Items.GLASS_BOTTLE)
				.addCriterion("has_potion", has(Items.POTION)).build(consumer, "from_strong_regen_potion", true);

		DecomposerRecipeBuilder.createRecipe(ModItems.REJUVENATING_MUCUS.get(), defaultDecomposingTime / 2, 4)
				.setIngredient(new ItemStackIngredient(PotionUtils.setPotion(new ItemStack(Items.POTION), Potions.HEALING)))
				.addByproduct(Items.GLASS_BOTTLE)
				.addCriterion("has_potion", has(Items.POTION)).build(consumer, "from_healing_potion", true);

		DecomposerRecipeBuilder.createRecipe(ModItems.REJUVENATING_MUCUS.get(), defaultDecomposingTime / 2, 6)
				.setIngredient(new ItemStackIngredient(PotionUtils.setPotion(new ItemStack(Items.POTION), Potions.STRONG_HEALING)))
				.addByproduct(Items.GLASS_BOTTLE)
				.addCriterion("has_potion", has(Items.POTION)).build(consumer, "from_strong_healing_potion", true);

		//misc
		DecomposerRecipeBuilder.createRecipe(ModItems.ERODING_BILE.get(), defaultDecomposingTime)
				.setIngredient(Items.ROTTEN_FLESH)
				.addByproduct(ModItems.FLESH_LUMP.get(), 0.2f)
				.addCriterion("has_rotten_flesh", has(Items.ROTTEN_FLESH)).build(consumer);

		DecomposerRecipeBuilder.createRecipe(Items.SKELETON_SKULL, defaultDecomposingTime * 2)
				.setIngredient(Items.ZOMBIE_HEAD)
				.addByproduct(ModItems.ERODING_BILE.get())
				.addByproduct(ModItems.FLESH_LUMP.get(), 0.2f)
				.addCriterion("has_zombie_head", has(Items.ZOMBIE_HEAD)).build(consumer);
	}

	private void registerCookingRecipes(Consumer<IFinishedRecipe> consumer) {
		CookingRecipeBuilder.cooking(Ingredient.of(ModItems.MENDED_SKIN.get()), Items.LEATHER, 0.1F, 350, IRecipeSerializer.SMOKING_RECIPE)
				.unlockedBy("has_skin_chunk", has(ModItems.SKIN_CHUNK.get())).save(consumer, new ResourceLocation(BiomancyMod.MOD_ID, "leather_from_smoking"));

		CookingRecipeBuilder.smelting(Ingredient.of(ModItems.SILICATE_PASTE.get()), Items.GLASS_PANE, 0.1F, 100)
				.unlockedBy("has_silicate", has(ModItems.SILICATE_PASTE.get())).save(consumer, new ResourceLocation(BiomancyMod.MOD_ID, "glass_pane_from_smelting_silicate"));
	}

	private void registerWorkbenchRecipes(Consumer<IFinishedRecipe> consumer) {

		ShapedRecipeBuilder.shaped(ModItems.OCULUS_KEY.get())
				.define('F', ModItems.FLESH_LUMP.get()).define('B', Tags.Items.BONES).define('O', ModItems.OCULUS.get()).define('S', ModItems.BONE_SCRAPS.get())
				.pattern("FBO").pattern("SS ")
				.unlockedBy(HAS_OCULUS, has(ModItems.OCULUS.get())).save(consumer);

		ShapedRecipeBuilder.shaped(ModItems.OCULUS.get())
				.define('F', ModItems.FLESH_LUMP.get()).define('R', ModItems.REJUVENATING_MUCUS.get()).define('L', ModItems.MENISCUS_LENS.get()).define('E', Items.SPIDER_EYE)
				.pattern("FRF").pattern("LER").pattern("FRF")
				.unlockedBy("has_rejuvenating_mucus", has(ModItems.REJUVENATING_MUCUS.get())).save(consumer);

		ShapedRecipeBuilder.shaped(ModItems.MENISCUS_LENS.get())
				.define('P', Tags.Items.GLASS_PANES).define('Q', Items.QUARTZ)
				.pattern(" P ").pattern("PQP").pattern(" P ")
				.unlockedBy("has_quartz", has(Items.QUARTZ)).save(consumer);

		ShapelessRecipeBuilder.shapeless(ModItems.SEWING_KIT_EMPTY.get())
				.requires(Tags.Items.BONES).requires(Items.FLINT)
				.unlockedBy("has_bone", has(Tags.Items.BONES)).save(consumer);

		ShapedRecipeBuilder.shaped(ModItems.SHARP_BONE.get())
				.define('S', ModItems.SEWING_KIT_EMPTY.get()).define('B', Tags.Items.BONES)
				.pattern("S").pattern("B")
				.unlockedBy("has_bone", has(Tags.Items.BONES)).save(consumer);

		ShapelessRecipeBuilder.shapeless(ModItems.SEWING_KIT.get())
				.requires(ModItems.SEWING_KIT_EMPTY.get()).requires(Tags.Items.STRING).requires(Tags.Items.STRING).requires(Tags.Items.STRING)
				.unlockedBy("has_empty_sewing_kit", has(ModItems.SEWING_KIT_EMPTY.get())).save(consumer);

		ShapedRecipeBuilder.shaped(ModItems.GLASS_VIAL.get(), 8)
				.define('G', Tags.Items.GLASS).define('T', ItemTags.WOODEN_SLABS)
				.pattern("GTG").pattern("G G").pattern(" G ")
				.unlockedBy("has_glass", has(Tags.Items.GLASS)).save(consumer);

		ShapedRecipeBuilder.shaped(ModItems.INJECTION_DEVICE.get())
				.define('B', ModItems.GLASS_VIAL.get()).define('S', ModItems.SEWING_KIT_EMPTY.get()).define('I', Items.IRON_INGOT).define('N', Items.IRON_NUGGET)
				.pattern("SBN").pattern("NII").pattern("  I")
				.unlockedBy(HAS_GLASS_VIAL, has(ModItems.GLASS_VIAL.get())).save(consumer);

		ShapelessRecipeBuilder.shapeless(Items.BONE_MEAL)
				.requires(ModItems.BONE_SCRAPS.get(), 4)
				.unlockedBy("has_bone_scraps", has(ModItems.BONE_SCRAPS.get())).save(consumer, BiomancyMod.createRL("bone_meal_from_bone_scraps"));

		ShapedRecipeBuilder.shaped(ModItems.MENDED_SKIN.get())
				.define('S', ModItems.SEWING_KIT.get())
				.define('C', ModItems.SKIN_CHUNK.get())
				.pattern("CC ").pattern("CC ").pattern("CCS")
				.unlockedBy("has_skin_chunk", has(ModItems.SKIN_CHUNK.get())).save(consumer);

		ShapedRecipeBuilder.shaped(ModItems.ARTIFICIAL_STOMACH.get())
				.define('S', ModItems.SEWING_KIT.get())
				.define('M', ModItems.MENDED_SKIN.get())
				.define('C', ModItems.SKIN_CHUNK.get())
				.pattern("  C").pattern("MMC").pattern("MMS")
				.unlockedBy("has_mended_skin", has(ModItems.MENDED_SKIN.get())).save(consumer);

		ShapedRecipeBuilder.shaped(ModItems.FLESH_BLOCK_SLAB.get(), 3 * 2)
				.define('F', ModItems.FLESH_BLOCK.get())
				.pattern("FFF")
				.unlockedBy(HAS_FLESH_BLOCK, has(ModItems.FLESH_BLOCK.get())).save(consumer);

		ShapedRecipeBuilder.shaped(ModItems.FLESH_BLOCK_STAIRS.get(), 4)
				.define('F', ModItems.FLESH_BLOCK.get())
				.pattern("F  ")
				.pattern("FF ")
				.pattern("FFF")
				.unlockedBy(HAS_FLESH_BLOCK, has(ModItems.FLESH_BLOCK.get())).save(consumer);

		ShapelessRecipeBuilder.shapeless(ModItems.NECROTIC_FLESH.get(), 9)
				.requires(ModItems.NECROTIC_FLESH_BLOCK.get())
				.unlockedBy("has_necrotic_flesh_block", has(ModItems.NECROTIC_FLESH_BLOCK.get())).save(consumer);

		ShapelessRecipeBuilder.shapeless(ModItems.NECROTIC_FLESH_BLOCK.get())
				.requires(ModItems.NECROTIC_FLESH.get(), 9)
				.unlockedBy("has_necrotic_flesh", has(ModItems.NECROTIC_FLESH.get())).save(consumer);

		ShapelessRecipeBuilder.shapeless(ModItems.FLESH_BLOCK.get())
				.requires(ModItems.FLESH_LUMP.get(), 9)
				.unlockedBy("has_flesh_lump", has(ModItems.FLESH_LUMP.get())).save(consumer);

		ShapelessRecipeBuilder.shapeless(ModItems.FLESH_LUMP.get(), 9)
				.requires(ModItems.FLESH_BLOCK.get())
				.unlockedBy(HAS_FLESH_BLOCK, has(ModItems.FLESH_BLOCK.get())).save(consumer);

		ShapelessRecipeBuilder.shapeless(ModItems.FLESH_LUMP.get())
				.requires(ModItems.NECROTIC_FLESH.get()).requires(ModItems.REJUVENATING_MUCUS.get())
				.unlockedBy("has_necrotic_flesh", has(ModItems.NECROTIC_FLESH.get())).save(consumer, BiomancyMod.createRL("flesh_from_necrotic"));

		ShapelessRecipeBuilder.shapeless(ModItems.FLESH_BLOCK.get())
				.requires(ModItems.NECROTIC_FLESH_BLOCK.get()).requires(ModItems.REJUVENATING_MUCUS.get(), 6)
				.unlockedBy("has_necrotic_flesh_block", has(ModItems.NECROTIC_FLESH_BLOCK.get())).save(consumer, BiomancyMod.createRL("flesh_from_necrotic_block"));

		ShapedRecipeBuilder.shaped(ModItems.BONE_GEAR.get(), 1)
				.define('B', Tags.Items.BONES)
				.pattern(" B ")
				.pattern("B B")
				.pattern(" B ")
				.unlockedBy("has_bone", has(Tags.Items.BONES)).save(consumer);

		// machines ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

		ShapedRecipeBuilder.shaped(ModItems.DECOMPOSER.get())
				.define('S', ModItems.DIGESTER.get())
				.define('F', ModItems.FLESH_BLOCK.get())
				.define('L', ModItems.FLESH_BLOCK_SLAB.get())
				.define('B', ModItems.BONE_GEAR.get())
				.pattern("FLF").pattern("BSB").pattern("FLF")
				.unlockedBy("has_digester", has(ModItems.DIGESTER.get())).save(consumer);

		DecomposerRecipeBuilder.createRecipe(ModItems.FLESH_BLOCK.get(), 600, 4)
				.setIngredient(ModItems.DECOMPOSER.get())
				.addByproduct(ModItems.BONE_SCRAPS.get(), 0.3f)
				.addByproduct(ModItems.FLESH_LUMP.get(), 1, 0.65f)
				.addByproduct(ModItems.FLESH_LUMP.get(), 2, 0.45f)
				.addByproduct(ModItems.FLESH_LUMP.get(), 4, 0.3f)
				.addCriterion("has_decomposer", has(ModItems.DECOMPOSER.get())).build(consumer, "from_decomposer", true);

		ShapedRecipeBuilder.shaped(ModItems.CHEWER.get())
				.define('S', Items.STONECUTTER)
				.define('F', ModItems.FLESH_BLOCK.get())
				.define('L', ModItems.FLESH_BLOCK_SLAB.get())
				.define('B', ModItems.BONE_GEAR.get())
				.pattern("FLF").pattern("BSB").pattern("FLF")
				.unlockedBy(HAS_FLESH_BLOCK, has(ModItems.FLESH_BLOCK.get())).save(consumer);

		DecomposerRecipeBuilder.createRecipe(ModItems.FLESH_BLOCK.get(), 500, 3)
				.setIngredient(ModItems.CHEWER.get())
				.addByproduct(ModItems.BONE_SCRAPS.get(), 0.3f)
				.addByproduct(ModItems.FLESH_LUMP.get(), 1, 0.65f)
				.addByproduct(ModItems.FLESH_LUMP.get(), 2, 0.45f)
				.addByproduct(ModItems.FLESH_LUMP.get(), 4, 0.3f)
				.addCriterion("has_chewer", has(ModItems.CHEWER.get())).build(consumer, "from_chewer", true);

		ShapedRecipeBuilder.shaped(ModItems.DIGESTER.get())
				.define('S', ModTags.Items.STOMACHS)
				.define('F', ModItems.FLESH_BLOCK.get())
				.define('L', ModItems.FLESH_BLOCK_SLAB.get())
				.define('B', ModItems.BONE_GEAR.get())
				.pattern("FLF").pattern("BSB").pattern("FLF")
				.unlockedBy("has_stomach", has(ModTags.Items.STOMACHS)).save(consumer);

		DecomposerRecipeBuilder.createRecipe(ModItems.FLESH_BLOCK.get(), 500, 3)
				.setIngredient(ModItems.DIGESTER.get())
				.addByproduct(ModItems.BONE_SCRAPS.get(), 0.3f)
				.addByproduct(ModItems.FLESH_LUMP.get(), 1, 0.65f)
				.addByproduct(ModItems.FLESH_LUMP.get(), 2, 0.45f)
				.addByproduct(ModItems.FLESH_LUMP.get(), 4, 0.3f)
				.addCriterion("has_digester", has(ModItems.DIGESTER.get())).build(consumer, "from_digester", true);

		ShapedRecipeBuilder.shaped(ModItems.SOLIDIFIER.get())
				.define('S', Items.SMOKER)
				.define('F', ModItems.FLESH_BLOCK.get())
				.define('L', ModItems.FLESH_BLOCK_SLAB.get())
				.define('B', ModItems.BONE_GEAR.get())
				.pattern("FLF").pattern("BSB").pattern("FLF")
				.unlockedBy(HAS_FLESH_BLOCK, has(ModItems.FLESH_BLOCK.get())).save(consumer);

		DecomposerRecipeBuilder.createRecipe(ModItems.FLESH_BLOCK.get(), 500, 3)
				.setIngredient(ModItems.SOLIDIFIER.get())
				.addByproduct(ModItems.BONE_SCRAPS.get(), 0.3f)
				.addByproduct(ModItems.FLESH_LUMP.get(), 1, 0.65f)
				.addByproduct(ModItems.FLESH_LUMP.get(), 2, 0.45f)
				.addByproduct(ModItems.FLESH_LUMP.get(), 4, 0.3f)
				.addCriterion("has_solidifier", has(ModItems.SOLIDIFIER.get())).build(consumer, "from_solidifier", true);

		// reagents ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

		String vialSuffix = "_vial";

		//noinspection ConstantConditions
		ShapelessNbtRecipeBuilder.shapelessRecipe(ReagentItem.getReagentItemStack(ModReagents.MUTAGEN_SERUM.get()))
				.addIngredient(ModItems.GLASS_VIAL.get()).addIngredient(ModItems.MUTAGENIC_BILE.get()).addIngredient(ModItems.HORMONE_BILE.get())
				.addCriterion(HAS_GLASS_VIAL, has(ModItems.GLASS_VIAL.get())).build(consumer, BiomancyMod.createRL(ModReagents.MUTAGEN_SERUM.get().getRegistryName().getPath() + vialSuffix));

		//noinspection ConstantConditions
		ShapelessNbtRecipeBuilder.shapelessRecipe(ReagentItem.getReagentItemStack(ModReagents.REJUVENATION_SERUM.get()))
				.addIngredient(ModItems.GLASS_VIAL.get()).addIngredient(ModItems.REJUVENATING_MUCUS.get()).addIngredient(ModItems.HORMONE_BILE.get()).addIngredient(ModItems.NUTRIENT_PASTE.get())
				.addCriterion(HAS_GLASS_VIAL, has(ModItems.GLASS_VIAL.get())).build(consumer, BiomancyMod.createRL(ModReagents.REJUVENATION_SERUM.get().getRegistryName().getPath() + vialSuffix));

		//noinspection ConstantConditions
		ShapelessNbtRecipeBuilder.shapelessRecipe(ReagentItem.getReagentItemStack(ModReagents.GROWTH_SERUM.get()))
				.addIngredient(ModItems.GLASS_VIAL.get()).addIngredient(ModItems.NUTRIENT_PASTE.get()).addIngredient(ModItems.HORMONE_BILE.get()).addIngredient(Items.BONE_MEAL)
				.addCriterion(HAS_GLASS_VIAL, has(ModItems.GLASS_VIAL.get())).build(consumer, BiomancyMod.createRL(ModReagents.GROWTH_SERUM.get().getRegistryName().getPath() + vialSuffix));

		//noinspection ConstantConditions
		ShapelessNbtRecipeBuilder.shapelessRecipe(ReagentItem.getReagentItemStack(ModReagents.DECAY_AGENT.get()))
				.addIngredient(ModItems.GLASS_VIAL.get()).addIngredient(ModItems.ERODING_BILE.get()).addIngredient(ModItems.HORMONE_BILE.get())
				.addCriterion(HAS_GLASS_VIAL, has(ModItems.GLASS_VIAL.get())).build(consumer, BiomancyMod.createRL(ModReagents.DECAY_AGENT.get().getRegistryName().getPath() + vialSuffix));

		//noinspection ConstantConditions
		ShapelessNbtRecipeBuilder.shapelessRecipe(ReagentItem.getReagentItemStack(ModReagents.BREEDING_STIMULANT.get()))
				.addIngredient(ModItems.GLASS_VIAL.get()).addIngredient(ModItems.NUTRIENT_BAR.get()).addIngredient(ModItems.HORMONE_BILE.get())
				.addCriterion(HAS_GLASS_VIAL, has(ModItems.GLASS_VIAL.get())).build(consumer, BiomancyMod.createRL(ModReagents.BREEDING_STIMULANT.get().getRegistryName().getPath() + vialSuffix));

		//noinspection ConstantConditions
		ShapelessNbtRecipeBuilder.shapelessRecipe(ReagentItem.getReagentItemStack(ModReagents.CLEANSING_SERUM.get()))
				.addIngredient(ModItems.GLASS_VIAL.get()).addIngredient(ModItems.REJUVENATING_MUCUS.get(), 2).addIngredient(ModItems.HORMONE_BILE.get()).addIngredient(ModItems.MILK_GEL.get(), 2)
				.addCriterion(HAS_GLASS_VIAL, has(ModItems.GLASS_VIAL.get())).build(consumer, BiomancyMod.createRL(ModReagents.CLEANSING_SERUM.get().getRegistryName().getPath() + vialSuffix));

		//noinspection ConstantConditions
		ShapelessNbtRecipeBuilder.shapelessRecipe(ReagentItem.getReagentItemStack(ModReagents.INSOMNIA_CURE.get()))
				.addIngredient(ModItems.GLASS_VIAL.get()).addIngredient(ModItems.REJUVENATING_MUCUS.get(), 3).addIngredient(ModItems.HORMONE_BILE.get())
				.addIngredient(ReagentItem.getReagentItemStack(ModReagents.CLEANSING_SERUM.get())).addIngredient(ModItems.NUTRIENT_PASTE.get())
				.addCriterion(HAS_GLASS_VIAL, has(ModItems.GLASS_VIAL.get())).build(consumer, BiomancyMod.createRL(ModReagents.INSOMNIA_CURE.get().getRegistryName().getPath() + vialSuffix));

		//noinspection ConstantConditions
		ShapelessNbtRecipeBuilder.shapelessRecipe(ReagentItem.getReagentItemStack(ModReagents.ABSORPTION_BOOST.get()))
				.addIngredient(ModItems.GLASS_VIAL.get()).addIngredient(ModItems.ERODING_BILE.get()).addIngredient(ModItems.HORMONE_BILE.get())
				.addIngredient(ReagentItem.getReagentItemStack(ModReagents.GROWTH_SERUM.get())).addIngredient(Items.GOLDEN_APPLE)
				.addCriterion(HAS_GLASS_VIAL, has(ModItems.GLASS_VIAL.get())).build(consumer, BiomancyMod.createRL(ModReagents.ABSORPTION_BOOST.get().getRegistryName().getPath() + vialSuffix));

		// food ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

		ShapedRecipeBuilder.shaped(ModItems.NUTRIENT_BAR.get())
				.define('N', ModItems.NUTRIENT_PASTE.get())
				.define('B', Items.SWEET_BERRIES)
				.define('S', Tags.Items.SEEDS)
				.pattern("SBS").pattern("NNN")
				.unlockedBy("has_nutrient_paste", has(ModItems.NUTRIENT_PASTE.get())).save(consumer);

		// misc ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		ShapelessRecipeBuilder.shapeless(Items.DIORITE)
				.requires(Items.COBBLESTONE).requires(ModItems.SILICATE_PASTE.get()).requires(ModItems.SILICATE_PASTE.get())
				.unlockedBy(HAS_SILICATE_PASTE, has(ModItems.SILICATE_PASTE.get())).save(consumer, BiomancyMod.createRL("diorite_from_silicate"));

		ShapelessRecipeBuilder.shapeless(Items.RED_SAND)
				.requires(Items.SAND).requires(ModItems.OXIDE_POWDER.get()).requires(ModItems.OXIDE_POWDER.get())
				.unlockedBy(HAS_OXIDE_POWDER, has(ModItems.OXIDE_POWDER.get())).save(consumer, BiomancyMod.createRL("red_sand_from_oxide"));

		ShapedRecipeBuilder.shaped(Items.DIRT)
				.define('S', Items.SAND)
				.define('D', ModItems.DIGESTATE.get())
				.pattern("DDD").pattern("DSD").pattern("DDD")
				.unlockedBy("has_digestate", has(ModItems.DIGESTATE.get())).save(consumer, BiomancyMod.createRL("dirt_from_digestate"));

		ShapedRecipeBuilder.shaped(Items.CLAY_BALL)
				.define('S', ModItems.SILICATE_PASTE.get())
				.define('D', ModItems.DIGESTATE.get())
				.pattern(" S ").pattern("SDS").pattern(" S ")
				.unlockedBy("has_siligestate", hasItems(ModItems.DIGESTATE.get(), ModItems.SILICATE_PASTE.get())).save(consumer, BiomancyMod.createRL("clay_ball_from_siligestate"));

		ShapelessRecipeBuilder.shapeless(Items.GUNPOWDER, 2)
				.requires(Items.CHARCOAL).requires(ModItems.SILICATE_PASTE.get(), 3).requires(Items.BLAZE_POWDER, 2)
				.unlockedBy(HAS_SILICATE_PASTE, has(ModItems.SILICATE_PASTE.get())).save(consumer, BiomancyMod.createRL("gunpowder_from_silicate_blaze"));

		// special /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		CustomRecipeBuilder.special(ModRecipes.REPAIR_SPECIAL_SEWING_KIT.get()).save(consumer, BiomancyMod.MOD_ID + ":" + "sewing_kit_nbt");
		CustomRecipeBuilder.special(ModRecipes.CRAFTING_SPECIAL_BOOMLING.get()).save(consumer, BiomancyMod.MOD_ID + ":" + "boomling");
		CustomRecipeBuilder.special(ModRecipes.CRAFTING_SPECIAL_BOOMLING_GUN.get()).save(consumer, BiomancyMod.MOD_ID + ":" + "boomling_gun");
		CustomRecipeBuilder.special(ModRecipes.CRAFTING_SPECIAL_ADD_USER_TO_KEY.get()).save(consumer, BiomancyMod.MOD_ID + ":" + "add_user_to_key");
	}

	@Override
	public String getName() {
		return StringUtils.capitalize(BiomancyMod.MOD_ID) + " " + super.getName();
	}
}
