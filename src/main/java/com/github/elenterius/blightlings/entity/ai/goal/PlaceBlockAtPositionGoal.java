package com.github.elenterius.blightlings.entity.ai.goal;

import com.github.elenterius.blightlings.entity.IPlaceBlockAtPositionMob;
import com.github.elenterius.blightlings.util.BlockPlacementTarget;
import com.github.elenterius.blightlings.util.RayTraceUtil;
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
        setMutexFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    private BlockPlacementTarget getPlacementTarget() {
        return blockPlacer.getBlockPlacementTarget();
    }

    @Override
    public boolean shouldExecute() {
        BlockPlacementTarget placementTarget = getPlacementTarget();
        if (placementTarget == null || !blockPlacer.hasPlaceableBlock()) return false;
        this.placementTarget = placementTarget.copy();
        return true;
    }

    @Override
    public boolean shouldContinueExecuting() {
        boolean hasPath = goalOwner.getNavigator().hasPath();
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

        goalOwner.getLookController().setLookPosition(x, y, z, 75F, 75f);
        goalOwner.getNavigator().tryMoveToXYZ(x, y, z, speed);

        double distSq = goalOwner.getDistanceSq(x + 0.5d, y + 0.5d, z + 0.5d);
        if (distSq <= 4d * 4d && RayTraceUtil.canEntitySeePosition(goalOwner, Vector3d.copyCentered(placementTarget.targetPos))) {
            if (blockPlacer.tryToPlaceBlockAtPosition(placementTarget.rayTraceResult)) {
                System.out.println("block was placed!");
                blockPlacer.setBlockPlacementTarget(null);
            }
            goalOwner.getNavigator().clearPath();
        }
    }

    @Override
    public void resetTask() {
        goalOwner.getNavigator().clearPath();
        idleTime = 0;
        placementTarget = null;
    }
}
