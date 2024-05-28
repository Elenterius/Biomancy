package com.github.elenterius.biomancy.util;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

public final class EnchantmentUtil {

	private EnchantmentUtil() {}

	public static List<Map.Entry<EquipmentSlot, ItemStack>> getItemsWithEnchantment(Enchantment enchantment, LivingEntity livingEntity, Predicate<ItemStack> predicate) {
		Map<EquipmentSlot, ItemStack> map = enchantment.getSlotItems(livingEntity);
		List<Map.Entry<EquipmentSlot, ItemStack>> list = new ArrayList<>();

		for (Map.Entry<EquipmentSlot, ItemStack> entry : map.entrySet()) {
			ItemStack stack = entry.getValue();
			if (!stack.isEmpty() && stack.getEnchantmentLevel(enchantment) > 0 && predicate.test(stack)) {
				list.add(entry);
			}
		}

		return list;
	}

}
