package com.github.elenterius.biomancy.item.weapon;

import com.github.elenterius.biomancy.init.ModEnchantments;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.enchantment.Enchantment;

public class DespoilingSwordItem extends SimpleSwordItem {

	public DespoilingSwordItem(Tier tier, int attackDamageModifier, float attackSpeedModifier, Properties properties) {
		super(tier, attackDamageModifier, attackSpeedModifier, properties);
	}

	@Override
	public int getEnchantmentLevel(ItemStack stack, Enchantment enchantment) {
		int level = super.getEnchantmentLevel(stack, enchantment);
		return enchantment == ModEnchantments.DESPOIL.get() ? level + 1 : level;
	}

	@Override
	public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
		int itemDamage = target.isDeadOrDying() ? 1 : 5;
		stack.hurtAndBreak(itemDamage, attacker, a -> a.broadcastBreakEvent(EquipmentSlot.MAINHAND));
		return true;
	}
}
