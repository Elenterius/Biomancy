package com.github.elenterius.blightlings.init;

import com.github.elenterius.blightlings.BlightlingsMod;
import com.github.elenterius.blightlings.recipe.MasonBeetleRecipe;
import com.github.elenterius.blightlings.recipe.PotionBeetleRecipe;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.SpecialRecipeSerializer;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public final class ModRecipes {
	public static final DeferredRegister<IRecipeSerializer<?>> RECIPE_SERIALIZERS = DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, BlightlingsMod.MOD_ID);

	public static final RegistryObject<SpecialRecipeSerializer<PotionBeetleRecipe>> CRAFTING_SPECIAL_POTION_BEETLE = RECIPE_SERIALIZERS.register("crafting_special_potion_beetle", () -> new SpecialRecipeSerializer<>(PotionBeetleRecipe::new));
	public static final RegistryObject<SpecialRecipeSerializer<MasonBeetleRecipe>> CRAFTING_SPECIAL_MASON_BEETLE = RECIPE_SERIALIZERS.register("crafting_special_mason_beetle", () -> new SpecialRecipeSerializer<>(MasonBeetleRecipe::new));

	private ModRecipes() {}
}
