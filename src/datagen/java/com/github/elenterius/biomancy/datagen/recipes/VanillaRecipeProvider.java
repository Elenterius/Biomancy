package com.github.elenterius.biomancy.datagen.recipes;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.datagen.recipes.builder.WorkbenchRecipeBuilder;
import com.github.elenterius.biomancy.init.ModItems;
import com.github.elenterius.biomancy.init.ModRecipes;
import com.github.elenterius.biomancy.init.tags.ModItemTags;
import com.github.elenterius.biomancy.item.SimpleBlockItem;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.common.Tags;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.function.Consumer;

public class VanillaRecipeProvider extends RecipeProvider {

	protected VanillaRecipeProvider(PackOutput output) {
		super(output);
	}

	protected static String hasName(ItemLike itemLike) {
		return "has_" + getItemName(itemLike);
	}

	protected static String getItemName(ItemLike itemLike) {
		ResourceLocation key = ForgeRegistries.ITEMS.getKey(itemLike.asItem());
		return key != null ? key.getPath() : "unknown";
	}

	@Override
	protected void buildRecipes(Consumer<FinishedRecipe> consumer) {
		buildWorkbenchRecipes(consumer);
		buildCookingRecipes(consumer);
	}

	private void buildCookingRecipes(Consumer<FinishedRecipe> consumer) {
		SimpleCookingRecipeBuilder.blasting(Ingredient.of(ModItems.STONE_POWDER.get()), RecipeCategory.BUILDING_BLOCKS, Items.GLASS_PANE, 0.01f, 100).unlockedBy(hasName(ModItems.STONE_POWDER.get()), has(ModItems.STONE_POWDER.get())).save(consumer, getBlastingRecipeId(Items.GLASS_PANE));
	}

	private void buildWorkbenchRecipes(Consumer<FinishedRecipe> consumer) {

		WorkbenchRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.PRIMORDIAL_CORE.get())
				.pattern("PFB")
				.pattern("#E#")
				.pattern("CFM")
				.define('B', Items.BEEF)
				.define('P', Items.PORKCHOP)
				.define('M', Items.MUTTON)
				.define('C', Items.CHICKEN)
				.define('F', Items.ROTTEN_FLESH)
				.define('E', Items.SPIDER_EYE)
				.define('#', Items.ENDER_PEARL)
				.unlockedBy(hasName(ModItems.PRIMORDIAL_CORE.get()), has(ModItems.PRIMORDIAL_CORE.get()))
				.save(consumer);

		WorkbenchRecipeBuilder.shaped(RecipeCategory.COMBAT, ModItems.DESPOIL_SICKLE.get())
				.define('B', Tags.Items.BONES)
				.define('M', ModItemTags.RAW_MEATS)
				.define('C', ModItems.PRIMORDIAL_CORE.get())
				.pattern("BB ")
				.pattern(" BM")
				.pattern("MC ")
				.unlockedBy(hasName(ModItems.PRIMORDIAL_CORE.get()), has(ModItems.PRIMORDIAL_CORE.get())).save(consumer);

		//		WorkbenchRecipeBuilder.shapeless(ModItems.GUIDE_BOOK.get())
		//				.requires(ModItems.MOB_SINEW.get())
		//				.requires(Items.BOOK)
		//				.requires(ModTags.Items.RAW_MEATS)
		//				.requires(ModItems.PRIMORDIAL_LIVING_OCULUS.get())
		//				.requires(ModItems.MOB_FANG.get())
		//				.unlockedBy(hasName(ModItems.PRIMORDIAL_LIVING_FLESH.get()), has(ModItems.PRIMORDIAL_LIVING_FLESH.get())).save(consumer);

		// machines ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

		WorkbenchRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.PRIMORDIAL_CRADLE.get())
				.define('E', ModItems.PRIMORDIAL_CORE.get())
				.define('M', ModItemTags.RAW_MEATS)
				.define('F', ModItemTags.FANGS)
				.pattern("F F")
				.pattern("MEM")
				.pattern("MMM")
				.unlockedBy(hasName(ModItems.PRIMORDIAL_CORE.get()), has(ModItems.PRIMORDIAL_CORE.get())).save(consumer);

		WorkbenchRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.DECOMPOSER.get())
				.define('M', ModItemTags.RAW_MEATS)
				.define('F', ModItemTags.FANGS)
				.define('G', ModItems.GENERIC_MOB_GLAND.get())
				.define('E', ModItems.LIVING_FLESH.get())
				.pattern("F F")
				.pattern("MGM")
				.pattern("MEM")
				.unlockedBy(hasName(ModItems.LIVING_FLESH.get()), has(ModItems.LIVING_FLESH.get())).save(consumer);

		WorkbenchRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.BIO_FORGE.get())
				.define('S', Items.SLIME_BALL)
				.define('M', ModItemTags.RAW_MEATS)
				.define('C', ModItemTags.CLAWS)
				.define('E', ModItems.LIVING_FLESH.get())
				.pattern("C C")
				.pattern("MSM")
				.pattern("MEM")
				.unlockedBy(hasName(ModItems.LIVING_FLESH.get()), has(ModItems.LIVING_FLESH.get())).save(consumer);

		// fuel ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

		WorkbenchRecipeBuilder.shapeless(RecipeCategory.FOOD, ModItems.NUTRIENT_BAR.get())
				.requires(ModItems.NUTRIENT_PASTE.get(), 9)
				.unlockedBy(hasName(ModItems.NUTRIENT_PASTE.get()), has(ModItems.NUTRIENT_PASTE.get()))
				.save(consumer);

		WorkbenchRecipeBuilder.shapeless(RecipeCategory.FOOD, ModItems.NUTRIENT_PASTE.get(), 9)
				.requires(ModItems.NUTRIENT_BAR.get())
				.unlockedBy(hasName(ModItems.NUTRIENT_PASTE.get()), has(ModItems.NUTRIENT_PASTE.get()))
				.save(consumer, getConversionRecipeId(ModItems.NUTRIENT_PASTE.get(), ModItems.NUTRIENT_BAR.get()));

		// misc ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		WorkbenchRecipeBuilder.shapeless(RecipeCategory.BUILDING_BLOCKS, Items.DIORITE)
				.requires(Items.COBBLESTONE)
				.requires(ModItems.MINERAL_FRAGMENT.get())
				.requires(ModItems.STONE_POWDER.get())
				.unlockedBy(hasName(ModItems.STONE_POWDER.get()), has(ModItems.STONE_POWDER.get()))
				.save(consumer, getConversionRecipeId(Items.DIORITE, ModItems.STONE_POWDER.get()));

		WorkbenchRecipeBuilder.shapeless(RecipeCategory.BUILDING_BLOCKS, Items.GRANITE)
				.requires(Items.DIORITE)
				.requires(ModItems.MINERAL_FRAGMENT.get(), 2)
				.unlockedBy(hasName(ModItems.MINERAL_FRAGMENT.get()), has(ModItems.MINERAL_FRAGMENT.get()))
				.save(consumer, getConversionRecipeId(Items.GRANITE, ModItems.MINERAL_FRAGMENT.get()));

		WorkbenchRecipeBuilder.shapeless(RecipeCategory.BUILDING_BLOCKS, Items.RED_SAND)
				.requires(Items.SAND)
				.requires(ModItems.MINERAL_FRAGMENT.get(), 2)
				.unlockedBy(hasName(ModItems.MINERAL_FRAGMENT.get()), has(ModItems.MINERAL_FRAGMENT.get()))
				.save(consumer, getConversionRecipeId(Items.RED_SAND, ModItems.MINERAL_FRAGMENT.get()));

		WorkbenchRecipeBuilder.shaped(RecipeCategory.MISC, Items.DIRT)
				.define('P', ModItems.ORGANIC_MATTER.get())
				.define('L', ModItems.STONE_POWDER.get())
				.pattern("LPL")
				.pattern("PLP")
				.pattern("LPL")
				.unlockedBy(hasName(ModItems.ORGANIC_MATTER.get()), has(ModItems.ORGANIC_MATTER.get()))
				.save(consumer, getConversionRecipeId(Items.DIRT, ModItems.STONE_POWDER.get()));

		WorkbenchRecipeBuilder.shaped(RecipeCategory.MISC, Items.SAND)
				.define('M', ModItems.MINERAL_FRAGMENT.get())
				.define('L', ModItems.STONE_POWDER.get())
				.pattern("LLL")
				.pattern("LML")
				.pattern("LLL")
				.unlockedBy(hasName(ModItems.STONE_POWDER.get()), has(ModItems.STONE_POWDER.get()))
				.save(consumer, getConversionRecipeId(Items.SAND, ModItems.STONE_POWDER.get()));

		WorkbenchRecipeBuilder.shapeless(RecipeCategory.MISC, Items.CLAY_BALL)
				.requires(Items.WATER_BUCKET)
				.requires(ModItems.STONE_POWDER.get(), 8)
				.unlockedBy(hasName(ModItems.STONE_POWDER.get()), has(ModItems.STONE_POWDER.get()))
				.save(consumer, getConversionRecipeId(Items.CLAY_BALL, ModItems.STONE_POWDER.get()));

		WorkbenchRecipeBuilder.shapeless(RecipeCategory.MISC, Items.GUNPOWDER)
				.requires(Items.CHARCOAL)
				.requires(ModItems.UNSTABLE_COMPOUND.get())
				.requires(Items.BLAZE_POWDER)
				.unlockedBy(hasName(ModItems.UNSTABLE_COMPOUND.get()), has(ModItems.UNSTABLE_COMPOUND.get()))
				.save(consumer, getConversionRecipeId(Items.GUNPOWDER, ModItems.UNSTABLE_COMPOUND.get()));

		WorkbenchRecipeBuilder.shaped(RecipeCategory.DECORATIONS, Items.GLOW_ITEM_FRAME)
				.define('F', Items.ITEM_FRAME)
				.define('L', ModItems.BIO_LUMENS.get())
				.pattern(" L ")
				.pattern("LFL")
				.pattern(" L ")
				.unlockedBy(hasName(ModItems.BIO_LUMENS.get()), has(ModItems.BIO_LUMENS.get()))
				.save(consumer, getSimpleRecipeId(Items.GLOW_ITEM_FRAME));

		// A recipe for converting between two versions of Flesh Door.
		WorkbenchRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.FLESH_DOOR.get())
				.requires(ModItems.FULL_FLESH_DOOR.get())
				.unlockedBy(hasName(ModItems.FULL_FLESH_DOOR.get()), has(ModItems.FULL_FLESH_DOOR.get()))
				.save(consumer, getConversionRecipeId(ModItems.FLESH_DOOR.get(), ModItems.FULL_FLESH_DOOR.get()));

		WorkbenchRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.FULL_FLESH_DOOR.get())
				.requires(ModItems.FLESH_DOOR.get())
				.unlockedBy(hasName(ModItems.FLESH_DOOR.get()), has(ModItems.FLESH_DOOR.get()))
				.save(consumer, getConversionRecipeId(ModItems.FULL_FLESH_DOOR.get(), ModItems.FLESH_DOOR.get()));

		stairs(consumer, ModItems.FLESH_STAIRS.get(), ModItems.FLESH_BLOCK.get());
		slab(consumer, ModItems.FLESH_SLAB.get(), ModItems.FLESH_BLOCK.get());
		wall(consumer, ModItems.FLESH_WALL.get(), ModItems.FLESH_BLOCK.get());
		stonecutting(consumer, ModItems.FLESH_STAIRS.get(), ModItems.FLESH_BLOCK.get());
		stonecutting(consumer, ModItems.FLESH_SLAB.get(), ModItems.FLESH_BLOCK.get(), 2);
		stonecutting(consumer, ModItems.FLESH_WALL.get(), ModItems.FLESH_BLOCK.get());

		stairs(consumer, ModItems.PACKED_FLESH_STAIRS.get(), ModItems.PACKED_FLESH_BLOCK.get());
		slab(consumer, ModItems.PACKED_FLESH_SLAB.get(), ModItems.PACKED_FLESH_BLOCK.get());
		wall(consumer, ModItems.PACKED_FLESH_WALL.get(), ModItems.PACKED_FLESH_BLOCK.get());
		stonecutting(consumer, ModItems.PACKED_FLESH_STAIRS.get(), ModItems.PACKED_FLESH_BLOCK.get());
		stonecutting(consumer, ModItems.PACKED_FLESH_SLAB.get(), ModItems.PACKED_FLESH_BLOCK.get(), 2);
		stonecutting(consumer, ModItems.PACKED_FLESH_WALL.get(), ModItems.PACKED_FLESH_BLOCK.get());

		stairs(consumer, ModItems.FIBROUS_FLESH_STAIRS.get(), ModItems.FIBROUS_FLESH_BLOCK.get());
		slab(consumer, ModItems.FIBROUS_FLESH_SLAB.get(), ModItems.FIBROUS_FLESH_BLOCK.get());
		wall(consumer, ModItems.FIBROUS_FLESH_WALL.get(), ModItems.FIBROUS_FLESH_BLOCK.get());
		stonecutting(consumer, ModItems.FIBROUS_FLESH_STAIRS.get(), ModItems.FIBROUS_FLESH_BLOCK.get());
		stonecutting(consumer, ModItems.FIBROUS_FLESH_SLAB.get(), ModItems.FIBROUS_FLESH_BLOCK.get(), 2);
		stonecutting(consumer, ModItems.FIBROUS_FLESH_WALL.get(), ModItems.FIBROUS_FLESH_BLOCK.get());

		slab(consumer, ModItems.ORNATE_FLESH_SLAB.get(), ModItems.ORNATE_FLESH_BLOCK.get());
		blockFromSlabs(consumer, ModItems.ORNATE_FLESH_BLOCK.get(), ModItems.ORNATE_FLESH_SLAB.get());
		stonecutting(consumer, ModItems.ORNATE_FLESH_SLAB.get(), ModItems.ORNATE_FLESH_BLOCK.get(), 2);

		stairs(consumer, ModItems.PRIMAL_FLESH_STAIRS.get(), ModItems.PRIMAL_FLESH_BLOCK.get());
		slab(consumer, ModItems.PRIMAL_FLESH_SLAB.get(), ModItems.PRIMAL_FLESH_BLOCK.get());
		blockFromSlabs(consumer, ModItems.PRIMAL_FLESH_BLOCK.get(), ModItems.PRIMAL_FLESH_SLAB.get());
		wall(consumer, ModItems.PRIMAL_FLESH_WALL.get(), ModItems.PRIMAL_FLESH_BLOCK.get());
		stonecutting(consumer, ModItems.PRIMAL_FLESH_WALL.get(), ModItems.PRIMAL_FLESH_BLOCK.get());
		stonecutting(consumer, ModItems.PRIMAL_FLESH_STAIRS.get(), ModItems.PRIMAL_FLESH_BLOCK.get());
		stonecutting(consumer, ModItems.PRIMAL_FLESH_SLAB.get(), ModItems.PRIMAL_FLESH_BLOCK.get(), 2);

		polished(consumer, ModItems.SMOOTH_PRIMAL_FLESH_BLOCK.get(), ModItems.PRIMAL_FLESH_BLOCK.get());
		stairs(consumer, ModItems.SMOOTH_PRIMAL_FLESH_STAIRS.get(), ModItems.SMOOTH_PRIMAL_FLESH_BLOCK.get());
		slab(consumer, ModItems.SMOOTH_PRIMAL_FLESH_SLAB.get(), ModItems.SMOOTH_PRIMAL_FLESH_BLOCK.get());
		blockFromSlabs(consumer, ModItems.SMOOTH_PRIMAL_FLESH_BLOCK.get(), ModItems.SMOOTH_PRIMAL_FLESH_SLAB.get());
		wall(consumer, ModItems.SMOOTH_PRIMAL_FLESH_WALL.get(), ModItems.SMOOTH_PRIMAL_FLESH_BLOCK.get());
		stonecutting(consumer, ModItems.SMOOTH_PRIMAL_FLESH_BLOCK.get(), ModItems.PRIMAL_FLESH_BLOCK.get());
		stonecutting(consumer, ModItems.SMOOTH_PRIMAL_FLESH_SLAB.get(), ModItems.SMOOTH_PRIMAL_FLESH_BLOCK.get(), 2);
		stonecutting(consumer, ModItems.SMOOTH_PRIMAL_FLESH_STAIRS.get(), ModItems.SMOOTH_PRIMAL_FLESH_BLOCK.get());
		stonecutting(consumer, ModItems.SMOOTH_PRIMAL_FLESH_WALL.get(), ModItems.SMOOTH_PRIMAL_FLESH_BLOCK.get());

		WorkbenchRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, ModItems.POROUS_PRIMAL_FLESH_BLOCK.get(), 4)
				.define('M', ModItems.MALIGNANT_FLESH_BLOCK.get())
				.define('P', ModItems.PRIMAL_FLESH_BLOCK.get())
				.pattern("PM")
				.pattern("MP")
				.unlockedBy(hasName(ModItems.PRIMAL_FLESH_BLOCK.get()), has(ModItems.PRIMAL_FLESH_BLOCK.get()))
				.save(consumer);
		stairs(consumer, ModItems.POROUS_PRIMAL_FLESH_STAIRS.get(), ModItems.POROUS_PRIMAL_FLESH_BLOCK.get());
		slab(consumer, ModItems.POROUS_PRIMAL_FLESH_SLAB.get(), ModItems.POROUS_PRIMAL_FLESH_BLOCK.get());
		blockFromSlabs(consumer, ModItems.POROUS_PRIMAL_FLESH_BLOCK.get(), ModItems.POROUS_PRIMAL_FLESH_SLAB.get());
		wall(consumer, ModItems.POROUS_PRIMAL_FLESH_WALL.get(), ModItems.POROUS_PRIMAL_FLESH_BLOCK.get());
		stonecutting(consumer, ModItems.POROUS_PRIMAL_FLESH_WALL.get(), ModItems.POROUS_PRIMAL_FLESH_BLOCK.get());
		stonecutting(consumer, ModItems.POROUS_PRIMAL_FLESH_STAIRS.get(), ModItems.POROUS_PRIMAL_FLESH_BLOCK.get());
		stonecutting(consumer, ModItems.POROUS_PRIMAL_FLESH_SLAB.get(), ModItems.POROUS_PRIMAL_FLESH_BLOCK.get(), 2);

		stairs(consumer, ModItems.MALIGNANT_FLESH_STAIRS.get(), ModItems.MALIGNANT_FLESH_BLOCK.get());
		slab(consumer, ModItems.MALIGNANT_FLESH_SLAB.get(), ModItems.MALIGNANT_FLESH_BLOCK.get());
		blockFromSlabs(consumer, ModItems.MALIGNANT_FLESH_BLOCK.get(), ModItems.MALIGNANT_FLESH_SLAB.get());
		wall(consumer, ModItems.MALIGNANT_FLESH_WALL.get(), ModItems.MALIGNANT_FLESH_BLOCK.get());
		stonecutting(consumer, ModItems.MALIGNANT_FLESH_WALL.get(), ModItems.MALIGNANT_FLESH_BLOCK.get());
		stonecutting(consumer, ModItems.MALIGNANT_FLESH_STAIRS.get(), ModItems.MALIGNANT_FLESH_BLOCK.get());
		stonecutting(consumer, ModItems.MALIGNANT_FLESH_SLAB.get(), ModItems.MALIGNANT_FLESH_BLOCK.get(), 2);

		WorkbenchRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.MALIGNANT_FLESH_BLOCK.get())
				.define('F', ModItems.FLESH_BITS.get())
				.define('V', ModItems.MALIGNANT_FLESH_VEINS.get())
				.pattern("VVV")
				.pattern("VFV")
				.pattern("VVV")
				.unlockedBy(hasName(ModItems.MALIGNANT_FLESH_VEINS.get()), has(ModItems.MALIGNANT_FLESH_VEINS.get()))
				.save(consumer);

		WorkbenchRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.PRIMAL_FLESH_BLOCK.get())
				.define('F', ModItems.FLESH_BITS.get())
				.define('V', ModItems.MALIGNANT_FLESH_BLOCK.get())
				.pattern("VVV")
				.pattern("VFV")
				.pattern("VVV")
				.unlockedBy(hasName(ModItems.MALIGNANT_FLESH_BLOCK.get()), has(ModItems.MALIGNANT_FLESH_BLOCK.get()))
				.save(consumer);

		WorkbenchRecipeBuilder.shaped(RecipeCategory.DECORATIONS, ModItems.PRIMORDIAL_BIO_LANTERN.get())
				.define('B', ModItems.BLOOMBERRY.get())
				.define('V', ModItems.MALIGNANT_FLESH_VEINS.get())
				.define('C', ModItems.TENDON_CHAIN.get())
				.pattern(" C ")
				.pattern("VBV")
				.pattern(" V ")
				.unlockedBy(hasName(ModItems.BLOOMBERRY.get()), has(ModItems.BLOOMBERRY.get()))
				.save(consumer);

		WorkbenchRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.BLOOMLIGHT.get(), 4)
				.define('B', ModItems.BLOOMBERRY.get())
				.define('V', ModItems.MALIGNANT_FLESH_VEINS.get())
				.pattern("BVB")
				.pattern("VBV")
				.pattern("BVB")
				.unlockedBy(hasName(ModItems.BLOOMBERRY.get()), has(ModItems.BLOOMBERRY.get()))
				.save(consumer);

		membrane(consumer, ModItems.IMPERMEABLE_MEMBRANE_PANE.get(), ModItems.IMPERMEABLE_MEMBRANE.get());
		membrane(consumer, ModItems.BABY_PERMEABLE_MEMBRANE_PANE.get(), ModItems.BABY_PERMEABLE_MEMBRANE.get());
		membrane(consumer, ModItems.ADULT_PERMEABLE_MEMBRANE_PANE.get(), ModItems.ADULT_PERMEABLE_MEMBRANE.get());
		membrane(consumer, ModItems.PRIMAL_PERMEABLE_MEMBRANE_PANE.get(), ModItems.PRIMAL_PERMEABLE_MEMBRANE.get());
		membrane(consumer, ModItems.UNDEAD_PERMEABLE_MEMBRANE_PANE.get(), ModItems.UNDEAD_PERMEABLE_MEMBRANE.get());

		SpecialRecipeBuilder.special(ModRecipes.BIOMETRIC_MEMBRANE_CRAFTING_SERIALIZER.get())
				.save(consumer, getSimpleRecipeId(ModItems.BIOMETRIC_MEMBRANE.get()).toString());

		SpecialRecipeBuilder.special(ModRecipes.CRADLE_CLEANSING_SERIALIZER.get())
				.save(consumer, BiomancyMod.createRLString(ModItems.PRIMORDIAL_CRADLE.getId().toLanguageKey() + "_cleansing"));
	}

	private void membrane(Consumer<FinishedRecipe> consumer, SimpleBlockItem pane, SimpleBlockItem membrane) {
		WorkbenchRecipeBuilder.shapeless(RecipeCategory.MISC, pane, 2)
				.requires(membrane)
				.unlockedBy(hasName(membrane), has(membrane))
				.save(consumer, getConversionRecipeId(pane, membrane));

		WorkbenchRecipeBuilder.shaped(RecipeCategory.MISC, membrane)
				.define('P', pane)
				.pattern("P")
				.pattern("P")
				.unlockedBy(hasName(pane), has(pane))
				.save(consumer, getConversionRecipeId(membrane, pane));
	}

	protected ResourceLocation getSimpleRecipeId(ItemLike itemLike) {
		return BiomancyMod.createRL(getItemName(itemLike));
	}

	protected ResourceLocation getConversionRecipeId(ItemLike result, ItemLike ingredient) {
		return BiomancyMod.createRL(getItemName(result) + "_from_" + getItemName(ingredient));
	}

	protected ResourceLocation getStoneCuttingRecipeId(ItemLike result, ItemLike ingredient) {
		return BiomancyMod.createRL(getItemName(result) + "_from_" + getItemName(ingredient) + "_stonecutting");
	}

	protected ResourceLocation getSmeltingRecipeId(ItemLike itemLike) {
		return BiomancyMod.createRL(getItemName(itemLike) + "_from_smelting");
	}

	protected ResourceLocation getBlastingRecipeId(ItemLike itemLike) {
		return BiomancyMod.createRL(getItemName(itemLike) + "_from_blasting");
	}

	protected void polished(Consumer<FinishedRecipe> consumer, ItemLike result, ItemLike ingredient) {
		polished(consumer, RecipeCategory.BUILDING_BLOCKS, result, ingredient);
	}

	protected void slab(Consumer<FinishedRecipe> consumer, BlockItem result, BlockItem ingredient) {
		slab(consumer, RecipeCategory.BUILDING_BLOCKS, result, ingredient);
	}

	protected void wall(Consumer<FinishedRecipe> consumer, BlockItem result, BlockItem ingredient) {
		wall(consumer, RecipeCategory.BUILDING_BLOCKS, result, ingredient);
	}

	protected void stonecutting(Consumer<FinishedRecipe> consumer, ItemLike result, ItemLike ingredient) {
		stonecutting(consumer, result, ingredient, 1);
	}

	protected void stonecutting(Consumer<FinishedRecipe> consumer, ItemLike result, ItemLike ingredient, int count) {
		SingleItemRecipeBuilder builder = SingleItemRecipeBuilder.stonecutting(Ingredient.of(ingredient), RecipeCategory.BUILDING_BLOCKS, result, count).unlockedBy(getHasName(ingredient), has(ingredient));
		ResourceLocation recipeName = getStoneCuttingRecipeId(result, ingredient);
		builder.save(consumer, recipeName);
	}

	protected void stairs(Consumer<FinishedRecipe> consumer, ItemLike result, ItemLike ingredient) {
		stairBuilder(result, Ingredient.of(ingredient)).unlockedBy(hasName(ingredient), has(ingredient)).save(consumer);
	}

	protected void blockFromSlabs(Consumer<FinishedRecipe> consumer, Item result, Item slab) {
		WorkbenchRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, result)
				.define('S', slab)
				.pattern(" S ")
				.pattern(" S ")
				.unlockedBy(hasName(slab), has(slab))
				.save(consumer, BiomancyMod.createRL(getItemName(result) + "_from_slabs"));
	}

}
