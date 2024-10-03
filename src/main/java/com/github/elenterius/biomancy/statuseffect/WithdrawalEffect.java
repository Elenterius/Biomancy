package com.github.elenterius.biomancy.statuseffect;

import net.minecraft.stats.Stats;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

public class WithdrawalEffect extends AttackDamageEffect {

	public WithdrawalEffect(int color) {
		super(MobEffectCategory.HARMFUL, color, false);
	}

	@Override
	public void applyEffectTick(LivingEntity livingEntity, int amplifier) {
		if (livingEntity instanceof Player player) {

			if (livingEntity.getRandom().nextFloat() < 0.09f) {
				if (amplifier < 1) {
					if (livingEntity.getRandom().nextFloat() < 0.7f) {
						MobEffectInstance effectInstance = new MobEffectInstance(MobEffects.CONFUSION, 20 * livingEntity.getRandom().nextIntBetweenInclusive(3, 5), 0);
						StatusEffectHandler.modifyOnNextWorldTick(livingEntity, living -> living.addEffect(effectInstance));
					}
					player.causeFoodExhaustion(1.5F);
				}
				else if (amplifier < 2 || livingEntity.getRandom().nextFloat() < 0.4f) {
					MobEffectInstance effectInstance = new MobEffectInstance(MobEffects.CONFUSION, 20 * livingEntity.getRandom().nextIntBetweenInclusive(3, 4 + amplifier * 2), 1);
					StatusEffectHandler.modifyOnNextWorldTick(livingEntity, living -> living.addEffect(effectInstance));
					player.causeFoodExhaustion(2F * (amplifier + 1f));
				}
				else {
					if (livingEntity.getHealth() > 1f) {
						livingEntity.hurt(livingEntity.damageSources().magic(), 1f);
					}
					player.causeFoodExhaustion(0.5F);
				}
			}

			if (!livingEntity.isSleeping()) {
				player.awardStat(Stats.CUSTOM.get(Stats.TIME_SINCE_REST), 20 * 2); //increase insomnia counter
			}
		}
	}

	@Override
	public boolean isDurationEffectTick(int duration, int amplifier) {
		return duration % 20 == 0;
	}

}
