package com.github.elenterius.biomancy.entity.mob.ai.goal;

import com.github.elenterius.biomancy.init.ModMobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;

public class FrenzyAttackableTargetGoal<T extends LivingEntity> extends NearestAttackableTargetGoal<T> {

	public FrenzyAttackableTargetGoal(Mob goalOwner, Class<T> targetClass) {
		super(goalOwner, targetClass, true);
	}

	@Override
	public boolean canUse() {
		if (!mob.hasEffect(ModMobEffects.FRENZY.get())) return false;

		return super.canUse();
	}

	@Override
	public boolean canContinueToUse() {
		if (!mob.hasEffect(ModMobEffects.FRENZY.get())) {
			stop();
			return false;
		}

		return super.canContinueToUse();
	}

	@Override
	public void stop() {
		//		setTarget(null); //immediately forget target
		super.stop();
	}

}