package com.github.elenterius.biomancy.client.gui;

import com.github.elenterius.biomancy.util.ClientTextUtil;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

@OnlyIn(Dist.CLIENT)
public final class GuiUtil {

	private GuiUtil() {}

	public static boolean isInRect(int x, int y, int xSize, int ySize, int mouseX, int mouseY) {
		return ((mouseX >= x && mouseX <= x + xSize) && (mouseY >= y && mouseY <= y + ySize));
	}

	public static boolean isInRect(int x, int y, int xSize, int ySize, double mouseX, double mouseY) {
		return ((mouseX >= x && mouseX <= x + xSize) && (mouseY >= y && mouseY <= y + ySize));
	}

	public static void drawFuelTooltip(Screen screen, PoseStack poseStack, int mouseX, int mouseY, int maxFuel, int fuelAmount, int totalFuelCost) {
		List<Component> hoveringText = new ArrayList<>();
		DecimalFormat df = ClientTextUtil.getDecimalFormatter("#,###,###");

		hoveringText.add(new TextComponent("Nutrient Fuel").withStyle(Style.EMPTY.withColor(0x65b52a)));
		hoveringText.add(new TextComponent(df.format(fuelAmount) + "/" + df.format(maxFuel) + " u"));
		hoveringText.add(new TextComponent("Cost:  " + df.format(totalFuelCost) + " u").withStyle(Style.EMPTY.withColor(0xe7bd42)));

		screen.renderComponentTooltip(poseStack, hoveringText, mouseX, mouseY);
	}

}
