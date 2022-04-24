package com.github.elenterius.biomancy.world.item;

import com.github.elenterius.biomancy.init.ModRarities;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.extensions.IForgeItem;

public interface IBiomancyItem extends IForgeItem {

	@Override
	default Component getHighlightTip(ItemStack stack, Component displayName) {
		return ModRarities.getHighlightTip(stack, displayName);
	}

}
