package com.github.elenterius.biomancy.world.item;

import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;

//TODO: replace with capability?
public interface INutrientsContainerItem {

	String NUTRIENTS_KEY = "BiomancyNutrients";

	default boolean consumeNutrients(ItemStack stack, int amount) {
		int nutrients = getNutrients(stack);
		if (nutrients < amount) return false;
		setNutrients(stack, nutrients - amount);
		return true;
	}

	default boolean addNutrients(ItemStack stack, int amount) {
		int nutrients = getNutrients(stack);
		int maxNutrients = getMaxNutrients(stack);
		if (nutrients + amount > maxNutrients) return false;
		setNutrients(stack, nutrients + amount);
		return true;
	}

	int getMaxNutrients(ItemStack stack);

	void onNutrientsChanged(ItemStack stack, int oldValue, int newValue);

	default int getNutrients(ItemStack stack) {
		return stack.getOrCreateTag().getInt(NUTRIENTS_KEY);
	}

	default boolean hasNutrients(ItemStack stack) {
		return getNutrients(stack) > 0;
	}

	default void setNutrients(ItemStack stack, int amount) {
		int maxNutrients = getMaxNutrients(stack);
		int oldValue = getNutrients(stack);
		int newValue = Mth.clamp(amount, 0, maxNutrients);
		stack.getOrCreateTag().putInt(NUTRIENTS_KEY, newValue);
		onNutrientsChanged(stack, oldValue, newValue);
	}

	default float getNutrientsPct(ItemStack stack) {
		return getNutrients(stack) / (float) getMaxNutrients(stack);
	}

}
