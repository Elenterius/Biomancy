package com.github.elenterius.biomancy.util;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import java.util.Random;
import java.util.function.Supplier;

public final class SoundUtil {

	private SoundUtil() {}

	private static float randomVanillaItemPitch(Random rand) {
		return 1f / (rand.nextFloat() * 0.5f + 1f) + 0.2f;
	}

	private static float randomVanillaPitch(Random rand) {
		return rand.nextFloat() * 0.1f + 0.9f;
	}

	public static void playItemSoundEffect(Level level, LivingEntity itemHolder, Supplier<SoundEvent> soundEventSupplier) {
		playItemSoundEffect(level, itemHolder, soundEventSupplier.get());
	}

	public static void playItemSoundEffect(Level level, LivingEntity itemHolder, SoundEvent soundEvent) {
		if (level.isClientSide) {
			clientPlayItemSound(level, itemHolder, soundEvent);
		}
		else {
			broadcastItemSound((ServerLevel) level, itemHolder, soundEvent);
		}
	}

	public static void clientPlayItemSound(Level level, LivingEntity itemHolder, SoundEvent soundEvent) {
		SoundSource soundSource = itemHolder instanceof Player ? SoundSource.PLAYERS : SoundSource.HOSTILE;
		float pitch = randomVanillaItemPitch(level.random);
		level.playSound(itemHolder instanceof Player player ? player : null, itemHolder.getX(), itemHolder.getY(0.5f), itemHolder.getZ(), soundEvent, soundSource, 0.8f, pitch);
	}

	public static void broadcastItemSound(ServerLevel level, LivingEntity itemHolder, SoundEvent soundEvent) {
		SoundSource soundSource = itemHolder instanceof Player ? SoundSource.PLAYERS : SoundSource.HOSTILE;
		float pitch = randomVanillaItemPitch(level.random);
		level.playSound(null, itemHolder.getX(), itemHolder.getY(0.5f), itemHolder.getZ(), soundEvent, soundSource, 0.8f, pitch);
	}

	public static void clientPlayBlockSound(Level level, BlockPos pos, Supplier<SoundEvent> soundEventSupplier, float volume, float pitch) {
		clientPlayBlockSound(level, pos, soundEventSupplier.get(), volume, pitch);
	}

	public static void clientPlayBlockSound(Level level, BlockPos pos, Supplier<SoundEvent> soundEventSupplier) {
		clientPlayBlockSound(level, pos, soundEventSupplier.get());
	}

	public static void clientPlayBlockSound(Level level, BlockPos pos, SoundEvent soundEvent) {
		clientPlayBlockSound(level, pos, soundEvent, 1f, randomVanillaPitch(level.random));
	}

	public static void clientPlayBlockSound(Level level, BlockPos pos, SoundEvent soundEvent, float volume, float pitch) {
		level.playLocalSound(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D, soundEvent, SoundSource.BLOCKS, volume, pitch, false);
	}

	public static void broadcastBlockSound(ServerLevel level, BlockPos pos, Supplier<SoundEvent> soundEventSupplier) {
		broadcastBlockSound(level, pos, soundEventSupplier.get());
	}

	public static void broadcastBlockSound(ServerLevel level, BlockPos pos, SoundEvent soundEvent) {
		level.playSound(null, pos, soundEvent, SoundSource.BLOCKS, 1f, randomVanillaPitch(level.random));
	}

	public static void broadcastBlockSound(ServerLevel level, BlockPos pos, SoundEvent soundEvent, float volume, float pitch) {
		level.playSound(null, pos, soundEvent, SoundSource.BLOCKS, volume, pitch);
	}

}
