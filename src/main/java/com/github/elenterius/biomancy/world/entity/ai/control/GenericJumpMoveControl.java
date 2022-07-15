package com.github.elenterius.biomancy.world.entity.ai.control;

import com.github.elenterius.biomancy.world.entity.JumpMoveMob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.control.MoveControl;

public class GenericJumpMoveControl extends MoveControl {

	private final JumpMoveMob<? extends PathfinderMob> jumpingMob;
	private double nextJumpSpeed;

	public GenericJumpMoveControl(JumpMoveMob<?> jumpingMob) {
		super(jumpingMob.getJumpingEntity());
		this.jumpingMob = jumpingMob;
	}

	@Override
	public void tick() {
		GenericJumpMoveHelper<?> jumpMovementState = jumpingMob.getJumpMoveState();
		if (mob.isOnGround() && !jumpMovementState.isJumping(jumpingMob) && !(jumpMovementState.jumpController.isJumping())) {
			jumpMovementState.setMoveSpeed(jumpingMob, 0);
		}
		else if (hasWanted()) {
			jumpMovementState.setMoveSpeed(jumpingMob, nextJumpSpeed);
		}

		super.tick();
	}

	@Override
	public void setWantedPosition(double x, double y, double z, double speed) {
		if (mob.isInWater()) {
			speed = 1.5d;
		}
		super.setWantedPosition(x, y, z, speed);
		if (speed > 0) {
			nextJumpSpeed = speed;
		}
	}

}
