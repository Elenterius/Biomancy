package com.github.elenterius.biomancy.block.digester;

import com.github.elenterius.biomancy.crafting.recipe.DigestingRecipe;
import com.github.elenterius.biomancy.crafting.state.FuelConsumingRecipeCraftingStateData;
import com.github.elenterius.biomancy.util.fuel.IFuelHandler;
import net.minecraft.world.Container;
import net.minecraft.world.item.crafting.Recipe;

public class DigesterStateData extends FuelConsumingRecipeCraftingStateData<DigestingRecipe, Container> {

	public DigesterStateData(IFuelHandler fuelHandler) {
		super(fuelHandler);
	}

	@Override
	protected boolean isRecipeOfInstance(Recipe<?> recipe) {
		return recipe instanceof DigestingRecipe;
	}

}
