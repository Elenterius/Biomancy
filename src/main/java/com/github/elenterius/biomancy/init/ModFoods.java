package com.github.elenterius.biomancy.init;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.food.FoodProperties;

public final class ModFoods {

	public static final FoodProperties VOLATILE_GLAND = new FoodProperties.Builder().nutrition(2).saturationMod(0.8f).meat().alwaysEat().fast().build();

	public static final FoodProperties TOXIN_GLAND = new FoodProperties.Builder().nutrition(2).saturationMod(0.8F).meat().alwaysEat()
			.effect(() -> new MobEffectInstance(MobEffects.POISON, 1400, 1), 1f)
			.effect(() -> new MobEffectInstance(MobEffects.CONFUSION, 200, 0), 1f)
			.build();

	public static final FoodProperties POOR_FLESH = new FoodProperties.Builder().nutrition(1).saturationMod(0.2f).meat().build();
	public static final FoodProperties AVERAGE_FLESH = new FoodProperties.Builder().nutrition(2).saturationMod(0.4f).meat().build();

	public static final FoodProperties DISEASED_FLESH = new FoodProperties.Builder().nutrition(2).saturationMod(0.1F).meat()
			.effect(() -> new MobEffectInstance(ModMobEffects.FLESH_EATING_DISEASE.get(), 300, 0), 0.55f)
			.build();
	public static final FoodProperties NUTRIENT_BAR = new FoodProperties.Builder().nutrition(4).saturationMod(0.3F).build();
	public static final FoodProperties PROTEIN_BAR = new FoodProperties.Builder().nutrition(5).saturationMod(0.4F).meat().build();

	private ModFoods() {}

}
