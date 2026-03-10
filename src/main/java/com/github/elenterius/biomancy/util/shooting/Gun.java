package com.github.elenterius.biomancy.util.shooting;

import com.github.elenterius.biomancy.entity.projectile.BaseProjectile;
import com.github.elenterius.biomancy.item.CrosshairProvider;
import com.github.elenterius.biomancy.styles.TextComponentUtil;
import com.github.elenterius.biomancy.styles.TextStyles;
import com.github.elenterius.biomancy.util.ComponentUtil;
import com.github.elenterius.biomancy.util.FormatUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.SharedConstants;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;

import java.text.DecimalFormat;
import java.util.List;
import java.util.Set;

@SuppressWarnings("unused")
public interface Gun<T extends BaseProjectile> extends CrosshairProvider {

	Set<Enchantment> VALID_ENCHANTMENTS = Set.of(Enchantments.PUNCH_ARROWS, Enchantments.POWER_ARROWS, Enchantments.QUICK_CHARGE, Enchantments.MULTISHOT);

	String AMMO_KEY = "ammo";
	String RELOAD_TIMESTAMP_KEY = "reload_timestamp";
	String WEAPON_STATE_KEY = "projectile_weapon_state";
	String SHOOT_TIMESTAMP_KEY = "shoot_timestamp";

	GunProperties<T> getGunProperties();

	default long getShootTimestamp(ItemStack stack) {
		return stack.getOrCreateTag().getLong(SHOOT_TIMESTAMP_KEY);
	}

	default int getDurabilityCost(ItemStack stack) {
		return 1;
	}

	default int getAmmoCost(ItemStack stack) {
		return 1;
	}

	/// Value shouldn't be larger than max ItemStack size of 64
	default int getReloadCost(ItemStack stack) {
		return 1;
	}

	default void stopShooting(ItemStack stack, ServerLevel level, LivingEntity shooter) {
		if (getGunProperties().isAutoReload()) startReload(stack, level, shooter);
	}

