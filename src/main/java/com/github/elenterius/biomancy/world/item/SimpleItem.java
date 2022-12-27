package com.github.elenterius.biomancy.world.item;

import com.github.elenterius.biomancy.client.util.ClientTextUtil;
import com.github.elenterius.biomancy.styles.TooltipHacks;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.List;

public class SimpleItem extends Item implements ICustomTooltip {

	public SimpleItem(Properties properties) {
		super(properties);
	}

	@Override
	public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag isAdvanced) {
		tooltip.add(TooltipHacks.HR_COMPONENT);
		tooltip.add(ClientTextUtil.getItemInfoTooltip(stack));
	}

	public static class ShinySimpleItem extends SimpleItem {
		public ShinySimpleItem(Properties properties) {
			super(properties);
		}

		@Override
		public boolean isFoil(ItemStack stack) {
			return true;
		}
	}

}
