package com.github.elenterius.biomancy.crafting.state;

import com.github.elenterius.biomancy.crafting.recipe.ProcessingRecipe;
import com.github.elenterius.biomancy.util.fuel.IFuelHandler;
import net.minecraft.world.Container;

public abstract class FuelConsumingRecipeCraftingStateData<T extends ProcessingRecipe<C>, C extends Container> extends RecipeCraftingStateData<T, C> {

	public static final int FUEL_INDEX = 3;

	public final IFuelHandler fuelHandler;

	protected FuelConsumingRecipeCraftingStateData(IFuelHandler fuelHandler) {
		this.fuelHandler = fuelHandler;
	}

	@Override
	public int getFuelCost() {
		return fuelHandler.getFuelCost(nutrientsCost);
	}

	@Override
	public int get(int index) {
		if (index == FUEL_INDEX) return fuelHandler.getFuelAmount();
		return super.get(index);
	}

	@Override
	public void set(int index, int value) {
		if (index == FUEL_INDEX) {
			fuelHandler.setFuelAmount(value);
			return;
		}
		super.set(index, value);
	}

	@Override
	public int getCount() {
		return 4;
	}
}
