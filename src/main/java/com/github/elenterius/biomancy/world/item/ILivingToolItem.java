package com.github.elenterius.biomancy.world.item;

import com.github.elenterius.biomancy.client.util.ClientTextUtil;
import com.github.elenterius.biomancy.styles.TextStyles;
import com.github.elenterius.biomancy.styles.TooltipHacks;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.ToolAction;

import java.text.DecimalFormat;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public interface ILivingToolItem extends INutrientsContainerItem {
	default LivingToolState getLivingToolState(ItemStack livingTool) {
		return LivingToolState.deserialize(livingTool.getOrCreateTag());
	}

	default void setLivingToolState(ItemStack livingTool, LivingToolState state) {
		state.serialize(livingTool.getOrCreateTag());
	}

	default int getLivingToolActionCost(ItemStack livingTool, ToolAction toolAction) {
		LivingToolState state = getLivingToolState(livingTool);
		return getLivingToolActionCost(livingTool, state, toolAction);
	}

	int getLivingToolActionCost(ItemStack livingTool, LivingToolState state, ToolAction toolAction);

	default int getLivingToolMaxActionCost(ItemStack livingTool, LivingToolState state) {
		return getLivingToolActions(livingTool).stream()
				.map(toolAction -> getLivingToolActionCost(livingTool, state, toolAction))
				.max(Integer::compareTo)
				.orElse(0);
	}

	default Set<ToolAction> getLivingToolActions(ItemStack livingTool) {
		return ToolAction.getActions().stream().filter(livingTool::canPerformAction).collect(Collectors.toSet());
	}

	@Override
	default void onNutrientsChanged(ItemStack livingTool, int oldValue, int newValue) {
		LivingToolState prevState = getLivingToolState(livingTool);
		LivingToolState state = prevState;

		if (newValue <= 0) {
			if (state != LivingToolState.DORMANT) setLivingToolState(livingTool, LivingToolState.DORMANT);
			return;
		}

		if (state == LivingToolState.DORMANT) {
			state = LivingToolState.AWAKE;
		}

		int maxCost = getLivingToolMaxActionCost(livingTool, state);

		if (newValue < maxCost) {
			if (state == LivingToolState.EXALTED) state = LivingToolState.AWAKE;
			else if (state == LivingToolState.AWAKE) state = LivingToolState.DORMANT;
		}

		if (state != prevState) setLivingToolState(livingTool, state);
	}

	default boolean handleOverrideStackedOnOther(ItemStack livingTool, Slot slot, ClickAction action, Player player) {
		if (livingTool.getCount() > 1) return false;
		if (action != ClickAction.SECONDARY) return false;

		if (!slot.getItem().isEmpty()) {
			ItemStack potentialFood = slot.safeTake(1, 1, player);
			ItemStack remainder = insertNutrients(livingTool, potentialFood);
			if (remainder.getCount() != potentialFood.getCount()) {
				slot.safeInsert(remainder);
				return true;
			}
		}

		return false;
	}

	default boolean handleOverrideOtherStackedOnMe(ItemStack livingTool, ItemStack potentialFood, Slot slot, ClickAction action, Player player, SlotAccess access) {
		if (livingTool.getCount() > 1) return false;
		if (action != ClickAction.SECONDARY || !slot.allowModification(player)) return false;

		if (!potentialFood.isEmpty()) {
			ItemStack remainder = insertNutrients(livingTool, potentialFood);
			int insertedAmount = potentialFood.getCount() - remainder.getCount();
			if (insertedAmount > 0) {
				potentialFood.shrink(insertedAmount);
				return true;
			}
		}

		return false;
	}

	default void appendLivingToolTooltip(ItemStack stack, List<Component> tooltip) {
		tooltip.add(getLivingToolState(stack).getItemTooltip().withStyle(TextStyles.ITALIC_GRAY));

		tooltip.add(TooltipHacks.EMPTY_LINE_COMPONENT);

		DecimalFormat df = ClientTextUtil.getDecimalFormatter("#,###,###");
		tooltip.add(new TranslatableComponent("tooltip.biomancy.nutrients_fuel").withStyle(ChatFormatting.GRAY));
		tooltip.add(new TextComponent("%s/%s u".formatted(df.format(getNutrients(stack)), df.format(getMaxNutrients(stack)))).withStyle(TextStyles.NUTRIENTS));

		tooltip.add(TooltipHacks.EMPTY_LINE_COMPONENT);

		tooltip.add(new TranslatableComponent("tooltip.biomancy.consumption").withStyle(ChatFormatting.GRAY));
		for (ToolAction toolAction : getLivingToolActions(stack)) {
			int actionCost = getLivingToolActionCost(stack, toolAction);
			String text = "%s:  %s u".formatted(toolAction.name(), df.format(actionCost));
			tooltip.add(new TextComponent(text).withStyle(TextStyles.NUTRIENTS_CONSUMPTION));
		}
	}

}
