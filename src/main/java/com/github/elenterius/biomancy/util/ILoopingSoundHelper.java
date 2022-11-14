package com.github.elenterius.biomancy.util;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.level.block.entity.BlockEntity;

public interface ILoopingSoundHelper {

	ILoopingSoundHelper NULL = new ILoopingSoundHelper() {};

	default void stopLoop() {}

	default void clear() {
		stopLoop();
	}

	default void startLoop(BlockEntity blockEntity, SoundEvent soundEvent) {}

}
