package com.github.elenterius.blightlings.entity.ai.goal;

import com.github.elenterius.blightlings.entity.PotionBeetleEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;

import java.util.EnumSet;

public class WalkToPositionGoal extends Goal
{
    protected final PotionBeetleEntity goalOwner;
    protected double x;
    protected double y;
    protected double z;
    protected final double speed;

    public WalkToPositionGoal(PotionBeetleEntity taskOwner, double speedMultiplier) {
        this.goalOwner = taskOwner;
        this.speed = speedMultiplier;
        setMutexFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
    }

    private BlockPos getPosition() {
        return goalOwner.getTargetPos();
    }

    @Override
    public boolean shouldExecute() {
        BlockPos blockPos = getPosition();
        if (blockPos == null) return false;
        Vector3d vec = Vector3d.copyCentered(blockPos);
        x = vec.getX();
        y = vec.getY();
        z = vec.getZ();
        return true;
    }

    @Override
    public boolean shouldContinueExecuting() {
        return !goalOwner.getNavigator().noPath() && getPosition() != null;
    }

    public void tick() {
        goalOwner.getLookController().setLookPosition(x, y, z, 75F, 75f);
        goalOwner.getNavigator().tryMoveToXYZ(x, y, z, speed);
        if (goalOwner.getDistanceSq(x, y, z) < 3d * 3d && goalOwner.tryToUsePotion()) {
            goalOwner.setTargetPos(null);
            resetTask();
        }
    }

    @Override
    public void resetTask() {
        goalOwner.getNavigator().clearPath();
        x = 0;
        y = 0;
        z = 0;
    }
}
