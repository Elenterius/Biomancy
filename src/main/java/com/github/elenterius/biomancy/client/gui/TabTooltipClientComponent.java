package com.github.elenterius.biomancy.client.gui;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.util.TabTooltipComponent;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Matrix4f;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class TabTooltipClientComponent implements ClientTooltipComponent {

	public static final ResourceLocation TEXTURE = BiomancyMod.createRL("textures/gui/ui_tooltip.png");

	public static final String TEXT = "ctrl";
	public static final float SCALE = 7f / 9f;
	public static final int GAP = 9;

	private final FormattedCharSequence displayText;

	public TabTooltipClientComponent(TabTooltipComponent component) {
		displayText = component.getText();
	}

	@Override
	public int getHeight() {
		return 10;
	}

	@Override
	public int getWidth(Font font) {
		return font.width(displayText) + GAP + 16 + (int) (font.width(TEXT) * SCALE) + 4 + 2;
	}

	@Override
	public void renderText(Font font, int x, int y, Matrix4f matrix4f, MultiBufferSource.BufferSource bufferSource) {
		font.drawInBatch(displayText, x, y, -1, true, matrix4f, bufferSource, false, 0, 0xf000f0);
	}

	@Override
	public void renderImage(Font font, int x, int y, PoseStack poseStack, ItemRenderer itemRenderer, int blitOffset) {
		int pX = x + font.width(displayText) + GAP;
		int width = (int) (font.width(TEXT) * SCALE);

		float uOffset;
		int color;
		if (Screen.hasControlDown()) {
			uOffset = 24;
			color = 0xff_555555;
		}
		else {
			uOffset = 0;
			color = 0xff_55ffff;
		}

		RenderSystem.setShaderColor(1, 1, 1, 1);
		RenderSystem.setShaderTexture(0, TEXTURE);
		poseStack.pushPose();
		poseStack.translate(0, 0, blitOffset);
		blit(poseStack, pX, y, uOffset, 23, 16, 9);
		blit(poseStack, pX + 16, y, 16 + uOffset, 23, 2, 9);
		blit(poseStack, pX + 16 + 2, y, width + 2, 18 + uOffset, 23, 1, 9);
		blit(poseStack, pX + 16 + width + 4, y, 19 + uOffset, 23, 2, 9);
		poseStack.popPose();

		poseStack.pushPose();
		poseStack.translate(pX + 16d + 3d, y + SCALE + 1d, blitOffset + 1d);
		poseStack.scale(SCALE, SCALE, 0);
		font.draw(poseStack, TEXT, 0, 0, color);
		poseStack.popPose();
	}

	private void blit(PoseStack pPoseStack, int pX, int pY, int width, float uOffset, float vOffset, int u, int v) {
		GuiComponent.blit(pPoseStack, pX, pY, width, v, uOffset, vOffset, u, v, 64, 32);
	}

	private void blit(PoseStack pPoseStack, int pX, int pY, float uOffset, float vOffset, int u, int v) {
		GuiComponent.blit(pPoseStack, pX, pY, uOffset, vOffset, u, v, 64, 32);
	}

}
