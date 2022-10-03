package com.github.elenterius.biomancy.init;

import com.github.elenterius.biomancy.BiomancyMod;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;

public final class ModRarities {

	public static final Rarity COMMON = createRarity("common", 0x5c4432);
	public static final Rarity UNCOMMON = createRarity("uncommon", 0xb3953a);
	public static final Rarity RARE = createRarity("rare", 0x0f701d);
	public static final Rarity VERY_RARE = createRarity("very_rare", 0x6f318b);
	public static final Rarity ULTRA_RARE = createRarity("ultra_rare", 0xbb0b3f);

	private ModRarities() {}

	private static Rarity createRarity(String name, int rgbColor) {
		return Rarity.create(BiomancyMod.MOD_ID + "_" + name, style -> style.withColor(rgbColor));
	}

	public static int getRGBColor(ItemStack stack) {
		TextColor color = stack.getRarity().getStyleModifier().apply(Style.EMPTY).getColor();
		return color != null ? color.getValue() : -1;
	}

	public static int getARGBColor(ItemStack stack) {
		return getRGBColor(stack) | 0xFF_000000;
	}

}
