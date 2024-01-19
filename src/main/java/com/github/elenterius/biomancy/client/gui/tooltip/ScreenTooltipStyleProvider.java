package com.github.elenterius.biomancy.client.gui.tooltip;

import com.github.elenterius.biomancy.styles.ColorStyles;

public interface ScreenTooltipStyleProvider {

	default ColorStyles.ITooltipStyle getTooltipStyle() {
		return ColorStyles.GENERIC_TOOLTIP;
	}

}
