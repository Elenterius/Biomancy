package com.github.elenterius.biomancy.world.entity.ai.goal.controllable;

import com.github.elenterius.biomancy.world.entity.ownable.IControllableMob;
import com.github.elenterius.biomancy.world.ownable.IOwnableMob;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.target.TargetGoal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.player.Player;

import java.util.EnumSet;
import java.util.Optional;

public class CopyOwnerAttackTargetGoal<T extends Mob & IOwnableMob & IControllableMob> extends TargetGoal {

	private final T entity;
	private LivingEntity attacker;
	private int lastAttackTime;

	public CopyOwnerAttackTargetGoal(T goalOwner) {
		super(goalOwner, false);
		entity = goalOwner;
		setFlags(EnumSet.of(Goal.Flag.TARGET));
	}

	@Override
	public boolean canUse() {
		if (entity.canExecuteCommand() && entity.getActiveCommand() == IControllableMob.Command.DEFEND_OWNER) {
			Optional<Player> entityOwner = entity.getOwnerAsPlayer();
			if (entityOwner.isPresent()) {
				attacker = entityOwner.get().getLastHurtMob();
				int attackTimer = entityOwner.get().getLastHurtMobTimestamp();
				return attackTimer != lastAttackTime && canAttack(attacker, TargetingConditions.DEFAULT) && entity.shouldAttackEntity(attacker, entityOwner.get());
			}
		}
		return false;
	}

	@Override
	public void start() {
		mob.setTarget(attacker);
		Optional<Player> optional = entity.getOwnerAsPlayer();
		optional.ifPresent(player -> lastAttackTime = player.getLastHurtMobTimestamp());
		super.start();
	}

}
