package com.github.elenterius.biomancy.item;

import com.github.elenterius.biomancy.chat.ComponentUtil;
import com.github.elenterius.biomancy.styles.TextStyles;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ObfuscatedTooltipBlockItem extends BlockItem implements CustomTooltipProvider {

	public ObfuscatedTooltipBlockItem(Block block, Properties properties) {
		super(block, properties);
	}

	@Override
	public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
		tooltip.add(ComponentUtil.horizontalLine());
		tooltip.add(getTooltipText(stack).withStyle(TextStyles.PRIMORDIAL_RUNES_GRAY));
		super.appendHoverText(stack, level, tooltip, flag);
	}

}
