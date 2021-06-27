package com.github.elenterius.biomancy.util;

import com.github.elenterius.biomancy.init.ModItems;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import java.util.function.Predicate;

public final class BiofuelUtil {

    private BiofuelUtil() {}

    public static short DEFAULT_FUEL_VALUE = 200;
    public static byte NUTRIENT_PASTE_MULTIPLIER = 1;
    public static byte NUTRIENT_BAR_MULTIPLIER = 6;

    public static final Predicate<ItemStack> VALID_SOLID_FUEL = stack -> stack.getItem() == ModItems.NUTRIENT_PASTE.get() || stack.getItem() == ModItems.NUTRIENT_BAR.get();

    public static boolean isItemValidFuel(ItemStack stackIn) {
        return VALID_SOLID_FUEL.test(stackIn);
    }

    public static float getItemFuelValue(ItemStack stackIn) {
        Item item = stackIn.getItem();
        if (item == ModItems.NUTRIENT_BAR.get()) return DEFAULT_FUEL_VALUE * NUTRIENT_BAR_MULTIPLIER;
        if (item == ModItems.NUTRIENT_PASTE.get()) return DEFAULT_FUEL_VALUE * NUTRIENT_PASTE_MULTIPLIER;
        return 0;
    }

}
