package com.github.elenterius.biomancy.util.fuel;

import com.github.elenterius.biomancy.init.ModItems;
import net.minecraft.util.Mth;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.Set;
import java.util.function.Predicate;

public final class NutrientFuelUtil {

	private NutrientFuelUtil() {}

	public static final byte DEFAULT_FUEL_VALUE = 1;
	public static final int SIXTY_SECONDS_IN_TICKS = 20 * 60;

	public static final Set<Item> FUEL_ITEMS = Set.of(
			ModItems.NUTRIENTS.get(),
			ModItems.NUTRIENT_PASTE.get(),
			ModItems.NUTRIENT_BAR.get()
	);

	/**
	 * valid items for gun ammunition
	 */
	public static final Predicate<ItemStack> AMMO_PREDICATE = stack -> stack.getItem() == ModItems.NUTRIENT_PASTE.get();

	public static boolean isValidFuel(ItemStack stack) {
		if (stack.isEmpty()) return false;
		if (FUEL_ITEMS.contains(stack.getItem())) return true;
		return getFoodNutritionValue(stack) > 0;
	}

	public static int getFuelValue(ItemStack stack) {
		Item item = stack.getItem();

		if (item == ModItems.NUTRIENTS.get()) return DEFAULT_FUEL_VALUE;
		if (item == ModItems.NUTRIENT_PASTE.get()) return DEFAULT_FUEL_VALUE * 5;
		if (item == ModItems.NUTRIENT_BAR.get()) return (DEFAULT_FUEL_VALUE * 5) * 9;
		if (getFoodNutritionValue(stack) > 0) return DEFAULT_FUEL_VALUE * 2;
		return 0;
	}

	public static int getFoodNutritionValue(ItemStack stack) {
		if (stack.isEdible()) {
			FoodProperties foodProperties = stack.getItem().getFoodProperties(stack, null);
			if (foodProperties != null) return foodProperties.getNutrition();
		}
		return 0;
	}

	public static int getFuelCostMultiplier(int craftingTimeInTicks) {
		return 1 + Mth.floor(craftingTimeInTicks / (float) SIXTY_SECONDS_IN_TICKS);
	}

	public static int getFuelCost(int craftingTimeInTicks) {
		return getFuelCost(DEFAULT_FUEL_VALUE, craftingTimeInTicks);
	}

	public static int getFuelCost(int baseCost, int craftingTimeInTicks) {
		return baseCost * getFuelCostMultiplier(craftingTimeInTicks);
	}

}
