package com.github.elenterius.biomancy.styles;

import com.github.elenterius.biomancy.tooltip.EmptyLineTooltipComponent;
import com.github.elenterius.biomancy.tooltip.HrTooltipComponent;
import com.github.elenterius.biomancy.tooltip.PlaceholderComponent;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraftforge.client.ForgeHooksClient;

public final class TooltipHacks {

	public static final PlaceholderComponent HR_COMPONENT = new PlaceholderComponent(new HrTooltipComponent());

	/**
	 * This is a replacement for {@link TextComponent#EMPTY}
	 * <br><br>
	 * When tooltip text is too wide it is wrapped around by forge ({@link ForgeHooksClient#gatherTooltipComponents}) to the next line and {@link TextComponent#EMPTY}
	 * components (empty strings) are discarded by minecraft's {@link net.minecraft.client.StringSplitter#splitLines StringSplitter#splitLines} method.<br>
	 */
	public static final PlaceholderComponent EMPTY_LINE_COMPONENT = new PlaceholderComponent(new EmptyLineTooltipComponent());

	private TooltipHacks() {}

	public static PlaceholderComponent wrap(TooltipComponent component) {
		return new PlaceholderComponent(component);
	}

}
