package com.github.elenterius.biomancy.item.weapon;

import com.github.elenterius.biomancy.entity.projectile.WitherProjectile;
import com.github.elenterius.biomancy.item.ICustomTooltip;
import com.github.elenterius.biomancy.util.fuel.NutrientFuelUtil;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.phys.Vec3;

import java.util.function.Predicate;

@Deprecated(forRemoval = true)
public class WithershotItem extends BaseGunItem implements ICustomTooltip {

	public WithershotItem(Properties properties) {
		super(properties, new GunProperties()
				.fireRate(0.75f)
				.accuracy(0.9f)
				.damage(8f)
				.maxAmmo(6)
				.reloadTime(3 * 20));
	}

	public static void fireProjectile(ServerLevel serverLevel, LivingEntity shooter, InteractionHand hand, ItemStack projectileWeapon, ProjectileProperties properties) {
		WitherProjectile projectile = new WitherProjectile(serverLevel, shooter);
		projectile.setDamage(properties.damage());
		if (properties.knockBack() > 0) {
			projectile.setKnockback((byte) properties.knockBack());
		}

		Vec3 direction = shooter.getLookAngle();
		projectile.shoot(direction.x(), direction.y(), direction.z(), 0.8f, properties.inaccuracy());

		projectileWeapon.hurtAndBreak(1, shooter, (entity) -> entity.broadcastBreakEvent(hand));

		if (serverLevel.addFreshEntity(projectile)) {
			serverLevel.playSound(null, shooter.getX(), shooter.getY(), shooter.getZ(), SoundEvents.WITHER_SHOOT, SoundSource.PLAYERS, 1f, 1.4f);
		}
	}

	@Override
	public void shoot(ServerLevel level, LivingEntity shooter, InteractionHand usedHand, ItemStack projectileWeapon, ProjectileProperties properties) {
		fireProjectile(level, shooter, usedHand, projectileWeapon, properties);
		consumeAmmo(shooter, projectileWeapon, 1);
	}

	@Override
	public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment) {
		return enchantment != Enchantments.PUNCH_ARROWS && super.canApplyAtEnchantingTable(stack, enchantment);
	}

	@Override
	public Predicate<ItemStack> getAllSupportedProjectiles() {
		return NutrientFuelUtil.AMMO_PREDICATE;
	}

	@Override
	public int getDefaultProjectileRange() {
		return 20; //max range
	}

	@Override
	public ItemStack getAmmoIcon(ItemStack stack) {
		return new ItemStack(Items.WITHER_SKELETON_SKULL);
	}

}
