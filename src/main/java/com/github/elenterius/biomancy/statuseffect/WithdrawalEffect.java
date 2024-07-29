package com.github.elenterius.biomancy.statuseffect;

import net.minecraft.stats.Stats;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

public class WithdrawalEffect extends AttackDamageEffect {

	public WithdrawalEffect(int color) {
		super(MobEffectCategory.HARMFUL, color);
	}

	@Override
	public void applyEffectTick(LivingEntity livingEntity, int amplifier) {
		if (livingEntity instanceof Player player) {
			if (!livingEntity.isSleeping()) {
				player.awardStat(Stats.CUSTOM.get(Stats.TIME_SINCE_REST), 20 * 2); //increase insomnia counter
			}
			player.causeFoodExhaustion(0.05F * (amplifier + 1f));
		}
	}

	@Override
	public boolean isDurationEffectTick(int duration, int amplifier) {
		return duration % 20 == 0;
	}

}
