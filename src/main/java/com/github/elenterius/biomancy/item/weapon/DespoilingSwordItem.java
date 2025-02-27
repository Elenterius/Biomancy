package com.github.elenterius.biomancy.item.weapon;

import com.github.elenterius.biomancy.init.ModEnchantments;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.ToolAction;

public class DespoilingSwordItem extends SimpleSwordItem {

	public DespoilingSwordItem(Tier tier, int attackDamageModifier, float attackSpeedModifier, Properties properties) {
		super(tier, attackDamageModifier, attackSpeedModifier, properties);
	}

	public static boolean isBroken(ItemStack stack) {
		return !isNotBroken(stack);
	}

	public static boolean isNotBroken(ItemStack stack) {
		return stack.getDamageValue() < stack.getMaxDamage() - 1;
	}

	@Override
	public boolean isDamageable(ItemStack stack) {
		return isNotBroken(stack);
	}

	@Override
	public int getEnchantmentLevel(ItemStack stack, Enchantment enchantment) {
		if (isBroken(stack)) return 0;

		int level = super.getEnchantmentLevel(stack, enchantment);

		if (enchantment == ModEnchantments.DESPOIL.get()) {
			return level + 1;
		}

		return level;
	}

	@Override
	public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
		if (isBroken(stack)) return false;

		if (!target.isDeadOrDying()) {
			stack.hurtAndBreak(2, attacker, a -> a.broadcastBreakEvent(EquipmentSlot.MAINHAND));
		}
		return true;
	}

	@Override
	public boolean mineBlock(ItemStack stack, Level level, BlockState state, BlockPos pos, LivingEntity livingEntity) {
		if (isBroken(stack)) return false;
		return super.mineBlock(stack, level, state, pos, livingEntity);
	}

	@Override
	public float getDestroySpeed(ItemStack stack, BlockState state) {
		if (isBroken(stack)) return 0f;
		return super.getDestroySpeed(stack, state);
	}

	@Override
	public boolean isCorrectToolForDrops(ItemStack stack, BlockState state) {
		if (isBroken(stack)) return false;
		return super.isCorrectToolForDrops(stack, state);
	}

	@Override
	public boolean canPerformAction(ItemStack stack, ToolAction toolAction) {
		if (isBroken(stack)) return false;
		return super.canPerformAction(stack, toolAction);
	}

	@Override
	public Multimap<Attribute, AttributeModifier> getAttributeModifiers(EquipmentSlot slot, ItemStack stack) {
		if (isBroken(stack)) return ImmutableMultimap.of();
		return super.getAttributeModifiers(slot, stack);
	}

}
