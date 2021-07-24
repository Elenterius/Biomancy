package com.github.elenterius.biomancy.item.weapon.shootable;

import com.github.elenterius.biomancy.entity.projectile.BoomlingProjectileEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potions;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.server.ServerWorld;

import java.util.Collections;
import java.util.function.Predicate;

public class BoomlingHiveGunItem extends ProjectileWeaponItem {

	public BoomlingHiveGunItem(Properties builder) {
		super(builder, 0.75f, 0.8f, 0f, 6, 5 * 20);
	}

	public static void fireProjectile(ServerWorld worldIn, LivingEntity shooter, Hand hand, ItemStack projectileWeapon, float damage, float inaccuracy) {
		BoomlingProjectileEntity projectile = new BoomlingProjectileEntity(worldIn, shooter);
		projectile.setPotionAndEffect(Potions.HARMING, Collections.emptyList(), -1);

		Vector3d direction = shooter.getLookVec();
		projectile.shoot(direction.getX(), direction.getY(), direction.getZ(), 0.85f, inaccuracy);

		projectileWeapon.damageItem(1, shooter, entity -> entity.sendBreakAnimation(hand));

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
		return ToothGunItem.VALID_AMMO_ITEM;
	}

	@Override
	public int func_230305_d_() {
		return 20; //max range
	}

}
