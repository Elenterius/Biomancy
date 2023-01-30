package com.github.elenterius.biomancy.world.entity;

import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public interface IFoodEater {
	void ate(@Nullable FoodProperties food);

	boolean isEating();

	void setEating(boolean flag);

	ItemStack getFoodItem();

	void setFoodItem(ItemStack stack);
}
