package com.github.elenterius.biomancy.item.weapon;

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

public interface IGun {

	record ProjectileProperties(float damage, float inaccuracy, int knockBack) {}

	float ONE_SECOND = 20f; //measured in ticks
	float MAX_INACCURACY = 1f; //0.0 - 1.0

	String NBT_KEY_AMMO = "Ammo";
	String NBT_KEY_RELOAD_TIMESTAMP = "ReloadStartTime";
	String NBT_KEY_WEAPON_STATE = "ProjectileWeaponState";
	String NBT_KEY_SHOOT_TIMESTAMP = "ShootTime";

	default long getShootTimestamp(ItemStack stack) {
		return stack.getOrCreateTag().getLong(NBT_KEY_SHOOT_TIMESTAMP);
	}

	void stopShooting(ItemStack stack, ServerLevel level, LivingEntity shooter);

	void shoot(ServerLevel level, LivingEntity shooter, InteractionHand usedHand, ItemStack projectileWeapon, ProjectileProperties properties);

	default State getState(ItemStack stack) {
		return State.fromId(stack.getOrCreateTag().getByte(NBT_KEY_WEAPON_STATE));
	}

	default void setState(ItemStack stack, State state) {
		stack.getOrCreateTag().putByte(NBT_KEY_WEAPON_STATE, state.getId());
	}

	default long getReloadStartTime(ItemStack stack) {
		return stack.getOrCreateTag().getLong(NBT_KEY_RELOAD_TIMESTAMP);
	}

	default void startReload(ItemStack stack, ServerLevel level, LivingEntity shooter) {
		if (canReload(stack, shooter)) {
			setState(stack, State.RELOADING);
			stack.getOrCreateTag().putLong(NBT_KEY_RELOAD_TIMESTAMP, level.getGameTime());
			onReloadStarted(stack, level, shooter);
		}
		else {
			playSFX(level, shooter, SoundEvents.DISPENSER_FAIL);
		}
	}

	default void finishReload(ItemStack stack, ServerLevel level, LivingEntity shooter) {
		setState(stack, State.NONE);

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
		setState(stack, State.NONE);
		onReloadStopped(stack, level, shooter);
	}

	default void cancelReload(ItemStack stack, ServerLevel level, LivingEntity shooter) {
		setState(stack, State.NONE);
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

	default float getInaccuracy() {
		return -MAX_INACCURACY * getAccuracy() + MAX_INACCURACY;
	}

	float getAccuracy();

	int getShootDelay(ItemStack stack);

	default float getFireRate(ItemStack stack) {
		return ONE_SECOND / getShootDelay(stack);
	}

	int getReloadTime(ItemStack stack);

	float getProjectileDamage(ItemStack stack);

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
		return stack.getOrCreateTag().getInt(NBT_KEY_AMMO);
	}

	default void setAmmo(ItemStack stack, int amount) {
		CompoundTag nbt = stack.getOrCreateTag();
		nbt.putInt(NBT_KEY_AMMO, Mth.clamp(amount, 0, getMaxAmmo(stack)));
	}

	default void addAmmo(ItemStack stack, int amount) {
		if (amount == 0) return;
		CompoundTag nbt = stack.getOrCreateTag();
		nbt.putInt(NBT_KEY_AMMO, Math.max(0, nbt.getInt(NBT_KEY_AMMO) + amount));
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

	ItemStack getAmmoIcon(ItemStack stack);

	enum State {
		NONE((byte) 0), SHOOTING((byte) 1), RELOADING((byte) 2);

		private final byte id;

		State(byte id) {
			this.id = id;
		}

		public static State fromId(int id) {
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
