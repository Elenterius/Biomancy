package com.github.elenterius.blightlings.entity.ai.goal;

import com.github.elenterius.blightlings.init.ModEffects;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;

public class FrenzyMeleeAttackGoal extends MeleeAttackGoal {
	public FrenzyMeleeAttackGoal(CreatureEntity creature, double speedIn, boolean useLongMemory) {
		super(creature, speedIn, useLongMemory);
	}

	@Override
	public boolean shouldExecute() {
		//noinspection ConstantConditions
		if (attacker.isPotionActive(ModEffects.FRENZY.get()) && attacker.getActivePotionEffect(ModEffects.FRENZY.get()).getDuration() > 0) {
			return super.shouldExecute();
		}
		return false;
	}

	@Override
	public boolean shouldContinueExecuting() {
		//noinspection ConstantConditions
		if (attacker.isPotionActive(ModEffects.FRENZY.get()) && attacker.getActivePotionEffect(ModEffects.FRENZY.get()).getDuration() > 0) {
			return super.shouldContinueExecuting();
		}
		return false;
	}
}
