package com.github.elenterius.biomancy.integration.jei;

import com.github.elenterius.biomancy.block.digester.DigesterBlockEntity;
import com.github.elenterius.biomancy.crafting.recipe.DigestingRecipe;
import com.github.elenterius.biomancy.crafting.recipe.FoodDigestingRecipe;
import com.github.elenterius.biomancy.crafting.recipe.StaticDigestingRecipe;
import com.github.elenterius.biomancy.init.ModRecipes;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.wrapper.RecipeWrapper;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.List;

public final class DigestingRecipes {

	private DigestingRecipes() {}

	public static List<DigestingRecipe> getRecipes(ClientLevel level) {
		List<DigestingRecipe> allRecipes = level.getRecipeManager().getAllRecipesFor(ModRecipes.DIGESTING_RECIPE_TYPE.get());

		List<DigestingRecipe> resolvedRecipes = new ArrayList<>();

		for (DigestingRecipe recipe : allRecipes) {
			if (recipe instanceof FoodDigestingRecipe dynamicRecipe) {
				List<DigestingRecipe> staticRecipes = convertToStaticRecipes(level, dynamicRecipe);
				resolvedRecipes.addAll(staticRecipes);
			}
			else {
				resolvedRecipes.add(recipe);
			}
		}

		return resolvedRecipes;
	}

	private static List<DigestingRecipe> convertToStaticRecipes(ClientLevel level, FoodDigestingRecipe dynamicRecipe) {
		List<DigestingRecipe> staticRecipes = new ArrayList<>();

		RecipeWrapper inputInventory = new RecipeWrapper(new ItemStackHandler(DigesterBlockEntity.INPUT_SLOTS));

		for (ItemStack ingredientItem : dynamicRecipe.getIngredient().getItems()) {
			inputInventory.setItem(0, ingredientItem);

			ItemStack result = dynamicRecipe.assemble(inputInventory, level.registryAccess());
			int craftingTimeTicks = dynamicRecipe.getCraftingTimeTicks(inputInventory);
			int craftingCostNutrients = dynamicRecipe.getCraftingCostNutrients(inputInventory);
			Ingredient ingredient = Ingredient.of(ingredientItem);

			String suffix = ForgeRegistries.ITEMS.getKey(ingredientItem.getItem()).toLanguageKey();
			StaticDigestingRecipe recipe = new StaticDigestingRecipe(dynamicRecipe.getId().withSuffix("_jei_" + suffix), result, craftingTimeTicks, craftingCostNutrients, ingredient);

			staticRecipes.add(recipe);
		}

		return staticRecipes;
	}

}
