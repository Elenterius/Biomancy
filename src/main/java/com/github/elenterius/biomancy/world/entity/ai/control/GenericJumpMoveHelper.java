package com.github.elenterius.biomancy.world.entity.ai.control;

import com.github.elenterius.biomancy.world.entity.JumpMoveMob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.phys.Vec3;

public class GenericJumpMoveHelper<T extends JumpMoveMob<? extends PathfinderMob>> {

	public static final int MAX_JUMP_DURATION = 20;

	private final byte stateUpdateId;

	private boolean wasOnGround;
	private int jumpTicks;
	private int jumpDuration;
	private int delay;

	GenericJumpControl jumpController;
	GenericJumpMoveControl moveController;

	public GenericJumpMoveHelper(T mob, byte stateUpdateId) {
		this.stateUpdateId = stateUpdateId;
		jumpController = new GenericJumpControl(mob);
		moveController = new GenericJumpMoveControl(mob);
		mob.setJumpControl(jumpController);
		mob.setMoveControl(moveController);
		setMoveSpeed(mob, 0);
	}

	public void onJumpFromGround(JumpMoveMob<?> mob) {
		if (moveController.getSpeedModifier() > 0 && mob.getJumpingEntity().getDeltaMovement().horizontalDistanceSqr() < 0.01d) {
			mob.getJumpingEntity().moveRelative(0.1f, new Vec3(0, 0, 1));
		}

		if (!mob.getJumpingEntity().level.isClientSide) {
			mob.getJumpingEntity().level.broadcastEntityEvent(mob.getJumpingEntity(), stateUpdateId);
		}
	}

	public void onServerAiStep(JumpMoveMob<?> mob) {
		if (delay > 0) --delay;

		if (mob.getJumpingEntity().isOnGround()) {
			if (!wasOnGround) {
				mob.getJumpingEntity().setJumping(false);
				delay = moveController.getSpeedModifier() < 2.2d ? MAX_JUMP_DURATION : 1;
				jumpController.setCanJump(false);
			}

			if (!jumpController.isJumping()) {
				if (delay == 0 && moveController.hasWanted()) {
					Path path = mob.getJumpingEntity().getNavigation().getPath();
					Vec3 heading = new Vec3(moveController.getWantedX(), moveController.getWantedY(), moveController.getWantedZ());
					if (path != null && !path.isDone()) {
						heading = path.getNextEntityPos(mob.getJumpingEntity());
					}

					mob.setJumpHeading(heading.x, heading.z);
					mob.startJumping();
				}
			}
			else if (!jumpController.canJump()) {
				jumpController.setCanJump(true);
			}
		}

		wasOnGround = mob.getJumpingEntity().isOnGround();
	}

	public void onAiStep(JumpMoveMob<?> mob) {
		if (jumpTicks != jumpDuration) {
			jumpTicks++;
		}
		else if (jumpDuration != 0) {
			jumpTicks = 0;
			jumpDuration = 0;
			mob.getJumpingEntity().setJumping(false);
		}
	}

	public boolean handleEntityEvent(byte id) {
		if (id == stateUpdateId) {
			onStartJumping(MAX_JUMP_DURATION);
			return true;
		}
		return false;
	}

	public void onStartJumping(int jumpDuration) {
		this.jumpDuration = jumpDuration;
		jumpTicks = 0;
	}

	public boolean isJumping(JumpMoveMob<?> mob) {
		return mob.isJumping();
	}

	public void setMoveSpeed(JumpMoveMob<?> mob, double speed) {
		mob.getJumpingEntity().getNavigation().setSpeedModifier(speed);
		moveController.setWantedPosition(moveController.getWantedX(), moveController.getWantedY(), moveController.getWantedZ(), speed);
	}

	public float getJumpCompletionPct(float partialTicks) {
		return jumpDuration == 0 ? 0 : (jumpTicks + partialTicks) / jumpDuration;
	}

}
