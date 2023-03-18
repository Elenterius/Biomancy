package com.github.elenterius.biomancy.world.block.biolab;

import com.github.elenterius.biomancy.recipe.BioLabRecipe;
import com.github.elenterius.biomancy.util.fuel.IFuelHandler;
import com.github.elenterius.biomancy.world.block.state.RecipeCraftingStateData;

public class BioLabStateData extends RecipeCraftingStateData<BioLabRecipe> {

	public static final int FUEL_INDEX = 2;
	public final IFuelHandler fuelHandler;

	public BioLabStateData(IFuelHandler fuelHandler) {
		this.fuelHandler = fuelHandler;
	}

	@Override
	protected Class<BioLabRecipe> getRecipeType() {
		return BioLabRecipe.class;
	}

	@Override
	public int getFuelCost() {
		return fuelHandler.getFuelCost(timeForCompletion);
	}

	@Override
	public int get(int index) {
		validateIndex(index);
		if (index == TIME_INDEX) return timeElapsed;
		else if (index == TIME_FOR_COMPLETION_INDEX) return timeForCompletion;
		else if (index == FUEL_INDEX) return fuelHandler.getFuelAmount();
		return 0;
	}

	@Override
	public void set(int index, int value) {
		validateIndex(index);
		if (index == TIME_INDEX) timeElapsed = value;
		else if (index == TIME_FOR_COMPLETION_INDEX) timeForCompletion = value;
		else if (index == FUEL_INDEX) fuelHandler.setFuelAmount(value);
	}

	@Override
	public int getCount() {
		return 3;
	}

}
