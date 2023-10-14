package com.github.elenterius.biomancy.tribute;

import com.github.elenterius.biomancy.init.ModMobEffects;
import com.mojang.datafixers.util.Pair;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.PotionItem;
import net.minecraft.world.item.alchemy.PotionUtils;

import java.util.List;
import java.util.Set;

class MobEffectTribute implements Tribute {

	//TODO: Replace with TagKeys?
	//
	//  Example:
	//
	//	TagKey<MobEffect> CRADLE_LIFE_ENERGY_EFFECTS = mobEffectTag("cradle/life_energy_modifiers");
	//	TagKey<MobEffect> CRADLE_DISEASE_EFFECTS = mobEffectTag("cradle/disease_modifiers");
	//	TagKey<MobEffect> CRADLE_HOSTILE_EFFECTS = mobEffectTag("cradle/hostile_modifiers");
	//
	//	private static TagKey<MobEffect> mobEffectTag(String name) {
	//		return TagKey.create(Registry.MOB_EFFECT_REGISTRY, BiomancyMod.createRL(name));
	//	}

	private static final Set<MobEffect> LIFE_ENERGY_MODIFIER = Set.of(MobEffects.HEAL, MobEffects.REGENERATION, MobEffects.HEALTH_BOOST);
	private static final Set<MobEffect> DISEASE_MODIFIER = Set.of(MobEffects.HUNGER, MobEffects.WITHER, MobEffects.POISON, MobEffects.CONFUSION, ModMobEffects.CORROSIVE.get());

	private float lifeEnergy = 0;
	private int diseaseModifier = 0;
	private int successModifier = 0;

	static MobEffectTribute from(ItemStack stack) {
		MobEffectTribute mobEffectTribute = new MobEffectTribute();

		if (stack.getItem() instanceof PotionItem) {
			List<MobEffectInstance> effectInstances = PotionUtils.getMobEffects(stack);
			for (MobEffectInstance instance : effectInstances) {
				mobEffectTribute.apply(instance, 1f);
			}
		}

		FoodProperties food = stack.getFoodProperties(null);
		if (food != null) {
			for (Pair<MobEffectInstance, Float> pair : food.getEffects()) {
				MobEffectInstance instance = pair.getFirst();
				float chance = pair.getSecond();
				mobEffectTribute.apply(instance, chance * 0.25f);
			}
		}

		return mobEffectTribute;
	}

	void apply(MobEffectInstance instance, float chance) {
		MobEffect effect = instance.getEffect();

		if (LIFE_ENERGY_MODIFIER.contains(effect)) {
			int level = instance.getAmplifier() + 1;
			lifeEnergy += level * 50 * chance;
		}
		else if (DISEASE_MODIFIER.contains(effect)) {
			int level = instance.getAmplifier() + 1;
			diseaseModifier += Math.round(level * 15 * chance);
		}
		else if (effect == MobEffects.LUCK) {
			int level = instance.getAmplifier() + 1;
			successModifier += Math.round(level * 50 * chance);
		}

	}

	@Override
	public int biomass() {
		return 0;
	}

	@Override
	public int lifeEnergy() {
		return Math.round(lifeEnergy);
	}

	@Override
	public int successModifier() {
		return successModifier;
	}

	@Override
	public int diseaseModifier() {
		return diseaseModifier;
	}

	@Override
	public int hostileModifier() {
		return 0;
	}

	@Override
	public int anomalyModifier() {
		return 0;
	}
}
