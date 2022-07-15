package com.github.elenterius.biomancy.client.gui;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;

public class ProgressBar extends Drawable {

	private float progress = 0f;
	private int argb = 0xFFFFFFFF;

	public ProgressBar(int posX, int posY, int widthIn, int heightIn) {
		super(posX, posY, widthIn, heightIn);
	}

	public ProgressBar(int posX, int posY, int widthIn, int heightIn, int argb) {
		super(posX, posY, widthIn, heightIn);
		this.argb = argb;
	}

	public void setColor(int argb) {
		this.argb = argb;
	}

	public void setProgress(float progress) {
		this.progress = progress;
	}

	@Override
	public void draw(Minecraft mc, PoseStack matrixStack, int guiLeft, int guiTop, int mouseX, int mouseY) {
		int posX = guiLeft + x;
		int posY = guiTop + y;
		int maxPosY = posY + height;
		GuiComponent.fill(matrixStack, posX, posY + (int) (height * (1f - progress)), posX + width, maxPosY, argb);
	}

}
