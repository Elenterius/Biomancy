package com.github.elenterius.biomancy.datagen;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.init.ModItems;
import com.github.elenterius.biomancy.init.ModRecipes;
import com.github.elenterius.biomancy.init.ModTags;
import com.github.elenterius.biomancy.recipe.ModIngredient;
import com.github.elenterius.biomancy.tileentity.EvolutionPoolTileEntity;
import net.minecraft.advancements.criterion.InventoryChangeTrigger;
import net.minecraft.advancements.criterion.ItemPredicate;
import net.minecraft.data.*;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.potion.PotionUtils;
import net.minecraft.potion.Potions;
import net.minecraft.tags.ITag;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.Tags;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import java.util.Arrays;
import java.util.function.Consumer;

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

	@Override
	protected void registerRecipes(Consumer<IFinishedRecipe> consumer) {
		LOGGER.info(logMarker, "registering workbench recipes...");
		registerWorkbenchRecipes(consumer);
		LOGGER.info(logMarker, "registering cooking recipes...");
		registerCookingRecipes(consumer);

		LOGGER.info(logMarker, "registering decomposing recipes...");
		registerDecomposingRecipes(consumer);

		LOGGER.info(logMarker, "registering evolution pool recipes...");
		registerEvolutionPoolRecipes(consumer);

		LOGGER.info(logMarker, "registering misc recipes...");
	}

	private void registerEvolutionPoolRecipes(Consumer<IFinishedRecipe> consumer) {
		final int defaultTime = EvolutionPoolTileEntity.DEFAULT_TIME;

		EvolutionPoolRecipeBuilder.createRecipe(ModItems.BIO_FLESH_DOOR.get(), defaultTime)
				.addIngredient(Items.IRON_DOOR).addIngredient(ModItems.FLESH_BLOCK.get())
				.addCriterion("has_mutagenic_bile", hasItem(ModItems.MUTAGENIC_BILE.get())).build(consumer);

		EvolutionPoolRecipeBuilder.createRecipe(ModItems.BIO_FLESH_TRAPDOOR.get(), defaultTime)
				.addIngredient(Items.IRON_TRAPDOOR).addIngredient(ModItems.OCULUS.get()).addIngredient(ModItems.FLESH_BLOCK.get())
				.addCriterion("has_mutagenic_bile", hasItem(ModItems.MUTAGENIC_BILE.get())).build(consumer);

		EvolutionPoolRecipeBuilder.createRecipe(ModItems.BIO_FLESH_PRESSURE_PLATE.get(), defaultTime)
				.addIngredient(Items.HEAVY_WEIGHTED_PRESSURE_PLATE).addIngredient(ModItems.OCULUS.get()).addIngredient(ModItems.FLESH_BLOCK.get())
				.addCriterion("has_mutagenic_bile", hasItem(ModItems.MUTAGENIC_BILE.get())).build(consumer);

		EvolutionPoolRecipeBuilder.createRecipe(ModItems.GULGE.get(), defaultTime)
				.addIngredient(Items.CHEST).addIngredients(ModItems.ARTIFICIAL_STOMACH.get(), 4).addIngredient(ModItems.FLESH_BLOCK.get())
				.addCriterion("has_mutagenic_bile", hasItem(ModItems.MUTAGENIC_BILE.get())).build(consumer);

		EvolutionPoolRecipeBuilder.createRecipe(ModItems.FLESH_CHEST.get(), defaultTime)
				.addIngredients(Items.CHEST, 2).addIngredient(ModItems.FLESH_BLOCK.get())
				.addCriterion("has_mutagenic_bile", hasItem(ModItems.MUTAGENIC_BILE.get())).build(consumer);

		EvolutionPoolRecipeBuilder.createRecipe(ModItems.SINGLE_ITEM_BAG_ITEM.get(), defaultTime)
				.addIngredient(ModItems.ARTIFICIAL_STOMACH.get()).addIngredient(Items.HOPPER).addIngredients(ModItems.FLESH_LUMP.get(), 3)
				.addCriterion("has_mutagenic_bile", hasItem(ModItems.MUTAGENIC_BILE.get())).build(consumer);

		EvolutionPoolRecipeBuilder.createRecipe(ModItems.ENTITY_STORAGE_ITEM.get(), defaultTime)
				.addIngredient(ModItems.ARTIFICIAL_STOMACH.get()).addIngredient(Items.EGG).addIngredient(Items.ENDER_CHEST).addIngredients(ModItems.FLESH_LUMP.get(), 3)
				.addCriterion("has_mutagenic_bile", hasItem(ModItems.MUTAGENIC_BILE.get())).build(consumer);

		EvolutionPoolRecipeBuilder.createRecipe(ModItems.OCULI_OF_UNVEILING.get(), defaultTime)
				.addIngredient(Items.IRON_HELMET).addIngredients(ModItems.OCULUS.get(), 4).addIngredient(ModItems.FLESH_BLOCK.get())
				.addCriterion("has_mutagenic_bile", hasItems(ModItems.MUTAGENIC_BILE.get(), ModItems.MENISCUS_LENS.get())).build(consumer);

		EvolutionPoolRecipeBuilder.createRecipe(ModItems.BIOFLESH_AXE.get(), defaultTime)
				.addIngredient(Items.IRON_AXE).addIngredient(Items.BONE).addIngredient(ModItems.FLESH_BLOCK.get())
				.addCriterion("has_mutagenic_bile", hasItem(ModItems.MUTAGENIC_BILE.get())).build(consumer);

		EvolutionPoolRecipeBuilder.createRecipe(ModItems.BIOFLESH_SHOVEL.get(), defaultTime)
				.addIngredient(Items.IRON_SHOVEL).addIngredient(Items.BONE).addIngredient(ModItems.FLESH_BLOCK.get())
				.addCriterion("has_mutagenic_bile", hasItem(ModItems.MUTAGENIC_BILE.get())).build(consumer);

		EvolutionPoolRecipeBuilder.createRecipe(ModItems.BIOFLESH_PICKAXE.get(), defaultTime)
				.addIngredient(Items.IRON_PICKAXE).addIngredient(Items.BONE).addIngredient(ModItems.FLESH_BLOCK.get())
				.addCriterion("has_mutagenic_bile", hasItem(ModItems.MUTAGENIC_BILE.get())).build(consumer);

		EvolutionPoolRecipeBuilder.createRecipe(ModItems.BIOFLESH_WAR_AXE.get(), defaultTime)
				.addIngredient(ModItems.BIOFLESH_AXE.get()).addIngredient(ModItems.OCULUS.get()).addIngredient(ModItems.SHARP_BONE.get()).addIngredient(ModItems.ARTIFICIAL_STOMACH.get())
				.addIngredients(ModItems.FLESH_LUMP.get(), 2)
				.addCriterion("has_bioflesh_axe", hasItem(ModItems.BIOFLESH_AXE.get())).build(consumer);
	}

	private void registerDecomposingRecipes(Consumer<IFinishedRecipe> consumer) {
		final int defaultDecomposingTime = 200;

		DecomposingRecipeBuilder.createRecipe(ModItems.MUTAGENIC_BILE.get(), defaultDecomposingTime)
				.addIngredient(Items.WARPED_FUNGUS)
				.addCriterion("has_warped_fungus", hasItem(Items.WARPED_FUNGUS)).build(consumer);

		DecomposingRecipeBuilder.createRecipe(ModItems.FLESH_LUMP.get(), defaultDecomposingTime)
				.addIngredient(Items.CHICKEN)
				.addByproduct(ModItems.MUTAGENIC_BILE.get(), 0.3f)
				.addCriterion("has_chicken", hasItem(Items.CHICKEN)).build(consumer);

		DecomposingRecipeBuilder.createRecipe(ModItems.FLESH_LUMP.get(), defaultDecomposingTime, 2)
				.addIngredient(ModTags.Items.RAW_MEATS)
				.addByproduct(ModItems.SKIN_CHUNK.get(), 0.4f)
				.addByproduct(ModItems.BONE_SCRAPS.get(), 0.2f)
				.addCriterion("has_raw_meat", hasItem(ModTags.Items.RAW_MEATS)).build(consumer, "from_raw_meat", true);

		DecomposingRecipeBuilder.createRecipe(ModItems.REJUVENATING_MUCUS.get(), defaultDecomposingTime)
				.addIngredient(Items.SUSPICIOUS_STEW)
				.addByproduct(ModItems.MUTAGENIC_BILE.get(), 0.15f)
				.addCriterion("has_suspicious_stew", hasItem(Items.SUSPICIOUS_STEW)).build(consumer);

		DecomposingRecipeBuilder.createRecipe(ModItems.REJUVENATING_MUCUS.get(), defaultDecomposingTime)
				.addIngredient(ModTags.Items.COOKED_MEATS)
				.addCriterion("has_any_cooked_meat", hasItem(ModTags.Items.COOKED_MEATS)).build(consumer, "from_cooked_meat", true);

//		DecomposingRecipeBuilder.decomposingRecipe(ModItems.REJUVENATING_MUCUS.get(), defaultDecomposingTime)
//				.addIngredient(new AnyMeatlessFoodIngredient()) //TODO: fix this
//				.addCriterion("has_any_meatless_food", hasItem(ModRecipes.ANY_MEATLESS_FOOD_ITEM_PREDICATE)).build(consumer, "from_meatless_food", true);

		DecomposingRecipeBuilder.createRecipe(ModItems.REJUVENATING_MUCUS.get(), defaultDecomposingTime, 3)
				.addIngredient(new ModIngredient(PotionUtils.addPotionToItemStack(new ItemStack(Items.POTION), Potions.REGENERATION)))
				.addCriterion("has_potion", hasItem(Items.POTION)).build(consumer, "from_regen_potion", true);

		DecomposingRecipeBuilder.createRecipe(ModItems.REJUVENATING_MUCUS.get(), defaultDecomposingTime, 5)
				.addIngredient(new ModIngredient(PotionUtils.addPotionToItemStack(new ItemStack(Items.POTION), Potions.LONG_REGENERATION)))
				.addCriterion("has_potion", hasItem(Items.POTION)).build(consumer, "from_long_regen_potion", true);

		DecomposingRecipeBuilder.createRecipe(ModItems.REJUVENATING_MUCUS.get(), defaultDecomposingTime, 5)
				.addIngredient(new ModIngredient(PotionUtils.addPotionToItemStack(new ItemStack(Items.POTION), Potions.STRONG_REGENERATION)))
				.addCriterion("has_potion", hasItem(Items.POTION)).build(consumer, "from_strong_regen_potion", true);

		DecomposingRecipeBuilder.createRecipe(ModItems.REJUVENATING_MUCUS.get(), defaultDecomposingTime, 3)
				.addIngredient(new ModIngredient(PotionUtils.addPotionToItemStack(new ItemStack(Items.POTION), Potions.HEALING)))
				.addCriterion("has_potion", hasItem(Items.POTION)).build(consumer, "from_healing_potion", true);

		DecomposingRecipeBuilder.createRecipe(ModItems.REJUVENATING_MUCUS.get(), defaultDecomposingTime, 5)
				.addIngredient(new ModIngredient(PotionUtils.addPotionToItemStack(new ItemStack(Items.POTION), Potions.STRONG_HEALING)))
				.addCriterion("has_potion", hasItem(Items.POTION)).build(consumer, "from_strong_healing_potion", true);

		DecomposingRecipeBuilder.createRecipe(ModItems.ERODING_BILE.get(), defaultDecomposingTime)
				.addIngredient(Items.ROTTEN_FLESH)
				.addByproduct(ModItems.FLESH_LUMP.get(), 0.2f)
				.addCriterion("has_rotten_flesh", hasItem(Items.ROTTEN_FLESH)).build(consumer);

		DecomposingRecipeBuilder.createRecipe(Items.ZOMBIE_HEAD, defaultDecomposingTime * 2)
				.addIngredient(Items.PLAYER_HEAD).addIngredients(ModItems.ERODING_BILE.get(), 2)
				.addCriterion("has_eroding_bile", hasItem(ModItems.ERODING_BILE.get())).build(consumer);

		DecomposingRecipeBuilder.createRecipe(Items.SKELETON_SKULL, defaultDecomposingTime * 2)
				.addIngredient(Items.ZOMBIE_HEAD)
				.addByproduct(ModItems.ERODING_BILE.get())
				.addByproduct(ModItems.FLESH_LUMP.get(), 0.2f)
				.addCriterion("has_zombie_head", hasItem(Items.ZOMBIE_HEAD)).build(consumer);

		DecomposingRecipeBuilder.createRecipe(Items.WITHER_SKELETON_SKULL, defaultDecomposingTime * 3)
				.addIngredient(Items.SKELETON_SKULL).addIngredient(Items.WITHER_ROSE).addIngredient(ModItems.ERODING_BILE.get())
				.addCriterion("has_eroding_bile_and_wither_rose", hasItem(createPredicate(ModItems.ERODING_BILE.get()), createPredicate(Items.WITHER_ROSE)))
				.build(consumer);
	}

	private void registerCookingRecipes(Consumer<IFinishedRecipe> consumer) {
		CookingRecipeBuilder.cookingRecipe(Ingredient.fromItems(ModItems.MENDED_SKIN.get()), Items.LEATHER, 0.1F, 350, IRecipeSerializer.SMOKING)
				.addCriterion("has_skin_chunk", hasItem(ModItems.SKIN_CHUNK.get())).build(consumer, new ResourceLocation(BiomancyMod.MOD_ID, "leather_from_smoking"));
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

		ShapedRecipeBuilder.shapedRecipe(ModItems.DECOMPOSER.get())
				.key('S', ModItems.ARTIFICIAL_STOMACH.get())
				.key('F', ModItems.FLESH_BLOCK.get())
				.key('L', ModItems.FLESH_BLOCK_SLAB.get())
				.patternLine("FLF").patternLine("FSF").patternLine("FLF")
				.addCriterion("has_stomach", hasItem(ModItems.ARTIFICIAL_STOMACH.get())).build(consumer);

		ShapelessRecipeBuilder.shapelessRecipe(ModItems.FLESH_LUMP.get())
				.addIngredient(ModItems.NECROTIC_FLESH.get()).addIngredient(ModItems.REJUVENATING_MUCUS.get())
				.addCriterion("has_necrotic_flesh", hasItem(ModItems.NECROTIC_FLESH.get())).build(consumer);

//		ShapelessRecipeBuilder.shapelessRecipe(ModItems.NECROTIC_FLESH.get(), 9)
//				.addIngredient(ModItems.NECROTIC_FLESH_BLOCK.get())
//				.addCriterion("has_necrotic_flesh_block", hasItem(ModItems.NECROTIC_FLESH_BLOCK.get())).build(consumer);

//		ShapelessRecipeBuilder.shapelessRecipe(ModItems.FLESH_BLOCK.get())
//				.addIngredient(ModItems.FLESH_LUMP.get(), 9)
//				.addCriterion("has_flesh_lump", hasItem(ModItems.FLESH_LUMP.get())).build(consumer);

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
