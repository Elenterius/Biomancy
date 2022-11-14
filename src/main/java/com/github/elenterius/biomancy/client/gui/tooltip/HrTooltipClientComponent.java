package com.github.elenterius.biomancy.client.gui.tooltip;

import com.github.elenterius.biomancy.tooltip.HrTooltipComponent;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;

/**
 * Horizontal Line element for Tooltips
 */
public class HrTooltipClientComponent implements ClientTooltipComponent {

	public HrTooltipClientComponent(HrTooltipComponent component) {}

	@Override
	public int getHeight() {
		return 8;
	}

	@Override
	public int getWidth(Font font) {
		return 1; //placeholder
	}

	/**
	 * @param tooltipWidth inner width
	 * @param lineNumber   zero-indexed
	 * @param color        argb
	 */
	public void renderLine(PoseStack poseStack, int posX, int posY, int tooltipWidth, int lineNumber, int color) {
		RenderSystem.setShaderColor(1, 1, 1, 1);
		int yOffset = lineNumber == 1 ? 0 : 2; //handle 2px bottom margin of the first line
		poseStack.pushPose();
		poseStack.translate(0, 0, 400);
		GuiComponent.fill(poseStack, posX, posY + yOffset, posX + tooltipWidth, posY + yOffset + 4, color);
		poseStack.popPose();
	}

}
