package com.github.elenterius.biomancy.entity.ai.goal;

import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;

import javax.annotation.Nullable;
import java.util.EnumSet;

public class RandomFlyGoal extends Goal {

	protected final double speed;
	protected final CreatureEntity goalOwner;

	public RandomFlyGoal(CreatureEntity entityIn, double speedIn) {
		goalOwner = entityIn;
		speed = speedIn;
		setMutexFlags(EnumSet.of(Flag.MOVE));
	}

	@Override
	public boolean shouldExecute() {
		return goalOwner.getNavigator().noPath() && goalOwner.getRNG().nextInt(12) == 0;
	}

	@Override
	public boolean shouldContinueExecuting() {
		return goalOwner.getNavigator().hasPath();
	}

	@Override
	public void startExecuting() {
		Vector3d vec = getRandomPos();
		if (vec != null) goalOwner.getNavigator().setPath(goalOwner.getNavigator().getPathToPos(new BlockPos(vec), 1), speed);
	}

	@Nullable
	protected Vector3d getRandomPos() {
		Vector3d lookVec = goalOwner.getLook(0f);
		Vector3d posVec = RandomPositionGenerator.findAirTarget(goalOwner, 8, 7, lookVec, ((float) Math.PI / 2F), 2, 1);
		return posVec != null ? posVec : RandomPositionGenerator.findGroundTarget(goalOwner, 8, 4, -2, lookVec, (float) Math.PI / 2F);
	}

}
