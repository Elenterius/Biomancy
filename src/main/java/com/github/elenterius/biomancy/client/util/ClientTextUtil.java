package com.github.elenterius.biomancy.client.util;

import com.github.elenterius.biomancy.init.client.ClientSetupHandler;
import com.github.elenterius.biomancy.styles.TextComponentUtil;
import com.github.elenterius.biomancy.styles.TextStyles;
import com.github.elenterius.biomancy.world.item.IBiomancyItem;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.MinecraftForgeClient;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.List;
import java.util.Locale;
import java.util.UUID;


public final class ClientTextUtil {

	private static final TranslatableComponent CTRL_KEY_TEXT = new TranslatableComponent("keyboard.biomancy.ctrl");
	private static final TranslatableComponent ALT_KEY_TEXT = new TranslatableComponent("keyboard.biomancy.alt");
	private static final TranslatableComponent SHIFT_KEY_TEXT = new TranslatableComponent("keyboard.biomancy.shift");
	private static final TranslatableComponent RIGHT_MOUSE_KEY_TEXT = new TranslatableComponent("keyboard.biomancy.right_mouse");

	private static DecimalFormat decimalFormat = null;
	private static String prevPattern = "";
	private static Locale prevLocale = MinecraftForgeClient.getLocale();

	private ClientTextUtil() {}

	public static void appendItemInfoTooltip(Item item, List<Component> tooltip) {
		tooltip.add(getItemInfoTooltip(item));
	}

	private static MutableComponent getItemTooltip(ItemStack stack) {
		Item item = stack.getItem();

		if (item instanceof IBiomancyItem biomancyItem) {
			return biomancyItem.getTooltip(stack);
		}

		return TextComponentUtil.getItemTooltip(item);
	}

	public static MutableComponent getItemInfoTooltip(ItemStack stack) {
		return Screen.hasControlDown() ? getItemTooltip(stack).withStyle(TextStyles.LORE) : pressButtonTo(CTRL_KEY_TEXT.plainCopy(), "show Info").withStyle(TextStyles.LORE);
	}

	@Deprecated
	public static MutableComponent getItemInfoTooltip(Item item) {
		return Screen.hasControlDown() ? TextComponentUtil.getItemTooltip(item).withStyle(TextStyles.LORE) : pressButtonTo(CTRL_KEY_TEXT.plainCopy(), "show Info").withStyle(TextStyles.LORE);
	}

	public static boolean showExtraInfo(List<Component> tooltip) {
		boolean flag = Screen.hasAltDown();
		if (!flag) tooltip.add(pressButtonTo(ALT_KEY_TEXT.plainCopy(), "show Info").withStyle(TextStyles.LORE));
		return flag;
	}

	public static MutableComponent pressButtonTo(MutableComponent key, Object action) {
		return new TranslatableComponent(TextComponentUtil.getTranslationKey("tooltip", "press_button_to"), key.withStyle(ChatFormatting.AQUA), action);
	}

	public static MutableComponent getAltKey() {
		return ALT_KEY_TEXT.plainCopy();
	}

	public static MutableComponent getCtrlKey() {
		return CTRL_KEY_TEXT.plainCopy();
	}

	public static MutableComponent getShiftKey() {
		return SHIFT_KEY_TEXT.plainCopy();
	}

	public static MutableComponent getRightMouseKey() {
		return RIGHT_MOUSE_KEY_TEXT.plainCopy();
	}

	public static MutableComponent getDefaultKey() {
		return ClientSetupHandler.ITEM_DEFAULT_KEY_BINDING.getTranslatedKeyMessage().plainCopy();
	}

	public static String tryToGetPlayerNameOnClientSide(UUID uuid) {
		if (Minecraft.getInstance().level != null) {
			Player player = Minecraft.getInstance().level.getPlayerByUUID(uuid);
			if (player != null) {
				return player.getGameProfile().getName();
			}
		}
		return uuid.toString();
	}

	public static String format(String format, Object... objects) {
		return String.format(MinecraftForgeClient.getLocale(), format, objects);
	}

	private static void setDFPattern(String pattern) {
		Locale locale = MinecraftForgeClient.getLocale();
		if (decimalFormat == null || !pattern.equals(prevPattern) || !locale.equals(prevLocale)) {
			decimalFormat = new DecimalFormat(pattern, DecimalFormatSymbols.getInstance(locale));
			prevPattern = pattern;
			prevLocale = locale;
		}
	}

	public static String formatNumber(String pattern, double value) {
		setDFPattern(pattern);
		return decimalFormat.format(value);
	}

	public static DecimalFormat getDecimalFormatter(String pattern) {
		setDFPattern(pattern);
		return decimalFormat;
	}
}
