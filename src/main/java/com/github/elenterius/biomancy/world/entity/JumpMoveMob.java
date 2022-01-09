package com.github.elenterius.biomancy.world.entity;

import com.github.elenterius.biomancy.world.entity.ai.control.GenericJumpControl;
import com.github.elenterius.biomancy.world.entity.ai.control.GenericJumpMoveControl;
import com.github.elenterius.biomancy.world.entity.ai.control.GenericJumpMoveHelper;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.PathfinderMob;

public interface JumpMoveMob<T extends PathfinderMob> {

	default PathfinderMob getJumpingEntity() {
		return (PathfinderMob) this;
	}

	boolean isJumping();

	void setMoveControl(GenericJumpMoveControl control);

	void setJumpControl(GenericJumpControl control);

	void setJumpHeading(double x, double z);

	default void startJumping() {
		getJumpingEntity().setJumping(true);
		getJumpMoveState().onStartJumping(10);
	}

	GenericJumpMoveHelper<? extends JumpMoveMob<T>> getJumpMoveState();

	SoundEvent getJumpSound();

}
