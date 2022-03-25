package com.github.elenterius.biomancy.init;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.recipe.*;
import com.github.elenterius.biomancy.recipe.RecipeTypeImpl.ItemStackRecipeType;
import com.github.elenterius.biomancy.recipe.RecipeTypeImpl.SimpleRecipeType;
import net.minecraft.core.Registry;
import net.minecraft.world.Container;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.ComposterBlock;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.Set;

public final class ModRecipes {


//	public static final RegistryObject<SpecialRecipeSerializer<AddPotionToBoomlingRecipe>> CRAFTING_SPECIAL_BOOMLING = RECIPE_SERIALIZERS.register("crafting_special_boomling", () -> new SpecialRecipeSerializer<>(AddPotionToBoomlingRecipe::new));
//	public static final RegistryObject<SpecialRecipeSerializer<AddPotionToBoomlingGunRecipe>> CRAFTING_SPECIAL_BOOMLING_GUN = RECIPE_SERIALIZERS.register("crafting_special_boomling_gun", () -> new SpecialRecipeSerializer<>(AddPotionToBoomlingGunRecipe::new));
//	public static final RegistryObject<SpecialRecipeSerializer<AddUserToAccessKeyRecipe>> CRAFTING_SPECIAL_ADD_USER_TO_KEY = RECIPE_SERIALIZERS.register("crafting_special_add_user_to_key", () -> new SpecialRecipeSerializer<>(AddUserToAccessKeyRecipe::new));

	public static final ItemStackRecipeType<DecomposerRecipe> DECOMPOSING_RECIPE_TYPE = createItemStackRecipeType("decomposing");
	public static final ItemStackRecipeType<BioLabRecipe> BIO_BREWING_RECIPE_TYPE = createItemStackRecipeType("bio_brewing");
	public static final SimpleRecipeType<BioForgeRecipe> BIO_FORGING_RECIPE_TYPE = createSimpleRecipeType("bio_forging");
	public static final ItemStackRecipeType<DigesterRecipe> DIGESTING_RECIPE_TYPE = createItemStackRecipeType("digesting");
	public static final Set<RecipeType<? extends Recipe<Container>>> RECIPE_TYPES = Set.of(DECOMPOSING_RECIPE_TYPE, BIO_FORGING_RECIPE_TYPE, BIO_BREWING_RECIPE_TYPE);

	public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS = DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, BiomancyMod.MOD_ID);
	public static final RegistryObject<RecipeSerializer<DecomposerRecipe>> DECOMPOSING_SERIALIZER = RECIPE_SERIALIZERS.register(DECOMPOSING_RECIPE_TYPE.getId(), DecomposerRecipe.Serializer::new);
	public static final RegistryObject<RecipeSerializer<BioLabRecipe>> BIO_BREWING_SERIALIZER = RECIPE_SERIALIZERS.register(BIO_BREWING_RECIPE_TYPE.getId(), BioLabRecipe.Serializer::new);
	public static final RegistryObject<RecipeSerializer<BioForgeRecipe>> BIO_FORGING_SERIALIZER = RECIPE_SERIALIZERS.register(BIO_FORGING_RECIPE_TYPE.getId(), BioForgeRecipe.Serializer::new);
	public static final RegistryObject<RecipeSerializer<DigesterRecipe>> DIGESTING_SERIALIZER = RECIPE_SERIALIZERS.register(DIGESTING_RECIPE_TYPE.getId(), DigesterRecipe.Serializer::new);

	private ModRecipes() {}

	public static void register() {
		registerRecipeTypes();
		registerItemPredicates();
		registerComposterRecipes();
	}

	private static void registerRecipeTypes() {
		registerRecipeType(BIO_BREWING_RECIPE_TYPE);
		registerRecipeType(DECOMPOSING_RECIPE_TYPE);
		registerRecipeType(DIGESTING_RECIPE_TYPE);
	}

	private static void registerItemPredicates() {
//		ItemPredicate.register(BiomancyMod.createRL("any_meatless_food"), jsonObject -> ANY_MEATLESS_FOOD_ITEM_PREDICATE);
	}

	private static void registerComposterRecipes() {
		ComposterBlock.COMPOSTABLES.putIfAbsent(ModItems.ORGANIC_MATTER.get(), 0.25f);
	}

	private static <T extends AbstractProductionRecipe> ItemStackRecipeType<T> createItemStackRecipeType(String identifier) {
		return new ItemStackRecipeType<>(identifier);
	}

	private static <T extends Recipe<Container>> SimpleRecipeType<T> createSimpleRecipeType(String identifier) {
		return new SimpleRecipeType<>(identifier);
	}

	private static void registerRecipeType(RecipeTypeImpl<?> recipeType) {
		Registry.register(Registry.RECIPE_TYPE, BiomancyMod.createRL(recipeType.getId()), recipeType);
	}

}
