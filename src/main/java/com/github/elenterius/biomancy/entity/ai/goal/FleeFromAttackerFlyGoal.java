package com.github.elenterius.biomancy.entity.ai.goal;

import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;

import javax.annotation.Nullable;

public class FleeFromAttackerFlyGoal extends RandomFlyGoal {

	public FleeFromAttackerFlyGoal(CreatureEntity entityIn, double speedIn) {
		super(entityIn, speedIn);
	}

	@Override
	public boolean canUse() {
		return goalOwner.getLastHurtByMob() != null;
	}

	@Override
	public boolean canContinueToUse() {
		return goalOwner.getLastHurtByMob() != null && super.canContinueToUse();
	}

	@Override
	public void start() {
		Vector3d vec = getRandomPos();
		if (vec != null) goalOwner.getNavigation().moveTo(goalOwner.getNavigation().createPath(new BlockPos(vec), 0), speed);
	}

	@Nullable
	@Override
	protected Vector3d getRandomPos() {
		LivingEntity revengeTarget = goalOwner.getLastHurtByMob();
		if (revengeTarget == null) return null;

		Vector3d vec = RandomPositionGenerator.getPosAvoid(goalOwner, 32, 8, revengeTarget.position());
		if (vec == null) {
			vec = RandomPositionGenerator.getLandPosAvoid(goalOwner, 32, 8, revengeTarget.position());
		}

		if (vec != null && revengeTarget.distanceToSqr(vec.x, vec.y, vec.z) < revengeTarget.distanceToSqr(goalOwner)) {
			return null;
		}

		return vec;
	}

}
