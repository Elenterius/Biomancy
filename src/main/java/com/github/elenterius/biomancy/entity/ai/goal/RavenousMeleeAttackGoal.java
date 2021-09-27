package com.github.elenterius.biomancy.entity.ai.goal;

import com.github.elenterius.biomancy.init.ModEffects;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;

public class RavenousMeleeAttackGoal extends MeleeAttackGoal {
	public RavenousMeleeAttackGoal(CreatureEntity creature, double speedIn, boolean useLongMemory) {
		super(creature, speedIn, useLongMemory);
	}

	@Override
	public boolean canUse() {
		//noinspection ConstantConditions
		if (mob.hasEffect(ModEffects.RAVENOUS_HUNGER.get()) && mob.getEffect(ModEffects.RAVENOUS_HUNGER.get()).getDuration() > 0) {
			return super.canUse();
		}
		return false;
	}

	@Override
	public boolean canContinueToUse() {
		//noinspection ConstantConditions
		if (mob.hasEffect(ModEffects.RAVENOUS_HUNGER.get()) && mob.getEffect(ModEffects.RAVENOUS_HUNGER.get()).getDuration() > 0) {
			return super.canContinueToUse();
		}
		return false;
	}
}
