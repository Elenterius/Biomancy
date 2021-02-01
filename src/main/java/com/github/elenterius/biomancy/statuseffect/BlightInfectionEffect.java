package com.github.elenterius.biomancy.statuseffect;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.potion.EffectType;
import net.minecraft.util.DamageSource;

public class BlightInfectionEffect extends StatusEffect {

	public BlightInfectionEffect(EffectType type, int liquidColor) {
		super(type, liquidColor);
	}

	@Override
	public void performEffect(LivingEntity livingEntity, int amplifier) {
		livingEntity.attackEntityFrom(DamageSource.MAGIC, (amplifier + 1f) * 0.5f);
		if (livingEntity instanceof PlayerEntity) {
			((PlayerEntity) livingEntity).addExhaustion((amplifier + 1f) * 0.0025f);
		}
	}

	@Override
	public boolean isReady(int duration, int amplifier) {
		int nTicks = 40 >> amplifier;
		return nTicks <= 0 || duration % nTicks == 0;
	}

}
