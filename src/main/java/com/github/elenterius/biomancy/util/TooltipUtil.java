package com.github.elenterius.biomancy.util;

import com.github.elenterius.biomancy.BiomancyMod;
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
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.List;
import java.util.UUID;

public final class TooltipUtil {
	private TooltipUtil() {}

	public static final Style LORE_STYLE = Style.EMPTY.setFormatting(TextFormatting.DARK_GRAY).setItalic(true);

	public static final TranslationTextComponent CTRL_KEY_TEXT = new TranslationTextComponent("keyboard.ctrl");

	public static ITextComponent FAKE_EMPTY_LINE = new StringTextComponent(" ");

	/**
	 * When the tooltip text is too long it gets wrapped and {@link StringTextComponent#EMPTY} components (empty strings) are eaten up.<br>
	 */
	// Is this a bug or intended vanilla behavior?
	@OnlyIn(Dist.CLIENT)
	public static ITextComponent EMPTY_LINE_HACK() {
		return Screen.hasControlDown() ? FAKE_EMPTY_LINE : StringTextComponent.EMPTY;
	}

	@OnlyIn(Dist.CLIENT)
	public static TranslationTextComponent getTooltip(Item item) {
		return Screen.hasControlDown() ? new TranslationTextComponent(Util.makeTranslationKey("tooltip", ForgeRegistries.ITEMS.getKey(item))) :
				new TranslationTextComponent(BiomancyMod.getTranslationKey("tooltip", "press_button_to"), new TranslationTextComponent("keyboard.ctrl").mergeStyle(TextFormatting.AQUA), "show Info");
	}

	@OnlyIn(Dist.CLIENT)
	public static boolean showExtraInfo(List<ITextComponent> tooltip) {
		boolean flag = Screen.hasControlDown();
		if (!flag)
			tooltip.add(new TranslationTextComponent(BiomancyMod.getTranslationKey("tooltip", "press_button_to"), new TranslationTextComponent("keyboard.ctrl").mergeStyle(TextFormatting.AQUA), "show Info"));
		return flag;
	}

	@OnlyIn(Dist.CLIENT)
	public static int getHideFlags(ItemStack stack) {
		//noinspection ConstantConditions
		return ((ItemStackMixinAccessor) (Object) stack).getHideFlags();
	}

	@OnlyIn(Dist.CLIENT)
	public static boolean isToolTipVisible(ItemStack stack, ItemStack.TooltipDisplayFlags flags) {
		return ItemStackMixinAccessor.isToolTipVisible(getHideFlags(stack), flags);
	}

	@OnlyIn(Dist.CLIENT)
	public static void setTooltipVisible(ItemStack stack, ItemStack.TooltipDisplayFlags tooltipDisplay) {
		if (stack.hasTag() && stack.getTag() != null && stack.getTag().contains("HideFlags", Constants.NBT.TAG_ANY_NUMERIC)) {
			stack.getTag().putInt("HideFlags", stack.getTag().getInt("HideFlags") & ~tooltipDisplay.func_242397_a());
		}
	}

	@OnlyIn(Dist.CLIENT)
	public static String tryToGetPlayerNameOnClientSide(UUID uuid) {
		if (Minecraft.getInstance().world != null) {
			PlayerEntity player = Minecraft.getInstance().world.getPlayerByUuid(uuid);
			if (player != null) {
				return player.getGameProfile().getName();
			}
		}
		return uuid.toString();
	}

}
