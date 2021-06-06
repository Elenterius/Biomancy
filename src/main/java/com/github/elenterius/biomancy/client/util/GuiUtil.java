package com.github.elenterius.biomancy.client.util;

public final class GuiUtil {
	private GuiUtil() {}

	public static boolean isInRect(int x, int y, int xSize, int ySize, int mouseX, int mouseY) {
		return ((mouseX >= x && mouseX <= x + xSize) && (mouseY >= y && mouseY <= y + ySize));
	}

}
