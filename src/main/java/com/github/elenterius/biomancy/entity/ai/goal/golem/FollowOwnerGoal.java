package com.github.elenterius.biomancy.entity.ai.goal.golem;

import com.github.elenterius.biomancy.entity.golem.IGolem;
import com.github.elenterius.biomancy.entity.golem.IOwnableCreature;
import net.minecraft.block.BlockState;
import net.minecraft.block.LeavesBlock;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.pathfinding.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorldReader;

import java.util.EnumSet;
import java.util.Optional;

public class FollowOwnerGoal<T extends CreatureEntity & IOwnableCreature & IGolem> extends Goal {

	private final T entity;
	private LivingEntity entityOwner;
	private final IWorldReader world;
	private final double speed;
	private final PathNavigator navigator;
	private final float maxDist;
	private final float minDist;
	private final boolean teleportToLeaves;
	private int pathingDelay;
	private float oldWaterCost;

	public FollowOwnerGoal(T goalOwner, double speedIn, float minDistIn, float maxDistIn, boolean teleportToLeaves) {
		entity = goalOwner;
		world = goalOwner.world;
		speed = speedIn;
		navigator = goalOwner.getNavigator();
		minDist = minDistIn;
		maxDist = maxDistIn;
		this.teleportToLeaves = teleportToLeaves;
		this.setMutexFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
		if (!(goalOwner.getNavigator() instanceof GroundPathNavigator) && !(goalOwner.getNavigator() instanceof FlyingPathNavigator)) {
			throw new IllegalArgumentException("Unsupported mob type for FollowOwnerGoal");
		}
	}

	public boolean shouldExecute() {
		if (entity.isGolemInactive() || entity.getGolemCommand() != IGolem.Command.DEFEND_OWNER) {
			return false;
		}

		Optional<PlayerEntity> optional = entity.getOwner();
		if (optional.isPresent()) {
			if (optional.get().isSpectator()) {
				return false;
			}
			else if (entity.getDistanceSq(optional.get()) < (double) (minDist * minDist)) {
				return false;
			}
			else {
				entityOwner = optional.get();
				return true;
			}
		}

		return false;
	}

	public boolean shouldContinueExecuting() {
		if (entity.isGolemInactive() || entity.getGolemCommand() != IGolem.Command.DEFEND_OWNER) {
			return false;
		}
		else if (navigator.noPath()) {
			return false;
		}
		else {
			return !(entity.getDistanceSq(entityOwner) <= (double) (maxDist * maxDist));
		}
	}

	public void startExecuting() {
		pathingDelay = 0;
		oldWaterCost = entity.getPathPriority(PathNodeType.WATER);
		entity.setPathPriority(PathNodeType.WATER, 0.0F);
	}

	public void resetTask() {
		entityOwner = null;
		navigator.clearPath();
		entity.setPathPriority(PathNodeType.WATER, oldWaterCost);
	}

	public void tick() {
		entity.getLookController().setLookPositionWithEntity(entityOwner, 10.0F, entity.getVerticalFaceSpeed());
		if (--pathingDelay <= 0) {
			pathingDelay = 10;
			if (!entity.getLeashed() && !entity.isPassenger()) {
				if (entity.getDistanceSq(entityOwner) >= 144d) {
					tryToTeleportNearEntity();
				}
				else {
					navigator.tryMoveToEntityLiving(entityOwner, speed);
				}

			}
		}
	}

	private void tryToTeleportNearEntity() {
		BlockPos blockpos = entityOwner.getPosition();
		for (int i = 0; i < 10; ++i) {
			int j = getRandomNumber(-3, 3);
			int k = getRandomNumber(-1, 1);
			int l = getRandomNumber(-3, 3);
			if (tryToTeleportToLocation(blockpos.getX() + j, blockpos.getY() + k, blockpos.getZ() + l)) {
				return;
			}
		}
	}

	private boolean tryToTeleportToLocation(int x, int y, int z) {
		if (Math.abs(x - entityOwner.getPosX()) < 2d && Math.abs(z - entityOwner.getPosZ()) < 2d) {
			return false;
		}
		else if (!isTeleportFriendlyBlock(new BlockPos(x, y, z))) {
			return false;
		}
		else {
			entity.setLocationAndAngles(x + 0.5d, y, z + 0.5d, entity.rotationYaw, entity.rotationPitch);
			navigator.clearPath();
			return true;
		}
	}

	private boolean isTeleportFriendlyBlock(BlockPos pos) {
		PathNodeType pathnodetype = WalkNodeProcessor.func_237231_a_(world, pos.toMutable());
		if (pathnodetype != PathNodeType.WALKABLE) {
			return false;
		}
		else {
			BlockState blockstate = world.getBlockState(pos.down());
			if (!this.teleportToLeaves && blockstate.getBlock() instanceof LeavesBlock) {
				return false;
			}
			else {
				BlockPos blockpos = pos.subtract(entity.getPosition());
				return world.hasNoCollisions(entity, entity.getBoundingBox().offset(blockpos));
			}
		}
	}

	private int getRandomNumber(int min, int max) {
		return entity.getRNG().nextInt(max - min + 1) + min;
	}

}
