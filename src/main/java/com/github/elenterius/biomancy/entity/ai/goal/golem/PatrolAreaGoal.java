package com.github.elenterius.biomancy.entity.ai.goal.golem;

import com.github.elenterius.biomancy.entity.golem.IGolem;
import com.github.elenterius.biomancy.entity.golem.IOwnableCreature;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.entity.ai.goal.RandomWalkingGoal;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.vector.Vector3d;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;

public class PatrolAreaGoal<T extends CreatureEntity & IOwnableCreature & IGolem> extends RandomWalkingGoal {

	private final T entity;

	public PatrolAreaGoal(T goalOwner, double speedIn) {
		super(goalOwner, speedIn, 240, false);
		entity = goalOwner;
	}

	@Override
	public boolean shouldExecute() {
		if (!entity.isGolemInactive()) {
			IGolem.Command command = entity.getGolemCommand();
			if (command == IGolem.Command.PATROL_AREA || command == IGolem.Command.DEFEND_OWNER) {
				return super.shouldExecute();
			}
		}
		return false;
	}

	@Override
	@Nullable
	protected Vector3d getPosition() {
		if (entity.getGolemCommand() == IGolem.Command.DEFEND_OWNER) {
			Optional<PlayerEntity> owner = entity.getOwner();
			return owner.isPresent() ? findPosTowards(owner.get().getPositionVec()) : findPosNearby();
		}
		if (creature.world.rand.nextFloat() < 0.3F) {
			return findPosNearby();
		}
		else {
			Vector3d pos = findPosTowardsRandomPlayer();
			return pos != null ? pos : findPosNearby();
		}
	}

	@Nullable
	private Vector3d findPosTowards(Vector3d pos) {
		return RandomPositionGenerator.func_234133_a_(creature, 10, 7, pos); // includes water
	}

	@Nullable
	private Vector3d findPosNearby() {
		return RandomPositionGenerator.getLandPos(creature, 10, 7);
	}

	@Nullable
	private Vector3d findPosTowardsRandomPlayer() {
		List<PlayerEntity> list = creature.world.getEntitiesWithinAABB(PlayerEntity.class, creature.getBoundingBox().grow(32d));
		if (!list.isEmpty()) {
			PlayerEntity player = list.get(creature.world.rand.nextInt(list.size()));
			return RandomPositionGenerator.func_234133_a_(creature, 10, 7, player.getPositionVec());
		}
		return null;
	}
}
