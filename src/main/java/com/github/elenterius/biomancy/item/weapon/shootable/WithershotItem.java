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
		int level = EnchantmentHelper.getEnchantmentLevel(Enchantments.PUNCH, projectileWeapon);
		if (level > 0) {
			projectile.setKnockback((byte) level);
		}

		Vector3d direction = shooter.getLookVec();
		projectile.shoot(direction.getX(), direction.getY(), direction.getZ(), 0.8f, inaccuracy);

		projectileWeapon.damageItem(1, shooter, (entity) -> entity.sendBreakAnimation(hand));

		if (worldIn.addEntity(projectile)) {
			worldIn.playSound(null, shooter.getPosX(), shooter.getPosY(), shooter.getPosZ(), SoundEvents.ENTITY_WITHER_SHOOT, SoundCategory.PLAYERS, 1f, 1.4f);
		}
	}

	@Override
	public void shoot(ServerWorld worldIn, LivingEntity shooter, Hand hand, ItemStack projectileWeapon, float damage, float inaccuracy) {
		fireProjectile(worldIn, shooter, hand, projectileWeapon, damage, inaccuracy);
		consumeAmmo(shooter, projectileWeapon, 1);
	}

	@Override
	public Predicate<ItemStack> getInventoryAmmoPredicate() {
		return stack -> stack.getItem() == Items.WITHER_SKELETON_SKULL;
	}

	@Override
	public int func_230305_d_() {
		return 20; //max range
	}

}
