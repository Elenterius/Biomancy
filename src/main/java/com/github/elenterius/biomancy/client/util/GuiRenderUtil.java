package com.github.elenterius.biomancy.client.util;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Matrix4f;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.item.ItemStack;
import org.lwjgl.opengl.GL11;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public final class GuiRenderUtil {

	private GuiRenderUtil() {}

	public static void drawFuelTooltip(Screen screen, PoseStack poseStack, int mouseX, int mouseY, int maxFuel, int fuelAmount, int totalFuelCost) {
		List<Component> hoveringText = new ArrayList<>();
		DecimalFormat df = ClientTextUtil.getDecimalFormatter("#,###,###");

		hoveringText.add(new TextComponent("Nutrient Fuel").withStyle(Style.EMPTY.withColor(0x65b52a)));
		hoveringText.add(new TextComponent("%s/%s u".formatted(df.format(fuelAmount), df.format(maxFuel))));
		if (totalFuelCost > 0) {
			String text = "Cost:  %s u".formatted(df.format(totalFuelCost));
			hoveringText.add(new TextComponent(text).withStyle(Style.EMPTY.withColor(0xe7bd42)));
		}

		screen.renderComponentTooltip(poseStack, hoveringText, mouseX, mouseY);
	}

	public static void drawGhostItem(ItemRenderer itemRenderer, PoseStack poseStack, int pX, int pY, ItemStack stack) {
		itemRenderer.renderAndDecorateFakeItem(stack, pX, pY);
		RenderSystem.depthFunc(GL11.GL_GREATER); //passes if the fragment's depth value is greater than the stored depth value
		GuiComponent.fill(poseStack, pX, pY, pX + 16, pY + 16, 0x30_ff_ff_ff);
		RenderSystem.depthFunc(GL11.GL_LEQUAL); //passes if the fragment's depth value is equal to the stored depth value
	}

	public static void drawAttackIndicator(GuiComponent gui, PoseStack poseStack, int x, int y, float pct) {
		RenderSystem.setShaderTexture(0, GuiComponent.GUI_ICONS_LOCATION);
		RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.ONE_MINUS_DST_COLOR, GlStateManager.DestFactor.ONE_MINUS_SRC_COLOR, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
		gui.blit(poseStack, x, y, 36, 94, 16, 4);
		gui.blit(poseStack, x, y, 52, 94, (int) (pct * 17f), 4);
	}

	public static void drawSquareProgressBar(PoseStack poseStack, int centerX, int centerY, int blitOffset, int radius, float pct) {
		RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.ONE_MINUS_DST_COLOR, GlStateManager.DestFactor.ONE_MINUS_SRC_COLOR, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
		drawSquareProgressBar(poseStack, centerX, centerY, radius, blitOffset, pct, 0xFF_FFFFFF);
	}

	private static void drawSquareProgressBar(PoseStack poseStack, int centerX, int centerY, int radius, int blitOffset, float pct, int argbColor) {
		int size = 1;
		int sideLength = radius * 2; //of the square
		int halfLength = radius;
		int currentLength = Math.round(pct * (sideLength * 4)); //sideLength * 4 = totalLength

		Matrix4f matrix4f = poseStack.last().pose();

		RenderSystem.enableBlend();
		RenderSystem.disableTexture();
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

		RenderSystem.enableTexture();
		RenderSystem.disableBlend();
	}

	private static void drawQuad(Matrix4f matrix4f, int minX, int minY, int maxX, int maxY, int blitOffset, int argbColor) {
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
		bufferbuilder.vertex(matrix4f, minX, maxY, blitOffset).color(argbColor).endVertex();
		bufferbuilder.vertex(matrix4f, maxX, maxY, blitOffset).color(argbColor).endVertex();
		bufferbuilder.vertex(matrix4f, maxX, minY, blitOffset).color(argbColor).endVertex();
		bufferbuilder.vertex(matrix4f, minX, minY, blitOffset).color(argbColor).endVertex();
		bufferbuilder.end();
		BufferUploader.end(bufferbuilder);
	}

	public static void fill(PoseStack poseStack, float minX, float minY, float maxX, float maxY, int blitOffset, int color) {
		Matrix4f matrix4f = poseStack.last().pose();

		BufferBuilder bufferbuilder = Tesselator.getInstance().getBuilder();
		RenderSystem.enableBlend();
		RenderSystem.disableTexture();
		RenderSystem.defaultBlendFunc();
		RenderSystem.setShader(GameRenderer::getPositionColorShader);
		bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
		bufferbuilder.vertex(matrix4f, minX, maxY, blitOffset).color(color).endVertex();
		bufferbuilder.vertex(matrix4f, maxX, maxY, blitOffset).color(color).endVertex();
		bufferbuilder.vertex(matrix4f, maxX, minY, blitOffset).color(color).endVertex();
		bufferbuilder.vertex(matrix4f, minX, minY, blitOffset).color(color).endVertex();
		bufferbuilder.end();
		BufferUploader.end(bufferbuilder);
		RenderSystem.enableTexture();
		RenderSystem.disableBlend();
	}

}
