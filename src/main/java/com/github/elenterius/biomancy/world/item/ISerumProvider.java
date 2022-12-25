package com.github.elenterius.biomancy.world.item;

import com.github.elenterius.biomancy.world.serum.Serum;
import net.minecraft.world.item.ItemStack;

public interface ISerumProvider {

	Serum getSerum(ItemStack stack);

	default int getSerumColor(ItemStack stack) {
		return getSerum(stack).getColor();
	}

}
