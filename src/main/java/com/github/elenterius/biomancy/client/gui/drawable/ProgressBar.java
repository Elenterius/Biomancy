package com.github.elenterius.biomancy.client.gui.drawable;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;

public class ProgressBar extends Drawable {

	public float progress = 0f;
	public int argb = 0xFFFFFFFF;

	public ProgressBar(int posX, int posY, int widthIn, int heightIn) {
		super(posX, posY, widthIn, heightIn);
	}

	public ProgressBar(int posX, int posY, int widthIn, int heightIn, int argbIn) {
		super(posX, posY, widthIn, heightIn);
		argb = argbIn;
	}

	public void setColor(int argbIn) {
		argb = argbIn;
	}

	public void setProgress(float progressIn) {
		progress = progressIn;
	}

	@Override
	public void draw(Minecraft mc, MatrixStack matrixStack, int guiLeft, int guiTop, int mouseX, int mouseY) {
		int posX = guiLeft + x;
		int posY = guiTop + y;
		int maxPosY = posY + height;
		AbstractGui.fill(matrixStack, posX, posY + (int) (height * (1f - progress)), posX + width, maxPosY, argb);
	}

}
