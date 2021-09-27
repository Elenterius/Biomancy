package com.github.elenterius.biomancy.entity.ai.goal.golem;

import com.github.elenterius.biomancy.entity.golem.IGolem;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.entity.ai.goal.RandomWalkingGoal;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;

import javax.annotation.Nullable;

public class ReturnToHomePosGoal<T extends CreatureEntity & IGolem> extends RandomWalkingGoal {

	private final T entity;

	public ReturnToHomePosGoal(T goalOwner, double speedIn, boolean stopWhenIdle) {
		super(goalOwner, speedIn, 10, stopWhenIdle);
		entity = goalOwner;
	}

	@Override
	public boolean canUse() {
		if (entity.isGolemInactive()) return false;

		IGolem.Command command = entity.getGolemCommand();
		if (command == IGolem.Command.HOLD_POSITION || command == IGolem.Command.PATROL_AREA) {
			BlockPos pos = mob.blockPosition();
			return !pos.equals(mob.getRestrictCenter()) && super.canUse();
		}
		return false;
	}

	@Override
	@Nullable
	protected Vector3d getPosition() {
		BlockPos pos = mob.blockPosition();
		if (!pos.equals(mob.getRestrictCenter())) {
			IGolem.Command command = entity.getGolemCommand();
			if (command == IGolem.Command.PATROL_AREA) {
				return RandomPositionGenerator.getPosTowards(mob, 10, 7, Vector3d.atBottomCenterOf(mob.getRestrictCenter())); // doesn't include fluids
			}
			if (command == IGolem.Command.HOLD_POSITION) {
				return Vector3d.atBottomCenterOf(mob.getRestrictCenter());
			}
		}
		return null;
	}
}
