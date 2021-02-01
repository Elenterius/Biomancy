package com.github.elenterius.biomancy.entity.ai.controller;

import com.github.elenterius.biomancy.entity.GenericJumpMovementHelper;
import com.github.elenterius.biomancy.entity.IJumpMovementMob;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.controller.MovementController;

public class GenericJumpMovementController extends MovementController {
	private final IJumpMovementMob<? extends MobEntity> jumpMoveMob;
	private double nextJumpSpeed;

	public GenericJumpMovementController(IJumpMovementMob<?> jumpMoveMob) {
		super(jumpMoveMob.getJumpingEntity());
		this.jumpMoveMob = jumpMoveMob;
	}

	@Override
	public void tick() {
		GenericJumpMovementHelper<?> jumpMovementState = jumpMoveMob.getJumpMovementState();
		if (mob.isOnGround() && !jumpMovementState.isJumping(jumpMoveMob) && !(jumpMovementState.jumpController.isJumping())) {
			jumpMovementState.setMovementSpeed(jumpMoveMob, 0d);
		}
		else if (isUpdating()) {
			jumpMovementState.setMovementSpeed(jumpMoveMob, nextJumpSpeed);
		}

		super.tick();
	}

	@Override
	public void setMoveTo(double x, double y, double z, double speedIn) {
		if (mob.isInWater()) {
			speedIn = 1.5D;
		}

		super.setMoveTo(x, y, z, speedIn);
		if (speedIn > 0.0D) {
			nextJumpSpeed = speedIn;
		}
	}

}
