package com.github.elenterius.biomancy.util;

import com.github.elenterius.biomancy.BiomancyMod;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;

public abstract class TextUtil {
	protected TextUtil() {}

	public static String getTranslationKey(String prefix, String suffix) {
		return prefix + "." + BiomancyMod.MOD_ID + "." + suffix;
	}

	public static TranslationTextComponent getTranslationText(String prefix, String suffix) {
		return new TranslationTextComponent(getTranslationKey(prefix, suffix));
	}

	public static IFormattableTextComponent getTooltipText(String tooltipKey) {
		return new TranslationTextComponent(getTranslationKey("tooltip", tooltipKey));
	}

	public static IFormattableTextComponent getTooltipText(String tooltipKey, Object... formatArgs) {
		return new TranslationTextComponent(getTranslationKey("tooltip", tooltipKey), formatArgs);
	}

	public static IFormattableTextComponent getMsgText(String msgKey) {
		return new TranslationTextComponent(getTranslationKey("msg", msgKey));
	}

	public static IFormattableTextComponent getMsgText(String msgKey, Object... formatArgs) {
		return new TranslationTextComponent(getTranslationKey("msg", msgKey), formatArgs);
	}

	public static IFormattableTextComponent getFailureMsgText(String msgKey) {
		return getMsgText(msgKey).withStyle(TextFormatting.RED);
	}

	public static IFormattableTextComponent getFailureMsgText(String msgKey, Object... formatArgs) {
		return getMsgText(msgKey, formatArgs).withStyle(TextFormatting.RED);
	}

}
