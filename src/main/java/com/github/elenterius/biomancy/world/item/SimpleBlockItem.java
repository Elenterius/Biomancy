package com.github.elenterius.biomancy.world.item;

import com.github.elenterius.biomancy.util.ClientTextUtil;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class SimpleBlockItem extends BlockItem implements IBiomancyItem {

	public SimpleBlockItem(Block block, Properties properties) {
		super(block, properties);
	}

	@Override
	public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
		tooltip.add(ClientTextUtil.getItemInfoTooltip(stack.getItem()));
		super.appendHoverText(stack, level, tooltip, flag);
	}

}
