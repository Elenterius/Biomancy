package com.github.elenterius.biomancy.client.util;

import com.github.elenterius.biomancy.styles.TextStyles;
import com.github.elenterius.biomancy.util.ComponentUtil;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public final class GuiRenderUtil {

	protected static final ResourceLocation GUI_ICONS_TEXTURE = new ResourceLocation("textures/gui/icons.png");

	private GuiRenderUtil() {}

	public static void drawFuelTooltip(Font font, GuiGraphics guiGraphics, int mouseX, int mouseY, int maxFuel, int fuelAmount, int totalFuelCost) {
		List<Component> hoveringText = new ArrayList<>();
		DecimalFormat df = ClientTextUtil.getDecimalFormatter("#,###,###");

		hoveringText.add(ComponentUtil.translatable("tooltip.biomancy.nutrients_fuel").withStyle(TextStyles.NUTRIENTS));
		hoveringText.add(ComponentUtil.literal("%s/%s u".formatted(df.format(fuelAmount), df.format(maxFuel))));
		if (totalFuelCost > 0) {
			hoveringText.add(ComponentUtil.translatable("tooltip.biomancy.nutrients_consumes", df.format(totalFuelCost)).withStyle(TextStyles.NUTRIENTS_CONSUMPTION));
		}

		guiGraphics.renderComponentTooltip(font, hoveringText, mouseX, mouseY);
	}

	public static void drawGhostItem(GuiGraphics guiGraphics, int pX, int pY, ItemStack stack) {
		guiGraphics.renderFakeItem(stack, pX, pY);
		RenderSystem.depthFunc(GL11.GL_GREATER); //passes if the fragment's depth value is greater than the stored depth value
		guiGraphics.fill(pX, pY, pX + 16, pY + 16, 0x30_ff_ff_ff);
		RenderSystem.depthFunc(GL11.GL_LEQUAL); //passes if the fragment's depth value is equal to the stored depth value
	}

	public static void drawAttackIndicator(GuiGraphics guiGraphics, int x, int y, float pct) {
		RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.ONE_MINUS_DST_COLOR, GlStateManager.DestFactor.ONE_MINUS_SRC_COLOR, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
		guiGraphics.blit(GUI_ICONS_TEXTURE, x, y, 36, 94, 16, 4);
		guiGraphics.blit(GUI_ICONS_TEXTURE, x, y, 52, 94, (int) (pct * 17f), 4);
	}

	public static void drawSquareProgressBar(GuiGraphics guiGraphics, int centerX, int centerY, int blitOffset, int radius, float pct) {
		RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.ONE_MINUS_DST_COLOR, GlStateManager.DestFactor.ONE_MINUS_SRC_COLOR, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
		drawSquareProgressBar(guiGraphics, centerX, centerY, radius, blitOffset, pct, 0xFF_FFFFFF);
	}

	private static void drawSquareProgressBar(GuiGraphics guiGraphics, int centerX, int centerY, int radius, int blitOffset, float pct, int argbColor) {
		int size = 1;
		int sideLength = radius * 2; //of the square
		int halfLength = radius;
		int currentLength = Math.round(pct * (sideLength * 4)); //sideLength * 4 = totalLength

		Matrix4f matrix4f = guiGraphics.pose().last().pose();

		RenderSystem.enableBlend();
		//		RenderSystem.disableTexture();
		RenderSystem.setShader(GameRenderer::getPositionColorShader);

		// top right line
		int dist = Math.min(currentLength, halfLength);
		if (dist > 0) {
			int y = centerY - halfLength;
			drawQuad(matrix4f, centerX, y - size, centerX + halfLength, y + size, blitOffset, argbColor);
		}

		// right line
		dist = Math.min(currentLength - halfLength, sideLength);
		if (dist > 0) {
			int x = centerX + halfLength;
			int y = centerY - halfLength;
			drawQuad(matrix4f, x - size, y, x + size, y + dist, blitOffset, argbColor);
		}

		// bottom line
		dist = Math.min(currentLength - 3 * halfLength, sideLength);
		if (dist > 0) {
			int x = centerX + halfLength;
			int y = centerY + halfLength;
			drawQuad(matrix4f, x - dist, y - size, x, y + size, blitOffset, argbColor);
		}

		// left line
		dist = Math.min(currentLength - 5 * halfLength, sideLength);
		if (dist > 0) {
			int x = centerX - halfLength;
			int y = centerY + halfLength;
			drawQuad(matrix4f, x - size, y - dist, x + size, y, blitOffset, argbColor);
		}

		// top left line
		dist = Math.min(currentLength - 7 * halfLength, halfLength);
		if (dist > 0) {
			int x = centerX - halfLength;
			int y = centerY - halfLength;
			drawQuad(matrix4f, x, y - size, x + halfLength, y + size, blitOffset, argbColor);
		}

		//		RenderSystem.enableTexture();
		RenderSystem.disableBlend();
	}

	private static void drawQuad(Matrix4f matrix4f, int minX, int minY, int maxX, int maxY, int z, int argbColor) {
		if (minX < maxX) {
			int n = minX;
			minX = maxX;
			maxX = n;
		}

		if (minY < maxY) {
			int n = minY;
			minY = maxY;
			maxY = n;
		}

		BufferBuilder bufferbuilder = Tesselator.getInstance().getBuilder();
		bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
		bufferbuilder.vertex(matrix4f, minX, maxY, z).color(argbColor).endVertex();
		bufferbuilder.vertex(matrix4f, maxX, maxY, z).color(argbColor).endVertex();
		bufferbuilder.vertex(matrix4f, maxX, minY, z).color(argbColor).endVertex();
		bufferbuilder.vertex(matrix4f, minX, minY, z).color(argbColor).endVertex();
		BufferUploader.drawWithShader(bufferbuilder.end());
	}

	public static void fill(GuiGraphics guiGraphics, float minX, float minY, float maxX, float maxY, int z, int colorARGB) {
		Matrix4f matrix4f = guiGraphics.pose().last().pose();

		BufferBuilder bufferbuilder = Tesselator.getInstance().getBuilder();
		RenderSystem.enableBlend();
		RenderSystem.defaultBlendFunc();
		RenderSystem.setShader(GameRenderer::getPositionColorShader);
		bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
		bufferbuilder.vertex(matrix4f, minX, maxY, z).color(colorARGB).endVertex();
		bufferbuilder.vertex(matrix4f, maxX, maxY, z).color(colorARGB).endVertex();
		bufferbuilder.vertex(matrix4f, maxX, minY, z).color(colorARGB).endVertex();
		bufferbuilder.vertex(matrix4f, minX, minY, z).color(colorARGB).endVertex();
		BufferUploader.drawWithShader(bufferbuilder.end());
		RenderSystem.disableBlend();
	}

}
