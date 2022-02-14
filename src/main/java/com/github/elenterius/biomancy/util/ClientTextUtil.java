package com.github.elenterius.biomancy.util;

import com.github.elenterius.biomancy.init.ClientSetupHandler;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.registries.ForgeRegistries;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

@OnlyIn(Dist.CLIENT)
public final class ClientTextUtil {

	public static final Style LORE_STYLE = Style.EMPTY.withColor(ChatFormatting.DARK_GRAY).withItalic(true);

	private static final TranslatableComponent CTRL_KEY_TEXT = new TranslatableComponent("keyboard.biomancy.ctrl");
	private static final TranslatableComponent ALT_KEY_TEXT = new TranslatableComponent("keyboard.biomancy.alt");
	private static final TranslatableComponent SHIFT_KEY_TEXT = new TranslatableComponent("keyboard.biomancy.shift");
	private static final TranslatableComponent RIGHT_MOUSE_KEY_TEXT = new TranslatableComponent("keyboard.biomancy.right_mouse");
	private static final TextComponent FAKE_EMPTY_LINE = new TextComponent(" ");

	private static DecimalFormat decimalFormat = null;
	private static String prevPattern = "";
	private static Locale prevLocale = MinecraftForgeClient.getLocale();

	private ClientTextUtil() {}

	/**
	 * When the tooltip text is too long it gets wrapped and {@link TextComponent#EMPTY} components (empty strings) are discarded.<br>
	 * Is this a bug or intended vanilla behavior?
	 */
	public static Component EMPTY_LINE_HACK() {
		return Screen.hasControlDown() ? FAKE_EMPTY_LINE : TextComponent.EMPTY;
	}

	public static MutableComponent getItemInfoTooltip(Item item) {
		return Screen.hasControlDown() ? new TranslatableComponent(Util.makeDescriptionId("tooltip", ForgeRegistries.ITEMS.getKey(item))).withStyle(LORE_STYLE) : pressButtonTo(CTRL_KEY_TEXT.plainCopy(), "show Info").withStyle(LORE_STYLE);
	}

	public static boolean showExtraInfo(List<Component> tooltip) {
		boolean flag = Screen.hasAltDown();
		if (!flag) tooltip.add(pressButtonTo(ALT_KEY_TEXT.plainCopy(), "show Info").withStyle(LORE_STYLE));
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
