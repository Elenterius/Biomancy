package com.github.elenterius.biomancy.block.decomposer;

import com.github.elenterius.biomancy.crafting.recipe.DecomposerRecipe;
import com.github.elenterius.biomancy.crafting.state.FuelConsumingRecipeCraftingStateData;
import com.github.elenterius.biomancy.util.fuel.IFuelHandler;

public class DecomposerStateData extends FuelConsumingRecipeCraftingStateData<DecomposerRecipe> {

	public DecomposerStateData(IFuelHandler fuelHandler) {
		super(fuelHandler);
	}

	@Override
	protected Class<DecomposerRecipe> getRecipeType() {
		return DecomposerRecipe.class;
	}

}
