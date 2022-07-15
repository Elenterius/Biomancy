package com.github.elenterius.biomancy.util;

import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.PotionItem;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
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

	public static ItemStack getPotionItemStack(Potion potion, Collection<MobEffectInstance> customEffects) {
		ItemStack stack = new ItemStack(Items.POTION);
		PotionUtils.setPotion(stack, potion);
		PotionUtils.setCustomEffects(stack, customEffects);
		return stack;
	}

	public static ItemStack getPotionItemStack(ItemStack stackIn) {
		Potion potion = PotionUtils.getPotion(stackIn);
		if (potion != Potions.EMPTY) {
			List<MobEffectInstance> effects = PotionUtils.getCustomEffects(stackIn);
			Item potionItem = ForgeRegistries.ITEMS.getValue(ResourceLocation.tryParse(stackIn.getOrCreateTag().getString(NBT_KEY_POTION_ITEM)));
			ItemStack stack = new ItemStack(potionItem != Items.AIR ? potionItem : Items.POTION);
			PotionUtils.setPotion(stack, potion);
			PotionUtils.setCustomEffects(stack, effects);
			return stack;
		}
		return ItemStack.EMPTY;
	}

	public static ItemStack setPotionOfHost(ItemStack hostStack, Potion potion, @Nullable Collection<MobEffectInstance> customEffects) {
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
			List<MobEffectInstance> effects = PotionUtils.getCustomEffects(potionStack);
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

	public static int getMergedColor(Potion potion, Collection<MobEffectInstance> effects) {
		return PotionUtils.getColor(PotionUtils.getAllEffects(potion, effects));
	}

	public static boolean hasCustomColor(CompoundTag nbt) {
		return nbt.contains(NBT_KEY_CUSTOM_POTION_COLOR, Tag.TAG_ANY_NUMERIC);
	}

	public static int readCustomColor(CompoundTag nbt) {
		return nbt.getInt(NBT_KEY_CUSTOM_POTION_COLOR);
	}

	public static Potion readPotion(CompoundTag nbt) {
		if (nbt.contains(NBT_KEY_POTION, Tag.TAG_STRING)) {
			return PotionUtils.getPotion(nbt);
		}
		return Potions.EMPTY;
	}

	public static void writePotion(CompoundTag nbt, @Nullable Potion potion) {
		if (potion != Potions.EMPTY && potion != null) {
			nbt.putString(NBT_KEY_POTION, Registry.POTION.getKey(potion).toString());
		}
		else {
			nbt.remove(NBT_KEY_POTION);
		}
	}

	public static void writeCustomEffects(CompoundTag nbt, Collection<MobEffectInstance> customEffects) {
		if (!customEffects.isEmpty()) {
			ListTag list = new ListTag();
			for (MobEffectInstance effect : customEffects) {
				list.add(effect.save(new CompoundTag()));
			}
			nbt.put(NBT_KEY_CUSTOM_POTION_EFFECTS, list);
		}
	}

}
