package com.github.elenterius.biomancy.init;

import com.github.elenterius.biomancy.BiomancyMod;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;

public final class ModRarities {

	public static final Rarity COMMON = createRarity("common", 0xA58369);
	public static final Rarity UNCOMMON = createRarity("uncommon", 0xB19748);
	public static final Rarity RARE = createRarity("rare", 0x2E9E3E);
	public static final Rarity VERY_RARE = createRarity("very_rare", 0xA870E1);
	public static final Rarity ULTRA_RARE = createRarity("ultra_rare", 0xFF3D51);

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
