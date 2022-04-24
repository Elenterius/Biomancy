package com.github.elenterius.biomancy.world.item.weapon;

import com.github.elenterius.biomancy.init.ModItems;
import com.github.elenterius.biomancy.world.entity.projectile.ToothProjectile;
import com.github.elenterius.biomancy.world.item.IBiomancyItem;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.phys.Vec3;

import java.util.function.Predicate;

public class ToothGunItem extends BaseGunItem implements IBiomancyItem {

	public ToothGunItem(Properties properties) {
		super(properties, new GunProperties()
				.fireRate(1.25f)
				.accuracy(0.92f)
				.damage(5f)
				.maxAmmo(6)
				.reloadTime(4 * 20)
				.autoReload(true));
	}

	public static void fireProjectile(ServerLevel serverLevel, LivingEntity shooter, InteractionHand usedHand, ItemStack stack, float damage, float inaccuracy) {
		ToothProjectile projectile = new ToothProjectile(serverLevel, shooter);
		projectile.setDamage(damage);
		int level = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.PUNCH_ARROWS, stack);
		if (level > 0) {
			projectile.setKnockback((byte) level);
		}

		Vec3 direction = shooter.getLookAngle();
		projectile.shoot(direction.x(), direction.y(), direction.z(), 1.75f, inaccuracy);

		stack.hurtAndBreak(1, shooter, entity -> entity.broadcastBreakEvent(usedHand));

		if (serverLevel.addFreshEntity(projectile)) {
			serverLevel.playSound(null, shooter.getX(), shooter.getY(), shooter.getZ(), SoundEvents.CROSSBOW_SHOOT, SoundSource.PLAYERS, 1f, 1.4f);
		}
	}

	@Override
	public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment) {
		return enchantment == Enchantments.PUNCH_ARROWS || super.canApplyAtEnchantingTable(stack, enchantment);
	}

	@Override
	public void shoot(ServerLevel level, LivingEntity shooter, InteractionHand usedHand, ItemStack projectileWeapon, float damage, float inaccuracy) {
		fireProjectile(level, shooter, usedHand, projectileWeapon, damage, inaccuracy);
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
	public ItemStack getAmmoItemForOverlayRender(ItemStack stack) {
		return new ItemStack(ModItems.MOB_FANG.get());
	}

	@Override
	public Predicate<ItemStack> getAllSupportedProjectiles() {
		return stack -> stack.getItem() == ModItems.NUTRIENTS.get();
	}

	@Override
	public int getDefaultProjectileRange() {
		return 10;
	}

}
