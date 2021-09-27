package com.github.elenterius.biomancy.entity.ai.goal.golem;

import com.github.elenterius.biomancy.entity.golem.OwnableCreatureEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.player.PlayerEntity;

import java.util.EnumSet;

public class ReturnToOwnerGoal extends Goal {
	protected final OwnableCreatureEntity goalOwner;
	protected final double speed;
	private LivingEntity entityOwner;
	private int delayTime = 0;

	public ReturnToOwnerGoal(OwnableCreatureEntity entity, double speed) {
		this.goalOwner = entity;
		this.speed = speed;
		setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
	}

	@Override
	public boolean canUse() {
		if (goalOwner.getTargetBlockPos() != null || goalOwner.getTarget() != null) return false;
		PlayerEntity playerEntity = goalOwner.getOwner().orElse(null);
		if (playerEntity == null || playerEntity.isSpectator() || !playerEntity.isAlive()) return false;
		if (goalOwner.distanceToSqr(playerEntity) < 3d * 3d) return false;
		entityOwner = playerEntity;
		return true;
	}

	@Override
	public boolean canContinueToUse() {
		return goalOwner.getNavigation().isInProgress() && entityOwner.isAlive() && goalOwner.distanceToSqr(entityOwner) > 1.25d * 1.25d;
	}

	@Override
	public void tick() {
		goalOwner.getLookControl().setLookAt(entityOwner, 10.0F, goalOwner.getMaxHeadXRot());
		goalOwner.getNavigation().moveTo(entityOwner, speed);
		if (goalOwner.distanceToSqr(entityOwner) < 1.5d * 1.5d && --delayTime <= 0) {
			if (goalOwner.tryToReturnIntoPlayerInventory()) {
				goalOwner.getNavigation().stop();
			}
			else {
				delayTime = 10;
			}
		}
	}

	@Override
	public void stop() {
		goalOwner.getNavigation().stop();
		entityOwner = null;
		delayTime = 0;
	}
}
