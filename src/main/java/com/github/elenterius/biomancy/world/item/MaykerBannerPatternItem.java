package com.github.elenterius.biomancy.world.item;

import com.github.elenterius.biomancy.styles.TextStyles;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.BannerPatternItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BannerPattern;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class MaykerBannerPatternItem extends BannerPatternItem {

	public MaykerBannerPatternItem(TagKey<BannerPattern> bannerPattern, Properties properties) {
		super(bannerPattern, properties);
	}

	@Override
	public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
		tooltip.add(this.getDisplayName().withStyle(TextStyles.MAYKR_RUNES_GRAY));
	}

}
