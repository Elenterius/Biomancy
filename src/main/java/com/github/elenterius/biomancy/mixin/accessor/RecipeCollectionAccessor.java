package com.github.elenterius.biomancy.mixin.accessor;

import net.minecraft.client.gui.screens.recipebook.RecipeCollection;
import net.minecraft.world.item.crafting.Recipe;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Set;

@Mixin(RecipeCollection.class)
public interface RecipeCollectionAccessor {
	@Accessor("fitsDimensions")
	Set<Recipe<?>> biomancy$getFitDimensions();

	@Accessor("craftable")
	Set<Recipe<?>> biomancy$getCraftable();
}
