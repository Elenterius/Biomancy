package com.github.elenterius.blightlings.item;

import com.github.elenterius.blightlings.init.ModItems;
import com.github.elenterius.blightlings.recipe.SewingKitRepairRecipe;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.Tags;

public class SewingKitItem extends Item {

	public SewingKitItem(Properties properties) {
		super(properties);
	}

	@Override
	public boolean hasContainerItem(ItemStack stack) {
		return true;
	}

	@Override
	public ItemStack getContainerItem(ItemStack stackIn) {
		//this way we don't need to implement custom crafting recipes for all the item crafting that use the sewing kit

		int damage = stackIn.getDamage() + 1;
		if (damage < getMaxDamage(stackIn)) {
			ItemStack stack = new ItemStack(this);
			stack.setDamage(damage);
			return stack;
		}
		else return new ItemStack(ModItems.SEWING_KIT_EMPTY.get());
	}

	/**
	 * we use our own repair recipe implementation
	 *
	 * @see SewingKitRepairRecipe
	 */
	public boolean isRepairableWith(ItemStack damagedStack, ItemStack materialStack) {
		return materialStack.getItem().isIn(Tags.Items.STRING);
	}

	/**
	 * disables workbench repair crafting
	 *
	 * @return false
	 */
	@Override
	public boolean isRepairable(ItemStack stack) {
		return false;
	}

	@Override
	public boolean getIsRepairable(ItemStack toRepair, ItemStack repair) {
		return false; // can't be repaired in an anvil, save the player from wasting exp (its false by default anyways, just for context here)
	}
}
