package com.github.elenterius.biomancy.enchantment;

import com.github.elenterius.biomancy.api.livingtool.LivingTool;
import com.github.elenterius.biomancy.api.nutrients.Nutrients;
import com.github.elenterius.biomancy.api.nutrients.NutrientsContainerItem;
import com.github.elenterius.biomancy.init.ModEnchantments;
import com.github.elenterius.biomancy.init.ModItems;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

public class ParasiticMetabolismEnchantment extends Enchantment {

	public ParasiticMetabolismEnchantment(Rarity rarity, EquipmentSlot... applicableSlots) {
		super(rarity, ModEnchantments.LIVING_CATEGORY, applicableSlots);
	}

	@Override
	public boolean isCurse() {
		return true;
	}

	public void repairLivingItems(Player player) {
		if (player.getHealth() <= 10f) return;

		FoodData foodData = player.getFoodData();
		if (foodData.getFoodLevel() <= 2) return;

		List<Map.Entry<EquipmentSlot, ItemStack>> enchantedItems = getItemsWithEnchantment(this, player, LivingTool.NEED_NUTRIENTS_PREDICATE);

		if (!enchantedItems.isEmpty()) {
			Map.Entry<EquipmentSlot, ItemStack> slotItem = enchantedItems.get(player.getRandom().nextInt(enchantedItems.size()));
			ItemStack stack = slotItem.getValue();
			NutrientsContainerItem item = (NutrientsContainerItem) stack.getItem();

			int bonusRepairValue = 2;
			item.increaseNutrients(stack, Nutrients.getRepairValue(ModItems.NUTRIENT_PASTE.get().getDefaultInstance()) + bonusRepairValue);

			if (!player.getAbilities().invulnerable) {
				foodData.setFoodLevel(foodData.getFoodLevel() - 1);
			}
		}
	}

	private static List<Map.Entry<EquipmentSlot, ItemStack>> getItemsWithEnchantment(Enchantment enchantment, LivingEntity livingEntity, Predicate<ItemStack> predicate) {
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
