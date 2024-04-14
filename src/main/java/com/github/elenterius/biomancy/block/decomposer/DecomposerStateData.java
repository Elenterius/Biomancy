package com.github.elenterius.biomancy.block.decomposer;

import com.github.elenterius.biomancy.crafting.recipe.DecomposerRecipe;
import com.github.elenterius.biomancy.crafting.state.FuelConsumingRecipeCraftingStateData;
import com.github.elenterius.biomancy.util.fuel.IFuelHandler;
import net.minecraft.world.Container;
import net.minecraft.world.item.crafting.Recipe;

public class DecomposerStateData extends FuelConsumingRecipeCraftingStateData<DecomposerRecipe, Container> {

	public DecomposerStateData(IFuelHandler fuelHandler) {
		super(fuelHandler);
	}

	@Override
	protected boolean isRecipeOfInstance(Recipe<?> recipe) {
		return recipe instanceof DecomposerRecipe;
	}

}
