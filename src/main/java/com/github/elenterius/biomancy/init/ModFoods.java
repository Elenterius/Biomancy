package com.github.elenterius.biomancy.init;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.food.FoodProperties;

public final class ModFoods {

	public static final FoodProperties POOR_FLESH = new FoodProperties.Builder().nutrition(1).saturationMod(0.2f).meat().build();
	public static final FoodProperties AVERAGE_FLESH = new FoodProperties.Builder().nutrition(2).saturationMod(0.4f).meat().build();
	public static final FoodProperties NECROTIC_FLESH = new FoodProperties.Builder().nutrition(2).saturationMod(0.1F).meat()
			.effect(() -> new MobEffectInstance(ModMobEffects.FLESH_EATING_DISEASE.get(), 300, 0), 0.55f)
			.build();

	public static final FoodProperties OCULUS = new FoodProperties.Builder().nutrition(1).saturationMod(0.2F).meat()
			.effect(() -> new MobEffectInstance(MobEffects.HUNGER, 560, 0), 1f)
			.effect(() -> new MobEffectInstance(MobEffects.NIGHT_VISION, 550, 0), 0.35f)
			.build();
	public static final FoodProperties NUTRIENT_BAR = new FoodProperties.Builder().nutrition(4).saturationMod(0.3F).build();
	public static final FoodProperties PROTEIN_BAR = new FoodProperties.Builder().nutrition(5).saturationMod(0.4F).meat().build();

//	public static final FoodProperties MEAT_BERRY = new FoodProperties.Builder().nutrition(3).saturationMod(0.4F).meat().build();
//	public static final FoodProperties MILK_GEL = new FoodProperties.Builder().alwaysEat().build();

	private ModFoods() {}

}
