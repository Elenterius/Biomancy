package com.github.elenterius.biomancy.api.serum;

import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

@ApiStatus.Experimental
public interface SerumContainer {

	@NotNull
	default Serum getSerum(ItemStack stack) {return Serum.EMPTY;}

}
