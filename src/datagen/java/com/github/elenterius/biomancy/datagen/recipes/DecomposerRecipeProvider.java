package com.github.elenterius.biomancy.datagen.recipes;

import com.github.alexmodguy.alexscaves.AlexsCaves;
import com.github.alexmodguy.alexscaves.server.block.ACBlockRegistry;
import com.github.alexmodguy.alexscaves.server.item.ACItemRegistry;
import com.github.alexthe666.alexsmobs.AlexsMobs;
import com.github.alexthe666.alexsmobs.block.AMBlockRegistry;
import com.github.alexthe666.alexsmobs.item.AMItemRegistry;
import com.github.elenterius.biomancy.datagen.recipes.builder.DatagenIngredient;
import com.github.elenterius.biomancy.datagen.recipes.builder.DecomposerRecipeBuilder;
import com.github.elenterius.biomancy.init.ModItems;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.common.Tags;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Arrays;
import java.util.function.Consumer;

public class DecomposerRecipeProvider extends RecipeProvider {

	protected DecomposerRecipeProvider(PackOutput packOutput) {
		super(packOutput);
	}

	protected static ItemPredicate createPredicate(ItemLike item) {
		return ItemPredicate.Builder.item().of(item).build();
	}

	protected static ItemPredicate createPredicate(TagKey<Item> tag) {
		return ItemPredicate.Builder.item().of(tag).build();
	}

