package com.github.elenterius.biomancy.item.weapon.shootable;

import com.github.elenterius.biomancy.entity.projectile.ToothProjectileEntity;
import com.github.elenterius.biomancy.init.ModItems;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.server.ServerWorld;

import java.util.function.Predicate;

public class ToothGunItem extends ProjectileWeaponItem {

	public static final Predicate<ItemStack> VALID_AMMO_ITEM = (stack) -> stack.getItem() == ModItems.NUTRIENT_PASTE.get();

	public ToothGunItem(Properties builder) {
		super(builder, 1.25f, 0.975f, 6, 4 * 20);
	}

	public static void fireProjectile(ServerWorld worldIn, LivingEntity shooter, Hand hand, ItemStack projectileWeapon, float inaccuracy) {
		if (!worldIn.isRemote) {
			ToothProjectileEntity projectile = new ToothProjectileEntity(worldIn, shooter);
			Vector3d direction = shooter.getLookVec();
			projectile.shoot(direction.getX(), direction.getY(), direction.getZ(), 1f, inaccuracy);

			projectileWeapon.damageItem(1, shooter, (entity) -> entity.sendBreakAnimation(hand));

			if (worldIn.addEntity(projectile)) {
				worldIn.playSound(null, shooter.getPosX(), shooter.getPosY(), shooter.getPosZ(), SoundEvents.ITEM_CROSSBOW_SHOOT, SoundCategory.PLAYERS, 1f, 1.4f);
			}
		}
	}

	@Override
	public void shoot(ServerWorld world, LivingEntity shooter, Hand hand, ItemStack projectileWeapon, float inaccuracy) {
		fireProjectile(world, shooter, hand, projectileWeapon, inaccuracy);
		consumeAmmo(projectileWeapon, 1);
	}

	@Override
	public int getAmmoReloadCost() {
		return 3;
	}

	@Override
	public void stopShooting(ItemStack stack, ServerWorld world, LivingEntity shooter) {
		startReload(stack, world, shooter); //auto reload
	}

	@Override
	public void onReloadTick(ItemStack stack, ServerWorld world, LivingEntity shooter, long elapsedTime) {
		if (elapsedTime % 20L == 0L) playSFX(world, shooter, SoundEvents.ENTITY_GENERIC_EAT);
	}

	@Override
	public void onReloadStarted(ItemStack stack, ServerWorld world, LivingEntity shooter) {
		playSFX(world, shooter, SoundEvents.ENTITY_GENERIC_EAT);
	}

	@Override
	public void onReloadCanceled(ItemStack stack, ServerWorld world, LivingEntity shooter) {
		playSFX(world, shooter, SoundEvents.BLOCK_DISPENSER_FAIL);
	}

	@Override
	public void onReloadStopped(ItemStack stack, ServerWorld world, LivingEntity shooter) {
		playSFX(world, shooter, SoundEvents.BLOCK_DISPENSER_FAIL);
	}

	@Override
	public void onReloadFinished(ItemStack stack, ServerWorld world, LivingEntity shooter) {
		playSFX(world, shooter, SoundEvents.ENTITY_PLAYER_BURP);
	}

	@Override
	public Predicate<ItemStack> getInventoryAmmoPredicate() {
		return VALID_AMMO_ITEM;
	}

	@Override
	public int func_230305_d_() {
		return 10; //max range
	}

}
