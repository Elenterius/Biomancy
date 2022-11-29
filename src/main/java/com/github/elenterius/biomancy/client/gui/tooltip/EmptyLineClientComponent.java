package com.github.elenterius.biomancy.client.gui.tooltip;

import com.github.elenterius.biomancy.tooltip.EmptyLineTooltipComponent;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTextTooltip;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;

public class EmptyLineClientComponent implements ClientTooltipComponent {

	public EmptyLineClientComponent(EmptyLineTooltipComponent component) {}

	/**
	 * @return same height as {@link ClientTextTooltip#getHeight()}
	 */
	@Override
	public int getHeight() {
		return 10;
	}

	@Override
	public int getWidth(Font font) {
		return 1;
	}

}
