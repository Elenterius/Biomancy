package com.github.elenterius.biomancy.util.shooting;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.jspecify.annotations.Nullable;

public record GunSounds(@Nullable SoundEvent shoot,
                        SoundEvent fail,
                        SoundEvent reloadStart,
                        @Nullable SoundEvent reloadTick,
                        SoundEvent reloadStop,
                        SoundEvent reloadCancel,
                        SoundEvent reloadFinish) {

	public static final GunSounds DEFAULT = new GunSounds(
			SoundEvents.CROSSBOW_SHOOT, SoundEvents.DISPENSER_FAIL,
			SoundEvents.CROSSBOW_LOADING_START, SoundEvents.CROSSBOW_LOADING_MIDDLE,
			SoundEvents.CROSSBOW_LOADING_END, SoundEvents.CROSSBOW_LOADING_END, SoundEvents.CROSSBOW_LOADING_END
	);

	public static void play(Level level, LivingEntity shooter, SoundEvent soundEvent) {
		float volume = Mth.lerp(shooter.getRandom().nextFloat(), 0.75f, 0.85f);
		float pitch = Mth.lerp(shooter.getRandom().nextFloat(), 0.86f, 1.2f);
		play(level, shooter, soundEvent, volume, pitch);
	}

	public static void play(Level level, LivingEntity shooter, SoundEvent soundEvent, float volume, float pitch) {
		level.playSound(null, shooter.getX(), shooter.getEyeY(), shooter.getZ(), soundEvent, shooter.getSoundSource(), volume, pitch);
	}

	public static void playLocal(Player player, SoundEvent soundEvent) {
		if (!player.level().isClientSide) return;
		float volume = Mth.lerp(player.getRandom().nextFloat(), 0.75f, 0.85f);
		float pitch = Mth.lerp(player.getRandom().nextFloat(), 0.86f, 1.2f);
		player.level().playSound(player, player.getX(), player.getEyeY(), player.getZ(), soundEvent, player.getSoundSource(), volume, pitch);
	}

	public GunSounds withShoot(SoundEvent shoot) {
		return new GunSounds(shoot, fail, reloadStart, reloadTick, reloadStop, reloadCancel, reloadFinish);
	}

	public void playShoot(Level level, LivingEntity shooter) {
		if (shoot != null) play(level, shooter, shoot);
	}

	public void playShoot(Level level, LivingEntity shooter, float volume, float pitch) {
		if (shoot != null) play(level, shooter, shoot, volume, pitch);
	}

	public void playFail(Level level, LivingEntity shooter) {
		play(level, shooter, fail);
	}

	public void playLocalFail(Level level, Player player) {
		if (!level.isClientSide) return;
		playLocal(player, fail);
	}

	public void playReloadStart(Level level, LivingEntity shooter) {
		play(level, shooter, reloadStart);
	}

	public void playReloadTick(Level level, LivingEntity shooter) {
		if (reloadTick != null) play(level, shooter, reloadTick);
	}

	public void playReloadStop(Level level, LivingEntity shooter) {
		play(level, shooter, reloadStop);
	}

	public void playReloadCancel(Level level, LivingEntity shooter) {
		play(level, shooter, reloadCancel);
	}

	public void playReloadFinish(Level level, LivingEntity shooter) {
		play(level, shooter, reloadFinish);
	}

}
