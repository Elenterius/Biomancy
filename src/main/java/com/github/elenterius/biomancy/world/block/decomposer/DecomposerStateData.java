package com.github.elenterius.biomancy.world.block.decomposer;

import com.github.elenterius.biomancy.recipe.DecomposerRecipe;
import com.github.elenterius.biomancy.util.fuel.IFuelHandler;
import com.github.elenterius.biomancy.world.block.state.RecipeCraftingStateData;

public class DecomposerStateData extends RecipeCraftingStateData<DecomposerRecipe> {

	public static final int FUEL_INDEX = 2;

	public final IFuelHandler fuelHandler;

	public DecomposerStateData(IFuelHandler fuelHandler) {
		this.fuelHandler = fuelHandler;
	}

	@Override
	protected Class<DecomposerRecipe> getRecipeType() {
		return DecomposerRecipe.class;
	}

	@Override
	public int getFuelCost() {
		return fuelHandler.getFuelCost(timeForCompletion);
	}

	@Override
	public int get(int index) {
		validateIndex(index);
		return switch (index) {
			case TIME_INDEX -> timeElapsed;
			case TIME_FOR_COMPLETION_INDEX -> timeForCompletion;
			case FUEL_INDEX -> fuelHandler.getFuelAmount();
			default -> 0;
		};
	}

	@Override
	public void set(int index, int value) {
		validateIndex(index);
		switch (index) {
			case TIME_INDEX -> timeElapsed = value;
			case TIME_FOR_COMPLETION_INDEX -> timeForCompletion = value;
			case FUEL_INDEX -> fuelHandler.setFuelAmount(value);
			default -> throw new IllegalStateException("Unexpected value: " + index);
		}
	}

	@Override
	public int getCount() {
		return 3;
	}

}
