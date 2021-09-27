package com.github.elenterius.biomancy.item.weapon.shootable;

import com.github.elenterius.biomancy.entity.projectile.WitherSkullProjectileEntity;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.server.ServerWorld;

import java.util.function.Predicate;

public class WithershotItem extends ProjectileWeaponItem {

	public WithershotItem(Properties builder) {
		super(builder, 0.75f, 0.9f, 8f, 6, 3 * 20);
	}

	public static void fireProjectile(ServerWorld worldIn, LivingEntity shooter, Hand hand, ItemStack projectileWeapon, float damage, float inaccuracy) {
		WitherSkullProjectileEntity projectile = new WitherSkullProjectileEntity(worldIn, shooter);
		projectile.setDamage(damage);
		int level = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.PUNCH_ARROWS, projectileWeapon);
		if (level > 0) {
			projectile.setKnockback((byte) level);
		}

		Vector3d direction = shooter.getLookAngle();
		projectile.shoot(direction.x(), direction.y(), direction.z(), 0.8f, inaccuracy);

		projectileWeapon.hurtAndBreak(1, shooter, (entity) -> entity.broadcastBreakEvent(hand));

		if (worldIn.addFreshEntity(projectile)) {
			worldIn.playSound(null, shooter.getX(), shooter.getY(), shooter.getZ(), SoundEvents.WITHER_SHOOT, SoundCategory.PLAYERS, 1f, 1.4f);
		}
	}

	@Override
	public void shoot(ServerWorld worldIn, LivingEntity shooter, Hand hand, ItemStack projectileWeapon, float damage, float inaccuracy) {
		fireProjectile(worldIn, shooter, hand, projectileWeapon, damage, inaccuracy);
		consumeAmmo(shooter, projectileWeapon, 1);
	}

	@Override
	public Predicate<ItemStack> getAllSupportedProjectiles() {
		return stack -> stack.getItem() == Items.WITHER_SKELETON_SKULL;
	}

	@Override
	public int getDefaultProjectileRange() {
		return 20; //max range
	}

}
