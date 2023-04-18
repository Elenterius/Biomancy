package com.github.elenterius.biomancy.tooltip;

import com.github.elenterius.biomancy.inventory.itemhandler.EnhancedItemHandler;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import org.jetbrains.annotations.Nullable;

public class StorageSacTooltipComponent implements TooltipComponent {

	@Nullable
	private final EnhancedItemHandler itemHandler;

	public StorageSacTooltipComponent(@Nullable EnhancedItemHandler itemHandler) {
		this.itemHandler = itemHandler;
	}

	@Nullable
	public EnhancedItemHandler getItemHandler() {
		return itemHandler;
	}

}
