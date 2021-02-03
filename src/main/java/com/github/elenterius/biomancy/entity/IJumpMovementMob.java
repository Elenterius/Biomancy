package com.github.elenterius.biomancy.entity;

import com.github.elenterius.biomancy.entity.ai.controller.GenericJumpController;
import com.github.elenterius.biomancy.entity.ai.controller.GenericJumpMovementController;
import com.github.elenterius.biomancy.entity.ai.controller.GenericJumpMovementHelper;
import net.minecraft.entity.MobEntity;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public interface IJumpMovementMob<T extends MobEntity> {

	default T getJumpingEntity() {
		//noinspection unchecked
		return (T) this;
	}

	boolean isJumping();

	void setMovementController(GenericJumpMovementController controller);

	void setJumpController(GenericJumpController controller);

	void updateRotationYaw(double x, double z);

	default void startJumping() {
		getJumpingEntity().setJumping(true);
		getJumpMovementState().jumpDuration = 10;
		getJumpMovementState().jumpTicks = 0;
	}

	GenericJumpMovementHelper<? extends IJumpMovementMob<T>> getJumpMovementState();

	@OnlyIn(Dist.CLIENT)
	default float getJumpCompletion(float partialTick) {
		GenericJumpMovementHelper<?> jumpMovementState = getJumpMovementState();
		return jumpMovementState.jumpDuration == 0 ? 0f : ((float) jumpMovementState.jumpTicks + partialTick) / (float) jumpMovementState.jumpDuration;
	}

	SoundEvent getJumpSound();
}
