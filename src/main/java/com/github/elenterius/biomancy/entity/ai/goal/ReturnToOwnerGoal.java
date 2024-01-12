package com.github.elenterius.biomancy.entity.ai.goal;

import com.github.elenterius.biomancy.entity.ownable.OwnableMob;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.player.Player;

import java.util.EnumSet;

public class ReturnToOwnerGoal extends Goal {

	protected final OwnableMob goalOwner;
	protected final double speed;
	private LivingEntity entityOwner;
	private int delayTime = 0;

	public ReturnToOwnerGoal(OwnableMob entity, double speed) {
		this.goalOwner = entity;
		this.speed = speed;
		setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
	}

	@Override
	public boolean canUse() {
		if (goalOwner.getTargetBlockPos() != null || goalOwner.getTarget() != null) return false;
		Player playerEntity = goalOwner.getOwnerAsPlayer().orElse(null);
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
