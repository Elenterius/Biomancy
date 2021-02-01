package com.github.elenterius.biomancy.entity.ai.controller;

import com.github.elenterius.biomancy.entity.IJumpMovementMob;
import net.minecraft.entity.ai.controller.JumpController;

public class GenericJumpController extends JumpController {
	private final IJumpMovementMob<?> jumpMoveMob;
	private boolean canJump;

	public GenericJumpController(IJumpMovementMob<?> jumpMoveMob) {
		super(jumpMoveMob.getJumpingEntity());
		this.jumpMoveMob = jumpMoveMob;
	}

	public boolean isJumping() {
		return isJumping;
	}

	public boolean canJump() {
		return canJump;
	}

	public void setCanJump(boolean canJumpIn) {
		canJump = canJumpIn;
	}

	@Override
	public void tick() {
		if (isJumping) {
			jumpMoveMob.startJumping();
			isJumping = false;
		}
	}
}
