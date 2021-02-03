package com.github.elenterius.biomancy.init;

import net.minecraft.item.Food;

public final class ModFoods {
	private ModFoods() {}

	public static final Food FLESH_MELON_SLICE = new Food.Builder().hunger(2).saturation(0.3F).meat().build();
	public static final Food COOKED_FLESH_MELON_SLICE = new Food.Builder().hunger(4).saturation(0.6F).meat().build();

}
