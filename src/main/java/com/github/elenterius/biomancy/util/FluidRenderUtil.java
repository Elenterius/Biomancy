/*
The MIT License (MIT)

Copyright (c) 2014-2015 mezz

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in
all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
THE SOFTWARE.
 */
package com.github.elenterius.biomancy.util;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.inventory.container.PlayerContainer;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * copied on the 2021/07/07 from https://github.com/mezz/JustEnoughItems/blob/6b7944d7da0ab4c6e6bf5ff706fb361b590e1a6f/src/main/java/mezz/jei/plugins/vanilla/ingredients/fluid/FluidStackRenderer.java
 */
@OnlyIn(Dist.CLIENT)
public final class FluidRenderUtil {

	private FluidRenderUtil() {}

	public static void drawTiledSprite(Minecraft mc, MatrixStack matrixStack, final int xPos, final int yPos, final int width, final int height, int color, int scaledAmount, TextureAtlasSprite sprite) {
		mc.getTextureManager().bindTexture(PlayerContainer.LOCATION_BLOCKS_TEXTURE);
		Matrix4f matrix = matrixStack.getLast().getMatrix();

		float red = (color >> 16 & 0xFF) / 255f;
		float green = (color >> 8 & 0xFF) / 255f;
		float blue = (color & 0xFF) / 255f;
		float alpha = ((color >> 24) & 0xFF) / 255f;
		RenderSystem.color4f(red, green, blue, alpha);

		final int tileCountX = width / 16;
		final int remainderX = width - (tileCountX * 16);
		final int tileCountY = scaledAmount / 16;
		final int remainderY = scaledAmount - (tileCountY * 16);

		final int yStart = yPos + height;

		for (int tileX = 0; tileX <= tileCountX; tileX++) {
			for (int tileY = 0; tileY <= tileCountY; tileY++) {
				int w = (tileX == tileCountX) ? remainderX : 16;
				int h = (tileY == tileCountY) ? remainderY : 16;
				int x = xPos + (tileX * 16);
				int y = yStart - ((tileY + 1) * 16);
				if (w > 0 && h > 0) {
					drawSpriteWithMasking(matrix, x, y, 16 - h, 16 - w, 100, sprite);
				}
			}
		}
	}

	private static void drawSpriteWithMasking(Matrix4f matrix, float x1, float y1, int maskTop, int maskRight, float zLevel, TextureAtlasSprite sprite) {
		float minU = sprite.getMinU();
		float maxU = sprite.getMaxU();
		float minV = sprite.getMinV();
		float maxV = sprite.getMaxV();
		maxU = maxU - (maskRight / 16f * (maxU - minU));
		maxV = maxV - (maskTop / 16f * (maxV - minV));

		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferBuilder = tessellator.getBuffer();
		bufferBuilder.begin(7, DefaultVertexFormats.POSITION_TEX);
		bufferBuilder.pos(matrix, x1, y1 + 16, zLevel).tex(minU, maxV).endVertex();
		bufferBuilder.pos(matrix, x1 + 16 - maskRight, y1 + 16, zLevel).tex(maxU, maxV).endVertex();
		bufferBuilder.pos(matrix, x1 + 16 - maskRight, y1 + maskTop, zLevel).tex(maxU, minV).endVertex();
		bufferBuilder.pos(matrix, x1, y1 + maskTop, zLevel).tex(minU, minV).endVertex();
		tessellator.draw();
	}
}
