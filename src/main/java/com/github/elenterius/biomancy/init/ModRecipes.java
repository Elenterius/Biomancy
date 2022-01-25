package com.github.elenterius.biomancy.init;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.recipe.AbstractProductionRecipe;
import com.github.elenterius.biomancy.recipe.BioForgeRecipe;
import com.github.elenterius.biomancy.recipe.DecomposerRecipe;
import com.github.elenterius.biomancy.recipe.RecipeTypeImpl;
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

	public static final RecipeTypeImpl.ItemStackRecipeType<DecomposerRecipe> DECOMPOSING_RECIPE_TYPE = createItemStackRecipeType("decomposing");
	public static final RecipeTypeImpl.ItemStackRecipeType<DecomposerRecipe> BIO_FORGING_RECIPE_TYPE = createItemStackRecipeType("bio_forging");
	public static final Set<RecipeType<? extends Recipe<Container>>> RECIPE_TYPES = Set.of(DECOMPOSING_RECIPE_TYPE, BIO_FORGING_RECIPE_TYPE);

	public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS = DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, BiomancyMod.MOD_ID);
	public static final RegistryObject<RecipeSerializer<DecomposerRecipe>> DECOMPOSING_SERIALIZER = RECIPE_SERIALIZERS.register(DECOMPOSING_RECIPE_TYPE.getId(), DecomposerRecipe.Serializer::new);
	public static final RegistryObject<RecipeSerializer<BioForgeRecipe>> BIO_FORGING_SERIALIZER = RECIPE_SERIALIZERS.register(BIO_FORGING_RECIPE_TYPE.getId(), BioForgeRecipe.Serializer::new);


//	public static final ItemPredicate ANY_MEATLESS_FOOD_ITEM_PREDICATE = new ItemPredicate() {
//		@Override
//		public boolean matches(ItemStack stack) {
//			Item item = stack.getItem();
//			return item.isEdible() && item.getFoodProperties() != null && !item.getFoodProperties().isMeat();
//		}
//
//		@Override
//		public JsonElement serializeToJson() {
//			return JsonNull.INSTANCE;
//		}
//	};

	private ModRecipes() {}

	public static void register() {
		registerRecipeTypes();
		registerItemPredicates();
		registerComposterRecipes();
	}

	private static <T extends AbstractProductionRecipe> RecipeTypeImpl.ItemStackRecipeType<T> createItemStackRecipeType(String identifier) {
		return new RecipeTypeImpl.ItemStackRecipeType<>(identifier);
	}

	private static void registerRecipeTypes() {
		Registry.register(Registry.RECIPE_TYPE, BiomancyMod.createRL(DECOMPOSING_RECIPE_TYPE.getId()), DECOMPOSING_RECIPE_TYPE);
	}

	private static void registerItemPredicates() {
//		ItemPredicate.register(BiomancyMod.createRL("any_meatless_food"), jsonObject -> ANY_MEATLESS_FOOD_ITEM_PREDICATE);
	}

	private static void registerComposterRecipes() {
		ComposterBlock.COMPOSTABLES.putIfAbsent(ModItems.PLANT_MATTER.get(), 0.25f);
	}

}
