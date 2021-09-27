package com.github.elenterius.biomancy.util;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.PotionItem;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionUtils;
import net.minecraft.potion.Potions;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.List;

public final class PotionUtilExt extends PotionUtils {

	public static final String NBT_KEY_POTION = "Potion";
	public static final String NBT_KEY_POTION_ITEM = "PotionItem";
	public static final String NBT_KEY_POTION_NAME = "PotionName";
	public static final String NBT_KEY_CUSTOM_POTION_EFFECTS = "CustomPotionEffects";
	public static final String NBT_KEY_CUSTOM_POTION_COLOR = "CustomPotionColor";

	private PotionUtilExt() {}

	public static ItemStack getPotionItemStack(Potion potion) {
		return PotionUtils.setPotion(new ItemStack(Items.POTION), potion);
	}

	public static ItemStack getPotionItemStack(Potion potion, Collection<EffectInstance> customEffects) {
		ItemStack stack = new ItemStack(Items.POTION);
		PotionUtils.setPotion(stack, potion);
		PotionUtils.setCustomEffects(stack, customEffects);
		return stack;
	}

	public static ItemStack getPotionItemStack(ItemStack stackIn) {
		Potion potion = PotionUtils.getPotion(stackIn);
		if (potion != Potions.EMPTY) {
			List<EffectInstance> effects = PotionUtils.getCustomEffects(stackIn);
			Item potionItem = ForgeRegistries.ITEMS.getValue(ResourceLocation.tryParse(stackIn.getOrCreateTag().getString(NBT_KEY_POTION_ITEM)));
			ItemStack stack = new ItemStack(potionItem != Items.AIR ? potionItem : Items.POTION);
			PotionUtils.setPotion(stack, potion);
			PotionUtils.setCustomEffects(stack, effects);
			return stack;
		}
		return ItemStack.EMPTY;
	}

	public static ItemStack setPotionOfHost(ItemStack hostStack, Potion potion, @Nullable Collection<EffectInstance> customEffects) {
		if (!hostStack.isEmpty()) {
			PotionUtils.setPotion(hostStack, potion);
			if (customEffects != null) PotionUtils.setCustomEffects(hostStack, customEffects);
			hostStack.getOrCreateTag().putString(NBT_KEY_POTION_NAME, potion.getName(Items.POTION.getDescriptionId() + ".effect."));
		}
		return hostStack;
	}

	public static ItemStack setPotionOfHost(ItemStack hostStack, ItemStack potionStack) {
		if (!hostStack.isEmpty() && !potionStack.isEmpty() && potionStack.getItem() instanceof PotionItem) {
			Potion potion = PotionUtils.getPotion(potionStack);
			List<EffectInstance> effects = PotionUtils.getCustomEffects(potionStack);
			PotionUtils.setPotion(hostStack, potion);
			PotionUtils.setCustomEffects(hostStack, effects);
			ResourceLocation registryKey = ForgeRegistries.ITEMS.getKey(potionStack.getItem());
			if (registryKey != null) hostStack.getOrCreateTag().putString(NBT_KEY_POTION_ITEM, registryKey.toString());
			hostStack.getOrCreateTag().putString(NBT_KEY_POTION_NAME, potionStack.getDescriptionId());
		}
		return hostStack;
	}

	public static void removePotionFromHost(ItemStack stack) {
		stack.removeTagKey(NBT_KEY_POTION);
		stack.removeTagKey(NBT_KEY_POTION_ITEM);
		stack.removeTagKey(NBT_KEY_POTION_NAME);
	}

	public static String getPotionTranslationKeyFromHost(ItemStack stack) {
		return stack.getOrCreateTag().getString(NBT_KEY_POTION_NAME);
	}

	public static int getPotionColor(ItemStack stack) {
		Potion potion = PotionUtils.getPotion(stack);
		if (potion != Potions.EMPTY) {
			return PotionUtils.getColor(PotionUtils.getMobEffects(stack));
		}
		return -1;
	}

	public static int getMergedColor(Potion potion, Collection<EffectInstance> effects) {
		return PotionUtils.getColor(PotionUtils.getAllEffects(potion, effects));
	}

	public static boolean hasCustomColor(CompoundNBT nbt) {
		return nbt.contains(NBT_KEY_CUSTOM_POTION_COLOR, Constants.NBT.TAG_ANY_NUMERIC);
	}

	public static int readCustomColor(CompoundNBT nbt) {
		return nbt.getInt(NBT_KEY_CUSTOM_POTION_COLOR);
	}

	public static Potion readPotion(CompoundNBT nbt) {
		if (nbt.contains(NBT_KEY_POTION, Constants.NBT.TAG_STRING)) {
			return PotionUtils.getPotion(nbt);
		}
		return Potions.EMPTY;
	}

	public static void writePotion(CompoundNBT nbt, @Nullable Potion potion) {
		if (potion != Potions.EMPTY && potion != null) {
			nbt.putString(NBT_KEY_POTION, Registry.POTION.getKey(potion).toString());
		}
		else {
			nbt.remove(NBT_KEY_POTION);
		}
	}

	public static void writeCustomEffects(CompoundNBT nbt, Collection<EffectInstance> customEffects) {
		if (!customEffects.isEmpty()) {
			ListNBT list = new ListNBT();
			for (EffectInstance effect : customEffects) {
				list.add(effect.save(new CompoundNBT()));
			}
			nbt.put(NBT_KEY_CUSTOM_POTION_EFFECTS, list);
		}
	}

}
