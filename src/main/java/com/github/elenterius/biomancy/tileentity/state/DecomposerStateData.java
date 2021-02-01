package com.github.elenterius.biomancy.tileentity.state;

import com.github.elenterius.biomancy.recipe.DecomposingRecipe;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.RecipeManager;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.IIntArray;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import java.util.Optional;

public class DecomposerStateData implements IIntArray {

	private final int TIME_INDEX = 0;
	private final int TIME_FOR_COMPLETION_INDEX = 1;
	private final int FUEL_INDEX = 2;
	private final int SPEED_FUEL_INDEX = 3;
	@Nonnull
	public CraftingState craftingState = CraftingState.NONE;
	public int timeElapsed;
	public int timeForCompletion;
	public short mainFuel; //raw-meat (fake "saturation", we use the food healing value instead)
	public short speedFuel; //glucose ("candy", food that contains sugar)
	private ResourceLocation recipeId; //we don't store the recipe reference, this way we don't have to check if the recipe was changed in the meantime

	public boolean isCraftingCanceled() {
		return craftingState == CraftingState.CANCELED;
	}

	public void cancelCrafting() {
		clear();
		craftingState = CraftingState.CANCELED;
	}

	public Optional<DecomposingRecipe> getCraftingGoalRecipe(World world) {
		if (recipeId == null) return Optional.empty();

		RecipeManager recipeManager = world.getRecipeManager();
		Optional<? extends IRecipe<?>> optional = recipeManager.getRecipe(recipeId);
		if (optional.isPresent()) {
			IRecipe<?> iRecipe = optional.get();
			if (iRecipe instanceof DecomposingRecipe) return Optional.of((DecomposingRecipe) iRecipe);
		}

		return Optional.empty();
	}

	public void setCraftingGoalRecipe(DecomposingRecipe recipe) {
		recipeId = recipe.getId();
		timeForCompletion = recipe.getDecomposingTime();
	}

	public void clear() {
		timeElapsed = 0;
		timeForCompletion = 0;
		recipeId = null;
	}

	public void serializeNBT(CompoundNBT nbt) {
		CraftingState.serialize(nbt, craftingState);
		nbt.putInt("TimeElapsed", timeElapsed);
		nbt.putInt("TimeForCompletion", timeForCompletion);
		if (recipeId != null) {
			nbt.putString("RecipeId", recipeId.toString());
		}
		nbt.putShort("MainFuel", mainFuel);
		nbt.putShort("SpeedFuel", speedFuel);
	}

	public void deserializeNBT(CompoundNBT nbt) {
		craftingState = CraftingState.deserialize(nbt);
		timeElapsed = nbt.getInt("TimeElapsed");
		timeForCompletion = nbt.getInt("TimeForCompletion");
		if (nbt.contains("RecipeId")) {
			String id = nbt.getString("RecipeId");
			recipeId = ResourceLocation.tryCreate(id);
		}
		else recipeId = null;
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
