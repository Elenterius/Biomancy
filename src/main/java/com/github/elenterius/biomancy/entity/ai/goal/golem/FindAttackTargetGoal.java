package com.github.elenterius.biomancy.entity.ai.goal.golem;

import com.github.elenterius.biomancy.entity.golem.IGolem;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.NearestAttackableTargetGoal;

import javax.annotation.Nullable;
import java.util.function.Predicate;

public class FindAttackTargetGoal<T extends CreatureEntity & IGolem, M extends LivingEntity> extends NearestAttackableTargetGoal<M> {

	private final T entity;

	public FindAttackTargetGoal(T goalOwnerIn, Class<M> targetClassIn, boolean checkSight) {
		super(goalOwnerIn, targetClassIn, checkSight);
		entity = goalOwnerIn;
	}

	public FindAttackTargetGoal(T goalOwnerIn, Class<M> targetClassIn, int targetChanceIn, boolean checkSight, boolean nearbyOnlyIn, @Nullable Predicate<LivingEntity> targetPredicate) {
		super(goalOwnerIn, targetClassIn, targetChanceIn, checkSight, nearbyOnlyIn, targetPredicate);
		entity = goalOwnerIn;
	}

	@Override
	public boolean canUse() {
		return !entity.isGolemInactive() && entity.getGolemCommand() != IGolem.Command.HOLD_POSITION && super.canUse();
	}
}
