package com.github.elenterius.biomancy.item.weapon.gun;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public interface Gun {

	int ONE_SECOND_IN_TICKS = 20;
	int ONE_HOUR_IN_TICKS = 60 * 60 * 20;
	float MAX_INACCURACY = 1f; //0.0 - 1.0

	String AMMO_KEY = "ammo";
	String RELOAD_TIMESTAMP_KEY = "reload_timestamp";
	String WEAPON_STATE_KEY = "projectile_weapon_state";
	String SHOOT_TIMESTAMP_KEY = "shoot_timestamp";

	default long getShootTimestamp(ItemStack stack) {
		return stack.getOrCreateTag().getLong(SHOOT_TIMESTAMP_KEY);
	}

	void stopShooting(ItemStack stack, ServerLevel level, LivingEntity shooter);

	void shoot(ServerLevel level, LivingEntity shooter, InteractionHand usedHand, ItemStack projectileWeapon);

	default GunState getGunState(ItemStack stack) {
		return GunState.fromId(stack.getOrCreateTag().getByte(WEAPON_STATE_KEY));
	}

	default void setGunState(ItemStack stack, GunState state) {
		stack.getOrCreateTag().putByte(WEAPON_STATE_KEY, state.getId());
	}

	default long getReloadStartTime(ItemStack stack) {
		return stack.getOrCreateTag().getLong(RELOAD_TIMESTAMP_KEY);
	}

	default void startReload(ItemStack stack, ServerLevel level, LivingEntity shooter) {
		if (canReload(stack, shooter)) {
			setGunState(stack, GunState.RELOADING);
			stack.getOrCreateTag().putLong(RELOAD_TIMESTAMP_KEY, level.getGameTime());
			onReloadStarted(stack, level, shooter);
		}
		else {
			playSFX(level, shooter, SoundEvents.DISPENSER_FAIL);
		}
	}

	default void finishReload(ItemStack stack, ServerLevel level, LivingEntity shooter) {
		setGunState(stack, GunState.NONE);

		if (shooter instanceof Player player && player.getAbilities().instabuild) {
			setAmmo(stack, getMaxAmmo(stack));
			onReloadFinished(stack, level, shooter);
			return;
		}

		ItemStack ammoStack = findAmmoInInv(stack, shooter);
		if (!ammoStack.isEmpty() && ammoStack.getCount() >= getAmmoReloadCost()) {
			ammoStack.shrink(getAmmoReloadCost());
			setAmmo(stack, getMaxAmmo(stack));
			onReloadFinished(stack, level, shooter);
		}
		else {
			playSFX(level, shooter, SoundEvents.DISPENSER_FAIL);
		}
	}

	default void stopReload(ItemStack stack, ServerLevel level, LivingEntity shooter) {
		setGunState(stack, GunState.NONE);
		onReloadStopped(stack, level, shooter);
	}

	default void cancelReload(ItemStack stack, ServerLevel level, LivingEntity shooter) {
		setGunState(stack, GunState.NONE);
		onReloadCanceled(stack, level, shooter);
	}

	default void onReloadStarted(ItemStack stack, ServerLevel level, LivingEntity shooter) {
		playSFX(level, shooter, SoundEvents.CROSSBOW_LOADING_START);
	}

	default void onReloadTick(ItemStack stack, ServerLevel level, LivingEntity shooter, long elapsedTime) {}

	default void onReloadStopped(ItemStack stack, ServerLevel level, LivingEntity shooter) {
		playSFX(level, shooter, SoundEvents.CROSSBOW_LOADING_END);
	}

	default void onReloadCanceled(ItemStack stack, ServerLevel level, LivingEntity shooter) {
		playSFX(level, shooter, SoundEvents.CROSSBOW_LOADING_END);
	}

	default void onReloadFinished(ItemStack stack, ServerLevel level, LivingEntity shooter) {
		playSFX(level, shooter, SoundEvents.CROSSBOW_LOADING_END);
	}

	default float getReloadProgress(long elapsedTime, long reloadTime) {
		return Mth.clamp((float) elapsedTime / (float) reloadTime, 0f, 1f);
	}

	default boolean canReload(ItemStack stack, LivingEntity shooter) {
		if (getAmmo(stack) >= getMaxAmmo(stack)) return false;
		ItemStack ammo = findAmmoInInv(stack, shooter);
		return !ammo.isEmpty() && ammo.getCount() >= getAmmoReloadCost();
	}

	default float modifyProjectileInaccuracy(float baseInaccuracy, ItemStack stack) {
		return baseInaccuracy + (-MAX_INACCURACY * getAccuracy(stack) + MAX_INACCURACY);
	}

	float getAccuracy(ItemStack stack);

	int getShootDelayTicks(ItemStack stack);

	default float getFireRate(ItemStack stack) {
		return ONE_SECOND_IN_TICKS / (float) getShootDelayTicks(stack);
	}

	int getReloadDurationTicks(ItemStack stack);

	float modifyProjectileDamage(float baseDamage, ItemStack stack);

	int modifyProjectileKnockBack(int baseKnockBack, ItemStack stack);

	int getMaxAmmo(ItemStack stack);

	ItemStack findAmmoInInv(ItemStack stack, LivingEntity shooter);

	/**
	 * value shouldn't be larger than max ItemStack size of 64
	 */
	default int getAmmoReloadCost() {
		return 1;
	}

	default boolean hasAmmo(ItemStack stack) {
		return getAmmo(stack) > 0;
	}

	default int getAmmo(ItemStack stack) {
		return stack.getOrCreateTag().getInt(AMMO_KEY);
	}

	default void setAmmo(ItemStack stack, int amount) {
		CompoundTag nbt = stack.getOrCreateTag();
		nbt.putInt(AMMO_KEY, Mth.clamp(amount, 0, getMaxAmmo(stack)));
	}

	default void addAmmo(ItemStack stack, int amount) {
		if (amount == 0) return;
		CompoundTag nbt = stack.getOrCreateTag();
		nbt.putInt(AMMO_KEY, Math.max(0, nbt.getInt(AMMO_KEY) + amount));
	}

	default void consumeAmmo(ItemStack stack, int amount) {
		addAmmo(stack, -amount);
	}

	default void consumeAmmo(LivingEntity shooter, ItemStack stack, int amount) {
		if (!(shooter instanceof Player player) || !player.getAbilities().instabuild) addAmmo(stack, -amount);
	}

	default void playSFX(Level level, LivingEntity shooter, SoundEvent soundEvent) {
		SoundSource soundSource = shooter instanceof Player ? SoundSource.PLAYERS : SoundSource.HOSTILE;
		level.playSound(null, shooter.getX(), shooter.getY(), shooter.getZ(), soundEvent, soundSource, 1f, 1f / (shooter.getRandom().nextFloat() * 0.5f + 1f) + 0.2f);
	}

	enum GunState {
		NONE((byte) 0), SHOOTING((byte) 1), RELOADING((byte) 2);

		private final byte id;

		GunState(byte id) {
			this.id = id;
		}

		public static GunState fromId(int id) {
			if (id == 0) return NONE;
			if (id == 1) return SHOOTING;
			if (id == 2) return RELOADING;
			return NONE;
		}

		public byte getId() {
			return id;
		}
	}

}
