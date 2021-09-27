package com.github.elenterius.biomancy.tileentity.state;

import com.github.elenterius.biomancy.recipe.EvolutionPoolRecipe;
import net.minecraft.nbt.CompoundNBT;

public class EvolutionPoolStateData extends RecipeCraftingStateData<EvolutionPoolRecipe> {

	public static final int FUEL_INDEX = 2;
	public static final String NBT_KEY_FUEL = "Fuel";
	public short fuel; //mutagenic bile

	@Override
	Class<EvolutionPoolRecipe> getRecipeType() {
		return EvolutionPoolRecipe.class;
	}

	@Override
	public void serializeNBT(CompoundNBT nbt) {
		super.serializeNBT(nbt);
		nbt.putShort(NBT_KEY_FUEL, fuel);
	}

	@Override
	public void deserializeNBT(CompoundNBT nbt) {
		super.deserializeNBT(nbt);
		fuel = nbt.getShort(NBT_KEY_FUEL);
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
	public int getCount() {
		return 3;
	}

}
