package com.github.elenterius.blightlings.util;

import com.github.elenterius.blightlings.mixin.ItemStackMixinAccessor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Util;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.registries.ForgeRegistries;

public abstract class TooltipUtil {

	public static final Style LORE_STYLE = Style.EMPTY.setFormatting(TextFormatting.DARK_GRAY).setItalic(true);

	public static TranslationTextComponent getTooltip(Item item) {
		return new TranslationTextComponent(Util.makeTranslationKey("tooltip", ForgeRegistries.ITEMS.getKey(item)));
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
		if (stack.hasTag() && stack.getTag() != null && stack.getTag().contains("HideFlags", 99)) {
			stack.getTag().putInt("HideFlags", stack.getTag().getInt("HideFlags") & ~tooltipDisplay.func_242397_a());
		}
	}
}
