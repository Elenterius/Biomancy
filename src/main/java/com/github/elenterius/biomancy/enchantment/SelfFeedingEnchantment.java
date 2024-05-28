package com.github.elenterius.biomancy.enchantment;

import com.github.elenterius.biomancy.api.livingtool.LivingTool;
import com.github.elenterius.biomancy.api.nutrients.Nutrients;
import com.github.elenterius.biomancy.api.nutrients.NutrientsContainerItem;
import com.github.elenterius.biomancy.init.ModEnchantments;
import com.github.elenterius.biomancy.init.ModItems;
import com.github.elenterius.biomancy.util.EnchantmentUtil;
import net.minecraft.core.NonNullList;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;

import java.util.List;
import java.util.Map;

public class SelfFeedingEnchantment extends Enchantment {

	public SelfFeedingEnchantment(Rarity rarity, EquipmentSlot... applicableSlots) {
		super(rarity, ModEnchantments.LIVING_CATEGORY, applicableSlots);
	}

	public void repairLivingItems(Player player) {
		List<Map.Entry<EquipmentSlot, ItemStack>> enchantedItems = EnchantmentUtil.getItemsWithEnchantment(this, player, LivingTool.NEED_NUTRIENTS_PREDICATE);
		if (enchantedItems.isEmpty()) return;

		Map.Entry<EquipmentSlot, ItemStack> slotItem = enchantedItems.get(player.getRandom().nextInt(enchantedItems.size()));
		ItemStack stack = slotItem.getValue();
		NutrientsContainerItem nutrientsContainer = (NutrientsContainerItem) stack.getItem();
		int neededRepairValue = nutrientsContainer.getMaxNutrients(stack) - nutrientsContainer.getNutrients(stack);

		ItemStack repairItemStack = getBestRepairItem(player, neededRepairValue);
		if (repairItemStack.isEmpty()) return;

		nutrientsContainer.increaseNutrients(stack, Nutrients.getRepairValue(repairItemStack));

		if (!player.getAbilities().instabuild) {
			if (repairItemStack.hasCraftingRemainingItem()) {
				ItemStack craftingRemainder = repairItemStack.getCraftingRemainingItem();
				repairItemStack.shrink(1);
				if (!craftingRemainder.isEmpty() && !player.addItem(craftingRemainder)) {
					player.drop(craftingRemainder, false);
				}
			}
			else repairItemStack.shrink(1);
		}
	}

	protected ItemStack getBestRepairItem(Player player, int neededRepairValue) {
		NonNullList<ItemStack> items = player.getInventory().items;

		ItemStack repairItemStack = ItemStack.EMPTY;
		int minError = Integer.MAX_VALUE;

		//loop through hot-bar slots
		for (int i = 0; i < 9; i++) {
			ItemStack itemStack = items.get(i);

			int repairValue = Nutrients.getRepairValue(itemStack);
			if (repairValue <= 0) continue;

			int error;
			Item item = itemStack.getItem();
			if (item == ModItems.NUTRIENT_PASTE.get() || item == ModItems.NUTRIENT_BAR.get()) {
				error = repairValue > neededRepairValue ? (repairValue - neededRepairValue) / 2 : -repairValue * 2;
			}
			else {
				error = repairValue > neededRepairValue ? (repairValue - neededRepairValue) * 2 : -repairValue / 2;
			}

			if (error < minError) {
				minError = error;
				repairItemStack = itemStack;
			}
		}

		return repairItemStack;
	}

}
