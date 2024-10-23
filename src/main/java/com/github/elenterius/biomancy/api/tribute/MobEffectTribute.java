package com.github.elenterius.biomancy.api.tribute;

import com.github.elenterius.biomancy.init.tags.ModMobEffectTags;
import com.github.elenterius.biomancy.mixin.accessor.SuspiciousStewItemAccessor;
import com.mojang.datafixers.util.Pair;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.PotionItem;
import net.minecraft.world.item.SuspiciousStewItem;
import net.minecraft.world.item.alchemy.PotionUtils;
import org.jetbrains.annotations.ApiStatus;

import java.util.List;

@ApiStatus.Internal
public record MobEffectTribute(int lifeEnergy, int successModifier, int diseaseModifier, int hostileModifier, int anomalyModifier) implements Tribute {

	static Tribute from(ItemStack stack) {
		boolean isPotionItem = stack.getItem() instanceof PotionItem; //we don't check if the potion has no effects because it should contain an effect in 99% of cases

		boolean isSuspiciousStewItem = stack.getItem() instanceof SuspiciousStewItem;

		FoodProperties food = stack.getFoodProperties(null);
		boolean isFoodItem = food != null && !food.getEffects().isEmpty(); //we check if the food has any effects because they are optional

		if (!isPotionItem && !isSuspiciousStewItem && !isFoodItem) return Tribute.EMPTY; //avoid creation of new empty objects

		Builder builder = new Builder();

		if (isPotionItem) {
			List<MobEffectInstance> effectInstances = PotionUtils.getMobEffects(stack);
			for (MobEffectInstance effectInstance : effectInstances) {
				builder.addEffect(effectInstance);
			}
		}

		if (isSuspiciousStewItem) {
			SuspiciousStewItemAccessor.biomancy$ListPotionEffects(stack, effectInstance -> builder.addEffect(effectInstance, 0.35f));
		}

		if (isFoodItem) {
			for (Pair<MobEffectInstance, Float> pair : food.getEffects()) {
				MobEffectInstance effectInstance = pair.getFirst();
				float chance = pair.getSecond();
				builder.addEffect(effectInstance, chance * 0.25f);
			}
		}

		return builder.build();
	}

	public static Builder builder() {
		return new Builder();
	}

	@Override
	public int biomass() {
		return 0;
	}

	public boolean isEmpty() {
		return lifeEnergy == 0
				&& successModifier == 0
				&& diseaseModifier == 0
				&& hostileModifier == 0
				&& anomalyModifier == 0;
	}

	public static class Builder {

		public static final int BASE_POTION_DURATION = 900;

		private float lifeEnergy = 0;
		private float diseaseModifier = 0;
		private float successModifier = 0;
		private float hostileModifier = 0;
		private float anomalyModifier = 0;

		private Builder() {}

		public Builder addEffect(MobEffectInstance instance) {
			addEffect(instance, 1f);
			return this;
		}

		public Builder addEffect(MobEffectInstance instance, float multiplier) {
			addEffect(instance.getEffect(), instance.getAmplifier(), instance.getDuration(), multiplier);
			return this;
		}

		public Builder addEffect(MobEffect effect) {
			addEffect(effect, 0, BASE_POTION_DURATION, 1f);
			return this;
		}

		public Builder addEffect(MobEffect effect, int amplifier, int duration, float multiplier) {
			if (ModMobEffectTags.isCradleLifeEnergySource(effect)) { //heal, regeneration, health boost, absorption
				lifeEnergy += calculateEffectModifier(effect.isInstantenous(), amplifier, duration, 60) * multiplier;
			}

			if (ModMobEffectTags.isCradleDiseaseSource(effect)) { //weakness, wither, poison
				diseaseModifier += calculateEffectModifier(effect.isInstantenous(), amplifier, duration, 15) * multiplier;
			}

			if (ModMobEffectTags.isCradleSuccessSource(effect)) { //luck, saturation, libido
				successModifier += calculateEffectModifier(effect.isInstantenous(), amplifier, duration, 50) * multiplier;
			}

			if (ModMobEffectTags.isCradleHostilitySource(effect)) { //hunger, confusion, blindness, harm, wither, poison, bleed
				hostileModifier += calculateEffectModifier(effect.isInstantenous(), amplifier, duration, 15) * multiplier;
			}

			if (ModMobEffectTags.isCradleAnomalySource(effect)) { //bad omen, darkness, corrosive
				anomalyModifier += calculateEffectModifier(effect.isInstantenous(), amplifier, duration, 50) * multiplier;
			}

			return this;
		}

		private float calculateEffectModifier(boolean isInstantEffect, int amplifier, int duration, float baseAmount) {
			if (isInstantEffect) return (amplifier + 1f) * baseAmount;
			if (duration == -1) return 1_000_000f;

			float durationPct = (float) duration / BASE_POTION_DURATION;
			return durationPct * baseAmount + (amplifier + amplifier * durationPct) * baseAmount;
		}

		public MobEffectTribute build() {
			return new MobEffectTribute(Math.round(lifeEnergy), Math.round(successModifier), Math.round(diseaseModifier), Math.round(hostileModifier), Math.round(anomalyModifier));
		}
	}

}
