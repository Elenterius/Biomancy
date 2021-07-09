package com.github.elenterius.biomancy.tileentity.state;

import com.github.elenterius.biomancy.recipe.AbstractProductionRecipe;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.RecipeManager;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.IIntArray;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import java.util.Optional;

public abstract class RecipeCraftingStateData<T extends AbstractProductionRecipe> implements IIntArray {

	public static final String NBT_KEY_RECIPE_ID = "RecipeId";
	public static final String NBT_KEY_TIME_ELAPSED = "TimeElapsed";
	public static final String NBT_KEY_TIME_FOR_COMPLETION = "TimeForCompletion";

	public static final int TIME_INDEX = 0;
	public static final int TIME_FOR_COMPLETION_INDEX = 1;

	public int timeElapsed;
	public int timeForCompletion;

	private CraftingState craftingState = CraftingState.NONE;
	private ResourceLocation recipeId; //we don't store the recipe reference, this way we don't have to check if the recipe was changed in the meantime

	public CraftingState getCraftingState() {
		return craftingState;
	}

	public void setCraftingState(CraftingState craftingState) {
		this.craftingState = craftingState;
	}

	public boolean isCraftingCanceled() {
		return craftingState == CraftingState.CANCELED;
	}

	public void cancelCrafting() {
		clear();
		craftingState = CraftingState.CANCELED;
	}

	abstract Class<T> getRecipeType();

	public Optional<T> getCraftingGoalRecipe(World world) {
		if (recipeId == null) return Optional.empty();

		RecipeManager recipeManager = world.getRecipeManager();
		Optional<? extends IRecipe<?>> optional = recipeManager.getRecipe(recipeId);
		if (optional.isPresent()) {
			IRecipe<?> iRecipe = optional.get();
			if (getRecipeType().isInstance(iRecipe)) {
				//noinspection unchecked
				return Optional.of((T) iRecipe);
			}
		}

		return Optional.empty();
	}

	public void setCraftingGoalRecipe(T recipe) {
		recipeId = recipe.getId();
		timeForCompletion = recipe.getCraftingTime();
	}

	public void clear() {
		recipeId = null;
		timeElapsed = 0;
		timeForCompletion = 0;
	}

	public void serializeNBT(CompoundNBT nbt) {
		CraftingState.serialize(nbt, craftingState);
		if (recipeId != null) {
			nbt.putString(NBT_KEY_RECIPE_ID, recipeId.toString());
		}
		nbt.putInt(NBT_KEY_TIME_ELAPSED, timeElapsed);
		nbt.putInt(NBT_KEY_TIME_FOR_COMPLETION, timeForCompletion);
	}

	public void deserializeNBT(CompoundNBT nbt) {
		craftingState = CraftingState.deserialize(nbt);
		if (nbt.contains(NBT_KEY_RECIPE_ID)) {
			String id = nbt.getString(NBT_KEY_RECIPE_ID);
			recipeId = ResourceLocation.tryCreate(id);
		}
		else recipeId = null;
		timeElapsed = nbt.getInt(NBT_KEY_TIME_ELAPSED);
		timeForCompletion = nbt.getInt(NBT_KEY_TIME_FOR_COMPLETION);
	}

	protected void validateIndex(int index) {
		if (index < 0 || index >= size()) throw new IndexOutOfBoundsException("Index out of bounds:" + index);
	}

	@Override
	public int get(int index) {
		validateIndex(index);
		if (index == TIME_INDEX) return timeElapsed;
		else if (index == TIME_FOR_COMPLETION_INDEX) return timeForCompletion;
		return 0;
	}

	@Override
	public void set(int index, int value) {
		validateIndex(index);
		if (index == TIME_INDEX) timeElapsed = value;
		else if (index == TIME_FOR_COMPLETION_INDEX) timeForCompletion = value;
	}

	@Override
	public int size() {
		return 2;
	}

	;

}
