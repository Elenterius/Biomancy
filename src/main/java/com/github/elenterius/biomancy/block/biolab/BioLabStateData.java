package com.github.elenterius.biomancy.block.biolab;

import com.github.elenterius.biomancy.crafting.recipe.BioLabRecipe;
import com.github.elenterius.biomancy.crafting.state.FuelConsumingRecipeCraftingStateData;
import com.github.elenterius.biomancy.util.fuel.IFuelHandler;
import net.minecraft.world.Container;
import net.minecraft.world.item.crafting.Recipe;

public class BioLabStateData extends FuelConsumingRecipeCraftingStateData<BioLabRecipe, Container> {

	public BioLabStateData(IFuelHandler fuelHandler) {
		super(fuelHandler);
	}

	@Override
	protected boolean isRecipeOfInstance(Recipe<?> recipe) {
		return recipe instanceof BioLabRecipe;
	}

}
