package com.github.elenterius.biomancy.init;

import net.minecraft.item.Food;

public final class ModFoods {
	private ModFoods() {}

	public static final Food FLESH_LUMP = new Food.Builder().hunger(2).saturation(0.4F).meat().build();
	public static final Food OCULUS = new Food.Builder().hunger(1).saturation(0.2F).meat().build();
	public static final Food STOMACH = new Food.Builder().hunger(1).saturation(0.2F).meat().build();
	public static final Food ARTIFICIAL_STOMACH = new Food.Builder().hunger(1).saturation(0.2F).meat().build();
	public static final Food BOLUS = new Food.Builder().hunger(2).saturation(0.1F).meat().build();
	public static final Food NUTRIENT_BAR = new Food.Builder().hunger(4).saturation(0.3F).build();
	public static final Food MEAT_BERRY = new Food.Builder().hunger(2).saturation(0.1F).meat().build();
}
