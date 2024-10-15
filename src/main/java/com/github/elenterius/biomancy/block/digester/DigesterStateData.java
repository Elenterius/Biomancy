package com.github.elenterius.biomancy.block.digester;

import com.github.elenterius.biomancy.api.nutrients.FuelHandler;
import com.github.elenterius.biomancy.crafting.recipe.DigestingRecipe;
import com.github.elenterius.biomancy.crafting.state.FuelConsumingRecipeCraftingStateData;
import net.minecraft.world.item.crafting.Recipe;

public class DigesterStateData extends FuelConsumingRecipeCraftingStateData<DigestingRecipe> {

	public DigesterStateData(FuelHandler fuelHandler) {
		super(fuelHandler);
	}

	@Override
	protected boolean isRecipeOfInstance(Recipe<?> recipe) {
		return recipe instanceof DigestingRecipe;
	}

}
