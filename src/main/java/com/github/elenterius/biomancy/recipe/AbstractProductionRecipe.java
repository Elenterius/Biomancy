package com.github.elenterius.biomancy.recipe;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.items.ItemHandlerHelper;

import javax.annotation.Nullable;

public abstract class AbstractProductionRecipe implements IRecipe<IInventory> {

	private final ResourceLocation registryKey;
	private final int time;

	public AbstractProductionRecipe(ResourceLocation registryKeyIn, int craftingTimeIn) {
		registryKey = registryKeyIn;
		time = craftingTimeIn;
	}

	@Override
	public ResourceLocation getId() {
		return registryKey;
	}

	public int getCraftingTime() {
		return time;
	}

	public boolean areRecipesEqual(AbstractProductionRecipe other, boolean relaxed) {
		boolean flag = registryKey.equals(other.getId());
		if (!relaxed && !ItemHandlerHelper.canItemStacksStack(getResultItem(), other.getResultItem())) {
			return false;
		}
		return flag;
	}

	public static boolean areRecipesEqual(AbstractProductionRecipe recipeA, AbstractProductionRecipe recipeB, boolean relaxed) {
		return recipeA.areRecipesEqual(recipeB, relaxed);
	}

	public static abstract class FluidInput extends AbstractProductionRecipe implements IFluidIngredientRecipe {

		public FluidInput(ResourceLocation registryKeyIn, int craftingTimeIn) {
			super(registryKeyIn, craftingTimeIn);
		}

		@Override
		public boolean matches(IInventory inv, World worldIn) {
			return false;
		}

		@Override
		public boolean areRecipesEqual(AbstractProductionRecipe other, boolean relaxed) {
			if (!(other instanceof FluidInput)) return false;
			return super.areRecipesEqual(other, relaxed);
		}

		public abstract ItemStack getFluidCraftingResult();

		@Override
		public ItemStack assemble(@Nullable IInventory inv) {
			return getFluidCraftingResult();
		}

		@Override
		public boolean canCraftInDimensions(int width, int height) {
			return true;
		}

		@Override
		public NonNullList<Ingredient> getIngredients() {
			return NonNullList.create();
		}

	}
}
