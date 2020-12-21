package com.github.elenterius.blightlings.entity.ai.goal;

import com.github.elenterius.blightlings.entity.IThrowPotionAtPositionMob;
import com.github.elenterius.blightlings.util.RayTraceUtil;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.util.math.vector.Vector3d;

import java.util.EnumSet;

public class ThrowPotionAtPositionGoal extends Goal {
	protected final MobEntity goalOwner;
	protected final IThrowPotionAtPositionMob potionThrower;
	private int idleTime;
	protected Vector3d targetPos;
	protected final double speed;

	public ThrowPotionAtPositionGoal(IThrowPotionAtPositionMob goalOwner, double speedMultiplier) {
		this.potionThrower = goalOwner;
		this.goalOwner = (MobEntity) goalOwner;
		this.speed = speedMultiplier;
		setMutexFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
	}

	private Vector3d getPosition() {
		return potionThrower.getTargetPos();
	}

	@Override
	public boolean shouldExecute() {
		Vector3d pos = getPosition();
		if (pos == null || !potionThrower.hasThrowablePotion()) return false;
		targetPos = new Vector3d(pos.x, pos.y, pos.z); //make copy
		return true;
	}

	@Override
	public boolean shouldContinueExecuting() {
		boolean hasPath = goalOwner.getNavigator().hasPath();
		if (!hasPath && getPosition() != null) {
			if (++idleTime > 50) {
				potionThrower.setTargetPos(null);
				return false;
			}
			return potionThrower.hasThrowablePotion();
		}
		return hasPath && getPosition() != null;
	}

	public void tick() {
		goalOwner.getLookController().setLookPosition(targetPos.x, targetPos.y, targetPos.z, 75F, 75f);
		goalOwner.getNavigator().tryMoveToXYZ(targetPos.x, targetPos.y, targetPos.z, speed);

		double distSq = goalOwner.getDistanceSq(targetPos.x, targetPos.y, targetPos.z);
		if (distSq <= 4d * 4d && RayTraceUtil.canEntitySeePosition(goalOwner, targetPos)) {
			if (potionThrower.tryToThrowPotionAtPosition(targetPos)) {
				potionThrower.setTargetPos(null);
			}
			goalOwner.getNavigator().clearPath();
		}
	}

	@Override
	public void resetTask() {
		goalOwner.getNavigator().clearPath();
		idleTime = 0;
		targetPos = null;
	}
}
