package com.github.elenterius.blightlings.entity;

import com.github.elenterius.blightlings.entity.ai.controller.GenericJumpController;
import com.github.elenterius.blightlings.entity.ai.controller.GenericJumpMovementController;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MobEntity;
import net.minecraft.pathfinding.Path;
import net.minecraft.util.math.vector.Vector3d;

public class GenericJumpMovementHelper<T extends IJumpMovementMob<? extends MobEntity>> {
	public final byte stateUpdateId;
	public int jumpTicks;
	public int jumpDuration;
	public boolean wasOnGround;
	public int currentMoveTypeDuration;
	public GenericJumpController jumpController;
	public GenericJumpMovementController moveController;

	public GenericJumpMovementHelper(T mob, byte stateUpdateId) {
		this.stateUpdateId = stateUpdateId;
		jumpController = new GenericJumpController(mob);
		moveController = new GenericJumpMovementController(mob);
		mob.setJumpController(jumpController);
		mob.setMovementController(moveController);
		setMovementSpeed(mob, 0d);
	}

	public void setMovementSpeed(IJumpMovementMob<?> mob, double speed) {
		mob.getJumpingEntity().getNavigator().setSpeed(speed);
		moveController.setMoveTo(moveController.getX(), moveController.getY(), moveController.getZ(), speed);
	}

	public void updateAIMovement(IJumpMovementMob<?> mob) {
		if (currentMoveTypeDuration > 0) {
			--currentMoveTypeDuration;
		}

		if (mob.getJumpingEntity().isOnGround()) {
			if (!wasOnGround) {
				mob.getJumpingEntity().setJumping(false);
				checkLandingDelay();
			}

			if (!jumpController.isJumping()) {
				if (moveController.isUpdating() && currentMoveTypeDuration == 0) {
					Path path = mob.getJumpingEntity().getNavigator().getPath();
					Vector3d heading = new Vector3d(moveController.getX(), moveController.getY(), moveController.getZ());
					if (path != null && !path.isFinished()) {
						heading = path.getPosition(mob.getJumpingEntity());
					}

					mob.updateRotationYaw(heading.x, heading.z);
					mob.startJumping();
				}
			}
			else if (!jumpController.canJump()) {
				jumpController.setCanJump(true);
			}
		}

		wasOnGround = mob.getJumpingEntity().isOnGround();
	}

	public void updateTick(IJumpMovementMob<?> mob) {
		if (jumpTicks != jumpDuration) {
			++jumpTicks;
		}
		else if (jumpDuration != 0) {
			jumpTicks = 0;
			jumpDuration = 0;
			mob.getJumpingEntity().setJumping(false);
		}
	}

	public void updateJump(IJumpMovementMob<?> mob) {
		if (moveController.getSpeed() > 0.0D) {
			if (Entity.horizontalMag(mob.getJumpingEntity().getMotion()) < 0.01D) {
				mob.getJumpingEntity().moveRelative(0.1F, new Vector3d(0.0D, 0.0D, 1.0D));
			}
		}

		if (!mob.getJumpingEntity().world.isRemote) {
			mob.getJumpingEntity().world.setEntityState(mob.getJumpingEntity(), stateUpdateId);
		}
	}

	public void onEntityStateUpdate() {
		jumpDuration = 10;
		jumpTicks = 0;
	}

	private void updateMoveTypeDuration() {
		if (moveController.getSpeed() < 2.2D) {
			currentMoveTypeDuration = 10;
		}
		else {
			currentMoveTypeDuration = 1;
		}
	}

	private void checkLandingDelay() {
		updateMoveTypeDuration();
		jumpController.setCanJump(false);
	}

	public boolean isJumping(IJumpMovementMob<?> mob) {
		return mob.isJumping();
	}
}
