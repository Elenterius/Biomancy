package com.github.elenterius.biomancy.util;

import com.github.elenterius.biomancy.init.ModItems;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.Set;

public final class FuelUtil {

	private FuelUtil() {}

	public static final short DEFAULT_FUEL_VALUE = 200;
	public static final byte NUTRIENT_PASTE_MULTIPLIER = 1;
	public static final byte NUTRIENT_BAR_MULTIPLIER = 6;

	public static final Set<Item> FUEL_ITEMS = Set.of(ModItems.NUTRIENTS.get(), ModItems.NUTRIENT_BAR.get(), ModItems.PROTEIN_BAR.get());

	public static boolean isItemValidFuel(ItemStack stack) {
		return FUEL_ITEMS.contains(stack.getItem());
	}

	public static float getItemFuelValue(ItemStack stackIn) {
		Item item = stackIn.getItem();
		if (item == ModItems.NUTRIENT_BAR.get()) return (float) DEFAULT_FUEL_VALUE * NUTRIENT_BAR_MULTIPLIER;
		if (item == ModItems.PROTEIN_BAR.get()) return (float) DEFAULT_FUEL_VALUE * NUTRIENT_BAR_MULTIPLIER;
		if (item == ModItems.NUTRIENTS.get()) return (float) DEFAULT_FUEL_VALUE * NUTRIENT_PASTE_MULTIPLIER;
		return 0;
	}

}
