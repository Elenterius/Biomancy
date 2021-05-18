package com.github.elenterius.biomancy.tileentity.state;

import com.github.elenterius.biomancy.recipe.DigesterRecipe;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.IIntArray;

public class DigesterStateData extends RecipeCraftingStateData<DigesterRecipe> implements IIntArray {

	public static final String NBT_KEY_TIME_ELAPSED = "TimeElapsed";
	public static final String NBT_KEY_TIME_FOR_COMPLETION = "TimeForCompletion";
	public static final String NBT_KEY_FUEL = "Fuel";

	public static final int TIME_INDEX = 0;
	public static final int TIME_FOR_COMPLETION_INDEX = 1;
	public static final int FUEL_INDEX = 2;

	public int timeElapsed;
	public int timeForCompletion;
	public short fuel; //water

	@Override
	Class<DigesterRecipe> getRecipeType() {
		return DigesterRecipe.class;
	}

	@Override
	public void setCraftingGoalRecipe(DigesterRecipe recipe) {
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
		nbt.putInt(NBT_KEY_TIME_ELAPSED, timeElapsed);
		nbt.putInt(NBT_KEY_TIME_FOR_COMPLETION, timeForCompletion);
		nbt.putShort(NBT_KEY_FUEL, fuel);
	}

	@Override
	public void deserializeNBT(CompoundNBT nbt) {
		super.deserializeNBT(nbt);
		timeElapsed = nbt.getInt(NBT_KEY_TIME_ELAPSED);
		timeForCompletion = nbt.getInt(NBT_KEY_TIME_FOR_COMPLETION);
		fuel = nbt.getShort(NBT_KEY_FUEL);
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
