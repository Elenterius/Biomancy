package com.github.elenterius.blightlings.enchantment;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;

public class ClimbingEnchantment extends Enchantment {
	public ClimbingEnchantment(Rarity rarityIn) {
		super(rarityIn, EnchantmentType.ARMOR_FEET, new EquipmentSlotType[]{EquipmentSlotType.LEGS});
	}

	@Override
	public int getMinEnchantability(int enchantmentLevel) {
		return enchantmentLevel * 10;
	}

	@Override
	public int getMaxEnchantability(int enchantmentLevel) {
		return getMinEnchantability(enchantmentLevel) + 15;
	}

	@Override
	public int getMaxLevel() {
		return 1;
	}

	@Override
	public boolean isTreasureEnchantment() {
		return true;
	}

	@Override
	public boolean canVillagerTrade() {
		return false;
	}

	@Override
	public boolean canGenerateInLoot() {
		return false;
	}

	public boolean tryToClimb(PlayerEntity entity) {
		if (entity.collidedHorizontally && (entity.getFoodStats().getFoodLevel() > 6.0F || entity.abilities.isCreativeMode)) {
			if (!entity.isOnLadder()) {  // normal climbing
				Vector3d motion = entity.getMotion();
				double minmax = 0.015f;
				double mx = MathHelper.clamp(motion.x, -minmax, minmax);
				double mz = MathHelper.clamp(motion.z, -minmax, minmax);
				double my = entity.isSneaking() ? Math.max(motion.y, 0d) : 0.1d; //stick to wall when sneaking
				entity.fallDistance = 0f;
				entity.setMotion(mx, my, mz);
				return true;
			} else if (!entity.isSneaking()) { // ladder climbing
				entity.setMotion(entity.getMotion().add(0d, 0.08700634D, 0d));
				return true;
			}
		}
		return false;
	}
}
