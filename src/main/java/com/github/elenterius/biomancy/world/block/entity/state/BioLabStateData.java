package com.github.elenterius.biomancy.world.block.entity.state;

import com.github.elenterius.biomancy.recipe.BioLabRecipe;
import net.minecraft.nbt.CompoundTag;

public class BioLabStateData extends RecipeCraftingStateData<BioLabRecipe> {

	public static final int FUEL_INDEX = 2;
	public static final String NBT_KEY_FUEL = "Fuel";
	private short fuelAmount;

	public int getFuelAmount() {
		return fuelAmount;
	}

	public void setFuelAmount(short value) {
		fuelAmount = value;
	}

	@Override
	Class<BioLabRecipe> getRecipeType() {
		return BioLabRecipe.class;
	}

	@Override
	public void serialize(CompoundTag nbt) {
		super.serialize(nbt);
		nbt.putShort(NBT_KEY_FUEL, fuelAmount);
	}

	@Override
	public void deserialize(CompoundTag nbt) {
		super.deserialize(nbt);
		fuelAmount = nbt.getShort(NBT_KEY_FUEL);
	}

	@Override
	public int get(int index) {
		validateIndex(index);
		if (index == TIME_INDEX) return timeElapsed;
		else if (index == TIME_FOR_COMPLETION_INDEX) return timeForCompletion;
		else if (index == FUEL_INDEX) return fuelAmount;
		return 0;
	}

	@Override
	public void set(int index, int value) {
		validateIndex(index);
		if (index == TIME_INDEX) timeElapsed = value;
		else if (index == TIME_FOR_COMPLETION_INDEX) timeForCompletion = value;
		else if (index == FUEL_INDEX) fuelAmount = (short) value;
	}

	@Override
	public int getCount() {
		return 3;
	}

}
