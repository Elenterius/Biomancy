package com.github.elenterius.biomancy.world.item;

import com.github.elenterius.biomancy.chat.ComponentUtil;
import com.github.elenterius.biomancy.init.ModRarities;
import com.github.elenterius.biomancy.styles.ColorStyles;
import com.github.elenterius.biomancy.styles.TextComponentUtil;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.ItemStack;

public interface ICustomTooltip {

	default String getTooltipKey(ItemStack stack) {
		return TextComponentUtil.getItemTooltipKey(stack.getItem());
	}

	default MutableComponent getTooltipText(ItemStack stack) {
		return ComponentUtil.translatable(getTooltipKey(stack));
	}

	default int getTooltipColor(ItemStack stack) {
		return ModRarities.getRGBColor(stack);
	}

	default int getTooltipColorWithAlpha(ItemStack stack) {
		return ModRarities.getARGBColor(stack);
	}

	default ColorStyles.ITooltipStyle getTooltipStyle() {
		return ColorStyles.CUSTOM_RARITY_TOOLTIP;
	}

}
