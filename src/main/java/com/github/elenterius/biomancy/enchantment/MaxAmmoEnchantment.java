package com.github.elenterius.biomancy.enchantment;

import com.github.elenterius.biomancy.init.ModEnchantments;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.enchantment.Enchantment;

@Deprecated
public class MaxAmmoEnchantment extends Enchantment {

	public MaxAmmoEnchantment(Enchantment.Rarity rarityIn, EquipmentSlot... slotTypes) {
		super(rarityIn, ModEnchantments.GUN_CATEGORY, slotTypes);
	}

	@Override
	public int getMinCost(int enchantmentLevel) {
		return 12 + (enchantmentLevel - 1) * 20;
	}

	@Override
	public int getMaxCost(int enchantmentLevel) {
		return 50;
	}

	@Override
	public int getMaxLevel() {
		return 3;
	}

}
