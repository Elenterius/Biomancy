package com.github.elenterius.blightlings.entity.ai.goal;

import com.github.elenterius.blightlings.entity.AbstractUtilityEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.player.PlayerEntity;

import java.util.EnumSet;

public class ReturnToOwnerGoal extends Goal {
    protected final AbstractUtilityEntity goalOwner;
    protected final double speed;
    private LivingEntity entityOwner;

    public ReturnToOwnerGoal(AbstractUtilityEntity entity, double speed) {
        this.goalOwner = entity;
        this.speed = speed;
        setMutexFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
    }

    @Override
    public boolean shouldExecute() {
        if (goalOwner.getTargetBlockPos() != null || goalOwner.getAttackTarget() != null) return false;
        PlayerEntity playerEntity = goalOwner.getOwner().orElse(null);
        if (playerEntity == null) return false;
        if (playerEntity.isSpectator() || goalOwner.getDistanceSq(playerEntity) < 3d * 3d) return false;
        entityOwner = playerEntity;
        return true;
    }

    @Override
    public boolean shouldContinueExecuting() {
        return !goalOwner.getNavigator().noPath() && !(goalOwner.getDistanceSq(entityOwner) <= 1.25d * 1.25d);
    }

    @Override
    public void tick() {
        goalOwner.getLookController().setLookPositionWithEntity(entityOwner, 10.0F, goalOwner.getVerticalFaceSpeed());
        goalOwner.getNavigator().tryMoveToEntityLiving(entityOwner, speed);
    }

    @Override
    public void resetTask() {
        goalOwner.getNavigator().clearPath();
        entityOwner = null;
    }
}
