package com.github.elenterius.biomancy.item;

import com.github.elenterius.biomancy.api.serum.ISerum;
import net.minecraft.world.item.ItemStack;

public interface ISerumProvider {

	ISerum getSerum(ItemStack stack);

	default int getSerumColor(ItemStack stack) {
		return getSerum(stack).getColor();
	}

}