	default void shoot(ServerLevel level, LivingEntity shooter, InteractionHand usedHand, ItemStack projectileWeapon) {
		boolean success = ProjectileUtil.shoot(level, shooter, projectileWeapon, this);

		if (success) {
			getGunProperties().sounds().playShoot(level, shooter);
			projectileWeapon.hurtAndBreak(getDurabilityCost(projectileWeapon), shooter, entity -> entity.broadcastBreakEvent(usedHand));
			consumeAmmo(shooter, projectileWeapon, getAmmoCost(projectileWeapon));
		}
	}

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
			getGunProperties().sounds().playFail(level, shooter);
		}
	}

	default void finishReload(ItemStack stack, ServerLevel level, LivingEntity shooter) {
		setGunState(stack, GunState.NONE);

		if (shooter instanceof Player player && player.getAbilities().instabuild) {
			setAmmo(stack, getMaxAmmo(stack));
			onReloadFinished(stack, level, shooter);
			return;
		}

		AmmoSupplier ammoSupplier = getAmmoForReload(stack, shooter);
		int reloadCost = getReloadCost(stack);

		if (ammoSupplier.getAmount() >= reloadCost) {
			ammoSupplier.consume(reloadCost);
			setAmmo(stack, getMaxAmmo(stack));
			onReloadFinished(stack, level, shooter);
		}
		else {
			getGunProperties().sounds().playFail(level, shooter);
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
		getGunProperties().sounds().playReloadStart(level, shooter);
	}

	default void onReloadTick(ItemStack stack, ServerLevel level, LivingEntity shooter, long elapsedTime) {
		if (elapsedTime % 20L == 0L) {
			getGunProperties().sounds().playReloadTick(level, shooter);
		}
	}

	default void onReloadStopped(ItemStack stack, ServerLevel level, LivingEntity shooter) {
		getGunProperties().sounds().playReloadStop(level, shooter);
	}

	default void onReloadCanceled(ItemStack stack, ServerLevel level, LivingEntity shooter) {
		getGunProperties().sounds().playReloadCancel(level, shooter);
	}

	default void onReloadFinished(ItemStack stack, ServerLevel level, LivingEntity shooter) {
		getGunProperties().sounds().playReloadFinish(level, shooter);
	}

	default float getReloadProgress(long elapsedTime, long reloadTime) {
		return Mth.clamp((float) elapsedTime / (float) reloadTime, 0f, 1f);
	}

	default boolean canReload(ItemStack stack, LivingEntity shooter) {
		if (getAmmo(stack) >= getMaxAmmo(stack)) return false;
		AmmoSupplier ammoSupplier = getAmmoForReload(stack, shooter);
		return ammoSupplier.getAmount() >= getReloadCost(stack);
	}

	default float getProjectileVelocity(ItemStack stack) {
		return getGunProperties().velocity();
	}

	default float getProjectileDamage(ItemStack stack) {
		return getGunProperties().damage() + 0.6f * stack.getEnchantmentLevel(Enchantments.POWER_ARROWS);
	}

	default int getProjectileKnockBack(ItemStack stack) {
		return getGunProperties().knockback() + stack.getEnchantmentLevel(Enchantments.PUNCH_ARROWS);
	}

	default float getAccuracy(ItemStack stack) {
		return getGunProperties().accuracy();
	}

	default int getProjectileCount(ItemStack stack) {
		return getGunProperties().projectileCount() + stack.getEnchantmentLevel(Enchantments.MULTISHOT);
	}

	default int getDelayBetweenShots(ItemStack stack) {
		//return getGunProperties().delayBetweenShots() - 2 * stack.getEnchantmentLevel(ModEnchantments.QUICK_SHOT.get());
		return getGunProperties().delayBetweenShots();
	}

	default float getFireRate(ItemStack stack) {
		return SharedConstants.TICKS_PER_SECOND / (float) getDelayBetweenShots(stack);
	}

	default int getReloadDurationTicks(ItemStack stack) {
		return getGunProperties().reloadDurationTicks() - 5 * stack.getEnchantmentLevel(Enchantments.QUICK_CHARGE);
	}

	default ShootBehavior getShootBehavior() {
		return getGunProperties().shootBehavior();
	}

	default int getMaxAmmo(ItemStack stack) {
		//return Mth.floor(getGunProperties().maxAmmo() + getGunProperties().maxAmmo() * 0.5f * stack.getEnchantmentLevel(ModEnchantments.MAX_AMMO.get()));
		return getGunProperties().maxAmmo();
	}

	AmmoSupplier getAmmoForReload(ItemStack stack, LivingEntity shooter);

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

	default void appendGunStats(ItemStack stack, List<Component> tooltip) {
		DecimalFormat df = FormatUtil.getDoubleFormatter();
		GunProperties<T> gunProperties = getGunProperties();

		float velocity = getProjectileVelocity(stack);
		float bonusVelocity = velocity - gunProperties.velocity();
		float accuracy = getAccuracy(stack);
		float bonusAccuracy = accuracy - gunProperties.accuracy();
		float damage = getProjectileDamage(stack);
		float bonusDamage = damage - gunProperties.damage();
		int knockBack = getProjectileKnockBack(stack);
		int projectileCount = getProjectileCount(stack);
		int bonusProjectileCount = projectileCount - gunProperties.projectileCount();
		float fireRate = getFireRate(stack);
		float bonusFireRate = fireRate - (SharedConstants.TICKS_PER_SECOND / (float) gunProperties.delayBetweenShots());
		float reloadDurationSeconds = getReloadDurationTicks(stack) / (float) SharedConstants.TICKS_PER_SECOND;
		float bonusReloadReduction = reloadDurationSeconds - (gunProperties.reloadDurationTicks() / (float) SharedConstants.TICKS_PER_SECOND);

		tooltip.add(TextComponentUtil.getTooltipText("projectile_speed").append(String.format(": %s m/s ", df.format(velocity * 20))).append(formatBonusValue(df, bonusVelocity * 20)).withStyle(ChatFormatting.GRAY));

		ProjectileRange range = GunProperties.computeRange(gunProperties, velocity, accuracy);
		float bonusRange = range.mean() - gunProperties.defaultRange().mean();
		tooltip.add(TextComponentUtil.getTooltipText("projectile_range").append(String.format(": %s m", range.format(df))).append(formatBonusValue(df, bonusRange)).withStyle(ChatFormatting.GRAY));

		tooltip.add(TextComponentUtil.getTooltipText("projectile_damage").append(String.format(": %s ", df.format(damage))).append(formatBonusValue(df, bonusDamage)).withStyle(ChatFormatting.GRAY));

		if (knockBack != 0) {
			int bonusValue = knockBack - gunProperties.knockback();
			tooltip.add(TextComponentUtil.getTooltipText("projectile_knock_back").append(String.format(": %s ", df.format(knockBack))).append(formatBonusValue(df, bonusValue)).withStyle(ChatFormatting.GRAY));
		}

		tooltip.add(TextComponentUtil.getTooltipText("accuracy").append(String.format(": %s ", df.format(accuracy))).append(formatBonusValue(df, bonusAccuracy)).withStyle(ChatFormatting.GRAY));
		tooltip.add(TextComponentUtil.getTooltipText("fire_rate").append(String.format(": %s rps ", df.format(fireRate))).append(formatBonusValue(df, bonusFireRate)).withStyle(ChatFormatting.GRAY));

		if (projectileCount > 1) {
			tooltip.add(TextComponentUtil.getTooltipText("projectile_count").append(String.format(": %s ", df.format(projectileCount))).append(formatBonusValue(df, bonusProjectileCount)).withStyle(ChatFormatting.GRAY));
		}

		tooltip.add(TextComponentUtil.getTooltipText("reload_time").append(String.format(": %ss ", df.format(reloadDurationSeconds))).append(formatBonusValue(df, bonusReloadReduction, true)).withStyle(ChatFormatting.GRAY));
		tooltip.add(ComponentUtil.EMPTY_LINE);
		tooltip.add(TextComponentUtil.getTooltipText("ammo").append(String.format(": %d/%d ", getAmmo(stack), getMaxAmmo(stack))).withStyle(ChatFormatting.GRAY));
	}

	static Component formatBonusValue(DecimalFormat df, float value) {
		return formatBonusValue(df, value, false);
	}

	static Component formatBonusValue(DecimalFormat df, float value, boolean inverted) {
		if (value == 0f) return ComponentUtil.EMPTY;

		boolean isBeneficial = (inverted && value < 0f) || (!inverted && value > 0f);
		Style style = isBeneficial ? TextStyles.LIME : TextStyles.ERROR;

		String formattedDecimal = (value > 0f ? "+" : "") + df.format(value);
		MutableComponent component = ComponentUtil.literal(formattedDecimal).withStyle(style);

		return ComponentUtil.mutable().append("(").append(component).append(")").withStyle(TextStyles.DARK_GRAY);
	}

}
