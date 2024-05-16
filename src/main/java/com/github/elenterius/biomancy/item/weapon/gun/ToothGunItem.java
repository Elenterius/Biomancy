package com.github.elenterius.biomancy.item.weapon.gun;

import com.github.elenterius.biomancy.init.ModItems;
import com.github.elenterius.biomancy.init.ModProjectiles;
import com.github.elenterius.biomancy.item.ItemTooltipStyleProvider;
import com.github.elenterius.biomancy.util.function.FloatOperator;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

import java.util.function.Predicate;

@Deprecated(forRemoval = true)
public class ToothGunItem extends GunItem implements ItemTooltipStyleProvider {

	public static final Predicate<ItemStack> AMMO_PREDICATE = stack -> stack.getItem() == ModItems.NUTRIENT_PASTE.get();

	public ToothGunItem(Properties properties) {
		super(properties, GunProperties.builder()
				.fireRate(1.25f).accuracy(0.92f).damageModifier(5f)
				.maxAmmo(6).reloadDuration(4 * 20).autoReload(true)
				.build());
	}

	@Override
	public void shoot(ServerLevel level, LivingEntity shooter, InteractionHand usedHand, ItemStack projectileWeapon) {
		ModProjectiles.TOOTH.shoot(level, shooter, FloatOperator.IDENTITY, d -> d + getProjectileDamageModifier(projectileWeapon), k -> k + getProjectileKnockBackModifier(projectileWeapon), i -> i + getProjectileInaccuracyModifier(projectileWeapon));
		projectileWeapon.hurtAndBreak(1, shooter, entity -> entity.broadcastBreakEvent(usedHand));
		consumeAmmo(shooter, projectileWeapon, 1);
	}

	@Override
	public void onReloadTick(ItemStack stack, ServerLevel level, LivingEntity shooter, long elapsedTime) {
		if (elapsedTime % 20L == 0L) playSFX(level, shooter, SoundEvents.GENERIC_EAT);
	}

	@Override
	public void onReloadStarted(ItemStack stack, ServerLevel level, LivingEntity shooter) {
		playSFX(level, shooter, SoundEvents.GENERIC_EAT);
	}

	@Override
	public void onReloadCanceled(ItemStack stack, ServerLevel level, LivingEntity shooter) {
		playSFX(level, shooter, SoundEvents.DISPENSER_FAIL);
	}

	@Override
	public void onReloadStopped(ItemStack stack, ServerLevel level, LivingEntity shooter) {
		playSFX(level, shooter, SoundEvents.DISPENSER_FAIL);
	}

	@Override
	public void onReloadFinished(ItemStack stack, ServerLevel level, LivingEntity shooter) {
		playSFX(level, shooter, SoundEvents.PLAYER_BURP);
	}

	@Override
	public int getAmmoReloadCost() {
		return 3;
	}

	@Override
	public ItemStack getAmmoIcon(ItemStack stack) {
		return new ItemStack(ModItems.MOB_FANG.get());
	}

	@Override
	public Predicate<ItemStack> getAllSupportedProjectiles() {
		return AMMO_PREDICATE;
	}

	@Override
	public int getDefaultProjectileRange() {
		return 10;
	}

}
