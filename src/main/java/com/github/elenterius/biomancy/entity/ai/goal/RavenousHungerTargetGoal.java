package com.github.elenterius.biomancy.entity.ai.goal;

import com.github.elenterius.biomancy.init.ModEffects;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.goal.NearestAttackableTargetGoal;

public class RavenousHungerTargetGoal<T extends LivingEntity> extends NearestAttackableTargetGoal<T> {
	public RavenousHungerTargetGoal(MobEntity goalOwnerIn, Class<T> targetClassIn) {
		super(goalOwnerIn, targetClassIn, true);
	}

	@Override
	public boolean shouldExecute() {
		//noinspection ConstantConditions
		if (goalOwner.isPotionActive(ModEffects.RAVENOUS_HUNGER.get()) && goalOwner.getActivePotionEffect(ModEffects.RAVENOUS_HUNGER.get()).getDuration() > 0) {
			return super.shouldExecute();
		}
		return false;
	}

	@Override
	public boolean shouldContinueExecuting() {
		//noinspection ConstantConditions
		if (!goalOwner.isPotionActive(ModEffects.RAVENOUS_HUNGER.get()) || goalOwner.getActivePotionEffect(ModEffects.RAVENOUS_HUNGER.get()).getDuration() < 1) {
			resetTask();
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
