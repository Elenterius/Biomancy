package com.github.elenterius.biomancy.util;

import com.github.elenterius.biomancy.init.ModItems;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.Set;
import java.util.function.Predicate;

public final class FuelUtil {

	private FuelUtil() {}

	public static final short DEFAULT_FUEL_VALUE = 200;
	public static final byte NUTRIENT_BAR_MULTIPLIER = 6;
	public static final byte NUTRIENTS_FUEL_VALUE = DEFAULT_FUEL_VALUE / 4;

	public static final Set<Item> FUEL_ITEMS = Set.of(
			ModItems.NUTRIENTS.get(),
			ModItems.NUTRIENT_PASTE.get(),
			ModItems.NUTRIENT_BAR.get()
//			ModItems.PROTEIN_BAR.get()
	);

	/**
	 * valid items for gun ammunition
	 */
	public static final Predicate<ItemStack> AMMO_PREDICATE = stack -> stack.getItem() == ModItems.NUTRIENT_PASTE.get();

	public static boolean isItemValidFuel(ItemStack stack) {
		if (FUEL_ITEMS.contains(stack.getItem())) return true;
		if (stack.isEdible()) {
			FoodProperties foodProperties = stack.getItem().getFoodProperties();
			return foodProperties != null && foodProperties.getNutrition() > 0;
		}
		return false;
	}

	public static int getItemFuelValue(ItemStack stack) {
		return getItemFuelValue(stack.getItem());
	}

	public static int getItemFuelValue(Item item) {
		if (item == ModItems.NUTRIENT_BAR.get()) return DEFAULT_FUEL_VALUE * NUTRIENT_BAR_MULTIPLIER;
//		if (item == ModItems.PROTEIN_BAR.get()) return DEFAULT_FUEL_VALUE * NUTRIENT_BAR_MULTIPLIER;
		if (item == ModItems.NUTRIENT_PASTE.get()) return DEFAULT_FUEL_VALUE;
		if (item == ModItems.NUTRIENTS.get()) return NUTRIENTS_FUEL_VALUE;
		if (item.isEdible()) {
			FoodProperties foodProperties = item.getFoodProperties();
			return foodProperties != null ? foodProperties.getNutrition() * 10 : 0;
		}
		return 0;
	}

	public static int getNutrientsFromFuelItem(Item item) {
		return getItemFuelValue(item) / NUTRIENTS_FUEL_VALUE;
	}

}
