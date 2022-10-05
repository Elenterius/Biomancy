package com.github.elenterius.biomancy.styles;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Style;

public final class TextStyles {

	public static final Style MAYKR_RUNES_GRAY = Style.EMPTY.withFont(Fonts.MAYKR_RUNES).withColor(ChatFormatting.GRAY);

	public static final Style LORE = Style.EMPTY.withColor(ChatFormatting.DARK_GRAY).withItalic(true);
	public static final Style ERROR = Style.EMPTY.withColor(ColorStyles.TEXT_ERROR);

	private TextStyles() {}

}
