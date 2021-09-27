package com.github.elenterius.biomancy.recipe;

import com.github.elenterius.biomancy.init.ModItems;
import com.github.elenterius.biomancy.init.ModRecipes;
import com.github.elenterius.biomancy.item.weapon.shootable.BoomlingHiveGunItem;
import com.github.elenterius.biomancy.util.PotionUtilExt;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.PotionItem;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.SpecialRecipe;
import net.minecraft.potion.Potion;
import net.minecraft.potion.Potions;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class AddPotionToBoomlingGunRecipe extends SpecialRecipe {

	public AddPotionToBoomlingGunRecipe(ResourceLocation id) {
		super(id);
	}

	@Override
	public boolean matches(CraftingInventory inv, World worldIn) {
		int potions = 0;
		int gun = 0;
		Potion storedPotion = Potions.EMPTY;
		Potion otherPotion = Potions.EMPTY;

		for (int i = 0; i < inv.getContainerSize(); i++) {
			ItemStack stack = inv.getItem(i);
			if (!stack.isEmpty()) {
				if (stack.getItem() instanceof BoomlingHiveGunItem) {
					storedPotion = PotionUtilExt.getPotion(stack);
					if (((BoomlingHiveGunItem) stack.getItem()).getPotionCount(stack) >= BoomlingHiveGunItem.MAX_POTION_COUNT) return false;
					if (++gun > 1) return false;
				}
				else if (!(stack.getItem() instanceof PotionItem)) {
					return false;
				}
				else {
					if (++potions > 1) return false;
					otherPotion = PotionUtilExt.getPotion(stack);
				}
			}
		}

		if (gun == 1 && potions == 1) {
			return storedPotion == Potions.EMPTY || storedPotion == otherPotion;
		}

		return gun == 1;
	}

	@Override
	public ItemStack assemble(CraftingInventory inv) {
		ItemStack gunStack = ItemStack.EMPTY;
		ItemStack potionStack = ItemStack.EMPTY;
		for (int i = 0; i < inv.getContainerSize(); i++) {
			ItemStack stack = inv.getItem(i);
			if (!stack.isEmpty()) {
				if (stack.getItem() instanceof BoomlingHiveGunItem) gunStack = stack;
				else potionStack = stack;
			}
		}

		if (!potionStack.isEmpty()) {
			ItemStack stack = gunStack.copy();
			ItemStack storedPotionStack = PotionUtilExt.getPotionItemStack(stack);
			if (storedPotionStack.isEmpty()) {
				PotionUtilExt.setPotionOfHost(stack, potionStack);
				ModItems.BOOMLING_HIVE_GUN.get().setPotionCount(stack, 1);
			}
			else if (ItemStack.tagMatches(storedPotionStack, potionStack)) {
				PotionUtilExt.setPotionOfHost(stack, potionStack);
				ModItems.BOOMLING_HIVE_GUN.get().growPotionCount(stack, 1);
			}
			return stack;
		}
		else {
			ItemStack stack = gunStack.copy();
			PotionUtilExt.removePotionFromHost(stack);
			ModItems.BOOMLING_HIVE_GUN.get().setPotionCount(stack, 0);
			return stack;
		}
	}

	@Override
	public boolean canCraftInDimensions(int width, int height) {
		return width * height >= 2;
	}

	@Override
	public IRecipeSerializer<?> getSerializer() {
		return ModRecipes.CRAFTING_SPECIAL_BOOMLING_GUN.get();
	}

}
