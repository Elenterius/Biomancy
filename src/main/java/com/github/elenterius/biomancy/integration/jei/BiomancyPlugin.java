package com.github.elenterius.biomancy.integration.jei;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.init.ModRecipes;
import com.google.common.collect.ImmutableSet;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.client.Minecraft;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.ResourceLocation;

import java.util.Objects;

@JeiPlugin
public class BiomancyPlugin implements IModPlugin {

	private static final ResourceLocation ID = BiomancyMod.createRL("plugin");

	@Override
	public ResourceLocation getPluginUid() {
		return ID;
	}

	@Override
	public void registerCategories(IRecipeCategoryRegistration registration) {
		registration.addRecipeCategories(new ChewerRecipeCategory(registration.getJeiHelpers().getGuiHelper()));
		registration.addRecipeCategories(new DigesterRecipeCategory(registration.getJeiHelpers().getGuiHelper()));
		registration.addRecipeCategories(new DecomposerRecipeCategory(registration.getJeiHelpers().getGuiHelper()));
		registration.addRecipeCategories(new SmallEvolutionPoolRecipeCategory(registration.getJeiHelpers().getGuiHelper()));
	}

	@Override
	public void registerRecipes(IRecipeRegistration registration) {
		ClientWorld world = Objects.requireNonNull(Minecraft.getInstance().world);
		registration.addRecipes(ImmutableSet.copyOf(world.getRecipeManager().getRecipesForType(ModRecipes.CHEWER_RECIPE_TYPE)), ChewerRecipeCategory.ID);
		registration.addRecipes(ImmutableSet.copyOf(world.getRecipeManager().getRecipesForType(ModRecipes.DIGESTER_RECIPE_TYPE)), DigesterRecipeCategory.ID);
		registration.addRecipes(ImmutableSet.copyOf(world.getRecipeManager().getRecipesForType(ModRecipes.DECOMPOSING_RECIPE_TYPE)), DecomposerRecipeCategory.ID);
		registration.addRecipes(ImmutableSet.copyOf(world.getRecipeManager().getRecipesForType(ModRecipes.EVOLUTION_POOL_RECIPE_TYPE)), SmallEvolutionPoolRecipeCategory.ID);
	}

}
