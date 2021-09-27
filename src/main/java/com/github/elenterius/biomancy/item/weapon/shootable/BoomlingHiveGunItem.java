package com.github.elenterius.biomancy.item.weapon.shootable;

import com.github.elenterius.biomancy.entity.projectile.BoomlingProjectileEntity;
import com.github.elenterius.biomancy.util.PotionUtilExt;
import com.github.elenterius.biomancy.util.TextUtil;
import com.google.common.primitives.UnsignedBytes;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Potion;
import net.minecraft.potion.Potions;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;

public class BoomlingHiveGunItem extends ProjectileWeaponItem {

	public static final Predicate<ItemStack> VALID_AMMO_ITEM = ToothGunItem.VALID_AMMO_ITEM;
	public static final String NBT_KEY_POTION_COUNT = "PotionCount";
	public static final byte MAX_POTION_COUNT = 32;

	public BoomlingHiveGunItem(Properties builder) {
		super(builder, 0.75f, 0.8f, 0f, 6, 5 * 20);
	}

	public static void fireProjectile(ServerWorld worldIn, LivingEntity shooter, Hand hand, ItemStack projectileWeapon, float inaccuracy) {
		BoomlingProjectileEntity projectile = new BoomlingProjectileEntity(worldIn, shooter);

		Potion potion = PotionUtilExt.getPotion(projectileWeapon);
		List<EffectInstance> customEffects = PotionUtilExt.getCustomEffects(projectileWeapon);
		projectile.setPotion(potion, customEffects, -1);

		Vector3d direction = shooter.getLookAngle();
		//slightly aim up
		projectile.shoot(direction.x(), direction.y() * 1.02d, direction.z(), 0.852f, inaccuracy);

		projectileWeapon.hurtAndBreak(1, shooter, entity -> entity.broadcastBreakEvent(hand));

		if (worldIn.addFreshEntity(projectile)) {
			worldIn.playSound(null, shooter.getX(), shooter.getY(), shooter.getZ(), SoundEvents.WITHER_SHOOT, SoundCategory.PLAYERS, 1f, 1.4f);
		}
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public void appendHoverText(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
		super.appendHoverText(stack, worldIn, tooltip, flagIn);
		int potionCount = getPotionCount(stack);
		if (potionCount > 0) {
			tooltip.add(new StringTextComponent(String.format("Amount: %d/%d", potionCount, MAX_POTION_COUNT)).withStyle(TextFormatting.GRAY));
		}
		else tooltip.add(TextUtil.getTranslationText("tooltip", "contains_nothing").withStyle(TextFormatting.GRAY));
		PotionUtilExt.addPotionTooltip(stack, tooltip, 1f);
	}

	@Override
	public void shoot(ServerWorld worldIn, LivingEntity shooter, Hand hand, ItemStack projectileWeapon, float damage, float inaccuracy) {
		fireProjectile(worldIn, shooter, hand, projectileWeapon, inaccuracy);
		consumeAmmo(shooter, projectileWeapon, 1);
		growPotionCount(projectileWeapon, -1);
	}

	@Override
	public boolean hasAmmo(ItemStack stack) {
		return hasPotion(stack) && getPotionCount(stack) > 0 && super.hasAmmo(stack);
	}

	@Override
	public Predicate<ItemStack> getAllSupportedProjectiles() {
		return VALID_AMMO_ITEM;
	}

	@Override
	public int getDefaultProjectileRange() {
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
		int value = MathHelper.clamp(count + amount, 0, MAX_POTION_COUNT);
		stack.getOrCreateTag().putByte(NBT_KEY_POTION_COUNT, UnsignedBytes.saturatedCast(value)); //saturated cast clamps to 0-255
		if (value == 0) {
			PotionUtilExt.removePotionFromHost(stack);
		}
	}

	public int getPotionCount(ItemStack stack) {
		return UnsignedBytes.toInt(stack.getOrCreateTag().getByte(NBT_KEY_POTION_COUNT));
	}

	public Potion getPotion(ItemStack stack) {
		return PotionUtilExt.getPotion(stack);
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
