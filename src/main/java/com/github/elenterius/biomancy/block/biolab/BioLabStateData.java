package com.github.elenterius.biomancy.block.biolab;

import com.github.elenterius.biomancy.crafting.recipe.BioLabRecipe;
import com.github.elenterius.biomancy.crafting.state.FuelConsumingRecipeCraftingStateData;
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
