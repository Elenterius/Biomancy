package com.github.elenterius.biomancy.item;

import com.github.elenterius.biomancy.client.util.ClientTextUtil;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class SimpleBlockItem extends BlockItem implements ItemTooltipStyleProvider {

	public SimpleBlockItem(Block block, Properties properties) {
		super(block, properties);
	}

	@Override
	public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
		tooltip.addAll(ClientTextUtil.getItemInfoTooltip(stack));
		super.appendHoverText(stack, level, tooltip, flag);
	}

}
