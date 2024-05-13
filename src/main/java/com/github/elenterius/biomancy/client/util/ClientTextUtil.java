package com.github.elenterius.biomancy.client.util;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.init.client.ClientSetupHandler;
import com.github.elenterius.biomancy.item.ItemTooltipStyleProvider;
import com.github.elenterius.biomancy.styles.TextComponentUtil;
import com.github.elenterius.biomancy.styles.TextStyles;
import com.github.elenterius.biomancy.util.ComponentUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.List;
import java.util.Locale;
import java.util.UUID;


public final class ClientTextUtil {

	private static final MutableComponent CTRL_KEY_TEXT = ComponentUtil.translatable("keyboard." + BiomancyMod.MOD_ID + ".ctrl");
	private static final MutableComponent ALT_KEY_TEXT = ComponentUtil.translatable("keyboard." + BiomancyMod.MOD_ID + ".alt");
	private static final MutableComponent SHIFT_KEY_TEXT = ComponentUtil.translatable("keyboard." + BiomancyMod.MOD_ID + ".shift");
	private static final MutableComponent RIGHT_MOUSE_KEY_TEXT = ComponentUtil.translatable("keyboard." + BiomancyMod.MOD_ID + ".right_mouse");
	private static final MutableComponent SHOW_INFO = ComponentUtil.translatable("tooltip." + BiomancyMod.MOD_ID + ".action.show_info");

	private static DecimalFormat decimalFormat = null;
	private static String prevPattern = "";
	private static Locale prevLocale = null;

	private ClientTextUtil() {}

	private static MutableComponent getItemTooltip(ItemStack stack) {
		Item item = stack.getItem();

		if (item instanceof ItemTooltipStyleProvider iTooltip) {
			return iTooltip.getTooltipText(stack);
		}

		return TextComponentUtil.getItemTooltip(item);
	}

	public static List<Component> getItemInfoTooltip(ItemStack stack) {
		if (Screen.hasControlDown()) {
			return splitLinesByNewLine(getItemTooltip(stack).withStyle(TextStyles.LORE));
		}

		return List.of(pressButtonTo(CTRL_KEY_TEXT.plainCopy(), SHOW_INFO).withStyle(TextStyles.LORE));
	}

	public static boolean showExtraInfo(List<Component> tooltip) {
		boolean flag = Screen.hasAltDown();
		if (!flag) tooltip.add(pressButtonTo(ALT_KEY_TEXT.plainCopy(), SHOW_INFO).withStyle(TextStyles.LORE));
		return flag;
	}

	public static MutableComponent pressButtonTo(MutableComponent key, Object action) {
		return ComponentUtil.translatable(TextComponentUtil.getTranslationKey("tooltip", "press_button_to"), key.withStyle(TextStyles.KEYBOARD_INPUT), action);
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
		return ComponentUtil.keybind(ClientSetupHandler.ITEM_DEFAULT_KEY_BINDING);
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

	public static List<Component> splitLinesByNewLine(Component component) {
		Locale locale = Minecraft.getInstance().getLocale();
		String text = component.getString();
		Style style = component.getStyle();
		return ComponentUtil.splitLines(locale, text, style);
	}

	public static List<Component> splitLinesByNewLine(String text, Style style) {
		Locale locale = Minecraft.getInstance().getLocale();
		return ComponentUtil.splitLines(locale, text, style);
	}

	public static List<Component> splitLines(Component component, int maxLength) {
		Locale locale = Minecraft.getInstance().getLocale();
		String text = component.getString();
		Style style = component.getStyle();
		return ComponentUtil.splitLines(locale, text, maxLength, style);
	}

	public static List<Component> splitLines(String text, int maxLength, Style style) {
		Locale locale = Minecraft.getInstance().getLocale();
		return ComponentUtil.splitLines(locale, text, maxLength, style);
	}

	public static String format(String format, Object... objects) {
		return String.format(Minecraft.getInstance().getLocale(), format, objects);
	}

	private static void setDFPattern(String pattern) {
		Locale locale = Minecraft.getInstance().getLocale();
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
