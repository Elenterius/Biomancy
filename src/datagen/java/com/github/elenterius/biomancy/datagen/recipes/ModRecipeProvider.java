package com.github.elenterius.biomancy.datagen.recipes;

import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeProvider;

import java.util.function.Consumer;

public class ModRecipeProvider extends RecipeProvider {

	private final DecomposingRecipeProvider decomposingRecipeProvider;
	private final DigestingRecipeProvider digestingRecipeProvider;
	private final BioForgingRecipeProvider bioforgingRecipeProvider;
	private final BioBrewingRecipeProvider biobrewingRecipeProvider;
	private final VanillaRecipeProvider vanillaRecipeProvider;

	public ModRecipeProvider(PackOutput output) {
		super(output);
		decomposingRecipeProvider = new DecomposingRecipeProvider(output);
		digestingRecipeProvider = new DigestingRecipeProvider(output);
		bioforgingRecipeProvider = new BioForgingRecipeProvider(output);
		biobrewingRecipeProvider = new BioBrewingRecipeProvider(output);
		vanillaRecipeProvider = new VanillaRecipeProvider(output);
	}

	@Override
	protected void buildRecipes(Consumer<FinishedRecipe> consumer) {
		decomposingRecipeProvider.buildRecipes(consumer);
		digestingRecipeProvider.buildRecipes(consumer);
		bioforgingRecipeProvider.buildRecipes(consumer);
		biobrewingRecipeProvider.buildRecipes(consumer);
		vanillaRecipeProvider.buildRecipes(consumer);
	}

}
