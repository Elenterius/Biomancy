package com.github.elenterius.biomancy.init;

import net.minecraft.item.Food;

public final class ModFoods {
	private ModFoods() {}

	public static final Food FLESH_LUMP = new Food.Builder().nutrition(2).saturationMod(0.4F).meat().build();
	public static final Food OCULUS = new Food.Builder().nutrition(1).saturationMod(0.2F).meat().build();
	public static final Food STOMACH = new Food.Builder().nutrition(1).saturationMod(0.2F).meat().build();
	public static final Food ARTIFICIAL_STOMACH = new Food.Builder().nutrition(1).saturationMod(0.2F).meat().build();
	public static final Food BOLUS = new Food.Builder().nutrition(2).saturationMod(0.1F).meat().build();
	public static final Food NUTRIENT_BAR = new Food.Builder().nutrition(4).saturationMod(0.3F).build();
	public static final Food MEAT_BERRY = new Food.Builder().nutrition(2).saturationMod(0.1F).meat().build();
}
