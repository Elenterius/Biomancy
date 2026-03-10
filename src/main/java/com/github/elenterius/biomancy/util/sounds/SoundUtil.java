package com.github.elenterius.biomancy.util.sounds;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.protocol.game.ClientboundSoundPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import java.util.function.Supplier;

public final class SoundUtil {

	private SoundUtil() {}

	private static float randomPitchForItem(RandomSource rand) {
		return 1f / (rand.nextFloat() * 0.5f + 1f) + 0.2f;
	}

	private static float randomPitchForBlock(RandomSource rand) {
		return rand.nextFloat() * 0.1f + 0.9f;
	}

	public static void playItemSound(Level level, LivingEntity itemHolder, Supplier<SoundEvent> soundEventSupplier) {
		playItemSound(level, itemHolder, soundEventSupplier.get());
	}

	public static void playItemSound(Level level, LivingEntity itemHolder, SoundEvent soundEvent) {
		if (level.isClientSide) {
			Client.playItemSound(level, itemHolder, soundEvent);
		}
		else {
			Server.playItemSound((ServerLevel) level, itemHolder, soundEvent);
		}
	}

	public static final class Client {

		private Client() {}

		public static void playItemSound(Level level, LivingEntity itemHolder, SoundEvent soundEvent) {
			SoundSource soundSource = itemHolder instanceof Player ? SoundSource.PLAYERS : SoundSource.HOSTILE;
			float pitch = randomPitchForItem(level.random);
			level.playSound(itemHolder instanceof Player player ? player : null, itemHolder.getX(), itemHolder.getY(0.5f), itemHolder.getZ(), soundEvent, soundSource, 0.8f, pitch);
		}

		public static void playBlockSound(Level level, BlockPos pos, Supplier<SoundEvent> soundEventSupplier) {
			playBlockSound(level, pos, soundEventSupplier.get());
		}

		public static void playBlockSound(Level level, BlockPos pos, Supplier<SoundEvent> soundEventSupplier, float volume, float pitch) {
			playBlockSound(level, pos, soundEventSupplier.get(), volume, pitch);
		}

		public static void playBlockSound(Level level, BlockPos pos, Supplier<SoundEvent> soundEventSupplier, float volume) {
			playBlockSound(level, pos, soundEventSupplier.get(), volume, randomPitchForBlock(level.random));
		}

		public static void playBlockSound(Level level, BlockPos pos, SoundEvent soundEvent) {
			playBlockSound(level, pos, soundEvent, 1f, randomPitchForBlock(level.random));
		}

		public static void playBlockSound(Level level, BlockPos pos, SoundEvent soundEvent, float volume, float pitch) {
			level.playLocalSound(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D, soundEvent, SoundSource.BLOCKS, volume, pitch, false);
		}

		public static void playUISound(Supplier<SoundEvent> soundEventSupplier) {
			playUISound(soundEventSupplier.get());
		}

		public static void playUISound(SoundEvent soundEvent) {
			Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(soundEvent, 0.5f, 1f));
		}

		public static LoopingSoundHelper createLoopingSoundHandler() {
			return new ClientLoopingSoundHelper();
		}

		static SimpleSoundInstance createLoopingSoundInstance(Supplier<SoundEvent> soundEventSupplier, BlockPos pos) {
			return createLoopingSoundInstance(soundEventSupplier.get(), pos);
		}

		static SimpleSoundInstance createLoopingSoundInstance(SoundEvent soundEvent, BlockPos pos) {
			return createLoopingSoundInstance(soundEvent, 1f, pos);
		}

		static SimpleSoundInstance createLoopingSoundInstance(SoundEvent soundEvent, float volume, BlockPos pos) {
			return new SimpleSoundInstance(soundEvent.getLocation(), SoundSource.BLOCKS, volume, 1f, RandomSource.create(), true, 0, SoundInstance.Attenuation.LINEAR, pos.getX() + 0.5d, pos.getY() + 0.5d, pos.getZ() + 0.5d, false);
		}

	}

	public static final class Server {

		private Server() {}

		/// send sound to the given player
		public static void sendSoundToClient(ServerPlayer player, SoundEvent soundEvent, SoundSource source, float volume, float pitch) {
			player.connection.send(new ClientboundSoundPacket(BuiltInRegistries.SOUND_EVENT.wrapAsHolder(soundEvent), source, player.getX(), player.getEyeY(), player.getZ(), volume, pitch, player.getRandom().nextLong()));
		}

		public static void playBlockSound(ServerLevel level, BlockPos pos, Supplier<SoundEvent> soundEventSupplier) {
			playBlockSound(level, pos, soundEventSupplier.get());
		}

		public static void playBlockSound(ServerLevel level, BlockPos pos, SoundEvent soundEvent) {
			level.playSound(null, pos, soundEvent, SoundSource.BLOCKS, 1f, randomPitchForBlock(level.random));
		}

		public static void playBlockSound(ServerLevel level, BlockPos pos, SoundEvent soundEvent, float volume, float pitch) {
			level.playSound(null, pos, soundEvent, SoundSource.BLOCKS, volume, pitch);
		}

		public static void playItemSound(ServerLevel level, LivingEntity itemHolder, SoundEvent soundEvent) {
			float pitch = randomPitchForItem(level.random);
			level.playSound(null, itemHolder.getX(), itemHolder.getY(0.5f), itemHolder.getZ(), soundEvent, itemHolder.getSoundSource(), 0.8f, pitch);
		}

		public static void playSound(ServerLevel level, LivingEntity source, SoundEvent soundEvent, float volume, float pitch) {
			level.playSound(null, source.getX(), source.getY(0.5f), source.getZ(), soundEvent, source.getSoundSource(), volume, pitch);
		}

	}

}
