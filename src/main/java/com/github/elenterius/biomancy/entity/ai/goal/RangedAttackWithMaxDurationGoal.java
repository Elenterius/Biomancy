package com.github.elenterius.biomancy.entity.ai.goal;

import net.minecraft.entity.IRangedAttackMob;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.goal.RangedAttackGoal;

public class RangedAttackWithMaxDurationGoal extends RangedAttackGoal {
	private final int maxTicks;
	protected int chaseTicks = 0;
	protected final MobEntity goalOwner;

	public RangedAttackWithMaxDurationGoal(IRangedAttackMob attacker, double movespeed, int maxAttackTime, float maxAttackDistanceIn, int maxAttackDuration) {
		super(attacker, movespeed, maxAttackTime, maxAttackDistanceIn);
		this.maxTicks = maxAttackDuration;
		goalOwner = (MobEntity) attacker;
	}

	@Override
	public boolean canContinueToUse() {
		if (chaseTicks >= maxTicks) goalOwner.setTarget(null);
		return super.canContinueToUse();
	}

	@Override
	public void tick() {
		super.tick();
		chaseTicks++;
	}

	@Override
	public void stop() {
		super.stop();
		chaseTicks = 0;
	}
}
