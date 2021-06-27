package com.github.elenterius.biomancy.recipe;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.items.ItemHandlerHelper;

public abstract class AbstractBioMechanicalRecipe implements IRecipe<IInventory> {

	private final ResourceLocation registryKey;
	private final int time;

	public AbstractBioMechanicalRecipe(ResourceLocation registryKeyIn, int craftingTimeIn) {
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

	public boolean areRecipesEqual(AbstractBioMechanicalRecipe other, boolean relaxed) {
		boolean flag = registryKey.equals(other.getId());
		if (!relaxed && !ItemHandlerHelper.canItemStacksStack(getRecipeOutput(), other.getRecipeOutput())) {
			return false;
		}
		return flag;
	}

	public static boolean areRecipesEqual(AbstractBioMechanicalRecipe recipeA, AbstractBioMechanicalRecipe recipeB, boolean relaxed) {
		return recipeA.areRecipesEqual(recipeB, relaxed);
	}
}
