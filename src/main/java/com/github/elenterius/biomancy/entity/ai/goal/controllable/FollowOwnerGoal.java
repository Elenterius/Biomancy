package com.github.elenterius.biomancy.entity.ai.goal.controllable;

import com.github.elenterius.biomancy.entity.ownable.IControllableMob;
import com.github.elenterius.biomancy.ownable.IOwnableMob;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.level.pathfinder.WalkNodeEvaluator;

import java.util.EnumSet;
import java.util.Optional;

public class FollowOwnerGoal<T extends Mob & IOwnableMob & IControllableMob> extends Goal {

	private final T entity;
	private LivingEntity entityOwner;
	private final LevelReader world;
	private final double speed;
	private final PathNavigation navigator;
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
		if (!(goalOwner.getNavigation() instanceof GroundPathNavigation) && !(goalOwner.getNavigation() instanceof FlyingPathNavigation)) {
			throw new IllegalArgumentException("Unsupported mob type for FollowOwnerGoal");
		}
	}

	@Override
	public boolean canUse() {
		if (!entity.canExecuteCommand() || entity.getActiveCommand() != IControllableMob.Command.DEFEND_OWNER) {
			return false;
		}

		Optional<Player> optional = entity.getOwnerAsPlayer();
		if (optional.isPresent()) {
			if (optional.get().isSpectator()) return false;

			if (entity.distanceToSqr(optional.get()) < minDist * minDist) return false;

			entityOwner = optional.get();
			return true;
		}

		return false;
	}

	@Override
	public boolean canContinueToUse() {
		if (!entity.canExecuteCommand() || entity.getActiveCommand() != IControllableMob.Command.DEFEND_OWNER) {
			return false;
		}

		if (navigator.isDone()) return false;

		return entity.distanceToSqr(entityOwner) > maxDist * maxDist;
	}

	@Override
	public void start() {
		pathingDelay = 0;
		oldWaterCost = entity.getPathfindingMalus(BlockPathTypes.WATER);
		entity.setPathfindingMalus(BlockPathTypes.WATER, 0f);
	}

	@Override
	public void stop() {
		entityOwner = null;
		navigator.stop();
		entity.setPathfindingMalus(BlockPathTypes.WATER, oldWaterCost);
	}

	@Override
	public void tick() {
		entity.getLookControl().setLookAt(entityOwner, 10f, entity.getMaxHeadXRot());
		if (--pathingDelay <= 0) {
			pathingDelay = 10;
			if (!entity.isLeashed() && !entity.isPassenger()) {
				if (entity.distanceToSqr(entityOwner) >= 144d) {
					tryToTeleportNearEntity();
					return;
				}

				navigator.moveTo(entityOwner, speed);
			}
		}
	}

	private void tryToTeleportNearEntity() {
		BlockPos blockpos = entityOwner.blockPosition();
		for (int i = 0; i < 10; i++) {
			int x = getRandomNumber(-3, 3);
			int y = getRandomNumber(-1, 1);
			int z = getRandomNumber(-3, 3);
			if (tryToTeleportToLocation(blockpos.getX() + x, blockpos.getY() + y, blockpos.getZ() + z)) {
				return;
			}
		}
	}

	private boolean tryToTeleportToLocation(int x, int y, int z) {
		if (Math.abs(x - entityOwner.getX()) < 2d && Math.abs(z - entityOwner.getZ()) < 2d) {
			return false;
		}

		if (!isTeleportFriendlyBlock(new BlockPos(x, y, z))) {
			return false;
		}

		entity.moveTo(x + 0.5d, y, z + 0.5d, entity.getYRot(), entity.getXRot());
		navigator.stop();
		return true;
	}

	private boolean isTeleportFriendlyBlock(BlockPos pos) {
		BlockPathTypes pathTypes = WalkNodeEvaluator.getBlockPathTypeStatic(world, pos.mutable());
		if (pathTypes != BlockPathTypes.WALKABLE) {
			return false;
		}

		BlockState blockstate = world.getBlockState(pos.below());
		if (!teleportToLeaves && blockstate.getBlock() instanceof LeavesBlock) {
			return false;
		}

		BlockPos blockpos = pos.subtract(entity.blockPosition());
		return world.noCollision(entity, entity.getBoundingBox().move(blockpos));
	}

	private int getRandomNumber(int min, int max) {
		return entity.getRandom().nextInt(max - min + 1) + min;
	}

}
