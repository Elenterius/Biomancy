package com.github.elenterius.biomancy.enchantment;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;

public class AnestheticEnchantment extends Enchantment {

	public AnestheticEnchantment(Rarity rarity, EnchantmentCategory category, EquipmentSlot... applicableSlots) {
		super(rarity, category, applicableSlots);
	}

}
