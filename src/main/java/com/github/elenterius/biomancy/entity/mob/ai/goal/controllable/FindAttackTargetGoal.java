package com.github.elenterius.biomancy.entity.mob.ai.goal.controllable;

import com.github.elenterius.biomancy.entity.mob.ControllableMob;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;

import javax.annotation.Nullable;
import java.util.function.Predicate;

public class FindAttackTargetGoal<T extends Mob & ControllableMob, M extends LivingEntity> extends NearestAttackableTargetGoal<M> {

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
		return entity.canExecuteCommand() && entity.getActiveCommand() != ControllableMob.Command.HOLD_POSITION && super.canUse();
	}

}
