package com.github.elenterius.blightlings.datagen;

import com.github.elenterius.blightlings.BlightlingsMod;
import com.github.elenterius.blightlings.init.ModItems;
import com.github.elenterius.blightlings.init.ModRecipes;
import net.minecraft.block.Blocks;
import net.minecraft.data.*;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.Tags;

import java.util.function.Consumer;

public class ModRecipeProvider extends RecipeProvider
{
    public ModRecipeProvider(DataGenerator generatorIn) {
        super(generatorIn);
    }

    @Override
    protected void registerRecipes(Consumer<IFinishedRecipe> consumer) {
        ShapedRecipeBuilder.shapedRecipe(ModItems.BLIGHT_EYE.get())
                .key('G', ModItems.BLIGHT_GOO.get()).key('S', ModItems.BLIGHT_SHARD.get()).key('E', Items.SPIDER_EYE)
                .patternLine("GSG").patternLine("SES").patternLine("GSG")
                .addCriterion("has_blight_shard", hasItem(ModItems.BLIGHT_SHARD.get())).build(consumer);

        ShapedRecipeBuilder.shapedRecipe(ModItems.TRUE_SIGHT_GOGGLES.get())
                .key('O', ModItems.BLIGHT_EYE.get()).key('S', ModItems.BLIGHT_SAC.get()).key('I', Tags.Items.INGOTS_IRON)
                .patternLine("OSO").patternLine("OIO")
                .addCriterion("has_blight_shard", hasItem(ModItems.BLIGHT_SHARD.get())).build(consumer);

        ShapedRecipeBuilder.shapedRecipe(Blocks.PINK_WOOL)
                .key('#', ModItems.BLIGHT_STRING.get()).patternLine("##").patternLine("##")
                .addCriterion("has_blight_string", hasItem(ModItems.BLIGHT_STRING.get())).build(consumer, new ResourceLocation(BlightlingsMod.MOD_ID, "pink_wool_from_string"));

        SmithingRecipeBuilder.smithingRecipe(Ingredient.fromItems(Items.DIAMOND_AXE), Ingredient.fromItems(ModItems.BLIGHT_SAC.get()), ModItems.BLIGHTBRINGER_AXE.get())
                .addCriterion("has_blight_sac", hasItem(ModItems.BLIGHT_SAC.get())).build(consumer, new ResourceLocation(BlightlingsMod.MOD_ID, ModItems.BLIGHTBRINGER_AXE.get().getRegistryName().getPath() + "_smithing"));

        CustomRecipeBuilder.customRecipe(ModRecipes.CRAFTING_SPECIAL_BEETLE_POTION.get()).build(consumer, BlightlingsMod.MOD_ID + ":" + "potion_beetle");
    }

    @Override
    public String getName() {
        return "BlightlingsMod " + super.getName();
    }
}
