package com.github.elenterius.biomancy.statuseffect;

import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Animal;

public class LibidoEffect extends StatusEffect {

	public LibidoEffect(MobEffectCategory category, int color) {
		super(category, color, false);
	}

	@Override
	public void applyEffectTick(LivingEntity livingEntity, int amplifier) {
		if (livingEntity instanceof Animal animal && !animal.isBaby() && animal.canFallInLove()) {
			int age = animal.getAge();
			if (age >= 0) {
				if (age > 0) animal.setAge(0); //growing age has to be 0 for animals to keep staying in love, else the in love state gets reset
				animal.setInLove(null);
				animal.setInLoveTime(animal.getInLoveTime());
			}
		}
	}

	@Override
	public boolean isDurationEffectTick(int duration, int amplifier) {
		int nTicks = 80 >> amplifier;
		return nTicks <= 0 || duration % nTicks == 0;
	}

}
