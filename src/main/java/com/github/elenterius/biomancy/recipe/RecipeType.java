package com.github.elenterius.biomancy.recipe;

import com.github.elenterius.biomancy.mixin.RecipeManagerMixinAccessor;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.RecipeManager;
import net.minecraft.util.Util;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;

import java.util.Optional;

public abstract class RecipeType<T extends AbstractProductionRecipe> implements IRecipeType<T> {
	private final String name;

	public RecipeType(String nameIn) {
		this.name = nameIn;
	}

	@Override
	public String toString() {
		return name;
	}

	public static class ItemStackRecipeType<R extends AbstractProductionRecipe> extends RecipeType<R> {

		public ItemStackRecipeType(String nameIn) {
			super(nameIn);
		}

		public Optional<R> getRecipeFromInventory(World world, IInventory inputInv) {
			RecipeManager recipeManager = world.getRecipeManager();
			return recipeManager.getRecipe(this, inputInv, world);
		}

		public Optional<R> getRecipeForItem(World world, ItemStack stack) {
			RecipeManagerMixinAccessor recipeManager = (RecipeManagerMixinAccessor) world.getRecipeManager();
			//noinspection unchecked
			return recipeManager.biomancy_getRecipes(this).values().stream().map(recipe -> (R) recipe)
					.filter(recipe -> {
						for (Ingredient ingredient : recipe.getIngredients()) {
							if (ingredient.test(stack)) return true;
						}
						return false;
					}).findFirst();
		}
	}

	public static class FluidStackRecipeType<R extends AbstractProductionRecipe.FluidInput> extends RecipeType<R> {

		public FluidStackRecipeType(String nameIn) {
			super(nameIn);
		}

		public Optional<R> matches(R recipe, World worldIn, IFluidHandler fluidHandler) {
			return recipe.matches(fluidHandler, worldIn) ? Optional.of(recipe) : Optional.empty();
		}

		public Optional<R> getRecipeFromFluidTank(World world, IFluidHandler fluidHandler) {
			RecipeManagerMixinAccessor recipeManager = (RecipeManagerMixinAccessor) world.getRecipeManager();
			//noinspection unchecked
			return recipeManager.biomancy_getRecipes(this).values().stream()
					.flatMap((recipe) -> Util.streamOptional(matches((R) recipe, world, fluidHandler)))
					.findFirst();
		}

		public Optional<R> getRecipeForFluid(World world, FluidStack stack) {
			RecipeManagerMixinAccessor recipeManager = (RecipeManagerMixinAccessor) world.getRecipeManager();
			//noinspection unchecked
			return recipeManager.biomancy_getRecipes(this).values().stream().map(recipe -> (R) recipe)
					.filter(recipe -> recipe.getFluidIngredient().test(stack))
					.findFirst();
		}

	}
}
