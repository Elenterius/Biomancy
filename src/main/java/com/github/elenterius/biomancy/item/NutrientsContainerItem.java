package com.github.elenterius.biomancy.item;

import com.github.elenterius.biomancy.util.fuel.NutrientFuelUtil;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.ItemHandlerHelper;

public interface NutrientsContainerItem {

	String NUTRIENTS_KEY = "Nutrients";

	default boolean consumeNutrients(ItemStack container, int amount) {
		int nutrients = getNutrients(container);
		if (nutrients < amount) return false;
		setNutrients(container, nutrients - amount);
		return true;
	}

	default boolean addNutrients(ItemStack container, int amount) {
		int nutrients = getNutrients(container);
		int maxNutrients = getMaxNutrients(container);
		if (nutrients + amount > maxNutrients) return false;
		setNutrients(container, nutrients + amount);
		return true;
	}

	int getMaxNutrients(ItemStack container);

	void onNutrientsChanged(ItemStack container, int oldValue, int newValue);

	default int getNutrients(ItemStack container) {
		return container.getOrCreateTag().getInt(NUTRIENTS_KEY);
	}

	default boolean hasNutrients(ItemStack container) {
		return getNutrients(container) > 0;
	}

	default void setNutrients(ItemStack container, int amount) {
		int maxNutrients = getMaxNutrients(container);
		int oldValue = getNutrients(container);
		int newValue = Mth.clamp(amount, 0, maxNutrients);
		container.getOrCreateTag().putInt(NUTRIENTS_KEY, newValue);
		onNutrientsChanged(container, oldValue, newValue);
	}

	default float getNutrientsPct(ItemStack container) {
		return getNutrients(container) / (float) getMaxNutrients(container);
	}

	default boolean isValidNutrientFuel(ItemStack container, ItemStack food) {
		return NutrientFuelUtil.isValidFuel(food);
	}

	default int getNutrientFuelValue(ItemStack container, ItemStack food) {
		return NutrientFuelUtil.getFuelValue(food);
	}

	default ItemStack insertNutrients(ItemStack container, ItemStack food) {
		if (food.isEmpty()) return food;
		if (!isValidNutrientFuel(container, food)) return food;

		final int nutrients = getNutrients(container);
		int maxNutrients = getMaxNutrients(container);
		if (nutrients >= maxNutrients) return food;

		int fuelValue = getNutrientFuelValue(container, food);
		if (fuelValue <= 0) return food;

		int neededCount = Mth.floor(Math.max(0, maxNutrients - nutrients) / (float) fuelValue);
		if (neededCount > 0) {
			setNutrients(container, nutrients + fuelValue);
			return ItemHandlerHelper.copyStackWithSize(food, food.getCount() - 1);
		}
		return food;
	}

}
