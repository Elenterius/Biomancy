package com.github.elenterius.biomancy.init;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.recipe.*;
import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import net.minecraft.advancements.criterion.ItemPredicate;
import net.minecraft.block.ComposterBlock;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.SpecialRecipeSerializer;
import net.minecraft.util.registry.Registry;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public final class ModRecipes {

	public static final DeferredRegister<IRecipeSerializer<?>> RECIPE_SERIALIZERS = DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, BiomancyMod.MOD_ID);

	public static final RegistryObject<SpecialRecipeSerializer<AddPotionToBoomlingRecipe>> CRAFTING_SPECIAL_BOOMLING = RECIPE_SERIALIZERS.register("crafting_special_boomling", () -> new SpecialRecipeSerializer<>(AddPotionToBoomlingRecipe::new));
	public static final RegistryObject<SpecialRecipeSerializer<AddPotionToBoomlingGunRecipe>> CRAFTING_SPECIAL_BOOMLING_GUN = RECIPE_SERIALIZERS.register("crafting_special_boomling_gun", () -> new SpecialRecipeSerializer<>(AddPotionToBoomlingGunRecipe::new));
	public static final RegistryObject<SpecialRecipeSerializer<AddUserToAccessKeyRecipe>> CRAFTING_SPECIAL_ADD_USER_TO_KEY = RECIPE_SERIALIZERS.register("crafting_special_add_user_to_key", () -> new SpecialRecipeSerializer<>(AddUserToAccessKeyRecipe::new));

	public static final RegistryObject<IRecipeSerializer<ChewerRecipe>> CHEWER_SERIALIZER = RECIPE_SERIALIZERS.register("chewing", ChewerRecipe.Serializer::new);
	public static final RegistryObject<IRecipeSerializer<DigesterRecipe>> DIGESTER_SERIALIZER = RECIPE_SERIALIZERS.register("digesting", DigesterRecipe.Serializer::new);
	public static final RegistryObject<IRecipeSerializer<SolidifierRecipe>> SOLIDIFIER_SERIALIZER = RECIPE_SERIALIZERS.register("solidifying", SolidifierRecipe.Serializer::new);
	public static final RegistryObject<IRecipeSerializer<DecomposerRecipe>> DECOMPOSING_SERIALIZER = RECIPE_SERIALIZERS.register("decomposing", DecomposerRecipe.Serializer::new);
	public static final RegistryObject<IRecipeSerializer<EvolutionPoolRecipe>> EVOLUTION_POOL_SERIALIZER = RECIPE_SERIALIZERS.register("evolution_pool", EvolutionPoolRecipe.Serializer::new);

	public static final RecipeType.ItemStackRecipeType<ChewerRecipe> CHEWER_RECIPE_TYPE = createItemStackRecipeType("chewing");
	public static final RecipeType.ItemStackRecipeType<DigesterRecipe> DIGESTER_RECIPE_TYPE = createItemStackRecipeType("digesting");
	public static final RecipeType.FluidStackRecipeType<SolidifierRecipe> SOLIDIFIER_RECIPE_TYPE = createFluidStackRecipeType("solidifying");
	public static final RecipeType.ItemStackRecipeType<DecomposerRecipe> DECOMPOSING_RECIPE_TYPE = createItemStackRecipeType("decomposing");
	public static final RecipeType.ItemStackRecipeType<EvolutionPoolRecipe> EVOLUTION_POOL_RECIPE_TYPE = createItemStackRecipeType("evolution_pool");
	public static final ImmutableSet<IRecipeType<? extends IRecipe<IInventory>>> RECIPE_TYPES = ImmutableSet.of(CHEWER_RECIPE_TYPE, DIGESTER_RECIPE_TYPE, SOLIDIFIER_RECIPE_TYPE, DECOMPOSING_RECIPE_TYPE, EVOLUTION_POOL_RECIPE_TYPE);

	public static final ItemPredicate ANY_MEATLESS_FOOD_ITEM_PREDICATE = new ItemPredicate() {
		@Override
		public boolean matches(ItemStack stack) {
			Item item = stack.getItem();
			return item.isEdible() && item.getFoodProperties() != null && !item.getFoodProperties().isMeat();
		}

		@Override
		public JsonElement serializeToJson() {
			return JsonNull.INSTANCE;
		}
	};

	private ModRecipes() {}

	public static void registerComposterRecipes() {
		ComposterBlock.COMPOSTABLES.putIfAbsent(ModItems.DIGESTATE.get(), 0.7f);
		ComposterBlock.COMPOSTABLES.putIfAbsent(ModItems.NUTRIENT_PASTE.get(), 0.5f);
	}

	public static void registerCustomItemPredicates() {
		ItemPredicate.register(BiomancyMod.createRL("any_meatless_food"), jsonObject -> ANY_MEATLESS_FOOD_ITEM_PREDICATE);
	}

	public static void registerRecipeTypes() {
		Registry.register(Registry.RECIPE_TYPE, BiomancyMod.createRL("chewing"), CHEWER_RECIPE_TYPE);
		Registry.register(Registry.RECIPE_TYPE, BiomancyMod.createRL("digesting"), DIGESTER_RECIPE_TYPE);
		Registry.register(Registry.RECIPE_TYPE, BiomancyMod.createRL("solidifying"), SOLIDIFIER_RECIPE_TYPE);
		Registry.register(Registry.RECIPE_TYPE, BiomancyMod.createRL("decomposing"), DECOMPOSING_RECIPE_TYPE);
		Registry.register(Registry.RECIPE_TYPE, BiomancyMod.createRL("evolution_pool"), EVOLUTION_POOL_RECIPE_TYPE);
	}

	private static <T extends AbstractProductionRecipe> RecipeType.ItemStackRecipeType<T> createItemStackRecipeType(String name) {
		return new RecipeType.ItemStackRecipeType<>(name);
	}

	private static <T extends AbstractProductionRecipe.FluidInput> RecipeType.FluidStackRecipeType<T> createFluidStackRecipeType(String name) {
		return new RecipeType.FluidStackRecipeType<>(name);
	}

}
