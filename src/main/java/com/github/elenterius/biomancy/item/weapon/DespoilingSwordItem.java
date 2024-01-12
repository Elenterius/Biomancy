package com.github.elenterius.biomancy.item.weapon;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;

public class DespoilingSwordItem extends SimpleSwordItem {

	public DespoilingSwordItem(Tier tier, int attackDamageModifier, float attackSpeedModifier, Properties properties) {
		super(tier, attackDamageModifier, attackSpeedModifier, properties);
	}

	//	@Override
	//	public int getEnchantmentLevel(ItemStack stack, Enchantment enchantment) {
	//		int level = super.getEnchantmentLevel(stack, enchantment);
	//		return enchantment == ModEnchantments.DESPOIL.get() ? level + 1 : level;
	//	}

	@Override
	public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
		stack.hurtAndBreak(5, attacker, a -> a.broadcastBreakEvent(EquipmentSlot.MAINHAND));
		return true;
	}

}
