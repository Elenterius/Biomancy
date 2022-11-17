package com.github.elenterius.biomancy.init;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.recipe.*;
import com.github.elenterius.biomancy.recipe.SimpleRecipeType.ItemStackRecipeType;
import net.minecraft.core.Registry;
import net.minecraft.world.Container;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.ComposterBlock;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

public final class ModRecipes {

	public static final DeferredRegister<RecipeType<?>> RECIPE_TYPES = DeferredRegister.create(Registry.RECIPE_TYPE_REGISTRY, BiomancyMod.MOD_ID);
	public static final RegistryObject<ItemStackRecipeType<DecomposerRecipe>> DECOMPOSING_RECIPE_TYPE = registerItemStackRecipeType("decomposing");
	public static final RegistryObject<ItemStackRecipeType<BioLabRecipe>> BIO_BREWING_RECIPE_TYPE = registerItemStackRecipeType("bio_brewing");
	public static final RegistryObject<ItemStackRecipeType<BioForgeRecipe>> BIO_FORGING_RECIPE_TYPE = registerItemStackRecipeType("bio_forging");
	public static final RegistryObject<ItemStackRecipeType<DigesterRecipe>> DIGESTING_RECIPE_TYPE = registerItemStackRecipeType("digesting");

	public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS = DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, BiomancyMod.MOD_ID);
	public static final RegistryObject<RecipeSerializer<DecomposerRecipe>> DECOMPOSING_SERIALIZER = registerRecipeSerializer(DECOMPOSING_RECIPE_TYPE, DecomposerRecipe.Serializer::new);
	public static final RegistryObject<RecipeSerializer<BioLabRecipe>> BIO_BREWING_SERIALIZER = registerRecipeSerializer(BIO_BREWING_RECIPE_TYPE, BioLabRecipe.Serializer::new);
	public static final RegistryObject<RecipeSerializer<BioForgeRecipe>> BIO_FORGING_SERIALIZER = registerRecipeSerializer(BIO_FORGING_RECIPE_TYPE, BioForgeRecipe.Serializer::new);
	public static final RegistryObject<RecipeSerializer<DigesterRecipe>> DIGESTING_SERIALIZER = registerRecipeSerializer(DIGESTING_RECIPE_TYPE, DigesterRecipe.Serializer::new);

	private ModRecipes() {}

	public static void registerComposterRecipes() {
		ComposterBlock.COMPOSTABLES.putIfAbsent(ModItems.ORGANIC_MATTER.get(), 0.25f);
	}

	private static <T extends RecipeType<?>, R extends Recipe<Container>> RegistryObject<RecipeSerializer<R>> registerRecipeSerializer(RegistryObject<T> recipeTypeHolder, Supplier<RecipeSerializer<R>> serializerSupplier) {
		return RECIPE_SERIALIZERS.register(recipeTypeHolder.getId().getPath(), serializerSupplier);
	}

	private static <T extends Recipe<Container>> RegistryObject<ItemStackRecipeType<T>> registerItemStackRecipeType(String identifier) {
		return RECIPE_TYPES.register(identifier, () -> new ItemStackRecipeType<>(BiomancyMod.createRLString(identifier)));
	}

	private static <T extends Recipe<Container>> RegistryObject<SimpleRecipeType<T>> createSimpleRecipeType(String identifier) {
		return RECIPE_TYPES.register(identifier, () -> new SimpleRecipeType<>(BiomancyMod.createRLString(identifier)));
	}

}
