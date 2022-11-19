package com.github.elenterius.biomancy.client.util;

import com.github.elenterius.biomancy.util.ILoopingSoundHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.Nullable;

public class ClientLoopingSoundHelper implements ILoopingSoundHelper {

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
			soundInstance = ClientSoundUtil.createLoopingSoundInstance(soundEvent, volume, blockEntity.getBlockPos());
			Minecraft.getInstance().getSoundManager().play(soundInstance);
		}
		else if (!soundInstance.getLocation().equals(soundEvent.getLocation())) {
			Minecraft.getInstance().getSoundManager().stop(soundInstance);
			soundInstance = ClientSoundUtil.createLoopingSoundInstance(soundEvent, volume, blockEntity.getBlockPos());
			Minecraft.getInstance().getSoundManager().play(soundInstance);
		}
	}

}
