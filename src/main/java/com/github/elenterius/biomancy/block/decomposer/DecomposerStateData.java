package com.github.elenterius.biomancy.block.decomposer;

import com.github.elenterius.biomancy.api.nutrients.FuelHandler;
import com.github.elenterius.biomancy.crafting.recipe.DecomposingRecipe;
import com.github.elenterius.biomancy.crafting.state.FuelConsumingRecipeCraftingStateData;
import net.minecraft.world.item.crafting.Recipe;

public class DecomposerStateData extends FuelConsumingRecipeCraftingStateData<DecomposingRecipe> {

	public DecomposerStateData(FuelHandler fuelHandler) {
		super(fuelHandler);
	}

	@Override
	protected boolean isRecipeOfInstance(Recipe<?> recipe) {
		return recipe instanceof DecomposingRecipe;
	}

}
