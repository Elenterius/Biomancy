package com.github.elenterius.biomancy.tileentity.state;

import com.github.elenterius.biomancy.recipe.EvolutionPoolRecipe;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.IIntArray;

public class EvolutionPoolStateData extends RecipeCraftingStateData<EvolutionPoolRecipe> implements IIntArray {

	public static final int TIME_INDEX = 0;
	public static final int TIME_FOR_COMPLETION_INDEX = 1;
	public static final int FUEL_INDEX = 2;

	public int timeElapsed;
	public int timeForCompletion;
	public short fuel; //mutagenic bile

	@Override
	Class<EvolutionPoolRecipe> getRecipeType() {
		return EvolutionPoolRecipe.class;
	}

	@Override
	public void setCraftingGoalRecipe(EvolutionPoolRecipe recipe) {
		super.setCraftingGoalRecipe(recipe);
		timeForCompletion = recipe.getCraftingTime();
	}

	@Override
	public void clear() {
		timeElapsed = 0;
		timeForCompletion = 0;
		super.clear();
	}

	@Override
	public void serializeNBT(CompoundNBT nbt) {
		super.serializeNBT(nbt);
		nbt.putInt("TimeElapsed", timeElapsed);
		nbt.putInt("TimeForCompletion", timeForCompletion);
		nbt.putShort("Fuel", fuel);
	}

	@Override
	public void deserializeNBT(CompoundNBT nbt) {
		super.deserializeNBT(nbt);
		timeElapsed = nbt.getInt("TimeElapsed");
		timeForCompletion = nbt.getInt("TimeForCompletion");
		fuel = nbt.getShort("Fuel");
	}

	private void validateIndex(int index) {
		if (index < 0 || index >= size()) throw new IndexOutOfBoundsException("Index out of bounds:" + index);
	}

	@Override
	public int get(int index) {
		validateIndex(index);
		if (index == TIME_INDEX) return timeElapsed;
		else if (index == TIME_FOR_COMPLETION_INDEX) return timeForCompletion;
		else if (index == FUEL_INDEX) return fuel;
		return 0;
	}

	@Override
	public void set(int index, int value) {
		validateIndex(index);
		if (index == TIME_INDEX) timeElapsed = value;
		else if (index == TIME_FOR_COMPLETION_INDEX) timeForCompletion = value;
		else if (index == FUEL_INDEX) fuel = (short) value;
	}

	@Override
	public int size() {
		return 3;
	}

}
