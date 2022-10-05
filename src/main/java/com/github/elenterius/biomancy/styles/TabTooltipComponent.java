package com.github.elenterius.biomancy.styles;

import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.inventory.tooltip.TooltipComponent;

public class TabTooltipComponent implements TooltipComponent {

	private final FormattedCharSequence text;

	public TabTooltipComponent(FormattedCharSequence text) {
		this.text = text;
	}

	public FormattedCharSequence getText() {
		return text;
	}

}
