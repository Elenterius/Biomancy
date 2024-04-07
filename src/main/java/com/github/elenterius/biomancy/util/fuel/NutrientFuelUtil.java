package com.github.elenterius.biomancy.util.fuel;

import com.github.elenterius.biomancy.init.ModItems;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.Set;

public final class NutrientFuelUtil {

	private NutrientFuelUtil() {}

	public static final Set<Item> FUEL_ITEMS = Set.of(
			ModItems.NUTRIENTS.get(),
			ModItems.NUTRIENT_PASTE.get(),
			ModItems.NUTRIENT_BAR.get()
	);

	public static boolean isValidFuel(ItemStack stack) {
		if (stack.isEmpty()) return false;
		if (FUEL_ITEMS.contains(stack.getItem())) return true;
		return getFoodNutrition(stack) > 0;
	}

	public static int getFuelValue(ItemStack stack) {
		if (stack.isEmpty()) return 0;

		Item item = stack.getItem();
		if (item == ModItems.NUTRIENTS.get()) return 1;
		if (item == ModItems.NUTRIENT_PASTE.get()) return 5;
		if (item == ModItems.NUTRIENT_BAR.get()) return 5 * 9;

		return getFoodNutrition(stack);
	}

	public static int getFoodNutrition(ItemStack stack) {
		if (!stack.isEdible()) return 0;

		FoodProperties foodProperties = stack.getFoodProperties(null);
		return foodProperties != null ? foodProperties.getNutrition() : 0;
	}

}
