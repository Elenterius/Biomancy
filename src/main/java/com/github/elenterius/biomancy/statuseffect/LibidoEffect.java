package com.github.elenterius.biomancy.statuseffect;

import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.npc.Villager;

public class LibidoEffect extends StatusEffect {

	public static final int VILLAGER_BREED_DELAY = 20 * 3;

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
		else if (livingEntity instanceof Villager villager && !villager.isBaby()) {
			int age = villager.getAge();
			if (age > VILLAGER_BREED_DELAY) {
				villager.setAge(VILLAGER_BREED_DELAY); //growing age has to be 0 for villagers to be able to breed
			}
		}
	}

	@Override
	public boolean isDurationEffectTick(int duration, int amplifier) {
		return duration % 40 == 0;
	}

}
