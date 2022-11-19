package com.github.elenterius.biomancy.client.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;

import java.util.function.Supplier;

public final class ClientSoundUtil {

	private ClientSoundUtil() {}

	public static void playUISound(Supplier<SoundEvent> soundEventSupplier) {
		playUISound(soundEventSupplier.get());
	}

	public static void playUISound(SoundEvent soundEvent) {
		Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(soundEvent, 0.5f, 1f));
	}

	public static SimpleSoundInstance createLoopingSoundInstance(SoundEvent soundEvent, BlockPos pos) {
		return createLoopingSoundInstance(soundEvent, 1f, pos);
	}

	public static SimpleSoundInstance createLoopingSoundInstance(SoundEvent soundEvent, float volume, BlockPos pos) {
		return new SimpleSoundInstance(soundEvent.getLocation(), SoundSource.BLOCKS, volume, 1f, true, 0, SoundInstance.Attenuation.LINEAR, pos.getX() + 0.5d, pos.getY() + 0.5d, pos.getZ() + 0.5d, false);
	}

	public static SimpleSoundInstance createLoopingSoundInstance(Supplier<SoundEvent> soundEventSupplier, BlockPos pos) {
		return createLoopingSoundInstance(soundEventSupplier.get(), pos);
	}

}
