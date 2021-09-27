package com.github.elenterius.biomancy.enchantment;

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
	public int getMinCost(int enchantmentLevel) {
		return enchantmentLevel * 10;
	}

	@Override
	public int getMaxCost(int enchantmentLevel) {
		return getMinCost(enchantmentLevel) + 15;
	}

	@Override
	public int getMaxLevel() {
		return 1;
	}

	@Override
	public boolean isTreasureOnly() {
		return true;
	}

	@Override
	public boolean isTradeable() {
		return true;
	}

	@Override
	public boolean isDiscoverable() {
		return true;
	}

	public boolean tryToClimb(PlayerEntity entity) {
		if (entity.horizontalCollision && (entity.getFoodData().getFoodLevel() > 6.0F || entity.abilities.instabuild)) {
			if (!entity.onClimbable()) {  // normal climbing
				Vector3d motion = entity.getDeltaMovement();
				double minmax = 0.015f;
				double mx = MathHelper.clamp(motion.x, -minmax, minmax);
				double mz = MathHelper.clamp(motion.z, -minmax, minmax);
				double my = entity.isShiftKeyDown() ? Math.max(motion.y, 0d) : 0.1d; //stick to wall when sneaking
				entity.fallDistance = 0f;
				entity.setDeltaMovement(mx, my, mz);
				return true;
			}
			else if (!entity.isShiftKeyDown()) { // ladder climbing
				entity.setDeltaMovement(entity.getDeltaMovement().add(0d, 0.08700634D, 0d));
				return true;
			}
		}
		return false;
	}
}
