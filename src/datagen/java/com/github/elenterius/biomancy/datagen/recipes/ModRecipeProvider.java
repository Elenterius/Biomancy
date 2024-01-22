package com.github.elenterius.biomancy.datagen.recipes;

import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeProvider;

import java.util.function.Consumer;

public class ModRecipeProvider extends RecipeProvider {

	private final DecomposerRecipeProvider decomposerRecipeProvider;
	private final DigesterRecipeProvider digesterRecipeProvider;
	private final BioForgeRecipeProvider bioForgeRecipeProvider;
	private final BioLabRecipeProvider bioLabRecipeProvider;
	private final VanillaRecipeProvider vanillaRecipeProvider;

	public ModRecipeProvider(PackOutput output) {
		super(output);
		decomposerRecipeProvider = new DecomposerRecipeProvider(output);
		digesterRecipeProvider = new DigesterRecipeProvider(output);
		bioForgeRecipeProvider = new BioForgeRecipeProvider(output);
		bioLabRecipeProvider = new BioLabRecipeProvider(output);
		vanillaRecipeProvider = new VanillaRecipeProvider(output);
	}

	@Override
	protected void buildRecipes(Consumer<FinishedRecipe> consumer) {
		decomposerRecipeProvider.buildRecipes(consumer);
		digesterRecipeProvider.buildRecipes(consumer);
		bioForgeRecipeProvider.buildRecipes(consumer);
		bioLabRecipeProvider.buildRecipes(consumer);
		vanillaRecipeProvider.buildRecipes(consumer);
	}

}
