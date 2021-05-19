package com.github.elenterius.biomancy.datagen;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.datagen.recipe.ChewerRecipeBuilder;
import com.github.elenterius.biomancy.datagen.recipe.DecomposerRecipeBuilder;
import com.github.elenterius.biomancy.datagen.recipe.DigesterRecipeBuilder;
import com.github.elenterius.biomancy.datagen.recipe.EvolutionPoolRecipeBuilder;
import com.github.elenterius.biomancy.init.ModEnchantments;
import com.github.elenterius.biomancy.init.ModItems;
import com.github.elenterius.biomancy.init.ModRecipes;
import com.github.elenterius.biomancy.init.ModTags;
import com.github.elenterius.biomancy.recipe.ItemStackIngredient;
import com.github.elenterius.biomancy.tileentity.EvolutionPoolTileEntity;
import net.minecraft.advancements.criterion.EnchantmentPredicate;
import net.minecraft.advancements.criterion.InventoryChangeTrigger;
import net.minecraft.advancements.criterion.ItemPredicate;
import net.minecraft.advancements.criterion.MinMaxBounds;
import net.minecraft.block.ComposterBlock;
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
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.registry.Registry;
import net.minecraftforge.common.Tags;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import java.util.Arrays;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class ModRecipeProvider extends RecipeProvider {

	public final Logger LOGGER = BiomancyMod.LOGGER;
	public final Marker logMarker = MarkerManager.getMarker("RecipeProvider");

	public ModRecipeProvider(DataGenerator generatorIn) {
		super(generatorIn);
	}

	protected static ItemPredicate createPredicate(IItemProvider item) {
		return ItemPredicate.Builder.create().item(item).build();
	}

	protected static ItemPredicate createPredicate(ITag<Item> tag) {
		return ItemPredicate.Builder.create().tag(tag).build();
	}

	protected static InventoryChangeTrigger.Instance hasItems(IItemProvider... itemProviders) {
		ItemPredicate[] predicates = Arrays.stream(itemProviders).map(ModRecipeProvider::createPredicate).toArray(ItemPredicate[]::new);
		return hasItem(predicates);
	}

	protected final Predicate<IItemProvider> IGNORE_COMPOSTABLE_PREDICATE = iItemProvider -> iItemProvider.asItem() != ModItems.DIGESTATE.get();

	@Override
	protected void registerRecipes(Consumer<IFinishedRecipe> consumer) {
		LOGGER.info(logMarker, "registering workbench recipes...");
		registerWorkbenchRecipes(consumer);
		LOGGER.info(logMarker, "registering cooking recipes...");
		registerCookingRecipes(consumer);

		LOGGER.info(logMarker, "registering chewer recipes...");
		registerChewerRecipes(consumer);
		LOGGER.info(logMarker, "registering digester recipes...");
		registerDigesterRecipes(consumer);
		LOGGER.info(logMarker, "registering decomposer recipes...");
		registerDecomposerRecipes(consumer);

		LOGGER.info(logMarker, "registering evolution pool recipes...");
		registerEvolutionPoolRecipes(consumer);
	}

	private void registerEvolutionPoolRecipes(Consumer<IFinishedRecipe> consumer) {
		final int defaultTime = EvolutionPoolTileEntity.DEFAULT_TIME;

		// Duplication /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		EvolutionPoolRecipeBuilder.createRecipe(Items.IRON_NUGGET, defaultTime, 5)
				.addIngredient(Items.IRON_NUGGET).addIngredients(ModItems.OXIDE_POWDER.get(), 5)
				.addCriterion("has_oxide_powder", hasItem(ModItems.OXIDE_POWDER.get())).build(consumer);

		EvolutionPoolRecipeBuilder.createRecipe(Items.GOLD_NUGGET, defaultTime, 5)
				.addIngredient(Items.GOLD_NUGGET).addIngredients(ModItems.OXIDE_POWDER.get(), 5)
				.addCriterion("has_oxide_powder", hasItem(ModItems.OXIDE_POWDER.get())).build(consumer);

		EvolutionPoolRecipeBuilder.createRecipe(Items.QUARTZ, defaultTime, 3)
				.addIngredient(Items.QUARTZ).addIngredients(ModItems.SILICATE_PASTE.get(), 5)
				.addCriterion("has_silicate_paste", hasItem(ModItems.SILICATE_PASTE.get())).build(consumer);

		// Transmutation////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		EvolutionPoolRecipeBuilder.createRecipe(Items.PRISMARINE_CRYSTALS, defaultTime * 2)
				.addIngredient(Items.QUARTZ).addIngredient(Items.LAPIS_LAZULI).addIngredients(ModItems.SILICATE_PASTE.get(), 2).addIngredient(ItemTags.FISHES)
				.addCriterion("has_silicate_paste", hasItem(ModItems.SILICATE_PASTE.get())).build(consumer);

		EvolutionPoolRecipeBuilder.createRecipe(Items.PRISMARINE_SHARD, defaultTime * 2)
				.addIngredient(Items.QUARTZ).addIngredient(Items.LAPIS_LAZULI).addIngredients(ModItems.KERATIN_FILAMENTS.get(), 2).addIngredient(ItemTags.FISHES)
				.addCriterion("has_keratin_filaments", hasItem(ModItems.KERATIN_FILAMENTS.get())).build(consumer);

		EvolutionPoolRecipeBuilder.createRecipe(Items.SHULKER_SHELL, defaultTime * 2)
				.addIngredients(Items.POPPED_CHORUS_FRUIT, 2).addIngredients(ModItems.SILICATE_PASTE.get(), 4)
				.addCriterion("has_silicate_paste", hasItem(ModItems.SILICATE_PASTE.get())).build(consumer);

		EvolutionPoolRecipeBuilder.createRecipe(Items.ZOMBIE_HEAD, defaultTime * 2)
				.addIngredient(Items.PLAYER_HEAD).addIngredients(ModItems.ERODING_BILE.get(), 2).addIngredient(Items.ROTTEN_FLESH)
				.addCriterion("has_eroding_bile", hasItem(ModItems.ERODING_BILE.get())).build(consumer);

		EvolutionPoolRecipeBuilder.createRecipe(Items.WITHER_SKELETON_SKULL, defaultTime * 3)
				.addIngredient(Items.SKELETON_SKULL).addIngredient(Items.WITHER_ROSE).addIngredients(ModItems.ERODING_BILE.get(), 2)
				.addCriterion("has_eroding_bile_and_wither_rose", hasItem(createPredicate(ModItems.ERODING_BILE.get()), createPredicate(Items.WITHER_ROSE)))
				.build(consumer);

		// Biometal ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		EvolutionPoolRecipeBuilder.createRecipe(ModItems.BIOMETAL.get(), defaultTime)
				.addIngredient(Items.IRON_INGOT).addIngredient(ModItems.FLESH_BLOCK.get()).addIngredients(ModItems.OXIDE_POWDER.get(), 2)
				.addIngredient(ModItems.KERATIN_FILAMENTS.get()).addIngredient(ModItems.HORMONE_SERUM.get())
				.addCriterion("has_mutagenic_bile", hasItem(ModItems.MUTAGENIC_BILE.get())).build(consumer);

		// Blocks //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		EvolutionPoolRecipeBuilder.createRecipe(ModItems.BIO_FLESH_DOOR.get(), defaultTime)
				.addIngredient(Items.IRON_DOOR).addIngredient(ModItems.FLESH_BLOCK.get()).addIngredients(ModItems.KERATIN_FILAMENTS.get(), 2).addIngredient(ModItems.HORMONE_SERUM.get())
				.addCriterion("has_mutagenic_bile", hasItem(ModItems.MUTAGENIC_BILE.get())).build(consumer);

		EvolutionPoolRecipeBuilder.createRecipe(ModItems.BIO_FLESH_TRAPDOOR.get(), defaultTime)
				.addIngredient(Items.IRON_TRAPDOOR).addIngredient(ModItems.OCULUS.get()).addIngredient(ModItems.FLESH_BLOCK.get()).addIngredients(ModItems.KERATIN_FILAMENTS.get(), 2).addIngredient(ModItems.HORMONE_SERUM.get())
				.addCriterion("has_mutagenic_bile", hasItem(ModItems.MUTAGENIC_BILE.get())).build(consumer);

		EvolutionPoolRecipeBuilder.createRecipe(ModItems.BIO_FLESH_PRESSURE_PLATE.get(), defaultTime)
				.addIngredient(Items.HEAVY_WEIGHTED_PRESSURE_PLATE).addIngredient(ModItems.OCULUS.get()).addIngredient(ModItems.FLESH_BLOCK.get()).addIngredients(ModItems.KERATIN_FILAMENTS.get(), 2).addIngredient(ModItems.HORMONE_SERUM.get())
				.addCriterion("has_mutagenic_bile", hasItem(ModItems.MUTAGENIC_BILE.get())).build(consumer);

		EvolutionPoolRecipeBuilder.createRecipe(ModItems.GULGE.get(), defaultTime * 2)
				.addIngredient(ModItems.FLESH_CHEST.get()).addIngredients(ModItems.ARTIFICIAL_STOMACH.get(), 4).addIngredient(ModItems.FLESH_BLOCK.get())
				.addCriterion("has_mutagenic_bile", hasItem(ModItems.MUTAGENIC_BILE.get())).build(consumer);

		EvolutionPoolRecipeBuilder.createRecipe(ModItems.FLESH_CHEST.get(), defaultTime)
				.addIngredients(Items.CHEST, 2).addIngredient(ModItems.FLESH_BLOCK.get()).addIngredients(ModItems.KERATIN_FILAMENTS.get(), 2).addIngredient(ModItems.HORMONE_SERUM.get())
				.addCriterion("has_mutagenic_bile", hasItem(ModItems.MUTAGENIC_BILE.get())).build(consumer);

		// Tools ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		EvolutionPoolRecipeBuilder.createRecipe(ModItems.SINGLE_ITEM_BAG_ITEM.get(), defaultTime + 150)
				.addIngredients(ModItems.ARTIFICIAL_STOMACH.get(), 2).addIngredient(Items.HOPPER).addIngredient(ModItems.FLESH_CHEST.get()).addIngredients(ModItems.KERATIN_FILAMENTS.get(), 2)
				.addCriterion("has_mutagenic_bile", hasItem(ModItems.MUTAGENIC_BILE.get())).build(consumer);

		EvolutionPoolRecipeBuilder.createRecipe(ModItems.ENTITY_STORAGE_ITEM.get(), defaultTime + 150)
				.addIngredients(ModItems.ARTIFICIAL_STOMACH.get(), 2).addIngredient(Items.EGG).addIngredient(Items.ENDER_CHEST).addIngredients(ModItems.KERATIN_FILAMENTS.get(), 2)
				.addCriterion("has_mutagenic_bile", hasItem(ModItems.MUTAGENIC_BILE.get())).build(consumer);

		EvolutionPoolRecipeBuilder.createRecipe(ModItems.OCULI_OF_UNVEILING.get(), defaultTime * 2)
				.addIngredient(Items.IRON_HELMET).addIngredients(ModItems.OCULUS.get(), 4).addIngredient(ModItems.BIOMETAL.get())
				.addCriterion("has_mutagenic_bile", hasItems(ModItems.MUTAGENIC_BILE.get(), ModItems.MENISCUS_LENS.get())).build(consumer);

		EvolutionPoolRecipeBuilder.createRecipe(ModItems.BIOFLESH_AXE.get(), defaultTime)
				.addIngredient(Items.IRON_AXE).addIngredient(Items.BONE).addIngredients(ModItems.BIOMETAL.get(), 3).addIngredient(ModItems.HORMONE_SERUM.get())
				.addCriterion("has_mutagenic_bile", hasItem(ModItems.MUTAGENIC_BILE.get())).build(consumer);

		EvolutionPoolRecipeBuilder.createRecipe(ModItems.BIOFLESH_SHOVEL.get(), defaultTime)
				.addIngredient(Items.IRON_SHOVEL).addIngredient(Items.BONE).addIngredient(ModItems.BIOMETAL.get()).addIngredients(ModItems.KERATIN_FILAMENTS.get(), 2).addIngredient(ModItems.HORMONE_SERUM.get())
				.addCriterion("has_mutagenic_bile", hasItem(ModItems.MUTAGENIC_BILE.get())).build(consumer);

		EvolutionPoolRecipeBuilder.createRecipe(ModItems.BIOFLESH_PICKAXE.get(), defaultTime)
				.addIngredient(Items.IRON_PICKAXE).addIngredient(Items.BONE).addIngredients(ModItems.BIOMETAL.get(), 3).addIngredient(ModItems.HORMONE_SERUM.get())
				.addCriterion("has_mutagenic_bile", hasItem(ModItems.MUTAGENIC_BILE.get())).build(consumer);

		// Weapons /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		EvolutionPoolRecipeBuilder.createRecipe(ModItems.BIOFLESH_WAR_AXE.get(), defaultTime * 2)
				.addIngredient(ModItems.BIOFLESH_AXE.get()).addIngredient(ModItems.OCULUS.get()).addIngredient(ModItems.HORMONE_SERUM.get()).addIngredient(ModItems.ARTIFICIAL_STOMACH.get())
				.addIngredients(ModItems.KERATIN_FILAMENTS.get(), 2)
				.addCriterion("has_bioflesh_axe", hasItem(ModItems.BIOFLESH_AXE.get())).build(consumer);

		ItemStackIngredient sweepingBook = new ItemStackIngredient(EnchantedBookItem.getEnchantedItemStack(new EnchantmentData(Enchantments.SWEEPING, Enchantments.SWEEPING.getMaxLevel())));
		EvolutionPoolRecipeBuilder.createRecipe(ModItems.LONG_RANGE_CLAW.get(), defaultTime * 2)
				.addIngredient(ModItems.OCULUS.get()).addIngredient(sweepingBook).addIngredients(ModItems.SHARP_BONE.get(), 2).addIngredient(Items.DIAMOND_SWORD).addIngredient(ModItems.BIOMETAL.get())
				.addCriterion("has_oculus", hasItem(ModItems.OCULUS.get())).build(consumer);

		ItemStackIngredient mendingBook = new ItemStackIngredient(EnchantedBookItem.getEnchantedItemStack(new EnchantmentData(Enchantments.MENDING, Enchantments.MENDING.getMaxLevel())));
		EvolutionPoolRecipeBuilder.createRecipe(ModItems.LEECH_CLAW.get(), defaultTime * 2)
				.addIngredient(ModItems.OCULUS.get()).addIngredient(ModItems.SYRINGE.get()).addIngredient(mendingBook).addIngredient(Items.DIAMOND_SWORD).addIngredient(ModItems.BIOMETAL.get())
				.addCriterion("has_oculus", hasItem(ModItems.OCULUS.get())).build(consumer);

		ItemStackIngredient maxBaneBook = new ItemStackIngredient(EnchantedBookItem.getEnchantedItemStack(new EnchantmentData(ModEnchantments.ATTUNED_BANE.get(), ModEnchantments.ATTUNED_BANE.get().getMaxLevel())));
		EvolutionPoolRecipeBuilder.createRecipe(ModItems.BIOFLESH_GUAN_DAO.get(), defaultTime * 2)
				.addIngredient(ModItems.OCULUS.get()).addIngredient(maxBaneBook).addIngredient(Items.BONE).addIngredient(Items.DIAMOND_SWORD).addIngredient(Items.DIAMOND_AXE).addIngredient(ModItems.BIOMETAL.get())
				.addCriterion("has_oculus", hasItem(ModItems.OCULUS.get())).build(consumer);

		// Enchantments ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		Enchantment enchantment = ModEnchantments.ATTUNED_BANE.get();
		for (int level = enchantment.getMinLevel(); level <= enchantment.getMaxLevel(); ++level) {
			ItemStack minBaneBook = EnchantedBookItem.getEnchantedItemStack(new EnchantmentData(ModEnchantments.ATTUNED_BANE.get(), level));
			ItemStackIngredient sharpnessBook = new ItemStackIngredient(EnchantedBookItem.getEnchantedItemStack(new EnchantmentData(Enchantments.SHARPNESS, level)));
			ItemStackIngredient smiteBook = new ItemStackIngredient(EnchantedBookItem.getEnchantedItemStack(new EnchantmentData(Enchantments.SMITE, level)));
			ItemStackIngredient arthropodsBook = new ItemStackIngredient(EnchantedBookItem.getEnchantedItemStack(new EnchantmentData(Enchantments.BANE_OF_ARTHROPODS, level)));
			EvolutionPoolRecipeBuilder.createRecipe(minBaneBook, defaultTime + 125 * level)
					.addIngredient(sharpnessBook).addIngredient(smiteBook).addIngredient(arthropodsBook).addIngredient(ModItems.ARTIFICIAL_STOMACH.get()).addIngredient(ModItems.ERODING_BILE.get()).addIngredient(ModItems.REJUVENATING_MUCUS.get())
					.addCriterion("has_smite_enchant", hasItem(ItemPredicate.Builder.create().enchantment(new EnchantmentPredicate(Enchantments.SMITE, MinMaxBounds.IntBound.atLeast(1))).build())).build(consumer, "attuned_bane_" + level, true);
		}
	}

	private void registerOxidesRecipes(Consumer<IFinishedRecipe> consumer) {
		final int defaultDecomposingTime = 300;
		int id = 0;

		DecomposerRecipeBuilder.createRecipe(ModItems.OXIDE_POWDER.get(), defaultDecomposingTime, 1)
				.addIngredients(Items.RED_SAND, 2)
				.addByproduct(ModItems.OXIDE_POWDER.get(), 0.5f)
				.addByproduct(ModItems.SILICATE_PASTE.get(), 2, 0.8f)
				.addCriterion("has_red_sand", hasItem(Items.RED_SAND)).build(consumer, "from_red_sand", true);

		DecomposerRecipeBuilder.createRecipe(ModItems.OXIDE_POWDER.get(), defaultDecomposingTime - 100)
				.addIngredients(Tags.Items.EGGS, 6)
				.addByproduct(ModItems.MUTAGENIC_BILE.get(), 0.05f)
				.addCriterion("has_oxide", hasItem(ModTags.Items.OXIDES)).build(consumer, "" + id++, true);

		DecomposerRecipeBuilder.createRecipe(ModItems.OXIDE_POWDER.get(), defaultDecomposingTime - 100)
				.addIngredients(Items.TURTLE_EGG, 6)
				.addByproduct(ModItems.MUTAGENIC_BILE.get(), 0.05f)
				.addCriterion("has_oxide", hasItem(ModTags.Items.OXIDES)).build(consumer, "" + id++, true);

		DecomposerRecipeBuilder.createRecipe(ModItems.OXIDE_POWDER.get(), defaultDecomposingTime - 100)
				.addIngredients(Tags.Items.BONES, 2)
				.addCriterion("has_oxide", hasItem(ModTags.Items.OXIDES)).build(consumer, "" + id++, true);

		DecomposerRecipeBuilder.createRecipe(ModItems.OXIDE_POWDER.get(), defaultDecomposingTime - 100)
				.addIngredients(Items.BONE_MEAL, 6)
				.addCriterion("has_oxide", hasItem(ModTags.Items.OXIDES)).build(consumer, "" + id, true);
	}

	private void registerSilicatesRecipes(Consumer<IFinishedRecipe> consumer) {
		final int defaultDecomposingTime = 200;
		int id = 0;

		DecomposerRecipeBuilder.createRecipe(ModItems.SILICATE_PASTE.get(), defaultDecomposingTime * 2)
				.addIngredients(Items.ANDESITE, 2)
				.addCriterion("has_silicates", hasItem(ModTags.Items.SILICATES)).build(consumer, "" + id++, true);

		DecomposerRecipeBuilder.createRecipe(ModItems.SILICATE_PASTE.get(), defaultDecomposingTime * 2)
				.addIngredients(Items.DIORITE, 2)
				.addCriterion("has_silicates", hasItem(ModTags.Items.SILICATES)).build(consumer, "" + id++, true);

		DecomposerRecipeBuilder.createRecipe(ModItems.SILICATE_PASTE.get(), defaultDecomposingTime * 2)
				.addIngredients(Items.GRANITE, 2)
				.addByproduct(ModItems.SILICATE_PASTE.get(), 0.5f)
				.addCriterion("has_silicates", hasItem(ModTags.Items.SILICATES)).build(consumer, "" + id++, true);

		DecomposerRecipeBuilder.createRecipe(ModItems.SILICATE_PASTE.get(), defaultDecomposingTime + 150)
				.addIngredients(Items.REDSTONE, 6)
				.addCriterion("has_silicates", hasItem(ModTags.Items.SILICATES)).build(consumer, "" + id++, true);

		DecomposerRecipeBuilder.createRecipe(ModItems.SILICATE_PASTE.get(), defaultDecomposingTime, 3)
				.addIngredient(Items.NAUTILUS_SHELL)
				.addByproduct(ModItems.MUTAGENIC_BILE.get(), 2, 1f)
				.addCriterion("has_silicates", hasItem(ModTags.Items.SILICATES)).build(consumer, "" + id++, true);

		DecomposerRecipeBuilder.createRecipe(ModItems.SILICATE_PASTE.get(), defaultDecomposingTime, 4)
				.addIngredient(Items.SHULKER_SHELL)
				.addCriterion("has_silicates", hasItem(ModTags.Items.SILICATES)).build(consumer, "" + id++, true);

		DecomposerRecipeBuilder.createRecipe(ModItems.SILICATE_PASTE.get(), defaultDecomposingTime + 150)
				.addIngredients(Items.GLOWSTONE_DUST, 6)
				.addCriterion("has_silicates", hasItem(ModTags.Items.SILICATES)).build(consumer, "" + id++, true);

		DecomposerRecipeBuilder.createRecipe(ModItems.SILICATE_PASTE.get(), defaultDecomposingTime * 2, 3)
				.addIngredient(Tags.Items.GEMS_EMERALD)
				.addCriterion("has_silicates", hasItem(ModTags.Items.SILICATES)).build(consumer, "" + id++, true);

		DecomposerRecipeBuilder.createRecipe(ModItems.SILICATE_PASTE.get(), defaultDecomposingTime * 2)
				.addIngredient(Tags.Items.GEMS_LAPIS)
				.addCriterion("has_silicates", hasItem(ModTags.Items.SILICATES)).build(consumer, "" + id++, true);

		DecomposerRecipeBuilder.createRecipe(ModItems.SILICATE_PASTE.get(), defaultDecomposingTime * 2)
				.addIngredient(Tags.Items.GEMS_QUARTZ)
				.addByproduct(ModItems.SILICATE_PASTE.get(), 0.5f)
				.addCriterion("has_silicates", hasItem(ModTags.Items.SILICATES)).build(consumer, "" + id++, true);

		DecomposerRecipeBuilder.createRecipe(ModItems.SILICATE_PASTE.get(), defaultDecomposingTime * 2, 3)
				.addIngredient(Tags.Items.GEMS_PRISMARINE)
				.addCriterion("has_silicates", hasItem(ModTags.Items.SILICATES)).build(consumer, "" + id++, true);

		DecomposerRecipeBuilder.createRecipe(ModItems.SILICATE_PASTE.get(), defaultDecomposingTime + 100)
				.addIngredients(Items.KELP, 6)
				.addByproduct(ModItems.DIGESTATE.get(), 0.3f)
				.addCriterion("has_silicates", hasItem(ModTags.Items.SILICATES)).build(consumer, "" + id++, true);

		DecomposerRecipeBuilder.createRecipe(ModItems.SILICATE_PASTE.get(), defaultDecomposingTime)
				.addIngredients(Items.DRIED_KELP, 6)
				.addByproduct(ModItems.DIGESTATE.get(), 0.3f)
				.addCriterion("has_silicates", hasItem(ModTags.Items.SILICATES)).build(consumer, "" + id++, true);

		DecomposerRecipeBuilder.createRecipe(ModItems.SILICATE_PASTE.get(), defaultDecomposingTime)
				.addIngredient(Items.DRIED_KELP_BLOCK)
				.addByproduct(ModItems.SILICATE_PASTE.get(), 0.5f)
				.addByproduct(ModItems.DIGESTATE.get(), 0.4f)
				.addCriterion("has_silicates", hasItem(ModTags.Items.SILICATES)).build(consumer, "" + id, true);
	}

	private void registerKeratinsRecipes(Consumer<IFinishedRecipe> consumer) {
		final int defaultDecomposingTime = 200;
		int id = 0;

		DecomposerRecipeBuilder.createRecipe(ModItems.KERATIN_FILAMENTS.get(), defaultDecomposingTime * 2, 3)
				.addIngredient(Items.SCUTE)
				.addCriterion("has_keratins", hasItem(ModTags.Items.KERATINS)).build(consumer, "" + id++, true);

		DecomposerRecipeBuilder.createRecipe(ModItems.KERATIN_FILAMENTS.get(), defaultDecomposingTime)
				.addIngredient(Tags.Items.LEATHER)
				.addCriterion("has_keratins", hasItem(ModTags.Items.KERATINS)).build(consumer, "" + id++, true);

		DecomposerRecipeBuilder.createRecipe(ModItems.KERATIN_FILAMENTS.get(), defaultDecomposingTime, 2)
				.addIngredient(Items.RABBIT_HIDE)
				.addCriterion("has_keratins", hasItem(ModTags.Items.KERATINS)).build(consumer, "" + id++, true);

		DecomposerRecipeBuilder.createRecipe(ModItems.KERATIN_FILAMENTS.get(), defaultDecomposingTime)
				.addIngredients(Tags.Items.FEATHERS, 4)
				.addCriterion("has_keratins", hasItem(ModTags.Items.KERATINS)).build(consumer, "" + id++, true);

		DecomposerRecipeBuilder.createRecipe(ModItems.KERATIN_FILAMENTS.get(), defaultDecomposingTime)
				.addIngredients(Tags.Items.STRING, 4)
				.addCriterion("has_keratins", hasItem(ModTags.Items.KERATINS)).build(consumer, "" + id++, true);

		DecomposerRecipeBuilder.createRecipe(ModItems.KERATIN_FILAMENTS.get(), defaultDecomposingTime)
				.addIngredient(ItemTags.WOOL)
				.addCriterion("has_keratins", hasItem(ModTags.Items.KERATINS)).build(consumer, "" + id++, true);

		DecomposerRecipeBuilder.createRecipe(ModItems.KERATIN_FILAMENTS.get(), defaultDecomposingTime)
				.addIngredient(ItemTags.CARPETS)
				.addCriterion("has_keratins", hasItem(ModTags.Items.KERATINS)).build(consumer, "" + id++, true);

		DecomposerRecipeBuilder.createRecipe(ModItems.KERATIN_FILAMENTS.get(), defaultDecomposingTime)
				.addIngredient(Items.LEAD)
				.addCriterion("has_keratins", hasItem(ModTags.Items.KERATINS)).build(consumer, "" + id++, true);

		DecomposerRecipeBuilder.createRecipe(ModItems.KERATIN_FILAMENTS.get(), defaultDecomposingTime * 2, 3)
				.addIngredient(Items.PHANTOM_MEMBRANE)
				.addCriterion("has_keratins", hasItem(ModTags.Items.KERATINS)).build(consumer, "" + id++, true);

		DecomposerRecipeBuilder.createRecipe(ModItems.KERATIN_FILAMENTS.get(), defaultDecomposingTime * 2)
				.addIngredient(Items.PRISMARINE_SHARD)
				.addCriterion("has_keratins", hasItem(ModTags.Items.KERATINS)).build(consumer, "" + id, true);
	}

	private void registerHormonesRecipes(Consumer<IFinishedRecipe> consumer) {
		final int defaultDecomposingTime = 200;
		int id = 0;

		DecomposerRecipeBuilder.createRecipe(ModItems.HORMONE_SERUM.get(), defaultDecomposingTime)
				.addIngredients(Items.INK_SAC, 2)
				.addCriterion("has_hormones", hasItem(ModTags.Items.HORMONES)).build(consumer, "" + id++, true);

		DecomposerRecipeBuilder.createRecipe(ModItems.HORMONE_SERUM.get(), defaultDecomposingTime, 3)
				.addIngredient(Items.RABBIT_FOOT)
				.addCriterion("has_hormones", hasItem(ModTags.Items.HORMONES)).build(consumer, "" + id++, true);

		DecomposerRecipeBuilder.createRecipe(ModItems.HORMONE_SERUM.get(), defaultDecomposingTime, 3)
				.addIngredient(Items.GHAST_TEAR)
				.addCriterion("has_hormones", hasItem(ModTags.Items.HORMONES)).build(consumer, "" + id++, true);

		DecomposerRecipeBuilder.createRecipe(ModItems.HORMONE_SERUM.get(), defaultDecomposingTime * 3)
				.addIngredients(Items.SPIDER_EYE, 6)
				.addCriterion("has_hormones", hasItem(ModTags.Items.HORMONES)).build(consumer, "" + id++, true);

		DecomposerRecipeBuilder.createRecipe(ModItems.HORMONE_SERUM.get(), defaultDecomposingTime * 2)
				.addIngredients(Items.FERMENTED_SPIDER_EYE, 4)
				.addCriterion("has_hormones", hasItem(ModTags.Items.HORMONES)).build(consumer, "" + id++, true);

		DecomposerRecipeBuilder.createRecipe(ModItems.HORMONE_SERUM.get(), defaultDecomposingTime)
				.addIngredients(Items.HONEYCOMB, 2)
				.addCriterion("has_hormones", hasItem(ModTags.Items.HORMONES)).build(consumer, "" + id++, true);

		DecomposerRecipeBuilder.createRecipe(ModItems.HORMONE_SERUM.get(), defaultDecomposingTime)
				.addIngredient(Items.SLIME_BALL)
				.addByproduct(ModItems.MUTAGENIC_BILE.get(), 0.15f)
				.addByproduct(ModItems.ERODING_BILE.get(), 0.2f)
				.addByproduct(ModItems.REJUVENATING_MUCUS.get(), 0.2f)
				.addCriterion("has_slime_ball", hasItem(Items.SLIME_BALL)).build(consumer, "" + id, true);
	}

	private void registerMutagenRecipes(Consumer<IFinishedRecipe> consumer) {
		final int defaultDecomposingTime = 200;
		int id = 0;

		DecomposerRecipeBuilder.createRecipe(ModItems.MUTAGENIC_BILE.get(), defaultDecomposingTime)
				.addIngredient(Items.WARPED_FUNGUS)
				.addByproduct(ModItems.DIGESTATE.get(), 0.4f)
				.addCriterion("has_warped_fungus", hasItem(Items.WARPED_FUNGUS)).build(consumer, "" + id++, true);

		DecomposerRecipeBuilder.createRecipe(ModItems.MUTAGENIC_BILE.get(), defaultDecomposingTime, 6)
				.addIngredient(Items.WARPED_WART_BLOCK)
				.addByproduct(ModItems.DIGESTATE.get(), 0.6f)
				.addCriterion("has_warped_fungus", hasItem(Items.WARPED_FUNGUS)).build(consumer, "" + id++, true);

		DecomposerRecipeBuilder.createRecipe(ModItems.MUTAGENIC_BILE.get(), defaultDecomposingTime)
				.addIngredients(Items.WARPED_ROOTS, 2)
				.addByproduct(ModItems.DIGESTATE.get(), 0.4f)
				.addCriterion("has_warped_roots", hasItem(Items.WARPED_ROOTS)).build(consumer, "" + id++, true);

		DecomposerRecipeBuilder.createRecipe(ModItems.MUTAGENIC_BILE.get(), defaultDecomposingTime, 6)
				.addIngredient(ModItems.TWISTED_HEART.get())
				.addByproduct(ModItems.KERATIN_FILAMENTS.get(), 2, 0.5f)
				.addCriterion("has_twisted_heart", hasItem(ModItems.TWISTED_HEART.get())).build(consumer, "" + id, true);
	}

	private void registerChewerRecipes(Consumer<IFinishedRecipe> consumer) {
		final int defaultChewingTime = 200;

		// crushed biomass /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		ComposterBlock.CHANCES.keySet().stream().filter(IGNORE_COMPOSTABLE_PREDICATE).forEach(iItemProvider -> {
			float chance = ComposterBlock.CHANCES.getFloat(iItemProvider);
			int amount = 7 - MathHelper.clamp(Math.round(-7.14f * chance + 8.14f), 1, 6);
			ResourceLocation itemId = Registry.ITEM.getKey(iItemProvider.asItem());
			String namespace = itemId.getNamespace().equals("minecraft") ? "mc" : itemId.getNamespace();
			ChewerRecipeBuilder.createRecipe(ModItems.BOLUS.get(), defaultChewingTime + Math.round(Math.max(1f - chance, 0f) * 150), amount)
					.setIngredient(iItemProvider.asItem())
					.addCriterion("has_raw_biomass", hasItem(ModTags.Items.RAW_BIOMASS)).build(consumer, "from_" + namespace + '_' + itemId.getPath(), true);
		});

		// misc ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		ChewerRecipeBuilder.createRecipe(Items.COBBLESTONE, defaultChewingTime)
				.setIngredient(Items.STONE)
				.addCriterion("has_stone", hasItem(Items.STONE)).build(consumer, "from_stone", true);

		ChewerRecipeBuilder.createRecipe(Items.GRAVEL, defaultChewingTime * 2)
				.setIngredient(Items.COBBLESTONE)
				.addCriterion("has_cobble", hasItem(Items.COBBLESTONE)).build(consumer, "from_cobblestone", true);

		ChewerRecipeBuilder.createRecipe(Items.SAND, defaultChewingTime * 3)
				.setIngredient(Items.GRAVEL)
				.addCriterion("has_gravel", hasItem(Items.GRAVEL)).build(consumer, "from_gravel", true);

		ChewerRecipeBuilder.createRecipe(Items.SAND, defaultChewingTime * 3, 4)
				.setIngredient(Items.SANDSTONE)
				.addCriterion("has_sandstone", hasItem(Items.SANDSTONE)).build(consumer, "from_sandstone", true);

		ChewerRecipeBuilder.createRecipe(Items.RED_SAND, defaultChewingTime * 3, 4)
				.setIngredient(Items.RED_SANDSTONE)
				.addCriterion("has_red_sandstone", hasItem(Items.RED_SANDSTONE)).build(consumer, "from_red_sandstone", true);

		// silicates ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		ChewerRecipeBuilder.createRecipe(ModItems.SILICATE_PASTE.get(), defaultChewingTime * 4, 2)
				.setIngredient(Items.SAND)
				.addCriterion("has_sand", hasItem(Items.SAND)).build(consumer, "from_sand", true);

		ChewerRecipeBuilder.createRecipe(ModItems.SILICATE_PASTE.get(), defaultChewingTime * 4, 1) //give less silicate because the sand contains oxides
				.setIngredient(Items.RED_SAND)
				.addCriterion("has_red_sand", hasItem(Items.RED_SAND)).build(consumer, "from_red_sand", true);

		// oxides //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		ChewerRecipeBuilder.createRecipe(ModItems.OXIDE_POWDER.get(), defaultChewingTime * 3, 9)
				.setIngredient(Tags.Items.INGOTS_IRON)
				.addCriterion("has_oxide", hasItem(ModTags.Items.OXIDES)).build(consumer, "from_iron_ingot", true);

		ChewerRecipeBuilder.createRecipe(ModItems.OXIDE_POWDER.get(), defaultChewingTime)
				.setIngredient(Tags.Items.NUGGETS_IRON)
				.addCriterion("has_oxide", hasItem(ModTags.Items.OXIDES)).build(consumer, "from_iron_nugget", true);

		ChewerRecipeBuilder.createRecipe(ModItems.OXIDE_POWDER.get(), defaultChewingTime * 3, 9)
				.setIngredient(Tags.Items.INGOTS_GOLD)
				.addCriterion("has_oxide", hasItem(ModTags.Items.OXIDES)).build(consumer, "from_gold_ingot", true);

		ChewerRecipeBuilder.createRecipe(ModItems.OXIDE_POWDER.get(), defaultChewingTime)
				.setIngredient(Tags.Items.NUGGETS_GOLD)
				.addCriterion("has_oxide", hasItem(ModTags.Items.OXIDES)).build(consumer, "from_gold_nugget", true);

		ChewerRecipeBuilder.createRecipe(ModItems.OXIDE_POWDER.get(), defaultChewingTime, 6)
				.setIngredient(Items.NETHERITE_SCRAP)
				.addCriterion("has_oxide", hasItem(ModTags.Items.OXIDES)).build(consumer, "from_netherite_scrap", true);

		ChewerRecipeBuilder.createRecipe(ModItems.OXIDE_POWDER.get(), defaultChewingTime * 3, 4 * 6 + 4 * 9)
				.setIngredient(Tags.Items.INGOTS_NETHERITE)
				.addCriterion("has_oxide", hasItem(ModTags.Items.OXIDES)).build(consumer, "from_netherite_ingot", true);
	}

	private void registerDigesterRecipes(Consumer<IFinishedRecipe> consumer) {
		final int defaultTime = 200;

		DigesterRecipeBuilder.createRecipe(ModItems.NUTRIENT_PASTE.get(), defaultTime)
				.setIngredient(ModItems.BOLUS.get())
				.setByproduct(ModItems.DIGESTATE.get(), 1f)
				.addCriterion("has_crushed_biomass", hasItem(ModItems.BOLUS.get())).build(consumer);
	}

	private void registerDecomposerRecipes(Consumer<IFinishedRecipe> consumer) {
		final int defaultDecomposingTime = 200;

		registerMutagenRecipes(consumer);
		registerOxidesRecipes(consumer);
		registerSilicatesRecipes(consumer);
		registerKeratinsRecipes(consumer);
		registerHormonesRecipes(consumer);

		// sugars //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		ComposterBlock.CHANCES.keySet().stream().filter(IGNORE_COMPOSTABLE_PREDICATE).forEach(iItemProvider -> {
			float chance = ComposterBlock.CHANCES.getFloat(iItemProvider);
			int amount = 7 - MathHelper.clamp(Math.round(-7.14f * chance + 8.14f), 1, 6);
			ResourceLocation itemId = Registry.ITEM.getKey(iItemProvider.asItem());
			String namespace = itemId.getNamespace().equals("minecraft") ? "mc" : itemId.getNamespace();
			DecomposerRecipeBuilder.createRecipe(Items.SUGAR, defaultDecomposingTime + Math.round(Math.max(1f - chance, 0f) * 150), amount)
					.addIngredient(iItemProvider.asItem())
//					.addByproduct(ModItems.DIGESTATE.get(), 0.05f)
					.addCriterion("has_raw_biomass", hasItem(ModTags.Items.RAW_BIOMASS)).build(consumer, "from_" + namespace + '_' + itemId.getPath(), true);
		});

		// misc ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		DecomposerRecipeBuilder.createRecipe(ModItems.FLESH_LUMP.get(), defaultDecomposingTime)
				.addIngredient(Items.CHICKEN)
				.addByproduct(ModItems.MUTAGENIC_BILE.get(), 0.3f)
				.addCriterion("has_chicken", hasItem(Items.CHICKEN)).build(consumer);

		DecomposerRecipeBuilder.createRecipe(ModItems.FLESH_LUMP.get(), defaultDecomposingTime, 2)
				.addIngredient(ModTags.Items.RAW_MEATS)
				.addByproduct(ModItems.SKIN_CHUNK.get(), 0.4f)
				.addByproduct(ModItems.BONE_SCRAPS.get(), 0.2f)
				.addCriterion("has_raw_meat", hasItem(ModTags.Items.RAW_MEATS)).build(consumer, "from_raw_meat", true);

		DecomposerRecipeBuilder.createRecipe(ModItems.REJUVENATING_MUCUS.get(), defaultDecomposingTime)
				.addIngredient(Items.SUSPICIOUS_STEW)
				.addByproduct(ModItems.MUTAGENIC_BILE.get(), 0.15f)
				.addCriterion("has_suspicious_stew", hasItem(Items.SUSPICIOUS_STEW)).build(consumer);

		DecomposerRecipeBuilder.createRecipe(ModItems.REJUVENATING_MUCUS.get(), defaultDecomposingTime)
				.addIngredient(ModTags.Items.COOKED_MEATS)
				.addCriterion("has_any_cooked_meat", hasItem(ModTags.Items.COOKED_MEATS)).build(consumer, "from_cooked_meat", true);

