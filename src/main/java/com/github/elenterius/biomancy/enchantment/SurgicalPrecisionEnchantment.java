package com.github.elenterius.biomancy.enchantment;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;

public class SurgicalPrecisionEnchantment extends Enchantment {

	public SurgicalPrecisionEnchantment(Rarity rarity, EnchantmentCategory category, EquipmentSlot... applicableSlots) {
		super(rarity, category, applicableSlots);
	}

	@Override
	public int getMaxLevel() {
		return 3;
	}

}
