package com.github.elenterius.biomancy.init;

import net.minecraft.item.Food;

public final class ModFoods {
	private ModFoods() {}

	public static final Food VILE_MELON_SLICE = new Food.Builder().hunger(2).saturation(0.3F).meat().build();
	public static final Food COOKED_VILE_MELON_SLICE = new Food.Builder().hunger(4).saturation(0.6F).meat().build();
	public static final Food NUTRIENT_BAR = new Food.Builder().hunger(4).saturation(0.3F).build();
	public static final Food MEAT_BERRY = new Food.Builder().hunger(2).saturation(0.1F).meat().build();
}
