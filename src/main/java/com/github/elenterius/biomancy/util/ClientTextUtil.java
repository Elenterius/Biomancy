package com.github.elenterius.biomancy.util;

import com.github.elenterius.biomancy.init.ClientSetupHandler;
import com.github.elenterius.biomancy.mixin.client.ItemStackMixinAccessor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Util;
import net.minecraft.util.text.*;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.registries.ForgeRegistries;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

@OnlyIn(Dist.CLIENT)
public final class ClientTextUtil extends TextUtil {

	public static final Style LORE_STYLE = Style.EMPTY.setFormatting(TextFormatting.DARK_GRAY).setItalic(true);

	private static final TranslationTextComponent CTRL_KEY_TEXT = new TranslationTextComponent("keyboard.ctrl");
	private static final TranslationTextComponent ALT_KEY_TEXT = new TranslationTextComponent("keyboard.alt");
	private static final ITextComponent FAKE_EMPTY_LINE = new StringTextComponent(" ");

	private static DecimalFormat decimalFormat = null;
	private static String prevPattern = "";
	private static Locale prevLocale = MinecraftForgeClient.getLocale();

	private ClientTextUtil() {super();}

	/**
	 * When the tooltip text is too long it gets wrapped and {@link StringTextComponent#EMPTY} components (empty strings) are eaten up.<br>
	 */
	// Is this a bug or intended vanilla behavior?
	public static ITextComponent EMPTY_LINE_HACK() {
		return Screen.hasControlDown() ? FAKE_EMPTY_LINE : StringTextComponent.EMPTY;
	}

	public static IFormattableTextComponent getItemInfoTooltip(Item item) {
		return Screen.hasControlDown() ? new TranslationTextComponent(Util.makeTranslationKey("tooltip", ForgeRegistries.ITEMS.getKey(item))) : pressButtonTo(CTRL_KEY_TEXT.copyRaw(), "show Info");
	}

	public static boolean showExtraInfo(List<ITextComponent> tooltip) {
		boolean flag = Screen.hasAltDown();
		if (!flag) tooltip.add(pressButtonTo(ALT_KEY_TEXT.copyRaw(), "show Info").mergeStyle(LORE_STYLE));
		return flag;
	}

	public static IFormattableTextComponent pressButtonTo(IFormattableTextComponent key, Object action) {
		return new TranslationTextComponent(TextUtil.getTranslationKey("tooltip", "press_button_to"), key.mergeStyle(TextFormatting.AQUA), action);
	}

	public static IFormattableTextComponent getCtrlKey() {
		return CTRL_KEY_TEXT.copyRaw();
	}

	public static IFormattableTextComponent getDefaultKey() {
		return ClientSetupHandler.ITEM_DEFAULT_KEY_BINDING.func_238171_j_().copyRaw();
	}

	public static int getHideFlags(ItemStack stack) {
		//noinspection ConstantConditions
		return ((ItemStackMixinAccessor) (Object) stack).getHideFlags();
	}

	public static boolean isToolTipVisible(ItemStack stack, ItemStack.TooltipDisplayFlags flags) {
		return ItemStackMixinAccessor.isToolTipVisible(getHideFlags(stack), flags);
	}

	public static void setTooltipVisible(ItemStack stack, ItemStack.TooltipDisplayFlags tooltipDisplay) {
		if (stack.hasTag() && stack.getTag() != null && stack.getTag().contains("HideFlags", Constants.NBT.TAG_ANY_NUMERIC)) {
			stack.getTag().putInt("HideFlags", stack.getTag().getInt("HideFlags") & ~tooltipDisplay.func_242397_a());
		}
	}

	public static String tryToGetPlayerNameOnClientSide(UUID uuid) {
		if (Minecraft.getInstance().world != null) {
			PlayerEntity player = Minecraft.getInstance().world.getPlayerByUuid(uuid);
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
