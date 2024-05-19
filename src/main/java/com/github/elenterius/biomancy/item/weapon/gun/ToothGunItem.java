package com.github.elenterius.biomancy.item.weapon.gun;

import com.github.elenterius.biomancy.init.ModItems;
import com.github.elenterius.biomancy.init.ModProjectiles;
import com.github.elenterius.biomancy.item.ItemTooltipStyleProvider;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

import java.util.function.Predicate;

@Deprecated(forRemoval = true)
public class ToothGunItem extends GunItem implements ItemTooltipStyleProvider {

	public static final Predicate<ItemStack> AMMO_PREDICATE = stack -> stack.getItem() == ModItems.NUTRIENT_PASTE.get();

	public ToothGunItem(Properties properties) {
		super(properties,
				GunProperties.builder()
						.fireRate(1.25f).accuracy(0.92f).damageModifier(5f)
						.maxAmmo(6).reloadDuration(4 * 20).autoReload(true)
						.build(),
				ModProjectiles.TOOTH);
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
	public Predicate<ItemStack> getAllSupportedProjectiles() {
		return AMMO_PREDICATE;
	}

	@Override
	public int getDefaultProjectileRange() {
		return 10;
	}

}
