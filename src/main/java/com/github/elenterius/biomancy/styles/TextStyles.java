package com.github.elenterius.biomancy.styles;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Style;

public final class TextStyles {

	public static final Style MAYKR_RUNES_GRAY = Style.EMPTY.withFont(Fonts.MAYKR_RUNES).withColor(ChatFormatting.GRAY);
	public static final Style MAYKR_RUNES_RED = Style.EMPTY.withFont(Fonts.MAYKR_RUNES).withColor(0xff_bb0b3f);

	public static final Style LORE = Style.EMPTY.withColor(ChatFormatting.DARK_GRAY).withItalic(true);
	public static final Style ITALIC_GRAY = Style.EMPTY.withColor(ChatFormatting.GRAY).withItalic(true);
	public static final Style ERROR = Style.EMPTY.withColor(ColorStyles.TEXT_ERROR);
	public static final Style KEYBOARD_INPUT = Style.EMPTY.withColor(ColorStyles.TEXT_MUTED_AQUA);

	public static final Style NUTRIENTS = Style.EMPTY.withColor(ColorStyles.TEXT_NUTRIENTS);
	public static final Style NUTRIENTS_CONSUMPTION = Style.EMPTY.withColor(ColorStyles.TEXT_NUTRIENTS_CONSUMPTION);

	private TextStyles() {}

}
