package com.github.elenterius.biomancy.recipe;

import com.github.elenterius.biomancy.init.ModRecipes;
import com.github.elenterius.biomancy.item.weapon.shootable.ThrowableBoomlingItem;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.*;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.SpecialRecipe;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionUtils;
import net.minecraft.potion.Potions;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.List;

public class PotionBeetleRecipe extends SpecialRecipe {
	public PotionBeetleRecipe(ResourceLocation id) {
		super(id);
	}

	@Override
	public boolean matches(CraftingInventory inv, World worldIn) {
		int potions = 0, beetles = 0;
		Potion potion = Potions.EMPTY;

		for (int i = 0; i < inv.getSizeInventory(); i++) {
			ItemStack stack = inv.getStackInSlot(i);
			if (!stack.isEmpty()) {
				if (stack.getItem() instanceof ThrowableBoomlingItem) {
					potion = PotionUtils.getPotionFromItem(stack);
					if (++beetles > 1) return false;
				}
				else if (!(stack.getItem() instanceof ThrowablePotionItem) || ++potions > 1) return false;
			}
		}

		return (beetles == 1 && potions == 1 && potion == Potions.EMPTY) || (beetles == 1 && potions == 0 && potion != Potions.EMPTY);
	}

	@Override
	public ItemStack getCraftingResult(CraftingInventory inv) {
		ItemStack beetleStack = ItemStack.EMPTY;
		ItemStack potionStack = ItemStack.EMPTY;
		for (int i = 0; i < inv.getSizeInventory(); i++) {
			ItemStack stack = inv.getStackInSlot(i);
			if (!stack.isEmpty()) {
				if (stack.getItem() instanceof ThrowableBoomlingItem) {
					beetleStack = stack;
				}
				else {
					potionStack = stack;
				}
			}
		}

		if (!potionStack.isEmpty()) {
			Potion potion = PotionUtils.getPotionFromItem(potionStack);
			List<EffectInstance> effects = PotionUtils.getFullEffectsFromItem(potionStack);
			ItemStack stack = beetleStack.copy();
			PotionUtils.addPotionToItemStack(stack, potion);
			PotionUtils.appendEffects(stack, effects);
			ResourceLocation registryKey = ForgeRegistries.ITEMS.getKey(potionStack.getItem());
			if (registryKey != null) stack.getOrCreateTag().putString("PotionItem", registryKey.toString());
			stack.getOrCreateTag().putString("PotionName", potionStack.getTranslationKey());
			return stack;
		}
		else {
			Potion potion = PotionUtils.getPotionFromItem(beetleStack);
			if (potion != Potions.EMPTY) {
				List<EffectInstance> effects = PotionUtils.getFullEffectsFromItem(beetleStack);
				Item potionItem = ForgeRegistries.ITEMS.getValue(ResourceLocation.tryCreate(beetleStack.getOrCreateTag().getString("PotionItem")));
				ItemStack stack = new ItemStack(potionItem instanceof PotionItem ? potionItem : Items.POTION);
				PotionUtils.addPotionToItemStack(stack, potion);
				PotionUtils.appendEffects(stack, effects);
				return stack;
			}
			return PotionUtils.addPotionToItemStack(new ItemStack(Items.POTION), Potions.WATER);
		}
	}

	@Override
	public boolean canFit(int width, int height) {
		return width * height >= 2;
	}

	@Override
	public IRecipeSerializer<?> getSerializer() {
		return ModRecipes.CRAFTING_SPECIAL_POTION_BEETLE.get();
	}
}
