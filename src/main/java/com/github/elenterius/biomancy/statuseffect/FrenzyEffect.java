package com.github.elenterius.biomancy.statuseffect;

import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

public class FrenzyEffect extends AttackDamageEffect implements StackingStatusEffect {

	public FrenzyEffect(MobEffectCategory category, int color) {
		super(category, color);
	}

	@Override
	public int getMaxEffectStack() {
		return 3;
	}

	@Override
	public boolean isDurationEffectTick(int duration, int amplifier) {
		return duration % 20 == 0;
	}

	@Override
	public void applyEffectTick(LivingEntity livingEntity, int amplifier) {
		if (livingEntity instanceof Player player) {
			player.causeFoodExhaustion(0.15f * (amplifier + 1f));
		}
	}

}
