package com.github.elenterius.blightlings.init;

import com.github.elenterius.blightlings.BlightlingsMod;
import com.github.elenterius.blightlings.recipe.PotionBeetleRecipe;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.SpecialRecipeSerializer;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ModRecipes
{
    public static final DeferredRegister<IRecipeSerializer<?>> RECIPE_SERIALIZERS = DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, BlightlingsMod.MOD_ID);

    public static final RegistryObject<SpecialRecipeSerializer<PotionBeetleRecipe>> CRAFTING_SPECIAL_BEETLE_POTION = RECIPE_SERIALIZERS.register("crafting_special_potion_beetle", () -> new SpecialRecipeSerializer<>(PotionBeetleRecipe::new));
}
