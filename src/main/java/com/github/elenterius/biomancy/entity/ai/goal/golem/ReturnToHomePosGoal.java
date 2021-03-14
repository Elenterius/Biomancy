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
	public boolean shouldExecute() {
		if (entity.isGolemInactive()) return false;

		IGolem.Command command = entity.getGolemCommand();
		if (command == IGolem.Command.HOLD_POSITION || command == IGolem.Command.PATROL_AREA) {
			BlockPos pos = creature.getPosition();
			return !pos.equals(creature.getHomePosition()) && super.shouldExecute();
		}
		return false;
	}

	@Override
	@Nullable
	protected Vector3d getPosition() {
		BlockPos pos = creature.getPosition();
		if (!pos.equals(creature.getHomePosition())) {
			IGolem.Command command = entity.getGolemCommand();
			if (command == IGolem.Command.PATROL_AREA) {
				return RandomPositionGenerator.findRandomTargetBlockTowards(creature, 10, 7, Vector3d.copyCenteredHorizontally(creature.getHomePosition())); // doesn't include fluids
			}
			if (command == IGolem.Command.HOLD_POSITION) {
				return Vector3d.copyCenteredHorizontally(creature.getHomePosition());
			}
		}
		return null;
	}
}
