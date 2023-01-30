package com.github.elenterius.biomancy.world.entity.ai.goal;

import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.PanicGoal;

public class BurningOrFreezingPanicGoal extends PanicGoal {

	public BurningOrFreezingPanicGoal(PathfinderMob mob, double speedModifier) {
		super(mob, speedModifier);
	}

	@Override
	protected boolean shouldPanic() {
		return mob.isFreezing() || mob.isOnFire();
	}

	@Override
	public boolean canContinueToUse() {
		if (shouldPanic()) return super.canContinueToUse();
		return false;
	}

}
