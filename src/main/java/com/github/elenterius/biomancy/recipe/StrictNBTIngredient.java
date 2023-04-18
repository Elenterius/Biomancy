package com.github.elenterius.biomancy.recipe;

import net.minecraft.item.ItemStack;
import net.minecraftforge.common.crafting.NBTIngredient;

public class StrictNBTIngredient extends NBTIngredient {

	protected StrictNBTIngredient(ItemStack stack) {
		super(stack);
	}

	public static StrictNBTIngredient of(ItemStack stack) {
		return new StrictNBTIngredient(stack);
	}

}
