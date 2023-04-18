package com.github.elenterius.biomancy.item;

import com.github.elenterius.biomancy.serum.Serum;
import net.minecraft.world.item.ItemStack;

public interface ISerumProvider {

	Serum getSerum(ItemStack stack);

	default int getSerumColor(ItemStack stack) {
		return getSerum(stack).getColor();
	}

}
