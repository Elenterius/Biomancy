package com.github.elenterius.biomancy.enchantment;

import com.github.elenterius.biomancy.init.ModEnchantments;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.enchantment.Enchantment;

public class PrimordialKnowledgeEnchantment extends Enchantment {

	public PrimordialKnowledgeEnchantment(Rarity rarity, EquipmentSlot... applicableSlots) {
		super(rarity, ModEnchantments.LIVING_CATEGORY, applicableSlots);
	}

	@Override
	public boolean isCurse() {
		return true;
	}

	@Override
	public int getMaxLevel() {
		return 2;
	}

}
