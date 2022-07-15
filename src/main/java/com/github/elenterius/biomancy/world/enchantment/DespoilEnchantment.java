package com.github.elenterius.biomancy.world.enchantment;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.item.enchantment.Enchantments;

public class DespoilEnchantment extends Enchantment {

	public DespoilEnchantment(Rarity rarity, EnchantmentCategory category, EquipmentSlot... applicableSlots) {
		super(rarity, category, applicableSlots);
	}

	@Override
	public int getMinCost(int level) {
		return 15 + (level - 1) * 9;
	}

	@Override
	public int getMaxCost(int level) {
		return getMinCost(level) + 50;
	}

	@Override
	public int getMaxLevel() {
		return 3;
	}

	@Override
	protected boolean checkCompatibility(Enchantment other) {
		return super.checkCompatibility(other) && other != Enchantments.SILK_TOUCH;
	}

}
