package com.github.elenterius.biomancy.world.block.entity.state;

import com.github.elenterius.biomancy.recipe.BioForgeRecipe;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

public class BioForgeStateData extends RecipeCraftingStateData<BioForgeRecipe> {

	public static final int FUEL_INDEX = 2;
	public static final String NBT_KEY_FUEL = "Fuel";
	private short fuelAmount;

	@Nullable
	public ResourceLocation selectedRecipeId;

	public int getFuelAmount() {
		return fuelAmount;
	}

	public void setFuelAmount(short value) {
		fuelAmount = value;
	}

	@Override
	Class<BioForgeRecipe> getRecipeType() {
		return BioForgeRecipe.class;
	}

	@Override
	public void serialize(CompoundTag nbt) {
		super.serialize(nbt);
		nbt.putShort(NBT_KEY_FUEL, fuelAmount);
		if (selectedRecipeId != null) {
			nbt.putString("RecipeId", selectedRecipeId.toString());
		}
	}

	@Override
	public void deserialize(CompoundTag nbt) {
		super.deserialize(nbt);
		fuelAmount = nbt.getShort(NBT_KEY_FUEL);
		selectedRecipeId = nbt.contains("RecipeId") ? ResourceLocation.tryParse(nbt.getString("RecipeId")) : null;
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
