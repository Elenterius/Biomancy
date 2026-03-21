package com.github.elenterius.biomancy.sounds;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jspecify.annotations.Nullable;

final class ClientLoopingSoundHelper implements LoopingSoundHelper {

	@Nullable
	private SimpleSoundInstance soundInstance = null;

	@Override
	public void stopLoop() {
		if (soundInstance != null) {
			Minecraft.getInstance().getSoundManager().stop(soundInstance);
			soundInstance = null;
		}
	}

	@Override
	public void clear() {
		stopLoop();
	}

	@Override
	public void startLoop(BlockEntity blockEntity, SoundEvent soundEvent, float volume) {
		if (blockEntity.isRemoved()) return;

		if (soundInstance == null) {
			soundInstance = SoundUtil.Client.createLoopingSoundInstance(soundEvent, volume, blockEntity.getBlockPos());
			Minecraft.getInstance().getSoundManager().play(soundInstance);
		}
		else if (!soundInstance.getLocation().equals(soundEvent.getLocation())) {
			Minecraft.getInstance().getSoundManager().stop(soundInstance);
			soundInstance = SoundUtil.Client.createLoopingSoundInstance(soundEvent, volume, blockEntity.getBlockPos());
			Minecraft.getInstance().getSoundManager().play(soundInstance);
		}
	}

}
