package com.github.elenterius.biomancy.item;

import com.github.elenterius.biomancy.chat.ComponentUtil;
import com.github.elenterius.biomancy.client.util.ClientTextUtil;
import com.github.elenterius.biomancy.init.ModSoundEvents;
import com.github.elenterius.biomancy.item.state.LivingToolState;
import com.github.elenterius.biomancy.styles.TextStyles;
import com.github.elenterius.biomancy.util.SoundUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraftforge.common.ToolAction;

import java.text.DecimalFormat;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public interface ILivingToolItem extends INutrientsContainerItem {
	Set<Enchantment> INVALID_ENCHANTMENTS = Set.of(Enchantments.FLAMING_ARROWS, Enchantments.FIRE_ASPECT);

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

	default void updateLivingToolState(ItemStack livingTool, ServerLevel level, Player player) {
		LivingToolState state = getLivingToolState(livingTool);
		boolean hasNutrients = hasNutrients(livingTool);

		if (state == LivingToolState.AWAKE) {
			setLivingToolState(livingTool, hasNutrients ? LivingToolState.EXALTED : LivingToolState.DORMANT);
			SoundUtil.broadcastItemSound(level, player, ModSoundEvents.FLESH_BLOCK_PLACE.get());
		}
		else if (state == LivingToolState.EXALTED) {
			setLivingToolState(livingTool, hasNutrients ? LivingToolState.AWAKE : LivingToolState.DORMANT);
			SoundUtil.broadcastItemSound(level, player, ModSoundEvents.FLESH_BLOCK_HIT.get());
		}
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
	default int getNutrientFuelValue(ItemStack container, ItemStack food) {
		return INutrientsContainerItem.super.getNutrientFuelValue(container, food) * 2;
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
		if (action != ClickAction.SECONDARY || !slot.allowModification(player)) return false;

		if (!slot.getItem().isEmpty()) {
			ItemStack potentialFood = slot.getItem();
			ItemStack remainder = insertNutrients(livingTool, potentialFood);
			int insertedAmount = potentialFood.getCount() - remainder.getCount();
			if (insertedAmount > 0) {
				return !slot.safeTake(insertedAmount, insertedAmount, player).isEmpty();
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

	default boolean isValidEnchantment(ItemStack livingTool, Enchantment enchantment) {
		return !INVALID_ENCHANTMENTS.contains(enchantment);
	}

	default void appendLivingToolTooltip(ItemStack stack, List<Component> tooltip) {
		tooltip.add(getLivingToolState(stack).getTooltip().withStyle(TextStyles.ITALIC_GRAY));

		tooltip.add(ComponentUtil.emptyLine());

		DecimalFormat df = ClientTextUtil.getDecimalFormatter("#,###,###");
		tooltip.add(ComponentUtil.translatable("tooltip.biomancy.nutrients_fuel").withStyle(ChatFormatting.GRAY));
		tooltip.add(ComponentUtil.literal("%s/%s u".formatted(df.format(getNutrients(stack)), df.format(getMaxNutrients(stack)))).withStyle(TextStyles.NUTRIENTS));

		//		tooltip.add(ComponentFacade.emptyLine());
		//
		//		tooltip.add(ComponentFacade.translatable("tooltip.biomancy.consumption").withStyle(ChatFormatting.GRAY));
		//		for (ToolAction toolAction : getLivingToolActions(stack)) {
		//			int actionCost = getLivingToolActionCost(stack, toolAction);
		//			String text = "%s:  %s u".formatted(toolAction.name(), df.format(actionCost));
		//			tooltip.add(ComponentFacade.literal(text).withStyle(TextStyles.NUTRIENTS_CONSUMPTION));
		//		}
	}

}
