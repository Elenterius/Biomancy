package com.github.elenterius.biomancy.entity.mob.ai.goal.controllable;

import com.github.elenterius.biomancy.entity.mob.ControllableMob;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.util.LandRandomPos;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;

public class ReturnToHomePosGoal<T extends PathfinderMob & ControllableMob> extends RandomStrollGoal {

	private final T entity;

	public ReturnToHomePosGoal(T goalOwner, double speedIn, boolean stopWhenIdle) {
		super(goalOwner, speedIn, 10, stopWhenIdle);
		entity = goalOwner;
	}

	@Override
	public boolean canUse() {
		if (!entity.canExecuteCommand()) return false;

		ControllableMob.Command command = entity.getActiveCommand();
		if (command == ControllableMob.Command.HOLD_POSITION || command == ControllableMob.Command.PATROL_AREA) {
			BlockPos pos = mob.blockPosition();
			return !pos.equals(mob.getRestrictCenter()) && super.canUse();
		}

		return false;
	}

	@Override
	@Nullable
	protected Vec3 getPosition() {
		BlockPos pos = mob.blockPosition();
		if (!pos.equals(mob.getRestrictCenter())) {
			ControllableMob.Command command = entity.getActiveCommand();
			if (command == ControllableMob.Command.PATROL_AREA) {
				return LandRandomPos.getPosTowards(mob, 10, 7, Vec3.atBottomCenterOf(mob.getRestrictCenter()));
			}

			if (command == ControllableMob.Command.HOLD_POSITION) {
				return Vec3.atBottomCenterOf(mob.getRestrictCenter());
			}
		}
		return null;
	}

}
