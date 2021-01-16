package com.github.elenterius.blightlings.datagen;

import com.github.elenterius.blightlings.BlightlingsMod;
import com.github.elenterius.blightlings.init.ModItems;
import com.github.elenterius.blightlings.init.ModRecipes;
import net.minecraft.data.*;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.Tags;

import java.util.function.Consumer;

public class ModRecipeProvider extends RecipeProvider {
	public ModRecipeProvider(DataGenerator generatorIn) {
		super(generatorIn);
	}

	@Override
	protected void registerRecipes(Consumer<IFinishedRecipe> consumer) {
		ShapedRecipeBuilder.shapedRecipe(ModItems.OCULUS.get())
				.key('F', ModItems.FLESH_LUMP.get()).key('R', ModItems.REJUVENATING_MUCUS.get()).key('S', ModItems.LENS_SHARD.get()).key('E', Items.SPIDER_EYE)
				.patternLine("RSF").patternLine("SER").patternLine("RSF")
				.addCriterion("has_rejuvenating_mucus", hasItem(ModItems.REJUVENATING_MUCUS.get())).build(consumer);

		ShapedRecipeBuilder.shapedRecipe(ModItems.TRUE_SIGHT_GOGGLES.get())
				.key('O', ModItems.OCULUS.get()).key('S', ModItems.TWISTED_HEART.get()).key('I', Tags.Items.INGOTS_IRON)
				.patternLine("OSO").patternLine("OIO")
				.addCriterion("has_lens_shard", hasItem(ModItems.LENS_SHARD.get())).build(consumer);

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

		CustomRecipeBuilder.customRecipe(ModRecipes.REPAIR_SPECIAL_SEWING_KIT.get()).build(consumer, BlightlingsMod.MOD_ID + ":" + "sewing_kit_nbt");

		ShapelessRecipeBuilder.shapelessRecipe(Items.BONE_MEAL)
				.addIngredient(ModItems.BONE_SCRAPS.get(), 4)
				.addCriterion("has_bone_scraps", hasItem(ModItems.BONE_SCRAPS.get())).build(consumer);

		ShapedRecipeBuilder.shapedRecipe(ModItems.MENDED_SKIN.get())
				.key('S', ModItems.SEWING_KIT.get())
				.key('C', ModItems.SKIN_CHUNK.get())
				.patternLine("CC ").patternLine("CC ").patternLine("CCS")
				.addCriterion("has_skin_chunk", hasItem(ModItems.SKIN_CHUNK.get())).build(consumer);

		CookingRecipeBuilder.cookingRecipe(Ingredient.fromItems(ModItems.MENDED_SKIN.get()), Items.LEATHER, 0.1F, 350, IRecipeSerializer.SMOKING)
				.addCriterion("has_skin_chunk", hasItem(ModItems.SKIN_CHUNK.get())).build(consumer, new ResourceLocation(BlightlingsMod.MOD_ID, "leather_from_smoking"));

		//noinspection ConstantConditions
		SmithingRecipeBuilder.smithingRecipe(Ingredient.fromItems(Items.DIAMOND_AXE), Ingredient.fromItems(ModItems.TWISTED_HEART.get()), ModItems.BLIGHTBRINGER_AXE.get())
				.addCriterion("has_blight_sac", hasItem(ModItems.TWISTED_HEART.get())).build(consumer, new ResourceLocation(BlightlingsMod.MOD_ID, ModItems.BLIGHTBRINGER_AXE.get().getRegistryName().getPath() + "_smithing"));

		CustomRecipeBuilder.customRecipe(ModRecipes.CRAFTING_SPECIAL_POTION_BEETLE.get()).build(consumer, BlightlingsMod.MOD_ID + ":" + "potion_beetle");
		CustomRecipeBuilder.customRecipe(ModRecipes.CRAFTING_SPECIAL_MASON_BEETLE.get()).build(consumer, BlightlingsMod.MOD_ID + ":" + "mason_beetle");
	}

	@Override
	public String getName() {
		return "BlightlingsMod " + super.getName();
	}
}
