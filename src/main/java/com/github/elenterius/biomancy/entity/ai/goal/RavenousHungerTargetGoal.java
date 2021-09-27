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
		if (!mob.hasEffect(ModEffects.RAVENOUS_HUNGER.get()) || mob.getEffect(ModEffects.RAVENOUS_HUNGER.get()).getDuration() < 1) {
			stop();
			return false;
		}
		return super.canContinueToUse();
	}

	@Override
	public void stop() {
		setTarget(null);
		super.stop();
	}
}
