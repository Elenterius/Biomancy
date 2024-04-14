package com.github.elenterius.biomancy.crafting.recipe;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;

public abstract non-sealed class StaticProcessingRecipe implements ProcessingRecipe<Container> {

	private final ResourceLocation id;

	protected final int craftingTimeTicks;
	protected final int craftingCostNutrients;

	protected StaticProcessingRecipe(ResourceLocation id, int craftingTimeTicks, int craftingCostNutrients) {
		this.id = id;
		this.craftingTimeTicks = craftingTimeTicks;
		this.craftingCostNutrients = craftingCostNutrients;
	}

	@Override
	public final ResourceLocation getId() {
		return id;
	}

	@Override
	public final int getCraftingTimeTicks(Container inputInventory) {
		return craftingTimeTicks;
	}

	@Override
	public final int getCraftingCostNutrients(Container inputInventory) {
		return craftingCostNutrients;
	}

}