	protected static InventoryChangeTrigger.TriggerInstance hasItems(ItemLike... itemProviders) {
		ItemPredicate[] predicates = Arrays.stream(itemProviders).map(DecomposerRecipeProvider::createPredicate).toArray(ItemPredicate[]::new);
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

	@Override
	protected void buildRecipes(Consumer<FinishedRecipe> consumer) {
		buildBaseRecipes(consumer);
		build119Recipes(consumer);
		build120Recipes(consumer);
		buildSpecialRecipes(consumer);
		buildBiomesOPlentyRecipes(consumer);
		buildAlexsMobsRecipes(consumer);
		buildAlexsCavesRecipes(consumer);
		buildTetraRecipes(consumer);
	}

	private void buildBaseRecipes(Consumer<FinishedRecipe> consumer) {
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
		DecomposerRecipeBuilder.create().setIngredient(Items.KELP, 2).addOutput(ModItems.ORGANIC_MATTER.get(), 1, 2).unlockedBy(Items.KELP).save(consumer);
		DecomposerRecipeBuilder.create().setIngredient(Items.BAMBOO).addOutput(ModItems.ORGANIC_MATTER.get(), 1, 2).unlockedBy(Items.BAMBOO).save(consumer);
		DecomposerRecipeBuilder.create().setIngredient(Items.CHORUS_FLOWER).addOutput(ModItems.MINERAL_FRAGMENT.get(), 3, 5).addOutput(ModItems.EXOTIC_DUST.get(), 2, 4).addOutput(ModItems.ORGANIC_MATTER.get(), 2, 5).unlockedBy(Items.CHORUS_FLOWER).save(consumer);
		DecomposerRecipeBuilder.create().setIngredient(Items.CLAY).addOutput(ModItems.STONE_POWDER.get(), 1, 2).addOutput(ModItems.MINERAL_FRAGMENT.get(), 1, 2).unlockedBy(Items.CLAY).save(consumer);
		DecomposerRecipeBuilder.create().setIngredient(Items.GLOWSTONE).addOutput(ModItems.STONE_POWDER.get(), 2, 4).addOutput(ModItems.EXOTIC_DUST.get(), 1, 4).addOutput(ModItems.BIO_LUMENS.get(), -4, 4).unlockedBy(Items.GLOWSTONE).save(consumer);
		DecomposerRecipeBuilder.create().setIngredient(Items.GLOW_LICHEN).addOutput(ModItems.BIO_LUMENS.get(), 1, 2).addOutput(ModItems.ORGANIC_MATTER.get(), 0, 1).unlockedBy(Items.GLOW_LICHEN).save(consumer);
		DecomposerRecipeBuilder.create().setIngredient(Items.DRAGON_EGG).addOutput(ModItems.EXOTIC_DUST.get(), 97, 128).addOutput(ModItems.BIO_LUMENS.get(), 6, 10).addOutput(ModItems.HORMONE_SECRETION.get(), 17, 23).unlockedBy(Items.DRAGON_EGG).save(consumer);
		DecomposerRecipeBuilder.create().setIngredient(Items.TURTLE_EGG).addOutput(ModItems.HORMONE_SECRETION.get(), 1, 2).addOutput(ModItems.ORGANIC_MATTER.get(), 0, 1).unlockedBy(Items.TURTLE_EGG).save(consumer);
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
		DecomposerRecipeBuilder.create().setIngredient(Items.GOLDEN_APPLE).addOutput(ModItems.MINERAL_FRAGMENT.get(), 37, 63).addOutput(ModItems.ORGANIC_MATTER.get(), 4, 6).addOutput(ModItems.REGENERATIVE_FLUID.get(), 3, 6).unlockedBy(Items.GOLDEN_APPLE).save(consumer);
		DecomposerRecipeBuilder.create().setIngredient(Items.ENCHANTED_GOLDEN_APPLE).addOutput(ModItems.MINERAL_FRAGMENT.get(), 43, 72).addOutput(ModItems.REGENERATIVE_FLUID.get(), 12, 20).addOutput(ModItems.EXOTIC_DUST.get(), 6, 10).unlockedBy(Items.ENCHANTED_GOLDEN_APPLE).save(consumer);
		DecomposerRecipeBuilder.create().setIngredient(Tags.Items.LEATHER).addOutput(ModItems.TOUGH_FIBERS.get(), 1, 4).unlockedBy(Tags.Items.LEATHER).save(consumer);
		DecomposerRecipeBuilder.create().setIngredient(Items.CLAY_BALL).addOutput(ModItems.STONE_POWDER.get(), 1, 2).addOutput(ModItems.MINERAL_FRAGMENT.get(), 0, 1).unlockedBy(Items.CLAY_BALL).save(consumer);
		DecomposerRecipeBuilder.create().setIngredient(Items.DRIED_KELP_BLOCK).addOutput(ModItems.ORGANIC_MATTER.get(), 1, 3).unlockedBy(Items.DRIED_KELP_BLOCK).save(consumer);
		DecomposerRecipeBuilder.create().setIngredient(Items.SLIME_BALL).addOutput(ModItems.REGENERATIVE_FLUID.get(), 3, 5).addOutput(ModItems.BILE.get(), 1, 2).unlockedBy(Items.SLIME_BALL).save(consumer);
		DecomposerRecipeBuilder.create().setIngredient(Tags.Items.EGGS).addOutput(ModItems.HORMONE_SECRETION.get(), 0, 1).addOutput(ModItems.ORGANIC_MATTER.get(), 0, 2).unlockedBy(Tags.Items.EGGS).save(consumer);
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
		DecomposerRecipeBuilder.create().setIngredient(Items.GHAST_TEAR).addOutput(ModItems.HORMONE_SECRETION.get(), 4, 8).addOutput(ModItems.BILE.get(), 1, 2).addOutput(ModItems.EXOTIC_DUST.get(), 1, 2).unlockedBy(Items.GHAST_TEAR).save(consumer);
		DecomposerRecipeBuilder.create().setIngredient(Items.GOLD_NUGGET).addOutput(ModItems.MINERAL_FRAGMENT.get(), 0, 1).unlockedBy(Items.GOLD_NUGGET).save(consumer);
		DecomposerRecipeBuilder.create().setIngredient(Items.NETHER_WART).addOutput(ModItems.ORGANIC_MATTER.get(), 1, 2).addOutput(ModItems.EXOTIC_DUST.get(), 0, 1).unlockedBy(Items.NETHER_WART).save(consumer);
		DecomposerRecipeBuilder.create().setIngredient(Items.SPIDER_EYE).addOutput(ModItems.BILE.get(), 0, 1).addOutput(ModItems.FLESH_BITS.get(), 1).addOutput(ModItems.ELASTIC_FIBERS.get(), 1, 2).unlockedBy(Items.SPIDER_EYE).save(consumer);
		DecomposerRecipeBuilder.create().setIngredient(Items.FERMENTED_SPIDER_EYE).addOutput(ModItems.FLESH_BITS.get(), 1).addOutput(ModItems.ELASTIC_FIBERS.get(), 1, 2).unlockedBy(Items.FERMENTED_SPIDER_EYE).save(consumer);
		DecomposerRecipeBuilder.create().setIngredient(Items.MAGMA_CREAM).addOutput(ModItems.BILE.get(), 1, 3).addOutput(ModItems.BIO_LUMENS.get(), 1, 3).addOutput(ModItems.VOLATILE_FLUID.get(), 0, 2).unlockedBy(Items.MAGMA_CREAM).save(consumer);
		DecomposerRecipeBuilder.create().setIngredient(Items.ENDER_EYE).addOutput(ModItems.EXOTIC_DUST.get(), 5, 6).unlockedBy(Items.ENDER_EYE).save(consumer);
		DecomposerRecipeBuilder.create().setIngredient(Items.GLISTERING_MELON_SLICE).addOutput(ModItems.MINERAL_FRAGMENT.get(), 3, 6).addOutput(ModItems.ORGANIC_MATTER.get(), 2).unlockedBy(Items.GLISTERING_MELON_SLICE).save(consumer);
		DecomposerRecipeBuilder.create().setIngredient(Items.CARROT).addOutput(ModItems.ORGANIC_MATTER.get(), 1, 2).unlockedBy(Items.CARROT).save(consumer);
		DecomposerRecipeBuilder.create().setIngredient(Items.POTATO).addOutput(ModItems.ORGANIC_MATTER.get(), 1, 2).unlockedBy(Items.POTATO).save(consumer);
		DecomposerRecipeBuilder.create().setIngredient(Items.BAKED_POTATO).addOutput(ModItems.ORGANIC_MATTER.get(), 2, 5).unlockedBy(Items.BAKED_POTATO).save(consumer);
		DecomposerRecipeBuilder.create().setIngredient(Items.POISONOUS_POTATO).addOutput(ModItems.TOXIN_EXTRACT.get(), 2, 4).addOutput(ModItems.ORGANIC_MATTER.get(), 1, 3).unlockedBy(Items.POISONOUS_POTATO).save(consumer);
		DecomposerRecipeBuilder.create().setIngredient(Items.GOLDEN_CARROT).addOutput(ModItems.MINERAL_FRAGMENT.get(), 4, 8).addOutput(ModItems.ORGANIC_MATTER.get(), 2, 4).unlockedBy(Items.GOLDEN_CARROT).save(consumer);
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
		DecomposerRecipeBuilder.create().setIngredient(ModItems.MOB_MARROW).addOutput(ModItems.HORMONE_SECRETION.get(), 1, 4).addOutput(ModItems.BONE_FRAGMENTS.get(), 2, 4).addOutput(ModItems.MINERAL_FRAGMENT.get(), 1, 2).unlockedBy(ModItems.MOB_MARROW).save(consumer);
		DecomposerRecipeBuilder.create().setIngredient(ModItems.WITHERED_MOB_MARROW).addOutput(ModItems.WITHERING_OOZE.get(), 3, 5).addOutput(ModItems.BONE_FRAGMENTS.get(), 2, 4).unlockedBy(ModItems.WITHERED_MOB_MARROW).save(consumer);
		DecomposerRecipeBuilder.create().setIngredient(ModItems.GENERIC_MOB_GLAND).addOutput(ModItems.BILE.get(), 4, 6).addOutput(ModItems.FLESH_BITS.get(), 2, 3).addOutput(ModItems.ELASTIC_FIBERS.get(), 1, 3).unlockedBy(ModItems.GENERIC_MOB_GLAND).save(consumer);
		DecomposerRecipeBuilder.create().setIngredient(ModItems.TOXIN_GLAND).addOutput(ModItems.TOXIN_EXTRACT.get(), 2, 5).addOutput(ModItems.FLESH_BITS.get(), 2, 3).addOutput(ModItems.ELASTIC_FIBERS.get(), 1, 3).unlockedBy(ModItems.TOXIN_GLAND).save(consumer);
		DecomposerRecipeBuilder.create().setIngredient(ModItems.VOLATILE_GLAND).addOutput(ModItems.VOLATILE_FLUID.get(), 2, 5).addOutput(ModItems.FLESH_BITS.get(), 2, 3).addOutput(ModItems.ELASTIC_FIBERS.get(), 1, 3).unlockedBy(ModItems.VOLATILE_GLAND).save(consumer);
	}

	private void build119Recipes(Consumer<FinishedRecipe> consumer) {
		DecomposerRecipeBuilder.create().setIngredient(Items.ECHO_SHARD).addOutput(ModItems.EXOTIC_DUST.get(), 8, 12).unlockedBy(Items.ECHO_SHARD).save(consumer);
		DecomposerRecipeBuilder.create().setIngredient(Items.GOAT_HORN).addOutput(ModItems.MINERAL_FRAGMENT.get(), 5, 7).addOutput(ModItems.TOUGH_FIBERS.get(), 6, 8).unlockedBy(Items.GOAT_HORN).save(consumer);
		DecomposerRecipeBuilder.create().setIngredient(Items.PEARLESCENT_FROGLIGHT).addOutput(ModItems.BIO_LUMENS.get(), 5, 9).addOutput(ModItems.BILE.get(), 2, 3).unlockedBy(Items.PEARLESCENT_FROGLIGHT).save(consumer);
		DecomposerRecipeBuilder.create().setIngredient(Items.VERDANT_FROGLIGHT).addOutput(ModItems.BIO_LUMENS.get(), 5, 9).addOutput(ModItems.BILE.get(), 2, 3).unlockedBy(Items.VERDANT_FROGLIGHT).save(consumer);
		DecomposerRecipeBuilder.create().setIngredient(Items.OCHRE_FROGLIGHT).addOutput(ModItems.BIO_LUMENS.get(), 5, 9).addOutput(ModItems.BILE.get(), 2, 3).unlockedBy(Items.OCHRE_FROGLIGHT).save(consumer);
		DecomposerRecipeBuilder.create().setIngredient(Items.FROGSPAWN).addOutput(ModItems.BILE.get(), 0, 1).unlockedBy(Items.FROGSPAWN).save(consumer);
	}

	private void build120Recipes(Consumer<FinishedRecipe> consumer) {
		DecomposerRecipeBuilder.create().setIngredient(Items.PITCHER_POD)
				.addOutput(ModItems.BILE.get(), 1, 3)
				.addOutput(ModItems.EXOTIC_DUST.get(), 0, 3)
				.addOutput(ModItems.ORGANIC_MATTER.get(), 2, 4)
				.unlockedBy(Items.PITCHER_POD).save(consumer);

		DecomposerRecipeBuilder.create().setIngredient(Items.PITCHER_PLANT)
				.addOutput(ModItems.EXOTIC_DUST.get(), 0, 3)
				.addOutput(ModItems.TOXIN_EXTRACT.get(), 0, 2)
				.addOutput(ModItems.ORGANIC_MATTER.get(), 2, 4)
				.unlockedBy(Items.PITCHER_PLANT).save(consumer);

		DecomposerRecipeBuilder.create().setIngredient(Items.TORCHFLOWER)
				.addOutput(ModItems.BIO_LUMENS.get(), 0, 2)
				.addOutput(ModItems.ORGANIC_MATTER.get(), 1, 2)
				.unlockedBy(Items.TORCHFLOWER).save(consumer);

		DecomposerRecipeBuilder.create().setIngredient(Items.TORCHFLOWER_SEEDS)
				.addOutput(ModItems.ORGANIC_MATTER.get(), 1, 2)
				.unlockedBy(Items.TORCHFLOWER_SEEDS).save(consumer);

		DecomposerRecipeBuilder.create().setIngredient(Items.SNIFFER_EGG)
				.addOutput(ModItems.STONE_POWDER.get(), 0, 4)
				.addOutput(ModItems.HORMONE_SECRETION.get(), 1, 4)
				.addOutput(ModItems.ORGANIC_MATTER.get(), 1, 4)
				.addOutput(ModItems.EXOTIC_DUST.get(), 2, 6)
				.unlockedBy(Items.SNIFFER_EGG).save(consumer);

		DecomposerRecipeBuilder.create().setIngredient(Items.PIGLIN_HEAD)
				.addOutput(ModItems.FLESH_BITS.get(), 22, 36)
				.addOutput(ModItems.ELASTIC_FIBERS.get(), 8, 12)
				.addOutput(Items.SKELETON_SKULL, 1)
				.unlockedBy(Items.PIGLIN_HEAD).save(consumer);
	}

	private void buildSpecialRecipes(Consumer<FinishedRecipe> consumer) {
		DecomposerRecipeBuilder.create().setIngredient(ModItems.LIVING_FLESH)
				.addOutput(ModItems.FLESH_BITS.get(), 3, 6)
				.addOutput(ModItems.EXOTIC_DUST.get(), 0, 2)
				.unlockedBy(ModItems.LIVING_FLESH).save(consumer);

		DecomposerRecipeBuilder.create().setIngredient(ModItems.BLOOMBERRY)
				.addOutput(ModItems.BIO_LUMENS.get(), 0, 2)
				.addOutput(ModItems.ORGANIC_MATTER.get(), 0, 1)
				.addOutput(ModItems.EXOTIC_DUST.get(), 0, 3)
				.addOutput(ModItems.BILE.get(), 1, 2)
				.unlockedBy(ModItems.BLOOMBERRY).save(consumer);

		DecomposerRecipeBuilder.create().setIngredient(ModItems.BLOOMLIGHT)
				.addOutput(ModItems.BIO_LUMENS.get(), 5, 9)
				.addOutput(ModItems.ORGANIC_MATTER.get(), 2, 3)
				.addOutput(ModItems.EXOTIC_DUST.get(), 0, 4)
				.addOutput(ModItems.BILE.get(), 1, 2)
				.unlockedBy(Items.SHROOMLIGHT).save(consumer);

		DecomposerRecipeBuilder.create().setIngredient(ModItems.NUTRIENT_PASTE)
				.addOutput(ModItems.NUTRIENTS.get(), 5)
				.addOutput(ModItems.ORGANIC_MATTER.get(), 0, 1)
				.unlockedBy(Items.SHROOMLIGHT).save(consumer);
	}

	private void buildBiomesOPlentyRecipes(Consumer<FinishedRecipe> consumer) {
		DecomposerRecipeBuilder.create().ifModLoaded("biomesoplenty")
				.setIngredient(new DatagenIngredient("biomesoplenty:flesh_tendons"))
				.addOutput(ModItems.ELASTIC_FIBERS.get(), 4, 8)
				.addOutput(ModItems.FLESH_BITS.get(), 1, 2)
				.unlockedBy(ModItems.MOB_SINEW).save(consumer);
	}

	private void buildTetraRecipes(Consumer<FinishedRecipe> consumer) {
		DecomposerRecipeBuilder.create().ifModLoaded("tetra")
				.setIngredient(new DatagenIngredient("tetra:dragon_sinew"))
				.addOutput(ModItems.ELASTIC_FIBERS.get(), 4, 8)
				.addOutput(ModItems.TOUGH_FIBERS.get(), 1, 2)
				.addOutput(ModItems.EXOTIC_DUST.get(), 4, 8)
				.unlockedBy(ModItems.MOB_SINEW).save(consumer);
	}

	private DecomposerRecipeBuilder createAlexsMobsRecipe() {
		return DecomposerRecipeBuilder.create().ifModLoaded(AlexsMobs.MODID);
	}

	private DecomposerRecipeBuilder createAlexsCavesRecipe() {
		return DecomposerRecipeBuilder.create().ifModLoaded(AlexsCaves.MODID);
	}

	private void buildAlexsMobsRecipes(Consumer<FinishedRecipe> consumer) {
		createAlexsMobsRecipe()
				.setIngredient(AMItemRegistry.BEAR_FUR)
				.addOutput(ModItems.MINERAL_FRAGMENT.get(), -1, 1)
				.unlockedBy(AMItemRegistry.BEAR_FUR).save(consumer);

		createAlexsMobsRecipe()
				.setIngredient(AMItemRegistry.ROADRUNNER_FEATHER)
				.addOutput(ModItems.TOUGH_FIBERS.get(), 1, 2)
				.addOutput(ModItems.MINERAL_FRAGMENT.get(), 0, 1)
				.unlockedBy(AMItemRegistry.ROADRUNNER_FEATHER).save(consumer);

		createAlexsMobsRecipe()
				.setIngredient(AMItemRegistry.BONE_SERPENT_TOOTH)
				.addOutput(ModItems.MINERAL_FRAGMENT.get(), 2, 4)
				.addOutput(ModItems.BONE_FRAGMENTS.get(), 4, 6)
				.unlockedBy(AMItemRegistry.BONE_SERPENT_TOOTH).save(consumer);

		createAlexsMobsRecipe()
				.setIngredient(AMItemRegistry.GAZELLE_HORN)
				.addOutput(ModItems.MINERAL_FRAGMENT.get(), 3, 5)
				.addOutput(ModItems.TOUGH_FIBERS.get(), 4, 6)
				.unlockedBy(AMItemRegistry.GAZELLE_HORN).save(consumer);

		createAlexsMobsRecipe()
				.setIngredient(AMItemRegistry.CROCODILE_SCUTE)
				.addOutput(ModItems.TOUGH_FIBERS.get(), 3, 5)
				.addOutput(ModItems.MINERAL_FRAGMENT.get(), 1, 3)
				.unlockedBy(AMItemRegistry.CROCODILE_SCUTE).save(consumer);

		createAlexsMobsRecipe()
				.setIngredient(AMItemRegistry.MAGGOT)
				.addOutput(ModItems.BILE.get(), 0, 1)
				.addOutput(ModItems.FLESH_BITS.get(), 0, 1)
				.unlockedBy(AMItemRegistry.MAGGOT).save(consumer);

		createAlexsMobsRecipe()
				.setIngredient(AMItemRegistry.BLOOD_SAC)
				.addOutput(ModItems.BILE.get(), 4, 6)
				.addOutput(ModItems.ELASTIC_FIBERS.get(), 1, 3)
				.unlockedBy(AMItemRegistry.BLOOD_SAC).save(consumer);

		createAlexsMobsRecipe()
				.setIngredient(AMItemRegistry.MOSQUITO_PROBOSCIS)
				.addOutput(ModItems.TOUGH_FIBERS.get(), 0, 2)
				.addOutput(ModItems.ELASTIC_FIBERS.get(), 1, 3)
				.unlockedBy(AMItemRegistry.MOSQUITO_PROBOSCIS).save(consumer);

		createAlexsMobsRecipe()
				.setIngredient(AMItemRegistry.RATTLESNAKE_RATTLE)
				.addOutput(ModItems.TOXIN_EXTRACT.get(), 2, 5)
				.addOutput(ModItems.TOUGH_FIBERS.get(), 2, 3)
				.addOutput(ModItems.MINERAL_FRAGMENT.get(), 1, 3)
				.unlockedBy(AMItemRegistry.RATTLESNAKE_RATTLE).save(consumer);

		createAlexsMobsRecipe()
				.setIngredient(AMItemRegistry.SHARK_TOOTH)
				.addOutput(ModItems.MINERAL_FRAGMENT.get(), 1, 2)
				.addOutput(ModItems.BONE_FRAGMENTS.get(), 2, 4)
				.unlockedBy(AMItemRegistry.SHARK_TOOTH).save(consumer);

		createAlexsMobsRecipe()
				.setIngredient(AMItemRegistry.KOMODO_SPIT)
				.addOutput(ModItems.BILE.get(), 1, 2)
				.addOutput(ModItems.TOXIN_EXTRACT.get(), 0, 1)
				.unlockedBy(AMItemRegistry.KOMODO_SPIT).save(consumer);

		createAlexsMobsRecipe()
				.setIngredient(AMItemRegistry.CENTIPEDE_LEG)
				.addOutput(ModItems.MINERAL_FRAGMENT.get(), 6, 10)
				.addOutput(ModItems.TOUGH_FIBERS.get(), 4, 7)
				.unlockedBy(AMItemRegistry.CENTIPEDE_LEG).save(consumer);

		createAlexsMobsRecipe()
				.setIngredient(AMItemRegistry.MOSQUITO_LARVA)
				.addOutput(ModItems.FLESH_BITS.get(), 0, 2)
				.addOutput(ModItems.TOUGH_FIBERS.get(), 1, 2)
				.addOutput(ModItems.ELASTIC_FIBERS.get(), 2, 3)
				.unlockedBy(AMItemRegistry.MOSQUITO_LARVA).save(consumer);

		createAlexsMobsRecipe()
				.setIngredient(AMItemRegistry.MOOSE_ANTLER)
				.addOutput(ModItems.MINERAL_FRAGMENT.get(), 3, 5)
				.addOutput(ModItems.TOUGH_FIBERS.get(), 4, 6)
				.unlockedBy(AMItemRegistry.MOOSE_ANTLER).save(consumer);

		createAlexsMobsRecipe()
				.setIngredient(AMItemRegistry.MOOSE_RIBS)
				.addOutput(ModItems.FLESH_BITS.get(), 2, 4)
				.addOutput(ModItems.ELASTIC_FIBERS.get(), 1, 2)
				.addOutput(ModItems.BONE_FRAGMENTS.get(), 2, 5)
				.unlockedBy(AMItemRegistry.MOOSE_RIBS).save(consumer);

		createAlexsMobsRecipe()
				.setIngredient(AMItemRegistry.MIMICREAM)
				.addOutput(ModItems.BILE.get(), 3, 4)
				.addOutput(ModItems.EXOTIC_DUST.get(), 4, 6)
				.unlockedBy(AMItemRegistry.MIMICREAM).save(consumer);

		createAlexsMobsRecipe()
				.setIngredient(AMItemRegistry.RACCOON_TAIL)
				.addOutput(ModItems.MINERAL_FRAGMENT.get(), 2, 4)
				.addOutput(ModItems.FLESH_BITS.get(), 2, 3)
				.addOutput(ModItems.ELASTIC_FIBERS.get(), 1, 3)
				.unlockedBy(AMItemRegistry.RACCOON_TAIL).save(consumer);

		createAlexsMobsRecipe()
				.setIngredient(AMItemRegistry.BLOBFISH)
				.addOutput(ModItems.BILE.get(), 2, 4)
				.addOutput(ModItems.FLESH_BITS.get(), 2, 4)
				.addOutput(ModItems.BONE_FRAGMENTS.get(), 1, 2)
				.addOutput(ModItems.ELASTIC_FIBERS.get(), 1, 2)
				.unlockedBy(AMItemRegistry.BLOBFISH).save(consumer);

		createAlexsMobsRecipe()
				.setIngredient(AMItemRegistry.COCKROACH_WING_FRAGMENT)
				.addOutput(ModItems.TOUGH_FIBERS.get(), 1, 2)
				.addOutput(ModItems.MINERAL_FRAGMENT.get(), 0, 1)
				.unlockedBy(AMItemRegistry.COCKROACH_WING_FRAGMENT).save(consumer);

		createAlexsMobsRecipe()
				.setIngredient(AMItemRegistry.COCKROACH_WING)
				.addOutput(ModItems.TOUGH_FIBERS.get(), 9, 18)
				.addOutput(ModItems.MINERAL_FRAGMENT.get(), 8, 10)
				.unlockedBy(AMItemRegistry.COCKROACH_WING).save(consumer);

		createAlexsMobsRecipe()
				.setIngredient(AMItemRegistry.COCKROACH_OOTHECA)
				.addOutput(ModItems.MINERAL_FRAGMENT.get(), 1)
				.addOutput(ModItems.ORGANIC_MATTER.get(), 0, 2)
				.unlockedBy(AMItemRegistry.COCKROACH_OOTHECA).save(consumer);

		createAlexsMobsRecipe()
				.setIngredient(AMItemRegistry.SPIKED_SCUTE)
				.addOutput(ModItems.TOUGH_FIBERS.get(), 3, 5)
				.addOutput(ModItems.MINERAL_FRAGMENT.get(), 1, 3)
				.unlockedBy(AMItemRegistry.SPIKED_SCUTE).save(consumer);

		createAlexsMobsRecipe()
				.setIngredient(AMItemRegistry.HEMOLYMPH_SAC)
				.addOutput(ModItems.BILE.get(), 4, 6)
				.addOutput(ModItems.ELASTIC_FIBERS.get(), 1, 3)
				.addOutput(ModItems.BIO_LUMENS.get(), 2, 4)
				.unlockedBy(AMItemRegistry.HEMOLYMPH_SAC).save(consumer);

		createAlexsMobsRecipe()
				.setIngredient(AMItemRegistry.STRADDLITE)
				.addOutput(ModItems.STONE_POWDER.get(), 2, 3)
				.addOutput(ModItems.MINERAL_FRAGMENT.get(), 4, 8)
				.unlockedBy(AMItemRegistry.STRADDLITE).save(consumer);

		createAlexsMobsRecipe()
				.setIngredient(AMItemRegistry.EMU_FEATHER)
				.addOutput(ModItems.TOUGH_FIBERS.get(), 1, 2)
				.addOutput(ModItems.MINERAL_FRAGMENT.get(), 0, 1)
				.unlockedBy(AMItemRegistry.EMU_FEATHER).save(consumer);

		createAlexsMobsRecipe()
				.setIngredient(AMItemRegistry.DROPBEAR_CLAW)
				.addOutput(ModItems.MINERAL_FRAGMENT.get(), 3, 5)
				.addOutput(ModItems.TOUGH_FIBERS.get(), 4, 6)
				.unlockedBy(AMItemRegistry.DROPBEAR_CLAW).save(consumer);

		createAlexsMobsRecipe()
				.setIngredient(AMItemRegistry.KANGAROO_MEAT)
				.addOutput(ModItems.FLESH_BITS.get(), 2, 4)
				.addOutput(ModItems.ELASTIC_FIBERS.get(), 1, 2)
				.addOutput(ModItems.BONE_FRAGMENTS.get(), 0, 2)
				.unlockedBy(AMItemRegistry.KANGAROO_MEAT).save(consumer);

		createAlexsMobsRecipe()
				.setIngredient(AMItemRegistry.KANGAROO_HIDE)
				.addOutput(ModItems.TOUGH_FIBERS.get(), 1, 2)
				.unlockedBy(AMItemRegistry.KANGAROO_HIDE).save(consumer);

		createAlexsMobsRecipe()
				.setIngredient(AMItemRegistry.AMBERGRIS)
				.addOutput(ModItems.MINERAL_FRAGMENT.get(), 1, 3)
				.addOutput(ModItems.BILE.get(), 4, 6)
				.addOutput(ModItems.ORGANIC_MATTER.get(), 2, 4)
				.unlockedBy(AMItemRegistry.AMBERGRIS).save(consumer);

		createAlexsMobsRecipe()
				.setIngredient(AMItemRegistry.CACHALOT_WHALE_TOOTH)
				.addOutput(ModItems.MINERAL_FRAGMENT.get(), 2, 4)
				.addOutput(ModItems.BONE_FRAGMENTS.get(), 4, 6)
				.unlockedBy(AMItemRegistry.CACHALOT_WHALE_TOOTH).save(consumer);

		createAlexsMobsRecipe()
				.setIngredient(AMItemRegistry.LEAFCUTTER_ANT_PUPA)
				.addOutput(ModItems.BILE.get(), 0, 1)
				.addOutput(ModItems.FLESH_BITS.get(), 0, 1)
				.unlockedBy(AMItemRegistry.LEAFCUTTER_ANT_PUPA).save(consumer);

		createAlexsMobsRecipe()
				.setIngredient(AMItemRegistry.TARANTULA_HAWK_WING_FRAGMENT)
				.addOutput(ModItems.TOUGH_FIBERS.get(), 1, 2)
				.addOutput(ModItems.MINERAL_FRAGMENT.get(), 0, 1)
				.unlockedBy(AMItemRegistry.TARANTULA_HAWK_WING_FRAGMENT).save(consumer);

		createAlexsMobsRecipe()
				.setIngredient(AMItemRegistry.TARANTULA_HAWK_WING)
				.addOutput(ModItems.TOUGH_FIBERS.get(), 9, 18)
				.addOutput(ModItems.MINERAL_FRAGMENT.get(), 8, 10)
				.unlockedBy(AMItemRegistry.TARANTULA_HAWK_WING).save(consumer);

		createAlexsMobsRecipe()
				.setIngredient(AMItemRegistry.VOID_WORM_MANDIBLE)
				.addOutput(ModItems.EXOTIC_DUST.get(), 20, 25)
				.addOutput(ModItems.MINERAL_FRAGMENT.get(), 6, 10)
				.addOutput(ModItems.TOUGH_FIBERS.get(), 4, 7)
				.unlockedBy(AMItemRegistry.VOID_WORM_MANDIBLE).save(consumer);

		createAlexsMobsRecipe()
				.setIngredient(AMItemRegistry.VOID_WORM_EYE)
				.addOutput(ModItems.BILE.get(), 5, 10)
				.addOutput(ModItems.EXOTIC_DUST.get(), 40, 50)
				.addOutput(ModItems.MINERAL_FRAGMENT.get(), 6, 10)
				.addOutput(ModItems.ELASTIC_FIBERS.get(), 4, 7)
				.unlockedBy(AMItemRegistry.VOID_WORM_EYE).save(consumer);

		createAlexsMobsRecipe()
				.setIngredient(AMItemRegistry.SERRATED_SHARK_TOOTH)
				.addOutput(ModItems.MINERAL_FRAGMENT.get(), 1, 2)
				.addOutput(ModItems.BONE_FRAGMENTS.get(), 2, 4)
				.unlockedBy(AMItemRegistry.SERRATED_SHARK_TOOTH).save(consumer);

		createAlexsMobsRecipe()
				.setIngredient(AMItemRegistry.FROSTSTALKER_HORN)
				.addOutput(ModItems.MINERAL_FRAGMENT.get(), 3, 5)
				.addOutput(ModItems.TOUGH_FIBERS.get(), 4, 6)
				.unlockedBy(AMItemRegistry.FROSTSTALKER_HORN).save(consumer);

		createAlexsMobsRecipe()
				.setIngredient(AMItemRegistry.SHED_SNAKE_SKIN)
				.addOutput(ModItems.TOUGH_FIBERS.get(), 0, 2)
				.unlockedBy(AMItemRegistry.SHED_SNAKE_SKIN).save(consumer);

		createAlexsMobsRecipe()
				.setIngredient(AMItemRegistry.ROCKY_SHELL)
				.addOutput(ModItems.TOUGH_FIBERS.get(), 3, 5)
				.addOutput(ModItems.MINERAL_FRAGMENT.get(), 1, 3)
				.addOutput(ModItems.STONE_POWDER.get(), 2, 4)
				.unlockedBy(AMItemRegistry.ROCKY_SHELL).save(consumer);

		createAlexsMobsRecipe()
				.setIngredient(AMItemRegistry.RAINBOW_JELLY)
				.addOutput(ModItems.BILE.get(), 3, 4)
				.addOutput(ModItems.EXOTIC_DUST.get(), 2, 5)
				.addOutput(ModItems.BIO_LUMENS.get(), 1, 3)
				.unlockedBy(AMItemRegistry.RAINBOW_JELLY).save(consumer);

		createAlexsMobsRecipe()
				.setIngredient(AMItemRegistry.BISON_FUR)
				.addOutput(ModItems.MINERAL_FRAGMENT.get(), 0, 1)
				.unlockedBy(AMItemRegistry.BISON_FUR).save(consumer);

		createAlexsMobsRecipe()
				.setIngredient(AMItemRegistry.LOST_TENTACLE)
				.addOutput(ModItems.ELASTIC_FIBERS.get(), 8, 15)
				.addOutput(ModItems.FLESH_BITS.get(), 3, 5)
				.unlockedBy(AMItemRegistry.LOST_TENTACLE).save(consumer);

		createAlexsMobsRecipe()
				.setIngredient(AMItemRegistry.RAW_CATFISH)
				.addOutput(ModItems.FLESH_BITS.get(), 2, 4)
				.addOutput(ModItems.BONE_FRAGMENTS.get(), 1, 2)
				.addOutput(ModItems.ELASTIC_FIBERS.get(), 1, 2)
				.unlockedBy(AMItemRegistry.RAW_CATFISH).save(consumer);

		createAlexsMobsRecipe()
				.setIngredient(AMItemRegistry.ELASTIC_TENDON)
				.addOutput(ModItems.ELASTIC_FIBERS.get(), 4, 8)
				.addOutput(ModItems.TOUGH_FIBERS.get(), 1, 2)
				.addOutput(ModItems.EXOTIC_DUST.get(), 4, 8)
				.unlockedBy(AMItemRegistry.ELASTIC_TENDON).save(consumer);

		createAlexsMobsRecipe()
				.setIngredient(AMItemRegistry.BANANA_SLUG_SLIME)
				.addOutput(ModItems.BILE.get(), 2, 4)
				.unlockedBy(AMItemRegistry.BANANA_SLUG_SLIME).save(consumer);

		createAlexsMobsRecipe()
				.setIngredient(AMItemRegistry.FISH_BONES)
				.addOutput(ModItems.BONE_FRAGMENTS.get(), 2, 5)
				.unlockedBy(AMItemRegistry.FISH_BONES).save(consumer);

		createAlexsMobsRecipe()
				.setIngredient(AMBlockRegistry.CAIMAN_EGG.get())
				.addOutput(ModItems.HORMONE_SECRETION.get(), 1)
				.addOutput(ModItems.ORGANIC_MATTER.get(), 0, 2)
				.unlockedBy(AMBlockRegistry.CAIMAN_EGG.get()).save(consumer);

		createAlexsMobsRecipe()
				.setIngredient(AMBlockRegistry.TRIOPS_EGGS.get())
				.addOutput(ModItems.HORMONE_SECRETION.get(), -1, 1)
				.addOutput(ModItems.ORGANIC_MATTER.get(), 0, 1)
				.unlockedBy(AMBlockRegistry.TRIOPS_EGGS.get()).save(consumer);
	}

	private void buildAlexsCavesRecipes(Consumer<FinishedRecipe> consumer) {
		createAlexsCavesRecipe()
				.setIngredient(ACItemRegistry.RAW_SCARLET_NEODYMIUM)
				.addOutput(ModItems.MINERAL_FRAGMENT.get(), 5, 9)
				.addOutput(ModItems.STONE_POWDER.get(), 1, 2)
				.addOutput(Items.RED_DYE, 1, 2)
				.unlockedBy(ACItemRegistry.RAW_SCARLET_NEODYMIUM).save(consumer);

		createAlexsCavesRecipe()
				.setIngredient(ACItemRegistry.RAW_AZURE_NEODYMIUM)
				.addOutput(ModItems.MINERAL_FRAGMENT.get(), 5, 9)
				.addOutput(ModItems.STONE_POWDER.get(), 1, 2)
				.addOutput(Items.BLUE_DYE, 1, 2)
				.unlockedBy(ACItemRegistry.RAW_AZURE_NEODYMIUM).save(consumer);

		createAlexsCavesRecipe()
				.setIngredient(ACItemRegistry.FERROUSLIME_BALL)
				.addOutput(ModItems.BILE.get(), 2, 3)
				.addOutput(ModItems.MINERAL_FRAGMENT.get(), 4, 7)
				.unlockedBy(ACItemRegistry.FERROUSLIME_BALL).save(consumer);

		createAlexsCavesRecipe()
				.setIngredient(ACItemRegistry.PINE_NUTS)
				.addOutput(ModItems.ORGANIC_MATTER.get(), 2, 3)
				.unlockedBy(ACItemRegistry.PINE_NUTS).save(consumer);

		createAlexsCavesRecipe()
				.setIngredient(ACItemRegistry.AMBER_CURIOSITY)
				.addOutput(ModItems.BILE.get(), 3, 6)
				.addOutput(ModItems.EXOTIC_DUST.get(), 3, 5)
				.addOutput(ModItems.ORGANIC_MATTER.get(), 0, 4)
				.unlockedBy(ACItemRegistry.AMBER_CURIOSITY).save(consumer);

		createAlexsCavesRecipe()
				.setIngredient(ACBlockRegistry.DINOSAUR_CHOP.get())
				.addOutput(ModItems.FLESH_BITS.get(), 3 * 12, 6 * 12)
				.addOutput(ModItems.ELASTIC_FIBERS.get(), 12, 2 * 12)
				.addOutput(ModItems.BONE_FRAGMENTS.get(), 2 * 6, 3 * 6)
				.addOutput(ModItems.HORMONE_SECRETION.get(), 3, 6)
				.unlockedBy(ACBlockRegistry.DINOSAUR_CHOP.get()).save(consumer);

		createAlexsCavesRecipe()
				.setIngredient(ACItemRegistry.TOUGH_HIDE)
				.addOutput(ModItems.TOUGH_FIBERS.get(), 2, 4)
				.unlockedBy(ACItemRegistry.TOUGH_HIDE).save(consumer);

		createAlexsCavesRecipe()
				.setIngredient(ACItemRegistry.HEAVY_BONE)
				.addOutput(ModItems.BONE_FRAGMENTS.get(), 3 * 2, 6 * 2)
				.addOutput(ModItems.MINERAL_FRAGMENT.get(), 1, 2)
				.addOutput(ModItems.HORMONE_SECRETION.get(), 1, 4)
				.unlockedBy(ACItemRegistry.HEAVY_BONE).save(consumer);

		createAlexsCavesRecipe()
				.setIngredient(ACItemRegistry.RADGILL)
				.addOutput(ModItems.FLESH_BITS.get(), 2, 4)
				.addOutput(ModItems.ELASTIC_FIBERS.get(), 1, 2)
				.addOutput(ModItems.BONE_FRAGMENTS.get(), 2, 3)
				.addOutput(ModItems.TOXIN_EXTRACT.get(), 2, 4)
				.unlockedBy(ACItemRegistry.RADGILL).save(consumer);

		createAlexsCavesRecipe()
				.setIngredient(ACItemRegistry.TOXIC_PASTE)
				.addOutput(ModItems.TOXIN_EXTRACT.get(), 2, 4)
				.unlockedBy(ACItemRegistry.TOXIC_PASTE).save(consumer);

		createAlexsCavesRecipe()
				.setIngredient(ACItemRegistry.LANTERNFISH)
				.addOutput(ModItems.FLESH_BITS.get(), 1, 2)
				.addOutput(ModItems.ELASTIC_FIBERS.get(), 0, 1)
				.addOutput(ModItems.BONE_FRAGMENTS.get(), 0, 2)
				.addOutput(ModItems.BIO_LUMENS.get(), 2, 4)
				.unlockedBy(ACItemRegistry.LANTERNFISH).save(consumer);

		createAlexsCavesRecipe()
				.setIngredient(ACItemRegistry.TRIPODFISH)
				.addOutput(ModItems.FLESH_BITS.get(), 2, 4)
				.addOutput(ModItems.ELASTIC_FIBERS.get(), 1, 2)
				.addOutput(ModItems.BONE_FRAGMENTS.get(), 2, 3)
				.unlockedBy(ACItemRegistry.TRIPODFISH).save(consumer);

		createAlexsCavesRecipe()
				.setIngredient(ACItemRegistry.SEA_PIG)
				.addOutput(ModItems.ORGANIC_MATTER.get(), 0, 1)
				.addOutput(ModItems.TOXIN_EXTRACT.get(), 2, 3)
				.addOutput(ModItems.BILE.get(), 2, 4)
				.unlockedBy(ACItemRegistry.SEA_PIG).save(consumer);

		createAlexsCavesRecipe()
				.setIngredient(ACItemRegistry.BIOLUMINESSCENCE)
				.addOutput(ModItems.BILE.get(), 2, 3)
				.addOutput(ModItems.BIO_LUMENS.get(), 4, 7)
				.unlockedBy(ACItemRegistry.BIOLUMINESSCENCE).save(consumer);

		createAlexsCavesRecipe()
				.setIngredient(ACItemRegistry.PEARL)
				.addOutput(ModItems.GEM_FRAGMENTS.get(), 4)
				.addOutput(ModItems.EXOTIC_DUST.get(), 7)
				.addOutput(ModItems.MINERAL_FRAGMENT.get(), 5)
				.unlockedBy(ACItemRegistry.PEARL).save(consumer);

		createAlexsCavesRecipe()
				.setIngredient(ACItemRegistry.GUANO)
				.addOutput(ModItems.BILE.get(), 2, 3)
				.addOutput(ModItems.ORGANIC_MATTER.get(), 3, 6)
				.unlockedBy(ACItemRegistry.GUANO).save(consumer);

		createAlexsCavesRecipe()
				.setIngredient(ACItemRegistry.CORRODENT_TEETH)
				.addOutput(ModItems.STONE_POWDER.get(), 0, 3)
				.addOutput(ModItems.MINERAL_FRAGMENT.get(), 2, 4)
				.addOutput(ModItems.BONE_FRAGMENTS.get(), 4, 6)
				.unlockedBy(ACItemRegistry.CORRODENT_TEETH).save(consumer);

		createAlexsCavesRecipe()
				.setIngredient(ACItemRegistry.VESPER_WING)
				.addOutput(ModItems.TOUGH_FIBERS.get(), 9, 18)
				.addOutput(ModItems.MINERAL_FRAGMENT.get(), 8, 10)
				.unlockedBy(ACItemRegistry.VESPER_WING).save(consumer);

		createAlexsCavesRecipe()
				.setIngredient(ACBlockRegistry.CURLY_FERN.get())
				.addOutput(ModItems.ORGANIC_MATTER.get(), 2, 4)
				.addOutput(Items.LIME_DYE, 1, 2)
				.unlockedBy(ACBlockRegistry.CURLY_FERN.get()).save(consumer);

		createAlexsCavesRecipe()
				.setIngredient(ACBlockRegistry.ANCIENT_SAPLING.get())
				.addOutput(ModItems.ORGANIC_MATTER.get(), 1, 3)
				.addOutput(ModItems.STONE_POWDER.get(), 0, 3)
				.addOutput(ModItems.EXOTIC_DUST.get(), 0, 1)
				.unlockedBy(ACBlockRegistry.ANCIENT_SAPLING.get()).save(consumer);

		createAlexsCavesRecipe()
				.setIngredient(ACBlockRegistry.FLYTRAP.get())
				.addOutput(ModItems.ORGANIC_MATTER.get(), 2, 4)
				.addOutput(Items.RED_DYE, 0, 1)
				.unlockedBy(ACBlockRegistry.FLYTRAP.get()).save(consumer);

		createAlexsCavesRecipe()
				.setIngredient(ACBlockRegistry.SUBTERRANODON_EGG.get())
				.addOutput(ModItems.STONE_POWDER.get(), 0, 4)
				.addOutput(ModItems.HORMONE_SECRETION.get(), 1, 4)
				.addOutput(ModItems.ORGANIC_MATTER.get(), 1, 4)
				.addOutput(ModItems.EXOTIC_DUST.get(), 2, 6)
				.unlockedBy(ACBlockRegistry.SUBTERRANODON_EGG.get()).save(consumer);

		createAlexsCavesRecipe()
				.setIngredient(ACBlockRegistry.VALLUMRAPTOR_EGG.get())
				.addOutput(ModItems.STONE_POWDER.get(), 0, 4)
				.addOutput(ModItems.HORMONE_SECRETION.get(), 1, 4)
				.addOutput(ModItems.ORGANIC_MATTER.get(), 1, 4)
				.addOutput(ModItems.EXOTIC_DUST.get(), 2, 6)
				.unlockedBy(ACBlockRegistry.VALLUMRAPTOR_EGG.get()).save(consumer);

		createAlexsCavesRecipe()
				.setIngredient(ACBlockRegistry.GROTTOCERATOPS_EGG.get())
				.addOutput(ModItems.STONE_POWDER.get(), 0, 4)
				.addOutput(ModItems.HORMONE_SECRETION.get(), 1, 4)
				.addOutput(ModItems.ORGANIC_MATTER.get(), 1, 4)
				.addOutput(ModItems.EXOTIC_DUST.get(), 2, 6)
				.unlockedBy(ACBlockRegistry.GROTTOCERATOPS_EGG.get()).save(consumer);

		createAlexsCavesRecipe()
				.setIngredient(ACBlockRegistry.TREMORSAURUS_EGG.get())
				.addOutput(ModItems.STONE_POWDER.get(), 0, 4)
				.addOutput(ModItems.HORMONE_SECRETION.get(), 1, 4)
				.addOutput(ModItems.ORGANIC_MATTER.get(), 1, 4)
				.addOutput(ModItems.EXOTIC_DUST.get(), 2, 6)
				.unlockedBy(ACBlockRegistry.TREMORSAURUS_EGG.get()).save(consumer);

		createAlexsCavesRecipe()
				.setIngredient(ACBlockRegistry.RELICHEIRUS_EGG.get())
				.addOutput(ModItems.STONE_POWDER.get(), 0, 4)
				.addOutput(ModItems.HORMONE_SECRETION.get(), 1, 4)
				.addOutput(ModItems.ORGANIC_MATTER.get(), 1, 4)
				.addOutput(ModItems.EXOTIC_DUST.get(), 2, 6)
				.unlockedBy(ACBlockRegistry.RELICHEIRUS_EGG.get()).save(consumer);

		createAlexsCavesRecipe()
				.setIngredient(ACBlockRegistry.CARMINE_FROGLIGHT.get())
				.addOutput(ModItems.BIO_LUMENS.get(), 5, 9)
				.addOutput(ModItems.BILE.get(), 2, 3)
				.unlockedBy(ACBlockRegistry.CARMINE_FROGLIGHT.get()).save(consumer);
	}

}
