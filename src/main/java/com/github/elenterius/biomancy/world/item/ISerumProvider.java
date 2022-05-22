package com.github.elenterius.biomancy.world.item;

import com.github.elenterius.biomancy.world.serum.Serum;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;

public interface ISerumProvider {

	@Nullable
	Serum getSerum(ItemStack stack);

	default int getSerumColor(ItemStack stack) {
		Serum serum = getSerum(stack);
		return serum != null ? serum.getColor() : -1;
	}

}
