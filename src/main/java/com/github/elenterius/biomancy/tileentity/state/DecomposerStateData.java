package com.github.elenterius.biomancy.tileentity.state;

import com.github.elenterius.biomancy.recipe.DecomposerRecipe;
import net.minecraft.nbt.CompoundNBT;

public class DecomposerStateData extends RecipeCraftingStateData<DecomposerRecipe> {

	public static final int FUEL_INDEX = 2;
	public static final int SPEED_FUEL_INDEX = 3;

	public static final String NBT_KEY_MAIN_FUEL = "MainFuel";
	public static final String NBT_KEY_SPEED_FUEL = "SpeedFuel";

	public short fuel; //raw-meat (fake "saturation", we use the food healing value instead)
	public short speedFuel; //glucose ("candy", food that contains sugar)

	@Override
	Class<DecomposerRecipe> getRecipeType() {
		return DecomposerRecipe.class;
	}

	@Override
	public void serializeNBT(CompoundNBT nbt) {
		super.serializeNBT(nbt);
		if (fuel > 0) nbt.putShort(NBT_KEY_MAIN_FUEL, fuel);
		if (speedFuel > 0) nbt.putShort(NBT_KEY_SPEED_FUEL, speedFuel);
	}

	@Override
	public void deserializeNBT(CompoundNBT nbt) {
		super.deserializeNBT(nbt);
		fuel = nbt.getShort(NBT_KEY_MAIN_FUEL);
		speedFuel = nbt.getShort(NBT_KEY_SPEED_FUEL);
	}

	@Override
	public int get(int index) {
		validateIndex(index);
		if (index == TIME_INDEX) return timeElapsed;
		else if (index == TIME_FOR_COMPLETION_INDEX) return timeForCompletion;
		else if (index == FUEL_INDEX) return fuel;
		else if (index == SPEED_FUEL_INDEX) return speedFuel;
		else return 0;
	}

	@Override
	public void set(int index, int value) {
		validateIndex(index);
		if (index == TIME_INDEX) timeElapsed = value;
		else if (index == TIME_FOR_COMPLETION_INDEX) timeForCompletion = value;
		else if (index == FUEL_INDEX) fuel = (short) value;
		else if (index == SPEED_FUEL_INDEX) speedFuel = (short) value;
	}

	@Override
	public int size() {
		return 4;
	}
}
