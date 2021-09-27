package com.github.elenterius.biomancy.enchantment;

import com.github.elenterius.biomancy.init.ModEnchantments;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.inventory.EquipmentSlotType;

public class MaxAmmoEnchantment extends Enchantment {

	public MaxAmmoEnchantment(Rarity rarityIn, EquipmentSlotType... slotTypes) {
		super(rarityIn, ModEnchantments.PROJECTILE_WEAPON_TYPE, slotTypes);
	}

	public int getMinCost(int enchantmentLevel) {
		return 12 + (enchantmentLevel - 1) * 20;
	}

	public int getMaxCost(int enchantmentLevel) {
		return 50;
	}

	public int getMaxLevel() {
		return 3;
	}
}
