package com.github.elenterius.biomancy.client.gui.drawable;

import com.github.elenterius.biomancy.client.util.GuiUtil;
import com.mojang.blaze3d.matrix.MatrixStack;

public abstract class Drawable {

	public final int x;
	public final int y;
	public final int width;
	public final int height;

	public Drawable(int posX, int posY, int widthIn, int heightIn) {
		x = posX;
		y = posY;
		width = widthIn;
		height = heightIn;
	}

	public boolean isMouseInside(int guiLeft, int guiTop, int mouseX, int mouseY) {
		return GuiUtil.isInRect(guiLeft + x, guiTop + y, width, height, mouseX, mouseY);
	}

	public abstract void draw(MatrixStack matrixStack, int guiLeft, int guiTop, int mouseX, int mouseY);

}
