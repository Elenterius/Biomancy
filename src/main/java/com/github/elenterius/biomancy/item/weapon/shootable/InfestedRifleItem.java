package com.github.elenterius.biomancy.item.weapon.shootable;

import com.github.elenterius.biomancy.entity.projectile.ToothProjectileEntity;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
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

/**
 * dev-gun, for experiments etc
 */
public class InfestedRifleItem extends ProjectileWeaponItem {

	public InfestedRifleItem(Properties builder) {
		super(builder, 2f, 0.95f, 2f, 60, 4 * 20);
	}

	public static void fireProjectiles(World worldIn, LivingEntity shooter, Hand hand, ItemStack stack, float damage, float velocity, float inaccuracy) {
		boolean isCreativePlayer = shooter instanceof PlayerEntity && ((PlayerEntity) shooter).abilities.instabuild;
		Random rng = shooter.getRandom();
		boolean flag = rng.nextBoolean();

		fireProjectile(worldIn, shooter, hand, stack, 1f, isCreativePlayer, damage, velocity, inaccuracy, 0f);
		fireProjectile(worldIn, shooter, hand, stack, rngPitch(rng, flag), isCreativePlayer, damage, velocity, inaccuracy, -2f);
		fireProjectile(worldIn, shooter, hand, stack, rngPitch(rng, !flag), isCreativePlayer, damage, velocity, inaccuracy, 2f);

		int extraProjectiles = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.MULTISHOT, stack) * 2;
		for (int i = 1; i < extraProjectiles + 1; i++) {
			fireProjectile(worldIn, shooter, hand, stack, Math.max(0, rngPitch(rng, flag) - 0.05f * i), isCreativePlayer, damage, velocity, inaccuracy, -2f - i);
			fireProjectile(worldIn, shooter, hand, stack, Math.max(0, rngPitch(rng, !flag) - 0.05f * i), isCreativePlayer, damage, velocity, inaccuracy, 2f + i);
		}

		if (shooter instanceof ServerPlayerEntity) {
			ServerPlayerEntity playerEntity = (ServerPlayerEntity) shooter;
			playerEntity.awardStat(Stats.ITEM_USED.get(stack.getItem()));
		}
	}

	public static float rngPitch(Random rng, boolean flag) {
		float v = 1f / rng.nextFloat() * 0.5f + 1.8f;
		return flag ? v + 0.63f : v + 0.43f;
	}

	private static void fireProjectile(World worldIn, LivingEntity shooter, Hand hand, ItemStack projectileWeapon, float soundPitch, boolean isCreativeMode, float damage, float velocity, float inaccuracy, float projectileAngle) {
		if (!worldIn.isClientSide) {
			ToothProjectileEntity toothProjectile = new ToothProjectileEntity(worldIn, shooter);
//			applyEnchantmentsOnProjectile(projectileWeapon, arrowentity);

//			if (shooter instanceof IProjectileWeaponUser) {
//				IProjectileWeaponUser user = (IProjectileWeaponUser) shooter;
//				user.aimAtTargetAndShoot(user.getAttackTarget(), projectileWeapon, projectileEntity, projectileAngle);
//			} else {

			//projectile pattern
			Vector3f forward = new Vector3f(shooter.getLookAngle());
			Vector3d up = shooter.getUpVector(1f);
			forward.transform(new Quaternion(new Vector3f(up), projectileAngle, true));
			Vector3d right = new Vector3d(forward).cross(up);
			forward.transform(new Quaternion(new Vector3f(right), projectileAngle * (0.5f * random.nextFloat() - 0.5f), true));

			toothProjectile.shoot(forward.x(), forward.y(), forward.z(), velocity, inaccuracy);
			toothProjectile.setDamage(damage);
//			}

			projectileWeapon.hurtAndBreak(1, shooter, (entity) -> entity.broadcastBreakEvent(hand));

			if (worldIn.addFreshEntity(toothProjectile)) {
				worldIn.playSound(null, shooter.getX(), shooter.getY(), shooter.getZ(), SoundEvents.CROSSBOW_SHOOT, SoundCategory.PLAYERS, 1f, soundPitch);
			}
		}
	}

	@Override
	public void shoot(ServerWorld world, LivingEntity shooter, Hand hand, ItemStack projectileWeapon, float damage, float inaccuracy) {
		fireProjectiles(world, shooter, hand, projectileWeapon, damage, 1f, inaccuracy);
		consumeAmmo(shooter, projectileWeapon, 3);
	}

	@Override
	public void onReloadStarted(ItemStack stack, ServerWorld world, LivingEntity shooter) {
		stack.getOrCreateTag().putInt("OldAmmo", getAmmo(stack));
	}

	@Override
	public void onReloadTick(ItemStack stack, ServerWorld world, LivingEntity shooter, long elapsedTime) {
		if (elapsedTime % 20L != 0L) return; //only here to prevent wonky screen from too many negative health updates  //TODO: find better way for this
		reloadAmmo(stack, world, shooter, elapsedTime);
		if (elapsedTime % 40L == 0L) playSFX(world, shooter, SoundEvents.CROSSBOW_LOADING_MIDDLE);
	}

	@Override
	public void finishReload(ItemStack stack, ServerWorld world, LivingEntity shooter) {
		setState(stack, State.NONE);
		onReloadFinished(stack, world, shooter);
	}

	@Override
	public void onReloadFinished(ItemStack stack, ServerWorld world, LivingEntity shooter) {
		long elapsedTime = world.getGameTime() - getReloadStartTime(stack);
		reloadAmmo(stack, world, shooter, elapsedTime);
	}

	@Override
	public void onReloadStopped(ItemStack stack, ServerWorld world, LivingEntity shooter) {
		long elapsedTime = world.getGameTime() - getReloadStartTime(stack);
		reloadAmmo(stack, world, shooter, elapsedTime);
	}

	@Override
	public void onReloadCanceled(ItemStack stack, ServerWorld world, LivingEntity shooter) {
		long elapsedTime = world.getGameTime() - getReloadStartTime(stack);
		reloadAmmo(stack, world, shooter, elapsedTime);
	}

	void reloadAmmo(ItemStack stack, ServerWorld world, LivingEntity livingEntity, long elapsedTime) {
		if (canReload(stack, livingEntity)) {
			float reloadAmountPerTick = (float) getMaxAmmo(stack) / (float) getReloadTime(stack);
			int oldAmmo = stack.getOrCreateTag().getInt("OldAmmo");
			int currAmmo = getAmmo(stack);
			int newAmmo = Math.min(getMaxAmmo(stack), MathHelper.floor(elapsedTime * reloadAmountPerTick) + oldAmmo);

			if (newAmmo > currAmmo) {
				int reloadAmount = newAmmo - currAmmo;

				float unitPrice = (0.13f / getMaxAmmo(stack)) * livingEntity.getMaxHealth();
				float healthCost = reloadAmount * unitPrice;
				boolean isCreativePlayer = livingEntity instanceof PlayerEntity && ((PlayerEntity) livingEntity).abilities.instabuild;
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
			}
		}
	}

	@Override
	public boolean canReload(ItemStack stack, LivingEntity shooter) {
		return shooter.getHealth() > 0f && getAmmo(stack) < getMaxAmmo(stack);
	}

	@Override
	public boolean hasAmmo(ItemStack stack) {
		return getAmmo(stack) >= 3;
	}

	@Override
	public Predicate<ItemStack> getAllSupportedProjectiles() {
		return stack -> false; //this item uses the players health to reload
	}

	@Override
	public int getDefaultProjectileRange() {
		return 15; //max range
	}
}
