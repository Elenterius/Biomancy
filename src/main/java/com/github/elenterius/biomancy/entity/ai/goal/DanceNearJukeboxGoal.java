package com.github.elenterius.biomancy.entity.ai.goal;

import com.github.elenterius.biomancy.entity.JukeboxDancer;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal;

import java.util.EnumSet;

public class DanceNearJukeboxGoal<T extends PathfinderMob & JukeboxDancer> extends Goal {

	protected final T mob;

	public DanceNearJukeboxGoal(T mob) {
		this.mob = mob;
		setFlags(EnumSet.of(Flag.MOVE));
	}

	@Override
	public boolean canUse() {
		return mob.getJukeboxPos() != null && mob.isDancing();
	}

	@Override
	public boolean canContinueToUse() {
		return !mob.getNavigation().isDone() && canUse();
	}

	@Override
	public void start() {
		//		BlockPos pos = mob.getJukeboxPos();
		//		if (pos != null) {
		//			float radius = GameEvent.JUKEBOX_PLAY.getNotificationRadius() * 0.45f;
		//			double distanceSqr = pos.distToCenterSqr(mob.position());
		//
		//			PathNavigation navigation = mob.getNavigation();
		//
		//			if (distanceSqr >= Mth.square(radius)) {
		//				navigation.moveTo(navigation.createPath(pos, 4), 0.9f);
		//			}
		//			else {
		//				Set<BlockPos> pathPoints = Set.of(
		//						pos.north(3),
		//						pos.east(3),
		//						pos.south(3),
		//						pos.west(3),
		//						pos.south(2).west(2),
		//						pos.east(2).south(2),
		//						pos.north(2).east(2),
		//						pos.west(2).north(2)
		//				);
		//				navigation.moveTo(navigation.createPath(pathPoints, 3), 0.7f);
		//			}
		//		}
	}

	@Override
	public void stop() {
		mob.getNavigation().stop();
	}

}
