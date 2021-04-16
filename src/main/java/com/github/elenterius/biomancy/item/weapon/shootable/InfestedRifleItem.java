package com.github.elenterius.biomancy.item.weapon.shootable;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.Stats;
import net.minecraft.util.EntityDamageSource;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Quaternion;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.ForgeHooks;

import java.util.Random;
import java.util.function.Predicate;

public class InfestedRifleItem extends ProjectileWeaponItem {

	public InfestedRifleItem(Properties builder) {
		super(builder, 2f, 1f - 0.0075f, 60, 4 * 20);
	}

	public static void fireProjectiles(World worldIn, LivingEntity shooter, Hand hand, ItemStack stack, float velocity, float inaccuracy) {
		boolean isCreativePlayer = shooter instanceof PlayerEntity && ((PlayerEntity) shooter).abilities.isCreativeMode;
		Random rng = shooter.getRNG();
		boolean flag = rng.nextBoolean();

		fireProjectile(worldIn, shooter, hand, stack, 1f, isCreativePlayer, velocity, inaccuracy, 0f);
		fireProjectile(worldIn, shooter, hand, stack, rngPitch(rng, flag), isCreativePlayer, velocity, inaccuracy, -2f);
		fireProjectile(worldIn, shooter, hand, stack, rngPitch(rng, !flag), isCreativePlayer, velocity, inaccuracy, 2f);

		int extraProjectiles = EnchantmentHelper.getEnchantmentLevel(Enchantments.MULTISHOT, stack) * 2;
		for (int i = 1; i < extraProjectiles + 1; i++) {
			fireProjectile(worldIn, shooter, hand, stack, Math.max(0, rngPitch(rng, flag) - 0.05f * i), isCreativePlayer, velocity, inaccuracy, -2f - i);
			fireProjectile(worldIn, shooter, hand, stack, Math.max(0, rngPitch(rng, !flag) - 0.05f * i), isCreativePlayer, velocity, inaccuracy, 2f + i);
		}

		if (shooter instanceof ServerPlayerEntity) {
			ServerPlayerEntity playerEntity = (ServerPlayerEntity) shooter;
			playerEntity.addStat(Stats.ITEM_USED.get(stack.getItem()));
		}
	}

	public static float rngPitch(Random rng, boolean flag) {
		float v = 1f / rng.nextFloat() * 0.5f + 1.8f;
		return flag ? v + 0.63f : v + 0.43f;
	}

	private static void fireProjectile(World worldIn, LivingEntity shooter, Hand hand, ItemStack projectileWeapon, float soundPitch, boolean isCreativeMode, float velocity, float inaccuracy, float projectileAngle) {
		if (!worldIn.isRemote) {
			ProjectileEntity projectileEntity;
			ArrowEntity arrowentity = new ArrowEntity(worldIn, shooter);
			if (shooter instanceof PlayerEntity) {
				arrowentity.setIsCritical(true);
			}
			arrowentity.setDamage(2.5d);
			arrowentity.setHitSound(SoundEvents.ITEM_CROSSBOW_HIT);
			arrowentity.setShotFromCrossbow(true);
			applyEnchantmentsOnProjectile(projectileWeapon, arrowentity);
			projectileEntity = arrowentity;

//			if (shooter instanceof IProjectileWeaponUser) {
//				IProjectileWeaponUser user = (IProjectileWeaponUser) shooter;
//				user.aimAtTargetAndShoot(user.getAttackTarget(), projectileWeapon, projectileEntity, projectileAngle);
//			} else {
			Vector3d upVec = shooter.getUpVector(1.0F);
			Quaternion quaternion = new Quaternion(new Vector3f(upVec), projectileAngle, true);
			Vector3f lookVec = new Vector3f(shooter.getLook(1.0F));
			lookVec.transform(quaternion);
			projectileEntity.shoot(lookVec.getX(), lookVec.getY(), lookVec.getZ(), velocity, inaccuracy);
//			}

			projectileWeapon.damageItem(1, shooter, (entity) -> entity.sendBreakAnimation(hand));
			worldIn.addEntity(projectileEntity);
			worldIn.playSound(null, shooter.getPosX(), shooter.getPosY(), shooter.getPosZ(), SoundEvents.ITEM_CROSSBOW_SHOOT, SoundCategory.PLAYERS, 1.0F, soundPitch);
		}
	}

