package com.github.elenterius.biomancy.block.digester;

import com.github.elenterius.biomancy.block.state.FuelConsumingRecipeCraftingStateData;
import com.github.elenterius.biomancy.recipe.DigesterRecipe;
import com.github.elenterius.biomancy.util.fuel.IFuelHandler;

public class DigesterStateData extends FuelConsumingRecipeCraftingStateData<DigesterRecipe> {

	public DigesterStateData(IFuelHandler fuelHandler) {
		super(fuelHandler);
	}

	@Override
	protected Class<DigesterRecipe> getRecipeType() {
		return DigesterRecipe.class;
	}

}
