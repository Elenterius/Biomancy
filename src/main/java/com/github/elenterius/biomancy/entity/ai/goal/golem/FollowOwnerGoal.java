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
		world = goalOwner.level;
		speed = speedIn;
		navigator = goalOwner.getNavigation();
		minDist = minDistIn;
		maxDist = maxDistIn;
		this.teleportToLeaves = teleportToLeaves;
		this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
		if (!(goalOwner.getNavigation() instanceof GroundPathNavigator) && !(goalOwner.getNavigation() instanceof FlyingPathNavigator)) {
			throw new IllegalArgumentException("Unsupported mob type for FollowOwnerGoal");
		}
	}

	public boolean canUse() {
		if (entity.isGolemInactive() || entity.getGolemCommand() != IGolem.Command.DEFEND_OWNER) {
			return false;
		}

		Optional<PlayerEntity> optional = entity.getOwner();
		if (optional.isPresent()) {
			if (optional.get().isSpectator()) {
				return false;
			}
			else if (entity.distanceToSqr(optional.get()) < (double) (minDist * minDist)) {
				return false;
			}
			else {
				entityOwner = optional.get();
				return true;
			}
		}

		return false;
	}

	public boolean canContinueToUse() {
		if (entity.isGolemInactive() || entity.getGolemCommand() != IGolem.Command.DEFEND_OWNER) {
			return false;
		}
		else if (navigator.isDone()) {
			return false;
		}
		else {
			return !(entity.distanceToSqr(entityOwner) <= (double) (maxDist * maxDist));
		}
	}

	public void start() {
		pathingDelay = 0;
		oldWaterCost = entity.getPathfindingMalus(PathNodeType.WATER);
		entity.setPathfindingMalus(PathNodeType.WATER, 0f);
	}

	public void stop() {
		entityOwner = null;
		navigator.stop();
		entity.setPathfindingMalus(PathNodeType.WATER, oldWaterCost);
	}

	public void tick() {
		entity.getLookControl().setLookAt(entityOwner, 10f, entity.getMaxHeadXRot());
		if (--pathingDelay <= 0) {
			pathingDelay = 10;
			if (!entity.isLeashed() && !entity.isPassenger()) {
				if (entity.distanceToSqr(entityOwner) >= 144d) {
					tryToTeleportNearEntity();
				}
				else {
					navigator.moveTo(entityOwner, speed);
				}

			}
		}
	}

	private void tryToTeleportNearEntity() {
		BlockPos blockpos = entityOwner.blockPosition();
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
		if (Math.abs(x - entityOwner.getX()) < 2d && Math.abs(z - entityOwner.getZ()) < 2d) {
			return false;
		}
		else if (!isTeleportFriendlyBlock(new BlockPos(x, y, z))) {
			return false;
		}
		else {
			entity.moveTo(x + 0.5d, y, z + 0.5d, entity.yRot, entity.xRot);
			navigator.stop();
			return true;
		}
	}

	private boolean isTeleportFriendlyBlock(BlockPos pos) {
		PathNodeType pathnodetype = WalkNodeProcessor.getBlockPathTypeStatic(world, pos.mutable());
		if (pathnodetype != PathNodeType.WALKABLE) {
			return false;
		}
		else {
			BlockState blockstate = world.getBlockState(pos.below());
			if (!this.teleportToLeaves && blockstate.getBlock() instanceof LeavesBlock) {
				return false;
			}
			else {
				BlockPos blockpos = pos.subtract(entity.blockPosition());
				return world.noCollision(entity, entity.getBoundingBox().move(blockpos));
			}
		}
	}

	private int getRandomNumber(int min, int max) {
		return entity.getRandom().nextInt(max - min + 1) + min;
	}

}