	public static void applyEnchantmentsOnProjectile(ItemStack projectileWeapon, ArrowEntity arrowEntity) {
		int i = EnchantmentHelper.getEnchantmentLevel(Enchantments.PIERCING, projectileWeapon);
		if (i > 0) {
			arrowEntity.setPierceLevel((byte) i);
		}

		int j = EnchantmentHelper.getEnchantmentLevel(Enchantments.POWER, projectileWeapon);
		if (j > 0) {
			arrowEntity.setDamage(arrowEntity.getDamage() + (double) j * 0.5D + 0.5D);
		}

		int k = EnchantmentHelper.getEnchantmentLevel(Enchantments.PUNCH, projectileWeapon);
		if (k > 0) {
			arrowEntity.setKnockbackStrength(k);
		}

		if (EnchantmentHelper.getEnchantmentLevel(Enchantments.FLAME, projectileWeapon) > 0) {
			//TODO: modify projectile to explode into an gas cloud on impact (lingering AOE effect cloud)
		}
	}

	@Override
	public void shoot(ServerWorld world, LivingEntity livingEntity, Hand hand, ItemStack projectileWeapon, float inaccuracy) {
		fireProjectiles(world, livingEntity, hand, projectileWeapon, 1f, inaccuracy);
		consumeAmmo(projectileWeapon, 3);
	}

	@Override
	public void startReload(ItemStack stack, ServerWorld world, LivingEntity livingEntity) {
		stack.getOrCreateTag().putInt("OldAmmo", getAmmo(stack));
		super.startReload(stack, world, livingEntity);
	}

	@Override
	public void onReloadTick(ItemStack stack, ServerWorld world, LivingEntity livingEntity, long elapsedTime) {
		if (elapsedTime % 20 != 0) return; //only here to prevent wonky screen from too many negative health updates  //TODO: find better way for this
		reloadAmmo(stack, world, livingEntity, elapsedTime);
	}

	@Override
	public void onReloadFinished(ItemStack stack, ServerWorld world, LivingEntity livingEntity) {
		long elapsedTime = world.getGameTime() - getReloadStartTime(stack);
		reloadAmmo(stack, world, livingEntity, elapsedTime);
	}

	@Override
	public void onReloadStopped(ItemStack stack, ServerWorld world, LivingEntity livingEntity) {
		long elapsedTime = world.getGameTime() - getReloadStartTime(stack);
		reloadAmmo(stack, world, livingEntity, elapsedTime);
	}

	@Override
	public void onReloadCanceled(ItemStack stack, ServerWorld world, LivingEntity livingEntity) {
		long elapsedTime = world.getGameTime() - getReloadStartTime(stack);
		reloadAmmo(stack, world, livingEntity, elapsedTime);
	}

	void reloadAmmo(ItemStack stack, ServerWorld world, LivingEntity livingEntity, long elapsedTime) {
		if (canReload(stack, livingEntity)) {
			float reloadAmountPerTick = (float) getMaxAmmo() / (float) getReloadTime(stack);
			int oldAmmo = stack.getOrCreateTag().getInt("OldAmmo");
			int currAmmo = getAmmo(stack);
			int newAmmo = Math.min(getMaxAmmo(), MathHelper.floor(elapsedTime * reloadAmountPerTick) + oldAmmo);

			if (newAmmo > currAmmo) {
				int reloadAmount = newAmmo - currAmmo;

				float unitPrice = (0.13f / getMaxAmmo()) * livingEntity.getMaxHealth();
				float healthCost = reloadAmount * unitPrice;
				boolean isCreativePlayer = livingEntity instanceof PlayerEntity && ((PlayerEntity) livingEntity).abilities.isCreativeMode;
				if (!isCreativePlayer && healthCost > livingEntity.getHealth()) {
					reloadAmount = MathHelper.floor(livingEntity.getHealth() / unitPrice);
				}
				if (!isCreativePlayer) {
					healthCost = ForgeHooks.onLivingDamage(livingEntity, new EntityDamageSource("self", livingEntity), healthCost);
					if (healthCost != 0.0F) {
						livingEntity.setHealth(livingEntity.getHealth() - healthCost);
					}
				}
				addAmmo(stack, reloadAmount);
				SoundCategory soundcategory = livingEntity instanceof PlayerEntity ? SoundCategory.PLAYERS : SoundCategory.HOSTILE;
				world.playSound(null, livingEntity.getPosX(), livingEntity.getPosY(), livingEntity.getPosZ(), SoundEvents.ITEM_CROSSBOW_LOADING_END, soundcategory, 1.0F, 1.0F / (random.nextFloat() * 0.5F + 1.0F) + 0.2F);
			}
		}
	}

	@Override
	public boolean canReload(ItemStack stack, LivingEntity entity) {
		return entity.getHealth() > 0f && super.canReload(stack, entity);
	}

	@Override
	public boolean hasAmmo(ItemStack stack) {
		return getAmmo(stack) >= 3;
	}

	@Override
	public Predicate<ItemStack> getInventoryAmmoPredicate() {
		return stack -> false; //this item uses the players health to reload
	}

	@Override
	public int func_230305_d_() {
		return 15; //max range
	}
}
