package com.github.elenterius.biomancy.styles;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.util.ComponentUtil;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

public final class TextComponentUtil {

	private TextComponentUtil() {}

	public static String getTranslationKey(String prefix, String suffix) {
		return prefix + "." + BiomancyMod.MOD_ID + "." + suffix;
	}

	public static MutableComponent getTranslationText(String prefix, String suffix) {
		return ComponentUtil.translatable(getTranslationKey(prefix, suffix));
	}

	public static String getItemTooltipKey(Item item) {
		return item.getDescriptionId() + ".tooltip";
	}

	public static String getItemTooltipKey(Block block) {
		return block.getDescriptionId() + ".tooltip";
	}

	public static MutableComponent getItemTooltip(Item item) {
		return ComponentUtil.translatable(getItemTooltipKey(item));
	}

	public static MutableComponent getTooltipText(String tooltipKey) {
		return ComponentUtil.translatable(getTranslationKey("tooltip", tooltipKey));
	}

	public static MutableComponent getTooltipText(String tooltipKey, Object... formatArgs) {
		return ComponentUtil.translatable(getTranslationKey("tooltip", tooltipKey), formatArgs);
	}

	public static MutableComponent getMsgText(String msgKey) {
		return ComponentUtil.translatable(getTranslationKey("msg", msgKey));
	}

	public static MutableComponent getMsgText(String msgKey, Object... formatArgs) {
		return ComponentUtil.translatable(getTranslationKey("msg", msgKey), formatArgs);
	}

	public static MutableComponent getFailureMsgText(String msgKey) {
		return getMsgText(msgKey).withStyle(TextStyles.ERROR);
	}

	public static MutableComponent getFailureMsgText(String msgKey, Object... formatArgs) {
		return getMsgText(msgKey, formatArgs).withStyle(TextStyles.ERROR);
	}

	public static MutableComponent getAbilityText(String key) {
		return ComponentUtil.translatable(getTranslationKey("ability", key));
	}

}
