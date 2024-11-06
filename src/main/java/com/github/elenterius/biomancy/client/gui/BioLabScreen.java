package com.github.elenterius.biomancy.client.gui;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.client.gui.tooltip.ScreenNutrientFuelConsumer;
import com.github.elenterius.biomancy.client.gui.tooltip.ScreenTooltipStyleProvider;
import com.github.elenterius.biomancy.client.util.GuiRenderUtil;
import com.github.elenterius.biomancy.client.util.GuiUtil;
import com.github.elenterius.biomancy.menu.BioLabMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class BioLabScreen extends AbstractContainerScreen<BioLabMenu> implements ScreenTooltipStyleProvider, ScreenNutrientFuelConsumer {

	private static final ResourceLocation BACKGROUND_TEXTURE = BiomancyMod.createRL("textures/gui/menu_bio_lab.png");

	public BioLabScreen(BioLabMenu menu, Inventory playerInventory, Component title) {
		super(menu, playerInventory, title);
		imageHeight = 219;
	}

	@Override
	protected void init() {
		super.init();
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		int x = leftPos + 130;
		int y = topPos + 34;
		if (GuiUtil.isInRect(x, y, 8, 10, mouseX, mouseY)) {
			minecraft.gameMode.handleInventoryButtonClick(menu.containerId, 0);
			return true;
		}

		return super.mouseClicked(mouseX, mouseY, button);
	}

	@Override
	protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
		//don't draw any labels
	}

	@Override
	public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
		renderBackground(guiGraphics);
		super.render(guiGraphics, mouseX, mouseY, partialTick);
		renderTooltip(guiGraphics, mouseX, mouseY);
	}

	@Override
	protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
		guiGraphics.blit(BACKGROUND_TEXTURE, leftPos, topPos, 0, 0, imageWidth, imageHeight);
		drawProgressBar(guiGraphics, menu.getCraftingProgressNormalized());
		drawFuelBar(guiGraphics, menu.getFuelAmountNormalized());

		menu.renderFilters(guiGraphics, minecraft, leftPos, topPos);
		drawLock(guiGraphics, menu.isFilterLocked());
	}

	private void drawProgressBar(GuiGraphics guiGraphics, float craftingPct) {
		int vHeight = (int) (craftingPct * 20) + (craftingPct > 0 ? 1 : 0);
		guiGraphics.blit(BACKGROUND_TEXTURE, leftPos + 68, topPos + 78, 176, 0, 40, vHeight);
	}

	private void drawFuelBar(GuiGraphics guiGraphics, float fuelPct) {
		int vHeight = (int) (fuelPct * 36) + (fuelPct > 0 ? 1 : 0);
		guiGraphics.blit(BACKGROUND_TEXTURE, leftPos + 36, topPos + 48 + 36 - vHeight, 178, 58 - vHeight, 5, vHeight);

		int cost = menu.getFuelCost();
		if (cost <= menu.getFuelAmount()) return;

		int x = leftPos + 36;
		int y = topPos + 48 + 36 - ((int) ((cost / (float) menu.getMaxFuelAmount()) * 36) + 1);
		guiGraphics.fill(x - 3, y, x + 5 + 3, y + 1, 0x30_ff0000);
		guiGraphics.fill(x + 5 + 3, y, x + 5 + 3 + 1, y + 1, 0xff_ff0000);
		guiGraphics.fill(x + 5 + 3 + 1, y - 1, x + 5 + 3 + 2, y + 2, 0xff_ff0000);
	}

	private void drawLock(GuiGraphics guiGraphics, boolean isLocked) {
		int offset = isLocked ? 0 : 8;
		guiGraphics.blit(BACKGROUND_TEXTURE, leftPos + 130, topPos + 34, 178 + offset, 58 + 2, 8, 10);
	}

	@Override
	protected void renderTooltip(GuiGraphics guiGraphics, int mouseX, int mouseY) {
		if (menu.getCarried().isEmpty()) {
			if (GuiUtil.isInRect(leftPos + 36, topPos + 48, 5, 36, mouseX, mouseY)) {
				drawFuelTooltip(guiGraphics, mouseX, mouseY);
				return;
			}
		}

		super.renderTooltip(guiGraphics, mouseX, mouseY);
	}

	private void drawFuelTooltip(GuiGraphics guiGraphics, int mouseX, int mouseY) {
		int maxFuel = menu.getMaxFuelAmount();
		int fuelAmount = menu.getFuelAmount();
		int totalFuelCost = menu.getFuelCost();
		GuiRenderUtil.drawFuelTooltip(font, guiGraphics, mouseX, mouseY, maxFuel, fuelAmount, totalFuelCost);
	}

}
