package com.github.elenterius.biomancy.init;

import net.minecraft.item.Food;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;

public final class ModFoods {
	private ModFoods() {}

	public static final Food FLESH_LUMP = new Food.Builder().nutrition(2).saturationMod(0.4F).meat().build();
	public static final Food NECROTIC_FLESH_LUMP = new Food.Builder().nutrition(2).saturationMod(0.1F).meat()
			.effect(() -> new EffectInstance(ModEffects.FLESH_EATING_DISEASE.get(), 300, 0), 0.55f)
			.build();
	public static final Food OCULUS = new Food.Builder().nutrition(1).saturationMod(0.2F).meat()
			.effect(() -> new EffectInstance(Effects.HUNGER, 560, 0), 1f)
			.effect(() -> new EffectInstance(Effects.NIGHT_VISION, 550, 0), 0.35f)
			.build();
	public static final Food STOMACH = new Food.Builder().nutrition(1).saturationMod(0.2F).meat().build();
	public static final Food ARTIFICIAL_STOMACH = new Food.Builder().nutrition(1).saturationMod(0.2F).meat().build();
	public static final Food NUTRIENT_BAR = new Food.Builder().nutrition(4).saturationMod(0.3F).build();
	public static final Food MEAT_BERRY = new Food.Builder().nutrition(3).saturationMod(0.4F).meat().build();
	public static final Food MILK_GEL = new Food.Builder().alwaysEat().build();
}
