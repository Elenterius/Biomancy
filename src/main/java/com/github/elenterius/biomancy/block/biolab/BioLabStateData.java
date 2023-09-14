package com.github.elenterius.biomancy.block.biolab;

import com.github.elenterius.biomancy.block.state.FuelConsumingRecipeCraftingStateData;
import com.github.elenterius.biomancy.recipe.BioLabRecipe;
import com.github.elenterius.biomancy.util.fuel.IFuelHandler;

public class BioLabStateData extends FuelConsumingRecipeCraftingStateData<BioLabRecipe> {

	public BioLabStateData(IFuelHandler fuelHandler) {
		super(fuelHandler);
	}

	@Override
	protected Class<BioLabRecipe> getRecipeType() {
		return BioLabRecipe.class;
	}

}
