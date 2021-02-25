package com.github.elenterius.biomancy.tileentity.state;

import com.github.elenterius.biomancy.recipe.DecomposingRecipe;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.IIntArray;

public class DecomposerStateData extends RecipeCraftingStateData<DecomposingRecipe> implements IIntArray {

	public static final int TIME_INDEX = 0;
	public static final int TIME_FOR_COMPLETION_INDEX = 1;
	public static final int FUEL_INDEX = 2;
	public static final int SPEED_FUEL_INDEX = 3;

	public int timeElapsed;
	public int timeForCompletion;
	public short mainFuel; //raw-meat (fake "saturation", we use the food healing value instead)
	public short speedFuel; //glucose ("candy", food that contains sugar)

	@Override
	Class<DecomposingRecipe> getRecipeType() {
		return DecomposingRecipe.class;
	}

	@Override
	public void setCraftingGoalRecipe(DecomposingRecipe recipe) {
		super.setCraftingGoalRecipe(recipe);
		timeForCompletion = recipe.getDecomposingTime();
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
		if (timeElapsed > 0) nbt.putInt("TimeElapsed", timeElapsed);
		if (timeForCompletion > 0) nbt.putInt("TimeForCompletion", timeForCompletion);
		if (mainFuel > 0) nbt.putShort("MainFuel", mainFuel);
		if (speedFuel > 0) nbt.putShort("SpeedFuel", speedFuel);
	}

	@Override
	public void deserializeNBT(CompoundNBT nbt) {
		super.deserializeNBT(nbt);
		timeElapsed = nbt.getInt("TimeElapsed");
		timeForCompletion = nbt.getInt("TimeForCompletion");
		mainFuel = nbt.getShort("MainFuel");
		speedFuel = nbt.getShort("SpeedFuel");
	}

	@Override
	public int get(int index) {
		validateIndex(index);
		if (index == TIME_INDEX) return timeElapsed;
		else if (index == TIME_FOR_COMPLETION_INDEX) return timeForCompletion;
		else if (index == FUEL_INDEX) return mainFuel;
		else if (index == SPEED_FUEL_INDEX) return speedFuel;
		else return 0;
	}

	@Override
	public void set(int index, int value) {
		validateIndex(index);
		if (index == TIME_INDEX) timeElapsed = value;
		else if (index == TIME_FOR_COMPLETION_INDEX) timeForCompletion = value;
		else if (index == FUEL_INDEX) mainFuel = (short) value;
		else if (index == SPEED_FUEL_INDEX) speedFuel = (short) value;
	}

	private void validateIndex(int index) {
		if (index < 0 || index >= size()) throw new IndexOutOfBoundsException("Index out of bounds:" + index);
	}

	@Override
	public int size() {
		return 4;
	}
}
