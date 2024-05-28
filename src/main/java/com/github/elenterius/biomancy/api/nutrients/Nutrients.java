package com.github.elenterius.biomancy.api.nutrients;

import com.github.elenterius.biomancy.init.ModItems;
import com.github.elenterius.biomancy.init.tags.ModItemTags;
import it.unimi.dsi.fastutil.objects.Object2IntArrayMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.util.Mth;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.ApiStatus;

import java.util.function.IntUnaryOperator;

@ApiStatus.Experimental
public final class Nutrients {

	private static final Object2IntMap<Item> FUEL_VALUES = new Object2IntArrayMap<>();
	private static final Object2IntMap<Item> REPAIR_VALUES = new Object2IntArrayMap<>();

	public static final IntUnaryOperator RAW_MEAT_NUTRITION_MODIFIER = nutrition -> nutrition > 0 ? Mth.ceil(3.75d * Math.exp(0.215d * nutrition)) : 0;

	static {
		registerFuel(ModItems.NUTRIENT_PASTE.get(), 3);
		registerFuel(ModItems.NUTRIENT_BAR.get(), 3 * 9);
		registerRepairMaterial(ModItems.NUTRIENT_PASTE.get(), 6);
		registerRepairMaterial(ModItems.NUTRIENT_BAR.get(), 6 * 9);
	}

	private Nutrients() {}

	public static void registerFuel(Item resourceItem, int value) {
		FUEL_VALUES.put(resourceItem, value);
	}
	public static void registerRepairMaterial(Item resourceItem, int value) {
		REPAIR_VALUES.put(resourceItem, value);
	}

	public static boolean isValidRepairMaterial(ItemStack resource) {
		if (resource.isEmpty()) return false;
		if (REPAIR_VALUES.containsKey(resource.getItem())) return true;

		if (resource.isEdible()) {
			FoodProperties foodProperties = resource.getFoodProperties(null);
			if (foodProperties == null) return false;

			return foodProperties.isMeat() && resource.is(ModItemTags.RAW_MEATS) && foodProperties.getNutrition() > 0;
		}

		return false;
	}

	public static int getRepairValue(ItemStack resource) {
		if (resource.isEmpty()) return 0;

		Item item = resource.getItem();
		if (REPAIR_VALUES.containsKey(item)) {
			return REPAIR_VALUES.getInt(item);
		}

		if (resource.isEdible()) {
			FoodProperties foodProperties = resource.getFoodProperties(null);
			if (foodProperties == null) return 0;

			if (foodProperties.isMeat() && resource.is(ModItemTags.RAW_MEATS)) {
				return RAW_MEAT_NUTRITION_MODIFIER.applyAsInt(foodProperties.getNutrition()) * 2; //TODO: don't give bonus for rotten meats
			}
		}

		return 0;
	}

	public static boolean isValidFuel(ItemStack resource) {
		if (resource.isEmpty()) return false;
		if (FUEL_VALUES.containsKey(resource.getItem())) return true;

		if (resource.isEdible()) {
			FoodProperties foodProperties = resource.getFoodProperties(null);
			return foodProperties != null && foodProperties.getNutrition() > 0;
		}

		return false;
	}

	public static int getFuelValue(ItemStack resource) {
		if (resource.isEmpty()) return 0;

		Item item = resource.getItem();
		if (FUEL_VALUES.containsKey(item)) {
			return FUEL_VALUES.getInt(item);
		}

		if (resource.isEdible()) {
			FoodProperties foodProperties = resource.getFoodProperties(null);
			if (foodProperties == null) return 0;

			int nutrition = foodProperties.getNutrition();
			if (foodProperties.isMeat() && resource.is(ModItemTags.RAW_MEATS)) {
				return RAW_MEAT_NUTRITION_MODIFIER.applyAsInt(nutrition);
			}
			return nutrition;
		}

		return 0;
	}

}
