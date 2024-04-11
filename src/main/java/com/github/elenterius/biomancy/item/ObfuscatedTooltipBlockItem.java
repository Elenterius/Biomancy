package com.github.elenterius.biomancy.item;

import com.github.elenterius.biomancy.client.util.ClientTextUtil;
import com.github.elenterius.biomancy.styles.TextStyles;
import com.github.elenterius.biomancy.util.ComponentUtil;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ObfuscatedTooltipBlockItem extends BlockItem implements ItemTooltipStyleProvider {

	public ObfuscatedTooltipBlockItem(Block block, Properties properties) {
		super(block, properties);
	}

	@Override
	public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
		tooltip.add(ComponentUtil.emptyLine());
		tooltip.add(getTooltipText(stack).withStyle(TextStyles.PRIMORDIAL_RUNES_GRAY));
		tooltip.add(ComponentUtil.emptyLine());
		tooltip.add(ClientTextUtil.getItemInfoTooltip(stack));

		super.appendHoverText(stack, level, tooltip, flag);
	}

}
