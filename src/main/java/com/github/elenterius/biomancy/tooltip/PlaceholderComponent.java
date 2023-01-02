package com.github.elenterius.biomancy.tooltip;

import com.github.elenterius.biomancy.client.gui.tooltip.TooltipRenderHandler;
import com.google.common.collect.Lists;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentContents;
//import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.Item;

import java.util.List;
import java.util.Objects;

/**
 * Text Component that acts as a temporary stand in for a TooltipComponent.
 * This placeholder will be replaced by the contained TooltipComponent during the GatherComponents Event
 * by {@link TooltipRenderHandler#onGatherTooltipComponents}
 * <br>
 * This allows easy insertion of TooltipComponents inside of {@link Item#appendHoverText}
 */
public class PlaceholderComponent implements Component {

	private final TooltipComponent replacement;

	public PlaceholderComponent(TooltipComponent tooltipComponent) {
		this.replacement = tooltipComponent;
	}

	/**
	 * @return TooltipComponent that will replace this placeholder
	 */
	public TooltipComponent getReplacement() {
		return replacement;
	}

	@Override
	public Style getStyle() {
		return Style.EMPTY;
	}

	@Override
	public List<Component> getSiblings() {
		return Lists.newArrayList();
	}

	@Override
	public boolean equals(Object other) {
		if (this == other) return true;
		if (!(other instanceof PlaceholderComponent that)) return false;
		return replacement.equals(that.replacement);
	}

	@Override
	public int hashCode() {
		return Objects.hash(replacement);
	}

	public String toString() {
		return "PlaceholderComponent{replacement=" + replacement + "}";
	}

	@Override
	public ComponentContents getContents() {
		return ComponentContents.EMPTY;
	}

	@Override
	public FormattedCharSequence getVisualOrderText() {
		return FormattedCharSequence.EMPTY;
	}

}
