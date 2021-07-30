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
	public boolean shouldExecute() {
		return goalOwner.getRevengeTarget() != null;
	}

	@Override
	public boolean shouldContinueExecuting() {
		return goalOwner.getRevengeTarget() != null && super.shouldContinueExecuting();
	}

	@Override
	public void startExecuting() {
		Vector3d vec = getRandomPos();
		if (vec != null) goalOwner.getNavigator().setPath(goalOwner.getNavigator().getPathToPos(new BlockPos(vec), 0), speed);
	}

	@Nullable
	@Override
	protected Vector3d getRandomPos() {
		LivingEntity revengeTarget = goalOwner.getRevengeTarget();
		if (revengeTarget == null) return null;

		//findRandomAirTargetAwayFromPos
		Vector3d vec = RandomPositionGenerator.func_234133_a_(goalOwner, 32, 8, revengeTarget.getPositionVec());
		if (vec == null) {
			vec = RandomPositionGenerator.findRandomTargetBlockAwayFrom(goalOwner, 32, 8, revengeTarget.getPositionVec());
		}

		if (vec != null && revengeTarget.getDistanceSq(vec.x, vec.y, vec.z) < revengeTarget.getDistanceSq(goalOwner)) {
			return null;
		}

		return vec;
	}

}
