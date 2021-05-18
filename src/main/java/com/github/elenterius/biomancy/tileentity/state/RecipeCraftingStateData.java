package com.github.elenterius.biomancy.tileentity.state;

import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.RecipeManager;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import java.util.Optional;

public abstract class RecipeCraftingStateData<T extends IRecipe<?>> {

	public static final String NBT_KEY_RECIPE_ID = "RecipeId";

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
	}

	public void clear() {
		recipeId = null;
	}

	public void serializeNBT(CompoundNBT nbt) {
		CraftingState.serialize(nbt, craftingState);
		if (recipeId != null) {
			nbt.putString(NBT_KEY_RECIPE_ID, recipeId.toString());
		}
	}

	public void deserializeNBT(CompoundNBT nbt) {
		craftingState = CraftingState.deserialize(nbt);
		if (nbt.contains(NBT_KEY_RECIPE_ID)) {
			String id = nbt.getString(NBT_KEY_RECIPE_ID);
			recipeId = ResourceLocation.tryCreate(id);
		}
		else recipeId = null;
	}

}
