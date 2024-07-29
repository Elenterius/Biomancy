package com.github.elenterius.biomancy.entity.mob.ai.goal;

import com.github.elenterius.biomancy.init.ModMobEffects;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;

public class FrenzyMeleeAttackGoal extends MeleeAttackGoal {

	public FrenzyMeleeAttackGoal(PathfinderMob goalOwner, double speedModifier, boolean followingTargetEvenIfNotSeen) {
		super(goalOwner, speedModifier, followingTargetEvenIfNotSeen);
	}

	@Override
	public boolean canUse() {
		if (!mob.hasEffect(ModMobEffects.FRENZY.get())) return false;

		return super.canUse();
	}

	@Override
	public boolean canContinueToUse() {
		if (!mob.hasEffect(ModMobEffects.FRENZY.get())) return false;

		return super.canContinueToUse();
	}

}
