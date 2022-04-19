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

	//can't be EnumMap due to Rarity enum implementing IExtensibleEnum
	private static final Map<Rarity, Integer> RARITIES = new HashMap<>();

	public static final Rarity COMMON = createRarity("common", 0xFFFFFF);
	public static final Rarity UNCOMMON = createRarity("uncommon", 0xc2bdb0);
	public static final Rarity EXOTIC = createRarity("exotic", 0x763474);
	public static final Rarity MACHINE = createRarity("machine", 0x65b52a);
	public static final Rarity EXALTED = createRarity("exalted", 0xccff00);

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
