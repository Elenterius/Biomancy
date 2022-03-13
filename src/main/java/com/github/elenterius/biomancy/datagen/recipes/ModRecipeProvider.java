package com.github.elenterius.biomancy.datagen.recipes;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.init.ModItems;
import com.github.elenterius.biomancy.init.ModSerums;
import com.github.elenterius.biomancy.init.ModTags;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.recipes.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.Tag;
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

	protected static ItemPredicate createPredicate(Tag<Item> tag) {
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

	protected static String getTagName(Tag.Named<Item> tag) {
		return tag.getName().getPath();
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

		ShapedRecipeBuilder.shaped(ModItems.BONE_SWORD.get())
				.define('S', Tags.Items.RODS_WOODEN).define('B', Tags.Items.BONES).define('C', ModItems.BONE_SCRAPS.get())
				.pattern(" B ").pattern("CBC").pattern(" S ")
				.unlockedBy(hasName(Items.BONE), has(Tags.Items.BONES)).save(consumer);

		ShapedRecipeBuilder.shaped(ModItems.GLASS_VIAL.get(), 8)
				.define('G', Tags.Items.GLASS).define('T', Items.CLAY_BALL)
				.pattern("GTG").pattern("G G").pattern(" G ")
				.unlockedBy(hasName(Items.GLASS), has(Tags.Items.GLASS)).save(consumer);

		ShapelessRecipeBuilder.shapeless(Items.BONE_MEAL)
				.requires(ModItems.BONE_SCRAPS.get(), 4)
				.unlockedBy(hasName(ModItems.BONE_SCRAPS.get()), has(ModItems.BONE_SCRAPS.get())).save(consumer, getConversionRecipeId(Items.BONE_MEAL, ModItems.BONE_SCRAPS.get()));

		ShapedRecipeBuilder.shaped(ModItems.FLESH_BLOCK_SLAB.get(), 3 * 2)
				.define('F', ModItems.FLESH_BLOCK.get())
				.pattern("FFF")
				.unlockedBy(hasName(ModItems.FLESH_BLOCK.get()), has(ModItems.FLESH_BLOCK.get())).save(consumer);

		ShapedRecipeBuilder.shaped(ModItems.FLESH_BLOCK_STAIRS.get(), 4)
				.define('F', ModItems.FLESH_BLOCK.get())
				.pattern("F  ")
				.pattern("FF ")
				.pattern("FFF")
				.unlockedBy(hasName(ModItems.FLESH_BLOCK.get()), has(ModItems.FLESH_BLOCK.get())).save(consumer);

		ShapelessRecipeBuilder.shapeless(ModItems.NECROTIC_FLESH_LUMP.get(), 9)
				.requires(ModItems.NECROTIC_FLESH_BLOCK.get())
				.unlockedBy(hasName(ModItems.NECROTIC_FLESH_BLOCK.get()), has(ModItems.NECROTIC_FLESH_BLOCK.get())).save(consumer);

		ShapelessRecipeBuilder.shapeless(ModItems.NECROTIC_FLESH_BLOCK.get())
				.requires(ModItems.NECROTIC_FLESH_LUMP.get(), 9)
				.unlockedBy(hasName(ModItems.NECROTIC_FLESH_LUMP.get()), has(ModItems.NECROTIC_FLESH_LUMP.get())).save(consumer);

		ShapelessRecipeBuilder.shapeless(ModItems.FLESH_BLOCK.get())
				.requires(ModItems.FLESH_BITS.get(), 9)
				.unlockedBy(hasName(ModItems.FLESH_BITS.get()), has(ModItems.FLESH_BITS.get())).save(consumer);

		ShapelessRecipeBuilder.shapeless(ModItems.FLESH_BITS.get(), 9)
				.requires(ModItems.FLESH_BLOCK.get())
				.unlockedBy(hasName(ModItems.FLESH_BLOCK.get()), has(ModItems.FLESH_BLOCK.get())).save(consumer);

		ShapelessRecipeBuilder.shapeless(ModItems.FLESH_BITS.get())
				.requires(ModItems.NECROTIC_FLESH_LUMP.get()).requires(ModItems.NUTRIENTS.get())
				.unlockedBy(hasName(ModItems.NECROTIC_FLESH_LUMP.get()), has(ModItems.NECROTIC_FLESH_LUMP.get())).save(consumer, getConversionRecipeId(ModItems.FLESH_BITS.get(), ModItems.NECROTIC_FLESH_LUMP.get()));

		ShapelessRecipeBuilder.shapeless(ModItems.FLESH_BLOCK.get())
				.requires(ModItems.NECROTIC_FLESH_BLOCK.get()).requires(ModItems.NUTRIENTS.get(), 6)
				.unlockedBy(hasName(ModItems.NECROTIC_FLESH_BLOCK.get()), has(ModItems.NECROTIC_FLESH_BLOCK.get())).save(consumer, getConversionRecipeId(ModItems.FLESH_BLOCK.get(), ModItems.NECROTIC_FLESH_BLOCK.get()));

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

		ShapedRecipeBuilder.shaped(ModItems.PROTEIN_BAR.get())
				.define('N', ModItems.NUTRIENTS.get())
				.define('B', ModItems.FLESH_BITS.get())
				.define('S', Tags.Items.SEEDS)
				.pattern("SBS").pattern("NNN")
				.unlockedBy(hasName(ModItems.NUTRIENTS.get()), has(ModItems.NUTRIENTS.get())).save(consumer);

		// misc ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		ShapelessRecipeBuilder.shapeless(Items.DIORITE)
				.requires(Items.COBBLESTONE).requires(ModItems.MINERAL_DUST.get()).requires(ModItems.LITHIC_POWDER.get())
				.unlockedBy(hasName(ModItems.LITHIC_POWDER.get()), has(ModItems.LITHIC_POWDER.get())).save(consumer, getConversionRecipeId(Items.DIORITE, ModItems.LITHIC_POWDER.get()));

		ShapelessRecipeBuilder.shapeless(Items.RED_SAND)
				.requires(Items.SAND).requires(ModItems.OXIDES.get(), 2)
				.unlockedBy(hasName(ModItems.OXIDES.get()), has(ModItems.OXIDES.get())).save(consumer, getConversionRecipeId(Items.RED_SAND, ModItems.OXIDES.get()));

		ShapedRecipeBuilder.shaped(Items.DIRT)
				.define('P', ModItems.PLANT_MATTER.get())
				.define('L', ModItems.LITHIC_POWDER.get())
				.pattern("LPL").pattern("PLP").pattern("LPL")
				.unlockedBy(hasName(ModItems.PLANT_MATTER.get()), has(ModItems.PLANT_MATTER.get())).save(consumer, getConversionRecipeId(Items.DIRT, ModItems.LITHIC_POWDER.get()));

		ShapelessRecipeBuilder.shapeless(Items.CLAY_BALL)
				.requires(Items.WATER_BUCKET)
				.requires(ModItems.LITHIC_POWDER.get(), 8)
				.unlockedBy(hasName(ModItems.LITHIC_POWDER.get()), has(ModItems.LITHIC_POWDER.get())).save(consumer, getConversionRecipeId(Items.CLAY_BALL, ModItems.LITHIC_POWDER.get()));

		ShapelessRecipeBuilder.shapeless(Items.GUNPOWDER, 2)
				.requires(Items.CHARCOAL).requires(ModItems.EXOTIC_DUST.get(), 4).requires(Items.BLAZE_POWDER, 2)
				.unlockedBy(hasName(ModItems.EXOTIC_DUST.get()), has(ModItems.EXOTIC_DUST.get())).save(consumer, getConversionRecipeId(Items.GUNPOWDER, ModItems.EXOTIC_DUST.get()));
	}

	private void registerDecomposingRecipes(Consumer<FinishedRecipe> consumer) {
		DecomposerRecipeBuilder.create(getItemName(Items.ENCHANTED_GOLDEN_APPLE))
				.setIngredient(Items.ENCHANTED_GOLDEN_APPLE)
				.setCraftingTime(200)
				.addOutput(ModItems.BIO_LUMENS.get(), 1, 3) //uniform
				.addOutput(ModItems.OXIDES.get(), 1) // constant value
				.addOutput(ModItems.PLANT_MATTER.get(), 1, 3)
				.addOutput(ModItems.EXOTIC_DUST.get(), 15, 20)
				.addByproduct(Items.SUGAR, 2, 4)
				.addByproduct(ModItems.BILE_EXTRACT.get(), 2, 4)
				.unlockedBy(hasName(Items.ENCHANTED_GOLDEN_APPLE), has(Items.ENCHANTED_GOLDEN_APPLE))
				.save(consumer);

		DecomposerRecipeBuilder.create(getItemName(ModItems.MOB_SINEW.get()))
				.setIngredient(ModItems.MOB_SINEW.get())
				.addOutput(ModItems.FLESH_BITS.get(), 1, 3)
				.addOutput(ModItems.ELASTIC_FIBERS.get(), 1, 2)
				.unlockedBy(hasName(ModItems.MOB_SINEW.get()), has(ModItems.MOB_SINEW.get()))
				.save(consumer);

		DecomposerRecipeBuilder.create(getItemName(ModItems.LARYNX.get()))
				.setIngredient(ModItems.LARYNX.get())
				.addOutput(ModItems.FLESH_BITS.get(), 1, 2)
				.addOutput(ModItems.ELASTIC_FIBERS.get(), 2, 4)
				.addByproduct(ModItems.BILE_EXTRACT.get(), 1, 2)
				.unlockedBy(hasName(ModItems.LARYNX.get()), has(ModItems.LARYNX.get()))
				.save(consumer);

		DecomposerRecipeBuilder.create(getItemName(ModItems.EXALTED_LIVING_FLESH.get()))
				.setIngredient(ModItems.LIVING_FLESH.get())
				.addOutput(ModItems.FLESH_BITS.get(), 2, 5)
				.addOutput(ModItems.ELASTIC_FIBERS.get(), 1, 4)
				.addOutput(ModItems.EXOTIC_DUST.get(), 4, 8)
				.addByproduct(ModItems.BILE_EXTRACT.get(), 2, 4)
				.unlockedBy(hasName(ModItems.EXALTED_LIVING_FLESH.get()), has(ModItems.EXALTED_LIVING_FLESH.get()))
				.save(consumer);

		DecomposerRecipeBuilder.create("raw_meats")
				.setIngredient(ModTags.Items.RAW_MEATS)
				.addOutput(ModItems.FLESH_BITS.get(), 2, 4)
				.addOutput(ModItems.ELASTIC_FIBERS.get(), 0, 1)
				.addOutput(ModItems.BONE_SCRAPS.get(), 0, 2)
				.unlockedBy("has_raw_meats", has(ModTags.Items.RAW_MEATS))
				.save(consumer);
	}

	private void registerBioForgeRecipes(Consumer<FinishedRecipe> consumer) {
		BioForgeRecipeBuilder.create(new ItemData(ModItems.OCULUS.get()))
				.addIngredient(ModItems.FLESH_BITS.get(), 20)
				.addIngredient(ModItems.ELASTIC_FIBERS.get(), 10)
				.unlockedBy(hasName(ModItems.ELASTIC_FIBERS.get()), has(ModItems.ELASTIC_FIBERS.get()))
				.save(consumer);
	}

	private void registerBioLabRecipes(Consumer<FinishedRecipe> consumer) {
		BioLabRecipeBuilder.create(ModSerums.REJUVENATION_SERUM.get())
				.addIngredient(ModItems.EXOTIC_DUST.get())
				.addIngredient(ModItems.REJUVENATING_MUCUS.get())
				.addIngredient(ModItems.NUTRIENTS.get())
				.setCraftingTime(5 * 20)
				.unlockedBy(hasName(ModItems.NUTRIENTS.get()), has(ModItems.NUTRIENTS.get()))
				.save(consumer);

		BioLabRecipeBuilder.create(ModSerums.GROWTH_SERUM.get())
				.addIngredient(Items.BONE_MEAL)
				.addIngredient(ModItems.REJUVENATING_MUCUS.get())
				.addIngredient(ModItems.NUTRIENTS.get())
				.setCraftingTime(5 * 20)
				.unlockedBy(hasName(ModItems.NUTRIENTS.get()), has(ModItems.NUTRIENTS.get()))
				.save(consumer);

		BioLabRecipeBuilder.create(ModSerums.BREEDING_STIMULANT.get())
				.addIngredient(Items.SUGAR)
				.addIngredients(ModItems.REJUVENATING_MUCUS.get(), 2)
				.addIngredient(ModItems.NUTRIENTS.get())
				.setCraftingTime(5 * 20)
				.unlockedBy(hasName(ModItems.NUTRIENTS.get()), has(ModItems.NUTRIENTS.get()))
				.save(consumer);

		BioLabRecipeBuilder.create(ModSerums.CLEANSING_SERUM.get())
				.addIngredients(ModSerums.REJUVENATION_SERUM.get(), 2)
				.addIngredient(ModItems.REJUVENATING_MUCUS.get())
				.addIngredient(ModItems.NUTRIENTS.get())
				.setCraftingTime(8 * 20)
				.unlockedBy(hasName(ModItems.NUTRIENTS.get()), has(ModItems.NUTRIENTS.get()))
				.save(consumer);

		BioLabRecipeBuilder.create(ModSerums.INSOMNIA_CURE.get())
				.addIngredients(Items.COCOA_BEANS, 2)
				.addIngredient(ModSerums.CLEANSING_SERUM.get())
				.addIngredient(ModItems.NUTRIENTS.get())
				.setCraftingTime(8 * 20)
				.unlockedBy(hasName(ModItems.NUTRIENTS.get()), has(ModItems.NUTRIENTS.get()))
				.save(consumer);

		BioLabRecipeBuilder.create(ModSerums.ABSORPTION_BOOST.get())
				.addIngredient(Items.GOLDEN_APPLE)
				.addIngredients(ModItems.TOUGH_FIBERS.get(), 2)
				.addIngredient(ModSerums.GROWTH_SERUM.get())
				.setCraftingTime(8 * 20)
				.unlockedBy(hasName(ModItems.NUTRIENTS.get()), has(ModItems.NUTRIENTS.get()))
				.save(consumer);

		BioLabRecipeBuilder.create(ModSerums.ADRENALINE_SERUM.get())
				.addIngredient(Items.COCOA_BEANS)
				.addIngredient(ModItems.REJUVENATING_MUCUS.get())
				.addIngredient(PotionUtils.setPotion(new ItemStack(Items.POTION), Potions.STRONG_STRENGTH))
				.addIngredient(PotionUtils.setPotion(new ItemStack(Items.POTION), Potions.STRONG_SWIFTNESS))
				.setCraftingTime(12 * 20)
				.unlockedBy(hasName(ModItems.NUTRIENTS.get()), has(ModItems.NUTRIENTS.get()))
				.save(consumer);

		BioLabRecipeBuilder.create(ModSerums.DECAY_AGENT.get())
				.addIngredients(Items.ROTTEN_FLESH, 2)
				.addIngredient(ModItems.REJUVENATING_MUCUS.get())
				.addIngredient(ModItems.VENOM_EXTRACT.get())
				.setCraftingTime(8 * 20)
				.unlockedBy(hasName(ModItems.NUTRIENTS.get()), has(ModItems.NUTRIENTS.get()))
				.save(consumer);
	}

}
