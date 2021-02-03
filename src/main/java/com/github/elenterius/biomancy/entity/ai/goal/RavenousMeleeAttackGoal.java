package com.github.elenterius.biomancy.entity.ai.goal;

import com.github.elenterius.biomancy.init.ModEffects;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;

public class RavenousMeleeAttackGoal extends MeleeAttackGoal {
	public RavenousMeleeAttackGoal(CreatureEntity creature, double speedIn, boolean useLongMemory) {
		super(creature, speedIn, useLongMemory);
	}

	@Override
	public boolean shouldExecute() {
		//noinspection ConstantConditions
		if (attacker.isPotionActive(ModEffects.RAVENOUS_HUNGER.get()) && attacker.getActivePotionEffect(ModEffects.RAVENOUS_HUNGER.get()).getDuration() > 0) {
			return super.shouldExecute();
		}
		return false;
	}

	@Override
	public boolean shouldContinueExecuting() {
		//noinspection ConstantConditions
		if (attacker.isPotionActive(ModEffects.RAVENOUS_HUNGER.get()) && attacker.getActivePotionEffect(ModEffects.RAVENOUS_HUNGER.get()).getDuration() > 0) {
			return super.shouldContinueExecuting();
		}
		return false;
	}
}
