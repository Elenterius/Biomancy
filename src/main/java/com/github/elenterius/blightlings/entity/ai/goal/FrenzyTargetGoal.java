package com.github.elenterius.blightlings.entity.ai.goal;

import com.github.elenterius.blightlings.init.ModEffects;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.goal.NearestAttackableTargetGoal;

public class FrenzyTargetGoal<T extends LivingEntity> extends NearestAttackableTargetGoal<T> {
	public FrenzyTargetGoal(MobEntity goalOwnerIn, Class<T> targetClassIn) {
		super(goalOwnerIn, targetClassIn, true);
	}

	@Override
	public boolean shouldExecute() {
		//noinspection ConstantConditions
		if (goalOwner.isPotionActive(ModEffects.FRENZY.get()) && goalOwner.getActivePotionEffect(ModEffects.FRENZY.get()).getDuration() > 0) {
			return super.shouldExecute();
		}
		return false;
	}

	@Override
	public boolean shouldContinueExecuting() {
		//noinspection ConstantConditions
		if (!goalOwner.isPotionActive(ModEffects.FRENZY.get()) || goalOwner.getActivePotionEffect(ModEffects.FRENZY.get()).getDuration() < 1) {
			return false;
		}
		return super.shouldContinueExecuting();
	}

	@Override
	public void resetTask() {
		setNearestTarget(null);
		super.resetTask();
	}
}
