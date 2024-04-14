package com.github.elenterius.biomancy.crafting.recipe;

import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;

/**
 * Recipe with dynamic cost, time and/or result
 */
public abstract non-sealed class DynamicProcessingRecipe implements ProcessingRecipe<Container> {

	private final ResourceLocation id;
	private final RecipeType<?> type;

	protected DynamicProcessingRecipe(ResourceLocation id, RecipeType<?> type) {
		this.id = id;
		this.type = type;
	}

	@Override
	public final boolean isSpecial() {
		return true;
	}

	@Override
	public final ResourceLocation getId() {
		return id;
	}

	@Override
	public final ItemStack getResultItem(RegistryAccess registryAccess) {
		return ItemStack.EMPTY;
	}

	@Override
	public final RecipeType<?> getType() {
		return type;
	}

}
