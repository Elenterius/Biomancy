package com.github.elenterius.biomancy.client.gui;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.client.gui.tooltip.ScreenNutrientFuelConsumer;
import com.github.elenterius.biomancy.client.gui.tooltip.ScreenTooltipStyleProvider;
import com.github.elenterius.biomancy.client.util.GuiRenderUtil;
import com.github.elenterius.biomancy.client.util.GuiUtil;
import com.github.elenterius.biomancy.menu.DigesterMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class DigesterScreen extends AbstractContainerScreen<DigesterMenu> implements ScreenTooltipStyleProvider, ScreenNutrientFuelConsumer {

	private static final ResourceLocation BACKGROUND_TEXTURE = BiomancyMod.createRL("textures/gui/menu_digester.png");

	public DigesterScreen(DigesterMenu menu, Inventory playerInventory, Component title) {
		super(menu, playerInventory, title);
		imageHeight = 193;
	}

	@Override
	protected void init() {
		super.init();
	}

	@Override
	public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
		renderBackground(guiGraphics);
		super.render(guiGraphics, mouseX, mouseY, partialTick);
		renderTooltip(guiGraphics, mouseX, mouseY);
	}

	@Override
	protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
		//don't draw any labels
	}

	@Override
	protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
		guiGraphics.blit(BACKGROUND_TEXTURE, leftPos, topPos, 0, 0, imageWidth, imageHeight);
		drawProgressBar(guiGraphics, menu.getCraftingProgressNormalized());
		drawFuelBar(guiGraphics, menu.getFuelAmountNormalized());
	}

	private void drawProgressBar(GuiGraphics guiGraphics, float craftingPct) {
		int uWidth = (int) (craftingPct * 20) + (craftingPct > 0 ? 1 : 0);
		guiGraphics.blit(BACKGROUND_TEXTURE, leftPos + 78, topPos + 17, 194, 0, uWidth, 2);
	}

	private void drawFuelBar(GuiGraphics guiGraphics, float fuelPct) {
		int vHeight = (int) (fuelPct * 36) + (fuelPct > 0 ? 1 : 0);
		guiGraphics.blit(BACKGROUND_TEXTURE, leftPos + 44, topPos + 28 + 36 - vHeight, 178, 36 - vHeight, 5, vHeight);
	}

	@Override
	protected void renderTooltip(GuiGraphics guiGraphics, int mouseX, int mouseY) {
		if (menu.getCarried().isEmpty() && (GuiUtil.isInRect(leftPos + 44, topPos + 26, 5, 36, mouseX, mouseY))) {
			drawFuelTooltip(guiGraphics, mouseX, mouseY);
			return;
		}

		super.renderTooltip(guiGraphics, mouseX, mouseY);
	}

	private void drawFuelTooltip(GuiGraphics graphics, int mouseX, int mouseY) {
		int maxFuel = menu.getMaxFuelAmount();
		int fuelAmount = menu.getFuelAmount();
		int totalFuelCost = menu.getFuelCost();
		GuiRenderUtil.drawFuelTooltip(font, graphics, mouseX, mouseY, maxFuel, fuelAmount, totalFuelCost);
	}

}
