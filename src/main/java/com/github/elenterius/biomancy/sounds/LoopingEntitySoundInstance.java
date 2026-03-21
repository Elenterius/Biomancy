package com.github.elenterius.biomancy.sounds;

import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

import java.util.function.Supplier;

public class LoopingEntitySoundInstance extends AbstractTickableSoundInstance {

	protected final Entity entity;

	private float targetVolume = 1f;
	private boolean isStopping = false;

	//	private float fadeInStep = convertDurationToStepSize(20);
	private float fadeOutStep = 1f / 20f;

	public LoopingEntitySoundInstance(Entity entity, Supplier<SoundEvent> soundEventSupplier) {
		this(entity, soundEventSupplier.get());
	}

	public LoopingEntitySoundInstance(Entity entity, SoundEvent soundEvent) {
		super(soundEvent, entity.getSoundSource(), entity instanceof LivingEntity livingEntity ? livingEntity.getRandom() : SoundInstance.createUnseededRandom());
		this.entity = entity;
		looping = true;
		delay = 0;
		volume = 0f;

		x = entity.getX();
		y = entity.getY();
		z = entity.getZ();
	}

	@Override
	public boolean canPlaySound() {
		return !entity.isSilent();
	}

	@Override
	public boolean canStartSilent() {
		return true;
	}

	@Override
	public void tick() {
		if (entity.isRemoved()) {
			fadeOut();
		}
		else {
			x = entity.getX();
			y = entity.getY();
			z = entity.getZ();
		}

		if (isStopping) {
			volume = Math.max(0f, volume - fadeOutStep);
			if (volume <= 1e-03f) stop();
		}
		else {
			//			volume = Math.min(targetVolume, volume + fadeInStep);
			volume += (targetVolume - volume) * 0.2f;
		}
	}

	public void setVolume(float volume, boolean fadeIn) {
		this.targetVolume = volume;
		if (!fadeIn) {
			this.volume = volume;
		}
	}

	public void fadeOut() {
		if (!isStopping) fadeOut(20);
	}

	public void fadeOut(int durationTicks) {
		fadeOutStep = durationTicks <= 0 ? 1f : volume / durationTicks;
		isStopping = true;
	}

	public void stopNow() {
		fadeOutStep = 1f;
		volume = 0;
		isStopping = true;
		stop();
	}

}
