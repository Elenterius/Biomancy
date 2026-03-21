package com.github.elenterius.biomancy.sounds;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.level.block.entity.BlockEntity;

public interface LoopingSoundHelper {

	LoopingSoundHelper NULL = new LoopingSoundHelper() {};

	default void stopLoop() {}

	default void clear() {
		stopLoop();
	}

	default void startLoop(BlockEntity blockEntity, SoundEvent soundEvent, float volume) {}

}
