package com.github.elenterius.biomancy.world.block.digester;

import com.github.elenterius.biomancy.recipe.DigesterRecipe;
import com.github.elenterius.biomancy.util.fuel.IFuelHandler;
import com.github.elenterius.biomancy.world.block.state.RecipeCraftingStateData;

public class DigesterStateData extends RecipeCraftingStateData<DigesterRecipe> {

	public static final int FUEL_AMOUNT_INDEX = 2;

	public final IFuelHandler fuelHandler;

	public DigesterStateData(IFuelHandler fuelHandler) {
		this.fuelHandler = fuelHandler;
	}

	@Override
	protected Class<DigesterRecipe> getRecipeType() {
		return DigesterRecipe.class;
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
			case FUEL_AMOUNT_INDEX -> fuelHandler.getFuelAmount();
			default -> 0;
		};
	}

	@Override
	public void set(int index, int value) {
		validateIndex(index);
		switch (index) {
			case TIME_INDEX -> timeElapsed = value;
			case TIME_FOR_COMPLETION_INDEX -> timeForCompletion = value;
			case FUEL_AMOUNT_INDEX -> fuelHandler.setFuelAmount(value);
			default -> throw new IllegalStateException("Unexpected value: " + index);
		}
	}

	@Override
	public int getCount() {
		return 3;
	}

}
