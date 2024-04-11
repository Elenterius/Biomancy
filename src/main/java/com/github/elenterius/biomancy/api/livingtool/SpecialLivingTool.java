package com.github.elenterius.biomancy.api.livingtool;

import com.github.elenterius.biomancy.init.ModSoundEvents;
import com.github.elenterius.biomancy.styles.TextStyles;
import com.github.elenterius.biomancy.util.ComponentUtil;
import com.github.elenterius.biomancy.util.SoundUtil;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.ToolAction;
import org.jetbrains.annotations.ApiStatus;

import java.util.List;

@ApiStatus.Experimental
public interface SpecialLivingTool extends LivingTool {

	default LivingToolState getLivingToolState(ItemStack livingTool) {
		return LivingToolState.deserialize(livingTool.getOrCreateTag());
	}

	default void setLivingToolState(ItemStack livingTool, LivingToolState state) {
		state.serialize(livingTool.getOrCreateTag());
	}

	default void updateLivingToolState(ItemStack livingTool, ServerLevel level, Player player) {
		LivingToolState state = getLivingToolState(livingTool);
		boolean hasNutrients = hasNutrients(livingTool);

		if (state == LivingToolState.DORMANT) {
			setLivingToolState(livingTool, hasNutrients ? LivingToolState.AWAKENED : LivingToolState.BROKEN);
			SoundUtil.broadcastItemSound(level, player, ModSoundEvents.FLESH_BLOCK_PLACE.get());
		}
		else if (state == LivingToolState.AWAKENED) {
			setLivingToolState(livingTool, hasNutrients ? LivingToolState.DORMANT : LivingToolState.BROKEN);
			SoundUtil.broadcastItemSound(level, player, ModSoundEvents.FLESH_BLOCK_HIT.get());
		}
	}

	@Override
	default void onNutrientsChanged(ItemStack livingTool, int oldValue, int newValue) {
		LivingToolState prevState = getLivingToolState(livingTool);
		LivingToolState state = prevState;

		if (newValue <= 0) {
			if (state != LivingToolState.BROKEN) setLivingToolState(livingTool, LivingToolState.BROKEN);
			return;
		}

		if (state == LivingToolState.BROKEN) {
			state = LivingToolState.DORMANT;
		}

		int maxCost = getLivingToolMaxActionCost(livingTool, state);

		if (newValue < maxCost) {
			if (state == LivingToolState.AWAKENED) state = LivingToolState.DORMANT;
			else if (state == LivingToolState.DORMANT) state = LivingToolState.BROKEN;
		}

		if (state != prevState) setLivingToolState(livingTool, state);
	}

	default int getLivingToolActionCost(ItemStack livingTool, ToolAction toolAction) {
		LivingToolState state = getLivingToolState(livingTool);
		return getLivingToolActionCost(livingTool, state, toolAction);
	}

	default int getLivingToolActionCost(ItemStack livingTool, LivingToolState state, ToolAction toolAction) {
		return switch (state) {
			case BROKEN -> 0;
			case DORMANT -> 1;
			case AWAKENED -> 2;
		};
	}

	default int getLivingToolMaxActionCost(ItemStack livingTool, LivingToolState state) {
		return ToolAction.getActions().stream()
				.filter(livingTool::canPerformAction)
				.map(toolAction -> getLivingToolActionCost(livingTool, state, toolAction))
				.max(Integer::compareTo)
				.orElse(0);
	}

	default void appendLivingToolTooltip(ItemStack stack, List<Component> tooltip) {
		tooltip.add(getLivingToolState(stack).getTooltip().withStyle(TextStyles.ITALIC_GRAY));
		tooltip.add(ComponentUtil.emptyLine());
		LivingTool.super.appendLivingToolTooltip(stack, tooltip);
	}

}
