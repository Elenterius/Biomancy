package com.github.elenterius.biomancy.block.biolab;

import com.github.elenterius.biomancy.api.nutrients.FuelHandler;
import com.github.elenterius.biomancy.crafting.recipe.BioLabRecipe;
import com.github.elenterius.biomancy.crafting.state.FuelConsumingRecipeCraftingStateData;
import com.github.elenterius.biomancy.inventory.BehavioralItemHandler;
import net.minecraft.world.item.crafting.Recipe;

public class BioLabStateData extends FuelConsumingRecipeCraftingStateData<BioLabRecipe> {

	public static final int LOCK_INDEX = 4;

	private final BehavioralItemHandler.LockableItemStackFilterInput inputFilterLock;

	public BioLabStateData(FuelHandler fuelHandler, BehavioralItemHandler.LockableItemStackFilterInput inputFilterLock) {
		super(fuelHandler);
		this.inputFilterLock = inputFilterLock;
	}

	@Override
	protected boolean isRecipeOfInstance(Recipe<?> recipe) {
		return recipe instanceof BioLabRecipe;
	}

	public boolean isFilterLocked() {
		return inputFilterLock.isLocked();
	}

	@Override
	public int get(int index) {
		if (index == LOCK_INDEX) return inputFilterLock.isLocked() ? 1 : 0;
		return super.get(index);
	}

	@Override
	public void set(int index, int value) {
		if (index == LOCK_INDEX) {
			inputFilterLock.setLocked(value != 0);
			return;
		}
		super.set(index, value);
	}

	@Override
	public int getCount() {
		return 5;
	}

}
