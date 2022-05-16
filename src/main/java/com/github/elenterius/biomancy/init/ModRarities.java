package com.github.elenterius.biomancy.init;

import com.github.elenterius.biomancy.BiomancyMod;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;

import java.util.HashMap;
import java.util.Map;

/**
 * Biomancy exclusive item rarities, the grey ChatFormatting color will be replaced with the rgbColor through the TooltipRenderHandler
 */
public final class ModRarities {

	//EnumMap doesn't work here due to the Rarity enum being internally changed during runtime by forge
	private static final Map<Rarity, Integer> RARITIES = new HashMap<>();

	public static final Rarity COMMON = createRarity("common", 0x5c4432);
	public static final Rarity UNCOMMON = createRarity("uncommon", 0xb3953a);
	public static final Rarity RARE = createRarity("rare", 0x0f701d);
	public static final Rarity VERY_RARE = createRarity("very_rare", 0x6f318b);
	public static final Rarity ULTRA_RARE = createRarity("ultra_rare", 0xbb0b3f);

	private ModRarities() {}

	private static Rarity createRarity(String name, int rgbColor) {
		Rarity rarity = Rarity.create(BiomancyMod.MOD_ID + "_" + name, ChatFormatting.RED);
		RARITIES.put(rarity, rgbColor);
		return rarity;
	}

	public static int getRGBColor(ItemStack stack) {
		return RARITIES.getOrDefault(stack.getRarity(), -1);
	}

	public static Component getHighlightTip(ItemStack stack, Component displayName) {
		int color = getRGBColor(stack);
		if (color > -1) {
			if (displayName instanceof MutableComponent mutableComponent) return mutableComponent.withStyle(Style.EMPTY.withColor(color));
			return new TextComponent("").append(displayName).withStyle(Style.EMPTY.withColor(color));
		}
		return displayName;
	}

}
