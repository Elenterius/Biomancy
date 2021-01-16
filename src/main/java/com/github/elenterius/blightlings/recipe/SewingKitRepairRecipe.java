package com.github.elenterius.blightlings.recipe;

import com.github.elenterius.blightlings.init.ModRecipes;
import com.github.elenterius.blightlings.item.SewingKitItem;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.SpecialRecipe;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class SewingKitRepairRecipe extends SpecialRecipe {

	private final int maxRepairCount = 3; // is the amount of string used to craft the original sewing kit

	public SewingKitRepairRecipe(ResourceLocation id) {
		super(id);
	}

	@Override
	public boolean matches(CraftingInventory inv, World worldIn) {
		ItemStack sewingKitStack = ItemStack.EMPTY;
		int repairCount = 0, damagedSewingKit = 0;

		//find sewing kit first
		for (int i = 0; i < inv.getSizeInventory(); i++) {
			ItemStack stack = inv.getStackInSlot(i);
			if (!stack.isEmpty() && stack.getItem() instanceof SewingKitItem) {
				if (stack.getDamage() == 0 || ++damagedSewingKit > 1) return false;
				sewingKitStack = stack;
			}
		}

		//find valid repair material
		if (sewingKitStack.isEmpty() || !(sewingKitStack.getItem() instanceof SewingKitItem)) return false;
		SewingKitItem item = (SewingKitItem) sewingKitStack.getItem();
		for (int i = 0; i < inv.getSizeInventory(); i++) {
			ItemStack stack = inv.getStackInSlot(i);
			if (!stack.isEmpty() && !(stack.getItem() instanceof SewingKitItem)) {
				if (!item.isRepairableWith(sewingKitStack, stack) || ++repairCount > maxRepairCount) return false;
			}
		}

		return damagedSewingKit == 1 && repairCount > 0;
	}

	@Override
	public NonNullList<ItemStack> getRemainingItems(CraftingInventory inv) {
		NonNullList<ItemStack> list = NonNullList.withSize(inv.getSizeInventory(), ItemStack.EMPTY);

		for (int i = 0; i < list.size(); ++i) {
			ItemStack stack = inv.getStackInSlot(i);
			if (stack.hasContainerItem() && !(stack.getItem() instanceof SewingKitItem)) {
				list.set(i, stack.getContainerItem());
			}
		}

		return list;
	}

	@Override
	public ItemStack getCraftingResult(CraftingInventory inv) {
		ItemStack damagedSewingKit = ItemStack.EMPTY;
		int itemCount = 0;
		for (int i = 0; i < inv.getSizeInventory(); i++) {
			ItemStack stack = inv.getStackInSlot(i);
			if (!stack.isEmpty()) {
				if (stack.getItem() instanceof SewingKitItem) {
					damagedSewingKit = stack;
				}
				else {
					itemCount++;
				}
			}
		}

		if (damagedSewingKit.isEmpty()) return ItemStack.EMPTY;

		if (itemCount > 0) {
			int repairPerItem = MathHelper.floor(damagedSewingKit.getMaxDamage() / (float) maxRepairCount);
			int damage = damagedSewingKit.getDamage() - itemCount * repairPerItem;
			if (damage < -repairPerItem) { // protect player against too much over-repair
				return ItemStack.EMPTY;
			}

			ItemStack stack = damagedSewingKit.copy();
			stack.setDamage(damage);
			return stack;
		}
		else {
			return ItemStack.EMPTY;
		}
	}

	@Override
	public boolean canFit(int width, int height) {
		return width * height >= 2;
	}

	@Override
	public IRecipeSerializer<?> getSerializer() {
		return ModRecipes.REPAIR_SPECIAL_SEWING_KIT.get();
	}
}
