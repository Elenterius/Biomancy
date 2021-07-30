package com.github.elenterius.biomancy.recipe;

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
		Potion potion = Potions.EMPTY;

		for (int i = 0; i < inv.getSizeInventory(); i++) {
			ItemStack stack = inv.getStackInSlot(i);
			if (!stack.isEmpty()) {
				if (stack.getItem() instanceof BoomlingHiveGunItem) {
					potion = PotionUtilExt.getPotionFromItem(stack);
					if (++gun > 1) return false;
				}
				else if (!(stack.getItem() instanceof PotionItem) || ++potions > 1) return false;
			}
		}

		return (gun == 1 && potions == 1 && potion == Potions.EMPTY) || (gun == 1 && potions == 0 && potion != Potions.EMPTY);
	}

	@Override
	public ItemStack getCraftingResult(CraftingInventory inv) {
		ItemStack gunStack = ItemStack.EMPTY;
		ItemStack potionStack = ItemStack.EMPTY;
		for (int i = 0; i < inv.getSizeInventory(); i++) {
			ItemStack stack = inv.getStackInSlot(i);
			if (!stack.isEmpty()) {
				if (stack.getItem() instanceof BoomlingHiveGunItem) gunStack = stack;
				else potionStack = stack;
			}
		}

		if (!potionStack.isEmpty()) {
			ItemStack stack = gunStack.copy();
			PotionUtilExt.setPotionOfHost(stack, potionStack);
			return stack;
		}
		else {
			ItemStack stack = PotionUtilExt.getPotionItemStack(gunStack);
			return !stack.isEmpty() ? stack : PotionUtilExt.getPotionItemStack(Potions.EMPTY);
		}
	}

	@Override
	public boolean canFit(int width, int height) {
		return width * height >= 2;
	}

	@Override
	public IRecipeSerializer<?> getSerializer() {
		return ModRecipes.CRAFTING_SPECIAL_BOOMLING_GUN.get();
	}

}
