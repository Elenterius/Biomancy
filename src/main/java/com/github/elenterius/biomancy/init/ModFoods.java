package com.github.elenterius.biomancy.init;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.food.FoodProperties;

public final class ModFoods {

	public static final FoodProperties VOLATILE_GLAND = new FoodProperties.Builder().nutrition(2).saturationMod(0.8f).meat().alwaysEat().fast().build();

	public static final FoodProperties TOXIN_GLAND = new FoodProperties.Builder().nutrition(2).saturationMod(0.8f).meat().alwaysEat()
			.effect(() -> new MobEffectInstance(MobEffects.POISON, 1400, 1), 1f)
			.effect(() -> new MobEffectInstance(MobEffects.CONFUSION, 150, 0), 0.8f)
			.build();

	public static final FoodProperties CORROSIVE_FLUID = new FoodProperties.Builder().nutrition(1).saturationMod(0.2f)
			.effect(() -> new MobEffectInstance(ModMobEffects.CORROSIVE.get(), 20 * 2, 0), 1f)
			.effect(() -> new MobEffectInstance(ModMobEffects.ARMOR_SHRED.get(), 20 * 5, 0), 1f)
			.build();

	public static final FoodProperties MARROW_FLUID = new FoodProperties.Builder().nutrition(1).saturationMod(0.2f).build();

	public static final FoodProperties POOR_FLESH = new FoodProperties.Builder().nutrition(1).saturationMod(0.2f).meat().build();

	public static final FoodProperties LIVING_FLESH = new FoodProperties.Builder().nutrition(2).saturationMod(0.4f).meat()
			.effect(() -> new MobEffectInstance(ModMobEffects.PRIMORDIAL_INFESTATION.get(), 20 * 20, 0), 0.4f)
			.effect(() -> new MobEffectInstance(MobEffects.CONFUSION, 20 * 6, 0), 0.8f)
			.build();

	public static final FoodProperties NUTRIENT_BAR = new FoodProperties.Builder().nutrition(9).saturationMod(1.2f).build();
	public static final FoodProperties NUTRIENT_PASTE = new FoodProperties.Builder().nutrition(1).saturationMod(0.6f).build();

	private ModFoods() {}

}
