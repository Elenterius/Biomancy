package com.github.elenterius.blightlings.entity.ai.goal;

import com.github.elenterius.blightlings.entity.SwarmGroupMemberEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.goal.Goal;

import java.util.List;
import java.util.function.Predicate;

public class FollowSwarmLeaderGoal extends Goal {
	private final SwarmGroupMemberEntity taskOwner;
	private int navigateTimer;
	private int joinGroupDelay;

	public FollowSwarmLeaderGoal(SwarmGroupMemberEntity taskOwner) {
		this.taskOwner = taskOwner;
		joinGroupDelay = rndDelay(taskOwner);
	}

	protected int rndDelay(SwarmGroupMemberEntity taskOwner) {
		return 200 + taskOwner.getRNG().nextInt(200) % 20;
	}

	@Override
	public boolean shouldExecute() {
		if (taskOwner.isLeader()) {
			return false;
		} else if (taskOwner.hasLeader()) {
			return true;
		} else if (joinGroupDelay > 0) {
			--joinGroupDelay;
			return false;
		} else {
			joinGroupDelay = rndDelay(taskOwner);
			Predicate<SwarmGroupMemberEntity> predicate = (groupMember) -> groupMember.canGroupGrow() || !groupMember.hasLeader();
			MobEntity ownerEntity = taskOwner.asMobEntity();
			List<SwarmGroupMemberEntity> list = ownerEntity.world.getEntitiesWithinAABB(SwarmGroupMemberEntity.class, ownerEntity.getBoundingBox().grow(8.0D, 8.0D, 8.0D), predicate);
			SwarmGroupMemberEntity groupLeader = list.stream().filter(SwarmGroupMemberEntity::canGroupGrow).findAny().orElse(taskOwner);
			groupLeader.addGroupMembers(list.stream().filter((entity) -> !entity.hasLeader()));
			return taskOwner.hasLeader();
		}
	}

	@Override
	public boolean shouldContinueExecuting() {
		return taskOwner.hasLeader() && taskOwner.inRangeOfLeader((SwarmGroupMemberEntity) taskOwner.getLeader().asMobEntity());
	}

	@Override
	public void startExecuting() {
		navigateTimer = 0;
	}

	@Override
	public void resetTask() {
		taskOwner.leaveGroup();
	}

	@Override
	public void tick() {
		if (--navigateTimer <= 0) {
			navigateTimer = 10;
			if (taskOwner.hasLeader()) {
				MobEntity leader = taskOwner.getLeader().asMobEntity();
				if (taskOwner.getAttackTarget() == null && leader.getAttackTarget() != null && leader.getAttackTarget().isAlive()) {
					taskOwner.setAttackTarget(leader.getAttackTarget());
				}
				if (taskOwner.getDistanceSq(leader) >= 16D) {
					taskOwner.moveToLeader();
				}
			}
		}
	}
}
