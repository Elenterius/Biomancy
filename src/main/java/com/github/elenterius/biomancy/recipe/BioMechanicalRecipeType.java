package com.github.elenterius.biomancy.recipe;

import com.github.elenterius.biomancy.mixin.RecipeManagerMixinAccessor;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.RecipeManager;
import net.minecraft.world.World;

import java.util.Optional;

public class BioMechanicalRecipeType<T extends AbstractBioMechanicalRecipe> implements IRecipeType<T> {
	private final String name;

	public BioMechanicalRecipeType(String nameIn) {
		this.name = nameIn;
	}

	@Override
	public String toString() {
		return name;
	}

	public Optional<T> getRecipeFromInventory(World world, IInventory inputInv) {
		RecipeManager recipeManager = world.getRecipeManager();
		return recipeManager.getRecipe(this, inputInv, world);
	}

	public Optional<T> getRecipeForItem(World world, ItemStack stack) {
		RecipeManagerMixinAccessor recipeManager = (RecipeManagerMixinAccessor) world.getRecipeManager();
		//noinspection unchecked
		return recipeManager.callGetRecipes(this).values().stream().map(recipe -> (T) recipe)
				.filter(recipe -> {
					for (Ingredient ingredient : recipe.getIngredients()) {
						if (ingredient.test(stack)) return true;
					}
					return false;
				}).findFirst();
	}
}
