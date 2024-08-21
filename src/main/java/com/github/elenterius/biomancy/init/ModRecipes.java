package com.github.elenterius.biomancy.init;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.crafting.recipe.*;
import com.github.elenterius.biomancy.crafting.recipe.SimpleRecipeType.ItemStackRecipeType;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.block.ComposterBlock;
import net.minecraftforge.common.brewing.BrewingRecipe;
import net.minecraftforge.common.brewing.BrewingRecipeRegistry;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.crafting.StrictNBTIngredient;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.Objects;
import java.util.function.Supplier;

public final class ModRecipes {

	public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS = DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, BiomancyMod.MOD_ID);
	public static final DeferredRegister<RecipeType<?>> RECIPE_TYPES = DeferredRegister.create(Registries.RECIPE_TYPE, BiomancyMod.MOD_ID);

	public static final RegistryObject<ItemStackRecipeType<DecomposerRecipe>> DECOMPOSING_RECIPE_TYPE = registerItemStackRecipeType("decomposing");
	public static final RegistryObject<RecipeSerializer<DecomposerRecipe>> DECOMPOSING_SERIALIZER = registerRecipeSerializer(DECOMPOSING_RECIPE_TYPE, DecomposerRecipe.Serializer::new);
	public static final RegistryObject<ItemStackRecipeType<BioLabRecipe>> BIO_BREWING_RECIPE_TYPE = registerItemStackRecipeType("bio_brewing");
	public static final RegistryObject<RecipeSerializer<BioLabRecipe>> BIO_BREWING_SERIALIZER = registerRecipeSerializer(BIO_BREWING_RECIPE_TYPE, BioLabRecipe.Serializer::new);
	public static final RegistryObject<ItemStackRecipeType<BioForgeRecipe>> BIO_FORGING_RECIPE_TYPE = registerItemStackRecipeType("bio_forging");
	public static final RegistryObject<RecipeSerializer<BioForgeRecipe>> BIO_FORGING_SERIALIZER = registerRecipeSerializer(BIO_FORGING_RECIPE_TYPE, BioForgeRecipe.Serializer::new);
	public static final RegistryObject<ItemStackRecipeType<DigestingRecipe>> DIGESTING_RECIPE_TYPE = registerItemStackRecipeType("digesting");
	public static final RegistryObject<RecipeSerializer<StaticDigestingRecipe>> DIGESTING_SERIALIZER = registerRecipeSerializer(DIGESTING_RECIPE_TYPE, StaticDigestingRecipe.Serializer::new);

	// DYNAMIC
	public static final RegistryObject<RecipeSerializer<FoodDigestingRecipe>> FOOD_DIGESTING_SERIALIZER = registerDynamicRecipeSerializer(DIGESTING_RECIPE_TYPE, "food", FoodDigestingRecipe.Serializer::new);
	public static final RegistryObject<RecipeSerializer<BiometricMembraneRecipe>> BIOMETRIC_MEMBRANE_CRAFTING_SERIALIZER = registerDynamicCraftingRecipeSerializer(RecipeType.CRAFTING, "biometric_membrane", () -> new SimpleCraftingRecipeSerializer<>(BiometricMembraneRecipe::new));
	public static final RegistryObject<RecipeSerializer<CradleCleansingRecipe>> CRADLE_CLEANSING_SERIALIZER = registerDynamicCraftingRecipeSerializer(RecipeType.CRAFTING, "cradle_cleansing", () -> new SimpleCraftingRecipeSerializer<>(CradleCleansingRecipe::new));

	private ModRecipes() {}

	public static void registerComposterRecipes() {
		ComposterBlock.COMPOSTABLES.putIfAbsent(ModItems.ORGANIC_MATTER.get(), 0.25f);
	}

	public static void registerBrewingRecipes() {
		registerBrewingRecipe(ModItems.TOXIN_EXTRACT.get(), Potions.AWKWARD, Potions.POISON);
		registerBrewingRecipe(ModItems.TOXIN_GLAND.get(), Potions.MUNDANE, Potions.LONG_POISON);
		registerBrewingRecipe(ModItems.TOXIN_GLAND.get(), Potions.THICK, Potions.STRONG_POISON);
		registerBrewingRecipe(ModItems.WITHERING_OOZE.get(), Potions.POISON, Potions.HARMING);
		registerBrewingRecipe(ModItems.WITHERING_OOZE.get(), Potions.STRONG_POISON, Potions.STRONG_HARMING);
		registerBrewingRecipe(ModItems.BLOOMBERRY.get(), Potions.MUNDANE, ModPotions.PRIMORDIAL_INFESTATION.get());
		registerBrewingRecipe(Items.REDSTONE, ModPotions.PRIMORDIAL_INFESTATION.get(), ModPotions.LONG_PRIMORDIAL_INFESTATION.get());
	}

	private static void registerBrewingRecipe(Item reactant, Potion potionBase, Potion potionResult) {
		BrewingRecipeRegistry.addRecipe(new BrewingRecipe(createPotionIngredient(potionBase), Ingredient.of(reactant), createPotionStack(potionResult)));
	}

	private static ItemStack createPotionStack(Supplier<Potion> supplier) {
		return createPotionStack(supplier.get());
	}

	private static ItemStack createPotionStack(Potion potion) {
		return PotionUtils.setPotion(new ItemStack(Items.POTION), potion);
	}

	private static Ingredient createPotionIngredient(Potion potion) {
		return StrictNBTIngredient.of(createPotionStack(potion));
	}

	public static void registerIngredientSerializers() {
		CraftingHelper.register(BiomancyMod.createRL("food_nutrition"), AnyFoodIngredient.Serializer.INSTANCE);
	}

	private static <T extends RecipeType<?>, R extends Recipe<Container>> RegistryObject<RecipeSerializer<R>> registerRecipeSerializer(RegistryObject<T> recipeType, Supplier<RecipeSerializer<R>> serializerSupplier) {
		return RECIPE_SERIALIZERS.register(recipeType.getId().getPath(), serializerSupplier);
	}

	private static <T extends RecipeType<?>, R extends Recipe<Container>> RegistryObject<RecipeSerializer<R>> registerDynamicRecipeSerializer(RegistryObject<T> recipeType, String name, Supplier<RecipeSerializer<R>> serializerSupplier) {
		String prefix = recipeType.getId().getPath() + "_dynamic_";
		return RECIPE_SERIALIZERS.register(prefix + name, serializerSupplier);
	}

	private static <T extends CraftingRecipe, R extends CraftingRecipe> RegistryObject<RecipeSerializer<R>> registerDynamicCraftingRecipeSerializer(RecipeType<T> recipeType, String name, Supplier<RecipeSerializer<R>> serializerSupplier) {
		String prefix = Objects.requireNonNull(ResourceLocation.tryParse(recipeType.toString())).getPath() + "_dynamic_";
		return RECIPE_SERIALIZERS.register(prefix + name, serializerSupplier);
	}

	private static <R extends CraftingRecipe> RegistryObject<RecipeSerializer<R>> registerCraftingRecipeSerializer(String name, Supplier<RecipeSerializer<R>> serializer) {
		return RECIPE_SERIALIZERS.register(name, serializer);
	}

	private static <T extends Recipe<Container>> RegistryObject<ItemStackRecipeType<T>> registerItemStackRecipeType(String namespacedId) {
		return RECIPE_TYPES.register(namespacedId, () -> new ItemStackRecipeType<>(BiomancyMod.createRLString(namespacedId)));
	}

	private static <T extends Recipe<Container>> RegistryObject<SimpleRecipeType<T>> createSimpleRecipeType(String namespacedId) {
		return RECIPE_TYPES.register(namespacedId, () -> new SimpleRecipeType<>(BiomancyMod.createRLString(namespacedId)));
	}

}
