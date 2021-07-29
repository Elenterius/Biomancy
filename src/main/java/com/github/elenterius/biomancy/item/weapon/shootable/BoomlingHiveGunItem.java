package com.github.elenterius.biomancy.item.weapon.shootable;

import com.github.elenterius.biomancy.entity.projectile.BoomlingProjectileEntity;
import com.github.elenterius.biomancy.util.PotionUtilExt;
import com.google.common.primitives.UnsignedBytes;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionUtils;
import net.minecraft.potion.Potions;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.server.ServerWorld;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;

public class BoomlingHiveGunItem extends ProjectileWeaponItem {

	public static final Predicate<ItemStack> VALID_AMMO_ITEM = ToothGunItem.VALID_AMMO_ITEM;
	public static final String NBT_KEY_POTION_COUNT = "PotionCount";

	public BoomlingHiveGunItem(Properties builder) {
		super(builder, 0.75f, 0.8f, 0f, 6, 5 * 20);
	}

	public static void fireProjectile(ServerWorld worldIn, LivingEntity shooter, Hand hand, ItemStack projectileWeapon, float inaccuracy) {
		BoomlingProjectileEntity projectile = new BoomlingProjectileEntity(worldIn, shooter);

		Potion potion = PotionUtilExt.getPotionFromItem(projectileWeapon);
		List<EffectInstance> customEffects = PotionUtils.getFullEffectsFromItem(projectileWeapon);
		projectile.setPotion(potion, customEffects, -1);

		Vector3d direction = shooter.getLookVec();
		//slightly aim up
		projectile.shoot(direction.getX(), direction.getY() * 1.02d, direction.getZ(), 0.852f, inaccuracy);

		projectileWeapon.damageItem(1, shooter, entity -> entity.sendBreakAnimation(hand));

		if (worldIn.addEntity(projectile)) {
			worldIn.playSound(null, shooter.getPosX(), shooter.getPosY(), shooter.getPosZ(), SoundEvents.ENTITY_WITHER_SHOOT, SoundCategory.PLAYERS, 1f, 1.4f);
		}
	}

	@Override
	public void shoot(ServerWorld worldIn, LivingEntity shooter, Hand hand, ItemStack projectileWeapon, float damage, float inaccuracy) {
		fireProjectile(worldIn, shooter, hand, projectileWeapon, inaccuracy);
		consumeAmmo(shooter, projectileWeapon, 1);
	}

	@Override
	public boolean hasAmmo(ItemStack stack) {
		return hasPotion(stack) && super.hasAmmo(stack);
	}

	@Override
	public Predicate<ItemStack> getInventoryAmmoPredicate() {
		return VALID_AMMO_ITEM;
	}

	@Override
	public int func_230305_d_() {
		return 20; //max range
	}

	public boolean hasPotion(ItemStack stack) {
		return getPotion(stack) != Potions.EMPTY;
	}

	public void setPotionCount(ItemStack stack, int count) {
		stack.getOrCreateTag().putByte(NBT_KEY_POTION_COUNT, UnsignedBytes.saturatedCast(count)); //saturated cast clamps to 0-255
	}

	public void growPotionCount(ItemStack stack, int amount) {
		int count = getPotionCount(stack);
		stack.getOrCreateTag().putByte(NBT_KEY_POTION_COUNT, UnsignedBytes.saturatedCast(count + amount)); //saturated cast clamps to 0-255
	}

	public int getPotionCount(ItemStack stack) {
		return UnsignedBytes.toInt(stack.getOrCreateTag().getByte(NBT_KEY_POTION_COUNT));
	}

	public Potion getPotion(ItemStack stack) {
		return PotionUtilExt.getPotionFromItem(stack);
	}

	public void setPotion(ItemStack stack, Potion potion, @Nullable Collection<EffectInstance> customEffects) {
		PotionUtilExt.setPotionOfHost(stack, potion, customEffects);
	}

	public void setPotion(ItemStack stack, ItemStack potionStack) {
		PotionUtilExt.setPotionOfHost(stack, potionStack);
	}

	public int getPotionColor(ItemStack stack) {
		return PotionUtilExt.getPotionColor(stack);
	}

}
