package com.github.elenterius.biomancy.world.block.entity.state;

import com.github.elenterius.biomancy.recipe.DecomposerRecipe;
import net.minecraft.nbt.CompoundTag;

public class DecomposerStateData extends RecipeCraftingStateData<DecomposerRecipe> {

	public static final String NBT_KEY_FUEL = "Fuel";
	public static final int FUEL_AMOUNT_INDEX = 2;

	private int fuelAmount;

	@Override
	Class<DecomposerRecipe> getRecipeType() {
		return DecomposerRecipe.class;
	}

	@Override
	public void serialize(CompoundTag nbt) {
		super.serialize(nbt);
		if (fuelAmount > 0) nbt.putInt(NBT_KEY_FUEL, fuelAmount);
	}

	@Override
	public void deserialize(CompoundTag nbt) {
		super.deserialize(nbt);
		fuelAmount = nbt.getInt(NBT_KEY_FUEL);
	}

	@Override
	public int get(int index) {
		validateIndex(index);
		return switch (index) {
			case TIME_INDEX -> timeElapsed;
			case TIME_FOR_COMPLETION_INDEX -> timeForCompletion;
			case FUEL_AMOUNT_INDEX -> fuelAmount;
			default -> 0;
		};
	}

	@Override
	public void set(int index, int value) {
		validateIndex(index);
		switch (index) {
			case TIME_INDEX -> timeElapsed = value;
			case TIME_FOR_COMPLETION_INDEX -> timeForCompletion = value;
			case FUEL_AMOUNT_INDEX -> fuelAmount = value;
			default -> throw new IllegalStateException("Unexpected value: " + index);
		}
	}

	@Override
	public int getCount() {
		return 3;
	}

	public int getFuelAmount() {
		return fuelAmount;
	}

	public void setFuelAmount(int value) {
		fuelAmount = value;
	}

}
