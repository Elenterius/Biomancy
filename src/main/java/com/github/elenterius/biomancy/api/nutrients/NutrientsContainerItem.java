package com.github.elenterius.biomancy.api.nutrients;

import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.ItemHandlerHelper;

public interface NutrientsContainerItem {

	String NUTRIENTS_TAG_KEY = "biomancy:nutrients";

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
		return container.getOrCreateTag().getInt(NUTRIENTS_TAG_KEY);
	}

	default boolean hasNutrients(ItemStack container) {
		return getNutrients(container) > 0;
	}

	default void setNutrients(ItemStack container, int amount) {
		int maxNutrients = getMaxNutrients(container);
		int oldValue = getNutrients(container);
		int newValue = Mth.clamp(amount, 0, maxNutrients);
		container.getOrCreateTag().putInt(NUTRIENTS_TAG_KEY, newValue);
		onNutrientsChanged(container, oldValue, newValue);
	}

	default float getNutrientsPct(ItemStack container) {
		return getNutrients(container) / (float) getMaxNutrients(container);
	}

	default boolean isValidNutrientFuel(ItemStack container, ItemStack resource) {
		return Nutrients.isValidFuel(resource);
	}

	default int getNutrientFuelValue(ItemStack container, ItemStack resource) {
		return Nutrients.getFuelValue(resource);
	}

	default ItemStack insertNutrients(ItemStack container, ItemStack resource) {
		if (resource.isEmpty()) return resource;
		if (!isValidNutrientFuel(container, resource)) return resource;

		final int nutrients = getNutrients(container);
		int maxNutrients = getMaxNutrients(container);
		if (nutrients >= maxNutrients) return resource;

		int fuelValue = getNutrientFuelValue(container, resource);
		if (fuelValue <= 0) return resource;

		int neededCount = Mth.floor(Math.max(0, maxNutrients - nutrients) / (float) fuelValue);
		if (neededCount > 0) {
			setNutrients(container, nutrients + fuelValue);
			return ItemHandlerHelper.copyStackWithSize(resource, resource.getCount() - 1);
		}
		return resource;
	}

}
