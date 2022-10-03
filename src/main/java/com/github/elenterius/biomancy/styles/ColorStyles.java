package com.github.elenterius.biomancy.styles;

import com.github.elenterius.biomancy.init.ModRarities;
import net.minecraftforge.client.event.RenderTooltipEvent;

public final class ColorStyles {

	public static final int WHITE_ARGB = 0xFF_FF_FF_FF;
	public static final int TOOLTIP_BACKGROUND_ARGB = 0xFA_2B2121;
	public static final int TEXT_ERROR = 0xC12727;
	public static final int TEXT_SUCCESS = 0x65B52A;
	public static final int TEXT_ACCENT_FORGE = 0xA88773;
	public static final int TEXT_ACCENT_FORGE_DARK = 0x51423A;

	public static final TooltipStyle GENERIC_TOOLTIP = new TooltipStyle(0xFA301020, 0xFF903E55, 0xFF5A233F);
	public static final ITooltipStyle CUSTOM_RARITY_TOOLTIP = tooltipEvent -> {
		int rarityColor = ModRarities.getRGBColor(tooltipEvent.getItemStack());
		if (rarityColor > -1) {
			tooltipEvent.setBackground(TOOLTIP_BACKGROUND_ARGB);
			//add alpha to rgb
			tooltipEvent.setBorderStart(0xFE_000000 | rarityColor); //fake color difference with lower alpha value
			tooltipEvent.setBorderEnd(0xFF_000000 | rarityColor);
		} else {
			//fallback
			GENERIC_TOOLTIP.applyColorTo(tooltipEvent);
		}
	};

	private ColorStyles() {}

	public interface ITooltipStyle {
		void applyColorTo(final RenderTooltipEvent.Color tooltipEvent);
	}

	/**
	 * @param backgroundColor  ARGB
	 * @param borderStartColor ARGB
	 * @param borderEndColor   ARGB
	 */
	public record TooltipStyle(int backgroundColor, int borderStartColor, int borderEndColor) implements ITooltipStyle {
		@Override
		public void applyColorTo(final RenderTooltipEvent.Color tooltipEvent) {
			tooltipEvent.setBackground(backgroundColor);
			tooltipEvent.setBorderStart(borderStartColor);
			tooltipEvent.setBorderEnd(borderEndColor);
		}
	}

}
