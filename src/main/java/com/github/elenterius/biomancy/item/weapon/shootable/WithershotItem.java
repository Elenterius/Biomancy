package com.github.elenterius.biomancy.item.weapon.shootable;

import com.github.elenterius.biomancy.util.RayTraceUtil;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.WitherSkullEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.server.ServerWorld;

import java.util.function.Predicate;

public class WithershotItem extends ProjectileWeaponItem {

	public WithershotItem(Properties builder) {
		super(builder, 0.75f, 1f, 2, 3 * 20);
	}

	//TODO: consume ammo

	public static void fireProjectile(ServerWorld worldIn, LivingEntity shooter, Hand hand, ItemStack projectileWeapon, double x, double y, double z) {
//			boolean invulnerable = false;

		double d0 = shooter.getPosX();
		double d1 = shooter.getPosYEye() - 0.1d;
		double d2 = shooter.getPosZ();

		WitherSkullEntity projectile = new WitherSkullEntity(worldIn, shooter, x - d0, y - d1, z - d2);
//			if (invulnerable) projectile.setSkullInvulnerable(true);
		projectile.setRawPosition(d0, d1, d2);

		projectileWeapon.damageItem(1, shooter, (entity) -> entity.sendBreakAnimation(hand));

		worldIn.playEvent(null, 1024, shooter.getPosition(), 0);
		worldIn.addEntity(projectile);
	}

	public void fireProjectile(ServerWorld worldIn, LivingEntity shooter, Hand hand, ItemStack projectileWeapon, LivingEntity target) {
		fireProjectile(worldIn, shooter, hand, projectileWeapon, target.getPosX(), target.getPosY() + target.getEyeHeight() * 0.5d, target.getPosZ());
	}

	@Override
	public void shoot(ServerWorld worldIn, LivingEntity livingEntity, Hand hand, ItemStack projectileWeapon, float inaccuracy) {
		RayTraceResult rayTraceResult = RayTraceUtil.rayTrace(livingEntity, target -> !target.isSpectator() && target.isAlive() && target.canBeCollidedWith() && target instanceof LivingEntity && !livingEntity.isRidingSameEntity(target), func_230305_d_());
		if (rayTraceResult.getType() == RayTraceResult.Type.MISS) return;

		if (rayTraceResult.getType() == RayTraceResult.Type.BLOCK && rayTraceResult instanceof BlockRayTraceResult) {
			BlockRayTraceResult rayTrace = (BlockRayTraceResult) rayTraceResult;
			BlockPos targetPos = rayTrace.getPos().offset(rayTrace.getFace());
			fireProjectile(worldIn, livingEntity, hand, projectileWeapon, targetPos.getX() + 0.5d, targetPos.getY() + 0.5d, targetPos.getZ() + 0.5d);
			consumeAmmo(projectileWeapon, 1);
		}
		else if (rayTraceResult.getType() == RayTraceResult.Type.ENTITY && rayTraceResult instanceof EntityRayTraceResult) {
			EntityRayTraceResult rayTrace = (EntityRayTraceResult) rayTraceResult;
			if (rayTrace.getEntity() instanceof LivingEntity) {
				fireProjectile(worldIn, livingEntity, hand, projectileWeapon, (LivingEntity) rayTrace.getEntity());
				consumeAmmo(projectileWeapon, 1);
			}
		}
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
