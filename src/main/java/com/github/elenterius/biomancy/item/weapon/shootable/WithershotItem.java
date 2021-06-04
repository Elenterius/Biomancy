package com.github.elenterius.biomancy.item.weapon.shootable;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.WitherSkullEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.util.Constants;

import java.util.function.Predicate;

public class WithershotItem extends ProjectileWeaponItem {

	public WithershotItem(Properties builder) {
		super(builder, 0.75f, 0.94f, 8f, 6, 3 * 20);
	}

	public static void fireProjectile(ServerWorld worldIn, LivingEntity shooter, Hand hand, ItemStack projectileWeapon, double x, double y, double z, float inaccuracy) {
		double d0 = shooter.getPosX();
		double d1 = shooter.getPosYEye() - 0.1d;
		double d2 = shooter.getPosZ();

		WitherSkullEntity projectile = new WitherSkullEntity(worldIn, shooter, x - d0, y - d1, z - d2);
		projectile.setRawPosition(d0, d1, d2);
		Vector3d direction = shooter.getLookVec();
		projectile.shoot(direction.getX(), direction.getY(), direction.getZ(), 1f, inaccuracy);

		projectileWeapon.damageItem(1, shooter, (entity) -> entity.sendBreakAnimation(hand));

		worldIn.playEvent(null, Constants.WorldEvents.WITHER_SHOOT_SOUND, shooter.getPosition(), 0);
		worldIn.addEntity(projectile);
	}

	@Override
	public void shoot(ServerWorld worldIn, LivingEntity shooter, Hand hand, ItemStack projectileWeapon, float damage, float inaccuracy) {
		Vector3d startVec = shooter.getEyePosition(1f);
		Vector3d lookVec = shooter.getLookVec();
		Vector3d directionVec = lookVec.scale(func_230305_d_());
		Vector3d endVec = startVec.add(directionVec);
		fireProjectile(worldIn, shooter, hand, projectileWeapon, endVec.x, endVec.y, endVec.z, inaccuracy);
		consumeAmmo(projectileWeapon, 1);
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
