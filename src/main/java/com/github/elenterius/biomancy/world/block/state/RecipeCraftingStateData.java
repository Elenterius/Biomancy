package com.github.elenterius.biomancy.world.block.state;

import com.github.elenterius.biomancy.recipe.AbstractProductionRecipe;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.Level;

import java.util.Optional;

public abstract class RecipeCraftingStateData<T extends AbstractProductionRecipe> implements ContainerData {

	public static final String NBT_KEY_RECIPE_ID = "RecipeId";
	public static final String NBT_KEY_TIME_ELAPSED = "TimeElapsed";
	public static final String NBT_KEY_TIME_FOR_COMPLETION = "TimeForCompletion";

	public static final int TIME_INDEX = 0;
	public static final int TIME_FOR_COMPLETION_INDEX = 1;

	public int timeElapsed;
	public int timeForCompletion;

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

	protected abstract Class<T> getRecipeType();

	public Optional<T> getCraftingGoalRecipe(Level world) {
		if (recipeId == null) return Optional.empty();

		RecipeManager recipeManager = world.getRecipeManager();
		Optional<? extends Recipe<?>> optional = recipeManager.byKey(recipeId);
		if (optional.isPresent()) {
			Recipe<?> iRecipe = optional.get();
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

	public abstract int getFuelCost();

	public void clear() {
		recipeId = null;
		timeElapsed = 0;
		timeForCompletion = 0;
	}

	public void serialize(CompoundTag tag) {
		CraftingState.toNBT(tag, craftingState);
		if (recipeId != null) {
			tag.putString(NBT_KEY_RECIPE_ID, recipeId.toString());
		}
		tag.putInt(NBT_KEY_TIME_ELAPSED, timeElapsed);
		tag.putInt(NBT_KEY_TIME_FOR_COMPLETION, timeForCompletion);
	}

	public void deserialize(CompoundTag tag) {
		craftingState = CraftingState.fromNBT(tag);
		if (tag.contains(NBT_KEY_RECIPE_ID)) {
			String id = tag.getString(NBT_KEY_RECIPE_ID);
			recipeId = ResourceLocation.tryParse(id);
		}
		else recipeId = null;
		timeElapsed = tag.getInt(NBT_KEY_TIME_ELAPSED);
		timeForCompletion = tag.getInt(NBT_KEY_TIME_FOR_COMPLETION);
	}

	protected void validateIndex(int index) {
		if (index < 0 || index >= getCount()) throw new IndexOutOfBoundsException("Index out of bounds:" + index);
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
	public int getCount() {
		return 2;
	}

}
