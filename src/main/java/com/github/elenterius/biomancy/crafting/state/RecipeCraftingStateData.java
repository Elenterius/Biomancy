package com.github.elenterius.biomancy.crafting.state;

import com.github.elenterius.biomancy.crafting.recipe.ProcessingRecipe;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.Level;

import java.util.Optional;

public abstract class RecipeCraftingStateData<T extends ProcessingRecipe<C>, C extends Container> implements ContainerData {

	public static final String RECIPE_ID_KEY = "RecipeId";
	public static final String TIME_ELAPSED_KEY = "TimeElapsed";
	public static final String TIME_FOR_COMPLETION_KEY = "TimeForCompletion";
	public static final String NUTRIENTS_COST_KEY = "NutrientsCost";

	public static final int TIME_INDEX = 0;
	public static final int TIME_FOR_COMPLETION_INDEX = 1;
	public static final int NUTRIENTS_COST_INDEX = 2;

	public int timeElapsed;
	public int timeForCompletion;
	public int nutrientsCost;

	private CraftingState craftingState = CraftingState.NONE;
	private ResourceLocation recipeId;

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

	protected abstract boolean isRecipeOfInstance(Recipe<?> recipe);

	public Optional<T> getCraftingGoalRecipe(Level level) {
		if (recipeId == null) return Optional.empty();

		RecipeManager recipeManager = level.getRecipeManager();
		Optional<? extends Recipe<?>> optional = recipeManager.byKey(recipeId);
		if (optional.isPresent()) {
			Recipe<?> recipe = optional.get();
			if (isRecipeOfInstance(recipe)) {
				//noinspection unchecked
				return Optional.of((T) recipe);
			}
		}

		return Optional.empty();
	}

	public void setCraftingGoalRecipe(T recipe, C inputInventory) {
		recipeId = recipe.getId();
		timeForCompletion = recipe.getCraftingTimeTicks(inputInventory);
		nutrientsCost = recipe.getCraftingCostNutrients(inputInventory);
	}

	public abstract int getFuelCost();

	public void clear() {
		recipeId = null;
		timeElapsed = 0;
		timeForCompletion = 0;
		nutrientsCost = 0;
	}

	public void serialize(CompoundTag tag) {
		CraftingState.toNBT(tag, craftingState);
		if (recipeId != null) {
			tag.putString(RECIPE_ID_KEY, recipeId.toString());
		}
		tag.putInt(TIME_ELAPSED_KEY, timeElapsed);
		tag.putInt(TIME_FOR_COMPLETION_KEY, timeForCompletion);
		tag.putInt(NUTRIENTS_COST_KEY, nutrientsCost);
	}

	public void deserialize(CompoundTag tag) {
		craftingState = CraftingState.fromNBT(tag);
		if (tag.contains(RECIPE_ID_KEY)) {
			String id = tag.getString(RECIPE_ID_KEY);
			recipeId = ResourceLocation.tryParse(id);
		}
		else recipeId = null;
		timeElapsed = tag.getInt(TIME_ELAPSED_KEY);
		timeForCompletion = tag.getInt(TIME_FOR_COMPLETION_KEY);
		nutrientsCost = tag.getInt(NUTRIENTS_COST_KEY);
	}

	protected void validateIndex(int index) {
		if (index < 0 || index >= getCount()) throw new IndexOutOfBoundsException("Index out of bounds:" + index);
	}

	@Override
	public int get(int index) {
		validateIndex(index);
		if (index == TIME_INDEX) return timeElapsed;
		else if (index == TIME_FOR_COMPLETION_INDEX) return timeForCompletion;
		else if (index == NUTRIENTS_COST_INDEX) return nutrientsCost;
		return 0;
	}

	@Override
	public void set(int index, int value) {
		validateIndex(index);
		if (index == TIME_INDEX) timeElapsed = value;
		else if (index == TIME_FOR_COMPLETION_INDEX) timeForCompletion = value;
		else if (index == NUTRIENTS_COST_INDEX) nutrientsCost = value;
	}

	@Override
	public int getCount() {
		return 3;
	}

}
