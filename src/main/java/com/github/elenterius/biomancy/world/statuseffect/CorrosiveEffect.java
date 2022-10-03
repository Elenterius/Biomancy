package com.github.elenterius.biomancy.world.statuseffect;

import com.github.elenterius.biomancy.init.ModDamageSources;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;

public class CorrosiveEffect extends StatusEffect {

	public CorrosiveEffect(MobEffectCategory category, int color) {
		super(category, color, false);
	}

	@Override
	public void applyEffectTick(LivingEntity livingEntity, int amplifier) {
		int damage = 2 * (amplifier + 1);
		livingEntity.invulnerableTime = 0; //bypass invulnerable ticks
		livingEntity.hurt(ModDamageSources.CORROSIVE_ACID, damage);
	}

	@Override
	public boolean isDurationEffectTick(int duration, int amplifier) {
		return duration % 7 == 0;
	}

}
