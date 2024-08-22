package com.github.elenterius.biomancy.entity.mob;

import com.github.elenterius.biomancy.mixin.accessor.MobEntityAccessor;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.control.JumpControl;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.phys.Vec3;

public class JumpMoveHelper<T extends PathfinderMob & JumpMoveHelper.JumpingPathfinderMob> {

	public static final int MAX_JUMP_DURATION = 20;
	protected static final float MAX_Y_ROT_CHANGE = 90f / 20f;

	private final byte stateUpdateId;
	private final JumpController jumpController;
	private final MoveController moveController;
	private final T jumpingMob;
	private int jumpDuration;
	private int jumpTicks;
	private boolean wasOnGround;
	private int jumpDelay;

	public JumpMoveHelper(T mob, byte stateUpdateId) {
		jumpingMob = mob;

		this.stateUpdateId = stateUpdateId;
		jumpController = new JumpController(mob);
		moveController = new MoveController(mob);

		MobEntityAccessor mobAccessor = (MobEntityAccessor) mob;
		mobAccessor.biomancy$setJumpControl(jumpController);
		mobAccessor.biomancy$setMoveControl(moveController);

		setSpeedModifier(0);
	}

	private void setSpeedModifier(double speed) {
		jumpingMob.getNavigation().setSpeedModifier(speed);
		moveController.setWantedPosition(moveController.getWantedX(), moveController.getWantedY(), moveController.getWantedZ(), speed);
	}

	public void startJumping() {
		jumpingMob.setJumping(true);
		jumpDuration = MAX_JUMP_DURATION;
		jumpTicks = 0;
	}

	public boolean handleEntityEvent(byte id) {
		if (id == stateUpdateId) {
			jumpingMob.spawnJumpParticle();
			jumpDuration = MAX_JUMP_DURATION;
			jumpTicks = 0;
			return true;
		}
		return false;
	}

	public void onJumpFromGround() {
		if (!jumpingMob.level().isClientSide) {
			jumpingMob.level().broadcastEntityEvent(jumpingMob, stateUpdateId);
		}
	}

	public void onAiStep() {
		if (jumpTicks != jumpDuration) {
			jumpTicks++;
		}
		else if (jumpDuration != 0) {
			jumpTicks = 0;
			jumpDuration = 0;
			jumpingMob.setJumping(false);
		}
	}

	public void onCustomServerAiStep() {
		if (jumpDelay > 0) --jumpDelay;

		if (jumpingMob.onGround()) {
			if (!wasOnGround) {
				jumpingMob.setJumping(false);
				jumpDelay = moveController.getSpeedModifier() < 2.2d ? MAX_JUMP_DURATION : 1;
				jumpController.setCanJump(false);
			}

			if (jumpDelay == 0) {
				LivingEntity target = jumpingMob.getTarget();
				if (target != null && jumpingMob.distanceToSqr(target) < 16) {
					moveController.setWantedPosition(target.getX(), target.getY(), target.getZ(), moveController.getSpeedModifier());
					setJumpHeading(target.getX(), target.getZ());
					startJumping();
					wasOnGround = true;
				}
			}

			if (!jumpController.wantsToJump()) {
				if (jumpDelay == 0 && moveController.hasWanted()) {
					Path path = jumpingMob.getNavigation().getPath();

					Vec3 heading;
					if (path != null && !path.isDone()) {
						heading = path.getNextEntityPos(jumpingMob);
					}
					else {
						heading = new Vec3(moveController.getWantedX(), moveController.getWantedY(), moveController.getWantedZ());
					}

					setJumpHeading(heading.x, heading.z);
					startJumping();
				}
			}
			else if (!jumpController.canJump()) {
				jumpController.setCanJump(true);
			}
		}

		wasOnGround = jumpingMob.onGround();
	}

	public void setJumpHeading(double x, double z) {
		Vec3 pos = jumpingMob.position();
		float bodyRotation = (float) (Mth.atan2(z - pos.z, x - pos.x) * Mth.RAD_TO_DEG) - 90f;
		jumpingMob.setYRot(moveController.rotlerp(jumpingMob.getYRot(), bodyRotation, MAX_Y_ROT_CHANGE));
	}

	public float getJumpCompletionPct(float partialTicks) {
		return jumpDuration == 0 ? 0 : (jumpTicks + partialTicks) / jumpDuration;
	}

	public interface JumpingPathfinderMob {
		void spawnJumpParticle();

		boolean isJumping();

		SoundEvent getJumpSound();
	}

	public class JumpController extends JumpControl {
		private boolean canJump;

		public JumpController(T jumpingMob) {
			super(jumpingMob);
		}

		public boolean wantsToJump() {
			return jump;
		}

		public boolean canJump() {
			return canJump;
		}

		public void setCanJump(boolean flag) {
			canJump = flag;
		}

		@Override
		public void tick() {
			if (jump) {
				startJumping();
				jump = false;
			}
		}
	}

	public class MoveController extends MoveControl {
		private double nextJumpSpeed;

		public MoveController(T jumpingMob) {
			super(jumpingMob);
		}

		@Override
		protected float rotlerp(float sourceAngle, float targetAngle, float maximumChange) {
			return super.rotlerp(sourceAngle, targetAngle, maximumChange);
		}

		@Override
		public void tick() {
			if (jumpingMob.onGround() && !jumpingMob.isJumping() && !jumpController.wantsToJump()) {
				setSpeedModifier(0);
			}
			else if (hasWanted()) {
				setSpeedModifier(nextJumpSpeed);
			}

			if (operation == Operation.MOVE_TO) {
				double motionMultiplier = 1f;

				Vec3 motionDiff = new Vec3(wantedX - jumpingMob.getX(), wantedY - jumpingMob.getY(), wantedZ - jumpingMob.getZ());

				float targetYRot = (float) (Mth.atan2(motionDiff.z, motionDiff.x) * Mth.RAD_TO_DEG) - 90f;
				float yRot = rotlerp(jumpingMob.getYRot(), targetYRot, MAX_Y_ROT_CHANGE);
				jumpingMob.setYRot(yRot);

				//should we wait for the y rotation to finish?
				float yRotChange = Mth.wrapDegrees(targetYRot - yRot);
				if (yRotChange < -MAX_Y_ROT_CHANGE || yRotChange > MAX_Y_ROT_CHANGE) {
					jumpingMob.setZza(0);
					return;
				}

				operation = Operation.WAIT;
				double length = motionDiff.length();
				double lengthSqr = motionDiff.lengthSqr();
				if (lengthSqr >= 1.0E-7D) {
					double speed = speedModifier * 0.05f;
					Vec3 motion = motionDiff.scale((speed / length) * motionMultiplier);
					jumpingMob.setDeltaMovement(jumpingMob.getDeltaMovement().add(motion));
				}
			}

			super.tick();
		}

		@Override
		public void setWantedPosition(double x, double y, double z, double speed) {
			if (jumpingMob.isInWater()) speed = 1.5d;

			super.setWantedPosition(x, y, z, speed);

			if (speed > 0) nextJumpSpeed = speed;
		}

	}
}
