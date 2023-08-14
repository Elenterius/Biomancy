package com.github.elenterius.biomancy.entity.fleshblob;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;

class FleshBlobAttackGoal extends MeleeAttackGoal {

	public FleshBlobAttackGoal(FleshBlob mob, double speed) {
		super(mob, speed, true);
	}

	@Override
	protected double getAttackReachSqr(LivingEntity attackTarget) {
		float radius = mob.getBbWidth() * 0.5f;
		return radius * radius + 0.75f + attackTarget.getBbWidth();
	}

}