//		DecomposingRecipeBuilder.decomposingRecipe(ModItems.REJUVENATING_MUCUS.get(), defaultDecomposingTime)
//				.addIngredient(new AnyMeatlessFoodIngredient()) //TODO: fix this
//				.addCriterion("has_any_meatless_food", hasItem(ModRecipes.ANY_MEATLESS_FOOD_ITEM_PREDICATE)).build(consumer, "from_meatless_food", true);

		DecomposerRecipeBuilder.createRecipe(ModItems.REJUVENATING_MUCUS.get(), defaultDecomposingTime, 3)
				.addIngredient(new ItemStackIngredient(PotionUtils.addPotionToItemStack(new ItemStack(Items.POTION), Potions.REGENERATION)))
				.addCriterion("has_potion", hasItem(Items.POTION)).build(consumer, "from_regen_potion", true);

		DecomposerRecipeBuilder.createRecipe(ModItems.REJUVENATING_MUCUS.get(), defaultDecomposingTime, 5)
				.addIngredient(new ItemStackIngredient(PotionUtils.addPotionToItemStack(new ItemStack(Items.POTION), Potions.LONG_REGENERATION)))
				.addCriterion("has_potion", hasItem(Items.POTION)).build(consumer, "from_long_regen_potion", true);

		DecomposerRecipeBuilder.createRecipe(ModItems.REJUVENATING_MUCUS.get(), defaultDecomposingTime, 5)
				.addIngredient(new ItemStackIngredient(PotionUtils.addPotionToItemStack(new ItemStack(Items.POTION), Potions.STRONG_REGENERATION)))
				.addCriterion("has_potion", hasItem(Items.POTION)).build(consumer, "from_strong_regen_potion", true);

		DecomposerRecipeBuilder.createRecipe(ModItems.REJUVENATING_MUCUS.get(), defaultDecomposingTime, 3)
				.addIngredient(new ItemStackIngredient(PotionUtils.addPotionToItemStack(new ItemStack(Items.POTION), Potions.HEALING)))
				.addCriterion("has_potion", hasItem(Items.POTION)).build(consumer, "from_healing_potion", true);

		DecomposerRecipeBuilder.createRecipe(ModItems.REJUVENATING_MUCUS.get(), defaultDecomposingTime, 5)
				.addIngredient(new ItemStackIngredient(PotionUtils.addPotionToItemStack(new ItemStack(Items.POTION), Potions.STRONG_HEALING)))
				.addCriterion("has_potion", hasItem(Items.POTION)).build(consumer, "from_strong_healing_potion", true);

		DecomposerRecipeBuilder.createRecipe(ModItems.ERODING_BILE.get(), defaultDecomposingTime)
				.addIngredient(Items.ROTTEN_FLESH)
				.addByproduct(ModItems.FLESH_LUMP.get(), 0.2f)
				.addCriterion("has_rotten_flesh", hasItem(Items.ROTTEN_FLESH)).build(consumer);

		DecomposerRecipeBuilder.createRecipe(Items.SKELETON_SKULL, defaultDecomposingTime * 2)
				.addIngredient(Items.ZOMBIE_HEAD)
				.addByproduct(ModItems.ERODING_BILE.get())
				.addByproduct(ModItems.FLESH_LUMP.get(), 0.2f)
				.addCriterion("has_zombie_head", hasItem(Items.ZOMBIE_HEAD)).build(consumer);
	}

	private void registerCookingRecipes(Consumer<IFinishedRecipe> consumer) {
		CookingRecipeBuilder.cookingRecipe(Ingredient.fromItems(ModItems.MENDED_SKIN.get()), Items.LEATHER, 0.1F, 350, IRecipeSerializer.SMOKING)
				.addCriterion("has_skin_chunk", hasItem(ModItems.SKIN_CHUNK.get())).build(consumer, new ResourceLocation(BiomancyMod.MOD_ID, "leather_from_smoking"));

		CookingRecipeBuilder.smeltingRecipe(Ingredient.fromItems(ModItems.SILICATE_PASTE.get()), Items.GLASS_PANE, 0.1F, 100)
				.addCriterion("has_silicate", hasItem(ModItems.SILICATE_PASTE.get())).build(consumer, new ResourceLocation(BiomancyMod.MOD_ID, "glass_pane_from_smelting_silicate"));

		CookingRecipeBuilder.cookingRecipe(Ingredient.fromItems(ModItems.VILE_MELON_SLICE.get()), ModItems.COOKED_VILE_MELON_SLICE.get(), 0.35F, 100, IRecipeSerializer.SMELTING)
				.addCriterion("has_vile_melon_slice", hasItem(ModItems.VILE_MELON_SLICE.get())).build(consumer, new ResourceLocation(BiomancyMod.MOD_ID, "vile_melon_from_smelting"));

		CookingRecipeBuilder.cookingRecipe(Ingredient.fromItems(ModItems.VILE_MELON_SLICE.get()), ModItems.COOKED_VILE_MELON_SLICE.get(), 0.35F, 600, IRecipeSerializer.CAMPFIRE_COOKING)
				.addCriterion("has_vile_melon_slice", hasItem(ModItems.VILE_MELON_SLICE.get())).build(consumer, new ResourceLocation(BiomancyMod.MOD_ID, "vile_melon_from_campfire"));
	}

	private void registerWorkbenchRecipes(Consumer<IFinishedRecipe> consumer) {

		ShapedRecipeBuilder.shapedRecipe(ModItems.OCULUS.get())
				.key('F', ModItems.FLESH_LUMP.get()).key('R', ModItems.REJUVENATING_MUCUS.get()).key('L', ModItems.MENISCUS_LENS.get()).key('E', Items.SPIDER_EYE)
				.patternLine("FRF").patternLine("LER").patternLine("FRF")
				.addCriterion("has_rejuvenating_mucus", hasItem(ModItems.REJUVENATING_MUCUS.get())).build(consumer);

		ShapedRecipeBuilder.shapedRecipe(ModItems.MENISCUS_LENS.get())
				.key('P', Tags.Items.GLASS_PANES).key('Q', Items.QUARTZ)
				.patternLine(" P ").patternLine("PQP").patternLine(" P ")
				.addCriterion("has_quartz", hasItem(Items.QUARTZ)).build(consumer);

		ShapelessRecipeBuilder.shapelessRecipe(ModItems.SEWING_KIT_EMPTY.get())
				.addIngredient(Tags.Items.BONES).addIngredient(Items.FLINT)
				.addCriterion("has_bone", hasItem(Tags.Items.BONES)).build(consumer);

		ShapedRecipeBuilder.shapedRecipe(ModItems.SHARP_BONE.get())
				.key('S', ModItems.SEWING_KIT_EMPTY.get()).key('B', Tags.Items.BONES)
				.patternLine("S").patternLine("B")
				.addCriterion("has_bone", hasItem(Tags.Items.BONES)).build(consumer);

		ShapelessRecipeBuilder.shapelessRecipe(ModItems.SEWING_KIT.get())
				.addIngredient(ModItems.SEWING_KIT_EMPTY.get()).addIngredient(Tags.Items.STRING).addIngredient(Tags.Items.STRING).addIngredient(Tags.Items.STRING)
				.addCriterion("has_empty_sewing_kit", hasItem(ModItems.SEWING_KIT_EMPTY.get())).build(consumer);

		ShapedRecipeBuilder.shapedRecipe(ModItems.SYRINGE.get())
				.key('B', Items.GLASS_BOTTLE).key('S', ModItems.SEWING_KIT_EMPTY.get()).key('I', Items.IRON_INGOT).key('N', Items.IRON_NUGGET)
				.patternLine("SBN").patternLine("NII").patternLine("  I")
				.addCriterion("has_quartz", hasItem(Items.QUARTZ)).build(consumer);

		ShapelessRecipeBuilder.shapelessRecipe(Items.BONE_MEAL)
				.addIngredient(ModItems.BONE_SCRAPS.get(), 4)
				.addCriterion("has_bone_scraps", hasItem(ModItems.BONE_SCRAPS.get())).build(consumer);

		ShapedRecipeBuilder.shapedRecipe(ModItems.MENDED_SKIN.get())
				.key('S', ModItems.SEWING_KIT.get())
				.key('C', ModItems.SKIN_CHUNK.get())
				.patternLine("CC ").patternLine("CC ").patternLine("CCS")
				.addCriterion("has_skin_chunk", hasItem(ModItems.SKIN_CHUNK.get())).build(consumer);

		ShapedRecipeBuilder.shapedRecipe(ModItems.ARTIFICIAL_STOMACH.get())
				.key('S', ModItems.SEWING_KIT.get())
				.key('M', ModItems.MENDED_SKIN.get())
				.key('C', ModItems.SKIN_CHUNK.get())
				.patternLine("  C").patternLine("MMC").patternLine("MMS")
				.addCriterion("has_mended_skin", hasItem(ModItems.MENDED_SKIN.get())).build(consumer);

		ShapedRecipeBuilder.shapedRecipe(ModItems.FLESH_BLOCK_SLAB.get(), 3 * 2)
				.key('F', ModItems.FLESH_BLOCK.get())
				.patternLine("FFF")
				.addCriterion("has_flesh_block", hasItem(ModItems.FLESH_BLOCK.get())).build(consumer);

		ShapedRecipeBuilder.shapedRecipe(ModItems.FLESH_BLOCK_STAIRS.get(), 4)
				.key('F', ModItems.FLESH_BLOCK.get())
				.patternLine("F  ")
				.patternLine("FF ")
				.patternLine("FFF")
				.addCriterion("has_flesh_block", hasItem(ModItems.FLESH_BLOCK.get())).build(consumer);

		ShapelessRecipeBuilder.shapelessRecipe(ModItems.NECROTIC_FLESH.get(), 9)
				.addIngredient(ModItems.NECROTIC_FLESH_BLOCK.get())
				.addCriterion("has_necrotic_flesh_block", hasItem(ModItems.NECROTIC_FLESH_BLOCK.get())).build(consumer);

		ShapelessRecipeBuilder.shapelessRecipe(ModItems.NECROTIC_FLESH_BLOCK.get())
				.addIngredient(ModItems.NECROTIC_FLESH.get(), 9)
				.addCriterion("has_necrotic_flesh", hasItem(ModItems.NECROTIC_FLESH.get())).build(consumer);

		ShapelessRecipeBuilder.shapelessRecipe(ModItems.FLESH_BLOCK.get())
				.addIngredient(ModItems.FLESH_LUMP.get(), 9)
				.addCriterion("has_flesh_lump", hasItem(ModItems.FLESH_LUMP.get())).build(consumer);

		ShapelessRecipeBuilder.shapelessRecipe(ModItems.FLESH_LUMP.get(), 9)
				.addIngredient(ModItems.FLESH_BLOCK.get())
				.addCriterion("has_flesh_block", hasItem(ModItems.FLESH_BLOCK.get())).build(consumer);

		ShapelessRecipeBuilder.shapelessRecipe(ModItems.FLESH_LUMP.get())
				.addIngredient(ModItems.NECROTIC_FLESH.get()).addIngredient(ModItems.REJUVENATING_MUCUS.get())
				.addCriterion("has_necrotic_flesh", hasItem(ModItems.NECROTIC_FLESH.get())).build(consumer, BiomancyMod.createRL("flesh_from_necrotic"));

		// machines ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

		ShapedRecipeBuilder.shapedRecipe(ModItems.DECOMPOSER.get())
				.key('S', ModItems.DIGESTER.get())
				.key('F', ModItems.FLESH_BLOCK.get())
				.key('L', ModItems.FLESH_BLOCK_SLAB.get())
				.patternLine("FLF").patternLine("FSF").patternLine("FLF")
				.addCriterion("has_digester", hasItem(ModItems.DIGESTER.get())).build(consumer);

		ShapedRecipeBuilder.shapedRecipe(ModItems.CHEWER.get())
				.key('S', Items.STONECUTTER)
				.key('F', ModItems.FLESH_BLOCK.get())
				.key('L', ModItems.FLESH_BLOCK_SLAB.get())
				.patternLine("FLF").patternLine("FSF").patternLine("FLF")
				.addCriterion("has_flesh_block", hasItem(ModItems.FLESH_BLOCK.get())).build(consumer);

		ShapedRecipeBuilder.shapedRecipe(ModItems.DIGESTER.get())
				.key('S', ModItems.ARTIFICIAL_STOMACH.get())
				.key('F', ModItems.FLESH_BLOCK.get())
				.key('L', ModItems.FLESH_BLOCK_SLAB.get())
				.patternLine("FLF").patternLine("FSF").patternLine("FLF")
				.addCriterion("has_stomach", hasItem(ModItems.ARTIFICIAL_STOMACH.get())).build(consumer);

		// food ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

		ShapedRecipeBuilder.shapedRecipe(ModItems.NUTRIENT_BAR.get())
				.key('N', ModItems.NUTRIENT_PASTE.get())
				.key('B', Items.SWEET_BERRIES)
				.key('S', Tags.Items.SEEDS)
				.patternLine("SBS").patternLine("NNN")
				.addCriterion("has_nutrient_paste", hasItem(ModItems.NUTRIENT_PASTE.get())).build(consumer);

		ShapelessRecipeBuilder.shapelessRecipe(ModItems.FLESH_MELON_SEEDS.get())
				.addIngredient(ModItems.VILE_MELON_SLICE.get())
				.addCriterion("has_vile_melon_slice", hasItem(ModItems.VILE_MELON_SLICE.get())).build(consumer);

		// misc ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		ShapelessRecipeBuilder.shapelessRecipe(Items.DIORITE)
				.addIngredient(Items.COBBLESTONE).addIngredient(ModItems.SILICATE_PASTE.get()).addIngredient(ModItems.SILICATE_PASTE.get())
				.addCriterion("has_silicate_paste", hasItem(ModItems.SILICATE_PASTE.get())).build(consumer, BiomancyMod.createRL("diorite_from_silicate"));

		ShapelessRecipeBuilder.shapelessRecipe(Items.RED_SAND)
				.addIngredient(Items.SAND).addIngredient(ModItems.OXIDE_POWDER.get()).addIngredient(ModItems.OXIDE_POWDER.get())
				.addCriterion("has_oxide_powder", hasItem(ModItems.OXIDE_POWDER.get())).build(consumer, BiomancyMod.createRL("red_sand_from_oxide"));

		ShapedRecipeBuilder.shapedRecipe(Items.DIRT)
				.key('S', Items.SAND)
				.key('D', ModItems.DIGESTATE.get())
				.patternLine("DDD").patternLine("DSD").patternLine("DDD")
				.addCriterion("has_digestate", hasItem(ModItems.DIGESTATE.get())).build(consumer, BiomancyMod.createRL("dirt_from_digestate"));

		ShapedRecipeBuilder.shapedRecipe(Items.CLAY_BALL)
				.key('S', ModItems.SILICATE_PASTE.get())
				.key('D', ModItems.DIGESTATE.get())
				.patternLine(" S ").patternLine("SDS").patternLine(" S ")
				.addCriterion("has_siligestate", hasItems(ModItems.DIGESTATE.get(), ModItems.SILICATE_PASTE.get())).build(consumer, BiomancyMod.createRL("clay_ball_from_siligestate"));

		ShapelessRecipeBuilder.shapelessRecipe(Items.GUNPOWDER, 2)
				.addIngredient(Items.CHARCOAL).addIngredient(ModItems.SILICATE_PASTE.get(), 3).addIngredient(Items.BLAZE_POWDER, 2)
				.addCriterion("has_silicate_paste", hasItem(ModItems.SILICATE_PASTE.get())).build(consumer, BiomancyMod.createRL("gunpowder_from_silicate_blaze"));

		// special /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		CustomRecipeBuilder.customRecipe(ModRecipes.REPAIR_SPECIAL_SEWING_KIT.get()).build(consumer, BiomancyMod.MOD_ID + ":" + "sewing_kit_nbt");
		CustomRecipeBuilder.customRecipe(ModRecipes.CRAFTING_SPECIAL_POTION_BEETLE.get()).build(consumer, BiomancyMod.MOD_ID + ":" + "potion_beetle");
		CustomRecipeBuilder.customRecipe(ModRecipes.CRAFTING_SPECIAL_MASON_BEETLE.get()).build(consumer, BiomancyMod.MOD_ID + ":" + "mason_beetle");
		CustomRecipeBuilder.customRecipe(ModRecipes.CRAFTING_SPECIAL_ADD_USER_TO_KEY.get()).build(consumer, BiomancyMod.MOD_ID + ":" + "add_user_to_key");
	}

	@Override
	public String getName() {
		return StringUtils.capitalize(BiomancyMod.MOD_ID) + " " + super.getName();
	}
}
