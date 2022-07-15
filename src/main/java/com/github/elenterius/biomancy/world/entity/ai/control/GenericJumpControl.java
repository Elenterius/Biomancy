package com.github.elenterius.biomancy.world.entity.ai.control;

import com.github.elenterius.biomancy.world.entity.JumpMoveMob;
import net.minecraft.world.entity.ai.control.JumpControl;

public class GenericJumpControl extends JumpControl {

	private final JumpMoveMob<?> jumpMoveMob;
	private boolean canJump;

	public GenericJumpControl(JumpMoveMob<?> jumpMoveMob) {
		super(jumpMoveMob.getJumpingEntity());
		this.jumpMoveMob = jumpMoveMob;
	}

	public boolean isJumping() {
		return jump;
	}

	public boolean canJump() {
		return canJump;
	}

	public void setCanJump(boolean canJumpIn) {
		canJump = canJumpIn;
	}

	@Override
	public void tick() {
		if (jump) {
			jumpMoveMob.startJumping();
			jump = false;
		}
	}

}
