package com.github.elenterius.biomancy.tooltip;

import com.github.elenterius.biomancy.inventory.ItemHandlerWrapper;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import org.jetbrains.annotations.Nullable;

public class StorageSacTooltipComponent implements TooltipComponent {

	@Nullable
	private final ItemHandlerWrapper itemHandler;

	public StorageSacTooltipComponent(@Nullable ItemHandlerWrapper itemHandler) {
		this.itemHandler = itemHandler;
	}

	@Nullable
	public ItemHandlerWrapper getItemHandler() {
		return itemHandler;
	}

}
