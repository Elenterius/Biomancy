package com.github.elenterius.biomancy.entity.ai.goal;

import com.github.elenterius.biomancy.entity.IPlaceBlockAtPositionMob;
import com.github.elenterius.biomancy.util.BlockPlacementTarget;
import com.github.elenterius.biomancy.util.RayTraceUtil;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.util.math.vector.Vector3d;

import java.util.EnumSet;

public class PlaceBlockAtPositionGoal extends Goal {
	protected final MobEntity goalOwner;
	protected final IPlaceBlockAtPositionMob blockPlacer;
	private int idleTime;
	protected BlockPlacementTarget placementTarget;
	protected final double speed;

	public PlaceBlockAtPositionGoal(IPlaceBlockAtPositionMob goalOwner, double speedMultiplier) {
		this.blockPlacer = goalOwner;
		this.goalOwner = (MobEntity) goalOwner;
		this.speed = speedMultiplier;
		setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
	}

	private BlockPlacementTarget getPlacementTarget() {
		return blockPlacer.getBlockPlacementTarget();
	}

	@Override
	public boolean canUse() {
		BlockPlacementTarget placementTarget = getPlacementTarget();
		if (placementTarget == null || !blockPlacer.hasPlaceableBlock()) return false;
		this.placementTarget = placementTarget.copy();
		return true;
	}

	@Override
	public boolean canContinueToUse() {
		boolean hasPath = goalOwner.getNavigation().isInProgress();
		if (!hasPath && getPlacementTarget() != null) {
			if (++idleTime > 50) {
				blockPlacer.setBlockPlacementTarget(null);
				return false;
			}
			return blockPlacer.hasPlaceableBlock();
		}
		return hasPath && getPlacementTarget() != null;
	}

	public void tick() {
		int x = placementTarget.targetPos.getX();
		int y = placementTarget.targetPos.getY();
		int z = placementTarget.targetPos.getZ();

		goalOwner.getLookControl().setLookAt(x, y, z, 75F, 75f);
		goalOwner.getNavigation().moveTo(x, y, z, speed);

		double distSq = goalOwner.distanceToSqr(x + 0.5d, y + 0.5d, z + 0.5d);
		if (distSq <= 4d * 4d && RayTraceUtil.canEntitySeePosition(goalOwner, Vector3d.atCenterOf(placementTarget.targetPos))) {
			if (blockPlacer.tryToPlaceBlockAtPosition(placementTarget.rayTraceResult, placementTarget.horizontalFacing)) {
				blockPlacer.setBlockPlacementTarget(null);
			}
			goalOwner.getNavigation().stop();
		}
	}

	@Override
	public void stop() {
		goalOwner.getNavigation().stop();
		idleTime = 0;
		placementTarget = null;
	}
}
