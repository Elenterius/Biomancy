package com.github.elenterius.biomancy.world.item.weapon;

import com.github.elenterius.biomancy.world.entity.projectile.WitherProjectile;
import com.github.elenterius.biomancy.world.item.IBiomancyItem;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.phys.Vec3;

import java.util.function.Predicate;

public class WithershotItem extends BaseGunItem implements IBiomancyItem {

	public WithershotItem(Properties properties) {
		super(properties, new GunProperties()
				.fireRate(0.75f)
				.accuracy(0.9f)
				.damage(8f)
				.maxAmmo(6)
				.reloadTime(3 * 20));
	}

	public static void fireProjectile(ServerLevel serverLevel, LivingEntity shooter, InteractionHand hand, ItemStack projectileWeapon, float damage, float inaccuracy) {
		WitherProjectile projectile = new WitherProjectile(serverLevel, shooter);
		projectile.setDamage(damage);
		int level = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.PUNCH_ARROWS, projectileWeapon);
		if (level > 0) {
			projectile.setKnockback((byte) level);
		}

		Vec3 direction = shooter.getLookAngle();
		projectile.shoot(direction.x(), direction.y(), direction.z(), 0.8f, inaccuracy);

		projectileWeapon.hurtAndBreak(1, shooter, (entity) -> entity.broadcastBreakEvent(hand));

		if (serverLevel.addFreshEntity(projectile)) {
			serverLevel.playSound(null, shooter.getX(), shooter.getY(), shooter.getZ(), SoundEvents.WITHER_SHOOT, SoundSource.PLAYERS, 1f, 1.4f);
		}
	}

	@Override
	public void shoot(ServerLevel level, LivingEntity shooter, InteractionHand usedHand, ItemStack projectileWeapon, float damage, float inaccuracy) {
		fireProjectile(level, shooter, usedHand, projectileWeapon, damage, inaccuracy);
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

	@Override
	public ItemStack getAmmoItemForOverlayRender(ItemStack stack) {
		return new ItemStack(Items.WITHER_SKELETON_SKULL);
	}

}
