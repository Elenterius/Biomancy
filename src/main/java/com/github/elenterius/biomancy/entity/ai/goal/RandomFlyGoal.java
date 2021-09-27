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
		setFlags(EnumSet.of(Flag.MOVE));
	}

	@Override
	public boolean canUse() {
		return goalOwner.getNavigation().isDone() && goalOwner.getRandom().nextInt(12) == 0;
	}

	@Override
	public boolean canContinueToUse() {
		return goalOwner.getNavigation().isInProgress();
	}

	@Override
	public void start() {
		Vector3d vec = getRandomPos();
		if (vec != null) goalOwner.getNavigation().moveTo(goalOwner.getNavigation().createPath(new BlockPos(vec), 1), speed);
	}

	@Nullable
	protected Vector3d getRandomPos() {
		Vector3d lookVec = goalOwner.getViewVector(0f);
		Vector3d posVec = RandomPositionGenerator.getAboveLandPos(goalOwner, 8, 7, lookVec, ((float) Math.PI / 2F), 2, 1);
		return posVec != null ? posVec : RandomPositionGenerator.getAirPos(goalOwner, 8, 4, -2, lookVec, (float) Math.PI / 2F);
	}

}
