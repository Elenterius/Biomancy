package com.github.elenterius.biomancy.world.item;

import com.github.elenterius.biomancy.styles.TextComponentUtil;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.item.ItemStack;

public interface IBiomancyItem {

	default String getTooltipKey(ItemStack stack) {
		return TextComponentUtil.getItemTooltipKey(stack.getItem());
	}

	default TranslatableComponent getTooltip(ItemStack stack) {
		return new TranslatableComponent(getTooltipKey(stack));
	}

}
